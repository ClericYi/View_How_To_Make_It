package com.clericyi.view.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.atan2

/**
 * author: ClericYi
 * time: 2020/3/21
 * 对Path的一个理解和使用
 */
class PathView : View {
    private var paint: Paint? = null
    private var paintPoint: Paint? = null
    private var path: Path? = null

    private var pathMeasure: PathMeasure? = null
    private var pathLength: Float? = null

    private var mPos: FloatArray? = null
    private var mTan: FloatArray? = null

    private var mDistance: Float? = null
    private var mStep: Float? = null

    private val INVALIDATE_TIMES = 100 // 执行次数

    private var mDrawOne = true


    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private fun init() {
        paint = Paint()
        // 为了看出路径，自然的我们应该使用我们的一个线的描边来制作
        paint?.style = Paint.Style.STROKE
        paint?.color = Color.RED
        paint?.strokeWidth = 30f
        paint?.isAntiAlias = true

        paintPoint = Paint()
        paintPoint?.style = Paint.Style.STROKE
        paintPoint?.color = Color.GREEN
        paintPoint?.isAntiAlias = true
        paintPoint?.strokeWidth = 30f
        paintPoint?.strokeCap = Paint.Cap.ROUND


        // 其实就是你要绘制图像的路径
        // 既然是路径，那就应该是对复合图形的操作了，一般都是直线，曲线等等
        // 绘制中会有一个不一样的地方，要么顺时针（CW）、要么逆时针（CCW）
        path = Path()
        path?.addCircle(150f, 150f, 90f, Path.Direction.CW)
        //        path?.addCircle(200f, 100f,100f, Path.Direction.CCW) // 需要测试的地方，使用两个方向相反的方案会出现什么样的结果
        path?.close()

        pathMeasure = PathMeasure(path, false)
        pathMeasure?.length.let {
            pathLength = it
            mStep = it?.div(INVALIDATE_TIMES)
        }

        mDistance = 0f
        mPos = FloatArray(2)
        mTan = FloatArray(2)

    }

    /**
     * 为了对应Path的路径其实canvas已经给出了描绘 路径的方法，drawPath
     */
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawColor(Color.BLACK)
        path?.let { pathIt ->
            paint?.let { paintIt -> canvas?.drawPath(pathIt, paintIt) }
        }


        if (pathLength!! >= mDistance!!) {
            mDistance?.let { pathMeasure?.getPosTan(it, mPos, mTan) }
            mDistance = mStep?.let { mDistance?.plus(it) }
            canvas?.drawPoint(mPos?.get(0)!!, mPos?.get(1)!!,paintPoint!!)
            invalidate()
        }else{
            // 如果不设置将会出现空白的问题
            canvas?.drawPoint(mPos?.get(0)!!, mPos?.get(1)!!,paintPoint!!)
        }

        // 贝塞尔曲线

//        path?.quadTo(350f,20f, 400f,50f)


    }
}