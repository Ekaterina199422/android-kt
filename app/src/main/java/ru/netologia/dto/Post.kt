package ru.netologia.dto

data class Post(
        val id: Long,
        val author: String,
        val content: String,
        val published: String,
        val likedByMe: Boolean = false,
        val likes: Int = 0,
        val share: Int = 0
)