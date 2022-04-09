package com.hencoder.drag.view


import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import kotlinx.android.synthetic.main.drag_up_down.view.*

/**
 * 使用 ViewDragHelper 实现拖拽
 * 可以纵向或者横向拖拽并且可以自动停靠的滑动控件
 */
class DragUpDownLeftRightLayout(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
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
        var capturedLeft = 0f
        var capturedTop = 0f

        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return child === draggedView
        }

        //钳制方法只钳制了纵向(只能纵向滑动横向无法滑动), 即钳制了哪个方向哪个方向才可以移动
        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            return top
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            return left
        }

        //当 view 被拖起来的时候会调用该方法, 可以执行一些初始化代码
        override fun onViewCaptured(capturedChild: View, activePointerId: Int) {
            //记录被拖起的 view 的初始位置
            capturedLeft = capturedChild.left.toFloat()
            capturedTop = capturedChild.top.toFloat()
        }

        //xvel 是水平速度, yvel 是垂直速度
        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {

            var finalLeft = releasedChild.left
            var finalTop = releasedChild.top

            //如果是垂直滑动
            if(Math.abs(releasedChild.top - capturedTop) > Math.abs(releasedChild.left - capturedLeft)){
                //垂直停靠
                //如果垂直速度大于最小速度阈值, 那么向要移动到的那一侧停靠
                if (Math.abs(yvel) > viewConfiguration.scaledMinimumFlingVelocity) {
                    //如果是向下滑动
                    if (yvel > 0) {
                        finalTop = height - releasedChild.height
                    } else {
                        finalTop = 0
                    }
                    Log.e("TAG", "onViewReleased  yvel > xvel  if{} finalTop = ${finalTop}")
                } else { //否则恢复到当前的一侧
                    //当 releasedChild.top == height - releasedChild.bottom 时, 拖动的 view 在界面中心
                    //所以 if (releasedChild.top < height - releasedChild.bottom) 拖动的 view 在靠近当前的一侧, 那么停靠到该侧
                    if (releasedChild.top < height - releasedChild.bottom) {
                        finalTop = 0
                    } else { //拖动的 view 在要移动到的那一侧, 那么停靠到该侧
                        finalTop = height - releasedChild.height
                    }
                    Log.e("TAG", "onViewReleased  yvel > xvel  else{} finalTop = ${finalTop}")
                }
            }else{
                //水平停靠
                if (Math.abs(xvel) > viewConfiguration.scaledMinimumFlingVelocity) {
                    //如果是向下滑动
                    if (xvel > 0) {
                        finalLeft = width - releasedChild.width
                    } else {
                        finalLeft = 0
                    }
                    Log.e("TAG", "onViewReleased  yvel < xvel  if{} finalLeft = ${finalLeft}")
                } else { //否则恢复到当前的一侧
                    //当 releasedChild.top == height - releasedChild.bottom 时, 拖动的 view 在界面中心
                    //所以 if (releasedChild.top < height - releasedChild.bottom) 拖动的 view 在靠近当前的一侧, 那么停靠到该侧
                    if (releasedChild.left < width - releasedChild.right) {
                        finalLeft = 0
                    } else { //拖动的 view 在要移动到的那一侧, 那么停靠到该侧
                        finalLeft = width - releasedChild.width
                    }
                    Log.e("TAG", "onViewReleased  yvel < xvel  else{} finalLeft = ${finalLeft}")
                }
            }
            dragHelper.settleCapturedViewAt(finalLeft, finalTop)
            postInvalidateOnAnimation()
        }
    }
}