package ru.netologia.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netologia.R
import ru.netologia.databinding.PostCardBinding
import ru.netologia.dto.Post

class PostsAdapter(
    private val onInteractionListener: IOnInteractionListener
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val postView = PostCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(postView, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: PostCardBinding,
    private val onInteractionListener: IOnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            likes.text = formatCountToStr(post.likes)
            share.text = formatCountToStr(post.share)
            chatCount.text = formatCountToStr(post.chat)
            viewCount.text = formatCountToStr(post.views)
            if (post.likes > 0) likes.isChecked = post.likedByMe
            if(post.videoUrl != null) {
                binding.frameVideoView.visibility = View.VISIBLE
            } else {
                binding.frameVideoView.visibility = View.GONE
            }
            menuPost.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.option_menu_post)
                    setOnMenuItemClickListener { item ->
                        when(item.itemId) {
                            R.id.postRemove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }
                            R.id.postEdit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }

            likes.setOnClickListener {
                onInteractionListener.onLike(post)

            }
            share.setOnClickListener {
                onInteractionListener.onShare(post)
            }

            binding.frameVideoView.setOnClickListener {
                onInteractionListener.playVideoPost(post)
            }
            binding.buttonPlay.setOnClickListener {
                onInteractionListener.playVideoPost(post)
            }

        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }

}

private fun formatCountToStr(value: Int): String {
    return when (value / 1000) {
        0 -> "$value"
        in 1..9 -> {
            val str = "%.1f".format(value / 1000.0)
                .dropLastWhile { it == '0' }
                .dropLastWhile { it == '.' }
            "${str}K"
        }
        in 10..999 -> {
            val res = value / 1000
            "${res}K"
        }
        else -> {
            val str = "%.1f".format(value / 1000000.0)
                .dropLastWhile { it == '0' }
                .dropLastWhile { it == '.' }
            "${str}М"
        }
    }
}