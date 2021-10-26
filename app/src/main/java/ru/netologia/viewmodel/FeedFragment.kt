package ru.netologia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import ru.netologia.EditPost.Companion.authorEdit
import ru.netologia.EditPost.Companion.contentEdit
import ru.netologia.EditPost.Companion.publishedEdit
import ru.netologia.PostReview.Companion.author
import ru.netologia.PostReview.Companion.content
import ru.netologia.PostReview.Companion.idPost
import ru.netologia.PostReview.Companion.published
import ru.netologia.ViewFragment.Companion.urlImage
import ru.netologia.adapter.IOnInteractionListener
import ru.netologia.adapter.PostsAdapter
import ru.netologia.databinding.FragmentFeedBinding
import ru.netologia.dto.Post
import ru.netologia.model.getCreateReadableMessageError
import ru.netologia.viewmodel.PostViewModel


class  FeedFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        var removeId = 0L
        var checkPost = Post()
        val binding = FragmentFeedBinding.inflate(layoutInflater)
        val adapter = PostsAdapter(object : IOnInteractionListener {


            override fun onLike(post: Post) {
                checkPost = post
                viewModel.like(post)
            }


            override fun onShare(post: Post) {
                viewModel.sharePost(post)
                checkPost = post

            }

            override fun onRemove(post: Post) {
                removeId = post.id
                viewModel.removePost(post.id)
            }


            override fun onPostItemClick(post: Post) {
                viewModel.editContent(post)
                findNavController().navigate(R.id.action_feedFragment_to_postReview,
                        Bundle().apply {
                            idPost = post.id.toString()
                            author = post.author
                            published = post.published
                            content = post.content
                            //videoUrl = post.videoUrl
                        })
            }

            override fun onRetrySendPost(post: Post) {
                viewModel.retrySendPost(post)
            }

            override fun onClickImage(post: Post) {
                findNavController().navigate(
                        R.id.action_feedFragment_to_viewFragment,
                                Bundle().apply {
                            urlImage = post.attachment?.url
                        }
                )
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

        binding.swipeRefreshLayout.setOnRefreshListener(viewModel::refreshingPosts)
        viewModel.postsRefreshError.observe(viewLifecycleOwner) {
            Snackbar.make(binding.root, R.string.message_status_error, Snackbar.LENGTH_SHORT)
                    .setAction("Retry") { viewModel.refreshingPosts() }
                    .show()
        }
        viewModel.postRemoveError.observe(viewLifecycleOwner) {
            Snackbar.make(
                    binding.root,
                    R.string.message_status_error,
                    Snackbar.LENGTH_LONG
            )
                    .setAction("Retry") { viewModel.removePost(removeId) }
                    .show()
        }

        viewModel.postLikeError.observe(viewLifecycleOwner) {
            Snackbar.make(
                    binding.root,
                    R.string.message_status_error,
                    Snackbar.LENGTH_SHORT
            )
                    .setAction("Retry") { viewModel.like(checkPost) }
                    .show()
        }
         binding.fabExtend.setOnClickListener {
             viewModel.getNewPosts()
        adapter.notifyDataSetChanged()
        binding.rvPostList.layoutManager?.smoothScrollToPosition(
                binding.rvPostList,
                RecyclerView.State(),
                0
        )
    }
    viewModel.newPosts.observe(viewLifecycleOwner) {
    viewModel.checkNewPosts(it)
}

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_addNewPost)
        }
        binding.rvPostList.adapter = adapter
        viewModel.state.observe(viewLifecycleOwner) { model ->
            binding.groupStatus.isVisible = model.errorVisible
            binding.tvTextStatusEmpty.isVisible = model.empty
            binding.tvTextStatusError.text = model.error.getCreateReadableMessageError(resources)
            binding.swipeRefreshLayout.isRefreshing = model.refreshing
            binding.pbProgress.isVisible = model.loading
            binding.fabExtend.isVisible = model.visibleFab
        }
        viewModel.posts.observe(viewLifecycleOwner, adapter::submitList)
        binding.errorButton.setOnClickListener {
            viewModel.loadPosts()
        }
        return binding.root
    }

}