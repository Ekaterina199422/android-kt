package ru.netologia

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.netologia.R
import ru.netologia.databinding.ActivityEditPostBinding


class EditPost : AppCompatActivity() {
    private val intentResult = Intent()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityEditPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.editContent.requestFocus()
        intent?.let {
            binding.author.text = it.getStringExtra("author")
            binding.published.text = it.getStringExtra("published")
            binding.editContent.setText(it.getStringExtra("content"))
        }

        binding.btnSavePost.setOnClickListener {
            with(binding.editContent) {
                if (TextUtils.isEmpty(text)) {
                    Toast.makeText(
                        this@EditPost,
                        context.getString(R.string.error_empty_post),
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }
                val content = binding.editContent.text.toString()
                intentResult.putExtra(Intent.EXTRA_TEXT, content)
                setResult(Activity.RESULT_OK, intentResult)
            }
            finish()
        }

        binding.btnCancelEdit.setOnClickListener {
            setResult(Activity.RESULT_CANCELED, intentResult)
            finish()
        }
    }

}