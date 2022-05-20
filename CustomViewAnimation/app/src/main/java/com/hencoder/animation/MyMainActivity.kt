package com.hencoder.animation

import android.animation.*
import android.graphics.PointF
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.minus
import androidx.core.graphics.plus
import com.hencoder.animation.view.MyProvinceEvaluator
import kotlinx.android.synthetic.main.activity_my_main.*


class MyMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_main)

        //imageViewAnim()
        circleImageViewAnim()
        //cameraViewAnimSet()
        //cameraViewValuesHolder()
        //cameraViewKeyFrame()
        //cameraViewInterpolator()
        //pointViewTypeEvaluator()
        //provinceViewAnim()
        //hardwareLayerViewAnim()
    }

    private fun imageViewAnim(){
        iv_view.animate()
            .translationX(200.dp)
            .translationY(200.dp)
            .alpha(0.5f)
            .scaleX(2f)
            .scaleY(2f)
            .setStartDelay(1000) //加一个延迟时间
    }

    private fun circleImageViewAnim(){
        //因为 radius 是 float, 所以使用 ObjectAnimator.ofFloat() 创造一个浮点数
        //ObjectAnimator.ofFloat(animView, propertyName, targetPropertyValue)
        //  animView : 要执行属性动画的 View
        //  propertyName : 要改变的属性名, java 需要写这个属性名的 set() 方法, kotlin 的属性自带 set() 和 get() 方法
        //  targetPropertyValue : 经过的属性值, 可以只填一个 (即最终的目标值)
        val animator = ObjectAnimator.ofFloat(circle_view, "radius", 150.dp)
        animator.startDelay = 1000 //启动延迟
        animator.start()
    }

    private fun cameraViewAnimSet(){

        val bottomFlipAnimator = ObjectAnimator.ofFloat(camera_view, "bottomFlipCameraRotateDegree", 60f)
        bottomFlipAnimator.startDelay = 1000
        bottomFlipAnimator.duration = 1000 //1500
        //bottomFlipAnimator.start()

        //需要为 bottomFlipCameraRotateDegree 设置初始值 30f, 否则不知道折痕在哪
        val flipRotationAnimator = ObjectAnimator.ofFloat(camera_view, "flipRotation", 270f)
        flipRotationAnimator.startDelay = 200 //1000
        flipRotationAnimator.duration = 1000
        //flipRotationAnimator.start()

        val topFlipAnimator = ObjectAnimator.ofFloat(camera_view, "topFlipCameraRotationDegree", -60f)
        topFlipAnimator.startDelay = 200 //1000
        topFlipAnimator.duration = 1000
        //topFlipAnimator.start()

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(bottomFlipAnimator, flipRotationAnimator, topFlipAnimator)
        animatorSet.start()
    }

    private fun cameraViewValuesHolder(){
        //三个属性一起发生动画
        val bottomFlipHolder = PropertyValuesHolder.ofFloat("bottomFlipCameraRotateDegree", 60f)
        val flipRotationHolder = PropertyValuesHolder.ofFloat("flipRotation", 270f)
        val topFlipHolder = PropertyValuesHolder.ofFloat("topFlipCameraRotationDegree", -60f)
        val holderAnimator = ObjectAnimator.ofPropertyValuesHolder(camera_view, bottomFlipHolder, flipRotationHolder, topFlipHolder)
        holderAnimator.startDelay = 1000
        holderAnimator.duration = 2000
        holderAnimator.start()
    }

    //希望水平移动时又阻力的, 先快速移动再有阻力再快速移动,
    // 即开头和结尾时间很少但是走过的距离却很远, 中间时间过了很久但是只走了一小段距离
    private fun cameraViewKeyFrame(){
        //Keyframe.ofFloat(fraction, value)
        //fraction : 进行了多少, 是一个比率
        //value : 到达 fraction 这个比率时动画实际的值是多少
        val length = 200.dp     //总的移动距离
        val keyframe1 = Keyframe.ofFloat(0f, 0f)
        val keyframe2 = Keyframe.ofFloat(0.2f, 1.5f * length) //0.4f
        val keyframe3 = Keyframe.ofFloat(0.8f, 0.6f * length)
        val keyframe4 = Keyframe.ofFloat(1f, 1f * length)
        //将 keyframe 添加到动画里
        val keyframeHolder = PropertyValuesHolder.ofKeyframe("translationX", keyframe1, keyframe2, keyframe3, keyframe4)
        val objectAnimator = ObjectAnimator.ofPropertyValuesHolder(camera_view, keyframeHolder)
        objectAnimator.startDelay = 1000
        objectAnimator.duration = 2000
        objectAnimator.start()
    }

    private fun cameraViewInterpolator(){
        //安卓默认提供了 4 种插值器
        //  1. AccelerateDecelerateInterpolator : 加速减速插值器, 适用于内部动画而不是入场出场动画
        //  2. AccelerateInterpolator : 速度越来越快, 到最后突然停止, 适用于出场动画
        //  例如一个 view 飞出屏幕, 飞出屏幕再停止用户无法感觉到, 只能感觉到飞出了屏幕, 用户不关心 view 的终点只关心 view 的起点
        //  3. DecelerateInterpolator : 速度越来越慢, 到最后突然停止, 适用于入场动画
        //  例如一个 view 飞入屏幕, 用户不关心 view 的起点只关心 view 的终点
        //  4. LinearInterpolator : 匀速插值器, 使用的场景反而比较少
        val animator = ObjectAnimator.ofFloat(circle_view, "radius", 150.dp)
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.startDelay = 1000 //启动延迟
        animator.duration = 2000
        animator.start()
    }

    private fun pointViewTypeEvaluator(){
        val animator = ObjectAnimator.ofObject(
                            point_view,
                "point",
                            PointFEvaluator(),
                            PointF(100.dp, 200.dp)
        )
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.startDelay = 1000 //启动延迟
        animator.duration = 2000
        animator.start()
    }

    private fun provinceViewAnim(){
        val animator = ObjectAnimator.ofObject(province_view, "province", MyProvinceEvaluator(), "澳门特别行政区")
        animator.startDelay = 1000
        animator.duration = 10 * 1000
        animator.start()
     }

    private fun hardwareLayerViewAnim(){
        hardware_layer_view
            .animate()
            .translationY(200.dp)
            .withLayer()
    }

    class PointFEvaluator : TypeEvaluator<PointF> {
        override fun evaluate(fraction: Float, startValue: PointF, endValue: PointF): PointF {
            //FloatEvaluator 代码示例 :
            //val startFloat: Float = startValue.floatValue()
            //初始值 + 当前的动画完成度 (fraction) * (结束值 - 初始值)
            //return startFloat + fraction * (endValue.floatValue() - startFloat)

            val startX = startValue.x
            val endX = endValue.x
            val currentX = startX + (endX - startX) * fraction

            val startY = startValue.y
            val endY = endValue.y
            val currentY = startY + (endY - startY) * fraction

            return PointF(currentX, currentY)

            //只能将 PointF 的 x 和 y 分开写, 而不能如下使用扩展函数 :
            //+ 运算符有 PointF 的扩展函数 PointF.plus(xy: Float)
            //- 运算符有 PointF 的扩展函数 PointF.minus(p: PointF)
            //但是 * 运算符 PointF 并没有扩展函数, (endValue - startValue) 的含义是点之间的距离, 而不是某个点
            //return startValue + (endValue - startValue) * fraction
        }
    }
}