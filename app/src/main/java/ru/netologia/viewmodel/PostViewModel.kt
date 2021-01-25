package ru.netologia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import ru.netologia.dto.*
import ru.netologia.model.FeedModel
import ru.netologia.repository.*
import ru.netologia.utils.SingleLiveEvent
import java.io.IOException
import kotlin.concurrent.thread




class PostViewModel(application: Application) : AndroidViewModel(application) {

    var isHandledBackPressed: String = ""

    private val empty = Post(
        id = 0,
        content = "",
        author = "",
        published = "",
        videoUrl = ""
    )
    private val repository: IPostRepository = PostRepositoryImpl()
    private val _state = MutableLiveData(FeedModel())
    val state: LiveData<FeedModel>
        get() = _state
    private val edited = MutableLiveData(empty)
    private val _postsRefresh = SingleLiveEvent<Unit>()
//    val postsRefresh: LiveData<Unit>
//        get() = _postsRefresh

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated


    init {
        loadPosts()
    }

    fun like(id: Long) {
        thread {
            repository.likeById(id)
        }
    }

    fun share(id: Long) = repository.share(id)

    fun removePost(id: Long) {
        thread {
            val old = _state.value?.posts.orEmpty()
            _state.postValue(
                _state.value?.copy(posts = _state.value?.posts.orEmpty()
                    .filter { it.id != id }
                )
            )
            try {
                repository.removePost(id)
            } catch (e: IOException) {
                _state.postValue(_state.value?.copy(posts = old))
            }
        }
    }

    fun refreshingPosts() {
        thread {
            _state.postValue(FeedModel(refreshing = true))
            val old = _state.value?.posts.orEmpty()
            try {
                val posts = repository.getAll()
                _state.postValue(FeedModel(posts = posts))
            } catch (e: IOException) {
                _state.postValue(_state.value?.copy(posts = old))
                _postsRefresh.postValue(Unit)
            }.also { _state::postValue }
        }
    }

    fun loadPosts() {
        thread {
            _state.postValue(FeedModel(loading = true))
            try {
                val posts = repository.getAll()
                _state.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            } catch (e: IOException) {
                _state.postValue(FeedModel(error = true))
            }.also { _state::postValue }
        }
    }

    fun savePost() {
        edited.value?.let {
            thread {
                repository.savePost(it)
                _postCreated.postValue(Unit)
            }
        }
        edited.value = empty
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun editContent(post: Post) {
        edited.value = post
    }

}