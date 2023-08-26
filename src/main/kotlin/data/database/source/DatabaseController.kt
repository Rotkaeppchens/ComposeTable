package data.database.source

import data.database.tables.PlayerTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun initDatabase() {
    Database.connect("jdbc:sqlite:./data.db", "org.sqlite.JDBC")

    transaction {
        SchemaUtils.create(PlayerTable)
    }
}
