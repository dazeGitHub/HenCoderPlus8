package com.zyz.testviewdrawsource.activity

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.os.SystemClock
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.zyz.testviewdrawsource.R
import com.zyz.testviewdrawsource.ext.fbi
import kotlin.concurrent.thread

class AddViewWithoutThreadActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_view_without_main_thread)

        thread{
            Looper.prepare()
            val button = Button(this)
            button.setBackgroundColor(Color.MAGENTA)
            button.text = "I will be added on child thread."
            button.setOnClickListener{
                (it as Button).text = "${Thread.currentThread().name} ${SystemClock.uptimeMillis()}"
            }
            windowManager.addView(button, WindowManager.LayoutParams().apply{
                this.width = WindowManager.LayoutParams.WRAP_CONTENT
                this.height = WindowManager.LayoutParams.WRAP_CONTENT
            })
            Looper.loop()
        }
    }
}