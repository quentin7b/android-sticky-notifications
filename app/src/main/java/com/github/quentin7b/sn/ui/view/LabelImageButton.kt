package com.github.quentin7b.sn.ui.view

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatImageButton
import android.util.AttributeSet

import com.github.quentin7b.sn.R


class LabelImageButton : AppCompatImageButton {

    var itemDrawable: Drawable? = null
    var colorResIdentifier: Int = 0

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    internal fun initView() {
        setImageResource(R.drawable.circle)
        this.colorResIdentifier = R.color.color_normal
        itemDrawable = ContextCompat.getDrawable(context, R.drawable.circle)
    }

    fun setColorRes(@ColorRes color: Int) {
        this.colorResIdentifier = color
        itemDrawable!!.setColorFilter(ContextCompat.getColor(context, color), PorterDuff.Mode.SRC)
        setImageDrawable(itemDrawable)
    }

    @ColorRes
    fun getColorRes(): Int {
        return this.colorResIdentifier
    }
}
