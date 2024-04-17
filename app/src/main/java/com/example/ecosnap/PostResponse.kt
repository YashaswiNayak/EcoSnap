package com.example.ecosnap
import java.util.Date
data class PostResponse(
    val _id: String,
    val title: String,
    val content: String,
    val imageUrl: String,
    val user: UserResponse, // Assuming user is a string representing the user ID
    val timestamp: Date,
    val donations: Int
)

data class UserResponse(
    val _id: String,
    val username: String,
    val email: String
)

