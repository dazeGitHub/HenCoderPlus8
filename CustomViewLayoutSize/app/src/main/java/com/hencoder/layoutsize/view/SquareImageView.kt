package com.hencoder.layoutsize.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.hencoder.layoutsize.dp
import kotlin.math.min

class SquareImageView(context: Context, attrs: AttributeSet?) : AppCompatImageView(context, attrs) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //这一行需要留着, 让父 View 来测量以下自己的尺寸, 宽 300dp, 高 200dp
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //父 View 测量完了就可以使用 getMeasuredWidth() 和 getMeasuredHeight() 拿到测量结果
        val size = min(getMeasuredWidth(), getMeasuredHeight())
        //使用 setMeasuredDimension() 将自己测量完的结果保存一下, 不做成返回值的形式是为了之后多次拿这个值比较方便
        //使用了 setMeasuredDimension() 保存测量结果, 测量才有意义
        setMeasuredDimension(size, size)
    }

    //父 View 调用这个 layout() 方法告诉当前这个 SquareImageView 的实际位置和尺寸
//    override fun layout(l: Int, t: Int, r: Int, b: Int) {
//        //super.layout(l, t, r, b) //super.layout() 将位置存了起来
//
//        //假设宽高都是 100dp
//        //super.layout(l, t, (l + 100.dp).toInt(), (t + 100.dp).toInt())
//
//        //以窄边为准
//        val width = r - l
//        val height = b - t
//        val size = min(width, height)
//        super.layout(l, t, l + size, t + size)
//    }
}