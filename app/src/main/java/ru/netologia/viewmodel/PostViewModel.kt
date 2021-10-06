package ru.netologia.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netologia.R
import ru.netologia.db.AppDb
import ru.netologia.dto.Post
import ru.netologia.dto.PostEntity
import ru.netologia.enumeration.PostState
import ru.netologia.model.ApiError
import ru.netologia.model.FeedModel
import ru.netologia.repository.IPostRepository
import ru.netologia.repository.PostRepositoryImpl
import ru.netology.nmedia.utils.SingleLiveEvent
import java.io.IOException


class PostViewModel(application: Application) : AndroidViewModel(application) {

    var isHandledBackPressed: String = ""

    private val empty = Post(
        id = 0,
        content = "",
        author = "Me",
        authorAvatar = "",
        published = ""
    )
    private var localId = 0L
    private val repository: IPostRepository = PostRepositoryImpl (
                AppDb.getInstance(application).postDao()
    )
    private val _state = MutableLiveData(FeedModel())
    val state: LiveData<FeedModel>
        get() = _state

    private val edited = MutableLiveData(empty)
    val posts: LiveData<List<Post>>
        get() = repository.data

    private val _postsRefreshError = SingleLiveEvent<Unit>()
    val postsRefreshError: LiveData<Unit>
        get() = _postsRefreshError
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated
    private val _postCreatedError = SingleLiveEvent<ApiError>()
    val postCreatedError: LiveData<ApiError>
        get() = _postCreatedError
    private val _postRemoveError = SingleLiveEvent<Unit>()
    val postRemoveError: LiveData<Unit>
        get() = _postRemoveError
    private val _postLikeError = SingleLiveEvent<Unit>()
    val postLikeError: LiveData<Unit>
        get() = _postLikeError

    init {
        loadPosts()
    }

    fun like(post: Post) {
        if (post.likedByMe) {
            viewModelScope.launch {
                try {
                    repository.unLikeById(post.id)
                } catch (e: IOException) {
                    _postLikeError.value = Unit
                }

            }
        } else {
            viewModelScope.launch {
                try {
                    repository.likeById(post.id)
                } catch (e: IOException) {
                    _postLikeError.value = Unit
                }
            }
        }
    }

    fun removePost(id: Long) {
        viewModelScope.launch {
            try {
                repository.removePost(id)
            } catch (e: IOException) {
                _postRemoveError.value = Unit
            }
        }
    }

    fun refreshingPosts() {
        viewModelScope.launch {
            _state.value = FeedModel(refreshing = true)
            try {
                val posts = repository.getAll()
                _state.value = FeedModel(empty = posts.isEmpty())
            } catch (e: IOException) {
                _state.value = FeedModel(refreshing = false)
                _postsRefreshError.value = Unit
            }
        }

    }

    fun loadPosts() {
        viewModelScope.launch {
            _state.value = FeedModel(loading = true)
            try {
                val posts = repository.getAll()
                _state.value = FeedModel(empty = posts.isEmpty())
            } catch (e: IOException) {
                _state.value = FeedModel(errorVisible = true)
            }
        }
    }
    fun retrySendPost(post: Post) {
        edited.value = post
        savePost()
    }

    fun savePost() {
        viewModelScope.launch {
            edited.value?.let {
            try {
                val localPost = PostEntity.fromDto(it)
                        .copy(state = PostState.Progress)
                if (it.id == 0L) {
                    localPost.let { entity ->
                        localId = repository.savePost(entity)
                        entity.copy(localId = localId, id = localId)
                    }
                } else {
                    localId = it.id
                }
                val networkPost = repository.sendPost(it)
                repository.savePost(
                        localPost.copy(
                                state = PostState.Success,
                                id = networkPost.id,
                                localId = localId
                        )
                )
                edited.value = empty
            } catch (e: IOException) {
                repository.savePost(
                        PostEntity.fromDto(it)
                                .copy(
                                        state = PostState.Error,
                                        localId = localId,
                                        id = localId
                                )
                )
            }
        }
    }
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
    fun sharePost(post: Post) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, post.content)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(
            intent,
            R.string.chooser_share_post.toString()
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        getApplication<Application>().startActivity(shareIntent)
    }
}

