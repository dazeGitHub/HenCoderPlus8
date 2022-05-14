package com.hencoder.xfermode.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.hencoder.xfermode.R
import com.hencoder.xfermode.px

private val IMAGE_WIDTH = 200f.px
private val IMAGE_PADDING = 20f.px
private val XFER_MODE = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

class MyAvatarView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bounds = RectF(
        IMAGE_PADDING,
        IMAGE_PADDING,
        IMAGE_PADDING + IMAGE_WIDTH,
        IMAGE_PADDING + IMAGE_WIDTH
    )

    override fun onDraw(canvas: Canvas) {
        //bounds : 挖出多大的缓冲区, 这里挖出正方形那么大的区域即可, 因为离屏缓冲很耗资源
        val count = canvas.saveLayer(bounds, null)
        //下边不只是圆, 还有整个 View, 所以直接这样写是没效果的
        canvas.drawOval(bounds, paint)
        paint.xfermode = XFER_MODE
        canvas.drawBitmap(getAvatar(IMAGE_WIDTH.toInt()), IMAGE_PADDING, IMAGE_PADDING, paint)
        paint.xfermode = null   //每次用完 xfermode 都恢复一下
        //canvas 恢复到原先第几个状态, 直接使用 canvas.saveLayer() 的返回值即可
        canvas.restoreToCount(count)
    }

    //可能图片有 1万像素宽, 但是只需要固定像素宽度的大小的图片, 读取的图片越大, Bitmap 就越大, 所以可以根据需求加载图片
    fun getAvatar(width: Int): Bitmap {
        val options = BitmapFactory.Options()
        //设置 options.inJustDecodeBounds = true, 这时候指挥解码边界
        //即 decode 的 bitmap 为 null, 只是读取图片的宽高到 options 里
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(resources, R.drawable.avatar_rengwuxian, options)
        options.inJustDecodeBounds = false
        options.inDensity = options.outWidth //options.inDensity : 图片原来有多大
        options.inTargetDensity = width      //options.inTargetDensity : 希望图片有多大
        return BitmapFactory.decodeResource(resources, R.drawable.avatar_rengwuxian, options)
    }
}