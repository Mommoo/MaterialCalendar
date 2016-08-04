package com.mommoo.materialcalendar.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.mommoo.materialcalendar.R;

/**
 * Created by mommoo on 2016-07-02.
 * @author mommoo
 *
 * Cannot use clipAnimation when android OS level lower than 6.0(Marshmallow)
 * so, make this class in order to compatatible with any OS version
 *
 * @see ObjectAnimator
 *
 *
 */
public class ClipAnimLayout extends FrameLayout {

    private Path path;
    private Builder builder;
    private boolean once, backgroundColorBlock,isStart,start;
    private float radius=0f;
    private float MAXIMUM_RADIUS;
    private int tempBackgroundColor = Color.WHITE;


    private ClipAnimLayout(Context context) {
        super(context);
    }

    /**
     *
     * @param context essential elements for view
     * @param builder want to initialize by use this variable
     */
    public ClipAnimLayout(Context context, @Nullable Builder builder){
        this(context);
        this.builder = builder;
        initialize(null);
    }

    public ClipAnimLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(attrs);
    }

    public ClipAnimLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(attrs);
    }

    @TargetApi(21)
    public ClipAnimLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(attrs);
    }

    /**
     *
     *  This view must have transparent background
     *
     */

    @Override
    public void setBackgroundColor(int color){
        if(!backgroundColorBlock) super.setBackgroundColor(color);
        else {
            tempBackgroundColor = color;
            invalidate();
        }
    }

    public void setBuilder(Builder builder){
        this.builder = builder;
    }

    public Builder getBuilder(){
        return builder;
    }

    public void initialize(AttributeSet set){
        setWillNotDraw(false);
        if(this.builder == null){
            builder = new Builder();
            if(set !=null){
                TypedArray typedArray = getContext().obtainStyledAttributes(set, R.styleable.ClipAnimLayout);
                builder.setStartLocation(typedArray.getFloat(R.styleable.ClipAnimLayout_start_pivot_x,builder.pivotX)
                        ,typedArray.getFloat(R.styleable.ClipAnimLayout_start_pivot_y,builder.pivotY));
                builder.setAnimDelay(typedArray.getInteger(R.styleable.ClipAnimLayout_animation_start_delay,builder.animDelay));
                builder.setAnimDuration(typedArray.getInteger(R.styleable.ClipAnimLayout_animation_duration,builder.animDuration));
                builder.tempRadius = typedArray.getFloat(R.styleable.ClipAnimLayout_start_radius,0f);
                typedArray.recycle();
            }
        }
        path = new Path();
        if(!this.isInEditMode())setBackgroundColor(Color.TRANSPARENT);
        backgroundColorBlock = true; // to block change original backgroundColor
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(!once){
            int viewWidth = getWidth();
            int viewHeight = getHeight();
            MAXIMUM_RADIUS = viewWidth>=viewHeight?viewWidth*1.5f:viewHeight*1.5f;
            once =  true;
        }
        if(start){
            if(!isStart) {
                isStart= true;
                start();
            }
            path.addCircle(builder.pivotX,builder.pivotY,radius, Path.Direction.CCW);
            canvas.clipPath(path);
        }

        super.onDraw(canvas);
        canvas.drawColor(tempBackgroundColor); // do draw backgroundColor instead of original setBackgroundColor();
    }

    /**
     *
     *  Since cannot gain view-size Info before calling onDraw() method
     *  divide start method into two separated method : startAnim(), start()
     *
     */

    public void startAnim(){
        start = true;
        backgroundColorBlock = false;
        setBackgroundColor(Color.TRANSPARENT);
        backgroundColorBlock = true;
        path.reset();
        invalidate();
    }

    private void start(){
        ObjectAnimator animator = ObjectAnimator.ofFloat(ClipAnimLayout.this,"Radius",builder.tempRadius,MAXIMUM_RADIUS);
        animator.setDuration(builder.animDuration);
        animator.setStartDelay(builder.animDelay);
        animator.setInterpolator(builder.interpolator);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if(builder.animListener !=null) builder.animListener.onAnimEnd(ClipAnimLayout.this);
                isStart = false;
                start = false;
                radius = builder.tempRadius;
            }
        });
        animator.start();
    }

    /**
     *
     *  Needed for objectAnimator
     *
     */

    protected void setRadius(float radius){
        this.radius = radius;
        invalidate();
    }

    protected float getRadius(){
        return radius;
    }

    /**
     *
     *  This class wanted many kinds of variable for animation.
     *  to good readability, this class contain builder pattern
     *
     */

    public static class Builder{
        private float pivotX,pivotY;
        private float tempRadius = 0f;
        private int animDuration = 1000;
        private int animDelay = 0;
        private TimeInterpolator interpolator = new DecelerateInterpolator();
        public interface AnimListener{void onAnimEnd(View view);}
        private AnimListener animListener;

        public Builder setStartRadius(float radius){
            this.tempRadius = radius;
            return this;
        }

        public Builder setAnimDuration(int animDuration){
            this.animDuration = animDuration;
            return this;
        }

        public Builder setAnimDelay(int animDelay){
            this.animDelay = animDelay;
            return this;
        }

        public Builder setStartLocation(float pivotX,float pivotY){
            this.pivotX = pivotX;
            this.pivotY = pivotY;
            return this;
        }

        public Builder setTimeInterpolator(TimeInterpolator interpolator){
            this.interpolator = interpolator;
            return this;
        }

        public Builder setAnimListener(AnimListener animListener){
            this.animListener = animListener;
            return this;
        }
    }
}
