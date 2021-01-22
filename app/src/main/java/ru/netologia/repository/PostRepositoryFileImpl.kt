package ru.netologia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netologia.dto.Post
import java.text.SimpleDateFormat
import java.util.*

class PostRepositoryFileImpl(
    private val context: Context
) : IPostRepository {
    private val gson = Gson()
    private val type = TypeToken.getParameterized(List::class.java, Post::class.java).type
    private val filename = "posts.json"
    private var nextId = 1L
    private var likesValue = 0
    private var shareValue = 0
    private var posts = emptyList<Post>()
    private val data = MutableLiveData(posts)

    init {
        val file = context.filesDir.resolve(filename)
        if (file.exists()) {
            context.openFileInput(filename).bufferedReader().use {
                posts = gson.fromJson(it, type)
                data.value = posts
            }
        } else {
            sync()
        }
    }


    override fun getAll(): LiveData<List<Post>> = data

    override fun like(id: Long) {
        posts = posts.map {
            if (it.id != id) {
                it
            } else {
                likesValue = it.likes
                if (it.likedByMe) {
                    likesValue--
                    it.copy(likes = likesValue, likedByMe = false)
                } else {
                    likesValue++
                    it.copy(likes = likesValue, likedByMe = true)
                }
            }

        }
        data.value = posts
        sync()
    }

    override fun share(id: Long) {
        posts = posts.map {
            if (it.id != id) {
                it
            } else {
                shareValue = it.share
                shareValue++
                it.copy(share = shareValue)
            }
        }
        data.value = posts
        sync()
    }

    override fun removePost(id: Long) {
        posts = posts.filter { post ->
            post.id != id
        }
        data.value = posts
        sync()
    }

    override fun savePost(post: Post) {
        val dateFormat = SimpleDateFormat("dd MMMM yyyy в HH:mm", Locale.ENGLISH)
        val currentDate = dateFormat.format(Date())
        if (post.id == 0L) {
            posts = listOf(
                post.copy(
                    id = nextId++,
                    author = "Этот пост создан мной",
                    published = currentDate
                )
            ) + posts
            data.value = posts
            sync()
            return
        }
        posts = posts.map {
            if (it.id != post.id) it else it.copy(content = post.content)
        }
        data.value = posts
        sync()
    }

    private fun sync() {
        context.openFileOutput(filename, Context.MODE_PRIVATE).bufferedWriter().use {
            it.write(gson.toJson(posts))
        }
    }
}