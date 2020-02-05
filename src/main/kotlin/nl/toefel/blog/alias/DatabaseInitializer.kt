package nl.toefel.blog.alias

import nl.toefel.blog.alias.db.User
import nl.toefel.blog.alias.db.Message
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
            SchemaUtils.createMissingTablesAndColumns(User, Message)
        }

        val users = transaction {
            User.selectAll().count()
        }

        if (users > 0) {
            logger.info("There appears to be data already present, not inserting test data!")
            return
        }

        logger.info("Inserting test transaction")

        transaction {
            val hector = User.insert {
                it[name] = "Hector"
            } get User.id

            val charlotte = User.insert {
                it[name] = "Charlotte"
            } get User.id

            val john = User.insert {
                it[name] = "John"
            } get User.id

            val messageFromHectorToCharlotte = Message.insert {
                it[fromUser] = hector
                it[toUser] = charlotte
                it[timestamp] = LocalDateTime.now().minusHours(1).truncatedTo(ChronoUnit.SECONDS)
                it[content] = "Hello Charlotte, wanna meet for a date with me (hector)?..."
            } get Message.id

            val messageFromJohnToHector = Message.insert {
                it[fromUser] = john
                it[toUser] = hector
                it[timestamp] = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
                it[content] = "Hector, stay away from my girlfriend?..."
            } get Message.id
        }
    }
}