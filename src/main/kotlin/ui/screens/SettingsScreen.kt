package ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
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
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import view_models.SettingsViewModel
import javax.usb.*


enum class SettingsNavState {
    INFO,
    MAIN_LOOP_INFO,
    MODULE_CONFIG,
    USB_CONFIG
}

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinInject(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    SettingsScreen(
        config = uiState.config,
        loopState = uiState.mainLoopIsActive,
        onStartLoop = { viewModel.startMainLoop() },
        onUpdateLoopState = { viewModel.updateLoopState() },
        moduleConfigList = uiState.moduleConfigList,
        onModuleMove = { moduleId, newIndex ->
            viewModel.moveModule(moduleId, newIndex)
        },
        onModuleEnable = { moduleId, enabled ->
            viewModel.setModuleEnabled(moduleId, enabled)
        },
        usbDeviceList = uiState.usbDeviceList,
        modifier = modifier
    )
}

@Composable
fun SettingsScreen(
    config: BaseConfig.Companion.Config,
    loopState: Boolean,
    onStartLoop: () -> Unit,
    onUpdateLoopState: () -> Unit,
    moduleConfigList: List<ModuleConfig>,
    onModuleMove: (id: String, newIndex: Int) -> Unit,
    onModuleEnable: (moduleId: String, enabled: Boolean) -> Unit,
    usbDeviceList: List<UsbDevice>,
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
                SettingsNavState.MAIN_LOOP_INFO -> MainLoopInfo(
                    loopState = loopState,
                    onStartLoop = onStartLoop,
                    onUpdateLoopState = onUpdateLoopState
                )
                SettingsNavState.MODULE_CONFIG -> ModuleList(
                    moduleConfigList = moduleConfigList,
                    onModuleMove = onModuleMove,
                    onModuleEnable = onModuleEnable
                )
                SettingsNavState.USB_CONFIG -> UsbPage(
                    deviceList = usbDeviceList
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
            InfoTitle("LED Service:")
            InfoLine("Init Strip:", config.ledService.initStrip)
            InfoLine("LED Count:", config.ledService.ledCount)
            InfoLine("GPIO Pin:", config.ledService.gpioPin)
            InfoLine("Frequency (Hz):", config.ledService.frequencyHz)
            InfoLine("DMA:", config.ledService.dma)
            InfoLine("Brightness:", config.ledService.brightness)
            InfoLine("PWM Channel:", config.ledService.pwmChannel)
            InfoLine("Invert:", config.ledService.invert)
            InfoLine("Type:", config.ledService.stripType)
            InfoLine("Clear On Exit:", config.ledService.clearOnExit)
        }
        Column {
            InfoTitle("Table Config:")
            InfoLine("Break Points:", config.tableConfig.breakPoints)
            InfoLine("Loop sleep time:", config.tableConfig.loopSleepTime)

            Spacer(Modifier.height(16.dp))

            InfoTitle("Interface Config:")
            InfoLine("Dark Theme:", config.interfaceConfig.useDarkTheme)
            InfoLine("Maximise Window:", config.interfaceConfig.maximiseWindow)
            InfoLine("Renderer:", config.interfaceConfig.renderer)
            InfoLine("Touch Slop:", config.interfaceConfig.touchSlop)
        }
    }
}

@Composable
fun InfoTitle(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.titleMedium,
        modifier = modifier
            .padding(bottom = 8.dp)
    )
}

@Composable
fun InfoLine(
    title: String,
    value: Any,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = value.toString(),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun MainLoopInfo(
    loopState: Boolean,
    onStartLoop: () -> Unit,
    onUpdateLoopState: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onUpdateLoopState
            ) {
                Text("Update")
            }
            Spacer(Modifier.width(8.dp))
            Switch(
                checked = loopState,
                onCheckedChange = null
            )
        }
        Button(
            onClick = onStartLoop
        ) {
            Text("Start Main Loop")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
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
                modifier = Modifier
                    .animateItemPlacement()
                    .fillMaxWidth()
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
fun UsbPage(
    deviceList: List<UsbDevice>,
    services: UsbServices = UsbHostManager.getUsbServices(),
    modifier: Modifier = Modifier
) {
    var deviceId by remember { mutableStateOf (0.toShort() to 0.toShort()) }
    var selectedDevice: UsbDevice? by remember { mutableStateOf(null) }

    LaunchedEffect(deviceId) {
        selectedDevice = deviceList.find {
            val descriptor = it.usbDeviceDescriptor
            val (vendorId, productId) = deviceId

            descriptor.idVendor() == vendorId && descriptor.idProduct() == productId
        }
    }

    Row(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text("${services.impDescription} Ver.: ${services.impVersion}")
                Text("API Ver.: ${services.apiVersion}")
            }

            UsbDeviceList(
                deviceList = deviceList,
                onDeviceSelected = { vendorId, productId ->
                    deviceId = vendorId to productId
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
        selectedDevice?.let {
            UsbDeviceInfo(
                device = it,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun UsbDeviceList(
    deviceList: List<UsbDevice>,
    onDeviceSelected: (vendorId: Short, productId: Short) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .draggable(
                state = rememberDraggableState { delta ->
                    scope.launch {
                        listState.scrollBy(-delta)
                    }
                },
                orientation = Orientation.Vertical
            )
    ) {
        items(deviceList) { device ->
            val descriptor = device.usbDeviceDescriptor

            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small)
                    .clickable {
                        onDeviceSelected(
                            descriptor.idVendor(),
                            descriptor.idProduct()
                        )
                    }
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer
                    )
                    .padding(8.dp)
            ) {
                Text(
                    text = device.toString(),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
fun UsbDeviceInfo(
    device: UsbDevice,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Card(
        modifier = modifier
            .padding(8.dp)

    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .verticalScroll(
                    state = scrollState
                )
                .draggable(
                    state = rememberDraggableState { delta ->
                        scope.launch {
                            scrollState.scrollBy(-delta)
                        }
                    },
                    orientation = Orientation.Vertical
                )
        ) {
            val producerString = try {
                device.manufacturerString ?: ""
            } catch (e: UsbException) {
                e.message
            } catch (e: UsbDisconnectedException) {
                e.message
            }
            val productString = try {
                device.productString ?: ""
            } catch (e: UsbException) {
                e.message
            } catch (e: UsbDisconnectedException) {
                e.message
            }
            val serialNumber = try {
                device.serialNumberString
            } catch (e: UsbException) {
                e.message
            } catch (e: UsbDisconnectedException) {
                e.message
            }

            val speed = when (device.speed) {
                UsbConst.DEVICE_SPEED_FULL -> "FULL"
                UsbConst.DEVICE_SPEED_LOW -> "LOW"
                else -> "UNKNOWN"
            }

            Text("Device:")
            Text(text = device.toString(), style = MaterialTheme.typography.bodyMedium)
            Text("Producer:")
            Text(text = producerString.toString(), style = MaterialTheme.typography.bodyMedium)
            Text("Product:")
            Text(text = productString.toString(), style = MaterialTheme.typography.bodyMedium)
            Text("Serial Number:")
            Text(text = serialNumber.toString(), style = MaterialTheme.typography.bodyMedium)
            Text("Speed: $speed")

            Spacer(Modifier.height(8.dp))
            Text(device.usbDeviceDescriptor.toString())

            try {
                device.parentUsbPort
            } catch (e: UsbDisconnectedException) {
                null
            } ?.let { port ->
                Text("Connected to port: ${port.portNumber}")
                Text("Parent: ${port.usbHub}")
            }

            // Process all configurations
            device.usbConfigurations.forEach { configuration ->
                if (configuration is UsbConfiguration) {
                    // Dump configuration descriptor
                    Text(configuration.usbConfigurationDescriptor.toString())

                    // Process all interfaces
                    configuration.usbInterfaces?.let { usbInterface ->
                        if (usbInterface is UsbInterface) {
                            // Dump the interface descriptor
                            Text("Interface Descriptor: $usbInterface.usbInterfaceDescriptor")

                            // Process all endpoints
                            usbInterface.usbEndpoints?.let { endpoint ->

                                if (endpoint is UsbEndpoint) {
                                    // Dump the endpoint descriptor
                                    Text(endpoint.usbEndpointDescriptor.toString())
                                }
                            }
                        }
                    }
                }
            }
        }
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
            selected = navState == SettingsNavState.MAIN_LOOP_INFO,
            onClick = { setNavState(SettingsNavState.MAIN_LOOP_INFO) },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.AllInclusive,
                    contentDescription = null
                )
            },
            label = {
                Text("Main Loop")
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
        NavigationBarItem(
            selected = navState == SettingsNavState.USB_CONFIG,
            onClick = { setNavState(SettingsNavState.USB_CONFIG) },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Usb,
                    contentDescription = null
                )
            },
            label = {
                Text("USB")
            }
        )
    }
}
