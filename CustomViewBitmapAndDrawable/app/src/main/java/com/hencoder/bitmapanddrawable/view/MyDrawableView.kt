package com.hencoder.bitmapanddrawable.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.View
import com.hencoder.bitmapanddrawable.dp
import com.hencoder.bitmapanddrawable.drawable.MeshDrawable
import com.hencoder.bitmapanddrawable.drawable.MyMeshDrawable

/**
 * 在 MyDrawableView 中显示 ColorDrawable 和 MyMeshDrawable
 */
class MyDrawableView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val drawable = MyMeshDrawable() //ColorDrawable(Color.RED)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //drawable 的默认范围是 (0,0), 所以为了显示需要调用 setBounds() 设置范围
        drawable.setBounds(50.dp.toInt(), 80.dp.toInt(), width, height) //50.dp.toInt(), 80.dp.toInt()
        drawable.draw(canvas)
    }
}