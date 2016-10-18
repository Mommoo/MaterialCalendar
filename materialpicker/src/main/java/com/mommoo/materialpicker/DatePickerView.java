package com.mommoo.materialpicker;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import java.util.Calendar;

/**
 * Created by mommoo on 2016-07-27.
 */
class DatePickerView extends View{

    private int startWeek,targetPosition=-1,todayDate=-1;
    private boolean reDraw=true,isAnim,cancel;
    private CalendarCalculator calendarCalculator;

    private int PADDING,year,month,date;
    private float area,radius,maxRadius;
    private float animX, animY,animCenterX,animCenterY;
    private String animDate;
    private Bitmap store;
    private Canvas storeCanvas;

    private DatePickerViewPagerAdapter.NotifyChangeData notifyChangeData;
    private NotifyClickedData notifyClickedData;

    private static final TimeInterpolator OVER_INTERPOLATOR;
    private static final Calendar CALCULATE_CALENDAR;
    private static final DecoPaint DECO_PAINT;
    static{
        OVER_INTERPOLATOR = new OvershootInterpolator();
        CALCULATE_CALENDAR = Calendar.getInstance();
        DECO_PAINT = new DecoPaint();
    }

    public interface NotifyClickedData{
        public void notify(DatePickerView dpv);
    }

    public void setNotifyClickedData(NotifyClickedData notifyClickedData){
        this.notifyClickedData = notifyClickedData;
    }

    public DatePickerView(Context context, int year, int month) {
        super(context);
        this.year = year;
        this.month = month;
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

    private void initialize(final Context context){
        final PickerDimension calendarDimension = PickerDimension.getInstance();
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                if(DECO_PAINT.themeColor == -1){
                    int standardColor = ContextCompat.getColor(context, R.color.colorAccent);
                    setThemeColor(standardColor);
                }

                PADDING = calendarDimension.getPadding(context);
                area = calendarDimension.getElementAreaSize(context);
                maxRadius = 7*area/10;

                store = Bitmap.createBitmap(calendarDimension.getContentWidth()
                        ,calendarDimension.getContentHeight(), Bitmap.Config.ARGB_8888);
                storeCanvas = new Canvas(store);
            }
        };
        thread.start();

        DECO_PAINT.setTextSize(calendarDimension.getTextPxSize(context));
        calendarCalculator = new CalendarCalculator(year,month);
        startWeek = calendarCalculator.getStartWeekCount();

        if(CALCULATE_CALENDAR.get(Calendar.YEAR) == year && CALCULATE_CALENDAR.get(Calendar.MONTH)+1 == month) todayDate = CALCULATE_CALENDAR.get(Calendar.DATE);
        try{
            thread.join();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public int getYear(){
        return year;
    }

    public int getMonth(){
        return month;
    }

    public void setDate(int date){
        this.date = date;
    }

    public int getDate(){
        return date;
    }

    public static void setThemeColor(int color){
        DECO_PAINT.setThemeColor(color);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(reDraw) drawBackground();
        else {
            canvas.drawBitmap(store,0,0,null);
            if(isAnim){
                canvas.drawCircle(animCenterX,animCenterY,radius,DECO_PAINT.setEraseColor());
                canvas.drawCircle(animCenterX,animCenterY,radius,DECO_PAINT.setCircleColor());
                canvas.drawText(animDate,animX,animY,DECO_PAINT.setInCircleTextColor());
            }
        }
    }
    /** draw Calendar in backgroundThread  */
    private void drawBackground(){
        new AsyncTask<Void,Void,Void>(){

            private void drawWeekDayText(Canvas canvas,int[] pointer){
                int position = (7*pointer[0]) + pointer[1];
                int date = position - startWeek +1;
                String dateString = Integer.toString(date);
                float x = (area *pointer[1])+(PADDING * (pointer[1]+1)) +((area - DECO_PAINT.getStringWidth(dateString))/2);
                float y = (area *pointer[0])+(PADDING * (pointer[0]+1))+((area - DECO_PAINT.getStringHeight())/2);// + decoPaint.getTextSize();
                if(todayDate>0){
                    if(date == todayDate) canvas.drawText(date+"",x,y,DECO_PAINT.setTodayColor());
                    else canvas.drawText(dateString,x,y,position % 7 ==0?DECO_PAINT.setHolidayColor():DECO_PAINT.setWeekDayColor());
                }else{
                    canvas.drawText(dateString,x,y,position % 7 ==0?DECO_PAINT.setHolidayColor():DECO_PAINT.setWeekDayColor());
                }
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try{
                    for(int i=0;i<6;i++){
                        for(int j=0; j<7; j++) {
                            int position = (7 * i) + j;
                            int[] pointer = new int[]{i,j};
                            if (position >= startWeek && position < calendarCalculator.getTotalMonthDate() + startWeek){
                                drawWeekDayText(storeCanvas, pointer);
                            }
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }

                reDraw = false;
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                invalidate();
            }
        }.execute();
    }
    /** set Information for notify coordinate to touch Animation */
    private void setInfoForAnim(int position){
        int row = position/7;
        int col = position%7;
        animDate = (Integer.toString(getDate()));
        animX = (area *col)+(PADDING * (col+1)) +((area - DECO_PAINT.getStringWidth(animDate))/2);
        animY = (area*row)+(PADDING * (row+1))+((area - DECO_PAINT.getStringHeight())/2);
        animCenterX = (area *col)+(PADDING * (col+1)) + (area/2);
        animCenterY = (area*row)+(PADDING * (row+1)) + (area/2);
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

        /** if touch the view, only apply code in date area. */
        boolean isArea = (rectWidth * colIndex)+(PADDING/2)<=x
                && x<=(rectWidth * (colIndex+1))+(PADDING/2)
                && (rectHeight * rowIndex)<=y
                && (rectHeight * (rowIndex+1))>=y
                && position >= startWeek
                && position < calendarCalculator.getTotalMonthDate() + startWeek;
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            if(isArea) {
                cancel = false;
                targetPosition = position;
            }
        } else if(event.getAction() == MotionEvent.ACTION_MOVE){
            if(targetPosition != position){
                cancel = true;
            }
        } else if(event.getAction() == MotionEvent.ACTION_UP){
            if(isArea&&!cancel){
                setDate(targetPosition - startWeek +1);
                if(notifyChangeData  != null) notifyChangeData.notifyChangeDate(year,month,getDate(),-1);
                if(notifyClickedData != null) notifyClickedData.notify(this);
                setInfoForAnim(targetPosition);
                startClickAnim();
            }
        }
        return true;
    }
    /** touch animation */
    public void startClickAnim(){
        isAnim = true;
        ObjectAnimator animator = ObjectAnimator.ofFloat(this,"Radius",3*area/10,maxRadius);
        animator.setInterpolator(OVER_INTERPOLATOR);
        animator.setDuration(150);
        animator.start();
    }
    /**
     * needed to objectAnimation
     * setRadius(),getRadius();
     */
    private void setRadius(float radius){
        this.radius = radius;
        this.invalidate();
    }

    private float getRadius(){
        return radius;
    }

    public void setNotifyDataChange(DatePickerViewPagerAdapter.NotifyChangeData notifyDataChange){
        this.notifyChangeData = notifyDataChange;
    }
    /** force to drawCircle checking Date  */
    public void setCheckedDate(boolean checkedDate){
        isAnim = checkedDate;
        if(checkedDate){
            targetPosition = getDate()+startWeek-1;
            if(notifyClickedData != null) notifyClickedData.notify(this);
            setInfoForAnim(targetPosition);
            radius = maxRadius;
        }else{
            targetPosition = -1;
        }
        invalidate();
    }

    public void destroy(){
        storeCanvas = null;
        store.recycle();
        store = null;
    }
    /** customPaint this class is only one instance by static field */
    private static class DecoPaint extends Paint {

        private float stringHeight;
        private int circleColor,todayColor;
        private int inCircleTextColor;
        private int themeColor;

        public DecoPaint(){
            setAntiAlias(true);
            inCircleTextColor = Color.WHITE;
        }

        private void setThemeColor(int color){
            this.themeColor = color;
            float[] HSV = new float[3];
            Color.colorToHSV(color,HSV);
            circleColor = Color.HSVToColor(100,HSV);
            todayColor = com.mommoo.materialpicker.Color.darker(color);
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

        public Paint setEraseColor(){
            setColor(Color.WHITE);
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
