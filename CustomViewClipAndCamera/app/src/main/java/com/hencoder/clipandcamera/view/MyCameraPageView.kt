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
 * 使用 Camera 实现翻页效果
 */
class MyCameraPageView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bitmap = getAvatar(BITMAP_SIZE.toInt())
    private val camera = Camera()

    init {
        //沿着 x 轴顺时针旋转 30 度
        camera.rotateX(30f)

        //使用 camera.rotate(x,y,z) 也无法指定轴心, 所以只能移动图像
        //camera.rotate(x, y , z)

        //camera.setLocation(x, y ,z)
        //让 camera 沿着 x 轴 和 y 轴移动没有什么应用场景, 所以直接填 0 即可
        //z 的默认值是 -8 英寸 (单位不是像素), 一英寸大致 72 像素,
        //但是写死 z 值是不行的, 因为手机像素密度是不一样的, 同样的 z 值像素密度越大的 camera 离屏幕越近, 糊脸效果就越严重
        //所以 z 值还需要乘像素密度, 具体的 z 值要自己试
        camera.setLocation(0f, 0f, - 6 * resources.displayMetrics.density)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        //裁剪上半部分
        canvas.translate(BITMAP_PADDING + BITMAP_SIZE / 2, BITMAP_PADDING + BITMAP_SIZE / 2)
        //在移动前先转回来
        canvas.rotate(-30f)
        canvas.clipRect(- BITMAP_SIZE, - BITMAP_SIZE, BITMAP_SIZE, 0f)
        canvas.rotate(30f)
        //将 canvas 坐标系挪动到原点, 二维平移
        canvas.translate(-(BITMAP_PADDING + BITMAP_SIZE / 2), -(BITMAP_PADDING + BITMAP_SIZE / 2))
        canvas.drawBitmap(bitmap, BITMAP_PADDING, BITMAP_PADDING, paint)
        canvas.restore()

        //裁剪下半部分
        // clipRect(left, top, right, bottom), 裁切图片的下一半 (只有下一半才显示)
        //不能再这裁切, 否则会导致多裁切了图片一部分的问题
//        canvas.clipRect(
//            BITMAP_PADDING, BITMAP_PADDING + BITMAP_SIZE / 2,
//            BITMAP_PADDING + BITMAP_SIZE,BITMAP_PADDING + BITMAP_SIZE
//        )
        canvas.save()
        //将 canvas 坐标系再挪动回来, 二维平移
        canvas.translate(BITMAP_PADDING + BITMAP_SIZE / 2, BITMAP_PADDING + BITMAP_SIZE / 2)
        //在移动前先转回来
        canvas.rotate(-30f)
        //三维旋转
        camera.applyToCanvas(canvas)    //等价于 camera 的复杂操作被 canvas 做了
        //裁切
        canvas.clipRect(- BITMAP_SIZE, 0f, BITMAP_SIZE, BITMAP_SIZE)
        canvas.rotate(30f)
        //将 canvas 坐标系挪动到原点, 二维平移
        canvas.translate(-(BITMAP_PADDING + BITMAP_SIZE / 2), -(BITMAP_PADDING + BITMAP_SIZE / 2))
        canvas.drawBitmap(bitmap, BITMAP_PADDING, BITMAP_PADDING, paint)
        canvas.restore()
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