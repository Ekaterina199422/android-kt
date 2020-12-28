package ru.netologia.repository

import androidx.lifecycle.LiveData
import ru.netologia.dto.Post

interface PostRepository {
    fun get(): LiveData<Post>
    fun like()
    fun share()
}