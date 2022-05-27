package com.zyz.testviewdrawsource.activity

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.zyz.testviewdrawsource.R
import com.zyz.testviewdrawsource.ext.fbi
import kotlin.concurrent.thread

class OnChildThreadActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        val textView = fbi<TextView>(R.id.tv_content)
        thread {
            textView.text = "onCreate !"
        }
    }
}