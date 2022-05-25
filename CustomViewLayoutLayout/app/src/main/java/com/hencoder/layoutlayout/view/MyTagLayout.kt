package com.hencoder.layoutlayout.view

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children

class MyTagLayout(context: Context?, attrs: AttributeSet?) : ViewGroup(context, attrs) {
    private val childrenRounds = listOf<Rect>()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //自己测量, 和 父View 如何测量无关, 所以这里注释掉
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        //1. 先测量 子View 的尺寸
        for ((index, childView) in children.withIndex()) {
            //父View 给了 MyTagLayout 限制, 那么 MyTagLayout 也需要给 子View 限制
            childView.measure(childWidthSpec, childHeightSpec)
            //测量完后将 子View 的 left,top,right,bottom 都定好了
            val childBounds = childrenRounds[index]
            childBounds.set()
        }
        //2. 算出自己的尺寸

    }

    //  做为一个 ViewGroup, 有 子View 那么必须去排列它们, 如果不重写 onLayout() 那么该 ViewGroup 就没有意义了
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        for ((index, childView) in children.withIndex()) {
            // 1. 先让 子View 充满 父View
            //     这里的 left, top, right, bottom 是 MyTagLayout 相对于它的 父View 的位置
            //     所以调用 childView.layout() 传入的 left 和 top 应该是 0
            //     right 应该是 父View 的宽度 = right - left
            // childView.layout(0, 0, right - left, bottom - top)

            // 2. 索引为 0 的 子View 放到左上角, 其他的 子View 放到右上角
            // if (children.indexOf(childView) == 0) {
            //    childView.layout(0, 0, (right - left) / 2, (bottom - top) / 2)
            // } else {
            //    childView.layout((right - left) / 2, (bottom - top) / 2, right - left, bottom - top)
            // }

            // 3. 测量后布局
            //     这里的尺寸需要 onMeasure() 测量完了保存一下,
            //     然后这里通过 childrenRounds 拿到子View 对应的 Bound 再做布局
            val childBounds = childrenRounds[index]
            childView.layout(childBounds.left, childBounds.top, childBounds.right, childBounds.bottom)
        }
    }
}