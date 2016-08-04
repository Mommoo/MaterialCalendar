package com.mommoo.materialcalendar.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

/**
 * Created by mommoo on 2016-07-28.
 */
public class SimpleRippleImageView extends ImageView {
    private int circleColor = Color.parseColor("#31000000");
    private float pivotX,pivotY;
    private Paint paint = new Paint();
    private float radius = 0f,MAX_RADIUS;
    private int totalDuration = 300,remainDuration = 0;
    private int viewWidth,viewHeight;
    private boolean once,startAnim;
    private ObjectAnimator slowAnim;
    private OnClickListener onClickListener;

    public SimpleRippleImageView(Context context) {
        super(context);
        initialize();
    }

    public SimpleRippleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public SimpleRippleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    @TargetApi(21)
    public SimpleRippleImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initialize(){
        paint.setAntiAlias(true);
        paint.setColor(circleColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!once){
            once = true;
            viewWidth = getWidth();
            viewHeight = getHeight();
            MAX_RADIUS = viewWidth>=viewHeight?viewWidth:viewHeight;
            MAX_RADIUS *= 1.2f;
        }
        if(startAnim){
            canvas.drawCircle(pivotX,pivotY,radius,paint);
        }
    }

    @Override
    public void setOnClickListener(OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        pivotX = event.getX();
        pivotY = event.getY();
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            if(!startAnim){
                startAnim = true;
                startSlowAnim();
            }
        }else if(event.getAction() == MotionEvent.ACTION_UP){

            if(startAnim){
                slowAnim.cancel();
            }
        }else if(event.getAction() == MotionEvent.ACTION_MOVE){

        }
        return true;
    }

    private void startSlowAnim(){
        slowAnim = ObjectAnimator.ofFloat(this,"Radius",0f,MAX_RADIUS);
        slowAnim.setDuration(totalDuration*2);
        slowAnim.setInterpolator(new LinearInterpolator());
        slowAnim.addListener(new AnimatorListenerAdapter() {
            private long startTime = System.currentTimeMillis();
            private boolean cancel;
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                remainDuration = (int)((totalDuration*2) -(System.currentTimeMillis() - startTime))/2;
                startQuickAnim();
                cancel = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if(!cancel){
                    exitConfig();
                }
            }
        });
        slowAnim.start();
    }

    private void startQuickAnim(){
        ObjectAnimator animator = ObjectAnimator.ofFloat(this,"Radius",radius,MAX_RADIUS);
        animator.setDuration(remainDuration);
        animator.setInterpolator(new LinearInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                exitConfig();
            }
        });
        animator.start();
    }

    private void exitConfig(){
        remainDuration = 0;
        radius = 0;
        startAnim = false;
        onClickListener.onClick(SimpleRippleImageView.this);
    }

    private void setRadius(float radius){
        this.radius = radius;
        invalidate();
    }

    private float getRadius(){
        return radius;
    }
}
