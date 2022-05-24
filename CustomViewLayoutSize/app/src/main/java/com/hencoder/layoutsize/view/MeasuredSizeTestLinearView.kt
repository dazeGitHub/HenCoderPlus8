package com.hencoder.layoutsize.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.LinearLayout

class MeasuredSizeTestLinearView(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    //即使什么也不干, MeasuredSizeTestLinearView 的 onMeasure() 方法也会调用两次
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.e("TAG", "MeasuredSizeTestLinearView onMeasure")
    }
}