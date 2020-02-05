package nl.toefel.blog.alias.db

import org.jetbrains.exposed.sql.Table

object User: Table("user") {
    val id = long("id").autoIncrement()
    val name = varchar("owner_type", 64)

    override val primaryKey = PrimaryKey(id)
}