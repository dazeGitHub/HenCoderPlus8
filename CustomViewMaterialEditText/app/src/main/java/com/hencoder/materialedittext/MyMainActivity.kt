package com.hencoder.materialedittext

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.postDelayed
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_my_main.*

class MyMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_main)

//        my_material_edit_text.postDelayed(10000){
//            my_material_edit_text.useFloatingLabel = false
//        }
    }
}