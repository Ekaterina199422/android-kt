package ru.netologia

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netologia.adapter.PostsAdapter
import ru.netologia.databinding.ActivityMainBinding
import ru.netologia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

                val binding = ActivityMainBinding.inflate(layoutInflater)
                setContentView(binding.root)

                val viewModel: PostViewModel by viewModels()
                val adapter = PostsAdapter(
                    onLikeListener = {
                        viewModel.likeById(it.id)
                    },
                    onShareListener = {
                        viewModel.shareById(it.id)
                    }
                )
                binding.listView.adapter = adapter
                viewModel.data.observe(this) { posts ->
                    adapter.list = posts
                }
            }
        }