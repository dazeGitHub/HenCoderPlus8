package com.hencoder.bitmapanddrawable

import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable

class MyMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val bitmap = Bitmap.createBitmap(20, 20, Bitmap.Config.ARGB_8888)
//        bitmap.toDrawable(resources) //toDrawable() 是 ktx 扩展函数
//        val drawable = ColorDrawable()
//        drawable.toBitmap()
    }
}