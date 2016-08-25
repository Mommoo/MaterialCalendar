package com.mommoo.materialpicker;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by Mommoo on 2015-02-18.
 */
class DIPManager {
    public static int dip2px(int dip, Context context){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
    }
    public static int px2dip(int px,Context context){
        return (int)(px/context.getResources().getDisplayMetrics().density);
    }
}
