package ru.netologia

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
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
import ru.netologia.viewmodel.PostViewModel

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class  FeedFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ) = FragmentFeedBinding.inflate(layoutInflater).apply {
        var removeId = 0L
        var checkPost = Post()
        val adapter = PostsAdapter(object : IOnInteractionListener {


            override fun onLike(post: Post) {
                checkPost = post
                viewModel.like(post)
            }


            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(
                    intent,
                    R.string.chooser_share_post.toString()
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(shareIntent)
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

        swipeRefreshLayout.setOnRefreshListener(adapter::refresh)
        fab.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_addNewPost)
        }
        rvPostList.adapter = adapter

        lifecycleScope.launchWhenCreated {
            viewModel.posts.collectLatest(adapter::submitData)
        }
        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest { state ->
                swipeRefreshLayout.isRefreshing =
                    state.refresh is LoadState.Loading ||
                            state.prepend is LoadState.Loading ||
                            state.append is LoadState.Loading
            }
        }


        viewModel.state.observe(viewLifecycleOwner) { model ->
            groupStatus.isVisible = model.errorVisible
            tvTextStatusEmpty.isVisible = model.empty
            tvTextStatusError.text = model.error?.code
            pbProgress.isVisible = model.loading
            swipeRefreshLayout.isRefreshing = model.refreshing
            fabExtend.isVisible = model.visibleFab
        }
        errorButton.setOnClickListener {
            viewModel.loadPosts()
        }

    } .root

}