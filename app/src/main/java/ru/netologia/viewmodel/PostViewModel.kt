package ru.netologia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netologia.dto.Post
import ru.netologia.model.FeedModel
import ru.netologia.repository.IPostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
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

                override fun onError(e: Exception) {
                    _state.value = FeedModel(error = true)
                }
            })
        } else {
            repository.likeById(post, object : IPostRepository.LikeByIdCallback {
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

                override fun onError(e: Exception) {
                    _state.value = FeedModel(error = true)
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

            override fun onError(e: Exception) {
                _state.postValue(FeedModel(posts = old))
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

            override fun onError(e: Exception) {
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

            override fun onError(e: Exception) {
                _state.postValue(FeedModel(error = true))
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

                override fun onError(e: Exception) {
                    _postCreated.postValue(Unit)
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
