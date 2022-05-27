package com.example.youtube

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.TransitionManager
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.example.youtube.ext.dp
import com.example.youtube.ext.smartCast

class ObjectAnimatorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_object_animator)

        findViewById<View>(R.id.iv_heart).setOnClickListener {
            // 1. 移动到屏幕最右边
            //  val distance = it.parent.smartCast<ViewGroup>().width - it.width
            //  it.animate()
            //    .translationX(distance.toFloat()) //200f.dp
            //    .start()

            // 2. 移动到屏幕中间
            // TransitionManager.beginDelayedTransition(it.parent.smartCast())
            // it.layoutParams.smartCast<FrameLayout.LayoutParams>().gravity = Gravity.CENTER

            // 3. 移动时缩放
            TransitionManager.beginDelayedTransition(it.parent.smartCast())
            with(it.layoutParams.smartCast<FrameLayout.LayoutParams>()){
                gravity = Gravity.CENTER
                height *= 2
                width *= 2
            }
            it.requestLayout()
        }
    }
}