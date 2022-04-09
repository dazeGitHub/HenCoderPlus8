package com.hencoder.drag

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //使用 ViewDragHelper : drag_up_down drag_up_down_left_right drag_helper_grid_view
        //使用 dragListener: drag_listener_grid_view(半透明)  drag_to_collect  drag_tip
        setContentView(R.layout.drag_up_down_left_right)
    }
}