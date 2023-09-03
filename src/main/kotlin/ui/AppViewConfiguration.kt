package ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration
import data.BaseConfig
import org.koin.compose.koinInject

@Composable
fun AppViewConfiguration(
    config: BaseConfig = koinInject(),
    content: @Composable () -> Unit
) {
    fun ViewConfiguration.appViewConfiguration() = object : ViewConfiguration {
        override val longPressTimeoutMillis get() =
            this@appViewConfiguration.longPressTimeoutMillis

        override val doubleTapTimeoutMillis get() =
            this@appViewConfiguration.doubleTapTimeoutMillis

        override val doubleTapMinTimeMillis get() =
            this@appViewConfiguration.doubleTapMinTimeMillis

        override val touchSlop: Float get() = config.config.interfaceConfig.touchSlop
    }

    CompositionLocalProvider (
        LocalViewConfiguration provides LocalViewConfiguration.current.appViewConfiguration()
    ) {
        content()
    }
}
