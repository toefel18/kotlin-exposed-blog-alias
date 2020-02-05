package nl.toefel.blog.alias.dto

import java.time.LocalDateTime

data class MessageDto(
    val from: String,
    val to: String,
    val timestamp: LocalDateTime,
    val message: String)