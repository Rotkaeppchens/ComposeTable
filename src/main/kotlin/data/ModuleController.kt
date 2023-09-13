package data

import data.entities.ModuleConfig
import data.repositories.ModuleConfigRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ModuleController(
    private val moduleList: List<LedModule>,
    private val configRepo: ModuleConfigRepository
) {
    private val scope = CoroutineScope(Dispatchers.Default)

    val moduleConfigList: StateFlow<List<ModuleConfig>> = configRepo.moduleConfigs.map { list ->
        list.sortedBy { it.priority }
    }.stateIn(scope, SharingStarted.Eagerly, emptyList())

    val sortedModuleList: StateFlow<List<LedModule>> = configRepo.moduleConfigMap.map { configs ->
        moduleList.mapNotNull {
            val enabled = configs[it.moduleId]?.enabled ?: false
            if (enabled) it else null
        }.sortedBy {
            configs[it.moduleId]?.priority ?: 999
        }
    }.stateIn(scope, SharingStarted.Eagerly, emptyList())

    init {
        scope.launch {
            val configMap = configRepo.moduleConfigMap.value

            // Load Module Priorities
            configMap.filterKeys { moduleId ->
                moduleList.none { it.moduleId == moduleId }
            }.forEach { (moduleId, _) ->
                configRepo.deleteModule(moduleId)
            }

            moduleList.forEach { module ->
                if (!configMap.containsKey(module.moduleId)) {
                    configRepo.replaceModule(
                        ModuleConfig(
                            id = module.moduleId,
                            enabled = true,
                            priority = configRepo.getMaxPriority() + 1
                        )
                    )
                }
            }
        }
    }

    fun setModulePriorityList(priorities: Map<String, Int>) {
        scope.launch {
            priorities.forEach { (moduleId, priority) ->
                configRepo.moduleConfigMap.value[moduleId]?.let { config ->
                    configRepo.replaceModule(config.copy(
                        priority = priority
                    ))
                }
            }
        }
    }

    fun setModuleEnabled(moduleId: String, enabled: Boolean) {
        scope.launch {
            configRepo.moduleConfigMap.value[moduleId]?.let { config ->
                configRepo.replaceModule(config.copy(
                    enabled = enabled
                ))
            }
        }
    }
}
