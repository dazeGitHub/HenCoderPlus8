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
class MyMultiTouchView1(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bitmap = getAvatar(resources, 200.dp.toInt())
    private var originalOffsetX = 0f
    private var originalOffsetY = 0f
    private var offsetX = 0f
    private var offsetY = 0f
    private var downX = 0f
    private var downY = 0f
    private var trackingPointerId = 0

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        Log.e("TAG", "onDraw offsetX = $offsetX offsetY = $offsetY")
        canvas.drawBitmap(bitmap, offsetX, offsetY, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) { //尤其是多点触控的时候, 使用 event.actionMasked 更加精准
            MotionEvent.ACTION_DOWN -> {
                //拿到第一根手指按下的 pointerId
                trackingPointerId = event.getPointerId(0)
                downX = event.x
                downY = event.y
                //可以在 MotionEvent.ACTION_DOWN 得到上一次的初始偏移 offsetX 和 offsetY
                originalOffsetX = offsetX
                originalOffsetY = offsetY
                Log.e("TAG", "onTouchEvent MotionEvent.ACTION_DOWN  downX = $downX downY = $downY")
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                val actionIndex = event.actionIndex
                trackingPointerId = event.getPointerId(actionIndex)
                //新按下手指也需要重新记录以下坐标值, 因为不是 pointerIndex 是 0 的手指坐标了, 所以不能直接用 event.getX(), 而应该使用 event.getX(pointerIndex)
                downX = event.getX(actionIndex)
                downY = event.getY(actionIndex)
                originalOffsetX = offsetX
                originalOffsetY = offsetY
                Log.e("TAG", "onTouchEvent MotionEvent.ACTION_POINTER_DOWN  downX = $downX downY = $downY")
            }
            MotionEvent.ACTION_MOVE -> {
//                for(index in 0 until event.pointerCount){
//                    event.getX(index)
//                    event.getY(index)
//                }
                val trackingPointerIndex = event.findPointerIndex(trackingPointerId)
                offsetX = event.getX(trackingPointerIndex) - downX + originalOffsetX
                offsetY = event.getY(trackingPointerIndex) - downY + originalOffsetY
                Log.e("TAG", "onTouchEvent MotionEvent.ACTION_MOVE " +
                        "trackingPointerId = $trackingPointerId " +
                        "trackingPointerIndex = $trackingPointerIndex " +
                        "event.getX(trackingPointerIndex) = ${event.getX(trackingPointerIndex)} offsetX = $offsetX" +
                        "event.getY(trackingPointerIndex) = ${event.getY(trackingPointerIndex)} offsetY = $offsetY"
                )
                invalidate()
            }
            //不需要考虑 MotionEvent.ACTION_UP, 因为当执行 MotionEvent.ACTION_POINTER_DOWN 时当前手指已经交棒给非第一根手指,
            //即如果是 MotionEvent.ACTION_UP 那么肯定不是刚才 Move 的手指, 所以不用管 MotionEvent.ACTION_UP
            MotionEvent.ACTION_UP -> {
                Log.e("TAG", "onTouchEvent ACTION_UP")
            }
            //通过 id 判断当前抬起的手指是否是正在跟踪的手指, 如果不是那么不用管, 如果是那么寻找一个新的手指来接棒
            MotionEvent.ACTION_POINTER_UP -> {
                Log.e("TAG", "onTouchEvent ACTION_POINTER_UP")
                val actionIndex = event.actionIndex
                val pointerId = event.getPointerId(actionIndex)
                //如果当前抬起的手指是否是正在跟踪的手指
                if(pointerId == trackingPointerId){
                    //找一个不是当前抬起手指的 index 做为的接力手指的 index
                    //注意: 此时的 event.pointerCount 是包含正在抬起的手指的, 如果正好是最后一根手指抬起, 那么 event.pointerCount - 1 得到的 newIndex 就是正在抬起的手指的 index (即接棒的是自己)
                    //所以这里不能直接使用 event.pointerCount - 1 做为新的接力的手指的 index, 要判断下当前手指是不是最后一个
                    val newIndex = if(actionIndex == event.pointerCount - 1){
                        event.pointerCount - 2
                    }else{
                        event.pointerCount - 1
                    }
                    //因为 ACTION_MOVE 是根据 trackingPointerId 来计算 trackingPointerIndex, 再计算偏移的, 所以交接后即使动的不是交接的那根手指, 图像也不会发生移动
                    trackingPointerId = event.getPointerId(newIndex)
                    //别忘了接力完了把 down 时的坐标也初始化一下
                    downX = event.getX(newIndex)
                    downY = event.getY(newIndex)
                    originalOffsetX = offsetX
                    originalOffsetY = offsetY
                }
            }
        }
        return true
    }
}