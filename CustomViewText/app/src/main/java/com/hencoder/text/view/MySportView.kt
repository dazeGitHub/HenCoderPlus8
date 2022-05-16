package com.hencoder.text.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.hencoder.text.R
import com.hencoder.text.dp

private val CIRCLE_COLOR = Color.parseColor("#90A4AE")
private val HIGHLIGHT_COLOR = Color.parseColor("#FF4081")
private val BOUNDS_COLOR = Color.parseColor("#00FF00")
private val RING_WIDTH = 20.dp //圆环宽度
private val BOUND_WIDTH = 2.dp //边界线宽度
private val RADIUS = 150.dp    //圆环半径

/**
 * 绘制圆环 和 圆环中间的文字
 */
class MySportView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        //安卓手机有额外功能, 将文字统一放大或缩小, 这个功能控制的就是 sp, sp 是为了方便阅读
        //dp 只受 像素密度 影响, 而 sp 受两个因素影响 : 像素密度 和 用户对系统设置文字额外的放大或缩小
        //而 MySportView 里边是个运动记录界面, 文字就 1 行, 不需要将文字缩小, 也不需要将文字放大来看清
        //并且不同用户手机该文字显示的也不一样, 可能文字会超出环
        textSize = 100.dp
        //设置字体, R.font 的 font 表示在 res/font 目录下, R.font.font 最后一个 font 是 font.ttf
        //每个 typeface 有不同的字重
        typeface = ResourcesCompat.getFont(context, R.font.font)
        //是否将文字加粗, Fake 是假的意思, 是将细体用算法描粗而不是粗体
        //isFakeBoldText = true
        //设置字体左右居中
        textAlign = Paint.Align.CENTER
    }
    private val bounds = Rect()
    private val fontMetrics = Paint.FontMetrics()

    override fun onDraw(canvas: Canvas) {
        // 绘制环
        paint.style = Paint.Style.STROKE
        paint.color = CIRCLE_COLOR
        paint.strokeWidth = RING_WIDTH
        canvas.drawCircle(width / 2f, height / 2f, RADIUS, paint)

        // 绘制进度条
        paint.color = HIGHLIGHT_COLOR
        paint.strokeCap = Paint.Cap.ROUND   //cap : 帽子, 线条的帽子就是线条的两端
        canvas.drawArc(
            width / 2f - RADIUS,
            height / 2f - RADIUS,
            width / 2f + RADIUS,
            height / 2f + RADIUS,
            -90f,
            225f,
            false,
            paint
        )

        paint.style = Paint.Style.FILL

        //绘制文字
        paint.getTextBounds("ababp", 0, "ababp".length, bounds)
        Log.e("TAG", "bounds.width = ${bounds.width()} bounds.height = ${bounds.height()} " +
                "bounds.top = ${bounds.top} bounds.bottom = ${bounds.bottom} " +
                "bounds.left = ${bounds.left} bounds.right = ${bounds.right} "
        )

        //使用该方法获取 ascent 和 descent
        paint.getFontMetrics(fontMetrics)

        //显示静态文字用这种方式
        //canvas.drawText("ababp", width / 2f, height / 2f - (bounds.top + bounds.bottom) / 2f, paint)

        //显示动态文字用这种方式
        canvas.drawText("aaaa", width / 2f, height / 2f - (fontMetrics.ascent + fontMetrics.descent) / 2f, paint)

        //绘制文字2
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("abab", 0f, 0f, paint)

        //绘制底部 Bound
        paint.color = BOUNDS_COLOR

        val leftTopX = width / 2f - (bounds.left + bounds.right) / 2f
        val leftTopY = height / 2f - (bounds.top + bounds.bottom) / 2f + bounds.top
        val leftBottomX = leftTopX
        val leftBottomY = height / 2f - (bounds.top + bounds.bottom) / 2f + bounds.bottom

        val rightTopX = width / 2f + (bounds.left + bounds.right) / 2f
        val rightTopY = leftTopY
        val rightBottomX = rightTopX
        val rightBottomY = leftBottomY

        paint.strokeWidth = BOUND_WIDTH

        //画左边 Bound
        canvas.drawLine(leftTopX, leftTopY, leftBottomX, leftBottomY, paint)

        //画上边 Bound
        canvas.drawLine(leftTopX, leftTopY, rightTopX, rightTopY, paint)
//
        //画右边 Bound
        canvas.drawLine(rightTopX, rightTopY, rightBottomX, rightBottomY, paint)
//
        //画底部 Bound
        canvas.drawLine(leftBottomX, leftBottomY, rightBottomX, leftBottomY, paint)
    }
}