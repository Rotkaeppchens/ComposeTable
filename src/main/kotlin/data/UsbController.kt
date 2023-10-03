package data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.usb.*
import javax.usb.event.UsbServicesEvent
import javax.usb.event.UsbServicesListener

class UsbController : UsbServicesListener {
    private val scope = CoroutineScope(Dispatchers.Default)

    private val _deviceList: MutableStateFlow<List<UsbDevice>> = MutableStateFlow(emptyList())

    val deviceList: StateFlow<List<UsbDevice>>
        get() = _deviceList

    val usbServices = UsbHostManager.getUsbServices()
    val rootHub = usbServices.rootUsbHub

    init {
        updateDeviceList()
        usbServices.addUsbServicesListener(this)
    }

    override fun usbDeviceAttached(event: UsbServicesEvent?) {
        updateDeviceList()
    }

    override fun usbDeviceDetached(event: UsbServicesEvent?) {
        updateDeviceList()
    }

    private fun updateDeviceList() {
        scope.launch {
            _deviceList.update {
                flattenEndDevice(rootHub).sortedBy { it.toString() }
            }
        }
    }

    private fun flattenEndDevice(usbHub: UsbHub): List<UsbDevice> {
        val usbList: MutableList<UsbDevice> = mutableListOf()

        usbHub.attachedUsbDevices.filterIsInstance<UsbDevice>().map {
            if (it.isUsbHub) {
               usbList.addAll(flattenEndDevice(it as UsbHub))
            } else usbList.add(it)
        }

        return usbList
    }
}
