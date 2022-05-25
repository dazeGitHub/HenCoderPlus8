package com.hencoder.layoutlayout.view

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children

class MyTagLayout(context: Context?, attrs: AttributeSet?) : ViewGroup(context, attrs) {
    private val childrenBounds = mutableListOf<Rect>()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //自己测量, 和 父View 如何测量无关, 所以这里注释掉
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        var widthUsed = 0
        var heightUsed = 0
        var lineMaxHeight = 0

        //先试试一行的
        //1. 先测量 子View 的尺寸
        for ((index, childView) in children.withIndex()) {

            //父View 给了 MyTagLayout 限制, 那么 MyTagLayout 也需要给 子View 限制 (即 childWidthSpec)
            //限制取决于 2 个条件 :
            // 1. 开发者意见 (即 layout_width 和 layout_height), 这个从 child.layoutParams 中取
            // 2. 可用空间, 看 MyTagLayout 的 父View 给了多少空间, 还要看 MyTagLayout 用了多少空间

            //widthUsed (已用宽度) 的计算 :
            //初始的已用宽度为 0, 在一行中每加一个 子View 那么 widthUsed 就会加上这个 子View 的宽度, 当换行后, 已用宽度重置为 0
            //heightUsed (已用高度) 的计算 :
            //初始的已用高度为 0, 直到换行后已用高度才是上一行最高的那一个

            measureChildWithMargins(childView, widthMeasureSpec, 0, heightMeasureSpec, heightUsed) //多行这么干
            //measureChildWithMargins(childView, widthMeasureSpec, widthUsed, heightMeasureSpec, heightUsed)  //单行这么干
            //myMeasureChildWithMargins(childView, widthMeasureSpec, widthUsed, heightMeasureSpec, heightUsed)



            //测量完后将 子View 的 left,top,right,bottom 都定好了, 保存到 childrenRounds 这个对应索引的 Rect 中
            //index 只有两种情况 : 小于 childrenRounds.size() 或者 等于 childrenRounds.size(),
            //为了防止取不到, 可以在 index == childrenRounds.size() 时创建一个 Bound, 然后添加到 childrenBounds 里
            if(index >= childrenBounds.size){
                childrenBounds.add(Rect()) //提示不要在 onMeasure() 里创建对象, 但是这里做了判断了, 防止一直加, 所以没有性能问题
            }
            val childBounds = childrenBounds[index]
            //left 是 已用宽度(widthUsed), top 是 已用高度(heightUsed)
            //right 是 已用宽度(widthUsed) + 子View 的宽度(childView.measuredWidth)
            //bottom 是 已用高度(heightUsed) + 子View 的高度(childView.measuredHeight)
            childBounds.set(widthUsed, heightUsed, widthUsed + childView.measuredWidth, heightUsed + childView.measuredHeight)

            //还需要不断更新 widthUsed 和 heightUsed
            widthUsed += childView.measuredWidth

            //每过一行增加上一行最高的那一个View 的高度
            lineMaxHeight = Math.max(lineMaxHeight, childView.measuredHeight)
        }

        //2. 算出自己的尺寸
        val selfWidth = widthUsed
        val selfHeight = lineMaxHeight
        setMeasuredDimension(selfWidth, selfHeight)
    }

    //可以使用安卓系统提供的 measureChildWithMargins() 方法来代替
    private fun myMeasureChildWithMargins(childView: View, widthMeasureSpec: Int, widthUsed: Int, heightMeasureSpec: Int, heightUsed: Int){

        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)

        val childLayoutParams = childView.layoutParams
        //如果是 layout_width="match_parent" 那么就是 LayoutParams.MATCH_PARENT = -1
        //如果是 layout_width="wrap_content" 那么就是 LayoutParams.WRAP_CONTENT = -2

        var childWidthSpecMode = 0
        var childWidthSpecSize = 0
        var childHeightSpecMode = 0
        var childHeightSpecSize = 0

        when(childLayoutParams.width){
            //1. 开发者让 MyTagLayout 的 子View 充满 MyTagLayout, 即 layout_width="match_parent"
            LayoutParams.MATCH_PARENT -> {
                when(widthSpecMode){         //看 MyTagLayout 的 父View 给的值
                    MeasureSpec.EXACTLY -> { //父View 给了精确的值
                        childWidthSpecMode = MeasureSpec.EXACTLY
                        childWidthSpecSize = widthSpecSize - widthUsed  //总的可用空间 - 用掉的空间
                    }
                    MeasureSpec.AT_MOST -> { //父View 告诉 MyTagLayout 最大只能这么大
                        //因为 父View MyTagLayout 的多大都给了子 View, 说明就是精确值了, 即 EXACTLY, 但是安卓官方的策略是 AT_MOST, 这里就用 AT_MOST 了
                        childWidthSpecMode = MeasureSpec.AT_MOST
                        childWidthSpecSize = widthSpecSize - widthUsed  //将 父View MyTagLayout 的所有值都给子 View 吧
                    }
                    MeasureSpec.UNSPECIFIED -> {
                        //父View 对 MyTagLayout 没限制, 但是 MyTagLayout 的子View 又是要填满 MyTagLayout, 那么只能给 MyTagLayout 的 子View 也设置为 UNSPECIFIED
                        childWidthSpecMode = MeasureSpec.UNSPECIFIED    //这里就违背开发者意愿了, 也是没办法的事情
                        childWidthSpecSize = 0  //因为不限制, 所以值没法填, 随便填个 0
                    }
                }
            }
            //2. 开发者让 MyTagLayout 的 子View 充满 包裹内容, 即 layout_width="wrap_content"
            LayoutParams.WRAP_CONTENT -> {
                when(widthSpecMode){
                    MeasureSpec.EXACTLY, MeasureSpec.AT_MOST -> { //子 View 随便量, 但是别超过 MyTagLayout 的精确值即可
                        childWidthSpecMode = MeasureSpec.AT_MOST
                        childWidthSpecSize = widthSpecSize - widthUsed
                    }
//                        MeasureSpec.AT_MOST -> { //这种情况也是 子View 随便量, 但是别超过 MyTagLayout 的上限
//                            childWidthSpecMode = MeasureSpec.AT_MOST
//                            childWidthSpecSize = widthSpecSize - widthUsed
//                        }
                    MeasureSpec.UNSPECIFIED -> {
                        //MyTagLayout 的 父View 对自己没要求, MyTagLayout 的子 View 是 layout_width="wrap_content"
                        //那么结果就是对 子View 也没要求, 即最后给 MyTagLayout 的 子View 也设置为 UNSPECIFIED
                        childWidthSpecMode = MeasureSpec.UNSPECIFIED
                        childWidthSpecSize = 0
                    }
                }
            }
            //3. 开发者给了 子View 一个精确值, 那么任何判断都不做, 直接用开发者的值
            else -> {
                childWidthSpecMode = MeasureSpec.UNSPECIFIED
                childWidthSpecSize = childLayoutParams.width
            }
        }

        //childHeightSpecMode 和 childHeightSpecSize 就不计算了, 但是计算过程是和 childWidthSpecMode 和 childHeightSpecSize 一样的
        val childWidthSpec = MeasureSpec.makeMeasureSpec(childWidthSpecMode, childWidthSpecSize)
        val childHeightSpec = MeasureSpec.makeMeasureSpec(childHeightSpecMode, childHeightSpecSize)
        childView.measure(childWidthSpec, childHeightSpec)
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
            val childBounds = childrenBounds[index]
            childView.layout(childBounds.left, childBounds.top, childBounds.right, childBounds.bottom)
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        //return super.generateLayoutParams(attrs)
        return MarginLayoutParams(context, attrs)
    }
}