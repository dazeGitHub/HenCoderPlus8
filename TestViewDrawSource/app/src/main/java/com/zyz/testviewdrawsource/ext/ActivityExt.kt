package com.zyz.testviewdrawsource.ext

import android.app.Activity
import android.view.View
import androidx.appcompat.app.AppCompatActivity

fun <T : View> Activity.fbi(id: Int): T {
    return this.findViewById(id) as T
}