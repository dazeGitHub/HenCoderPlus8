package com.hencoder.drag.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.customview.widget.ViewDragHelper

private const val COLUMNS = 2
private const val ROWS = 3

/**
 * 使用 ViewDragHelper 实现拖拽
 */
class DragHelperGridView(context: Context?, attrs: AttributeSet?) : ViewGroup(context, attrs) {
    private var dragHelper = ViewDragHelper.create(this, DragCallback())

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //获取自己的 specWidth 和 specHeight
        //如果父 view 给的尺寸限制是 EXACTLY 或 AT_MOST 那么没关系, 但如果是 UNSPECIFIED 这样写就出问题了
        val specWidth = MeasureSpec.getSize(widthMeasureSpec)
        val specHeight = MeasureSpec.getSize(heightMeasureSpec)
        val childWidth = specWidth / COLUMNS
        val childHeight = specHeight / ROWS
        //用同样的尺寸对每个 view 都进行测量
        measureChildren(
            MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY)
        )
        setMeasuredDimension(specWidth, specHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var childLeft: Int
        var childTop: Int
        val childWidth = width / COLUMNS
        val childHeight = height / ROWS
        //遍历每个子 View, 然后调用它们的 layout() 方法, 从左往右摆放, 从上往下摆放
        for ((index, child) in children.withIndex()) {
            childLeft = index % 2 * childWidth
            childTop = index / 2 * childHeight
            child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return dragHelper.shouldInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        dragHelper.processTouchEvent(event)
        return true
    }

    override fun computeScroll() {
        //dragHelper.continueSettling() 方法会自动调用 ViewCompat.offsetLeftAndRight() 自动做偏移的处理
        //和 OverScroller 的用法很相似
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    private inner class DragCallback : ViewDragHelper.Callback() {
        var capturedLeft = 0f
        var capturedTop = 0f

        //只有这个方法是必须实现的, tryCaptureView 表示手是不是要将 view 拖动起来
        //在手刚摸到 view 的时候就会调用, 如果返回 true, 那么这个 view 就会跟着手走
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return true
        }

        //clamp : 夹紧, 钳子
        //根据手的偏移 left 或 top 是多少来返回实际偏移是多少 (任何钳制工作都不做), 如果 return 0 表示无论手指偏移多少 view 都偏移 0
        //重写了 tryCaptureView() 返回 true 表示可以拖, 但是重写了 clampViewPositionHorizontal() 和 clampViewPositionVertical() 才表示能拖动
        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            return left
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            return top
        }

        //当 view 被拖起来的时候会调用该方法, 可以执行一些初始化代码
        override fun onViewCaptured(capturedChild: View, activePointerId: Int) {
            //将 capturedChild 这个 View 的高度 + 1, 从而让整个 view 可以显示在其他兄弟 view 的上方, 从而盖住别的兄弟 view
            capturedChild.elevation = elevation + 1
            //记录被拖起的 view 的初始位置
            capturedLeft = capturedChild.left.toFloat()
            capturedTop = capturedChild.top.toFloat()
        }

        //当 View 的位置(坐标)改变, 例如实现 sort() 自动重排
        override fun onViewPositionChanged(
            changedView: View,
            left: Int,
            top: Int,
            dx: Int,
            dy: Int
        ) {
        }

        //当 view 被释放时该方法就会被调用
        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            //这只是做各种计算工作, 如果松手的时候那么将 view 放回到 onViewCaptured() 刚开始拖拽时记录的初始位置
            dragHelper.settleCapturedViewAt(capturedLeft.toInt(), capturedTop.toInt())  //settle : 解决, 把 ... 放好
            //这里让界面强制重绘, 触发 draw(), 然后会调用 computeScroll()
            postInvalidateOnAnimation()
        }
    }
}
