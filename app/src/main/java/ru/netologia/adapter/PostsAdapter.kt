package ru.netologia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netologia.R
import ru.netologia.databinding.CardPostBinding
import ru.netologia.dto.Post

interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onShare(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onEditCancel(post: Post) {}
}

class PostsAdapter(
    private val onInteractionListener: OnInteractionListener,
): ListAdapter<Post, PostViewHolder>(PostDiffCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,
): RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
        binding.apply {
            txtAuthor.text = post.author
            txtPublished.text = post.published
            txtContent.text = post.content
            txtLikes.text = numbersString(post.likes)
            txtShare.text = numbersString(post.share)

            imgLike.setImageResource(
                if (post.likedByMe) R.drawable.ic_liked_24 else R.drawable.ic_favorite_24
            )
            imgLike.setOnClickListener {
                onInteractionListener.onLike(post)
            }
            imgShare.setOnClickListener {
                onInteractionListener.onShare(post)
            }
            imgMenu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener {item ->
                        when(item.itemId){
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }
                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }
                            else -> false
                        }

                    }
                }.show()
            }
        }
    }

    fun numbersString(number: Int): String {
        return when (number) {
            in 0..999 -> number.toString()
            in 1_000..9_999 -> if ((number / 100) % 10 == 0) {
                (number / 1_000).toString() + "K"
            } else {
                String.format("%.1f", number * 1.0 / 1_000) + "K"
            }
            in 10_000..999_999 -> (number / 1_000).toString() + "K"
            else -> if ((number / 100_000) % 10 == 0) {
                (number / 1000_000).toString() + "M"
            } else {
                String.format("%.1f", number * 1.0 / 1000_000) + "M"
            }

        }
    }
}

class PostDiffCallBack: DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }

}