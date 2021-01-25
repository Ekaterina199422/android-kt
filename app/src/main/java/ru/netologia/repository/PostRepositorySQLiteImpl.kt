package ru.netologia.repository


import ru.netologia.dao.PostDao
import ru.netologia.dto.Post
import ru.netologia.dto.PostEntity

class PostRepositorySQLiteImpl(
    private val dao: PostDao
) : IPostRepository {

    override fun getAll() : List<Post>{ //= dao.getAll().map { list ->
//        list.map {
//            it.toPost()
        TODO("")
//        }
    }

    override fun likeById(id: Long) {
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