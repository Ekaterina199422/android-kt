package ru.netologia

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.netologia.databinding.ActivityNewPostBinding

class NewPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnSaveNewPost.setOnClickListener{
            val result = Intent().putExtra(Intent.EXTRA_TEXT, binding.textNewPost.text.toString())
            setResult(RESULT_OK, result)
            finish()
        }

    }
}