package com.hencoder.materialedittext.view

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatEditText
import com.hencoder.materialedittext.R
import com.hencoder.materialedittext.ext.dp

private val FLOATING_LABEL_TEXT_SIZE = 12.dp     //floating label 的文字尺寸
private val FLOATING_LABEL_TEXT_MARGIN = 8.dp    //floating label 到文本输入框之间的垂直距离
private val HORIZONTAL_OFFSET = 5.dp             //绘制的 floating label (顶部提示文字) 到左边的距离
private val VERTICAL_OFFSET = 23.dp              //绘制的 floating label (顶部提示文字) 到顶部的距离
private val EXTRA_VERTICAL_OFFSET = 16.dp        //文本从上到下动画的最大的偏移量

class MyMaterialEditText(context: Context, attrs: AttributeSet) : AppCompatEditText(context, attrs) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var floatingLabelShown = false  //顶部提示文字是否显示
    var floatingLabelFraction = 0f          //顶部提示文字动画完成度
        set(value){
            field = value
            Log.e("TAG", "floatingLabelFraction set() floatingLabelFraction = $field")
            invalidate()
        }
    private val animator by lazy {
        //当 顶部提示文字 上升 和 下降 用一个 ObjectAnimator 对象时, 起点就应该是 0f, 终点应该是 1f
        ObjectAnimator.ofFloat(this, "floatingLabelFraction", 0f, 1f)
    }

    //是否使用顶部提示文字    //true      这里需要改为 false, 那么 typeArray 赋值时 if(field != value) 才会是不等
    var useFloatingLabel = false
        set(value){
            if(field != value){             //防止同样的值会调用 set 方法多次, 导致 setPadding() 额外调用多次
                field = value
                if(field){
                    setPadding(paddingLeft, (paddingTop + FLOATING_LABEL_TEXT_SIZE + FLOATING_LABEL_TEXT_MARGIN).toInt(), paddingRight, paddingBottom)
                }else{
                    //这个 paddingTop 是当前的 paddingTop, 之前已经加过 FLOATING_LABEL_TEXT_SIZE 和 FLOATING_LABEL_TEXT_MARGIN 了, 所有这里用减号
                    setPadding(paddingLeft, (paddingTop - FLOATING_LABEL_TEXT_SIZE - FLOATING_LABEL_TEXT_MARGIN).toInt(), paddingRight, paddingBottom)
                }
            }
        }

    init {
        paint.textSize = FLOATING_LABEL_TEXT_SIZE

        for(index in 0 until attrs.attributeCount){
            //拿到 属性名 和 属性值
            println("Attrs: key: ${attrs.getAttributeName(index)}, value: ${attrs.getAttributeValue(index)}")
        }

        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.MaterialEditText)
        useFloatingLabel = typeArray.getBoolean(R.styleable.MaterialEditText_useFloatingLabel, true)
        typeArray.recycle()

        //因为前边为 useFloatingLabel 赋值了, 会直接调用 set 方法, 所有这里可以注释掉了
//        if(useFloatingLabel){
//            setPadding(paddingLeft, (paddingTop + FLOATING_LABEL_TEXT_SIZE + FLOATING_LABEL_TEXT_MARGIN).toInt(), paddingRight, paddingBottom)
//        }
    }

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        //如果顶部提示正在显示 并且文本输入框变为空, 那么停止显示顶部提示的动画
        if(floatingLabelShown && text.isNullOrEmpty()){
            Log.e("TAG", "onTextChanged if floatingLabelFraction = $floatingLabelFraction")
            floatingLabelShown = false //每次做状态切换的时候还需要修改 floatingLabelShown 的值

            //显示的时候动画目标值从 0 到 1, 消失时从 1 到 0, 这个是消失所以目标值是 0
            //如果只是指定了结束值而没有指定初始值, 那么就会通过 floatingLabelFraction 的 get() 方法获取初始值,
            //但是该字段又是私有的, 所以无法取到初始值, 就会用 0 做为初始值, 所以下边的从 0 到 0 没有效果, 但是从 0 到 1 是有效果的
            //解决方案是将 floatingLabelFraction 字段的 private 修饰去掉

            //val animator1 = ObjectAnimator.ofFloat(this, "floatingLabelFraction", 0f)
            //animator1.start()
            animator.reverse()

        //如果顶部提示没有正在显示 并且文本输入框为非空, 那么开始显示顶部提示的动画
        }else if (!floatingLabelShown && (!text.isNullOrEmpty())){
            Log.e("TAG", "onTextChanged else if floatingLabelFraction = $floatingLabelFraction")
            floatingLabelShown = true

            //val animator2 = ObjectAnimator.ofFloat(this, "floatingLabelFraction", 1f)
            //animator2.start()
            animator.start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //if(!text.isNullOrEmpty()){
            //当动画改变 floatingLabelFraction 的值的时候修改透明度
            paint.alpha = (floatingLabelFraction * 0xff).toInt().also {
                Log.e("TAG", "floatingLabelFraction = $floatingLabelFraction paint.alpha = $it")
            }
            //从下往上是显示, floatingLabelFraction 就越大, 而 currVerticalValue 是向下偏移量, 所以 currVerticalValue 和 floatingLabelFraction 是相反的关系
            val currVerticalValue = VERTICAL_OFFSET + EXTRA_VERTICAL_OFFSET * (1 - floatingLabelFraction)
            //x 和 y 是文字到左边的距离, 是固定值, 可以试
            canvas.drawText(hint.toString(), HORIZONTAL_OFFSET, currVerticalValue, paint)
        //}
    }
}