package com.hencoder.viewgroup.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.OverScroller
import androidx.core.view.children
import kotlin.math.abs


class TwoPager(context: Context, attrs: AttributeSet) : ViewGroup(context, attrs) {
    private var downX = 0f
    private var downY = 0f
    private var downScrollX = 0f
    private var scrolling = false
    private val overScroller: OverScroller = OverScroller(context)
    private val viewConfiguration: ViewConfiguration = ViewConfiguration.get(context)
    private val velocityTracker = VelocityTracker.obtain()

    //最小快滑速度
    private var minVelocity = viewConfiguration.scaledMinimumFlingVelocity

    //最大快滑速度, 这是为了防止用户不小心快滑动了一下, 导致程序出现不符合用户行为的结果, 所以使用 maxVelocity 限制快滑速度, 如果快滑速度超过了 maxVelocity, 那么就将其设置为 maxVelocity
    //例如最大快滑速度是 8, 而用户手指滑动速度是 24, 那么当用户一松手就会以 8 的初始速度进行快滑
    private var maxVelocity = viewConfiguration.scaledMaximumFlingVelocity

    //开始滑动的距离阈值, 每个系统不一样
    private var pagingSlop = viewConfiguration.scaledPagingTouchSlop

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //给所有的子 View 统一的宽和高的限制, 这里填入了自己的宽高限制, 即用我自己的宽高限制来测量子 View, 所有 measureChildren() 这个方法很不常用
        //因为子 View 要填满自己, 要正好和自己一样高一样宽
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        //super.onMeasure() 调用的是 setMeasuredDimension()
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        //不管开发者 xml 里边写什么, 这里都忽略掉, 因为这是私有控件(自己的软件里的控件, 自己开发自己用),
        var childLeft = 0
        val childTop = 0
        var childRight = width
        val childBottom = height
        for (child in children) {
            child.layout(childLeft, childTop, childRight, childBottom)
            childLeft += width
            childRight += width
        }
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
            velocityTracker.clear()
        }
        velocityTracker.addMovement(event)
        var result = false
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                scrolling = false
                //在 MotionEvent.ACTION_DOWN 的时候存以下落点, 稍后在 onTouchEvent() 中会使用
                downX = event.x
                downY = event.y
                downScrollX = scrollX.toFloat()
                Log.e("TAG", "onInterceptTouchEvent downX = $downX downY = $downY")
            }
            MotionEvent.ACTION_MOVE -> if (!scrolling) {
                val dx = downX - event.x
                if (abs(dx) > pagingSlop) {
                    scrolling = true
                    //当是一个滑动控件时, 并且拦截了子 View 的事件的时候, 除了 onInterceptTouchEvent() 要返回 true 让子 View 收到 Cancel 事件外, 还需要告诉父 View 不要拦截自己了
                    //例如 ScrollView 在 ViewPager 里边, 当 ScrollView 开始上下滑动的时候, 用户是不希望再左右滑动的
                    parent.requestDisallowInterceptTouchEvent(true)
                    result = true
                }
            }
        }
        return result
    }

    //onTouchEvent() 的 ACTION_DOWN 中的代码和 onInterceptTouchEvent() 的 ACTION_DOWN 中的代码几乎一样,
    //
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
            velocityTracker.clear()
        }
        velocityTracker.addMovement(event)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
                //获取初始偏移量
                downScrollX = scrollX.toFloat()
                Log.e("TAG", "onTouchEvent downX = $downX downY = $downY downScrollX = $downScrollX")
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = (downX - event.x + downScrollX).toInt()
                    .coerceAtLeast(0) //当滑动到最左边时就不能再滑动了, 即 dx 不能小于 0
                    .coerceAtMost(width) //当滑动到最右边时就不能再滑动了, 即 dx 不能大于 width
                Log.e("TAG", "onTouchEvent dx = $dx")
                scrollTo(dx, 0)
            }
            MotionEvent.ACTION_UP -> {
                //计算速度, 速度 = 在 1000ms 内移动的像素数, 如果速度超过 maxVelocity, 那么 速度 = maxVelocity
                //1000 是单位, 表示 1000ms; maxVelocity 是最大速度限制, 如果速度超过 maxVelocity, 那么就认为是 maxVelocity
                velocityTracker.computeCurrentVelocity(1000, maxVelocity.toFloat()) // 5m/s 5km/h
                val vx = velocityTracker.xVelocity //速度即 1000ms 能移动的像素数, 如果改为 100 那么 vx 就会缩小 1/10
                val scrollX = scrollX
                Log.e("TAG", "onTouchEvent ACTION_UP vx = $vx scrollX = $scrollX")
                //如果 |水平速度| 小于最小速度, 即速度比较慢, 那么选择最近的一侧来吸附
                val targetPage = if (abs(vx) < minVelocity) {
                    // 从右往左滑动时 scrollX 才是正的, 如果从右往左滑动并且超过了当前控件宽度的一半, 那么就选择第 1 张图, 否则选择第 0 张图
                    if (scrollX > width / 2) 1 else 0
                } else {// 如果速度比较快, 那么直接撞过去
                    // vx 是 (终点 - 起点) 的距离计算出来的速度, 如果水平速度 < 0 说明是从右往左滑动 <-
                    // 如果是负值, 表示往坐标轴的负向快滑(从右往左滑), 并且速度又比较快, 那么 targetPage = 1 选择第 1 张图, 否则选择第 0 张图
                    if (vx < 0) 1 else 0
                }
                //具体停靠的实现
                val scrollDistance = if (targetPage == 1) width - scrollX else -scrollX
                //之前是使用 OverScroller 的 fling() 方法实现惯性滚动, 这里使用 startScroll() 方法
                //给一个目标位置, 自己算应该如何移动过去, 到达的时候要求速度刚好是0
                //void startScroll(int startX, int startY, int dx, int dy) { }
                overScroller.startScroll(getScrollX(), 0, scrollDistance, 0)
                //postOnAnimation(runnable) 是在下一帧执行 runnable, 而 postInvalidateOnAnimation() 却没有参数
                //将界面标记为失效, 因为没有 runnable 代码, 所以使用 computeScroll() 方法
                postInvalidateOnAnimation()
            }
        }
        return true
    }

    override fun computeScroll() {
        //使用 overScroller.computeScrollOffset() 终止循环
        if (overScroller.computeScrollOffset()) {
            scrollTo(overScroller.currX, overScroller.currY)
            postInvalidateOnAnimation() //自动将界面标记为失效, 下一帧到的时候 draw() 方法就会被调用, draw() 方法里又会调用 computeScroll()
        }
    }
}
