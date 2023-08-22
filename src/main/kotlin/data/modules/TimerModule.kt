package data.modules

import data.BaseConfig
import data.LedColor
import data.LedModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimerModule(
    private val config: BaseConfig
) : LedModule {

    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
    private var ledId: Int = 0

    init {
        scope.launch {
            while (true) {
                ledId += 1

                if (ledId == config.config.ledService.ledCount) {
                    ledId = 0
                }

                delay(16)
            }
        }
    }

    override fun calc(ledNr: Int): LedColor {
        return if (ledNr == ledId) { LedColor(1.0, 1.0, 1.0, 1.0) } else { LedColor() }
    }
}
