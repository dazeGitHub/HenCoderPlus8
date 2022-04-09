package com.hencoder.drag.view

import android.content.ClipData
import android.content.Context
import android.util.AttributeSet
import android.view.DragEvent
import android.view.View
import android.view.View.OnLongClickListener
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import kotlinx.android.synthetic.main.drag_to_collect.view.*

/**
 * 使用 OnDragListener 实现拖拽
 * 拖动图片 或 logo 会在底部显示对应的文本描述, 拖拽的图像是半透明的
 */
class DragToCollectLayout(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {

    private var dragListener: OnDragListener = CollectListener()

    //使用 OnDragListener 是全局的, 是跨进程的, 所以能将图像拖动到 状态栏 和 导航栏 上
    private var dragStarterLongClickListener = OnLongClickListener { longClickView ->
        //长按的 View 在 xml 中都设置了 android:contentDescription 属性, 这里可以直接使用 v.contentDescription 获取设置好的属性值
        //android:contentDescription 属性 是给无障碍人士用的, 虽然看不到图片, 但是可以听到单词
        val imageClipData : ClipData = ClipData.newPlainText("name", longClickView.contentDescription)
        //兼容 view.startDragAndDrop() 和 view.startDrag() 写法
        //DragShadowBuilder 表示拖动时那个半透明的物体的样子, 可以自定义, 默认是 view 长什么样子拖动时的 view 就是什么样子
        //该半透明的物体并不是一个 view, 而是一片像素, 被额外绘制到界面的最上边
        ViewCompat.startDragAndDrop(longClickView, imageClipData, DragShadowBuilder(longClickView), null, 0)
        //longClickView.startDrag(null, DragShadowBuilder(longClickView), longClickView, 0)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        avatarView.setOnLongClickListener(dragStarterLongClickListener)
        logoView.setOnLongClickListener(dragStarterLongClickListener)
        //collectorLayout 是底部的 LinearLayout, 所以只为它注册这个 dragListener 监听器
        collectorLayout.setOnDragListener(dragListener)
    }

    inner class CollectListener : OnDragListener {
        override fun onDrag(v: View, event: DragEvent): Boolean {
            when (event.action) {
                //只需要落下事件, 这个 v 是底部的 LinearLayout
                DragEvent.ACTION_DROP -> if (v is LinearLayout) {
                    val textView = TextView(context)
                    textView.textSize = 16f
                    //event.clipData 就是上边 ViewCompat.startDragAndDrop(view, clipData, shadowBuilder, localState, flags) 里的 clipData
                    //而 ViewCompat.startDragAndDrop(view, clipData, shadowBuilder, localState, flags) 内部调用了 v.startDrag(data, shadowBuilder, localState, flags)
                    //之前说的 v.startDrag(data, shadowBuilder, localState, flags); 的 localState 是本地数据, data 只能在 Drag 或 Drop 的时候才能拿到,
                    //这里正好是 ACTION_DROP 所以能拿到 data, data 和 localState 都是数据, 区别就是 data 能跨进程, 也就是能跨进程拖拽东西,
                    //例1 : 将图片从照片软件中拖动到微信里边, 如果软件支持分屏显示可以试试, 微信是支持的, 只看照片软件是否支持即可。例2 : 还可以将文件拖动到垃圾桶。
                    //这是就是为什么松手的时候才能拿到 data, 因为数据比较重, 而且还需要用 ClipData 类包装一下, 这样不支持的格式也会支持
                    if(event.clipData.itemCount != 0){
                        textView.text = event.clipData.getItemAt(0).text
                    }
                    v.addView(textView)
                }
            }
            return true
        }
    }
}
