package com.clericyi.views.model

/**
 * author: ClericYi
 * time: 2020/3/25
 */
class PieBean {
    private var mRatio: Float? = null
    private var mDescription: String? = null
    private var mArcColor: Int? = null

    fun setRatio(ratio: Float): PieBean {
        mRatio = ratio
        return this
    }

    fun setDescription(description: String): PieBean {
        mDescription = description
        return this
    }

    fun setArcColor(arcColor: Int): PieBean {
        mArcColor = arcColor
        return this
    }

    fun getRatio(): Float? {
        return mRatio
    }

    fun getDescription(): String? {
        return mDescription
    }

    fun getArcColor(): Int? {
        return mArcColor
    }
}