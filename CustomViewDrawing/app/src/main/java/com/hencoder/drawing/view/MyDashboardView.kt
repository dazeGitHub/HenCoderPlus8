package com.hencoder.drawing.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

private val RADIUS = 150f.px             //半径宽度
private val POINTER_LENGTH = 120f.px     //指针长度
private val DASH_WIDTH = 2f.px           //刻度宽度
private val DASH_LENGTH = 10f.px         //刻度长度
private const val OPEN_ANGLE = 120f
private const val START_ANGLE = 90 + OPEN_ANGLE / 2f

class MyDashboardView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val mArcPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mDashPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mArcPath = Path()
    private val mDashPathShape = Path()
    private lateinit var mPathDashEffect: PathEffect

    init {
        mArcPaint.strokeWidth = 3f.px
        mArcPaint.style = Paint.Style.STROKE //FILL, STROKE, FILL_AND_STROKE
        mDashPaint.strokeWidth = 3f.px
        mDashPaint.style = Paint.Style.STROKE

        mDashPathShape.addRect(0f, 0f, DASH_WIDTH, DASH_LENGTH, Path.Direction.CCW)
     }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        //因为 mArcPath 的位置是和 View 的尺寸有关的, 横纵坐标和 View 的宽高相关的
        mArcPath.reset()
        mArcPath.addArc(
            width / 2f - RADIUS,
            height / 2f - RADIUS,
            width / 2f + RADIUS,
            height / 2f + RADIUS,
            START_ANGLE,
            360 - OPEN_ANGLE
        )

        //forceClosed : 是否要自动闭合, 如果是 true 那么就是 从起点到终点的直线距离长度
        val pathMeasure = PathMeasure(mArcPath, false)

        //PathDashPathEffect(Path shape, float advance, float phase, Style style)
        //  advance : 是否提前移动距离, 将前置量空过去再开始绘制
        //  phase   : 每过多长距离绘制一次
        //  style   : 拐角的 style
        //但是 Android 将 advance 和 phase 搞反了, 所以 advance 填 50f, phase 填 0f
        //PathDash 的含义 : 使用 Path 来做 Dash
        //总共是 21 个刻度(因为最开始是刻度 0), 20 个间隔, 所以有 :
        //  advance = (pathMeasure.length - DASH_WIDTH) / 20
        mPathDashEffect = PathDashPathEffect(
            mDashPathShape,
            (pathMeasure.length - DASH_WIDTH) / 20f,
            0f,
            PathDashPathEffect.Style.ROTATE
        )
        mDashPaint.pathEffect = mPathDashEffect
    }

    override fun onDraw(canvas: Canvas) {
        //画弧
        //可以指定一个矩形, 也可以指定 left,top,right,bottom
        //仪表盘是将一个圆或者椭圆去掉一部分就是弧, 因为仪表盘一般都是圆的, 所以 rect 是正方形而不是长方形
        //canvas.drawArc(left, top, right, bottom, startAngle, sweepAngle(划过的角度), useCenter, paint)
        //useCenter : 是否起点和终点连向中心点
//        canvas.drawArc(
//            width / 2f - 150f.px,
//            height / 2f - 150f.px,
//            width / 2f + 150f.px,
//            height / 2f + 150f.px,
//            START_ANGLE,
//            360 - OPEN_ANGLE,
//            false,
//            mArcPaint
//        )
        canvas.drawPath(mArcPath, mArcPaint)

        //画刻度
//        canvas.drawArc(
//            width / 2f - 150f.px,
//            height / 2f - 150f.px,
//            width / 2f + 150f.px,
//            height / 2f + 150f.px,
//            START_ANGLE,
//            360 - OPEN_ANGLE,
//            false,
//            mDashPaint
//        )
        //因为 mDashPaint 的 pathEffect 是 mPathDashEffect(带虚线), 所以可以用来绘制刻度
        canvas.drawPath(mArcPath, mDashPaint)

        //绘制指针
        //drawLine(startX, startY, stopX, stopY, paint)
        //stopX = startX + POINTER_WIDTH * cos(theta)
        //stopY = startY + POINTER_WIDTH * sin(theta)
        //因为 Math.cos() 里边的参数是弧度, 所以还需要将角度转换为弧度
        val pointerIndex = 10    //当前刻度
        val thetaAngle = markToRadians(pointerIndex)
        val stopX = width / 2f + POINTER_LENGTH * cos(thetaAngle).toFloat()
        val stopY = height / 2f + POINTER_LENGTH * sin(thetaAngle).toFloat()

        canvas.drawLine(
            width / 2f,
            height / 2f,
            stopX,
            stopY,
            mArcPaint
        )
    }

    private fun markToRadians(mark: Int) =
        Math.toRadians((90 + OPEN_ANGLE / 2f + (360 - OPEN_ANGLE) / 20f * mark).toDouble())
}