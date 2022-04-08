package com.hencoder.viewgroup.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.FrameLayout
import kotlin.math.abs

/**
 * 模拟 ScrollView, 但是不处理滑动过程, 只关注事件分发过程
 */
class MyInnerScrollView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {
    private var downX = 0f
    private var downY = 0f
    private var scrolling = false
    private val viewConfiguration: ViewConfiguration = ViewConfiguration.get(context)
    private var pagingHorSlop = viewConfiguration.scaledPagingTouchSlop
    private var pagingVerSlop = 30

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        var result = false
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                scrolling = false
                //在 MotionEvent.ACTION_DOWN 的时候存以下落点, 稍后在 onTouchEvent() 中会使用
                downX = event.x
                downY = event.y
                Log.e("TAG", "MyInnerScrollView onInterceptTouchEvent ACTION_DOWN downX = $downX downY = $downY")
            }
            MotionEvent.ACTION_MOVE -> {
                if (!scrolling) {
                    val dx = downX - event.x
                    val dy = downY - event.y
                    Log.e("TAG", "MyInnerScrollView onInterceptTouchEvent ACTION_MOVE abs(dy) = ${abs(dy)} pagingVerSlop = $pagingVerSlop   " +
                            "abs(dx) = ${abs(dx)}  pagerHorPagingSlop = $pagingHorSlop")
                    if (abs(dy) > pagingVerSlop) {
                        scrolling = true
                        //当是一个滑动控件时, 并且拦截了子 View 的事件的时候, 除了 onInterceptTouchEvent() 要返回 true 让子 View 收到 Cancel 事件外, 还需要告诉父 View 不要拦截自己了
                        //例如 ScrollView 在 ViewPager 里边, 当 ScrollView 开始上下滑动的时候, 用户是不希望再左右滑动的
                        parent.requestDisallowInterceptTouchEvent(true)
                        Log.e("TAG", "MyInnerScrollView onInterceptTouchEvent ACTION_MOVE  parent.requestDisallowInterceptTouchEvent(true)") // false
                        result = true
                    }
                }
                Log.e("TAG", "MyInnerScrollView onInterceptTouchEvent ACTION_MOVE result = $result")
            }
            MotionEvent.ACTION_UP -> {
                Log.e("TAG", "MyInnerScrollView onInterceptTouchEvent ACTION_UP")
            }
            MotionEvent.ACTION_CANCEL -> {
                Log.e("TAG", "MyInnerScrollView onInterceptTouchEvent ACTION_CANCEL")
            }
        }
        Log.e("TAG", "MyInnerScrollView onInterceptTouchEvent result = $result")
        return result
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                scrolling = false
                downX = event.x
                downY = event.y
                Log.e("TAG", "MyInnerScrollView onTouchEvent ACTION_DOWN downX = $downX downY = $downY")
            }
            MotionEvent.ACTION_MOVE -> {
                Log.e("TAG", "MyInnerScrollView onTouchEvent ACTION_MOVE")

                val dx = downX - event.x
                val dy = downY - event.y
                Log.e("TAG", "MyInnerScrollView onTouchEvent ACTION_MOVE abs(dy) = ${abs(dy)} pagingVerSlop = $pagingVerSlop   " +
                        "abs(dx) = ${abs(dx)}  pagerHorPagingSlop = $pagingHorSlop")
            }
            MotionEvent.ACTION_UP -> {
                Log.e("TAG", "MyInnerScrollView onTouchEvent ACTION_UP")
            }
            MotionEvent.ACTION_CANCEL -> {
                Log.e("TAG", "MyInnerScrollView onTouchEvent ACTION_CANCEL")
            }
        }
        Log.e("TAG", "MyInnerScrollView onTouchEvent return true")
        return true
    }
}