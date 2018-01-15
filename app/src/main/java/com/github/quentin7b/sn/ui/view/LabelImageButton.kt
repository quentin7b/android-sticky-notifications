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

    private var itemDrawable: Drawable? = null

    var colorResIdentifier: Int = R.color.color_normal
        set(colorRes) {
            field = colorRes
            itemDrawable?.setColorFilter(ContextCompat.getColor(context, colorRes), PorterDuff.Mode.SRC)
            setImageDrawable(itemDrawable)
        }

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    private fun initView() {
        setImageResource(R.drawable.circle)
        this.colorResIdentifier = R.color.color_normal
        itemDrawable = ContextCompat.getDrawable(context, R.drawable.circle)
    }

}
