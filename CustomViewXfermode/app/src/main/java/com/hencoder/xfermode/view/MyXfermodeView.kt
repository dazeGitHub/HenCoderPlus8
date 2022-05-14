package com.hencoder.xfermode.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.hencoder.xfermode.px

private val XFERMODE = PorterDuffXfermode(PorterDuff.Mode.DST_OVER)//PorterDuff.Mode.SRC_IN

class MyXfermodeView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bounds = RectF(150f.px, 50f.px, 300f.px, 200f.px)
    //使用 Bitmap 绘制带透明部分的 方块 和 圆形, 因为圆和方块的宽高都是 100f.px,
    //所以 bitmap 的宽高为 150f.px = 方块的宽度 100f.px + 圆形宽度的一半 50f.xp
    private val circleBitmap = Bitmap.createBitmap(150f.px.toInt(), 150f.px.toInt(), Bitmap.Config.ARGB_8888)
    private val squareBitmap = Bitmap.createBitmap(150f.px.toInt(), 150f.px.toInt(), Bitmap.Config.ARGB_8888)

    init{
        //创建一个 Canvas, 把 circleBitmap 做为背景, 可以往 circleBitmap 上画圆和方块
        val canvas = Canvas(circleBitmap)
        paint.color = Color.parseColor("#D81B60")
        canvas.drawOval(50f.px, 0f.px, 150f.px, 100f.px, paint)

        //将背景重新设置 squareBitmap
        canvas.setBitmap(squareBitmap)
        paint.color = Color.parseColor("#2196F3")
        canvas.drawRect(0f.px, 50f.px, 100f.px, 150f.px, paint)
    }

    override fun onDraw(canvas: Canvas) {
        //先绘制圆形, 再开启 xfermode 绘制方形
        val count = canvas.saveLayer(bounds, null)

        //paint.color = Color.parseColor("#D81B60")
        //先绘制 destination 底部圆 (100 x 100)
        //canvas.drawOval(200f.px, 50f.px, 300f.px, 150f.px, paint)
        canvas.drawBitmap(circleBitmap, 150f.px, 50f.px, paint)

        //rect 相对于 oval : x - 50, y + 50
        //paint.color = Color.parseColor("#2196F3")
        paint.xfermode = XFERMODE
        //canvas.drawRect(150f.px, 100f.px, 250f.px, 200f.px, paint)
        canvas.drawBitmap(squareBitmap, 150f.px, 50f.px, paint)
        paint.xfermode = null

        canvas.restoreToCount(count)
    }
}