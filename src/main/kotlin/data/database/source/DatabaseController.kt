package data.database.source

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseController {
    init {
        Database.connect("jdbc:sqlite:./data.db", "org.sqlite.JDBC")

        transaction {

        }
    }
}
