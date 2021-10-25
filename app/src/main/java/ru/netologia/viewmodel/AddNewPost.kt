package ru.netologia

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import ru.netologia.R.*
import ru.netologia.databinding.FragmentAddNewPostBinding
import ru.netologia.utils.AndroidUtils
import ru.netologia.utils.StringArg
import ru.netologia.viewmodel.PostViewModel

class AddNewPost : Fragment() {
    private val photoRequestCode = 1
    private val cameraRequestCode = 2
    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)
    private var fragmentBinding: FragmentAddNewPostBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_new_post, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save -> {
                fragmentBinding?.let {
                    viewModel.changeContent(it.newContent.text.toString())
                    viewModel.savePost()
                    viewModel.isHandledBackPressed = ""
                    AndroidUtils.hideKeyboard(requireView())
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAddNewPostBinding.inflate(layoutInflater)
        fragmentBinding = binding
        requireActivity().onBackPressedDispatcher.addCallback(
                viewLifecycleOwner,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        viewModel.isHandledBackPressed = binding.newContent.text.toString()
                        findNavController().popBackStack(R.id.feedFragment, false)
                    }
                })
        binding.newContent.requestFocus()
        binding.newContent.setText(viewModel.isHandledBackPressed)
        binding.pickPhoto.setOnClickListener {
            ImagePicker.with(this)
                    .crop()
                    .compress(2048)
                    .galleryOnly()
                    .galleryMimeTypes(
                            arrayOf(
                                    "image/png",
                                    "image/jpeg"
                            )
                    )
                    .start(photoRequestCode)
        }

        binding.takePhoto.setOnClickListener {
            ImagePicker.with(this)
                    .crop()
                    .compress(2048)
                    .cameraOnly()
                    .start(cameraRequestCode)
        }
        viewModel.postCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
        binding.removePhoto.setOnClickListener {
            viewModel.changePhoto(null)
        }
        viewModel.photo.observe(viewLifecycleOwner) {
            if (it.uri == null) {
                binding.photoContainer.isVisible = false
                return@observe
            }
            binding.photoContainer.isVisible = true
            binding.photo.setImageURI(it.uri)

        }

        return binding.root
    }
        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (resultCode == ImagePicker.RESULT_ERROR) {
                fragmentBinding?.let {
                    Snackbar.make(it.root, ImagePicker.getError(data), Snackbar.LENGTH_SHORT).show()
                }
                return
            }
            if (resultCode == Activity.RESULT_OK &&
                    requestCode == photoRequestCode ||
                    requestCode == cameraRequestCode
            ) {
                val url = data?.data
                val file = ImagePicker.getFile(data)
                viewModel.changePhoto(url)
            }

        }


    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }

    }



