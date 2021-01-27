package ru.netologia.repository

import androidx.lifecycle.LiveData
import ru.netologia.dto.*

interface IPostRepository {

    fun getAll(): LiveData<List<Post>>
    fun like(id: Long)
    fun removePost(id: Long)
    fun savePost(post: Post)
    fun share(id: Long)

}
