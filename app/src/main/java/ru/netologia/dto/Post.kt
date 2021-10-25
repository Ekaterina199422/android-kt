package ru.netologia.dto

import ru.netologia.enumeration.PostState

data class Post(
        val id: Long = 0,
        val authorId: Long = 0,
        val author: String  = " ",
        val authorAvatar: String = " ",
        val content: String = " ",
        val published: String = " ",
        val likes: Int = 0,
        val share: Int = 0,
        val chat: Int = 0,
        val views: Int = 0,
        val likedByMe: Boolean = false,
        val state: PostState = PostState.Success,
        val attachment: Attachment? = null,
        val ownedByMe: Boolean = false
)
