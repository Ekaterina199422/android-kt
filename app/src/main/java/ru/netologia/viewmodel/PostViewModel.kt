package ru.netologia.viewmodel

import androidx.lifecycle.ViewModel
import ru.netologia.repository.PostRepository
import ru.netologia.repository.PostRepositoryInMemoryImpl


class PostViewModel : ViewModel() {
    private val repository: PostRepository = PostRepositoryInMemoryImpl()
    val data = repository.getAll()
    fun likeById(id: Long) = repository.likeById(id)
    fun shareById(id: Long) = repository.shareById(id)
}