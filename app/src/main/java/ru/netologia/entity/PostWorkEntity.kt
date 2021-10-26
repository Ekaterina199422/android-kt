package ru.netologia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netologia.dto.Post
import ru.netologia.enumeration.PostState

@Entity
data class PostWorkEntity(
    @PrimaryKey(autoGenerate = true)
    val localId: Long,
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    val likes: Int = 0,
    val share: Int = 0,
    val chat: Int = 0,
    val views: Int = 0,
    val likedByMe: Boolean = false,
    val state: PostState = PostState.Success,
    @Embedded
    var attachment: AttachmentEmbeddable?,
    var uri: String? = null
) {

    companion object {
        fun fromDto(dto: Post) = PostWorkEntity(
            0,
            dto.id,
            dto.authorId,
            dto.author,
            dto.authorAvatar,
            dto.content,
            dto.published,
            dto.likes,
            dto.share,
            dto.chat,
            dto.views,
            dto.likedByMe,
            dto.state,
            AttachmentEmbeddable.fromDto(dto.attachment))
    }

}