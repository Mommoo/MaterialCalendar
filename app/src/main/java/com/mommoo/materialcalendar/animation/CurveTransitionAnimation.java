package com.mommoo.materialcalendar.animation;

import android.animation.ValueAnimator;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.view.View;

/**
 * Created by mommoo on 2016-07-24.
 */
public class CurveTransitionAnimation extends Animation {

    private float fromX, toX, fromY, toY,depthX,depthY;

    public CurveTransitionAnimation(View targetView) {
        this.targetView = targetView;
    }

    public CurveTransitionAnimation(View targetView, float depthX, float depthY, float fromX, float toX, float fromY, float toY) {
        this.targetView = targetView;
        setValue(depthX,depthY,fromX,toX,fromY,toY);
    }

    public CurveTransitionAnimation setValue(float depthX,float depthY, float fromX, float toX, float fromY, float toY) {
        this.depthX = depthX;
        this.depthY = depthY;
        this.fromX = fromX;
        this.toX = toX;
        this.fromY = fromY;
        this.toY = toY;
        return this;
    }

    public void start(int duration, final AnimationCallBack animationCallBack) {
        final ValueAnimator pathAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        final Path path = new Path();
        if (repeated) {
            pathAnimator.setRepeatCount(ValueAnimator.INFINITE);
        }
        path.moveTo(fromX,fromY);
        path.quadTo(depthX,depthY,toX,toY);
        path.close();
        pathAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            float[] point = new float[2];
            float lastX;
            private PathMeasure pathMeasure = new PathMeasure(path, false);
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float val = animation.getAnimatedFraction();
                pathMeasure.getPosTan(pathMeasure.getLength() * val, point, null);
                if(lastX > point[0]){
                    if (animationCallBack != null) animationCallBack.callBack();
                    pathAnimator.removeUpdateListener(this);
                }else{
                    targetView.setX(point[0]);
                    targetView.setY(point[1]);
                    lastX = point[0];
                }
            }
        });
        pathAnimator.setDuration(duration*2);
        pathAnimator.setInterpolator(timeInterpolator);
        pathAnimator.setStartDelay(delayTime);
        pathAnimator.start();
    }

}
