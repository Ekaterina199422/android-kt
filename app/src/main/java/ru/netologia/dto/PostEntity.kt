package ru.netologia.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    val likes: Int = 0,
    val share: Int = 0,
    val chat: Int = 0,
    val views: Int = 0,
    val videoUrl: String,
    val likedByMe: Boolean
) {
    companion object {
        fun fromPost(post: Post): PostEntity =
            with(post) {
                PostEntity(
                    id,
                    author,
                        authorAvatar,
                        content,
                    published,
                    likes,
                    share,
                    chat,
                    views,
                    videoUrl,
                    likedByMe
                )
            }
    }

}
fun PostEntity.toPost(): Post =
    Post(
        id,
        author,
            authorAvatar,
            content,
        published,
        likes,
        share,
        chat,
        views,
        videoUrl,
        likedByMe
    )
