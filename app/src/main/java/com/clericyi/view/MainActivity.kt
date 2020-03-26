package com.clericyi.view

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.clericyi.views.model.BarBean
import com.clericyi.views.model.PieBean
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private val yellowColor = Color.argb(255, 253, 197, 53)
    private val greenColor = Color.argb(255, 27, 147, 76)
    private val redColor = Color.argb(255, 211, 57, 53)
    private val blueColor = Color.argb(255, 76, 139, 245)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        initPieDatas()
        initBarDatas()
    }

    private fun initBarDatas() {
        val barBean0 = BarBean().setValue(30f).setDescription("描述一")
        val barBean1 = BarBean().setValue(50f).setDescription("描述二")
        val barBean2 = BarBean().setValue(60f).setDescription("描述三")
        val barBean3 = BarBean().setValue(80f).setDescription("描述四")
        val barBean4 = BarBean().setValue(20f).setDescription("描述五")
        val barBean5 = BarBean().setValue(10f).setDescription("描述留")
        val barBeans: MutableList<BarBean> = ArrayList()
        barBeans.add(barBean0)
        barBeans.add(barBean1)
        barBeans.add(barBean2)
        barBeans.add(barBean3)
        barBeans.add(barBean4)
        barBeans.add(barBean5)
        barBeans.add(barBean4)
        bar.setData(barBeans)
    }

    private fun initPieDatas() {
        val mRatios: MutableList<Float> =
            ArrayList()
        val mDescription: MutableList<String> =
            ArrayList()
        val mArcColors: MutableList<Int> = ArrayList()
        val pieBean1 = PieBean().setArcColor(blueColor).setRatio(1f).setDescription("描述一")
        val pieBean2 = PieBean().setArcColor(redColor).setRatio(1f).setDescription("描述二")
        val pieBean3 = PieBean().setArcColor(yellowColor).setRatio(1f).setDescription("描述三")
        val pieBean4 = PieBean().setArcColor(greenColor).setRatio(1f).setDescription("描述四")
        val pieBeans: MutableList<PieBean> = ArrayList()
        pieBeans.add(pieBean1)
        pieBeans.add(pieBean2)
        pieBeans.add(pieBean3)
        pieBeans.add(pieBean4)

        //点击动画开启
//        pie.setData(mRatios, mArcColors, mDescription)
//        pie.setData(pieBeans)

    }
}
