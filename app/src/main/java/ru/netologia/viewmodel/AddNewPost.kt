package ru.netologia

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.netologia.R
import ru.netologia.databinding.ActivityAddNewPostBinding

class AddNewPost : AppCompatActivity() {
    private val intentResult = Intent()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAddNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.newContent.requestFocus()
        binding.btnSaveNewPost.setOnClickListener {
            with(binding.newContent) {
                if (TextUtils.isEmpty(text)) {
                    Toast.makeText(
                        this@AddNewPost,
                        context.getString(R.string.error_empty_post),
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }
                val content = binding.newContent.text.toString()
                intentResult.putExtra(Intent.EXTRA_TEXT, content)
                setResult(Activity.RESULT_OK, intentResult)
            }
            finish()
        }

        binding.btnCancelAddPost.setOnClickListener {
            setResult(Activity.RESULT_CANCELED, intentResult)
            finish()
        }
    }

}