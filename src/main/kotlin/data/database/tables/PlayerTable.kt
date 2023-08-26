package data.database.tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object PlayerTable : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val name: Column<String> = varchar("name", 255)
    val colorRed: Column<Double> = double("color_red")
    val colorGreen: Column<Double> = double("color_green")
    val colorBlue: Column<Double> = double("color_blue")
    val colorAlpha: Column<Double> = double("color_alpha")

    override val primaryKey = PrimaryKey(id)
}
