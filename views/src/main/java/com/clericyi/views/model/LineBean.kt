package com.clericyi.views.model

/**
 * author: ClericYi
 * time: 2020/3/28
 */
class LineBean {
    private var mValue: Float ?= null
    private var mDescription: String? = null

    fun setValue(value: Float): LineBean {
        mValue = value
        return this
    }

    fun setDescription(description: String): LineBean {
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