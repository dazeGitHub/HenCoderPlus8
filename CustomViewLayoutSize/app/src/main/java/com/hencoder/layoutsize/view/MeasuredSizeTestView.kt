package com.hencoder.layoutsize.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatImageView
import com.hencoder.layoutsize.dp

class MeasuredSizeTestView(context: Context, attrs: AttributeSet?) : AppCompatImageView(context, attrs) {

    init {
        Log.e("TAG", "max layout_width = ${240.dp} max layout_height = ${80.dp}")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.e("TAG", "MeasuredSizeTestView onMeasure measuredWidth Pre = $measuredWidth")
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.e("TAG", "MeasuredSizeTestView onMeasure super.onMeasure()")
        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        val measuredWidth = measuredWidth
        Log.e("TAG", "MeasuredSizeTestView onMeasure measuredWidth Back = $measuredWidth")
//        Log.e("TAG", "MeasuredSizeTestView " +
//                "widthSpecMode = $widthSpecMode widthSpecSize = $widthSpecSize " +
//                "heightSpecMode = $heightSpecMode heightSpecSize = $heightSpecSize "
//        )
//
//        if(widthSpecMode == MeasureSpec.EXACTLY){
//            Log.e("TAG","widthSpecMode == MeasureSpec.EXACTLY")
//        }else if(widthSpecMode == MeasureSpec.AT_MOST){
//            Log.e("TAG","widthSpecMode == MeasureSpec.AT_MOST")
//        }else{
//            Log.e("TAG","widthSpecMode == MeasureSpec.UNSPECIFIED")
//        }
//
//        if(heightSpecMode == MeasureSpec.EXACTLY){
//            Log.e("TAG","heightSpecMode == MeasureSpec.EXACTLY")
//        }
    }
}