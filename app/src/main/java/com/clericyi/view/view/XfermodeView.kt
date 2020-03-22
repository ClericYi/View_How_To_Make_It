package com.clericyi.view.view

import android.content.Context
import android.graphics.*
import android.graphics.Canvas.ALL_SAVE_FLAG
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.clericyi.view.R


/**
 * author: ClericYi
 * time: 2020/3/22
 */
class XfermodeView : View {
    private var paint: Paint? = null
    private var circleBitmap: Bitmap? = null
    private var rectBitmap: Bitmap? = null
    private var xfermode: Xfermode?= null

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initPaint()
    }

    private fun initPaint() {

        paint = Paint()


        circleBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.circle)
        rectBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.rect)
        xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val layerId = canvas?.saveLayer(
            null, null
        )
        canvas?.let { canvasIt ->
            paint?.let {paintIt->
                paintIt.color = Color.GREEN
                canvasIt.drawCircle(100f, 100f, 50f, paintIt)
//                rectBitmap?.let { canvas.drawBitmap(it,0f,0f, paintIt) }
                paintIt.xfermode = xfermode
                paintIt.color = Color.BLUE
                canvasIt.drawRect(100f, 100f, 200f, 200f, paintIt)
//                circleBitmap?.let { canvas.drawBitmap(it, 50f, 50f,paintIt) }
                paintIt.xfermode = null
            }

        }
        layerId?.let { canvas.restoreToCount(it) }
    }


}