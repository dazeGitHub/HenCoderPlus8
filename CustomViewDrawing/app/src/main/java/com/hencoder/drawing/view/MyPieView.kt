package com.hencoder.drawing.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import java.lang.Math.cos
import java.lang.Math.sin

/**
 * 绘制饼状图
 */
private val RADIUS = 150f.px                                   //扇形的半径宽度
private val PIE_SWEEP_ANGLES = floatArrayOf(60f, 90f, 150f, 60f)     //定义各个扇形的角度
private val PIE_OFFSET_LENGTH = 20f.px
private val PIE_COLORS = listOf(
    Color.parseColor("#C2185B"),
    Color.parseColor("#00ACC1"),
    Color.parseColor("#558B2F"),
    Color.parseColor("#5D4037")
)

class MyPieView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        var startAngle = 0f
        for((index, sweepAngle) in PIE_SWEEP_ANGLES.withIndex()){
            mPaint.color = PIE_COLORS[index]
            if(index == 1){
                val pieOffsetAngle = Math.toRadians((startAngle + sweepAngle / 2f).toDouble())
                canvas.save()
                canvas.translate(
                    (PIE_OFFSET_LENGTH * cos(pieOffsetAngle)).toFloat(),
                    (PIE_OFFSET_LENGTH * sin(pieOffsetAngle)).toFloat()
                )
            }
            canvas.drawArc(
                width / 2f - RADIUS,
                height / 2f - RADIUS,
                width / 2f + RADIUS,
                height / 2f + 150f.px,
                startAngle,
                sweepAngle,
                true,
                mPaint
            )
            startAngle += sweepAngle
            if(index == 1){
                canvas.restore()
            }
        }
    }
}