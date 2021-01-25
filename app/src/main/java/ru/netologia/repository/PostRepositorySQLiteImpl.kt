package ru.netologia.repository


import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netologia.dao.PostDao
import ru.netologia.dto.Post
import ru.netologia.dto.toPost
import ru.netologia.dto.PostEntity
import ru.netologia.dto.toPost
import java.text.SimpleDateFormat
import java.util.*

class PostRepositorySQLiteImpl(
        private val dao: PostDao
) : IPostRepository {

    override fun getAll(): LiveData<List<Post>> = dao.getAll().map { list ->
        list.map {
            it.toPost()
        }

    }

    override fun like(id: Long) {
        dao.likeById(id)

    }

    override fun share(id: Long) {
        dao.share(id)

    }

    override fun removePost(id: Long) {
        dao.removeById(id)

    }

    override fun savePost(post: Post) {
        dao.save(PostEntity.fromPost(post))
    }

}