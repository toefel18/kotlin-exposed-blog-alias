package nl.toefel.blog.alias

import com.fasterxml.jackson.databind.SerializationFeature
import io.javalin.Javalin
import io.javalin.core.JavalinConfig
import io.javalin.http.Context
import io.javalin.plugin.json.JavalinJackson
import nl.toefel.blog.alias.db.Message
import nl.toefel.blog.alias.db.User
import nl.toefel.blog.alias.dto.MessageDto
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Creates the webserver:
 * 1. configures a request logger
 * 2. configures available paths and their handlers
 * 3. transforms database results to DTOs
 */
class Router(val port: Int) {
    private val logger: Logger = LoggerFactory.getLogger(Router::class.java)

    private val app: Javalin = Javalin.create(this::configureJavalin)
        .get("/messages", ::listMessages)

    fun start(): Router {
        app.start(port)
        println("started on port $port, visit http://localhost:8080/messages")
        return this
    }

    fun listMessages(ctx: Context) {
        val allMessages = transaction {
            val fromUser = User.alias("fromUser")
            val toUser = User.alias("toUser")

            Message.join(fromUser, JoinType.INNER, Message.fromUser, fromUser[User.id])
                .join(toUser, JoinType.INNER, Message.toUser, toUser[User.id])
                .selectAll()
                .map { row ->
                    MessageDto(
                        from = row[fromUser[User.name]],
                        to = row[toUser[User.name]],
                        timestamp = row[Message.timestamp],
                        message = row[Message.content]
                    )
                }
        }

        ctx.json(allMessages)
    }

    private fun configureJavalin(cfg: JavalinConfig) {
        cfg.requestLogger(::logRequest).enableCorsForAllOrigins();
        JavalinJackson.getObjectMapper().findAndRegisterModules()
        JavalinJackson.getObjectMapper().configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        JavalinJackson.getObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, true)
    }

    private fun logRequest(ctx: Context, executionTimeMs: Float) =
        logger.info("${ctx.method()} ${ctx.fullUrl()} status=${ctx.status()} durationMs=$executionTimeMs")

}

