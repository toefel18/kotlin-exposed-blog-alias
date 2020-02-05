package nl.toefel.blog.alias

import nl.toefel.blog.alias.db.UserTable
import nl.toefel.blog.alias.db.MessageTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/**
 * Creates the schema and loads some test data
 */
object DatabaseInitializer {
    val logger: Logger = LoggerFactory.getLogger(DatabaseInitializer::class.java)

    fun createSchemaAndTestData() {
        logger.info("Creating/Updating schema")

        transaction {
            SchemaUtils.createMissingTablesAndColumns(UserTable, MessageTable)
        }

        val users = transaction {
            UserTable.selectAll().count()
        }

        if (users > 0) {
            logger.info("There appears to be data already present, not inserting test data!")
            return
        }

        logger.info("Inserting test transaction")

        transaction {
            val hector = UserTable.insert {
                it[name] = "Hector"
            } get UserTable.id

            val charlotte = UserTable.insert {
                it[name] = "Charlotte"
            } get UserTable.id

            val john = UserTable.insert {
                it[name] = "John"
            } get UserTable.id

            val messageFromHectorToCharlotte = MessageTable.insert {
                it[fromUser] = hector
                it[toUser] = charlotte
                it[timestamp] = LocalDateTime.now().minusHours(1).truncatedTo(ChronoUnit.SECONDS)
                it[content] = "Hello Charlotte, wanna meet for a date with me (hector)?..."
            } get MessageTable.id

            val messageFromJohnToHector = MessageTable.insert {
                it[fromUser] = john
                it[toUser] = hector
                it[timestamp] = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
                it[content] = "Hector, stay away from my girlfriend?..."
            } get MessageTable.id
        }
    }
}