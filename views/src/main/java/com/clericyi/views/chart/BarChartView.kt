package com.clericyi.views.chart

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.animation.AlphaAnimation
import com.clericyi.views.BaseView
import com.clericyi.views.DensityUtil.dp2px
import com.clericyi.views.model.BarBean
import kotlin.math.ceil


/**
 * author: ClericYi
 * time: 2020/3/25
 */
class BarChartView : BaseView, View.OnTouchListener {
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

    private var mBarPaint: Paint? = null

    // 数据组成成分：1。数据 2。数据的介绍
    private var mData: List<Float>? = null
    private var mDescription: List<String>? = null

    // 数据显示量
    private var mBarShowNum = 6
    private val mMinBarScrollShowNum = 5

    // 单个Bar的宽度
    private var mBarSingleWidth = 0f
    private var mBarMaxHeight = 0f
    private var mBarMaxWidth = 0f
    private var mBarBlankSize = 0f
    private val mBarCornerSize = 20f

    private var mHeightBlankSize = 0f
    private var mTextHeight = 0f

    private val mTextBounds = Rect()

    private var mMaxData = 0f

    // 渐变色
    private var shader: LinearGradient? = null

    // 手势动作
    private var detector: GestureDetector? = null

    private var offset = 0f

    // 外部设定变量
    private var mColor: Int = Color.RED

    private fun init() {
        mData = ArrayList()
        mDescription = ArrayList()

        mBarPaint = Paint()
        mBarPaint?.style = Paint.Style.FILL
        mBarPaint?.color = Color.BLACK
        mBarPaint?.isAntiAlias = true
        mBarPaint?.textSize = dp2px(context, 14f).toFloat()
        mBarPaint?.strokeWidth = dp2px(context, 1f).toFloat()

        detector = GestureDetector(context, BarGesture())
        detector?.setIsLongpressEnabled(true)

        setOnTouchListener(this)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        offset = dp2px(context, 32f).toFloat()
        mBarMaxWidth = mWidth
        mBarSingleWidth = mBarMaxWidth / mBarShowNum

        mBarMaxHeight = mHeight - 2 * offset

        mBarBlankSize = dp2px(context, mBarSingleWidth / 4).toFloat()
        // 用于文字的测量
        val fontMetrics = mBarPaint?.fontMetricsInt
        mHeightBlankSize =
            (offset - fontMetrics!!.let { it.bottom - it.top }) / 2
        mTextHeight =
            (offset + fontMetrics.let { it.bottom - it.top }) / 2

        if (shader == null) {
            shader = LinearGradient(
                0f,
                0f,
                mBarBlankSize * 2,
                mBarMaxHeight,
                Color.WHITE,
                mColor,
                Shader.TileMode.CLAMP
            )
        }
    }


    // Draw中要干什么事情？
    // 1。 柱状绘制
    // 2。 文字描述绘制
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        var count:Int = 0
        for (i in mData!!.indices) {
            if (!checkIsNeedDraw(i)) {
                val height = (mMaxData - mData!![i]) / mMaxData * mBarMaxHeight
                mBarPaint?.shader = null
                drawBarValue(canvas, i, height)
                drawDescriptions(canvas, i)
                mBarPaint?.shader = shader
                drawBars(canvas, height)
                count++
            }
            if (i != mData!!.size - 1) {
                canvas?.translate(mBarSingleWidth, 0f)
            }
        }
        Log.e("onDraw", count.toString())
    }

    private fun checkIsNeedDraw(i: Int): Boolean {
        if (mBarSingleWidth * (i + 1) < scrollX) return true
        if (mBarSingleWidth * i > scrollX + mWidth) return true
        return false
    }

    private fun drawBarValue(canvas: Canvas?, index: Int, height: Float) {
        val value = mData?.get(index).toString()
        mBarPaint?.getTextBounds(
            value,
            0,
            value.length,
            mTextBounds
        )
        val blankSize = (mBarSingleWidth - (mTextBounds.right - mTextBounds.left)) / 2
        mBarPaint?.let { canvas?.drawText(value, blankSize, mTextHeight + height, it) }
    }

    private fun drawDescriptions(canvas: Canvas?, index: Int) {
        val description = mDescription?.get(index)
        mBarPaint?.getTextBounds(
            description,
            0,
            description?.length!!,
            mTextBounds
        )
        val blankSize = (mBarSingleWidth - (mTextBounds.right - mTextBounds.left)) / 2
        mBarPaint?.let {
            canvas?.drawText(
                description!!,
                blankSize,
                mTextHeight + mBarMaxHeight + offset,
                it
            )
        }
    }


    private fun drawBars(canvas: Canvas?, height: Float) {
        Log.e("drawBars", (offset + height).toString())
        mBarPaint?.let {
            canvas?.drawRoundRect(
                mBarBlankSize,
                offset + mBarMaxHeight - scale * (mBarMaxHeight - height),
                mBarSingleWidth - mBarBlankSize,
                mBarMaxHeight + offset,
                mBarCornerSize,
                mBarCornerSize,
                it
            )
        }
    }

    // 手势动作
    private inner class BarGesture : SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            if (mBarShowNum <= mMinBarScrollShowNum) return false

            val position = dp2px(context, scrollX.toFloat())
            if (distanceX >= 0) {
                if (position <= mBarMaxWidth) {
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

    fun setData(barBeanList: List<BarBean>) {
        val data: MutableList<Float> = ArrayList()
        val descriptions: MutableList<String> = ArrayList()
        for (i in barBeanList.indices) {
            barBeanList[i].getValue()?.let {
                data.add(it)
                if (mMaxData < it) {
                    mMaxData = it
                }
            }
            barBeanList[i].getDescription()?.let { descriptions.add(it) }
        }
        if (mBarShowNum > barBeanList.size) mBarShowNum = barBeanList.size
        fixMaxData()
        setData(data, descriptions)
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