package com.clericyi.view.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 * author: ClericYi
 * time: 2020/3/21
 * 基础用法，一般我们的自定义View基于的就是画笔
 */
class BaseView : View {
    private var paint: Paint? = null


    constructor(context: Context) : super(context) {
        initPaint()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initPaint()
    }

    private fun initPaint() {
        paint = Paint()
        // 绘制模式FILL、STROKE、StrokeAndFill
        paint?.style = Paint.Style.FILL
        // 画笔颜色
        paint?.color = Color.RED
        // 线条宽度,在Stroke存在时有效
        // 加上FILL时是叠加效果
        paint?.strokeWidth = 50f
        // 文字大小
        paint?.textSize = 30f
        // 抗锯齿
        paint?.isAntiAlias = true
        // 绘制时的笔头样式，BUTT（平头）、SQUARE（方头）、ROUND（圆头）
        paint?.strokeCap = Paint.Cap.BUTT
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        // 三个数据表示的含义要清楚，因为Android坐标系和现实不同
        paint?.let { canvas?.drawCircle(200f, 100f, 50f, it) }
        paint?.let { canvas?.drawPoint(50f, 100f, it) }
        // 尝试注释和运行这行代码
        // 知道画布的区域是哪里
        // ARGB、RGB.......
//        canvas?.drawColor(Color.BLACK)
    }


}