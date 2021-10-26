package ru.netologia.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netologia.R
import ru.netologia.databinding.PostCardBinding
import ru.netologia.dto.Post
import ru.netologia.enumeration.AttachmentType
import ru.netologia.enumeration.PostState


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
            if (post.likes > 0) likes.isChecked = post.likedByMe else likes.isChecked = false
            Glide.with(avatar)
                .load("http://10.0.2.2:9999/avatars/${post.authorAvatar}")
                .placeholder(R.drawable.ic_account_circle_48)
                .timeout(10_000)
                .circleCrop()
                .into(avatar)
            btnErrorApiLoad.isVisible = post.state == PostState.Error
            pbProgress.isVisible = post.state == PostState.Progress
            ivStatus.isVisible = post.state == PostState.Success

          if (post.attachment != null && post.attachment.type == AttachmentType.IMAGE) {
                frameAttachView.visibility = View.VISIBLE
                Glide.with(ivImageAttachPost)
                   .load("http://10.0.2.2:9999/images/${post.attachment.url}")
                    .placeholder(R.drawable.ic_attach_error_48)
                    .timeout(10_000)
                    .into(ivImageAttachPost)
            } else {
                frameAttachView.visibility = View.GONE
            }

            binding.root.setOnClickListener {
                onInteractionListener.onPostItemClick(post)
            }
            binding.ivImageAttachPost.setOnClickListener {
                    onInteractionListener.onClickImage(post)

            }
            btnErrorApiLoad.setOnClickListener {
                onInteractionListener.onRetrySendPost(post)

            }
            menuPost.visibility = if (post.ownedByMe) View.VISIBLE else View.GONE

            menuPost.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.option_menu_post)
                    menu.setGroupVisible(R.id.own, post.ownedByMe)
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