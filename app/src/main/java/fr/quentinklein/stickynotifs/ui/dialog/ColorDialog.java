package fr.quentinklein.stickynotifs.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;

import fr.quentinklein.stickynotifs.R;

/**
 * Created by qklein<qklein@eliocity.com> on 17/08/15.
 */
public class ColorDialog {

    public static final int[] PRIMARY_COLORS = {
            R.color.material_red500,
            R.color.material_pink500,
            R.color.material_purple500,
            R.color.material_deeppurple500,
            R.color.material_indigo500,
            R.color.material_blue500,
            R.color.material_lightblue500,
            R.color.material_cyan500,
            R.color.material_teal500,
            R.color.material_green500,
            R.color.material_lightgreen500,
            R.color.material_lime500,
            R.color.material_yellow500,
            R.color.material_amber500,
            R.color.material_orange500,
            R.color.material_deeporange500,
            R.color.material_brown500,
            R.color.material_grey500,
            R.color.material_bluegrey500
    };
    public static final int[] PRIMARY_DARK_COLORS = {
            R.color.material_red800,
            R.color.material_pink800,
            R.color.material_purple800,
            R.color.material_deeppurple800,
            R.color.material_indigo800,
            R.color.material_blue800,
            R.color.material_lightblue800,
            R.color.material_cyan800,
            R.color.material_teal800,
            R.color.material_green800,
            R.color.material_lightgreen800,
            R.color.material_lime800,
            R.color.material_yellow800,
            R.color.material_amber800,
            R.color.material_orange800,
            R.color.material_deeporange800,
            R.color.material_brown800,
            R.color.material_grey800,
            R.color.material_bluegrey800
    };

    public static void show(@NonNull Context context, @NonNull final ColorListener listener, @ColorRes int basePrimary, @ColorRes int baseSecondary) {
        final ColorAdapter adapter = new ColorAdapter(context, listener, basePrimary, baseSecondary);
        MaterialDialog show = new MaterialDialog.Builder(context)
                .customView(R.layout.color_dialog, false)
                .positiveText(android.R.string.ok)
                .positiveColor(ColorStateList.valueOf(context.getResources().getColor(basePrimary)))
                .cancelable(true)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(final MaterialDialog dialog) {
                        dialog.dismiss();
                        int[] selectedColors = adapter.getSelectedColors();
                        listener.onColorValidated(selectedColors[0], selectedColors[1]);
                    }
                })
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(final DialogInterface dialog) {
                        dialog.dismiss();
                        listener.onCancel();
                    }
                })
                .show();
        GridView colorGridView = (GridView) show.getView().findViewById(R.id.color_gv);
        colorGridView.setAdapter(adapter);
    }

    public interface ColorListener {
        void onColorChanged(int primaryColor, int secondaryColor);

        void onColorValidated(int primaryColor, int secondaryColor);

        void onCancel();
    }

    public static class ColorAdapter extends BaseAdapter {

        private final Context mContext;
        private final int mButtonSize;
        private final ColorListener mListener;
        private int mSelectedPrimaryColor;
        private int mSelectedPrimaryDarkColor;

        public ColorAdapter(final Context context, final ColorListener listener, int basePrimaryColors, int basePrimaryDarkColors) {
            mContext = context;
            mButtonSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, mContext.getResources().getDisplayMetrics());
            mListener = listener;
            mSelectedPrimaryColor = basePrimaryColors;
            mSelectedPrimaryDarkColor = basePrimaryDarkColors;
        }

        @Override
        public int getCount() {
            return PRIMARY_COLORS.length;
        }

        @Override
        public Object getItem(final int position) {
            return null;
        }

        @Override
        public long getItemId(final int position) {
            return position;
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            Button button;
            if (convertView == null) {
                button = new Button(mContext);
                button.setLayoutParams(new LinearLayout.LayoutParams(mButtonSize, mButtonSize));
            } else {
                button = (Button) convertView;
            }
            int color = mContext.getResources().getColor(PRIMARY_COLORS[position]);
            button.setBackgroundColor(color);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                button.setBackgroundTintList(ColorStateList.valueOf(color));
            }
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    mSelectedPrimaryColor = PRIMARY_COLORS[position];
                    mSelectedPrimaryDarkColor = PRIMARY_DARK_COLORS[position];
                    mListener.onColorChanged(mSelectedPrimaryColor, mSelectedPrimaryDarkColor);
                }
            });
            return button;
        }

        public int[] getSelectedColors() {
            return new int[]{mSelectedPrimaryColor, mSelectedPrimaryDarkColor};
        }
    }

}
