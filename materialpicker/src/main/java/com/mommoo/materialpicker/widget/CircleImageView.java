package com.mommoo.materialpicker.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Mommoo1 on 2016-02-08.
 */
public class CircleImageView extends ImageView {
    private Paint paint,paint2;
    private int PADDING;
    private Context context;
    private AttributeSet attr;
    public CircleImageView(Context context) {
        super(context);
        initialize(context);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.attr = attrs;
        initialize(context);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.attr = attrs;
        initialize(context);
    }

    private void initialize(Context context){
        this.context = context;
        paint = new Paint();
        paint2 = new Paint();
        paint.setAntiAlias(true);
        paint2.setAntiAlias(true);
        paint.setColor(Color.TRANSPARENT);
        paint2.setColor(Color.TRANSPARENT);
        setTypedStyle();
    }

    private void setTypedStyle(){

    }

    public void setCircleBackgroundColor(int color){
        paint2.setColor(color);
        invalidate();
    }

    public void setCirclePadding(int padding){
        this.PADDING = padding;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, (getWidth() / 2)-PADDING, paint2);
        super.onDraw(canvas);
    }
}
