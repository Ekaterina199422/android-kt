package ru.netologia.dto

data class Post(
        val id: Long,
        val author: String,
        val content: String,
        val published: String,
        val likes: Int = 0,
        val share: Int = 0,
        val chat: Int = 0,
        val views: Int = 0,
        val videoUrl: String,
        val likedByMe: Boolean = false
)