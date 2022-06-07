package com.lutalic.backend.entities

import java.time.LocalDate

/**
 * Posts exist inseparably from tables
 * One table can have many posts
 */
data class Post(
    val id: Int,
    var name: String,
    var description: String,
    val date: LocalDate,
    var colour: String,
    val tableId: Int
)
