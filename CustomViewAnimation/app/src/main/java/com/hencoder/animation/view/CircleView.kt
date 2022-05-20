package com.hencoder.animation.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.hencoder.animation.dp

class CircleView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    //每次更新 radius 都需要调用一次 invalidate() 刷新界面
    var radius = 50.dp
        set(value) {
            field = value
            invalidate()
        }

    init {
        paint.color = Color.parseColor("#00796B")
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(width / 2f, height / 2f, radius, paint)
    }
}