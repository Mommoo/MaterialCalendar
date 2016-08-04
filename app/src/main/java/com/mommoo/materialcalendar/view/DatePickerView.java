package com.mommoo.materialcalendar.view;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.mommoo.materialcalendar.R;
import com.mommoo.materialcalendar.adapter.DatePickerViewPagerAdapter;
import com.mommoo.materialcalendar.toolkit.CalendarCalculator;
import com.mommoo.materialcalendar.toolkit.PickerDimension;

import java.util.Calendar;

/**
 * Created by mommoo on 2016-07-27.
 */
public class DatePickerView extends View{

    private int startWeek,targetPosition=-1,todayDate=-1;
    private boolean reDraw=true,isAnim,isClicked;
    private CalendarInfo calendarInfo;
    private CalendarCalculator calendarCalculator;
    private DecoPaint decoPaint = new DecoPaint(getContext());
    private int PADDING;
    private float area,radius,maxRadius;
    private float animX, animY,animCenterX,animCenterY;
    private String animDate;
    private Bitmap store;
    private Canvas storeCanvas;
    private TimeInterpolator overInter = new OvershootInterpolator();
    private DatePickerViewPagerAdapter.NotifyChangeData notifyChangeData;
    private NotifyClickedData notifyClickedData;

    public interface NotifyClickedData{
        public void notify(int year, int month, int date);
    }

    public void setNotifyClickedData(NotifyClickedData notifyClickedData){
        this.notifyClickedData = notifyClickedData;
    }

    public DatePickerView(Context context, CalendarInfo calendarInfo) {
        super(context);
        this.calendarInfo = calendarInfo;
        initialize(context);
    }

    public DatePickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public DatePickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    @TargetApi(21)
    public DatePickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context);
    }

    private void initialize(Context context){
        PickerDimension calendarDimension = PickerDimension.getInstance();
        PADDING = calendarDimension.getPadding(context);
        area = calendarDimension.getElementAreaSize(context);
        maxRadius = 7*area/10;
        store = Bitmap.createBitmap(calendarDimension.getContentWidth()
                ,calendarDimension.getContentHeight(), Bitmap.Config.ARGB_8888);
        storeCanvas = new Canvas(store);
        decoPaint.setTextSize(calendarDimension.getTextPxSize(context));
        calendarCalculator = new CalendarCalculator(calendarInfo.year,calendarInfo.month);
        startWeek = calendarCalculator.getStartWeekCount();

        Calendar cal = Calendar.getInstance();
        if(cal.get(Calendar.YEAR) == calendarInfo.year && cal.get(Calendar.MONTH)+1 == calendarInfo.month) todayDate = cal.get(Calendar.DATE);
    }

    public CalendarInfo getCalendarInfo(){
        return calendarInfo;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(reDraw){
            for(int i=0;i<6;i++){
                for(int j=0; j<7; j++) {
                    int position = (7 * i) + j;
                    int[] pointer = new int[]{i,j};
                    if (position >= startWeek && position < calendarCalculator.getTotalMonthDate() + startWeek){
                        if(targetPosition != position) drawWeekDayText(storeCanvas, pointer);
                        else setInfoForAnim(i,j);
                    }
                }
            }
            reDraw = false;
        }
        canvas.drawBitmap(store,0,0,null);
        if(isAnim){
            canvas.drawCircle(animCenterX,animCenterY,radius,decoPaint.setCircleColor());
            canvas.drawText(animDate,animX,animY,decoPaint.setInCircleTextColor());
        }
    }

    private void drawWeekDayText(Canvas canvas,int[] pointer){
        int position = (7*pointer[0]) + pointer[1];
        int date = position - startWeek +1;
        String dateString = Integer.toString(date);
        float x = (area *pointer[1])+(PADDING * (pointer[1]+1)) +((area - decoPaint.getStringWidth(dateString))/2);
        float y = (area*pointer[0])+(PADDING * (pointer[0]+1))+((area - decoPaint.getStringHeight())/2);// + decoPaint.getTextSize();
        if(todayDate>0){
            if(date == todayDate) canvas.drawText(date+"",x,y,decoPaint.setTodayColor());
            else canvas.drawText(dateString,x,y,position % 7 ==0?decoPaint.setHolidayColor():decoPaint.setWeekDayColor());
        }else{
            canvas.drawText(dateString,x,y,position % 7 ==0?decoPaint.setHolidayColor():decoPaint.setWeekDayColor());
        }

    }

    private void setInfoForAnim(int i , int j){
        int position = (7 * i) + j;
        animDate = (Integer.toString(position - startWeek +1));
        animX = (area *j)+(PADDING * (j+1)) +((area - decoPaint.getStringWidth(animDate))/2);
        animY = (area*i)+(PADDING * (i+1))+((area - decoPaint.getStringHeight())/2);
        animCenterX = (area *j)+(PADDING * (j+1)) + (area/2);
        animCenterY = (area*i)+(PADDING * (i+1)) + (area/2);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int rectWidth = (int)area+PADDING;
        int rectHeight = rectWidth;
        int colIndex = (int)(x/rectWidth);
        int rowIndex = (int)(y/rectHeight);
        int position = colIndex + (rowIndex*7);
        /**
         *
         *  달력 클릭시, 숫자영역만 클릭이 먹히도록
         *
         */
        boolean isArea = (rectWidth * colIndex)+(PADDING/2)<=x
                && x<=(rectWidth * (colIndex+1))+(PADDING/2)
                && (rectHeight * rowIndex)<=y
                && (rectHeight * (rowIndex+1))>=y
                && position >= startWeek
                && position < calendarCalculator.getTotalMonthDate() + startWeek;
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            if(isArea) {
                targetPosition = position;
            }
        } else if(event.getAction() == MotionEvent.ACTION_UP){
            if(isArea){
                notifyChangeData.notifyChangeDate(calendarInfo.year,calendarInfo.month,position - startWeek +1,-1);
                if(notifyClickedData != null) notifyClickedData.notify(calendarInfo.year,calendarInfo.month,targetPosition-startWeek+1);
                if(targetPosition == position) startClickAnim();
            }
        }
        return true;
    }

    public void startClickAnim(){
        reDraw = true;
        isAnim = true;
        isClicked = true;
        ObjectAnimator animator = ObjectAnimator.ofFloat(this,"Radius",3*area/10,maxRadius);
        animator.setInterpolator(overInter);
        animator.setDuration(150);
        animator.start();
    }

    private void setRadius(float radius){
        this.radius = radius;
        this.invalidate();
    }

    private float getRadius(){
        return radius;
    }

    public void setClickCircleColor(int color){
        decoPaint.circleColor = color;
    }

    public int getClickCircleColor(){
        return decoPaint.circleColor;
    }

    public void setClickInCircleTextColor(int color){
        decoPaint.inCircleTextColor = color;
    }
    public int getClickInCircleTextColor(){
        return decoPaint.inCircleTextColor;
    }

    public void setNotifyDataChange(DatePickerViewPagerAdapter.NotifyChangeData notifyDataChange){
        this.notifyChangeData = notifyDataChange;
    }

    public void setNotClickedState(){
        isAnim = false;
        reDraw = true;
        isClicked = false;
        targetPosition = -1;
        if(notifyClickedData != null) notifyClickedData.notify(0,0,0);
        invalidate();
    }

    public void setClickedState(int targetDate){
        isAnim = true;
        reDraw = true;
        isClicked = true;
        if(notifyClickedData != null) notifyClickedData.notify(calendarInfo.year,calendarInfo.month,targetDate);
        this.targetPosition = targetDate+startWeek-1;
        radius = maxRadius;
        invalidate();
    }

    public boolean isClicked(){
        return isClicked;
    }

    public static class CalendarInfo{
        private int year,month;
        public CalendarInfo(int year,int month){
            this.year = year;
            this.month = month;
        }

        public int getYear(){
            return year;
        }

        public int getMonth(){
            return month;
        }

    }

    public void destroy(){
        storeCanvas = null;
        store.recycle();
        store = null;
    }

    private class DecoPaint extends Paint {

        private float stringHeight;
        private int circleColor,todayColor;
        private int inCircleTextColor;

        public DecoPaint(Context context){
            setAntiAlias(true);
            int standardColor = ContextCompat.getColor(context, R.color.colorAccent);
            circleColor = com.mommoo.materialcalendar.toolkit.Color.lighter(standardColor);
            todayColor = com.mommoo.materialcalendar.toolkit.Color.darker(standardColor);
            inCircleTextColor = Color.WHITE;
        }

        @Override
        public void setTextSize(float textSize) {
            super.setTextSize(textSize);
            stringHeight = -Math.abs(ascent())+Math.abs(descent()) + Math.abs(getFontMetrics().leading);
        }

        public Paint setCircleColor(){
            setColor(circleColor);
            return this;
        }

        public Paint setInCircleTextColor(){
            setColor(inCircleTextColor);
            return this;
        }

        public Paint setHolidayColor(){
            setColor(Color.parseColor("#D50000"));
            return this;
        }

        public Paint setWeekDayColor(){
            setColor(Color.BLACK);
            return this;
        }

        public Paint setTodayColor(){
            setColor(todayColor);
            return this;
        }

        public float getStringWidth(String date){
            return measureText(date);
        }

        public float getStringHeight(){
            return stringHeight;
        }
    }
}
