package ru.netologia.repository

import androidx.lifecycle.LiveData
import ru.netologia.dto.Post
import ru.netologia.dto.PostEntity


interface IPostRepository {

    val data: LiveData<List<Post>> // отвечает за предоставление данных в виде LiveData
    suspend fun getAll(): List<Post>
    suspend fun unLikeById(id: Long)
    suspend fun likeById(id: Long)
    suspend fun removePost(id: Long):Unit
    suspend fun savePost(post: PostEntity):Long
    suspend fun sendPost(post: Post): Post
    }