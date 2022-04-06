package com.hencoder.multitouch.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.hencoder.multitouch.dp
import com.hencoder.multitouch.getAvatar

/**
 * 多点触摸的第一种情况 : 接力型
 */
class MySingleTouchView1(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bitmap = getAvatar(resources, 200.dp.toInt())
    private var originalOffsetX = 0f
    private var originalOffsetY = 0f
    private var offsetX = 0f
    private var offsetY = 0f
    private var downX = 0f
    private var downY = 0f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        Log.e("TAG", "onDraw offsetX = $offsetX offsetY = $offsetY")
        canvas.drawBitmap(bitmap, offsetX, offsetY, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) { //尤其是多点触控的时候, 使用 event.actionMasked 更加精准
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
                //可以在 MotionEvent.ACTION_DOWN 得到上一次的初始偏移 offsetX 和 offsetY
                originalOffsetX = offsetX
                originalOffsetY = offsetY
                Log.e("TAG", "onTouchEvent MotionEvent.ACTION_DOWN  downX = $downX downY = $downY")
            }
            MotionEvent.ACTION_MOVE -> {
//                for(index in 0 until event.pointerCount){
//                    event.getX(index)
//                    event.getY(index)
//                }
                offsetX = event.x - downX + originalOffsetX
                offsetY = event.y - downY + originalOffsetY
                Log.e("TAG", "onTouchEvent MotionEvent.ACTION_MOVE  event.x = ${event.x} event.y = ${event.y}  ")
                invalidate()
            }
//            MotionEvent.ACTION_UP -> {
//                originalOffsetX = offsetX
//                originalOffsetY = offsetY
//            }
        }
        return true
    }
}