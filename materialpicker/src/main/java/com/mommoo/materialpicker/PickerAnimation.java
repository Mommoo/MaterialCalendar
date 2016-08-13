package com.mommoo.materialpicker;

import android.animation.TimeInterpolator;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by mommoo on 2016-07-28.
 */
public abstract class PickerAnimation {
    protected final int ANIM_DURATION = 450;
    protected TimeInterpolator interpolator = new LinearInterpolator();

    public interface AnimCallBack{
        void callBack();
    }
    public int getDuration(){
        return ANIM_DURATION;
    }
    public void setTimeInterpolator(TimeInterpolator timeInterpolator){
        this.interpolator = timeInterpolator;
    }
    public TimeInterpolator getTimeInterpolator(){
        return interpolator;
    }
    abstract void setTargetView(View targetView);
    abstract void start();
}
