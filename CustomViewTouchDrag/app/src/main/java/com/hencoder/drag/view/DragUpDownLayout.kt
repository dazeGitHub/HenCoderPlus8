package com.hencoder.drag.view


import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import kotlinx.android.synthetic.main.drag_up_down.view.*

/**
 * 使用 ViewDragHelper 实现拖拽
 */
class DragUpDownLayout(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    private var dragListener: ViewDragHelper.Callback = DragCallback()
    private var dragHelper: ViewDragHelper = ViewDragHelper.create(this, dragListener)
    private var viewConfiguration: ViewConfiguration = ViewConfiguration.get(context)

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return dragHelper.shouldInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        dragHelper.processTouchEvent(event)
        return true
    }

    override fun computeScroll() {
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    internal inner class DragCallback : ViewDragHelper.Callback() {
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return child === draggedView
        }

        //钳制方法只钳制了纵向(只能纵向滑动横向无法滑动), 即钳制了哪个方向哪个方向才可以移动
        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            return top
        }

        //xvel 是水平速度, yvel 是垂直速度
        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            //如果垂直速度大于最小速度阈值, 那么向要移动到的那一侧停靠
            if (Math.abs(yvel) > viewConfiguration.scaledMinimumFlingVelocity) {
                //如果是向下滑动
                if (yvel > 0) {
                    dragHelper.settleCapturedViewAt(0, height - releasedChild.height)
                } else {
                    dragHelper.settleCapturedViewAt(0, 0)
                }
            } else { //否则恢复到当前的一侧
                //当 releasedChild.top == height - releasedChild.bottom 时, 拖动的 view 在界面中心
                //所以 if (releasedChild.top < height - releasedChild.bottom) 拖动的 view 在靠近当前的一侧, 那么停靠到该侧
                if (releasedChild.top < height - releasedChild.bottom) {
                    dragHelper.settleCapturedViewAt(0, 0)
                } else { //拖动的 view 在要移动到的那一侧, 那么停靠到该侧
                    dragHelper.settleCapturedViewAt(0, height - releasedChild.height)
                }
            }
            postInvalidateOnAnimation()
        }
    }
}