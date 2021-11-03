package ru.netologia.adapter

import ru.netologia.dto.Ad
import ru.netologia.dto.Post

interface IOnInteractionListener {
    fun onLike(post: Post)
    fun onShare(post: Post)
    fun onRemove(post: Post)
    fun onEdit(post: Post)
    fun onClickImage(post: Post)
    fun onPostItemClick(post: Post)
    fun onRetrySendPost(post: Post)
    fun onAdClick(ad: Ad)
}