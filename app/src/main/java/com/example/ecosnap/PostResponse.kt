package com.example.ecosnap
import java.util.Date
data class PostResponse(
    val _id: String,
    val title: String,
    val content: String,
    val imageUrl: String,
    val user: String, // Assuming user is a string representing the user ID
    val timestamp: Date
)

