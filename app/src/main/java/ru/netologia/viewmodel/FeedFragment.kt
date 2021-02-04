package ru.netologia

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import ru.netologia.EditPost.Companion.authorEdit
import ru.netologia.EditPost.Companion.contentEdit
import ru.netologia.EditPost.Companion.publishedEdit
import ru.netologia.PostReview.Companion.author
import ru.netologia.PostReview.Companion.content
import ru.netologia.PostReview.Companion.idPost
import ru.netologia.PostReview.Companion.published
import ru.netologia.PostReview.Companion.videoUrl
import ru.netologia.adapter.IOnInteractionListener
import ru.netologia.adapter.PostsAdapter
import ru.netologia.databinding.FragmentFeedBinding
import ru.netologia.dto.Post
import ru.netologia.viewmodel.PostViewModel


class FeedFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentFeedBinding.inflate(layoutInflater)
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

            override fun onPostItemClick(post: Post) {
                viewModel.editContent(post)
                findNavController().navigate(R.id.action_feedFragment_to_postReview,
                    Bundle().apply {
                        idPost = post.id.toString()
                        author = post.author
                        published = post.published
                        content = post.content
                        videoUrl = post.videoUrl
                    })
            }

            override fun onEdit(post: Post) {
                viewModel.editContent(post)
                findNavController().navigate(R.id.action_feedFragment_to_editPost,
                    Bundle().apply {
                        authorEdit = post.author
                        publishedEdit = post.published
                        contentEdit = post.content
                    })
            }
        })

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_addNewPost)
        }

        binding.rvPostList.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) { posts ->
            adapter.submitList(posts)
        }
        return binding.root
    }

}