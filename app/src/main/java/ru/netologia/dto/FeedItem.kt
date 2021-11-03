package ru.netologia.dto

import ru.netologia.enumeration.AttachmentType
import ru.netologia.enumeration.PostState

sealed class FeedItem{
    abstract val id: Long
}
//на основании этих данных мы будем смотерть что нам необходимо отразить по id
data class Ad( // рекламу
    override val id: Long,
    val url: String,
    val image: String,
) : FeedItem()

data class Post( // посты
    override val id: Long = 0,
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
) : FeedItem()

data class Attachment(
    val url: String,
    val type: AttachmentType,
)