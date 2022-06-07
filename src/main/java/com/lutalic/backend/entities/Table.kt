package com.lutalic.backend.entities

/**
 * Entity describing the table
 * One user can have many tables
 * One table can have many posts and single user as administrator
 */
data class Table(
    val id: Int,
    var name: String,
    var admin: String
)
