package com.github.quentin7b.sn.ui.view;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;

import com.github.quentin7b.sn.R;


public class LabelImageButton extends AppCompatImageButton {

    private Drawable itemDrawable;
    private int colorRes;

    public LabelImageButton(Context context) {
        super(context);
        initView();
    }

    public LabelImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public LabelImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    void initView() {
        setImageResource(R.drawable.circle);
        this.colorRes = R.color.color_normal;
        itemDrawable = ContextCompat.getDrawable(getContext(), R.drawable.circle);
    }

    public void setColorRes(@ColorRes int color) {
        this.colorRes = color;
        itemDrawable.setColorFilter(ContextCompat.getColor(getContext(), color), PorterDuff.Mode.SRC);
        setImageDrawable(itemDrawable);
    }

    @ColorRes
    public int getColorRes() {
        return this.colorRes;
    }
}
