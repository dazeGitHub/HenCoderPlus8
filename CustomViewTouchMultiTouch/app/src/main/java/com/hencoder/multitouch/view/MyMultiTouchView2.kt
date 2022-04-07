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
 * 多点触摸的第二种情况 : 配合型
 * 1. 两根手指, 一个往上一个往下, 那么图像在中间不动
 * 2. 两根手指都往上, 那么它就会往上
 * 3. 一根手指不动, 一根手指往上, 那么图像就半速往上移动
 */
class MyMultiTouchView2(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
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
        Log.e("TAG", "onDraw offsetY = $offsetY")
        canvas.drawBitmap(bitmap, offsetX, offsetY, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        var pointerCount = event.pointerCount
        Log.e("TAG", "onTouchEvent Pre pointerCount = $pointerCount")

        val focusX: Float
        val focusY: Float
        var sumX = 0f
        var sumY = 0f

        //如果是手指抬起那么就需要改变焦点, 就不能再是两个手指的中心点了, 应当变为手指1的坐标(如果超过 2 指就应当是排除当前抬起的手指的剩余手指所计算出的焦点),
        //focusY 变为手指1 的 event.getY(index), 那么 downY = focusY 也会改变,
        //那么当下次 ACTION_MOVE 的时候 offsetY = focusY - downY + originalOffsetY 得到的 offsetY 的值就是正常的,
        //canvas.drawBitmap(bitmap, offsetX, offsetY, paint) 使用 offsetY 显示图片也是正常的
        val isPointerUp = event.actionMasked == MotionEvent.ACTION_POINTER_UP

        for(index in 0 until pointerCount){
            if(!(isPointerUp && index == event.actionIndex)){ //判断这个 index 就是抬起的手指, 即 index == event.actionIndex
                sumX += event.getX(index)
                sumY += event.getY(index)
            }

//            sumX += event.getX(index)
//            sumY += event.getY(index)

            Log.e("TAG", "onTouchEvent for  index = $index  event.getY(index) = ${event.getY(index)}")
        }
        if(isPointerUp){
            pointerCount--
        }

        //焦点坐标 = n 个点的坐标相加的和 / n = sumX / count
        focusX = sumX / pointerCount
        focusY = sumY / pointerCount
        Log.e("TAG", "onTouchEvent focusY = $focusY sumY = $sumY  pointerCount = $pointerCount")

        //得到焦点坐标, 那么就可以认为焦点就是单指操作的触点, 只需要将后边的 event.getX() 和 event.getY() 替换为 focusX 和 focusY 即可
        when (event.actionMasked) { //尤其是多点触控的时候, 使用 event.actionMasked 更加精准
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN, MotionEvent.ACTION_POINTER_UP -> {
                //设置焦点
                downX = focusX
                downY = focusY
                //可以在 MotionEvent.ACTION_DOWN 得到上一次的初始偏移 offsetX 和 offsetY
                originalOffsetX = offsetX
                originalOffsetY = offsetY

                if(event.actionMasked == MotionEvent.ACTION_POINTER_UP){
                    Log.e("TAG", "onTouchEvent ACTION_POINTER_UP pointerCount = $pointerCount downY = $downY  offsetY = $offsetY originalOffsetY = $originalOffsetY")
                }else{
                    Log.e("TAG", "onTouchEvent ACTION_DOWN,ACTION_POINTER_DOWN pointerCount = $pointerCount  downY = $downY  offsetY = $offsetY originalOffsetY = $originalOffsetY")
                }
            }
            MotionEvent.ACTION_MOVE -> {
                offsetX = focusX - downX + originalOffsetX
                offsetY = focusY - downY + originalOffsetY
                Log.e("TAG", "onTouchEvent ACTION_MOVE  offsetY = $offsetY  focusY = $focusY  downY = $downY  originalOffsetY = $originalOffsetY")
                invalidate()
            }
        }
        return true
    }
}