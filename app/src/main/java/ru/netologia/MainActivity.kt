package ru.netologia

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import ru.netologia.adapter.IOnInteractionListener
import ru.netologia.adapter.PostsAdapter
import ru.netologia.databinding.ActivityMainBinding
import ru.netologia.dto.Post
import ru.netologia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {
    private val editPostRequestCode = 1
    private val newPostRequestCode = 2
    val viewModel: PostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = PostsAdapter(object : IOnInteractionListener {
            override fun onLike(post: Post) {
                viewModel.like(post.id)
            }

            override fun onShare(post: Post) {
                viewModel.share(post.id)
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(
                        intent,
                        getString(R.string.chooser_share_post)
                )
                startActivity(shareIntent)
            }

            override fun onRemove(post: Post) {
                viewModel.removePost(post.id)
            }

            override fun playVideoPost(post: Post) {
                val videoIntent = Intent(Intent.ACTION_VIEW, Uri.parse(post.videoUrl))
                startActivity(videoIntent)
            }

            override fun onEdit(post: Post) {
                viewModel.editContent(post)
                val intent = Intent(this@MainActivity, EditPost::class.java)
                intent.putExtra("author", post.author)
                intent.putExtra("published", post.published)
                intent.putExtra("content", post.content)
                startActivityForResult(intent, editPostRequestCode)
            }
        })

        binding.fab.setOnClickListener {
            val intent = Intent(this, AddNewPost::class.java)
            startActivityForResult(intent, newPostRequestCode)
        }

        binding.rvPostList.adapter = adapter
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            editPostRequestCode -> {
                if (resultCode != Activity.RESULT_OK) {
                    return
                }
                data?.getStringExtra(Intent.EXTRA_TEXT)?.let {
                    viewModel.changeContent(it)
                    viewModel.savePost()
                }
            }
            newPostRequestCode -> {
                if (resultCode != Activity.RESULT_OK) {
                    return
                }
                data?.getStringExtra(Intent.EXTRA_TEXT)?.let {
                    viewModel.changeContent(it)
                    viewModel.savePost()
                }
            }
        }
    }

}