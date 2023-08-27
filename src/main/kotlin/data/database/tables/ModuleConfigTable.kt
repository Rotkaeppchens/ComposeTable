package data.database.tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object ModuleConfigTable : Table() {
    val id: Column<String> = varchar("id", 255)
    val enabled: Column<Boolean> = bool("enabled")
    val priority: Column<Int> = integer("priority")

    override val primaryKey = PrimaryKey(id)
}
