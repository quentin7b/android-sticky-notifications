package fr.quentinklein.stickynotifs;

import android.content.Context;
import android.os.Build;
import android.support.annotation.ColorRes;

/**
 * Created by qklein<qklein@eliocity.com> on 21/08/15.
 */
public class APIUtils {

    public static int getColor(Context context, @ColorRes int colorRes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getResources().getColor(colorRes, context.getTheme());
        } else {
            return context.getResources().getColor(colorRes);
        }
    }

}
