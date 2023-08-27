package data

import data.entities.ModuleConfig
import data.modules.PlayerSidesModule
import data.modules.TimerModule
import data.repositories.ModuleConfigRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ModuleController(
    private val timerModule: TimerModule,
    private val playerSidesModule: PlayerSidesModule,
    private val configRepo: ModuleConfigRepository
) {
    private val scope = CoroutineScope(Dispatchers.Default)

    private val _moduleList: MutableStateFlow<List<LedModule>> = MutableStateFlow(emptyList())
    private val _moduleConfigMap: MutableStateFlow<Map<String, ModuleConfig>> = MutableStateFlow(emptyMap())

    val moduleConfigList: StateFlow<List<ModuleConfig>> = _moduleConfigMap.map { map ->
        map.map { (_, config) ->
            config
        }.sortedBy { it.priority }
    }.stateIn(scope, SharingStarted.Eagerly, emptyList())

    val moduleList: StateFlow<List<LedModule>> = combine(
        _moduleList,
        _moduleConfigMap
    ) { modules, configs ->
        modules.mapNotNull {
            val enabled = configs[it.moduleId]?.enabled ?: false
            if (enabled) it else null
        }.sortedBy {
            configs[it.moduleId]?.priority ?: 999
        }
    }.stateIn(scope, SharingStarted.Eagerly, emptyList())

    init {
        scope.launch {
            loadModuleList()

            // Load Modules
            val moduleList = listOf(
                timerModule,
                playerSidesModule
            )

            _moduleList.update { moduleList }

            // Load Module Priorities
            _moduleConfigMap.value.filterKeys { moduleId ->
                moduleList.none { it.moduleId == moduleId }
            }.forEach { (moduleId, _) ->
                configRepo.deleteModule(moduleId)
            }

            moduleList.forEach { module ->
                if (!_moduleConfigMap.value.containsKey(module.moduleId)) {
                    configRepo.replaceModule(
                        ModuleConfig(
                            id = module.moduleId,
                            enabled = true,
                            priority = configRepo.getMaxPriority() + 1
                        )
                    )
                }
            }

            loadModuleList()
        }
    }

    private fun loadModuleList() {
        _moduleConfigMap.update {
            configRepo.getModuleList().associateBy { it.id }
        }
    }

    fun setModulePriorityList(priorities: Map<String, Int>) {
        scope.launch {
            priorities.forEach { (moduleId, priority) ->
                _moduleConfigMap.value[moduleId]?.let { config ->
                    configRepo.replaceModule(config.copy(
                        priority = priority
                    ))
                }
            }

            loadModuleList()
        }
    }

    fun setModuleEnabled(moduleId: String, enabled: Boolean) {
        scope.launch {
            _moduleConfigMap.value[moduleId]?.let { config ->
                configRepo.replaceModule(config.copy(
                    enabled = enabled
                ))
            }

            loadModuleList()
        }
    }
}
