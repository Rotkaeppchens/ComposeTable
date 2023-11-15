
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import data.BaseConfig
import data.database.source.initDatabase
import koin.modules.dataModule
import koin.modules.ledModulesModule
import koin.modules.viewModelsModule
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import ui.App
import ui.AppOffScreen

fun main() = application {
    initDatabase()

    KoinApplication(application = {
        modules(
            ledModulesModule,
            dataModule,
            viewModelsModule
        )
    }) {
        val config: BaseConfig = koinInject()
        val interfaceConfig = config.config.interfaceConfig

        if (interfaceConfig.renderer != BaseConfig.Companion.ComposeRenderer.DEFAULT) {
            val renderer = when (interfaceConfig.renderer) {
                BaseConfig.Companion.ComposeRenderer.SOFTWARE -> "SOFTWARE"
                BaseConfig.Companion.ComposeRenderer.OPENGL -> "OPENGL"
                BaseConfig.Companion.ComposeRenderer.METAL -> "METAL"
                else -> "SOFTWARE"
            }

            System.setProperty("skiko.renderApi", renderer)
        }

        // Main Window on the touch display
        val windowState = if (interfaceConfig.mainWindowMaximised) {
            rememberWindowState(
                placement = WindowPlacement.Fullscreen
            )
        } else {
            /**
             * The display for the pi has a resolution of 800x480 at 7"
             * DPI: 133.28
             * DP-Resolution: 960.38415366146 x 576.23049219688
             */
            rememberWindowState(
                placement = WindowPlacement.Floating,
                position = WindowPosition(Alignment.Center),
                size = DpSize(
                    width = 960.dp,
                    height = 540.dp
                )
            )
        }

        Window(
            onCloseRequest = ::exitApplication,
            state = windowState,
        ) {
            App(
                useDarkTheme = interfaceConfig.useDarkTheme,
                onExit = ::exitApplication
            )
        }

        // Off window to display information to the players
        val offWindowState = if (interfaceConfig.offWindowMaximised) {
            rememberWindowState(
                placement = WindowPlacement.Fullscreen
            )
        } else {
            rememberWindowState(
                position = WindowPosition(
                    interfaceConfig.offWindowPosition.first.dp,
                    interfaceConfig.offWindowPosition.second.dp
                ),
                size = DpSize(
                    width = 960.dp,
                    height = 540.dp
                )
            )
        }

        var displayOffWindow by remember { mutableStateOf(interfaceConfig.offWindowOpen) }
        if (displayOffWindow) {
            Window(
                onCloseRequest = { displayOffWindow = false },
                state = offWindowState
            ) {
                AppOffScreen()
            }
        }
    }
}
