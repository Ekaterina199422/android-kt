package ru.netologia.repository


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netologia.dao.PostDao
import ru.netologia.dto.Post

class PostRepositorySQLiteImpl(
        private val dao: PostDao
) : IPostRepository {
    private var posts = emptyList<Post>()
    private val data = MutableLiveData(posts)

    init {
        posts = dao.getAll()
        data.value = posts
    }

    override fun getAll(): LiveData<List<Post>> = data

    override fun like(id: Long) {
        dao.likeById(id)
        posts = posts.map {
            if (it.id != id) it else it.copy(
                    likedByMe = !it.likedByMe,
                    likes = if (it.likedByMe) it.likes - 1 else it.likes + 1
            )
        }
        data.value = posts
    }

    override fun share(id: Long) {
        dao.share(id)
        posts = posts.map {
            if (it.id != id) it else it.copy(
                    share = it.share + 1
            )
        }
        data.value = posts
    }

    override fun removePost(id: Long) {
        dao.removeById(id)
        posts = posts.filter { it.id != id }
        data.value = posts
    }

    override fun savePost(post: Post) {
        val id = post.id
        val saved = dao.save(post)
        posts = if (id == 0L) {
            listOf(saved) + posts
        } else {
            posts.map {
                if (it.id != id) it else saved
            }
        }
        data.value = posts
    }

}