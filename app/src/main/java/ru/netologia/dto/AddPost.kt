package ru.netologia.dto

data class AddPost(
        val postId: Long,
        val postAuthor: String,
        val content: String
)
