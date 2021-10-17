package ru.netologia.repository


import kotlinx.coroutines.flow.Flow
import ru.netologia.Auth.AuthState
import ru.netologia.dto.Media
import ru.netologia.dto.MediaUpload
import ru.netologia.dto.Post
import ru.netologia.dto.PostEntity


interface IPostRepository {

    val posts: Flow<List<Post>> // отвечает за предоставление данных в виде LiveData
    suspend fun getAll(): List<Post>
    suspend fun unLikeById(id: Long)
    suspend fun likeById(id: Long)
    suspend fun removePost(id: Long):Unit
    suspend fun savePost(post: PostEntity):Long
    suspend fun sendPost(post: Post): Post
    fun getNewerCount(id: Long): Flow<Int>
    fun getNewList(id: Long) : Flow<List<Post>>
    suspend fun sendNewPost(posts: List<Post>)
    suspend fun saveWithAttachment(post: Post, upload: MediaUpload)
    suspend fun upload(upload: MediaUpload): Media
    suspend fun updateUser(login: String, pass: String): AuthState
    }