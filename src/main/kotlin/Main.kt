
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import data.BaseConfig
import koin.modules.dataModule
import koin.modules.ledModulesModule
import koin.modules.viewModelsModule
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import ui.App

fun main() = application {
    KoinApplication(application = {
        modules(
            ledModulesModule,
            dataModule,
            viewModelsModule
        )
    }) {
        val config: BaseConfig = koinInject()
        val interfaceConfig = config.config.interfaceConfig

        val windowState = if (interfaceConfig.maximiseWindow) {
            rememberWindowState(
                placement = WindowPlacement.Maximized
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
                    height = 576.dp
                )
            )
        }

        Window(
            onCloseRequest = ::exitApplication,
            state = windowState,
            undecorated = true,
            resizable = false,
            alwaysOnTop = true
        ) {
            App(
                useDarkTheme = interfaceConfig.useDarkTheme,
                onExit = ::exitApplication
            )
        }
    }
}
