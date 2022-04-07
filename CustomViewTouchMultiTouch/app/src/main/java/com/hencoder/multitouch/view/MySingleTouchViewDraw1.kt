package com.hencoder.multitouch.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.hencoder.multitouch.dp

/**
 * 单指画图, 对应于 MyMultiTouchView3
 */
class MySingleTouchViewDraw1(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var path = Path()

    init{
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 4.dp
        paint.strokeCap = Paint.Cap.ROUND   //线帽，即画的线条两端是否带有圆角
        paint.strokeJoin = Paint.Join.ROUND //path 的拐角处为圆角
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.actionMasked){
            MotionEvent.ACTION_DOWN -> {
                //记录绘制起点
                path.moveTo(event.x, event.y)
                //ACTION_DOWN 的时候刷新不刷新都可以, 但是好的习惯是改变了 path 就刷新界面
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                path.lineTo(event.x, event.y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                //抬起时将画面清空
                path.reset()
                invalidate()
            }
        }
        return true
    }
}