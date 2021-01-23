package ru.netologia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.netologia.dto.Post
import ru.netologia.repository.IPostRepository
import ru.netologia.repository.PostRepositoryFileImpl

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val empty = Post(
            id = 0,
            content = "",
            author = "",
            published = "",
            videoUrl = null
    )
    private val repository: IPostRepository = PostRepositoryFileImpl(application)
    val data = repository.getAll()
    private val edited = MutableLiveData(empty)
    fun like(id: Long) = repository.like(id)
    fun share(id: Long) = repository.share(id)
    fun removePost(id: Long) = repository.removePost(id)

    fun savePost() {
        edited.value?.let {
            repository.savePost(it)
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