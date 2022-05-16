package com.hencoder.text.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.hencoder.text.R
import com.hencoder.text.dp

private val HIGHLIGHT_COLOR = Color.parseColor("#FF4081")

/**
 * 文字 贴顶边 和 左贴边
 */
class MySportView2(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 100.dp
        typeface = ResourcesCompat.getFont(context, R.font.font)
        textAlign = Paint.Align.CENTER
    }
    private val bounds = Rect()
    private val fontMetrics = Paint.FontMetrics()

    override fun onDraw(canvas: Canvas) {
        paint.color = HIGHLIGHT_COLOR
        paint.style = Paint.Style.FILL

        //绘制文字1
        paint.textSize = 150.dp
        paint.textAlign = Paint.Align.LEFT
        paint.getFontMetrics(fontMetrics)
        paint.getTextBounds("abab", 0, "abab".length, bounds)
        canvas.drawText("abab", -bounds.left.toFloat(), - bounds.top.toFloat(), paint) //-fontMetrics.top, -fontMetrics.ascent,

        //绘制文字2
        paint.textSize = 15.dp
        paint.getTextBounds("abab", 0, "abab".length, bounds)
        canvas.drawText("abab", -bounds.left.toFloat(), - bounds.top.toFloat(), paint)
    }
}