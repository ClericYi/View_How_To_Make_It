package com.clericyi.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.clericyi.views.DensityUtil.dp2px
import java.util.*
import kotlin.math.floor

/**
 * author: ClericYi
 * time: 2020/3/23
 */
class PieChartView : BaseView {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    // 绘制圆的半径
    private var mRadius = 0f
    // 弧形的画笔
    private var mArcPaint: Paint? = null
    private var arcRect: RectF? = null

    // 三个数据：1。数据量 2。关于数据的描述 3。各个数据的颜色
    private var mRatios: List<Float>? = null
    private var mDescription: List<String>? = null
    private var mArcColors: List<Int>? = null

    private var mCircleWidth = 0f

    private fun init() {
        mRatios = ArrayList()
        mArcColors = ArrayList()
        mDescription = ArrayList()

        mArcPaint = Paint()
        mArcPaint?.style = Paint.Style.FILL
        mArcPaint?.isAntiAlias = true
        mArcPaint?.textSize = dp2px(context, 12f).toFloat()
        mArcPaint?.strokeWidth = dp2px(context, 2f).toFloat()

        arcRect = RectF()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mCircleWidth = if (measuredWidth > measuredHeight) {
            measuredHeight.toFloat()
        } else {
            measuredWidth.toFloat()
        }
        // 选用0.3的长度话，比较合适去绘制
        // 可以尝试修改，放大缩小，mRadius
        mRadius = mCircleWidth * 0.3f
        // 矩形的大小位置，正好覆盖一个圆
        arcRect?.set(
            measuredWidth / 2 - mRadius
            , measuredHeight / 2 - mRadius
            , measuredWidth / 2 + mRadius
            , measuredHeight / 2 + mRadius
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 两个工作部分：文字描述、弧形
        drawDescription(canvas)
        drawArc(canvas)
    }

    // 绘制弧形
    private fun drawArc(canvas: Canvas) {
        val drawArc = 360 * scale
        for (i in mRatios!!.indices) {
            mArcPaint?.color = mArcColors!![i]
            mArcPaint?.let {
                canvas.drawArc(
                    arcRect!!,
                    getRatioSum(i) * drawArc,
                    mRatios!![i] * drawArc,
                    true,
                    it
                )
            }
        }
    }

    // 描绘文字和指向的文字
    private fun drawDescription(canvas: Canvas) {
        for (i in mRatios!!.indices) {
            mArcPaint?.color = mArcColors!![i]
            // 每进行一次绘制，需要保存一次
            // 因为对整个布局进行了旋转
            canvas.save()
            canvas.translate((measuredWidth / 2).toFloat(), mCircleWidth / 2)
            canvas.rotate(getRatioHalfSumDegrees(i))
            // 线的位置计算，中心点已经转移到了正中心
            // 通过一个弧来进行旋转
            mArcPaint?.let {
                canvas.drawLine(
                    mRadius,
                    0f,
                    mRadius + dp2px(context, 32f).toFloat(),
                    0f,
                    it
                )
            }
            drawDictateLines(canvas, i)
            canvas.restore()
        }
    }

    private fun drawDictateLines(canvas: Canvas, i: Int) {
        canvas.save()
        canvas.translate(mRadius + dp2px(context, 32f), 0f)
        val ro = getRatioHalfSumDegrees(i)
        canvas.rotate(-ro)
        mArcPaint?.let {
            canvas.drawLine(0f, 0f, dp2px(context, 16f).toFloat(), 0f, it)
            it.color = mArcColors!![i]

            canvas.drawText(
                mDescription!![i],
                dp2px(context, 18f).toFloat(),
                -dp2px(context, 4f).toFloat(),
                it
            )
            canvas.drawText(
                floor(mRatios!![i] * scale * 100).toString() + "%",
                dp2px(context, 18f).toFloat(),
                dp2px(context, 8f).toFloat(),
                it
            )
        }

        canvas.restore()
    }

    private fun getRatioSum(j: Int): Float {
        var sum = 0f
        for (i in 0 until j) {
            sum += mRatios!![i]
        }
        return sum
    }

    private fun getRatioHalfSumDegrees(j: Int): Float {
        var sum = getRatioSum(j)
        sum += mRatios!![j] / 2
        return sum * 360
    }

    fun setData(
        data: MutableList<Float>?,
        colors: List<Int>?,
        descriptions: List<String>?
    ) {
        if (data != null && data.isNotEmpty() && colors != null && colors.isNotEmpty() && descriptions != null && descriptions.isNotEmpty()
        ) {
            mRatios = transformData(data)
            mDescription = descriptions
            mArcColors = colors
            animator?.start()
        }
    }

    // 数据修正，转换成百分比
    private fun transformData(data: MutableList<Float>): List<Float>? {
        var sum = 0f
        for (i in data.indices) {
            sum += data[i]
        }
        for (i in data.indices) {
            data[i] = data[i] / sum
        }
        return data
    }
}