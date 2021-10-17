package ru.netologia.dto

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import ru.netologia.enumeration.AttachmentType
import ru.netologia.enumeration.PostState

@Entity
data class PostEntity(
        @PrimaryKey(autoGenerate = true)
        val localId: Long,
        val authorId: Long,
        val id: Long,
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
        var attachment: AttachmentEmbeddable?
) {


fun toDto() =
    Post(
        id,
        author,
        authorId,
        authorAvatar,
        content,
        published,
        likes,
        share,
        chat,
        views,
        likedByMe,
        state,
        attachment?.toDto()
    )
companion object {
    fun fromDto(dto: Post) = PostEntity(
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
class PostStateConverter {
    @TypeConverter
    fun toPostState(raw: String): PostState = PostState.values()
            .find { it.name == raw } ?: PostState.Success

    @TypeConverter
    fun fromPostState(postState: PostState): String = postState.name
}

data class AttachmentEmbeddable(
        var url: String,
        var type: AttachmentType
) {
    fun toDto() = Attachment(url, type)

    companion object {
        fun fromDto(dto: Attachment?) = dto?.let {
            AttachmentEmbeddable(it.url, it.type)
        }
    }
}
class Converters {
    @TypeConverter
    fun toAttachmentType(value: String) = enumValueOf<AttachmentType>(value)
    @TypeConverter
    fun fromAttachmentType(value: AttachmentType) = value.name
}

fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity.Companion::fromDto)
fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)