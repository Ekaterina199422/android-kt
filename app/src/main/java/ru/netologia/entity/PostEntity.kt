package ru.netologia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import ru.netologia.dto.Post
import ru.netologia.enumeration.AttachmentType
import ru.netologia.enumeration.PostState

@Entity
data class PostEntity(
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
        var attachment: AttachmentEmbeddable?
) {


fun toDto() =
    Post(
        id,
        authorId,
        author,
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

    fun fromWorkDto(dto: PostWorkEntity) = PostEntity(
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
        dto.attachment
    )


}

}
class PostStateConverter {
    @TypeConverter
    fun toPostState(raw: String): PostState = PostState.values()
            .find { it.name == raw } ?: PostState.Success

    @TypeConverter
    fun fromPostState(postState: PostState): String = postState.name
}


class Converters {
    @TypeConverter
    fun toAttachmentType(value: String) = enumValueOf<AttachmentType>(value)
    @TypeConverter
    fun fromAttachmentType(value: AttachmentType) = value.name
}

fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity.Companion::fromDto)
fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)