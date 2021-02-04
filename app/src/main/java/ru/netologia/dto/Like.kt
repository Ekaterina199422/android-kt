package ru.netologia.dto

data class Like(
        val userId: Long,
        val userName: String,
        val postId: Long,
        val postAuthor: String
)
