package ru.netologia.repository

import androidx.lifecycle.LiveData
import ru.netologia.dto.Post

interface PostRepository {
    fun getAll(): LiveData<List<Post>>
    fun likeById(id: Long)
    fun shareById(id: Long)
}