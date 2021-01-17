package ru.netologia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.netologia.R
import ru.netologia.databinding.CardPostBinding
import ru.netologia.dto.Post

typealias OnLikeListener = (post: Post) -> Unit
typealias OnShareListener = (post: Post) -> Unit

class PostsAdapter(
    private val onLikeListener: OnLikeListener,
    private val onShareListener: OnShareListener,
): RecyclerView.Adapter<PostViewHolder>() {
    var list = emptyList<Post>()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onLikeListener, onShareListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = list[position]
        holder.bind(post)
    }

    override fun getItemCount(): Int = list.size


}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onLikeListener: OnLikeListener,
    private val onShareListener: OnShareListener
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
                onLikeListener(post)
            }
            imgShare.setOnClickListener {
                onShareListener(post)
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