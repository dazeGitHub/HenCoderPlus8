package com.example.motionlayout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.TransitionAdapter

class SampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_float_action_button) //activity_sample

        findViewById<MotionLayout>(R.id.motion_layout).addTransitionListener(object: TransitionAdapter(){
            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {
                super.onTransitionTrigger(motionLayout, triggerId, positive, progress)
                Toast.makeText(this@SampleActivity, "triggerId = $triggerId", LENGTH_SHORT).show()
            }
        })
    }
}
