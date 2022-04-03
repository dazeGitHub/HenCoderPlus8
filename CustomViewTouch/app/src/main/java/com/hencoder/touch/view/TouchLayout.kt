package com.hencoder.touch.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import kotlin.math.abs

//class TouchLayout(context: Context?, attrs: AttributeSet?) : ViewGroup(context, attrs) {
//  override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
//    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//  }
//
//  override fun shouldDelayChildPressedState(): Boolean {
//    return false
//  }
//
//  override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
//    return super.onInterceptTouchEvent(ev)
//    val delta = ev.y - ??
//    //如果 delta 超过 SLOP 这个阈值
//    //注意 : onInterceptTouchEvent() 需要先返回 false, 在需要的时候再返回 true, 如果在 ACTION_DOWN 的时候就直接拦截掉, 那么子 View 就永远收不到事件了
//    if (abs(delta) > SLOP) {
//      return true
//    } else {
//      return false
//    }
//  }
//
//  override fun onTouchEvent(event: MotionEvent?): Boolean {
//    ???
//    return ???
//  }
//}