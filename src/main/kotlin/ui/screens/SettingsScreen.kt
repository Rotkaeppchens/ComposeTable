package ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import data.BaseConfig
import data.entities.ModuleConfig
import org.koin.compose.koinInject
import view_models.SettingsViewModel

enum class SettingsNavState {
    INFO,
    MODULE_CONFIG
}

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinInject(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    SettingsScreen(
        config = uiState.config,
        moduleConfigList = uiState.moduleConfigList,
        onModuleMove = { moduleId, newIndex ->
            viewModel.moveModule(moduleId, newIndex)
        },
        onModuleEnable = { moduleId, enabled ->
            viewModel.setModuleEnabled(moduleId, enabled)
        },
        modifier = modifier
    )
}

@Composable
fun SettingsScreen(
    config: BaseConfig.Companion.Config,
    moduleConfigList: List<ModuleConfig>,
    onModuleMove: (id: String, newIndex: Int) -> Unit,
    onModuleEnable: (moduleId: String, enabled: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val (navState, setNavState) = remember { mutableStateOf(SettingsNavState.INFO) }

    Column(
        modifier = modifier
    ) {
        AnimatedContent(
            targetState = navState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { state ->
            when(state) {
                SettingsNavState.INFO -> InfoScreen(config)
                SettingsNavState.MODULE_CONFIG -> ModuleList(
                    moduleConfigList = moduleConfigList,
                    onModuleMove = onModuleMove,
                    onModuleEnable = onModuleEnable
                )
            }
        }
        NavBar(
            navState = navState,
            setNavState = setNavState
        )
    }
}

@Composable
fun InfoScreen(
    config: BaseConfig.Companion.Config,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
            .padding(8.dp)
            .fillMaxSize()
    ) {
        Column {
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
        }
        Column {
            Text("Table Config:", fontWeight = FontWeight.Bold)
            Text(
                "Break Points: ${config.tableConfig.breakPoints}\n" +
                        "Loop sleep time: ${config.tableConfig.loopSleepTime}\n"
            )
            Text("Interface Config:", fontWeight = FontWeight.Bold)
            Text(
                "Dark Theme: ${config.interfaceConfig.useDarkTheme}\n" +
                        "Maximise Window: ${config.interfaceConfig.maximiseWindow}\n" +
                        "Renderer: ${config.interfaceConfig.renderer}\n"
            )
        }
    }
}

@Composable
fun ModuleList(
    moduleConfigList: List<ModuleConfig>,
    onModuleMove: (id: String, newIndex: Int) -> Unit,
    onModuleEnable: (moduleId: String, enabled: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val state = rememberLazyListState()

    LazyColumn(
        state = state,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp),
        modifier = modifier
    ) {
        items(moduleConfigList, key = { it.id }) { item ->
            val currentIndex = moduleConfigList.indexOf(item)

            ModuleItem(
                item = item,
                onModuleEnable = {
                    onModuleEnable(item.id, it)
                },
                onModuleMove = { up ->
                    val newIndex = if (up) {
                        currentIndex - 1
                    } else {
                        currentIndex + 1
                    }
                    onModuleMove(item.id, newIndex)
                },
                moveUpEnabled = currentIndex != 0,
                moveDownEnabled = currentIndex != moduleConfigList.lastIndex,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ModuleItem(
    item: ModuleConfig,
    onModuleMove: (up: Boolean) -> Unit,
    onModuleEnable: (Boolean) -> Unit,
    moveUpEnabled: Boolean,
    moveDownEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val switchIcon: (@Composable () -> Unit)? = if (item.enabled) {
        {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null,
                modifier = Modifier.size(SwitchDefaults.IconSize)
            )
        }
    } else {
        null
    }

    val backgroundColor = if (item.enabled) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(backgroundColor)
            .padding(start = 16.dp, end = 8.dp)
    ) {
        Text(
            text = item.priority.toString(),
            fontWeight = FontWeight.Light,
            fontStyle = FontStyle.Italic,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.width(16.dp))
        FilledTonalIconButton(
            enabled = moveUpEnabled,
            onClick = { onModuleMove(true) }
        ) {
            Icon(
                imageVector = Icons.Outlined.ArrowUpward,
                contentDescription = null
            )
        }
        FilledTonalIconButton(
            enabled = moveDownEnabled,
            onClick = { onModuleMove(false) }
        ) {
            Icon(
                imageVector = Icons.Outlined.ArrowDownward,
                contentDescription = null
            )
        }
        Spacer(Modifier.width(16.dp))
        Text(
            text = item.id,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = item.enabled,
            onCheckedChange = onModuleEnable,
            thumbContent = switchIcon
        )
    }
}

@Composable
fun NavBar(
    navState: SettingsNavState,
    setNavState: (SettingsNavState) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        NavigationBarItem(
            selected = navState == SettingsNavState.INFO,
            onClick = { setNavState(SettingsNavState.INFO) },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null
                )
            },
            label = {
                Text("Info")
            }
        )
        NavigationBarItem(
            selected = navState == SettingsNavState.MODULE_CONFIG,
            onClick = { setNavState(SettingsNavState.MODULE_CONFIG) },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Tune,
                    contentDescription = null
                )
            },
            label = {
                Text("Modules")
            }
        )
    }
}
