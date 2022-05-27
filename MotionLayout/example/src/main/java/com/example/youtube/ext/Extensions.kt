package com.example.youtube.ext

import android.content.res.Resources
import android.util.TypedValue
import android.view.ViewParent

val Float.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        Resources.getSystem().displayMetrics
    )

val Int.dp
    get() = this.toFloat().dp

fun <ViewType> Any.smartCast() : ViewType{
    return this as ViewType
}