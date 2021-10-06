package ru.netologia.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import ru.netologia.enumeration.PostState

@Entity
data class PostEntity(
        @PrimaryKey(autoGenerate = true)
        val localId: Long,
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
        val state: PostState = PostState.Success
) {


fun toDto() =
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
        likedByMe,
        state
    )
companion object {
    fun fromDto(dto: Post) = PostEntity(
            0,
            dto.id,
            dto.author,
            dto.authorAvatar,
            dto.content,
            dto.published,
            dto.likes,
            dto.share,
            dto.chat,
            dto.views,
            dto.likedByMe,
            dto.state


    )
}
    class PostConverter{
        @TypeConverter
        fun toPostState(raw: String) : PostState = PostState.values()
                .find { it.name == raw } ?: PostState.Success

        @TypeConverter
        fun fromPostState(postState: PostState): String = postState.name
    }

}
