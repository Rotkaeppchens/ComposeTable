package data.repositories

import data.database.tables.ModuleConfigTable
import data.entities.ModuleConfig
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.replace
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class ModuleConfigRepository {
    fun getModuleList(): List<ModuleConfig> {
        return transaction {
            ModuleConfigTable.selectAll().map {
                ModuleConfig(
                    id = it[ModuleConfigTable.id],
                    enabled = it[ModuleConfigTable.enabled],
                    priority = it[ModuleConfigTable.priority],
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
        }
    }

    fun deleteModule(moduleId: String) {
        transaction {
            ModuleConfigTable.deleteWhere { id eq moduleId }
        }
    }

    fun getMaxPriority(): Int {
        return transaction {
            ModuleConfigTable
                .selectAll()
                .maxByOrNull { ModuleConfigTable.priority }
                ?.let {
                    it[ModuleConfigTable.priority]
                } ?: 0
        }
    }
}
