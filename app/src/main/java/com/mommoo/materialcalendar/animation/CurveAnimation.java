package com.mommoo.materialcalendar.animation;

import android.animation.TimeInterpolator;
import android.graphics.Color;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

import com.mommoo.materialcalendar.manager.ScreenManager;
import com.mommoo.materialcalendar.widget.CircleImageView;


/**
 * Created by mommoo on 2016-07-28.
 */
public class CurveAnimation extends PickerAnimation{

    private View targetView;
    private Builder builder;

    public CurveAnimation(Builder builder){
        this.builder = builder;
    }

    @Override
    public void setTargetView(View targetView){
        this.targetView = targetView;
    }

    public float getStartRadius(){
        return this.builder.tempRadius;
    }

    public float[] getStartLocation(){
        return new float[]{builder.pivotX,builder.pivotY};
    }
    public float[] getEndLocation(){
        ScreenManager sm = new ScreenManager(targetView.getContext());
        return new float[]{(sm.getScreenWidth() - builder.tempRadius)/2,(sm.getScreenHeight() - builder.tempRadius)/2};
    }

    public static class Builder{
        private float pivotX;
        private float pivotY;
        private float tempRadius = 0.0F;
        private int startCircleColor;
        private TimeInterpolator interpolator = new AccelerateDecelerateInterpolator();
        private AnimCallBack animCallBack;

        public CurveAnimation.Builder setStartRadius(float radius) {
            this.tempRadius = radius;
            return this;
        }

        public CurveAnimation.Builder setStartCircleColor(int circleColor) {
            this.startCircleColor = circleColor;
            return this;
        }

        public CurveAnimation.Builder setStartLocation(float pivotX, float pivotY) {
            this.pivotX = pivotX;
            this.pivotY = pivotY;
            return this;
        }

        public CurveAnimation.Builder setTimeInterpolator(TimeInterpolator interpolator) {
            this.interpolator = interpolator;
            return this;
        }

        public CurveAnimation.Builder setAnimCallBack(AnimCallBack callBack){
            this.animCallBack = callBack;
            return this;
        }
    }

    @Override
    public void start() {
        ScreenManager sm = new ScreenManager(targetView.getContext());
        final CircleImageView tempImageView = new CircleImageView(targetView.getContext());
        tempImageView.setCircleBackgroundColor(Color.WHITE);
        final FrameLayout decoView = ((FrameLayout)targetView.getParent());
        decoView.addView(tempImageView,new FrameLayout.LayoutParams((int)builder.tempRadius*2,(int)builder.tempRadius*2));
        tempImageView.setX(builder.pivotX);
        tempImageView.setY(builder.pivotY);
        float toX = (sm.getScreenWidth() - builder.tempRadius)/2;
        float toY = (sm.getScreenHeight() - builder.tempRadius)/2;
        float depthX = builder.pivotX + (toX - builder.pivotX)/4;
        float depthY = builder.pivotY+ (3*(toY -builder.pivotY)/4);
        AnimationCallBack animationCallBack = new AnimationCallBack() {
            @Override
            public void callBack() {
                if(builder.animCallBack != null) builder.animCallBack.callBack();
                decoView.removeView(tempImageView);
            }
        };
        new CurveTransitionAnimation(tempImageView,depthX,depthY,builder.pivotX,toX,builder.pivotY,toY)
                .setInterpolator(builder.interpolator)
                .start(ANIM_DURATION, animationCallBack);
    }
}
