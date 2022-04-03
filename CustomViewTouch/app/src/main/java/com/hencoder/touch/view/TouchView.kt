package com.hencoder.touch.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class TouchView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    override fun onTouchEvent(event: MotionEvent): Boolean {
        //如果是抬起, 那么触发点击
        if (event.actionMasked == MotionEvent.ACTION_UP) {
            performClick()
        }
        MotionEvent.ACTION_POINTER_3_DOWN
        //return true
        return event.actionMasked == MotionEvent.ACTION_DOWN
    }
}