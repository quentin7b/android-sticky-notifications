package com.github.quentin7b.sn;

import android.support.annotation.ColorRes;

import com.github.quentin7b.sn.database.model.StickyNotification;

import java.util.HashMap;
import java.util.Map;

public class ColorHelper {

    private static final BiMap DEFCON_COLORS = new BiMap();

    static {
        DEFCON_COLORS.put(StickyNotification.Defcon.USELESS, R.color.color_useless);
        DEFCON_COLORS.put(StickyNotification.Defcon.NORMAL, R.color.color_normal);
        DEFCON_COLORS.put(StickyNotification.Defcon.IMPORTANT, R.color.color_important);
        DEFCON_COLORS.put(StickyNotification.Defcon.ULTRA, R.color.color_ultra);
    }

    public static
    @ColorRes
    int getDefconColor(StickyNotification.Defcon defcon) {
        return DEFCON_COLORS.get(defcon);
    }

    public static StickyNotification.Defcon getColorDefcon(@ColorRes Integer colorRes) {
        return DEFCON_COLORS.from(colorRes);
    }

    private static final class BiMap extends HashMap<StickyNotification.Defcon, Integer> {

        public StickyNotification.Defcon from(Integer integer) {
            for (Map.Entry<StickyNotification.Defcon, Integer> entry : entrySet()) {
                if (entry.getValue().equals(integer)) {
                    return entry.getKey();
                }
            }
            throw new IllegalArgumentException("No value for this integer");
        }

    }
}
