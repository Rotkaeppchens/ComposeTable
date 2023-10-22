package ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.unit.Density
import data.BaseConfig
import org.koin.compose.koinInject

@Composable
fun AppViewConfiguration(
    config: BaseConfig = koinInject(),
    content: @Composable () -> Unit
) {
    fun ViewConfiguration.appViewConfiguration() = object : ViewConfiguration {
        override val longPressTimeoutMillis = this@appViewConfiguration.longPressTimeoutMillis
        override val doubleTapTimeoutMillis = this@appViewConfiguration.doubleTapTimeoutMillis
        override val doubleTapMinTimeMillis = this@appViewConfiguration.doubleTapMinTimeMillis

        override val touchSlop = config.config.interfaceConfig.touchSlop
    }

    val densityConfiguration = object : Density {
        override val density = config.config.interfaceConfig.density
        override val fontScale = config.config.interfaceConfig.fontScale
    }

    CompositionLocalProvider (
        LocalViewConfiguration provides LocalViewConfiguration.current.appViewConfiguration(),
        LocalDensity provides densityConfiguration
    ) {
        content()
    }
}
