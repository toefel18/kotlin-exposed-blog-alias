package nl.toefel.blog.alias.db

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.`java-time`.datetime

object Message: Table("message") {
    val id = long("id").autoIncrement()
    val fromUser = long("from_user_id").references(User.id, onDelete = ReferenceOption.CASCADE)
    val toUser = long("to_user_id").references(User.id, onDelete = ReferenceOption.CASCADE)
    val timestamp = datetime("timestamp")
    val content = text("content")

    override val primaryKey = PrimaryKey(id)
}