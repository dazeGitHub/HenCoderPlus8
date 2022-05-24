package com.hencoder.layoutsize.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatImageView
import com.hencoder.layoutsize.dp

class MeasuredSizeTestView(context: Context, attrs: AttributeSet?) : AppCompatImageView(context, attrs) {

    init {
        Log.e("TAG", "max layout_width = ${240.dp}")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val measuredWidth = measuredWidth
        Log.e("TAG", "MeasuredSizeTestView measuredWidth = $measuredWidth")
    }
}