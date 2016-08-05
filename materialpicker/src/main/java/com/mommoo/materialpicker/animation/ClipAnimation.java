package com.mommoo.materialpicker.animation;

import android.view.View;

/**
 * Created by mommoo on 2016-07-29.
 */
public class ClipAnimation extends PickerAnimation{

    private float fromX,fromY;
    private int duration;

    public ClipAnimation(float fromX,float fromY){
        this.fromX = fromX;
        this.fromY = fromY;
    }

    public void setAnimDuration(int duration){
        this.duration = duration;
    }

    public int getAnimDuration(){
        return duration;
    }

    public float getFromX(){
        return fromX;
    }

    public float getFromY(){
        return fromY;
    }

    @Override
    public void setTargetView(View targetView) {

    }

    @Override
    public void start() {

    }
}
