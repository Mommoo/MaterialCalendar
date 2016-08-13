package com.mommoo.materialpicker;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by Mommoo on 2016-02-08.
 */
class ScreenManager {

    private Context context;
    private DisplayMetrics displayMetrics = new DisplayMetrics();

    public ScreenManager(Context context){
        this.context = context;
    }

    public int getScreenHeight(){
        ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }
    public int getScreenWidth(){
        ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }
    public int getStatusBarHeight(){
        int statusHeight = 0;
        int screenSizeType = (context.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK);

        if(screenSizeType != Configuration.SCREENLAYOUT_SIZE_XLARGE) {                                  //태블릿이 아닌경우
            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusHeight = context.getResources().getDimensionPixelSize(resourceId);
            }
        }
        return statusHeight;
    }
}
