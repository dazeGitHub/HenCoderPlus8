package com.example.motionlayout

import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.transition.Scene
import androidx.transition.TransitionManager

class Go : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_go)

        bindData()
    }

    private var toggle = true

    override fun onClick(view: View) {
        val root = findViewById<ViewGroup>(R.id.root)
        val startScene = Scene.getSceneForLayout(root, R.layout.go_start, this)
        val endScene = Scene.getSceneForLayout(root, R.layout.go_end, this)
        if (toggle) {
            TransitionManager.go(endScene)
        } else {
            TransitionManager.go(startScene)
        }

        //切换场景后又绑定了数据 和 设置了点击事件, 如果注释掉 bindData() 那么只有首次点击封面图生效
        //因为 起始场景 和 结束场景 的封面图是两个不同的对象, 动画开始的时候 起始场景中的布局对象已经被移除了,
        //所以动画结束场景的布局对象还需要设置一次点击事件, 同时其他数据也再绑定一次, 这也是过渡动画不好用的地方
        bindData()
        toggle = !toggle
    }

    private fun bindData() {
        findViewById<ImageView>(R.id.image_film_cover).setOnClickListener(this)
        findViewById<RatingBar>(R.id.rating_film_rating).rating = 4.5f
        findViewById<TextView>(R.id.text_film_title).text = getString(R.string.film_title)
        findViewById<TextView>(R.id.text_film_description).text = getString(R.string.film_description)
    }
}
