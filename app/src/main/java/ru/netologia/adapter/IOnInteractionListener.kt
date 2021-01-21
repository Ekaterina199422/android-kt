package ru.netologia.adapter

import ru.netologia.dto.*

interface IOnInteractionListener {
    fun onLike(post: Post)
    fun onShare(post: Post)
    fun onRemove(post: Post)
    fun onEdit(post: Post)
    fun playVideoPost(post: Post)
}