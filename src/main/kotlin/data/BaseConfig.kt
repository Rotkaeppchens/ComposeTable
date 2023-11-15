package data

import com.github.mbelling.ws281x.LedStripType
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addResourceOrFileSource
import kotlin.time.Duration

class BaseConfig {
    val config = ConfigLoaderBuilder
        .default()
        .addResourceOrFileSource("./config.yaml")
        .build()
        .loadConfigOrThrow<Config>()

    val ledCount: Int
        get() = config.ledService.ledCount

    fun getLEDs(): List<Int> {
        return (0 until config.ledService.ledCount).toList()
    }

    fun getSides(): List<Int> {
        return (0..config.tableConfig.breakPoints.size).toList()
    }

    fun getLEDsForSide(side: Int): List<Int> {
        val startIndex = config.tableConfig.breakPoints.getOrNull(side - 1) ?: 0
        val endIndex = config.tableConfig.breakPoints.getOrNull(side) ?: config.ledService.ledCount

        return (startIndex until endIndex).toList()
    }

    companion object {
        enum class ComposeRenderer {
            DEFAULT,
            SOFTWARE,
            OPENGL,
            METAL
        }

        data class LedService (
            val initStrip: Boolean,
            val ledCount: Int,
            val gpioPin: Int,
            val frequencyHz: Int,
            val dma: Int,
            val brightness: Int,
            val pwmChannel: Int,
            val invert: Boolean,
            val stripType: LedStripType,
            val clearOnExit: Boolean
        )

        data class TableConfig (
            val breakPoints: List<Int>,
            val loopSleepTime: Duration
        )

        data class InterfaceConfig(
            val useDarkTheme: Boolean,
            val mainWindowMaximised: Boolean,
            val offWindowOpen: Boolean,
            val offWindowPosition: Pair<Int, Int>,
            val offWindowMaximised: Boolean,
            val renderer: ComposeRenderer,
            val touchSlop: Float,
            val density: Float,
            val fontScale: Float
        )

        data class Config(
            val ledService: LedService,
            val tableConfig: TableConfig,
            val interfaceConfig: InterfaceConfig
        )
    }
}
