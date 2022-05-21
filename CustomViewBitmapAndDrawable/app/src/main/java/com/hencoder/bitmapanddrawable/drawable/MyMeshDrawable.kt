package com.hencoder.bitmapanddrawable.drawable

import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.core.graphics.toColorInt
import com.hencoder.bitmapanddrawable.dp

/**
 * 网眼状 Drawable
 */
private val INTERVAL = 50.dp //网线之间的间隔

class MyMeshDrawable : Drawable() {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply{
        color = "#F9A825".toColorInt()  //Color.parseColor("#F9A825")
        strokeWidth = 5.dp
    }

    override fun draw(canvas: Canvas) {
        //使用两次循环, 先绘制竖线, 再绘制横线
        //最左边不是 0, 而是通过 bounds.left 来获取左边界
        var x = bounds.left.toFloat()
        while(x <= bounds.right){
            canvas.drawLine(x, bounds.top.toFloat(), x, bounds.bottom.toFloat(), paint)
            x += INTERVAL
        }
        var y = bounds.top.toFloat()
        while(y <= bounds.bottom){
            canvas.drawLine(bounds.left.toFloat(), y, bounds.right.toFloat(), y, paint)
            y += INTERVAL
        }
    }

    /**
     * 该方法设置 Drawable 整体的透明度, 将参数 透明度 alpha 直接传递给 paint 即可
     */
    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    /**
     * 取透明度也是直接从 paint 中获取
     */
    override fun getAlpha() : Int{
        return paint.alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    override fun getColorFilter(): ColorFilter? {
        return paint.colorFilter
    }

    /**
     * Opacity : 不透明度, 该方法用来判断是否和别的图像融合计算并绘制
     * 如果上边的图像是不透明的, 那么最终图像只受上边的图像影响;
     * 如果上边的图像是半透明的, 那么最终图像受 下边的图像 和 上边的图像 影响
     */
    override fun getOpacity(): Int {
        return when(paint.alpha){
            0 -> PixelFormat.TRANSPARENT
            0xff -> PixelFormat.OPAQUE      //不透明
            else -> PixelFormat.TRANSLUCENT //半透明
        }
    }
}