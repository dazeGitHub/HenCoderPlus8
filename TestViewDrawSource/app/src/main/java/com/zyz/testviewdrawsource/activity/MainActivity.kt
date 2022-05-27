package com.zyz.testviewdrawsource.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.zyz.testviewdrawsource.R
import com.zyz.testviewdrawsource.ext.fbi
import kotlin.concurrent.thread

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fbi<TextView>(R.id.tv_on_create_update).setOnClickListener{
            startActivity(Intent(this, OnChildThreadActivity::class.java))
        }
        fbi<TextView>(R.id.tv_child_thread_sleep_update).setOnClickListener{
            startActivity(Intent(this, ChangeWithSleepActivity::class.java))
        }
        fbi<TextView>(R.id.tv_click_event_update).setOnClickListener{
            startActivity(Intent(this, OnClickActivity::class.java))
        }
        fbi<TextView>(R.id.tv_modify_tv_wrap_content_update).setOnClickListener{
            startActivity(Intent(this, WrapTextActivity::class.java))
        }
        fbi<TextView>(R.id.tv_pre_request_layout_update).setOnClickListener{
            startActivity(Intent(this, RequestBeforeActivity::class.java))
        }
        fbi<TextView>(R.id.tv_child_thread_add_view_update).setOnClickListener{
            startActivity(Intent(this, AddViewWithoutThreadActivity::class.java))
        }
        fbi<TextView>(R.id.tv_surface_view_update).setOnClickListener{
            startActivity(Intent(this, SampleSurfaceViewActivity::class.java))
        }
    }
}
