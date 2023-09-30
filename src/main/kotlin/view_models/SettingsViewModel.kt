package view_models

import data.BaseConfig
import data.LedController
import data.ModuleController
import data.entities.ModuleConfig
import kotlinx.coroutines.flow.*
import view_models.base.ViewModel

class SettingsViewModel(
    baseConfig: BaseConfig,
    private val ledController: LedController,
    private val moduleController: ModuleController
) : ViewModel() {
    private val _mainLoopIsActive: MutableStateFlow<Boolean> = MutableStateFlow(ledController.loopIsActive)

    val uiState: StateFlow<UiState> = combine(
        _mainLoopIsActive,
        moduleController.moduleConfigList
    ) { mainLoopIsActive, moduleConfigList ->
        UiState(
            config = baseConfig.config,
            mainLoopIsActive = mainLoopIsActive,
            moduleConfigList = moduleConfigList
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UiState(
        config = baseConfig.config,
        mainLoopIsActive = ledController.loopIsActive,
        moduleConfigList = emptyList()
    ))

    fun setModuleEnabled(moduleId: String, enabled: Boolean) = moduleController.setModuleEnabled(moduleId, enabled)

    fun moveModule(moduleId: String, newIndex: Int) {
        val moduleList = moduleController.moduleConfigList.value.toMutableList()

        val moduleIndex = moduleList.indexOfFirst { it.id == moduleId }
        val removedModule = moduleList.removeAt(moduleIndex)

        moduleList.add(newIndex, removedModule)

        moduleController.setModulePriorityList(
            moduleList.mapIndexed { i, module ->
                module.id to i
            }.toMap()
        )
    }

    fun startMainLoop() = ledController.startLoop()

    fun updateLoopState() {
        _mainLoopIsActive.update {
            ledController.loopIsActive
        }
    }

    data class UiState (
        val config: BaseConfig.Companion.Config,
        val mainLoopIsActive: Boolean,
        val moduleConfigList: List<ModuleConfig>
    )
}
