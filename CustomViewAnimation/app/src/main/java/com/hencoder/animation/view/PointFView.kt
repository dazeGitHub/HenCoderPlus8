package com.hencoder.animation.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import com.hencoder.animation.dp

class PointFView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var point = PointF(0f, 0f)
        set(value) {
            field = value
            invalidate()
        }

    init {
        paint.strokeWidth = 20.dp
        paint.strokeCap = Paint.Cap.ROUND
    }

    override fun onDraw(canvas: Canvas) {
        //因为 drawPoint() 的圆心是在原点的, 所以圆点的左边和上边都看不见
        canvas.drawPoint(point.x, point.y, paint)
    }
}