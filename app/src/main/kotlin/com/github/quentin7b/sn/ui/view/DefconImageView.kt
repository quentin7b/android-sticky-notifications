package com.github.quentin7b.sn.ui.view

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import com.github.quentin7b.sn.ColorHelper

import com.github.quentin7b.sn.R
import com.github.quentin7b.sn.database.model.StickyNotification


class DefconImageView : AppCompatImageView {

    var itemDrawable: Drawable? = null

    var defcon: StickyNotification.Defcon = StickyNotification.Defcon.NORMAL
        set(value) {
            field = value
            val color = ColorHelper.getDefconColor(value)
            itemDrawable?.setColorFilter(ContextCompat.getColor(context, color), PorterDuff.Mode.SRC)
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
        itemDrawable = ContextCompat.getDrawable(context, R.drawable.circle)
    }
}
