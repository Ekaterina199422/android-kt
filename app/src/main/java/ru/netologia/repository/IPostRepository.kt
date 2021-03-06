package ru.netologia.repository


import ru.netologia.dto.Post

interface IPostRepository {

    fun getAll(): List<Post>
    fun likeById(id: Long)
    fun share(id: Long)
    fun removePost(id: Long)
    fun savePost(post: Post)



}
