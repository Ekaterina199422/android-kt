package ru.netologia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netologia.dto.Post
import ru.netologia.model.ApiError
import ru.netologia.model.FeedModel
import ru.netologia.repository.IPostRepository
import ru.netologia.repository.PostRepositoryImpl
import ru.netology.nmedia.utils.SingleLiveEvent


class PostViewModel(application: Application) : AndroidViewModel(application) {

    var isHandledBackPressed: String = ""

    private val empty = Post(
            id = 0,
            content = "",
            author = "Me",
            authorAvatar = "",
            published = "",
            videoUrl = ""
    )
    private val repository: IPostRepository = PostRepositoryImpl()
    private val _state = MutableLiveData(FeedModel())
    val state: LiveData<FeedModel>
        get() = _state
    private val edited = MutableLiveData(empty)
    private val _postsRefreshError = SingleLiveEvent<Unit>()
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated
    private val _postCreatedError = SingleLiveEvent<ApiError>()
    val postCreatedError: LiveData<ApiError>
        get() = _postCreatedError
    private val _postRemoveError = SingleLiveEvent<ApiError>()
    val postRemoveError: LiveData<ApiError>
        get() = _postRemoveError
    private val _postLikeError = SingleLiveEvent<ApiError>()
    val postLikeError: LiveData<ApiError>
        get() = _postLikeError

    init {
        loadPosts()
    }

    fun like(post: Post) {
        if (post.likedByMe) {
            repository.unLikeById(post.id, object : IPostRepository.LikeByIdCallback {
                override fun onSuccess(post: Post) {
                    _state.postValue(
                            FeedModel(posts = _state.value?.posts.orEmpty().map {
                                if (it.id != post.id) it else it.copy(
                                        likes = post.likes,
                                        likedByMe = post.likedByMe,
                                        videoUrl = "test"
                                )
                            })
                    )
                }

                override fun onError(e: ApiError) {
                    _postLikeError.value = e
                }
            })
        } else {
            repository.likeById(post.id, object : IPostRepository.LikeByIdCallback {
                override fun onSuccess(post: Post) {
                    _state.postValue(
                            FeedModel(posts = _state.value?.posts.orEmpty().map {
                                if (it.id != post.id) it else it.copy(
                                        likes = post.likes,
                                        likedByMe = post.likedByMe,
                                        videoUrl = "test"
                                )
                            })
                    )
                }

                override fun onError(e: ApiError) {
                    _postLikeError.value = e
                }
            })
        }
    }

    fun removePost(id: Long) {
        val old = _state.value?.posts.orEmpty()
        repository.removePost(id, object : IPostRepository.RemovePostCallback {
            override fun onSuccess() {
                _state.postValue(
                        FeedModel(posts = _state.value?.posts.orEmpty()
                                .filter { it.id != id }
                        )
                )
            }

            override fun onError(e: ApiError) {
                _state.postValue(FeedModel(posts = old))
                _postRemoveError.value = e
            }

        })
    }

    fun refreshingPosts() {
        val old = _state.value?.posts.orEmpty()
        _state.value = FeedModel(refreshing = true)
        repository.getAllAsync(object : IPostRepository.GetAllCallback {
            override fun onSuccess(posts: List<Post>) {
                _state.postValue(FeedModel(posts = posts))
            }

            override fun onError(e: ApiError) {
                _state.value?.copy(refreshing = false)
                _state.postValue(FeedModel(posts = old))
                _postsRefreshError.postValue(Unit)
            }

        })
    }

    fun loadPosts() {
        _state.value = FeedModel(loading = true)
        repository.getAllAsync(object : IPostRepository.GetAllCallback {
            override fun onSuccess(posts: List<Post>) {
                _state.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError(e: ApiError) {
                _state.postValue(FeedModel(errorVisible = true, error = e))
            }
        })
    }

    fun savePost() {
        edited.value?.let {post ->
            repository.savePost(post, object : IPostRepository.SavePostCallback {
                override fun onSuccess(post: Post) {
                    _state.postValue(_state.value?.posts?.let {
                        FeedModel(posts = it.plus(post))
                    })
                }

                override fun onError(e: ApiError) {
                    _postCreatedError.value = e
                }
            })
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

}
