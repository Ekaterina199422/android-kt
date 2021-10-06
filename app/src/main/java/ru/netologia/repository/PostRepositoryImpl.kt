package ru.netologia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netologia.api.PostsApi
import ru.netologia.dao.PostDao
import ru.netologia.dto.Post
import ru.netologia.dto.PostEntity



class PostRepositoryImpl(private val dao: PostDao) : IPostRepository {
    override val data: LiveData<List<Post>>
        get() = dao.getAll().map {
            it.sortedWith(Comparator { o1, o2 ->
                when {
                    o1.id == 0L && o2.id == 0L -> o1.localId.compareTo(o2.localId)
                    o1.id == 0L -> -1
                    o2.id == 0L -> 1
                    else -> -o1.id.compareTo(o2.id)
                }
            })
                    .map(PostEntity::toDto)
        }

    override suspend fun getAll(): List<Post> {
        val netPosts = PostsApi.Service.getAll()
        dao.insert(netPosts.map(PostEntity.Companion::fromDto))
        return netPosts
}

        override suspend fun unLikeById(id: Long){
            PostsApi.Service.unLikeById(id)
            dao.likeById(id)

        }

    override suspend fun likeById(id: Long) {
        PostsApi.Service.likeById(id)
        dao.likeById(id)
    }
        override suspend fun removePost(id: Long) {
            dao.removeById(id)
    }
    override suspend fun savePost(post: PostEntity): Long = dao.insert(post)

    override suspend fun sendPost(post: Post): Post = PostsApi.Service.savePost(post)

}
