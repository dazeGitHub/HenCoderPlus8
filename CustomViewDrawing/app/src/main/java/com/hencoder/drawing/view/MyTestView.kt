package com.hencoder.drawing.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.util.AttributeSet
import android.view.View

val RADIUS_CIRCLE = 100f.px

class MyTestView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    //抗锯齿是修改图像, 将边缘模糊化, 加减一些半透明的像素, 默认是不开启的
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()
    lateinit var pathMeasure: PathMeasure

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        path.reset()
        //Direction :
        //  Path.Direction.CW : clockwise
        //  Path.Direction.CCW : counter-clockwise (逆时针), counter 是反对的意思
        //方向配合填充方式判断在多个图形相交的时候, 相交的内容是填充还是留空
        path.addCircle(width / 2f, height / 2f, RADIUS_CIRCLE, Path.Direction.CCW)
        path.addRect(
            width / 2f - RADIUS_CIRCLE,
            height / 2f,
            width / 2f + RADIUS_CIRCLE,
            height / 2f + 2 * RADIUS_CIRCLE,
            Path.Direction.CW
        )

        path.addCircle(width / 2f, height / 2f, RADIUS_CIRCLE * 1.5f, Path.Direction.CCW)
        //forceClosed : 是否要自动闭合, 如果是 true 那么就是 从起点到终点的直线距离长度
        pathMeasure = PathMeasure(path, false)
        pathMeasure.length //获得 path 的总长度
        //pathMeasure.getPosTan() //到指定长度时的切角大小, 即角度的正切值

        //WINDING (默认的填充规则, 一个方向画圈画出来的图形, 那么它们的内部不管怎么相交, 都是属于内部的)
        //EVEN_ODD (每遇到一个交点, 如果是偶数就是外部, 如果是奇数就是外部)
        path.fillType = Path.FillType.INVERSE_EVEN_ODD //EVEN_ODD
    }

    override fun onDraw(canvas: Canvas) {
//        canvas.drawLine(100f, 100f, 200f, 200f, paint)
//        canvas.drawCircle(
//            width / 2f,
//            height / 2f,
//            RADIUS_CIRCLE, //Utils.dp2px(RADIUS_CIRCLE), 将 dp 转换为 px
//            paint
//        )
        canvas.drawPath(path, paint)
    }
}