package com.hencoder.multitouch.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.util.SparseArray
import android.view.MotionEvent
import android.view.View
import com.hencoder.multitouch.dp

/**
 * 多点触摸的第三种情况 : 各自为战型
 * 多指画图
 */
class MyMultiTouchView3(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var paths = SparseArray<Path>() //key 是 pointerId, value 是 Path

    init{
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 4.dp
        paint.strokeCap = Paint.Cap.ROUND   //线帽，即画的线条两端是否带有圆角
        paint.strokeJoin = Paint.Join.ROUND //path 的拐角处为圆角
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //paths 没有 iterator, 所以不能直接使用 for(path in paths){} 遍历, 否则提示 : For-loop range must have an 'iterator()' method
        for(index in 0 until paths.size()){
            //SparseArray 取值有两种取法
            //paths.get(key) 是通过 key 来取值的, 这里是需要根据 index 来取值, 所以不能用这个方法, 只能使用 paths.valueAt(index) 方法
            val path = paths.valueAt(index)
            canvas.drawPath(path, paint)
        }
    }

    //不需要找到哪个手指动了, 只需要在发生 ACTION_MOVE 的时候将每个手指的当前位置都更新到自己的 path 中即可
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.actionMasked){
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                //记录绘制起点
                val actionIndex = event.actionIndex //当 ACTION_DOWN 时 event.actionIndex 也是 0, 那么 event.getX(actionIndex) 就是 event.getX(0) = event.getX(), 正好巧合

                //不需要区分 ACTION_DOWN 和 ACTION_POINTER_DOWN, 只要有手指按下那么就是新的点, 就创建一个新的 path
                val path = Path()
                path.moveTo(event.getX(actionIndex), event.getY(actionIndex))

                //SparseArray 的 key 就是 pointerId, value 是 path
                val trackingPointerId = event.getPointerId(actionIndex)
                paths.append(trackingPointerId, path)
                Log.e("TAG", "ACTION_DOWN paths.size() = ${paths.size()}")

                //ACTION_DOWN 的时候刷新不刷新都可以, 但是好的习惯是改变了 path 就刷新界面
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                for(index in 0 until paths.size()){
                    //这个不准
//                    val pointerId = paths.keyAt(index)
//                    val pointerIndex = event.findPointerIndex(pointerId)
//                    val path = paths.get(pointerId)
//                    Log.d("TAG", "pointerId = $pointerId pointerIndex = $pointerIndex index = $index")
//                    path.lineTo(event.getX(pointerIndex), event.getY(pointerIndex))

                    //这个会崩溃
                    val pointerId = event.getPointerId(index)
                    val path = paths.get(pointerId)
                    path.lineTo(event.getX(index), event.getY(index))

                    Log.e("TAG", "ACTION_MOVE paths.size() = ${paths.size()} event.getPointerCount = ${event.pointerCount} index = $index")
                }
                invalidate()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                //抬起时将画面清空, 拿到 pointerId 才能从 paths 中删掉抬起手指的那个 path
                val actionIndex = event.actionIndex //MotionEvent.ACTION_UP 时  event.actionIndex 正好也是 0
                val pointerId = event.getPointerId(actionIndex)
                paths.remove(pointerId)
                Log.e("TAG", "ACTION_UP paths.size() = ${paths.size()}")
                //path.reset()
                invalidate()
            }
        }
        return true
    }
}