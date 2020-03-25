package com.clericyi.view.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 * author: ClericYi
 * time: 2020/3/24
 */
class CanvasOperationView : View {
    private var paint: Paint? = null

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initPaint()
    }

    private fun initPaint() {
        paint = Paint()
        paint?.style = Paint.Style.FILL
        paint?.color = Color.RED
        paint?.isAntiAlias = true
        paint?.strokeCap = Paint.Cap.BUTT
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.translate(60f, 80f)
        paint?.let { canvas?.drawCircle(0f, 0f, 100f, it) }

        //
//        for (i in 0..2) {
            canvas?.save()
            paint?.color = Color.BLUE
            canvas?.translate(60f, 80f)
//            canvas?.scale(0.5f, 0.5f)
//            canvas?.rotate(45f)
//            canvas?.rotate(45f,30f, 40f)
        canvas?.skew(0f, 0.5f)
            paint?.let { canvas?.drawRect(0f, 0f, 100f, 100f, it) }
            canvas?.restore()
//        }
    }

}