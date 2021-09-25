package ru.netologia.model

import ru.netologia.dto.Post

data class FeedModel(
        val posts: List<Post> = emptyList(),
        val loading: Boolean = false,
        val errorVisible: Boolean = false,
        val error: ApiError? = null,
        val empty: Boolean = false,
        val refreshing: Boolean = false
)
