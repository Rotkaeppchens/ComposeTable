package data

import com.github.mbelling.ws281x.LedStrip
import com.github.mbelling.ws281x.Ws281xLedStrip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LedController(
    private val config: BaseConfig,
    private val moduleController: ModuleController,
    private val ledAnimClock: LedAnimationClock
) {
    private val strip: LedStrip? = if (config.config.ledService.initStrip) {
        val ledService = config.config.ledService

        Ws281xLedStrip(
            /* ledsCount = */ ledService.ledCount,
            /* gpioPin = */ ledService.gpioPin,
            /* frequencyHz = */ ledService.frequencyHz,
            /* dma = */ ledService.dma,
            /* brightness = */ ledService.brightness,
            /* pwmChannel = */ ledService.pwmChannel,
            /* invert = */ ledService.invert,
            /* stripType = */ ledService.stripType,
            /* clearOnExit = */ ledService.clearOnExit
        )
    } else {
        null
    }

    private val _ledState: MutableStateFlow<List<LedColor>> = MutableStateFlow(emptyList())

    val ledState: StateFlow<List<LedColor>>
        get() = _ledState

    init {
        startLoop()
    }

    private fun startLoop() {
        CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                val now = System.nanoTime()

                // Render eventual animations
                ledAnimClock.clock.sendFrame(now)

                val moduleList = moduleController.sortedModuleList.value.map {
                    it.onUpdate(now)
                }

                val colors = config.getLEDs().map { ledId ->
                    moduleList.fold(LedColor.Dark) { color, moduleColors ->
                        return@fold color.blend(moduleColors.getOrElse(ledId) { LedColor.Dark })
                    }
                }

                _ledState.update { colors }

                strip?.let {
                    colors.forEachIndexed { i, color ->
                        it.setPixel(i, color.toStripColor())
                    }

                    it.render()
                }

                delay(config.config.tableConfig.loopSleepTime)
            }
        }
    }
}
