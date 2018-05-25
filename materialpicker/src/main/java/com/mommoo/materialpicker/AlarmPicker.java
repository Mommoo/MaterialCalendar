package com.mommoo.materialpicker;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;


import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by mommoo on 2016-08-10.
 */
public final class AlarmPicker extends Picker implements NotifyListViewAdapter.DataChanged{

    private int d_day,am_pm,hour,minute;
    private Calendar calendar = Calendar.getInstance();
    private final static StringBuilder STRING_BUILDER = new StringBuilder();
    private NotifyListView[] pickListView = new NotifyListView[4];
    private TimePickerListViewAdapter[] timePickerListViewAdapter = new TimePickerListViewAdapter[3];
    private DdayPickerListViewAdapter d_dayPickerListViewAdapter;
    private String[] days = CalendarCalculator.getDays();
    private String[] am_pmStrings = CalendarCalculator.getAM_PM();
    private OnAcceptListener onAcceptListener;
    private OnDeclineListener onDeclineListener;
    private OnAlarmSet alarmSet;
    private ArrayList<View> views = new ArrayList<>();

    public interface OnAlarmSet{
        public void onAlarm(boolean isAccept,int dDay,int am_pm,int hour, int minute);
    }

    public AlarmPicker(Context context) {
        super(context);
        this.d_day = 0;
        initialize(context);
    }

    public AlarmPicker(Context context,int d_day,Calendar calendar){
        super(context);
        this.d_day = d_day;
        this.calendar.setTimeInMillis(calendar.getTimeInMillis());
        initialize(context);
    }

    public AlarmPicker(Context context,int d_day,int am_pm,int hour, int minute){
        super(context);
        this.d_day = d_day;
        calendar.set(Calendar.AM_PM,am_pm);
        calendar.set(Calendar.HOUR,hour==12?0:hour);
        calendar.set(Calendar.MINUTE,minute);
        calendar.add(Calendar.DATE,d_day);
        initialize(context);
    }

    private AlarmPicker(Context context, int themeResId) {
        super(context, themeResId);
        initialize(context);
    }

    protected AlarmPicker(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initialize(context);
    }

    @Override
    public void setThemeColor(int color) {
        super.setThemeColor(color);
        pickBtn.setBackgroundColor(Color.TRANSPARENT);
        if(views != null &&views.size() !=0 ){
            int lightenColor = com.mommoo.materialpicker.Color.applyAlpha(color,100);
            for(int i=0,size = views.size();i<size;i++){
                View view = views.get(i);
                if(i==0 || i==2) ((CircleImageView)view).setCircleBackgroundColor(color);
                else view.setBackgroundColor(lightenColor);
            }
        }
    }

    public void initialize(Context context){

        this.hour = calendar.get(Calendar.HOUR);
        this.minute = calendar.get(Calendar.MINUTE);
        this.am_pm = calendar.get(Calendar.AM_PM);

        setThemeColor(ContextCompat.getColor(context,R.color.colorAccent));
        preventChangeWidth(true);

        int textSize = DIPManager.px2dip(getDialogWidth()/10,context);
        setDialogTitleSize(TypedValue.COMPLEX_UNIT_SP,textSize);

        pickBtn.setBackgroundColor(Color.TRANSPARENT);
        PickerDimension pickerDimension = PickerDimension.getInstance();
        int viewHeight = 7*pickerDimension.getContentHeight()/10;
        super.forceChangeContentHeight(viewHeight);
        int padding = DIPManager.dip2px(20,getContext());
        int viewWidth = pickerDimension.getContentWidth()/4;

        FrameLayout frameLayout = new FrameLayout(context);
        for(int i=0; i<4; i++){
            pickListView[i] = new NotifyListView(context);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(viewWidth, viewHeight);
            //params.setMargins(0,padding,0,0);
            pickListView[i].setLayoutParams(params);
            frameLayout.addView(pickListView[i]);
            if(i<3) {
                timePickerListViewAdapter[i] = new TimePickerListViewAdapter(context,i);
                timePickerListViewAdapter[i].setDataChanged(this);
                pickListView[i].setAdapter(timePickerListViewAdapter[i]);
            }
            else{
                d_dayPickerListViewAdapter = new DdayPickerListViewAdapter(context);
                d_dayPickerListViewAdapter.setDataChanged(this);
                pickListView[i].setAdapter(d_dayPickerListViewAdapter);
            }
        }
        pickListView[3].setX(0);
        pickListView[0].setX(viewWidth);
        pickListView[1].setX(viewWidth*2);
        pickListView[2].setX(viewWidth*3);
        applyTime();

        CircleImageView[] dots = new CircleImageView[2];

        View[] lines = new View[8];
        int itemHeight = (viewHeight -(2*padding))/5;
        for(int i=0,size=lines.length;i<size;i++){
            lines[i] = getLineAppearance(viewWidth);
            if(i<2){
                dots[i] = new CircleImageView(context);
                dots[i].setCircleBackgroundColor(getThemeColor());
                dots[i].setLayoutParams(new FrameLayout.LayoutParams(viewWidth/15, viewWidth/15));
                dots[i].setX((3*viewWidth)-viewWidth/30);
                if(i==0)dots[i].setY(padding + 7*itemHeight/3);
                else dots[i].setY(padding + 8*itemHeight/3);
                frameLayout.addView(dots[i]);
                views.add(dots[i]);
            }
            if(i<4){
                lines[i].setX((2*viewWidth/10) + i*(viewWidth));
                lines[i].setY((2*padding/3) + itemHeight*2);
            } else{
                lines[i].setX((2*viewWidth/10) + (i-4)*(viewWidth));
                lines[i].setY((4*padding/3) + itemHeight*3);
            }
            frameLayout.addView(lines[i]);
            views.add(lines[i]);
        }

        setDialogContentView(frameLayout);
    }

    private View getLineAppearance(int viewWidth){
        View view = new View(getContext());
        view.setLayoutParams(new FrameLayout.LayoutParams(3*viewWidth/5, DIPManager.dip2px(2,getContext())));
        return view;
    }

    private void setDialogInfoText(String title,String statusTitle){
        super.setDialogTitle(title);
        super.setDialogStatusTitle(statusTitle);
    }


    public void setDate(int year, int month, int date){
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.DATE,date);
        callBack(-1,-1);
    }

    public void setTime(int dDay,int am_pm, int hour, int minute){
        this.d_day = dDay;
        this.am_pm = am_pm;
        this.hour = hour;
        this.minute = minute;
        applyTime();
    }

    private void applyTime(){
        pickListView[3].setSelection(100+d_day); d_dayPickerListViewAdapter.notifyDataSetChanged(100+d_day);
        pickListView[0].setSelection(am_pm); timePickerListViewAdapter[0].notifyDataSetChanged(am_pm);
        pickListView[1].setSelection(hour-1); timePickerListViewAdapter[1].notifyDataSetChanged(hour-1);
        pickListView[2].setSelection(minute); timePickerListViewAdapter[2].notifyDataSetChanged(minute);
    }

    public void setOnAcceptListener(OnAcceptListener acceptListener) {
        this.onAcceptListener = acceptListener;
        super.callBackAcceptListener = new CallBackAcceptListener() {
            @Override
            public void callBack(boolean isAccept) {
                if (alarmSet != null) alarmSet.onAlarm(isAccept, d_day,am_pm ,hour, minute);
            }

            @Override
            public void accept() {
                onAcceptListener.accept();
            }
        };
    }

    public void setOnAlarmSet(OnAlarmSet alarmSet) {
        this.alarmSet = alarmSet;
        super.callBackAcceptListener = new CallBackAcceptListener() {
            @Override
            public void callBack(boolean isAccept) {
                AlarmPicker.this.alarmSet.onAlarm(isAccept, d_day,am_pm ,hour, minute);
            }

            @Override
            public void accept() {
                if (onAcceptListener != null) onAcceptListener.accept();
            }
        };
    }

    public void setOnDeclineListener(final OnDeclineListener declineListener) {
        super.callBackDeclineListener = new CallBackDeclineListener() {
            @Override
            public void callBack(boolean isAccept) {
                if (alarmSet != null) alarmSet.onAlarm(isAccept, d_day,am_pm ,hour, minute);
            }

            @Override
            public void decline() {
                declineListener.decline();
            }
        };
    }

    @Override
    public void callBack(int tag, int value){
        if(tag==0) {
            am_pm = value;
            calendar.set(Calendar.AM_PM,am_pm);
        }
        if(tag==1) {
            hour = value;
            calendar.set(Calendar.HOUR,hour==12?0:hour);
        }
        if(tag==2){
            minute = value;
            calendar.set(Calendar.MINUTE,minute);
        }
        if(tag==3) {
            calendar.add(Calendar.DATE,value-d_day);
            d_day = value;
        }
        STRING_BUILDER.append(calendar.get(Calendar.YEAR)).append(". ")
                .append((calendar.get(Calendar.MONTH)+1)).append(". ")
                .append(calendar.get(Calendar.DATE)).append(". (")
                .append(days[calendar.get(Calendar.DAY_OF_WEEK)-1]).append(")");
        String statusTitle = STRING_BUILDER.toString();
        STRING_BUILDER.delete(0,statusTitle.length());
        if(d_day==0) STRING_BUILDER.append("Today");
        else STRING_BUILDER.append("D").append((d_day>-1?"+ ":" ")).append(d_day);
        STRING_BUILDER.append("\n").append(am_pmStrings[am_pm]).append(" ")
                .append(hour).append(" : ").append(minute<10?"0":"").append(minute);
        String title = STRING_BUILDER.toString();
        STRING_BUILDER.delete(0,title.length());
        setDialogInfoText(title,statusTitle);
    }
}
