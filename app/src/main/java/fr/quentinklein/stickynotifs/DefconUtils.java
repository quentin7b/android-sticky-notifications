package fr.quentinklein.stickynotifs;

import android.support.annotation.ColorRes;
import android.support.annotation.StyleRes;

import fr.quentinklein.stickynotifs.model.StickyNotification;

/**
 * Created by qklein<qklein@eliocity.com> on 17/08/15.
 */
public class DefconUtils {
    public static
    @ColorRes
    int getDefconColorResource(StickyNotification.Defcon defcon) {
        switch (defcon) {
            case USELESS:
                return R.color.colorUseless;
            case NORMAL:
                return R.color.colorNormal;
            case IMPORTANT:
                return R.color.colorImportant;
            case ULTRA:
                return R.color.colorUltra;
            default:
                return R.color.color_grey;
        }
    }

    public static
    @ColorRes
    int getDefconDarkColorResource(StickyNotification.Defcon defcon) {
        switch (defcon) {
            case USELESS:
                return R.color.colorUselessDark;
            case NORMAL:
                return R.color.colorNormalDark;
            case IMPORTANT:
                return R.color.colorImportantDark;
            case ULTRA:
                return R.color.colorUltraDark;
            default:
                return R.color.color_grey;
        }
    }

    public static
    @StyleRes
    int getDefconStyleResource(StickyNotification.Defcon defcon) {
        switch (defcon) {
            case USELESS:
                return R.style.Base_Theme_Sticky_Switch_Blue;
            case NORMAL:
                return R.style.Base_Theme_Sticky_Switch_Green;
            case IMPORTANT:
                return R.style.Base_Theme_Sticky_Switch_Orange;
            case ULTRA:
                return R.style.Base_Theme_Sticky_Switch_Red;
            default:
                return R.style.Base_Theme_Sticky;
        }
    }
}
