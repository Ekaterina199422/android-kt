package ru.netologia.dao

import ru.netologia.dto.Post

interface PostDao {
    fun getAll(): List<Post>
    fun save(post: Post): Post
    fun likeById(id: Long)
    fun removeById(id: Long)
    fun share(id: Long)

}