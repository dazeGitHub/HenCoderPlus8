package com.hencoder.animation.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

/**
 * 使用 hardware layer 实现离屏缓冲
 */
class HardwareLayerView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    init{
        //设置 LayerType 对 View 来说, 要么使用要么就不使用, 不存在 onDraw() 过程中先打开 绘制完再关闭的情况,
        //因为 setLayerType() 会导致整个绘制机制发生改变, 所以它会触发重绘, 需要写到 onDraw() 的外边
        //setLayerType() 可以设置 3 种 LayerType :
        //  1. LAYER_TYPE_HARDWARE
        //  2. LAYER_TYPE_SOFTWARE
        //  3. LAYER_TYPE_NONE

        //这个不是开启硬件加速, 而是开启 View 级别的离屏缓冲, 并且使用硬件绘制实现这个缓冲
        setLayerType(LAYER_TYPE_HARDWARE,null)
        //这个也不是关闭硬件加速改用软件加速, 而是开启 View 级别的离屏缓冲, 同时使用软件绘制实现这个缓冲

        //这个是关闭 View 的离屏缓冲
        setLayerType(LAYER_TYPE_NONE,null)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }
}