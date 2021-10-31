package ru.netologia.viewmodel

import android.net.Uri
import androidx.core.net.toFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import androidx.work.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netologia.auth.AppAuth
import ru.netologia.dto.MediaUpload
import ru.netologia.dto.Photo
import ru.netologia.dto.Post
import ru.netologia.entity.PostEntity
import ru.netologia.enumeration.PostState
import ru.netologia.model.FeedModel
import ru.netologia.repository.IPostRepository
import ru.netologia.work.RemovePostWorker
import ru.netologia.work.SavePostWorker
import ru.netology.nmedia.utils.SingleLiveEvent
import java.io.IOException
import javax.inject.Inject


private val empty = Post(
    id = 0,
    content = "",
    authorId = 0,
    author = "",
    authorAvatar = "",
    published = ""
)

private val noPhoto = Photo()

@ExperimentalCoroutinesApi
@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository:IPostRepository,
    private val workManager: WorkManager,
    private val auth: AppAuth,
    ) : ViewModel() {

    private var cached: Flow<PagingData<Post>> = repository
        .posts
        .cachedIn(viewModelScope)

    val posts: Flow<PagingData<Post>> = auth.authStateFlow
        .flatMapLatest { (myId, _) ->
            repository.posts.map { pagingData ->
                pagingData.map { post ->
                    post.copy(ownedByMe = post.authorId == myId)
                }
            }
        }
    var isHandledBackPressed: String = ""


    private val _state = MutableLiveData(FeedModel())
    val state: LiveData<FeedModel>
        get() = _state

    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated



    private val _postsRefreshError = SingleLiveEvent<Unit>()
    val postsRefreshError: LiveData<Unit>
        get() = _postsRefreshError


    private val _postRemoveError = SingleLiveEvent<Unit>()
    val postRemoveError: LiveData<Unit>
        get() = _postRemoveError

    private val _postLikeError = SingleLiveEvent<Unit>()
    val postLikeError: LiveData<Unit>
        get() = _postLikeError


  /*  val newPosts = posts.switchMap {// switchMap позваляет нам подписаться на именения в наших постах
        repository.getNewerCount(it.firstOrNull()?.id ?: 0L) // вызвыем функцию getNewerCount с репозитория и передаем id самого первого поста
                .asLiveData() // возращеается новая LiveData
    }*/
    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<Photo>
        get() = _photo

    init {
        loadPosts()
    }

    fun changePhoto(uri: Uri?) {
        _photo.value = Photo(uri)
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
                val data = workDataOf(RemovePostWorker.postKey to id)
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
                val request = OneTimeWorkRequestBuilder<RemovePostWorker>()
                    .setInputData(data)
                    .setConstraints(constraints)
                    .build()
                workManager.enqueue(request)
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
        var localId = 0L
        viewModelScope.launch {
            edited.value?.let { post ->
                _postCreated.value = Unit
               try {
                   val id = when (_photo.value) {
                       noPhoto -> repository.prepareWork(post)
                       else -> _photo.value?.uri?.let {
                           repository.saveWork(post, MediaUpload(it.toFile()))
                       }

                   }
                   val data = workDataOf(SavePostWorker.postKey to id)
                   val constraints = Constraints.Builder()
                       .setRequiredNetworkType(NetworkType.CONNECTED)
                       .build()
                   val request = OneTimeWorkRequestBuilder<SavePostWorker>()
                       .setInputData(data)
                       .setConstraints(constraints)
                       .build()
                   workManager.enqueue(request)
            } catch (e: IOException) {
                   repository.savePost(
                    PostEntity.fromDto(post)
                            .copy(
                                state = PostState.Error,
                                localId = localId,
                                id = localId
                            )
            )

        }
        }
    }
    edited.value = empty
    _photo.value = noPhoto
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


