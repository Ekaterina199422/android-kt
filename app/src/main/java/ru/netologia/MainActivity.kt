package ru.netologia

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import ru.netologia.adapter.OnInteractionListener
import ru.netologia.adapter.PostsAdapter
import ru.netologia.databinding.ActivityMainBinding
import ru.netologia.dto.Post
import ru.netologia.util.AndroidUtils
import ru.netologia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {
    companion object {
        private const val NEW_POST_REQUEST_CODE = 1
        private const val EDIT_POST_REQUEST_CODE = 1
    }

    private val viewModel: PostViewModel by viewModels()
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onShare(post: Post) {
                Intent(Intent.ACTION_SEND)
                    .putExtra(Intent.EXTRA_TEXT, post.content)
                    .setType("text/plain")
                    .also {
                        if (it.resolveActivity(packageManager) == null) {
                            Toast.makeText(
                                this@MainActivity,
                                "app not found",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Intent.createChooser(it, "Show text")
                                .also(::startActivity)
                        }
                    }
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onEdit(post: Post) {
                viewModel.edit(post)
            }
        })

        binding.listView.adapter = adapter
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }
        viewModel.edited.observe(this) { post ->
            if (post.id == 0L) {
                return@observe
            }

            startActivityForResult(
                Intent(this, EditPostActivity::class.java)
                    .putExtra(EditPostActivity.EDIT_CONTENT, post.content),
                EDIT_POST_REQUEST_CODE
            )
        }

        binding.btnAddPost.setOnClickListener {
            startActivityForResult(
                Intent(this, NewPostActivity::class.java), NEW_POST_REQUEST_CODE
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == NEW_POST_REQUEST_CODE && requestCode == EDIT_POST_REQUEST_CODE
            && resultCode == RESULT_OK && data != null) {
            val text = data.getStringExtra(Intent.EXTRA_TEXT)
            if (text.isNullOrBlank()) {
                Toast.makeText(
                    this,
                    getString(R.string.error_empty_content),
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            viewModel.changeContent(text.toString())
            viewModel.save()
            AndroidUtils.hideKeyboard(binding.root)
        }
    }
}
