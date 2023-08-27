package view_models

import data.BaseConfig
import data.ModuleController
import data.entities.ModuleConfig
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import view_models.base.ViewModel

class SettingsViewModel(
    baseConfig: BaseConfig,
    private val moduleController: ModuleController
) : ViewModel() {
    val uiState: StateFlow<UiState> = moduleController.moduleConfigList.map {
        UiState(
            config = baseConfig.config,
            moduleConfigList = it
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UiState(
        config = baseConfig.config,
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

    data class UiState (
        val config: BaseConfig.Companion.Config,
        val moduleConfigList: List<ModuleConfig>
    )
}
