package ru.netologia

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netologia.databinding.ActivityMainBinding
import ru.netologia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()
        viewModel.data.observe(this) { post ->
            with(binding) {
                txtAuthor.text = post.author
                txtPublished.text = post.published
                txtContent.text = post.content
                txtLikes.text = numbersString(post.likes)
                txtShare.text = numbersString(post.share)

                imgLike.setImageResource(
                        if (post.likedByMe) R.drawable.ic_liked_24 else R.drawable.ic_favorite_24
                )
            }
        }
        binding.imgLike.setOnClickListener {
            viewModel.like()
        }
        binding.imgShare.setOnClickListener {
            viewModel.share()
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



