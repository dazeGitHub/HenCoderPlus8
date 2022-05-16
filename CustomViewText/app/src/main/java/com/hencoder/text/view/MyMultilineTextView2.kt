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
 * 绘制多行文字, 并且 图文混排
 */
private val IMAGE_SIZE = 150.dp
private val IMAGE_TOP = 50.dp

class MyMultilineTextView2(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    val text =
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur tristique urna tincidunt maximus viverra. Maecenas commodo pellentesque dolor ultrices porttitor. Vestibulum in arcu rhoncus, maximus ligula vel, consequat sem. Maecenas a quam libero. Praesent hendrerit ex lacus, ac feugiat nibh interdum et. Vestibulum in gravida neque. Morbi maximus scelerisque odio, vel pellentesque purus ultrices quis. Praesent eu turpis et metus venenatis maximus blandit sed magna. Sed imperdiet est semper urna laoreet congue. Praesent mattis magna sed est accumsan posuere. Morbi lobortis fermentum fringilla. Fusce sed ex tempus, venenatis odio ac, tempor metus."

    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 16.dp
    }
    private val paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 16.dp
    }

    private var staticLayout: StaticLayout? = null
    private val bitmap = getAvatar(IMAGE_SIZE.toInt())
    private val fontMetrics = Paint.FontMetrics()

    init {
        this.post {
            //不能直接把 staticLayout 当做成员变量并初始化, 因为 width 是 0
            staticLayout = StaticLayout(
                text, textPaint, width, Layout.Alignment.ALIGN_NORMAL,
                1F, 0F, false
            )
        }
    }

    override fun onDraw(canvas: Canvas) {
        //staticLayout?.draw(canvas)
        canvas.drawBitmap(bitmap, width - IMAGE_SIZE, IMAGE_TOP, paint)
        paint.getFontMetrics(fontMetrics)
        //drawDoubleLine(canvas)
        drawMultiLine(canvas)
    }

    //不使用循环绘制两行
    fun drawDoubleLine(canvas: Canvas){
        val measuredWidth = floatArrayOf(0f)

        //绘制第一行
        //breakText(
        //  String text,
        //  boolean measureForwards,    //向前测量
        //  float maxWidth,             //能够使用的最大宽度
        //  float[] measuredWidth       //测量出的文字实际显示的宽度, 将传入的数组填满
        //)
        var count = paint.breakText(text, true, width.toFloat(), measuredWidth)
        //drawText(text, start, end, x, y, paint)
        //  start : 从哪个字符开始
        //  end   : 到哪个字符结束
        canvas.drawText(text, 0, count, 0f, -fontMetrics.top, paint)
        //绘制第二行
        //保存旧的 count 值, 防止 breakText() 将 count 更新
        val oldCount = count
        //breakText(text, start, end, measureForwards, maxWidth, measuredWidth)
        //从 count 开始测量, 然后更新 count
        count = paint.breakText(text, count, text.length, true, width.toFloat(), measuredWidth)
        //drawText(text, start, end, x, y, paint)
        //paint.fontSpacing : 行间距, 上一行的底 和 下一行的底 之间的距离
        //折行的y 值可以写为 - fontMetrics.top * 2, 更准确的写法是 - fontMetrics.top + paint.fontSpacing
        canvas.drawText(
            text,
            oldCount,
            oldCount + count,
            0f,
            -fontMetrics.top + paint.fontSpacing,
            paint
        )
    }

    //使用循环绘制多行
    fun drawMultiLine(canvas: Canvas){
        val measuredWidth = floatArrayOf(0f)
        //定义初始值
        var start = 0
        var count = 0
        var line = 1
        var verticalOffset = -fontMetrics.top
        //当循环完成时会到最后一个字符的右边, 即 start 等于整个字符串的长度时绘制完成,
        //所以循环的条件是 start < text.length
        var maxWidth : Float
        while(start < text.length){
            maxWidth = if(
                (verticalOffset + fontMetrics.bottom < IMAGE_TOP) ||
                (verticalOffset + fontMetrics.top > IMAGE_TOP + IMAGE_SIZE)
            ){
                width.toFloat()
            }else{
                width.toFloat() - IMAGE_SIZE
            }
            count = paint.breakText(text, start, text.length, true, maxWidth, measuredWidth)
            canvas.drawText(text, start, start + count, 0f, verticalOffset, paint)
            start += count
            verticalOffset += paint.fontSpacing
            line += 1
        }
    }

    fun getAvatar(width: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(resources, R.drawable.avatar_rengwuxian, options)
        options.inJustDecodeBounds = false
        options.inDensity = options.outWidth
        options.inTargetDensity = width
        return BitmapFactory.decodeResource(resources, R.drawable.avatar_rengwuxian, options)
    }
}