package ru.netologia.model

data class FeedModelState(
    val loading: Boolean = false,
    val error: Boolean = false,
    val refreshing: Boolean = false,

    ) {
    val errorVisible: Boolean = false
}