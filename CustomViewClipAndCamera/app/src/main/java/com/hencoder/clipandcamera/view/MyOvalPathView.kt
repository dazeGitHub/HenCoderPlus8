package com.hencoder.clipandcamera.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.hencoder.clipandcamera.R
import com.hencoder.clipandcamera.dp

private val BITMAP_SIZE = 200.dp
private val BITMAP_PADDING = 100.dp

/**
 * 使用 canvas.clipPath(clippedPath) 裁剪出圆形图像, 边缘有锯齿
 */
class MyOvalPathView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bitmap = getAvatar(BITMAP_SIZE.toInt())
    private val clippedPath = Path().apply{
        //为 Path 添加一个圆形(整个图形的范围), 但是裁剪出的圆是带有锯齿的, 而之前的 xfermode 裁剪出的圆是没有锯齿的
        addOval(
            BITMAP_PADDING,
            BITMAP_PADDING,
            BITMAP_PADDING + BITMAP_SIZE,
            BITMAP_PADDING + BITMAP_SIZE,
            Path.Direction.CCW
        )
    }

    override fun onDraw(canvas: Canvas) {
        canvas.clipPath(clippedPath)
        //切取从 图片的左上角 到 图片中心 的 Rect
//        canvas.clipRect(
//            BITMAP_PADDING,
//            BITMAP_PADDING,
//            BITMAP_PADDING + BITMAP_SIZE / 2,
//            BITMAP_PADDING + BITMAP_SIZE / 2
//        )
        canvas.drawBitmap(bitmap, BITMAP_PADDING, BITMAP_PADDING, paint)
    }

    private fun getAvatar(width: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(resources, R.drawable.avatar_rengwuxian, options)
        options.inJustDecodeBounds = false
        options.inDensity = options.outWidth
        options.inTargetDensity = width
        return BitmapFactory.decodeResource(resources, R.drawable.avatar_rengwuxian, options)
    }
}