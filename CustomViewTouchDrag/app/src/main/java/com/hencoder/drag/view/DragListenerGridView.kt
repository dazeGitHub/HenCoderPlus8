package com.hencoder.drag.view

import android.content.Context
import android.util.AttributeSet
import android.view.DragEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import java.util.*

private const val COLUMNS = 2
private const val ROWS = 3

/**
 * 使用 OnDragListener 实现 长按拖拽 (注意: 需要长按才能拖拽), 拖拽的图像是半透明的
 */
class DragListenerGridView(context: Context?, attrs: AttributeSet?) : ViewGroup(context, attrs) {
    private var dragListener: OnDragListener = HenDragListener()
    private var draggedView: View? = null
    private var orderedChildren: MutableList<View> = ArrayList()

    init {
        isChildrenDrawingOrderEnabled = true // 获取当前这个ViewGroup是否是按照顺序进行绘制的
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        for (child in children) {
            orderedChildren.add(child) // 初始化位置
            child.setOnLongClickListener { longClickView ->
                draggedView = longClickView

                //使用过时的 startDrag() 方法, 手指移动的时候 longClickView 会自动跟随手指, 而不需要自己写移动代码
                //startDrag() 可以传两个数据, data 和 myLocalState 都是数据
                //区别是 myLocalState 是本地数据, 即随时随刻都能拿到的数据, 而 data 是只能在松手的时刻, 即 HenDragListener 的 onDrag() 中 event.action = DragEvent.ACTION_DROP 时才能拿到
                //public final boolean startDrag(ClipData data, DragShadowBuilder shadowBuilder, Object myLocalState, int flags) { }
                longClickView.startDrag(null, DragShadowBuilder(longClickView), longClickView, 0)
                //使用不过时的方法 startDragAndDrop() 会提示 : Call requires API level 24 (current min is 21)
                //v.startDragAndDrop(null, DragShadowBuilder(v), v, 0)

                false
            }
            //拖拽时实现子 View 自动排序, 所以要设置拖拽监听, 因为所有的子View 都是用的一个回调 dragListener, 所以一个 View 移动的时候其他 view 的回调都会被执行
            child.setOnDragListener(dragListener)
        }
    }

    //对于自定义 view 甚至可以不用写 dragListener, 直接重写 onDragEvent() 方法也行,
    //不会只是被拖拽的 view 会回调这个 onDragEvent() 方法, 同样是所有的 view 都会回调这个方法, 并且无论是否设置 dragListener 该方法也会回调
    //回调太多不会影响性能, 因为 super.onDragEvent(event) 只是返回了一个 false
    override fun onDragEvent(event: DragEvent?): Boolean {
        return super.onDragEvent(event)
    }

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

            //将子 View 全部摆放到左上角, 而不是依次摆放, 这样算动画的时候是以左上角来做为基准原点的, 比较好算
            // child.layout(left = 0, top = 0, right = childWidth, bottom = childHeight)
            child.layout(0, 0, childWidth, childHeight)

            child.translationX = childLeft.toFloat()
            child.translationY = childTop.toFloat()
        }
    }

    private inner class HenDragListener : OnDragListener {
        override fun onDrag(v: View, event: DragEvent): Boolean {
            when (event.action) {
                //拖拽刚开始时
                //因为所有的子 View 都共用一个监听 HenDragListener, 所以需要判断 如果当前触发拖拽事件是 v 这个 view, 那么才隐藏 v, 否则所有的子 view 都会被隐藏掉,
                //这个 event.localState 是拖拽刚开始时 longClickView.startDrag(data = null, shadowBuilder = dragShadowBuilder, myLocalState = longClickView, flags = 0) 传入的第三个参数 longClickView
                //即 event.localState 是开始长按拖动时的那个 view
                DragEvent.ACTION_DRAG_STARTED -> if (event.localState === v) {
                    //将要拖拽的 view 手动隐藏, 否则拖拽时原先的位置还有刚才拖拽的 view
                    v.visibility = View.INVISIBLE
                }
                //当手指触摸到某个 view 的区域内, 那么 onDrag() DragEvent.ACTION_DRAG_ENTERED 内的代码就会回调, 在 View 刚刚托起时也会触发, 因为自己进入了自己的范围内
                DragEvent.ACTION_DRAG_ENTERED -> if (event.localState !== v) { //如果不在自己的范围内才会 sort 重排
                    sort(v)
                }
                //当手指离开某个 view 的区域, 和 DragEvent.ACTION_DRAG_ENTERED 正好是相反的
                DragEvent.ACTION_DRAG_EXITED -> {

                }
                //因为 DragEvent.ACTION_DRAG_STARTED 的时候将原来的 view 隐藏了, 所以这里要再将其显示
                DragEvent.ACTION_DRAG_ENDED -> if (event.localState === v) {
                    v.visibility = View.VISIBLE
                }
                DragEvent.ACTION_DROP -> { //松手的时候

                }
            }
            return true
        }
    }

    //因为是在 DragListener 的 onDrag() DragEvent.ACTION_DRAG_ENTERED 时调用的这个 sort(v) 方法, 传入的 targetView 就是要进入的区域的 view
    //而这个 draggedView 是刚才长按监听触发时赋值的 longClickView
    private fun sort(targetView: View) {
        var draggedIndex = -1
        var targetIndex = -1
        //找到要拖拽到该区域的 view (即 targetView) 的索引 targetIndex, 当前拖拽的 view (即 draggedView) 的索引 draggedIndex
        for ((index, child) in orderedChildren.withIndex()) {
            if (targetView === child) {
                targetIndex = index
            } else if (draggedView === child) {
                draggedIndex = index
            }
        }
        //将 orderedChildren 中的 targetView 和 draggedView 交换位置
        orderedChildren.removeAt(draggedIndex)
        orderedChildren.add(targetIndex, draggedView!!)
        var childLeft: Int
        var childTop: Int
        val childWidth = width / COLUMNS
        val childHeight = height / ROWS
        //遍历 orderedChildren 中的 view, 依次修改 translationX 和 translationY 从而设置 view 的位置
        for ((index, child) in orderedChildren.withIndex()) {
            childLeft = index % 2 * childWidth
            childTop = index / 2 * childHeight
            child.animate()
                .translationX(childLeft.toFloat())
                .translationY(childTop.toFloat())
                .setDuration(150)
        }
    }
}
