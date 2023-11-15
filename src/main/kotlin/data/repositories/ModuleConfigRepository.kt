package data.repositories

import data.database.tables.ModuleConfigTable
import data.entities.ModuleConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class ModuleConfigRepository {
    private val _moduleConfigs: MutableStateFlow<List<ModuleConfig>> = MutableStateFlow(emptyList())

    val moduleConfigs: StateFlow<List<ModuleConfig>>
        get() = _moduleConfigs

    val moduleConfigMap: StateFlow<Map<String, ModuleConfig>> = _moduleConfigs.map { list ->
        list.associateBy { it.id }
    }.stateIn(CoroutineScope(Dispatchers.Default), SharingStarted.Eagerly, emptyMap())

    init {
        transaction {
            loadModuleList()
        }
    }

    private fun loadModuleList() {
        _moduleConfigs.update {
            ModuleConfigTable.selectAll().map { row ->
                ModuleConfig(
                    id = row[ModuleConfigTable.id],
                    enabled = row[ModuleConfigTable.enabled],
                    priority = row[ModuleConfigTable.priority],
                )
            }
        }
    }

    fun replaceModule(config: ModuleConfig) {
        transaction {
            ModuleConfigTable.replace {
                it[id] = config.id
                it[enabled] = config.enabled
                it[priority] = config.priority
            }

            loadModuleList()
        }
    }

    fun deleteModule(moduleId: String) {
        transaction {
            ModuleConfigTable.deleteWhere { id eq moduleId }
            loadModuleList()
        }
    }

    fun getMaxPriority(): Int {
        return transaction {
            ModuleConfigTable
                .slice(ModuleConfigTable.priority)
                .selectAll()
                .orderBy(ModuleConfigTable.priority to SortOrder.DESC)
                .limit(1)
                .firstOrNull()
                ?.get(ModuleConfigTable.priority) ?: 0
        }
    }
}
