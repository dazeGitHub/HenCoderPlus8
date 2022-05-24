package com.hencoder.layoutsize.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.hencoder.layoutsize.dp

private val RADIUS = 100.dp
private val PADDING = 100.dp

/**
 * CircleView 的尺寸和内部的圆形相关, 而不关心 父View 的测量结果
 */
class MyCircleView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //自己测量出的宽度
        val size = ((PADDING + RADIUS) * 2).toInt()
        //val width = generateWidth(size, widthMeasureSpec)
        val width = resolveSize(size, widthMeasureSpec)
        val height = resolveSize(size, heightMeasureSpec)
        //resolveSizeAndState()
        setMeasuredDimension(width, height)
    }

    private fun generateWidth(size: Int, widthMeasureSpec: Int): Int{
        val specWidthMode = MeasureSpec.getMode(widthMeasureSpec)
        val specWidthSize = MeasureSpec.getSize(widthMeasureSpec)
        val width = when(specWidthMode){
            MeasureSpec.EXACTLY -> {
                specWidthSize
            }
            MeasureSpec.AT_MOST -> {
                //如果自己的测量值大 (超过了父View 给的 specWidthSize), 那么就用父View 给的 specWidthSize
                //否则使用自己的测量值
                if(size > specWidthSize){
                    specWidthSize
                }else{
                    size
                }
            }
            else -> { //MeasureSpec.UNSPECIFIED
                //不限制那么就用自己的 size
                size
            }
        }
        return width
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //以前的写法
        // canvas.drawCircle(width / 2f, height / 2f, RADIUS, paint)

        //现在因为尺寸只和内部的圆形相关, 所以这样写
        canvas.drawCircle(PADDING + RADIUS, PADDING + RADIUS, RADIUS, paint)
    }
}