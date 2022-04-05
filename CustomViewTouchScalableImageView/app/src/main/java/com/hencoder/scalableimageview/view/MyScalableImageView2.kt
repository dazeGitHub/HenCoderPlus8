package com.hencoder.scalableimageview.view

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.OverScroller
import android.widget.Scroller
import androidx.core.animation.doOnEnd
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.ViewCompat
import com.hencoder.scalableimageview.dp
import com.hencoder.scalableimageview.getAvatar
import kotlin.math.max
import kotlin.math.min

private val IMAGE_SIZE = 300.dp.toInt()         //图片宽度
private const val EXTRA_SCALE_FACTOR = 1.5f     //额外缩放比, 为了使得外贴边的时候也能任意滑动

class MyScalableImageView2(context: Context?, attrs: AttributeSet?) : View(context, attrs) { //GestureDetector.OnGestureListener,

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG);
    private val bitmap = getAvatar(resources, IMAGE_SIZE)

    //这两个变量表示图像的横纵坐标的偏移量, 是动态的, 和 View 的尺寸有关的, 而 View 的尺寸是变的
    private var originalOffsetX = 0f
    private var originalOffsetY = 0f
    private var offsetX = 0f
    private var offsetY = 0f
    private var smallScale = 0f
    private var bigScale = 0f       //图片的最大缩放比率, 乘了额外缩放比
    private var big = false
    private var henGestureListener = HenGestureListener()
    private var henScaleGestureListener = HenScaleGestureListener()
    private var henFlingRunner = HenFlingRunner()

    //缩放比, 即动画完成度, 放大时 从 0 到 1, 缩小时从 1 到 0
    //ObjectAnimator.ofFloat() 的动画对 scaleFraction 进行了赋值
//    private var scaleFraction = 0f  //Fraction : 少部分, 一点
//        set(value){
//            field = value
//            Log.e("TAG", "scaleFraction = $value")
//            invalidate()
//        }

    private var currentScale = 0f
        set(value){
            field = value
            Log.e("TAG", "scaleFraction = $value")
            invalidate()
        }

    //scaleAnimator 的 propertyName 就是 scaleFraction, 所以 scaleAnimator 会不断的修改 scaleFraction 值,
    //而修改 scaleFraction 值就会触发它的 set() 方法, 从而不断的调用 invalidate() 触发 onDraw()
//  private val scaleAnimator: ObjectAnimator by lazy{
        //使用 scaleFraction : 属性值的起始值为 0, 结束值为 1. 因为想让动画正反都可以, 所以要将 起始值 和 结束值 都填上
        //ObjectAnimator.ofFloat(this, "scaleFraction", 0f, 1f)

        //因为 onDoubleTap() 的时候重新设置了 offsetX, 所以就不需要在之前动画结束的时候再将 offsetX 设置为 0 了
        //ObjectAnimator.ofFloat(this, "scaleFraction", 0f, 1f)
//            .apply{
//            doOnEnd {
//                Log.e("TAG", "MyScalableImageView2 scaleAnimator offsetX = 0f offsetY = 0f")
//                if(!big){
//                    offsetX = 0f
//                    offsetY = 0f
//                }
//            }
//      }
//  }

    //currentScale 的初始值已经不是 0 和 1 了
    //当在 onSizeChanged() 中 smallScale 和 bigScale 改变时还需要更新 scaleAnimator
    //因为 smallScale 和 bigScale 是会变的, 所以也不能用 by lazy{} 了, 因为 by lazy{} 只会在第一次用的使用才会执行 {} 代码块, 以后都不会执行
    private val scaleAnimator: ObjectAnimator = ObjectAnimator.ofFloat(this, "currentScale", smallScale, bigScale)

    //OverScroller 是一个计算器, 放到 onFling() 中可以帮助计算坐标
    //当使用 Scroller 时无论初始速度多快都没有效果, 但是使用 OverScroller 时初始速度越快那么滚动的初始速度也越快, 惯性滑动就用 OverScroller
    private val scroller = Scroller(context) //OverScroller

    //GestureDetector 随着 Android 系统的更新代码也会改变, GestrureDetectorCompat 不随着 Android 系统更新也有最新特性, 所以 GestrureDetectorCompat 对旧版本的 Android 有更好的支持 (手机不用更新系统, 只要 AndroidX 的包更新了, 就能用 AndroidX 的包的新东西)。
    //一般用 GestrureDetectorCompat, 因为它的兼容性比较好。
    private val gestureDetector = GestureDetectorCompat(context, henGestureListener)
//  private val gestureDetector = GestureDetectorCompat(context, henGestureListener).apply {
//      //为 gestureDetector 添加双击监听器, 才能让他支持双击监听。GestureDetectorCompat 的 OnGestureListener 的方法中只有 onDown() 方法用上了, 别的方法都可以不要
//      setOnDoubleTapListener(henGestureListener)
//  }

    //注意 : ScaleGestureDetectorCompat 不是 ScaleGestureDetector 的兼容版本, GestureDetectorCompat 有基础功能也有更多内容, 而 ScaleGestureDetector 是只有更多内容
    //ScaleGestureDetectorCompat 是兼容性的辅助工具类, 是对 ScaleGestureDetector 兼容性辅助的, 是为 ScaleGestureDetector 添加一些额外内容
    //所以不能使用 ScaleGestureDetectorCompat 而应当使用 ScaleGestureDetector
    private val scaleGestureDetector = ScaleGestureDetector(context, henScaleGestureListener)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        originalOffsetX = (width - IMAGE_SIZE) / 2f
        originalOffsetY = (height - IMAGE_SIZE) / 2f

        if ((bitmap.width / bitmap.height.toFloat()) > (width / height.toFloat())) { //图片比较宽
            smallScale = width / bitmap.width.toFloat()
            bigScale = height / bitmap.height.toFloat() * EXTRA_SCALE_FACTOR
        } else {
            smallScale = height / bitmap.height.toFloat()
            bigScale = width / bitmap.width.toFloat() * EXTRA_SCALE_FACTOR
        }

        //gestureDetector 默认是支持长按的, 这里可以关闭长按
        //gestureDetector.setIsLongpressEnabled(false)

        currentScale = smallScale

        //
        scaleAnimator.setFloatValues(smallScale, bigScale)
    }

    //正着想不容易想通, 所以倒着想, 从底下往上看, 画完图 -> 缩放 -> 移动
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //scaleFraction 由全局计算改为通过 currentScale 局部计算
        val scaleFraction = (currentScale - smallScale) / (bigScale - smallScale)   //Fraction : 小部分, 少量
        Log.e("TAG", "onDraw offsetX = $offsetX  offsetY = $offsetY  scaleFraction = $scaleFraction  (offsetX * scaleFraction) = ${offsetX * scaleFraction}")
        canvas?.translate(offsetX * scaleFraction, offsetY * scaleFraction) //scaleFraction  是为了解决 : 双击时可以移动, 再双击缩小时图片不在界面中心 的问题

        //这里的 scale 是在算当前的缩放比, 所以有了 currentScale 代替 scale 那么就不需要再用 scaleFraction 来计算 scale 了
        //所以有 scaleFraction = (scale - smallScale) / (bigScale - smallScale) = (currentScale - smallScale) / (bigScale - smallScale)
        //
        //val scale = if(big) bigScale else smallScale                        //bigScale
        //val scale = smallScale + (bigScale - smallScale) * scaleFraction    //初始值 + 差值 * 百分比
        //canvas?.scale(scale, scale, width / 2f, height / 2f)        //以 view 的中心 (width / 2f, height / 2f) 做为轴心, 缩放后坐标系也缩放了
        canvas?.scale(currentScale, currentScale, width / 2f, height / 2f)

        canvas?.drawBitmap(bitmap, originalOffsetX, originalOffsetY, paint)
    }

    //提示 : Custom view MyScalableImageView2 overrides onTouchEvent but not performClick, 即当前 View 不支持点击, 但是要的效果就是不支持点击, 只支持双击
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        scaleGestureDetector.onTouchEvent(event)
        //如果 scaleGestureDetector 在捏手指, 那么就不能调用 gestureDetector
        if(!scaleGestureDetector.isInProgress){
            gestureDetector.onTouchEvent(event)
        }
        return true
    }

    //将修正 offsetX 和 offsetY 的操作抽取出来, 做为 fixOffset()
    private fun fixOffset(){
        //因为只有在 big 外贴边的时候图片缩放比才是 bigScale
        //offsetX 最大不能大于 (bitmap.width * bigScale - width) / 2, 所以选择 offsetX 和 它的最小的值
        offsetX = min(offsetX, (bitmap.width * bigScale - width) / 2)
        //最小不能小于 (bitmap.width * bigScale - width) / 2 的相反数, 所以选择 offsetX 和 它的最的大值
        offsetX = max(offsetX, -(bitmap.width * bigScale - width) / 2)

        offsetY = min(offsetY, (bitmap.height * bigScale - height) / 2)
        offsetY = max(offsetY, -(bitmap.height * bigScale - height) / 2)
    }

    //GestureDetector.SimpleOnGestureListener 实现了 OnGestureListener 和 OnDoubleTapListener
    inner class HenGestureListener : GestureDetector.SimpleOnGestureListener() { //不再使用 GestureDetector.OnGestureListener, 而是使用抽象类 GestureDetector.SimpleOnGestureListener

        //ACTION_DOWN 发生的时候 onDown() 就会触发, 这里返回什么那么 ACTION_DOWN 的时候就返回什么, 表示事件要不要消费
        //这里返回 true 那么上边的 onTouchEvent() 的 gestureDetector.onTouchEvent(event) 在 ACTION_DOWN 的时候才返回 true, 那么要整个事件序列,
        override fun onDown(e: MotionEvent?): Boolean {
            //因为只在 ACTION_DOWN 时返回 true 才有效, 所以下面两种情况写法都行
            //return true
            return e?.actionMasked == MotionEvent.ACTION_DOWN
        }

        //当手指发生移动 (ACTION_MOVE) 的时候这个方法就会被触发, 可以理解为 onMove()
        // downEvent : 在 onDown() 的时候如果返回 true, 那么这个 event 会被记录下来, 在 onScroll() 的时候会被传给 downEvent
        // currentEvent : 当前的事件, 即导致 onScroll() 方法被调用的事件
        // distanceX : 这次 ACTION_MOVE 事件的点 和 上次 ACTION_MOVE 事件的点之间的 X 轴的距离, distanceX = 旧位置的 x 坐标 - 新位置的 x 坐标
        // distanceY : 这次 ACTION_MOVE 事件的点 和 上次 ACTION_MOVE 事件的点之间的 Y 轴的距离, distanceY = 旧位置的 y 坐标 - 新位置的 y 坐标
        // 该方法的返回值没用
        override fun onScroll(
            downEvent: MotionEvent?,
            currentEvent: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            if(big){
                offsetX -= distanceX    //offsetX = offsetX - distanceX = offsetX + (-distanceX)
                offsetY -= distanceY    //需要在 offsetY 修改完再使用 min() 和 max() 防止越界
                fixOffset()
                Log.d("TAG", "onScroll offsetX = $offsetX offsetY = $offsetY")
                invalidate()            //移动的时候要刷新界面, 所以调用  invalidate()
            }
            return false
        }

        //onScroll() 和 onFling() 是一体的, 当滑动比较快然后松手的时候那么 onFling() 就会触发, 松手的时候列表还在继续滑, 这个行为就叫做 Fling, Fling 也可以叫做惯性滑动
        //downEvent : 同 onScroll() 的 downEvent
        //currentEvent : 同 onScroll() 的 currentEvent
        //velocityX : 横向的速率, 即单位时间内在 x 轴的位移, 是矢量
        //velocityY : 纵向的速率
        override fun onFling(
            downEvent: MotionEvent?,
            currentEvent: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            //在 onFling() 方法中使用 OverScroller 从而实现惯性滑动
            //这里也要加 big 判断, 否则在内贴边的时候快速滑动然后松手就会触发 onFling() 方法造成滑动
            if(big){
                //velocityX : 横向的速度, 抬起手的时候手滑动有多快
                //overX : 微信中滑动到底再撒手不是不能滑动了, 而是仍然可以用很慢的速度滑动, 可以惯性滑动过头, 惯性滑动过头后可以复位回来, overX 就是最多可以惯性滑动过头多少距离, OverScroller 提供的就是这个功能
                scroller.fling(
                    offsetX.toInt(), offsetY.toInt(), velocityX.toInt(), velocityY.toInt(),
                    (-(bitmap.width * bigScale - width) / 2).toInt(),
                    ((bitmap.width * bigScale - width) / 2).toInt(),
                    (-(bitmap.height * bigScale - height) / 2).toInt(),
                    ((bitmap.height * bigScale - height) / 2).toInt(),
                    //40.dp.toInt(), 40.dp.toInt()
                )
                //方式一 : 粗暴的使用循环调用 refresh()
//            for(i in 10..100 step 10){ //从 10 到 100 每过 10ms 就刷新一次
//                postDelayed({refresh()}, i.toLong())
//            }
                //方式二 :
                //在下一帧调用 postOnAnimation(), 但是这样写会一直创建 runnable 对象, 可以单独写一个 runnable 对象, 但是比较麻烦,
                //postOnAnimation { refresh() }

                //可以让 MyScalableImageView2 自己实现 Runnable, 然后将 refresh() 改为 run() 即可
                //postOnAnimation(this)

                //post()

                ViewCompat.postOnAnimation(this@MyScalableImageView2, henFlingRunner) //postOnAnimation(view, runnable)  其中 view 表示对哪个 view 做 animation
                Log.e("TAG", "onFling()  postOnAnimation { refresh() }")
            }
            return false
        }

        //OnDoubleTapListener 接口中的方法
        //当双击的时候触发 onDoubleTap() 方法, onDoubleTap() 收到 ACTION_DOWN 事件
        //双击 : 按下抬起再按下就会触发, 第一次按下与第二次按下有很短的时间间隔 300ms,
        //还会防手抖 : 如果两次点击间隔时间太短 (40ms), 那么也不算
        //返回值也没用
        override fun onDoubleTap(e: MotionEvent): Boolean {
            big = !big //使用动画后 big 还是有用的, 用来标记正着播放动画还是反着播放动画
            if(big){
                offsetX = -(e.x - width / 2f) * ((bigScale / smallScale) - 1)
                offsetY = -(e.y - height / 2f) * ((bigScale / smallScale) - 1)
                Log.e("TAG", "onDoubleTap offsetX = $offsetX offsetY = $offsetY")
                fixOffset()
                scaleAnimator.start()
            }else{
                //这里直接设置为 0 会导致突兀的复位到屏幕中心
//            offsetX = 0f
//            offsetY = 0f
                scaleAnimator.reverse()
            }
            //invalidate() //触发 onDraw(), 使用动画后就不用在这里 invalidate() 了
            return true
        }

        //现在的 Google 地图是双指上下滑动可以调整三维角度, 以前是双击后手指不要抬起来, 按住屏幕不动下滑可以调整三维角度
        //双击后手指不抬起来的后续事件就会回调到 onDoubleTapEvent() 方法, onDoubleTap() 收到 ACTION_DOWN 事件, 而 onDoubleTapEvent() 收到 ACTION_MOVE ACTION_UP ACTION_CANCEL 事件
        //所以处理双击用 onDoubleTap(), 但是处理双击的后续事件用 onDoubleTapEvent()
//        override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
//            return false
//        }

        //onSingleTapConfirmed() : 确保就是单独的按下操作, 而不是双击中的第一次按下, 防止误判双击的中的第一次按下也是按下操作
        //前面的 onSingleTapUp() 是当单次点击抬起的时候触发, 必须是长按间隔以内才会触发, onSingleTapConfirmed() 要比 onSingleTapUp() 判断更严一点, 不是双击才会触发。
        //双击事件中的首次单击就不应当再按单击处理了, 所以双击中不能再用 onSingleTapUp() 方法, 不够准确, 而使用 onSingleTapConfirmed() 判断单击更加准确
        //所以 onSingleTapConfirmed() 的作用 : 双击的时候的单击判断除了判断是不是长按外, 还应当确保不是双击
        //按下抬起之后有没有又按下, 按下抬起后会等 300ms, 如果 300ms 内有第二次按下, 就认为是双击, 那么 onDoubleTap() 方法会触发,
        //如果 300ms 内有第二次没有按下, 那么 300ms 整的时候就会触发 onSingleTapConfirmed() 方法
        //
        //如果不支持双击, 那么用这个方法就不够准确了, 用 onSingleTapUp() 更准确, 因为不支持双击那么按下再抬起就是一个单击事件, 但是这个 onSingleTapConfirmed() 却需要等 300ms, 在时间上不够及时
//        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
//            return false
//        }

        //如果摸了 View 并且超过 100ms, 那么就表示按下, 不管是不是在一个滑动控件里, 这个方法是 按下 和 预按下 结合的方法。
//        override fun onShowPress(e: MotionEvent?) {
//
//        }

        //当单次点击抬起的时候触发, 即一次点击抬起的时候, 因为在 onTouchEvent() 里把 onClick() 给吞掉了, 没执行 performClick() 无法支持点击, 所以可以在这写点击监听里的代码
        //该方法必须是在长按的间隔以内才会触发, gestureDetector 也可以设置长按监听, 如果是在 长按的间隔 500ms 外抬起那么这个方法不会触发
        //这个方法的返回值表示是否消费了这个点击事件, 这个是给系统做记录的, 返回 true 或 false 都没有影响, 真正的事件是否消费是看 onDown() 的返回值而不是看这里
//        override fun onSingleTapUp(e: MotionEvent?): Boolean {
//            return true
//        }

        //onTouchEvent() 里同样把 onLongClick 也吞掉了, 所以在这里有 onLongPress() 这个长按监听方法
//        override fun onLongPress(e: MotionEvent?) {
//
//        }
    }

    //onFling() 时 ViewCompat.postOnAnimation() 使用这个 runnable
    inner class HenFlingRunner : Runnable{
        override fun run() {
            //在需要计算和刷新的时候计算一下当前的值, 类似掐一下表, 计算下模型中小球的当前的位置
            //scroller.computeScrollOffset() 是有返回值的, 表示动画是否在进行中
            if(scroller.computeScrollOffset()){
                //因为 scroller 的起始点就是选的 (offsetX, offsetY), 所以移动到哪新的坐标就仍然是 (offsetX, offsetY), 不需要任何处理
                offsetX = scroller.currX.toFloat()
                offsetY = scroller.currY.toFloat()
                invalidate()
                Log.e("TAG", "run() offsetX = $offsetX offsetY = $offsetY")
                ViewCompat.postOnAnimation(this@MyScalableImageView2, this) //但是只调用一次 postOnAnimation() 不够, 为了能持续动画, 这里还需要再调用一次, 为了防止一直递归调用, 就需要根据 scroller.computeScrollOffset() 的返回值判断
            }
        }
    }

    inner class HenScaleGestureListener : ScaleGestureDetector.OnScaleGestureListener{

        //手指同时靠近开始时回调这个方法, 这里必须返回 true, 类似于 onDown(), 否则没效果
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            offsetX = -(detector.focusX - width / 2f) * ((bigScale / smallScale) - 1)
            offsetY = -(detector.focusY - height / 2f) * ((bigScale / smallScale) - 1)
            return true
        }

        //手指松开的时候调用这个方法
        override fun onScaleEnd(detector: ScaleGestureDetector?) {

        }

        override fun onScale(detector: ScaleGestureDetector): Boolean { //这个 detector 就是上边创建的 scaleGestureDetector

            //为了解决 手指从无法缩小时再放大 (还没到之前到达 smallScale 边界的位置) 时图片就开始放大 的问题
            val tempCurrentScale = currentScale * detector.scaleFactor
            if(tempCurrentScale < smallScale || tempCurrentScale > bigScale){
                //返回 false 表示不消费事件, 上一次的值会一直保存着
                return false
            }else{
                currentScale *= detector.scaleFactor
                return true
            }

            //detector.scaleFactor 获取到实时的放缩系数 :
            //例如两个手指同时往相反方向移动, 每个移动距离为初始时两根手指距离的一半, 那么移动完毕后两个手指的距离 = 初始时两个手指的距离 * 2, 此时放缩系数为 2
            //即 放缩系数 = 移动完的双指的距离 / 移动开始时双指的距离
            //当两个手指缩小到很小那么 detector.scaleFactor 也可能是 0, 也可能无穷大, 即范围是 0-无穷
            //现在要做的是放缩, 所以要修改 scaleFraction (动画完成度), scaleFraction 的取值范围 0-1, 这两个区间不同, 比较难映射
            //但是之前有 : smallScale < 图片放缩系数 < bigScale, 如果不考虑各种限制, 那么图片放缩系数也可以是 0-无穷
            //scaleFraction = detector.scaleFactor

            //即不要写 scaleFraction, 而是换一个值 currentScale
            //当前值 = 上一个状态的值 * (当前状态/上一状态的缩放系数)
            //detector.scaleFactor 可以获取到 [当前状态/初始状态的缩放系数] 和 [当前状态/上一状态的缩放系数], 关键看返回值
            currentScale *= detector.scaleFactor
            Log.e("TAG", "onScale currentScale Pre currentScale = $currentScale  detector.scaleFactor = ${detector.scaleFactor}" )

            //防止缩放越界
            //currentScale = max(smallScale, currentScale)
            //currentScale = min(bigScale, currentScale)
            //上述写法也可以这么写
            currentScale = currentScale.coerceAtLeast(smallScale).coerceAtMost(bigScale) //coerce : 胁迫
            Log.e("TAG", "onScale coerceAt Back currentScale = $currentScale  smallScale = $smallScale bigScale = $bigScale")

            //如果这里返回 false, 表示不消费, 下一次 onScale() 这里 detector.scaleFactor 返回的就是 [当前状态/初始状态的缩放系数]
            //如果这里返回 true,  表示消费,   下一次 onScale() 这里 detector.scaleFactor 返回的就是 [当前状态/上一状态的缩放系数]
            //所以这里返回 true, 这里的返回值不和 onFling() 和 onScroll() 一样没有意义, 这个是有意义的
            return true
        }
    }
}