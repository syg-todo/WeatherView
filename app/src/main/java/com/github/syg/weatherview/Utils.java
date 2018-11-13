package com.github.syg.weatherview;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by lenove on 2017/12/18.
 */

public class Utils {
    public static int dp2px(Context context, float dp) {
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return Math.round(px);
    }
}
