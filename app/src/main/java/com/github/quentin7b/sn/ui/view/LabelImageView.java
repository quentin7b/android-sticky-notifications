package com.github.quentin7b.sn.ui.view;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.github.quentin7b.sn.R;


public class LabelImageView extends AppCompatImageView {

    private Drawable itemDrawable;

    public LabelImageView(Context context) {
        super(context);
        initView();
    }

    public LabelImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public LabelImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    void initView() {
        setImageResource(R.drawable.circle);
        itemDrawable = ContextCompat.getDrawable(getContext(), R.drawable.circle);
    }

    public void setColorRes(@ColorRes int color) {
        itemDrawable.setColorFilter(ContextCompat.getColor(getContext(), color), PorterDuff.Mode.SRC);
        setImageDrawable(itemDrawable);
    }
}
