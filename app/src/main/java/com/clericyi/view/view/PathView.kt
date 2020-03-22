package com.clericyi.view.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
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
    private var paintLine: Paint? = null
    private var paintMoNi: Paint? = null

    private var path: Path? = null
    private var pathLine1: Path? = null
    private var pathLine2: Path? = null

    private var pathMeasure: PathMeasure? = null
    private var pathLength: Float? = null
    private var pathBezierLength1: Float? = null
    private var pathBezierLength2: Float? = null

    private var mPos: FloatArray? = null
    private var mTan: FloatArray? = null

    private var mDistance: Float? = null
    private var mStep: Float? = null

    private val INVALIDATE_TIMES = 100 // 执行次数

    private var mBezierPathMeasure1: PathMeasure? = null
    private var mBezierPathMeasure2: PathMeasure? = null

    private var mBezierPos1: FloatArray? = null
    private var mBezierTan1: FloatArray? = null
    private var mBezierPos2: FloatArray? = null
    private var mBezierTan2: FloatArray? = null

    private var mBezierStep1: Float? = null
    private var mBezierStep2: Float? = null

    private var mBezierDistance1: Float? = null
    private var mBezierDistance2: Float? = null


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
        paint?.strokeWidth = 15f
        paint?.isAntiAlias = true
        paintPoint?.strokeCap = Paint.Cap.ROUND

        paintPoint = Paint()
        paintPoint?.style = Paint.Style.STROKE
        paintPoint?.color = Color.GREEN
        paintPoint?.isAntiAlias = true
        paintPoint?.strokeWidth = 15f
        paintPoint?.strokeCap = Paint.Cap.ROUND

        paintLine = Paint()
        paintLine?.style = Paint.Style.STROKE
        paintLine?.color = Color.GRAY
        paintLine?.strokeWidth = 15f
        paintLine?.isAntiAlias = true
        paintPoint?.strokeCap = Paint.Cap.ROUND

        paintMoNi = Paint()
        paintMoNi?.style = Paint.Style.STROKE
        paintMoNi?.color = Color.WHITE
        paintMoNi?.isAntiAlias = true
        paintMoNi?.strokeWidth = 10f
        paintMoNi?.strokeCap = Paint.Cap.ROUND


        // 其实就是你要绘制图像的路径
        // 既然是路径，那就应该是对复合图形的操作了，一般都是直线，曲线等等
        // 绘制中会有一个不一样的地方，要么顺时针（CW）、要么逆时针（CCW）
        path = Path()
//        path?.addCircle(150f, 150f, 90f, Path.Direction.CW)
        //        path?.addCircle(200f, 100f,100f, Path.Direction.CCW) // 需要测试的地方，使用两个方向相反的方案会出现什么样的结果
        path?.moveTo(20f, 20f)
        path?.quadTo(200f, 200f, 400f, 50f)

        pathLine1 = Path()
        pathLine1?.moveTo(20f, 20f)
        pathLine1?.lineTo(200f,200f)

        pathLine2 = Path()
        pathLine2?.moveTo(200f, 200f)
        pathLine2?.lineTo(400f,50f)

        pathMeasure = PathMeasure(path, false)
        pathMeasure?.length.let {
            pathLength = it
            mStep = it?.div(INVALIDATE_TIMES)
        }

        mBezierPathMeasure1 = PathMeasure(pathLine1, false)
        mBezierPathMeasure1?.length.let {
            pathBezierLength1 = it
            mBezierStep1 = it?.div(INVALIDATE_TIMES)
        }

        mBezierPathMeasure2 = PathMeasure(pathLine2, false)
        mBezierPathMeasure2?.length.let {
            pathBezierLength2 = it
            mBezierStep2 = it?.div(INVALIDATE_TIMES)
        }


        mDistance = 0f
        mBezierDistance1 = 0f
        mBezierDistance2 = 0f

        mPos = FloatArray(2)
        mTan = FloatArray(2)

        mBezierPos1 = FloatArray(2)
        mBezierTan1 = FloatArray(2)
        mBezierPos2 = FloatArray(2)
        mBezierTan2 = FloatArray(2)
    }

    /**
     * 为了对应Path的路径其实canvas已经给出了描绘 路径的方法，drawPath
     */
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawColor(Color.BLACK)


        // 动态展示一个物体的绘制轨迹
        pathDraw(canvas)
        // 贝塞尔曲线
        Bezier(canvas)
        invalidate()
    }

    private fun Bezier(canvas: Canvas?) {
        paintLine?.let {
            pathLine1?.let { line1 -> canvas?.drawPath(line1, it) }
            pathLine2?.let { line2 -> canvas?.drawPath(line2, it) }
        }


        if (pathBezierLength2!! >= mBezierDistance2!!) {
            mBezierDistance1?.let { mBezierPathMeasure1?.getPosTan(it, mBezierPos1, mBezierTan1) }
            mBezierDistance2?.let { mBezierPathMeasure2?.getPosTan(it, mBezierPos2, mBezierTan2) }
            mBezierDistance2 = mBezierStep2?.let { mBezierDistance2?.plus(it) }
            mBezierDistance1 = mBezierStep1?.let { mBezierDistance1?.plus(it) }
            canvas?.drawLine(mBezierPos1?.get(0)!!, mBezierPos1?.get(1)!!, mBezierPos2?.get(0)!!, mBezierPos2?.get(1)!!, paintMoNi!!)
            canvas?.drawPoint(mBezierPos1?.get(0)!!, mBezierPos1?.get(1)!!, paintPoint!!)
            canvas?.drawPoint(mBezierPos2?.get(0)!!, mBezierPos2?.get(1)!!, paintPoint!!)
        }else{
            canvas?.drawPoint(mBezierPos1?.get(0)!!, mBezierPos1?.get(1)!!, paintPoint!!)
            canvas?.drawPoint(mBezierPos2?.get(0)!!, mBezierPos2?.get(1)!!, paintPoint!!)
        }
    }

    private fun pathDraw(canvas: Canvas?) {
        path?.let { pathIt ->
            paint?.let { paintIt -> canvas?.drawPath(pathIt, paintIt) }
        }


        if (pathLength!! >= mDistance!!) {
            mDistance?.let { pathMeasure?.getPosTan(it, mPos, mTan) }
            mDistance = mStep?.let { mDistance?.plus(it) }
            canvas?.drawPoint(mPos?.get(0)!!, mPos?.get(1)!!, paintPoint!!)

        } else {
            // 如果不设置将会出现空白的问题
            canvas?.drawPoint(mPos?.get(0)!!, mPos?.get(1)!!, paintPoint!!)
        }
    }
}