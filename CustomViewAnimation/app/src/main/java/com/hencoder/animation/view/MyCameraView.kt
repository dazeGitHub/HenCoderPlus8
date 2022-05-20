package com.hencoder.animation.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.withSave
import com.hencoder.animation.R
import com.hencoder.animation.dp

private val BITMAP_SIZE = 200.dp
private val BITMAP_PADDING = 100.dp

/**
 * 使用 Camera 实现翻页效果, 并且使用属性动画实现多层变换
 */
class MyCameraView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bitmap = getAvatar(BITMAP_SIZE.toInt())
    private val camera = Camera()

    var topFlipCameraRotationDegree = 0f    //从顶部翻起来的三维旋转角度
        set(value) {
            field = value
            invalidate()
        }

    var bottomFlipCameraRotateDegree = 30f  //从底部翻起来的三维旋转角度  //30f
        set(value) {
            field = value
            invalidate()
        }

    var flipRotation = 0f //canvas 的旋转角度, 即折线的角度   //30f
        set(value) {
            field = value
            invalidate()
        }

    init {
        //沿着 x 轴顺时针旋转 30 度
        //camera.rotateX(bottomFlipCameraRotateDegree) //需要将这两行代码移动到 onDraw() 里, 否则不会起作用
        //camera.setLocation(0f, 0f, - 6 * resources.displayMetrics.density)
    }

    override fun onDraw(canvas: Canvas) {

        //裁剪上半部分
        canvas.withSave { //使用 canvas.withSave {} 扩展函数替代 canvas.save() 和 canvas.restore()
            //canvas.save()
            canvas.translate(BITMAP_PADDING + BITMAP_SIZE / 2, BITMAP_PADDING + BITMAP_SIZE / 2)
            //在移动前先转回来
            canvas.rotate(-flipRotation)

            //对 camera 的操作使用 camera.save() 和 camera.restore()
            camera.save()
            //三维旋转
            camera.rotateX(topFlipCameraRotationDegree)
            camera.applyToCanvas(canvas)    //等价于 camera 的复杂操作被 canvas 做了
            camera.restore()

            canvas.clipRect(-BITMAP_SIZE, -BITMAP_SIZE, BITMAP_SIZE, 0f)
            canvas.rotate(flipRotation)
            //将 canvas 坐标系挪动到原点, 二维平移
            canvas.translate(
                -(BITMAP_PADDING + BITMAP_SIZE / 2),
                -(BITMAP_PADDING + BITMAP_SIZE / 2)
            )
            canvas.drawBitmap(bitmap, BITMAP_PADDING, BITMAP_PADDING, paint)
            //canvas.restore()
        }

        //裁剪下半部分
        // clipRect(left, top, right, bottom), 裁切图片的下一半 (只有下一半才显示)
        //不能再这裁切, 否则会导致多裁切了图片一部分的问题
//        canvas.clipRect(
//            BITMAP_PADDING, BITMAP_PADDING + BITMAP_SIZE / 2,
//            BITMAP_PADDING + BITMAP_SIZE,BITMAP_PADDING + BITMAP_SIZE
//        )
        canvas.withSave {
            //canvas.save()
            //将 canvas 坐标系再挪动回来, 二维平移
            canvas.translate(BITMAP_PADDING + BITMAP_SIZE / 2, BITMAP_PADDING + BITMAP_SIZE / 2)
            //在移动前先转回来
            canvas.rotate(-flipRotation)

            //对 camera 的操作使用 camera.save() 和 camera.restore()
            camera.save()
            //三维旋转
            camera.rotateX(bottomFlipCameraRotateDegree)
            camera.applyToCanvas(canvas)    //等价于 camera 的复杂操作被 canvas 做了
            camera.restore()

            //裁切
            canvas.clipRect(- BITMAP_SIZE, 0f, BITMAP_SIZE, BITMAP_SIZE)
            canvas.rotate(flipRotation)
            //将 canvas 坐标系挪动到原点, 二维平移
            canvas.translate(-(BITMAP_PADDING + BITMAP_SIZE / 2), -(BITMAP_PADDING + BITMAP_SIZE / 2))
            canvas.drawBitmap(bitmap, BITMAP_PADDING, BITMAP_PADDING, paint)
            //canvas.restore()
        }
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