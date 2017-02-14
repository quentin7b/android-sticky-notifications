package com.github.quentin7b.sn.ui.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.quentin7b.sn.ColorHelper;
import com.github.quentin7b.sn.R;
import com.github.quentin7b.sn.database.model.StickyNotification;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StickyNoteFullView extends LinearLayout {

    Integer colorRes;
    @Nullable
    @BindView(R.id.note_title_et)
    TextInputEditText noteTitleEt;
    @BindView(R.id.notification_cb)
    AppCompatCheckBox showNotificationCb;
    @BindView(R.id.level_btn)
    LabelImageButton levelImageBtn;
    @BindView(R.id.note_content_et)
    TextInputEditText noteContentEt;

    public StickyNoteFullView(Context context) {
        super(context);
        initLayout();
    }

    public StickyNoteFullView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout();
    }

    public StickyNoteFullView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public StickyNoteFullView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initLayout();
    }

    private void initLayout() {
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.view_note_full, this, true);
        ButterKnife.bind(this);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle instanceState = new Bundle();
        instanceState.putParcelable(EXTRA.SUPER, super.onSaveInstanceState());
        instanceState.putString(EXTRA.TITLE, getTitle());
        instanceState.putString(EXTRA.CONTENT, getContent());
        instanceState.putBoolean(EXTRA.NOTIFICATION, isNotification());
        instanceState.putInt(EXTRA.DEFCON, getDefcon().describe());
        return super.onSaveInstanceState();
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            this.setTitle(bundle.getString(EXTRA.TITLE, ""));
            this.setContent(bundle.getString(EXTRA.CONTENT, ""));
            this.setNotification(bundle.getBoolean(EXTRA.NOTIFICATION, true));
            this.setDefcon(StickyNotification.Defcon.from(bundle.getInt(EXTRA.DEFCON, StickyNotification.Defcon.NORMAL.describe())));
            state = bundle.getParcelable(EXTRA.SUPER);
        }
        super.onRestoreInstanceState(state);
    }

    @OnClick(R.id.level_btn)
    void onDefconClick() {
        final View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_color_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @SuppressLint("NewApi")
                @Override
                public void onShow(final DialogInterface dialog) {
                    final View view = dialogView.findViewById(R.id.reveal_view);
                    int w = view.getWidth();
                    int h = view.getHeight();
                    float maxRadius = (float) Math.sqrt(w * w / 2 + h * h / 2);
                    Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view,
                            w, h / 2, 0, maxRadius);
                    view.setVisibility(View.VISIBLE);
                    revealAnimator.start();
                }
            });
        }

        LabelImageButton uselessBtn = (LabelImageButton) dialogView.findViewById(R.id.useless_btn);
        LabelImageButton normalBtn = (LabelImageButton) dialogView.findViewById(R.id.normal_btn);
        LabelImageButton importantBtn = (LabelImageButton) dialogView.findViewById(R.id.important_btn);
        LabelImageButton ultraBtn = (LabelImageButton) dialogView.findViewById(R.id.ultra_btn);

        uselessBtn.setColorRes(ColorHelper.getDefconColor(StickyNotification.Defcon.USELESS));
        normalBtn.setColorRes(ColorHelper.getDefconColor(StickyNotification.Defcon.NORMAL));
        importantBtn.setColorRes(ColorHelper.getDefconColor(StickyNotification.Defcon.IMPORTANT));
        ultraBtn.setColorRes(ColorHelper.getDefconColor(StickyNotification.Defcon.ULTRA));

        View.OnClickListener colorClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                LabelImageButton imageButton = (LabelImageButton) v;
                StickyNoteFullView.this.colorRes = imageButton.getColorRes();
                alertDialog.dismiss();
            }
        };

        uselessBtn.setOnClickListener(colorClickListener);
        normalBtn.setOnClickListener(colorClickListener);
        importantBtn.setOnClickListener(colorClickListener);
        ultraBtn.setOnClickListener(colorClickListener);

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                StickyNoteFullView.this.levelImageBtn.setColorRes(StickyNoteFullView.this.colorRes);
            }
        });

        alertDialog.setCancelable(true);
        alertDialog.show();
    }

    public String getTitle() {
        if (this.noteTitleEt != null) {
            return this.noteTitleEt.getText().toString();
        } else {
            return null;
        }
    }

    public void setTitle(String title) {
        if (this.noteTitleEt != null) {
            this.noteTitleEt.setText(title);
            this.noteTitleEt.setSelection(title.length());
        }
    }

    public StickyNotification.Defcon getDefcon() {
        return ColorHelper.getColorDefcon(this.colorRes);
    }

    public void setDefcon(StickyNotification.Defcon defcon) {
        this.colorRes = ColorHelper.getDefconColor(defcon);
        this.levelImageBtn.setColorRes(colorRes);
    }

    public String getContent() {
        return this.noteContentEt.getText().toString();
    }

    public void setContent(String content) {
        this.noteContentEt.setText(content);
        this.noteContentEt.setSelection(content.length());
    }

    public boolean isNotification() {
        return this.showNotificationCb.isChecked();
    }

    public void setNotification(boolean isNotification) {
        this.showNotificationCb.setChecked(isNotification);
    }

    private static final class EXTRA {
        private static final String SUPER = "superState";
        private static final String TITLE = "titleState";
        private static final String CONTENT = "contentState";
        private static final String DEFCON = "defconState";
        private static final String NOTIFICATION = "notificationState";
    }

}
