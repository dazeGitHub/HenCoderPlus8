package com.hencoder.text.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import com.hencoder.text.R
import com.hencoder.text.dp

/**
 * 绘制多行文字, 使用 StaticLayout 类
 */
class MyMultilineTextView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    val text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur tristique urna tincidunt maximus viverra. Maecenas commodo pellentesque dolor ultrices porttitor. Vestibulum in arcu rhoncus, maximus ligula vel, consequat sem. Maecenas a quam libero. Praesent hendrerit ex lacus, ac feugiat nibh interdum et. Vestibulum in gravida neque. Morbi maximus scelerisque odio, vel pellentesque purus ultrices quis. Praesent eu turpis et metus venenatis maximus blandit sed magna. Sed imperdiet est semper urna laoreet congue. Praesent mattis magna sed est accumsan posuere. Morbi lobortis fermentum fringilla. Fusce sed ex tempus, venenatis odio ac, tempor metus."

    private val paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 16.dp
    }

    private var staticLayout : StaticLayout? = null

    init {
        this.post{
            //StaticLayout(
            //      CharSequence source,
            //      TextPaint paint,
            //      int width,
            //      Alignment align,
            //      float spacingmult,
            //      float spacingadd,
            //      boolean includepad
            //)
            //
            //source      : 文本
            //paint       : 画笔
            //width       : 给文字多少宽度, 从而折行, 这里直接传 view 的 宽度
            //align       : 文字如何对齐
            //spacingmult : 文字之间的间距 额外乘的值, 如果要保持原样那么直接传 1 即可
            //spacingadd  : 文字之间的间距 额外加的值, 如果要保持原样那么直接传 0 即可
            //includepad  : 行与行之间是否有额外间距, 如果没有那么传 false 即可
            //
            //不能直接把 staticLayout 当做成员变量并初始化, 因为 width 是 0
            staticLayout = StaticLayout(text, paint, width, Layout.Alignment.ALIGN_NORMAL,
                1F, 0F, false)
        }
    }

    override fun onDraw(canvas: Canvas) {
        staticLayout?.draw(canvas)
    }
}