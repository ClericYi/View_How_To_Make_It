package com.clericyi.views.model

/**
 * author: ClericYi
 * time: 2020/3/26
 */
class BarBean {
    private var mValue: Float ?= null
    private var mDescription: String? = null

    fun setValue(value: Float): BarBean {
        mValue = value
        return this
    }

    fun setDescription(description: String): BarBean {
        mDescription = description
        return this
    }

    fun getValue(): Float? {
        return mValue
    }

    fun getDescription(): String? {
        return mDescription
    }
}