package nl.toefel.blog.alias

import org.jetbrains.exposed.sql.Database
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Starts an in-memory H2 database, creates the schema and loads some test data and exposes a HTTP API
 */
class Main {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(Main::class.java)
        val h2ConnectionString = "jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1;"

        @JvmStatic
        fun main(args: Array<String>) {
            logger.info("H2 database connection string: $h2ConnectionString")
            // also possible to use Spring and spring transactions
            Database.connect(h2ConnectionString, driver = "org.h2.Driver")
            DatabaseInitializer.createSchemaAndTestData()
            Router(8080).start()
        }
    }
}





