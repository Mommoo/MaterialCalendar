package com.mommoo.materialpicker;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;

import com.mommoo.materialpicker.adapter.DdayPickerListViewAdapter;
import com.mommoo.materialpicker.adapter.TimePickerListViewAdapter;
import com.mommoo.materialpicker.helper.NotifyListViewAdapter;
import com.mommoo.materialpicker.helper.Picker;
import com.mommoo.materialpicker.manager.DIPManager;
import com.mommoo.materialpicker.toolkit.CalendarCalculator;
import com.mommoo.materialpicker.toolkit.PickerDimension;
import com.mommoo.materialpicker.widget.CircleImageView;
import com.mommoo.materialpicker.widget.NotifyListView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by mommoo on 2016-08-10.
 */
public class AlarmPicker extends Picker implements NotifyListViewAdapter.DataChanged{

    private int d_day,am_pm,hour,minute;
    private Calendar calendar = Calendar.getInstance();
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
        this.calendar = calendar;
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
            for(int i=0,size = views.size();i<size;i++){
                View view = views.get(i);
                if(i==0 || i==2) ((CircleImageView)view).setCircleBackgroundColor(color);
                else view.setBackgroundColor(color);
            }
        }
    }

    public void initialize(Context context){

        this.hour = calendar.get(Calendar.HOUR);
        this.minute = calendar.get(Calendar.MINUTE);
        this.am_pm = calendar.get(Calendar.AM_PM);

        setThemeColor(ContextCompat.getColor(context,R.color.colorAccent));
        preventChangeWidth(true);
        setDialogTitleSize(TypedValue.COMPLEX_UNIT_SP,getDialogWidth()/45);
        pickBtn.setBackgroundColor(Color.TRANSPARENT);
        PickerDimension pickerDimension = PickerDimension.getInstance();
        int viewHeight = 4*pickerDimension.getContentHeight()/5;
        super.forceChangeContentHeight(viewHeight);
        int padding = DIPManager.dip2px(20,getContext());
        int viewWidth = pickerDimension.getContentWidth()/4;

        FrameLayout frameLayout = new FrameLayout(context);
        for(int i=0; i<4; i++){
            pickListView[i] = new NotifyListView(context);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(viewWidth, viewHeight-(padding*2));
            params.setMargins(0,padding,0,0);
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
        pickListView[3].setX(0); pickListView[3].setSelection(100+d_day); d_dayPickerListViewAdapter.notifyDataSetChanged(100+d_day);
        pickListView[0].setX(viewWidth); pickListView[0].setSelection(am_pm); timePickerListViewAdapter[0].notifyDataSetChanged(am_pm);
        pickListView[1].setX(viewWidth*2); pickListView[1].setSelection(hour-1); timePickerListViewAdapter[1].notifyDataSetChanged(hour-1);
        pickListView[2].setX(viewWidth*3); pickListView[2].setSelection(minute); timePickerListViewAdapter[2].notifyDataSetChanged(minute);



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
                lines[i].setX((viewWidth/4) + i*(viewWidth));
                lines[i].setY(padding + itemHeight*2);
            } else{
                lines[i].setX((viewWidth/4) + (i-4)*(viewWidth));
                lines[i].setY(padding + itemHeight*3);
            }
            frameLayout.addView(lines[i]);
            views.add(lines[i]);
        }

        setDialogContentView(frameLayout);
    }

    private View getLineAppearance(int viewWidth){
        View view = new View(getContext());
        view.setBackgroundColor(com.mommoo.materialpicker.toolkit.Color.lighten(getThemeColor(),0.2f));
        view.setLayoutParams(new FrameLayout.LayoutParams(viewWidth/2, DIPManager.dip2px(1,getContext())));
        return view;
    }

    private void setDialogInfoText(String title,String statusTitle){
        super.setDialogTitle(title);
        super.setDialogStatusTitle(statusTitle);
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
        String statusTitle = calendar.get(Calendar.YEAR)+". "+(calendar.get(Calendar.MONTH)+1)+". "+calendar.get(Calendar.DATE)+". ("+days[calendar.get(Calendar.DAY_OF_WEEK)-1]+")";
        String dDay;
        if(d_day==0) dDay = "Today";
        else dDay = "D"+(d_day>-1?"+ ":" ")+d_day;
        String title = dDay+"\n"+am_pmStrings[am_pm]+" "+hour+" : "+(minute<10?"0":"")+minute;
        setDialogInfoText(title,statusTitle);
    }
}
