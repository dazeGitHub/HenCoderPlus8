package com.zyz.testviewdrawsource.activity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.zyz.testviewdrawsource.R
import com.zyz.testviewdrawsource.ext.fbi
import java.util.*
import kotlin.concurrent.thread

class SampleSurfaceViewActivity : AppCompatActivity() {

    var destroyed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample_surface_view)

        var surfaceView = fbi<SurfaceView>(R.id.surface)
        surfaceView.holder.addCallback(object: SurfaceHolder.Callback {

            override fun surfaceCreated(holder: SurfaceHolder) {
                //在子线程进行绘制
                thread {
                    while(!destroyed) {
                        val canvas = holder.lockCanvas()
                        val random = Random()
                        val r = random.nextInt(255)
                        val g = random.nextInt(255)
                        val b = random.nextInt(255)
                        canvas.drawColor(Color.rgb(r, g, b))
                        holder.unlockCanvasAndPost(canvas)
                        SystemClock.sleep(500)
                    }
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {

            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {

            }
        })
    }
}