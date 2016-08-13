package com.mommoo.materialpicker;

import android.animation.TimeInterpolator;
import android.view.View;

/**
 * Created by Mommoo1 on 2016-02-03.
 */
abstract class Animation {
    protected View targetView;
    protected int delayTime;
    protected TimeInterpolator timeInterpolator;
    protected boolean repeated;

    @SuppressWarnings("unchecked")
    public <T extends Animation>T setTargetView(View targetView){
        this.targetView = targetView;
        return (T)this;
    };
    @SuppressWarnings("unchecked")
    public <T extends Animation>T setDelay(int delayTime){
        this.delayTime = delayTime;
        return (T)this;
    }
    @SuppressWarnings("unchecked")
    public <T extends Animation>T setInterpolator(TimeInterpolator timeInterpolator){
        this.timeInterpolator = timeInterpolator;
        return (T)this;
    };
    public <T extends Animation>T setRepeated(boolean repeated){
        this.repeated = repeated;
        return (T)this;
    };

    abstract public void start(int duration,AnimationCallBack animationCallBack);
}
