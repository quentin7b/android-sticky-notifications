package com.github.quentin7b.sn.ui.view

import android.view.ViewGroup
import android.view.ViewManager
import com.github.quentin7b.sn.R
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.dip
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.view


inline fun ViewManager.labelImageButton(theme: Int = 0, init: LabelImageButton.() -> Unit) = ankoView({ LabelImageButton(it) }, theme, init)
fun ViewManager.horizontalDivider(colorResource: Int = R.color.accent) = view {
    layoutParams = ViewGroup.LayoutParams(matchParent, dip(.5f))
    backgroundResource = colorResource
}