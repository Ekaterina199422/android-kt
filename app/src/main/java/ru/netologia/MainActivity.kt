package ru.netologia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        println(resources.displayMetrics.heightPixels) // 1794
        println(resources.displayMetrics.widthPixels) // 1080
        println(resources.displayMetrics.densityDpi) // 420
        println(resources.displayMetrics.density) // 2.625
    }
}