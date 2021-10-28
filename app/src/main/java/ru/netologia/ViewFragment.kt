package ru.netologia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import ru.netologia.databinding.FragmentViewBinding
import ru.netologia.utils.StringArg


@AndroidEntryPoint
class ViewFragment : Fragment() {
    companion object {
        var Bundle.urlImage: String? by StringArg
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentViewBinding.inflate(layoutInflater)
        binding.apply {
            Glide.with(ivImagePick)
                .load("http://10.0.2.2:9999/media/${arguments?.urlImage}")
                .placeholder(R.drawable.ic_attach_error_48)
                .timeout(10_000)
                .into(ivImagePick)
        }
        return binding.root
    }
}
