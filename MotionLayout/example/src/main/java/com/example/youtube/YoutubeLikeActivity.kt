package com.example.youtube

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Transition
import android.view.View

class YoutubeLikeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN //隐藏状态栏
        setContentView(R.layout.activity_youtube)
    }
}
