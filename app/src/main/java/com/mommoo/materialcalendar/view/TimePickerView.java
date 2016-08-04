package com.mommoo.materialcalendar.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;


import com.mommoo.materialcalendar.R;
import com.mommoo.materialcalendar.manager.DIPManager;
import com.mommoo.materialcalendar.toolkit.CalendarCalculator;
import com.mommoo.materialcalendar.toolkit.PickerDimension;

import java.util.Calendar;

/**
 * Created by mommoo on 2016-08-01.
 */
public class TimePickerView extends View {

    private boolean once,movable,isAnim,isLeftArrowTouch,isRightArrowTouch;
    private Calendar calendar;
    private float centerX, centerY, amPmCenterX;
    private float am_pmRadius,radius,pivotRadius,selectedRadius,pointerRadius, textRadius,textRadius2;
    private DecoPaint decoPaint;
    private static final int HOUR_MODE = 0;
    private static final int MINUTE_MODE = 1;
    private int viewMode = 0,angle,textAlpha,hour,minute,am_pm;
    private int padding = DIPManager.dip2px(16,getContext());
    private String[] am_pmStrings = CalendarCalculator.getAM_PM();
    private Bitmap[] arrow = new Bitmap[2];

    public interface NotifyChanged{
        public void notify(int hour, int minute, int am_pm);
        public void vibrate();
    }

    private NotifyChanged notifyChanged;

    public void setNotifyChanged(NotifyChanged notifyChanged){
        this.notifyChanged = notifyChanged;
    }

    public TimePickerView(Context context,int viewMode,Calendar cal) {
        super(context);
        this.viewMode = viewMode;
        this.calendar = cal;
        initialize(context);
    }

    private TimePickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    private TimePickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    @TargetApi(21)
    private TimePickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context);
    }

    public void initialize(Context context){
        decoPaint = new DecoPaint(context);
        hour = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);
        arrow[0] = BitmapFactory.decodeResource(getResources(), R.mipmap.arrow);
        arrow[1] = BitmapFactory.decodeResource(getResources(),R.mipmap.arrow2);
    }

    public void changeViewMode(int viewMode){
        this.viewMode = viewMode;
        invalidate();
    }

    public void setData(int am_pm,int hour, int minute){
        this.am_pm = am_pm;
        this.hour = hour;
        this.minute = minute;
        angle = viewMode==HOUR_MODE?CalendarCalculator.transHourToAngle(this.hour):CalendarCalculator.transMinuteToAngle(this.minute);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!once){
            once = true;
            setFitRadius();
            centerX = getWidth()/2;
            centerY = getHeight()/2;
            amPmCenterX = padding+am_pmRadius;
            if(this.viewMode == MINUTE_MODE) amPmCenterX = -amPmCenterX;
            this.hour = calendar.get(Calendar.HOUR);
            if(hour==0) this.hour = 12;
            angle = viewMode==HOUR_MODE?CalendarCalculator.transHourToAngle(this.hour):CalendarCalculator.transMinuteToAngle(this.minute);
        }
        drawBackgroundCircle(canvas);
        if(viewMode== HOUR_MODE) {
            drawAM_PM_CircleAndText(canvas);
            drawRightArrowIcon(canvas);
        }
        else drawLeftArrowIcon(canvas);
        drawPivotCircle(canvas);
        drawSelectedTextCircle(canvas,angle);
        drawTimeText(canvas);
        drawLine(canvas,angle);
        if(movable) drawPointerCircle(canvas,angle);
        else if(viewMode == MINUTE_MODE && angle%30!=0) drawPointerCircle(canvas,angle);
    }

    private void setFitRadius(){
        radius = (getWidth()/2) - padding;
        textRadius = 4*radius/5;
        textRadius2 = textRadius;
        selectedRadius = radius/7;
        am_pmRadius = radius/5;
        pivotRadius = padding/8;
        pointerRadius = radius/30;
        for(int i=0; i<2; i++) arrow[i] = Bitmap.createScaledBitmap(arrow[i],(int)am_pmRadius,(int)am_pmRadius,true);
    }

    private void drawBackgroundCircle(Canvas canvas){
        canvas.drawCircle(centerX,centerY,radius,decoPaint.setBackgroundCircleColor());
    }

    private void drawLeftArrowIcon(Canvas canvas){
        canvas.drawCircle(-amPmCenterX,am_pmRadius,am_pmRadius,isLeftArrowTouch?decoPaint.setSelectedCircleColor():decoPaint.setBackgroundCircleColor());
        canvas.drawBitmap(arrow[0],-arrow[0].getWidth()/2-amPmCenterX,arrow[0].getHeight()/2,null);
    }

    private void drawRightArrowIcon(Canvas canvas){
        canvas.drawCircle(getWidth()-amPmCenterX,am_pmRadius,am_pmRadius,isRightArrowTouch?decoPaint.setSelectedCircleColor():decoPaint.setBackgroundCircleColor());
        canvas.drawBitmap(arrow[1],-arrow[1].getWidth()/2+getWidth()-amPmCenterX,arrow[1].getHeight()/2,null);
    }

    private void drawAM_PM_CircleAndText(Canvas canvas){
        float centerY = getHeight()-(am_pmRadius);
        for(int i =0; i <2 ; i++){
            float amPmCenterX = this.amPmCenterX;
            if(i==1) amPmCenterX = getWidth() - amPmCenterX;
            canvas.drawCircle(amPmCenterX,centerY,am_pmRadius
                    ,i==am_pm?decoPaint.setAM_PM_SelectedCircleColor():decoPaint.setAM_PM_BackgroundCircleColor());
            canvas.drawText(am_pmStrings[i],amPmCenterX-(decoPaint.getStringWidth(am_pmStrings[i])/2)
                    ,centerY-(decoPaint.getStringHeight())/2
                    ,decoPaint.setTextColor());
        }
    }

    private void drawPivotCircle(Canvas canvas){
        canvas.drawCircle(centerX,centerY,pivotRadius,decoPaint.setPivotCircleColor());
    }

    private void drawSelectedTextCircle(Canvas canvas,int angle){
        canvas.drawCircle(centerX + getX_ByAngle(textRadius2,angle)
                ,centerY - getY_ByAngle(textRadius2,angle)
                ,selectedRadius
                ,decoPaint.setSelectedCircleColor());
    }

    private void drawTimeText(Canvas canvas){
        for(int i=1; i<13;i++){
            String time = viewMode == HOUR_MODE?Integer.toString(i):Integer.toString(i*5);
            int angle = viewMode == HOUR_MODE?CalendarCalculator.transHourToAngle(i):CalendarCalculator.transMinuteToAngle(i*5);
            if(time.equals("60")) time = "00";
            canvas.drawText(time
                    ,centerX + getX_ByAngle(textRadius,angle) - (decoPaint.getStringWidth(time)/2)
                    ,centerY - getY_ByAngle(textRadius,angle) - (decoPaint.getStringHeight()/2)
                    ,decoPaint.setTextColor());
        }
    }

    private void drawLine(Canvas canvas, int angle){
        canvas.drawLine(centerX,centerY
                ,getX_ByAngle(textRadius2-selectedRadius,angle) + centerX
                ,-getY_ByAngle(textRadius2-selectedRadius,angle) + centerY
                ,decoPaint.setLineColor());
    }

    private void drawPointerCircle(Canvas canvas, int angle){
        canvas.drawCircle(centerX + getX_ByAngle(4*radius/5,angle)
                ,centerY - getY_ByAngle(4*radius/5,angle)
                ,pointerRadius
                ,decoPaint.setPivotCircleColor());
    }

    private float getX_ByAngle(float standardRadius, int angle){
        return (float)(standardRadius*Math.cos(Math.toRadians(angle)));
    }

    private float getY_ByAngle(float standardRadius, int angle){
        return (float)(standardRadius*Math.sin(Math.toRadians(angle)));
    }

    private int getAngleByCoordinate(float x, float y){
        float height = y - centerY;
        float width = x - centerX;
        double radian = Math.atan2(height,width);
        double tempDegree = Math.toDegrees(radian);
        float resultDegree;
        if(tempDegree<0) resultDegree = -(float)tempDegree;
        else resultDegree = (float)(360 - tempDegree);

        return (int)resultDegree;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        boolean am = x>=padding && x<= padding +(am_pmRadius*2) && y>=getHeight()-(am_pmRadius*2) && y<=getHeight();
        boolean pm = x>=getWidth()-padding-(am_pmRadius*2) && x<= getWidth() -padding && y>=getHeight()-(am_pmRadius*2) && y<=getHeight();
        boolean isLeftArrow = x>=padding && x<=padding+(am_pmRadius*2) && y>=0 && y<= am_pmRadius*2;
        boolean isRightArrow = x>=getWidth()-padding-(am_pmRadius*2) && x<=getWidth()-padding && y>=0 && y<= am_pmRadius*2;

        if(event.getAction() == MotionEvent.ACTION_DOWN){
            float distance = (float)(Math.pow(centerX - x,2) + Math.pow(centerY - y,2));
            if(distance<Math.pow(radius,2) && distance>Math.pow(3*radius/5,2) && !isAnim){
                movable = true;
                moveLine(x,y);
            }
            if(am || pm){
                if(am)am_pm = 0;
                if(pm)am_pm = 1;
                if(notifyChanged != null) notifyChanged.notify(hour,minute,am_pm);
                invalidate();
            }
            if(viewMode == MINUTE_MODE && !movable){
                if(isLeftArrow) {
                    isLeftArrowTouch=  true;
                    invalidate();
                }
            }else if(viewMode == HOUR_MODE && !movable){
                if(isRightArrow){
                    isRightArrowTouch = true;
                    invalidate();
                }
            }

        }else if(event.getAction() == MotionEvent.ACTION_MOVE){
            if(movable){
                moveLine(x,y);
            }else{
                if(!isLeftArrow) {
                    isLeftArrowTouch = false;
                    invalidate();
                }
                if(!isRightArrow){
                    isRightArrowTouch = false;
                    invalidate();
                }
            }
        }else if(event.getAction() == MotionEvent.ACTION_UP){
            if(movable){
                movable = false;
                invalidate();
                if(viewMode == HOUR_MODE){
                    isAnim = true;
                    startTransAnimation();
                }
            }
            if(isLeftArrowTouch){
                if(isLeftArrow) {
                    startTransAnimation();
                    if(notifyChanged != null) notifyChanged.vibrate();
                }
                else {
                    isLeftArrowTouch = false;
                    invalidate();
                }
            }
            if(isRightArrowTouch){
                if(isRightArrow) {
                    startTransAnimation();
                    if(notifyChanged != null) notifyChanged.vibrate();
                }
                else {
                    isRightArrowTouch = false;
                    invalidate();
                }
            }
        }
        return true;
    }

    private void moveLine(float x,float y){
        int quotient;
        int standardRemind;
        boolean mode =viewMode == HOUR_MODE;
        if(mode){
            quotient = 30;
            standardRemind = 15;
        }else{
            quotient = 6;
            standardRemind = 3;
        }
        int tempAngle = getAngleByCoordinate(x,y);
        int remainder = tempAngle%quotient;
        if(remainder <standardRemind) angle = tempAngle - remainder;
        else if(remainder>standardRemind) angle = tempAngle + (quotient-remainder);
        if(mode)hour = CalendarCalculator.transAngleToHour(angle);
        else minute = CalendarCalculator.transAngleToMinute(angle)==60?0:CalendarCalculator.transAngleToMinute(angle);
        if(notifyChanged != null && hour != -1 && minute != -1) notifyChanged.notify(hour,minute,am_pm);
        invalidate();
    }

    private void startTransAnimation(){
        if(isLeftArrowTouch) isLeftArrowTouch = false;
        if(isRightArrowTouch) isRightArrowTouch = false;
        final float initRadius = textRadius;
        final float initTextSize = decoPaint.getTextSize();

        ObjectAnimator transAnim = ObjectAnimator.ofFloat(this,"TextRadius",textRadius,6*radius/5);
        ObjectAnimator amPmTransAnim = ObjectAnimator.ofFloat(this,"AmPmCenterX",amPmCenterX,-amPmCenterX);
        ObjectAnimator alphaAnim = ObjectAnimator.ofInt(this,"TextAlpha",255,0);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(transAnim,amPmTransAnim,alphaAnim);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.setDuration(350);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                textRadius = initRadius;
                decoPaint.setTextSize(initTextSize);
                decoPaint.textPaint.setAlpha(255);
                decoPaint.AM_PM_Paint.setAlpha(255);
                isAnim = false;
                if(viewMode == HOUR_MODE){
                    viewMode = MINUTE_MODE;
                    angle = CalendarCalculator.transMinuteToAngle(minute);
                }else{
                    viewMode = HOUR_MODE;
                    angle = CalendarCalculator.transHourToAngle(hour);
                }
                //invalidate();
            }
        });
        animatorSet.start();
    }

    private void setTextRadius(float textRadius){
        this.textRadius = textRadius;
        invalidate();
    }

    private float getTextRadius(){
        return this.textRadius;
    }

    private void setTextAlpha(int textAlpha){
        if(textAlpha>255) textAlpha = 255;
        this.textAlpha = textAlpha;
        decoPaint.AM_PM_Paint.setAlpha(textAlpha);
        decoPaint.textPaint.setAlpha(textAlpha);
    }

    private int getTextAlpha(){
        return this.textAlpha;
    }

    private void setAmPmCenterX(float centerX){
        this.amPmCenterX = centerX;
    }

    private float getAmPmCenterX(){
        return this.amPmCenterX;
    }

    class DecoPaint extends Paint {
        private Context context;
        private TextPaint textPaint;
        private Paint AM_PM_Paint;
        private int pivotCircleColor,selectedCircleColor,lineColor,backgroundColor = Color.parseColor("#10000000");
        private PickerDimension pickerDimension;
        private float stringHeight;

        public DecoPaint(Context context){
            this.context = context;
            pickerDimension = PickerDimension.getInstance();
            setAntiAlias(true);
            setBackgroundCircleColor();
            setStrokeWidth(DIPManager.dip2px(1,context));
            setTextSize(pickerDimension.getTextPxSize(context));
            textPaint = new TextPaint();
            textPaint.setAntiAlias(true);
            textPaint.setTextSize(pickerDimension.getTextPxSize(context));

            AM_PM_Paint = new Paint();
            AM_PM_Paint.setAntiAlias(true);

            stringHeight = -Math.abs(textPaint.ascent())+Math.abs(textPaint.descent()) + Math.abs(textPaint.getFontMetrics().leading);
            pivotCircleColor = ContextCompat.getColor(context, R.color.colorAccent);
            selectedCircleColor = pivotCircleColor;
            float[] HSV = new float[3];
            Color.colorToHSV(selectedCircleColor,HSV);
            selectedCircleColor = Color.HSVToColor(100,HSV);
            lineColor = pivotCircleColor;
        }

        public DecoPaint setBackgroundCircleColor(){
            setColor(backgroundColor);
            return this;
        }

        public Paint setAM_PM_BackgroundCircleColor(){
            AM_PM_Paint.setColor(backgroundColor);
            return AM_PM_Paint;
        }

        public Paint setAM_PM_SelectedCircleColor(){
            AM_PM_Paint.setColor(selectedCircleColor);
            return AM_PM_Paint;
        }

        public DecoPaint setPivotCircleColor(){
            setColor(pivotCircleColor);
            return this;
        }

        public DecoPaint setSelectedCircleColor(){
            setColor(selectedCircleColor);
            return this;
        }

        public DecoPaint setLineColor(){
            setColor(lineColor);
            return this;
        }

        public TextPaint setTextColor(){
            //textPaint.setColor(Color.BLACK);
            //textPaint.setAlpha(100);
            return textPaint;
        }

        public float getStringWidth(String str){
            return measureText(str);
        }

        public float getStringHeight(){
            return stringHeight;
        }
    }
}
