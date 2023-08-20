package ui.screens

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.github.mbelling.ws281x.LedStripType
import data.BaseConfig
import org.koin.compose.koinInject
import ui.theme.AppTheme
import view_models.SettingsViewModel
import kotlin.time.Duration

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinInject(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    SettingsScreen(
        config = uiState.config,
        modifier = modifier
    )
}

@Composable
fun SettingsScreen(
    config: BaseConfig.Companion.Config,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text("LED Service:", fontWeight = FontWeight.Bold)
        Text(
            "Init Strip: ${config.ledService.initStrip}\n" +
                    "LED Count: ${config.ledService.ledCount}\n" +
                    "GPIO: ${config.ledService.gpioPin}\n" +
                    "Frequency: ${config.ledService.frequencyHz}\n" +
                    "DMA: ${config.ledService.dma}\n" +
                    "Brightness: ${config.ledService.brightness}\n" +
                    "PWM channel: ${config.ledService.pwmChannel}\n" +
                    "Invert: ${config.ledService.invert}\n" +
                    "Type: ${config.ledService.stripType}\n" +
                    "Clear on exit: ${config.ledService.clearOnExit}\n"
        )
        Text("Table Config:", fontWeight = FontWeight.Bold)
        Text(
            "Break Points: ${config.tableConfig.breakPoints}\n" +
                    "Loop sleep time: ${config.tableConfig.loopSleepTime}\n"
        )
    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    AppTheme {
        SettingsScreen(
            config = BaseConfig.Companion.Config(
                ledService = BaseConfig.Companion.LedService(
                    initStrip = true,
                    ledCount = 286,
                    gpioPin = 10,
                    frequencyHz = 800000,
                    dma = 0,
                    brightness = 255,
                    pwmChannel = 18,
                    invert = false,
                    stripType = LedStripType.WS2811_STRIP_GBR,
                    clearOnExit = true,
                ),
                tableConfig = BaseConfig.Companion.TableConfig(
                    breakPoints = listOf(1, 2, 3, 4),
                    loopSleepTime = Duration.ZERO
                ),
                interfaceConfig = BaseConfig.Companion.InterfaceConfig(
                    useDarkTheme = true,
                    maximiseWindow = false
                )
            )
        )
    }
}
