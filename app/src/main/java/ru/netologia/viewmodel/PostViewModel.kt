package ru.netologia.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netologia.dto.Post
import ru.netologia.repository.PostRepository
import ru.netologia.repository.PostRepositoryInMemoryImpl


private val empty = Post(
    id = 0,
    author = "",
    content = "",
    published = "",
    likedByMe = false,
    likes = 0,
    share = 0
)

class PostViewModel : ViewModel() {
    private val repository: PostRepository = PostRepositoryInMemoryImpl()
    val data = repository.getAll()
    val edited = MutableLiveData(empty)

    fun save(){
        edited.value?.let {
            repository.save(it)
        }
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post

    }

    fun changeContent(content: String){
        val text = content.trim()
        if (edited.value?.content == text){
            return
        }
        edited.value = edited.value?.copy(content = text)
    }
    fun likeById(id: Long) = repository.likeById(id)
    fun shareById(id: Long) = repository.shareById(id)
    fun removeById(id: Long) = repository.removeById(id)
}
