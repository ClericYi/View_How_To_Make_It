package com.clericyi.views.chart

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.clericyi.views.BaseView
import com.clericyi.views.DensityUtil
import com.clericyi.views.model.LineBean
import kotlin.math.ceil
import kotlin.math.min

/**
 * author: ClericYi
 * time: 2020/3/28
 */
class LineChartView : BaseView, View.OnTouchListener {
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

    private var mLinePaint: Paint? = null

    // 数据组成成分：1。数据 2。数据的介绍
    private var mData: List<Float>? = null
    private var mDescription: List<String>? = null

    // 数据显示量
    private var mLineShowNum = 6
    private val mMinLineScrollShowNum = 5

    // 单个Line的宽度
    private var mLineSingleWidth = 0f
    private var mLineMaxHeight = 0f
    private var mLineMaxWidth = 0f

    private var mHeightBlankSize = 0f
    private var mTextHeight = 0f

    private val mTextBounds = Rect()

    private var mMaxData = 0f

    private var dataPoints: FloatArray? = null

    private var mPaintCapSize = 0f
    private var mPaintDefaultSize = 0f


    // 手势动作
    private var detector: GestureDetector? = null

    private var offset = 0f

    // 外部设定变量
    private var mColor: Int = Color.RED

    private fun init() {
        mPaintDefaultSize = DensityUtil.dp2px(context, 1f).toFloat()
        mPaintCapSize = DensityUtil.dp2px(context, 8f).toFloat()

        mData = ArrayList()
        mDescription = ArrayList()

        mLinePaint = Paint()
        mLinePaint?.style = Paint.Style.FILL
        mLinePaint?.color = Color.BLACK
        mLinePaint?.isAntiAlias = true
        mLinePaint?.textSize = DensityUtil.dp2px(context, 14f).toFloat()
        mLinePaint?.strokeWidth = mPaintDefaultSize
        mLinePaint?.strokeCap = Paint.Cap.ROUND

        detector = GestureDetector(context, LineGesture())

        setOnTouchListener(this)

        // 小数据防止重复初始化
        offset = DensityUtil.dp2px(context, 32f).toFloat()
        // 用于文字的测量
        val fontMetrics = mLinePaint?.fontMetricsInt
        mHeightBlankSize =
            (offset - fontMetrics!!.let { it.bottom - it.top }) / 2
        mTextHeight =
            (offset + fontMetrics.let { it.bottom - it.top }) / 2

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (mLineMaxWidth == 0f) {
            mLineMaxWidth = mWidth
            mLineSingleWidth = mLineMaxWidth / min(mLineShowNum, mMinLineScrollShowNum)
            mLineMaxHeight = mHeight - 2 * offset
        }
    }


    // Draw中要干什么事情？
    // 1。 线绘制
    // 2。点绘制
    // 3。 文字描述绘制
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mLinePaint?.color = Color.BLACK
        mLinePaint?.strokeWidth = mPaintDefaultSize
        for (i in mData!!.indices) {
            if (!checkIsNeedDraw(i)) {
                val height = (mMaxData - mData!![i]) / mMaxData * mLineMaxHeight
                drawLineValue(canvas, i, height)
                drawDescriptions(canvas, i)
                calPoints(height, i)
            }
            if (i != mData!!.size - 1) {
                canvas?.translate(mLineSingleWidth, 0f)
            }
        }
        canvas?.translate(-1 * mLineSingleWidth * (mData!!.size - 1), 0f)
        canvas?.let {
            mLinePaint?.let { paint ->
                dataPoints?.let { points ->
                    it.drawLines(points, paint)
                    paint.color = Color.RED
                    paint.strokeWidth = mPaintCapSize
                    it.drawPoints(points, paint)
                }
            }
        }
    }

    private fun calPoints(height: Float, index: Int) {
        dataPoints!![index * 4] = mLineSingleWidth * (index + 0.5f)
        dataPoints!![index * 4 + 1] = (mLineMaxHeight - height) * (1 - scale) + height + offset
        if (index != 0) {
            dataPoints!![index * 4 - 2] = dataPoints!![index * 4]
            dataPoints!![index * 4 - 1] = dataPoints!![index * 4 + 1]
        }
        if (index == mData!!.size - 1) {
            dataPoints!![index * 4 + 2] = dataPoints!![index * 4]
            dataPoints!![index * 4 + 3] = dataPoints!![index * 4 + 1]
        }
    }

    private fun checkIsNeedDraw(i: Int): Boolean {
        if (mLineSingleWidth * (i + 1) < scrollX) return true
        if (mLineSingleWidth * i > scrollX + mWidth) return true
        return false
    }

    private fun drawLineValue(canvas: Canvas?, index: Int, height: Float) {
        val value = mData?.get(index).toString()
        mLinePaint?.getTextBounds(
            value,
            0,
            value.length,
            mTextBounds
        )
        val blankSize = (mLineSingleWidth - (mTextBounds.right - mTextBounds.left)) / 2
        mLinePaint?.let { canvas?.drawText(value, blankSize, mTextHeight + height, it) }
    }

    private fun drawDescriptions(canvas: Canvas?, index: Int) {
        val description = mDescription?.get(index)
        mLinePaint?.getTextBounds(
            description,
            0,
            description?.length!!,
            mTextBounds
        )
        val blankSize = (mLineSingleWidth - (mTextBounds.right - mTextBounds.left)) / 2
        mLinePaint?.let {
            canvas?.drawText(
                description!!,
                blankSize,
                mTextHeight + mLineMaxHeight + offset,
                it
            )
        }
    }

    // 手势动作
    private inner class LineGesture : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            if (mLineShowNum <= mMinLineScrollShowNum) return false

            val position = DensityUtil.dp2px(context, scrollX.toFloat())
            if (distanceX >= 0) {
                if (position <= mLineMaxWidth) {
                    scrollBy(distanceX.toInt(), 0)
                }
            } else {
                if (distanceX >= -1 * position) {
                    scrollBy(distanceX.toInt(), 0)
                }
            }
            return false
        }
    }

    // 如果手势能响应，就交给手势
    // 不能则放弃
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return detector!!.onTouchEvent(event)
    }

    private fun setData(
        data: List<Float>?,
        descriptions: List<String>?
    ) {
        if (data != null && data.isNotEmpty()
            && descriptions != null && descriptions.isNotEmpty()
        ) {
            mData = data
            mDescription = descriptions
            animator?.start()
        }
    }

    fun setData(lineBeanList: List<LineBean>) {
        val data: MutableList<Float> = ArrayList()
        val descriptions: MutableList<String> = ArrayList()
        for (i in lineBeanList.indices) {
            lineBeanList[i].getValue()?.let {
                data.add(it)
                if (mMaxData < it) {
                    mMaxData = it
                }
            }
            lineBeanList[i].getDescription()?.let { descriptions.add(it) }
        }
        mLineShowNum = lineBeanList.size
        fixMaxData()
        setData(data, descriptions)
        dataPoints = FloatArray(mData!!.size * 4)
    }

    // 将最大值修正
    private fun fixMaxData() {
        mMaxData = ceil(mMaxData / 40) * 40
    }

    // 自定义颜色
    fun setColor(color: Int) {
        mColor = color
    }
}