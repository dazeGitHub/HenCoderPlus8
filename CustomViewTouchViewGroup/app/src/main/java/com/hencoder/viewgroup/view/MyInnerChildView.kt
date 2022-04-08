package com.hencoder.viewgroup.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

class MyInnerChildView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                Log.e("TAG", "MyInnerChildView onTouchEvent ACTION_DOWN")
            }
            MotionEvent.ACTION_MOVE -> {
                Log.e("TAG", "MyInnerChildView onTouchEvent ACTION_MOVE")
            }
            MotionEvent.ACTION_UP -> {
                Log.e("TAG", "MyInnerChildView onTouchEvent ACTION_UP")
                performClick()
            }
            MotionEvent.ACTION_CANCEL -> {
                Log.e("TAG", "MyInnerChildView onTouchEvent ACTION_CANCEL")
            }
        }
        Log.e("TAG", "MyInnerChildView onTouchEvent return true")
        return true
    }
}