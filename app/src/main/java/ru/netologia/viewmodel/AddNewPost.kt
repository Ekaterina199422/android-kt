package ru.netologia

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netologia.databinding.FragmentAddNewPostBinding
import ru.netologia.utils.AndroidUtils
import ru.netologia.utils.StringArg
import ru.netologia.viewmodel.PostViewModel
import ru.netologia.R

class AddNewPost : Fragment() {
    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAddNewPostBinding.inflate(layoutInflater)
        binding.newContent.requestFocus()
        binding.btnSaveNewPost.setOnClickListener {
            with(binding.newContent) {
                if (TextUtils.isEmpty(text)) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.error_empty_post),
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }
                viewModel.changeContent(binding.newContent.text.toString())
                viewModel.savePost()
                AndroidUtils.hideKeyboard(requireView())
                findNavController().navigateUp()
            }
        }
        return binding.root
    }

}