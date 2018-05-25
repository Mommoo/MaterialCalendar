package com.mommoo.materialpicker;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;


import java.util.Calendar;

/**
 * Created by mommoo on 2016-08-01.
 */
public final class TimePicker extends Picker {

    private Calendar calendar = Calendar.getInstance();
    private int hour,minute,am_pm;
    private String[] am_pmStrings = CalendarCalculator.getAM_PM();
    public static final int HOUR_MODE = 0;
    public static final int MINUTE_MODE = 1;
    private int viewMode;
    private TimePickerView pickerView;
    private OnAcceptListener onAcceptListener;
    private OnTimeSet onTimeSet;
    private boolean isTime = true;

    public interface OnTimeSet{
        public void onTime(boolean isAccept, int hour, int minute, int am_pm);
    }

    public TimePicker(Context context) {
        super(context);
        initialize(context,calendar.get(Calendar.HOUR),calendar.get(Calendar.MINUTE),calendar.get(Calendar.AM_PM));
    }

    public TimePicker(Context context,int am_pm, int hour, int minute){
        super(context);
        if(am_pm>1) am_pm = 1;
        calendar.set(Calendar.AM_PM,am_pm);
        calendar.set(Calendar.HOUR,hour);
        calendar.set(Calendar.MINUTE,minute);
        initialize(context,calendar.get(Calendar.HOUR),calendar.get(Calendar.MINUTE),calendar.get(Calendar.AM_PM));
    }

    private TimePicker(Context context, int themeResId) {
        super(context, themeResId);
    }

    private TimePicker(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    private void initialize(Context context,int hour,int minute,int am_pm){
        int textSize = DIPManager.px2dip(9*getDialogWidth()/40,getContext());
        setDialogTitleSize(TypedValue.COMPLEX_UNIT_SP,textSize);
        /** if dialog width changed, execute this listener */
        setOnDialogWidthChanged(new OnDialogWidthChanged() {
            @Override
            public void changed(int width) {
                int textSize = DIPManager.px2dip(9*width/40,getContext());
                setDialogTitleSize(TypedValue.COMPLEX_UNIT_SP,textSize);
            }
        });
        pickBtn.setImageResource(R.mipmap.swap);
        setData(calendar);
        pickerView = new TimePickerView(context, HOUR_MODE, calendar);

        pickerView.setNotifyChanged(new TimePickerView.NotifyChanged() {
            @Override
            public void notify(int hour, int minute, int am_pm) {
                TimePicker.this.hour = hour;
                TimePicker.this.minute = minute;
                TimePicker.this.am_pm = am_pm;

                String time = hour+" : "+(minute<10?"0":"")+minute;
                if(!time.equals(getDialogTitle())) doVibration();
                setDialogStatusTitle(am_pmStrings[am_pm]);
                setDialogTitle(time);
            }

            @Override
            public void vibrate() {
                doVibration();
            }
        });
        View view = makeContentView(context);
        setDialogContentView(view);
        saveContentView();

        ClipAnimLayout.Builder builder = makeBuilder();
        ScrollTimePickerView scrollTimePickerView = makeScrollTimePickerView(context);
        scrollTimePickerView.setBuilder(builder);
        saveContentView(1,scrollTimePickerView);
        ((ClipAnimLayout)view).setBuilder(builder);

        setTitle(calendar.get(Calendar.AM_PM),hour,minute);

        setOnPickBtnListener(new OnPickBtnListener() {
            private int swapResId = R.mipmap.swap;
            private int timeResId = R.mipmap.time;

            @Override
            public void onClick(View view, final FrameLayout decoView) {

                ClipAnimLayout layout = null;

                if (!isTime) {
                    layout = (ClipAnimLayout) getSavedContentView(0);
                    pickerView.setData(TimePicker.this.am_pm,TimePicker.this.hour,TimePicker.this.minute);
                } else {
                    layout = (ClipAnimLayout) getSavedContentView(1);
                    ((ScrollTimePickerView)layout).setData(TimePicker.this.am_pm,TimePicker.this.hour,TimePicker.this.minute);
                }
                layout.setX(pickerDimension.getContentX());
                layout.setY(pickerDimension.getContentY());
                decoView.addView(layout);

                layout.startAnim();

                isTime = !isTime;
                ((ImageView) view).setImageResource(isTime?swapResId:timeResId);
            }
        });
    }

    private void setData(Calendar calendar){
        this.am_pm = calendar.get(Calendar.AM_PM);
        this.hour = (calendar.get(Calendar.HOUR))==0?12:calendar.get(Calendar.HOUR);
        this.minute = calendar.get(Calendar.MINUTE);
    }

    private View makeContentView(Context context){
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        /** add paddingView */
        linearLayout.addView(new View(context),new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DIPManager.dip2px(16,context)));
        linearLayout.addView(pickerView);
        ClipAnimLayout layout = new ClipAnimLayout(context,new ClipAnimLayout.Builder());
        layout.addView(linearLayout);
        return layout;
    }

    private ClipAnimLayout.Builder makeBuilder(){
        int radius = pickerDimension.getPickBtnAnimCircleRadius();
        ClipAnimLayout.Builder builder = new ClipAnimLayout.Builder();
        builder.setAnimDuration(400).setStartLocation((pickerDimension.getContentWidth()) / 2
                , pickerDimension.getContentHeight() / 2)
                .setStartRadius(radius).setTimeInterpolator(new AccelerateInterpolator())
                .setAnimListener(new ClipAnimLayout.Builder.AnimListener() {
                    @Override
                    public void onAnimEnd(View view) {
                        getDecoView().removeView(view);
                        saveContentView();
                        setDialogContentView(view);
                        pickBtnClick = false;
                    }
                });
        return builder;
    }

    private ScrollTimePickerView makeScrollTimePickerView(Context context){
        int contentHeight = pickerDimension.getContentHeight();
        int contentWidth = pickerDimension.getContentWidth();
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(contentWidth, contentHeight);
        Calendar tempCalendar = Calendar.getInstance();
        tempCalendar.set(Calendar.AM_PM,TimePicker.this.am_pm);
        tempCalendar.set(Calendar.HOUR,TimePicker.this.hour);
        tempCalendar.set(Calendar.MINUTE,TimePicker.this.minute);
        ScrollTimePickerView layout = new ScrollTimePickerView(context,tempCalendar);
        layout.setPickChanged(new ScrollTimePickerView.PickChanged() {
            @Override
            public void dataSet(int am_pm, int hour, int minute) {
                TimePicker.this.am_pm = am_pm;
                TimePicker.this.hour = hour;
                TimePicker.this.minute = minute;
                setTitle(am_pm,hour,minute);
            }
        });
        layout.setLayoutParams(params);
        layout.setX(pickerDimension.getContentX());
        layout.setY(pickerDimension.getContentY());
        return layout;
    }

    public void setScrollMode(boolean scrollMode){
        super.setScrollMode(scrollMode);
        isTime = !scrollMode;
        pickBtn.setImageResource(scrollMode ? R.mipmap.time : R.mipmap.swap);
    }

    @Override
    public void setThemeColor(int color) {
        super.setThemeColor(color);
        if(pickerView != null) pickerView.setThemeColor(color);
    }

    public void setTime(int am_pm, int hour, int minute){
        if(am_pm>1) am_pm = 1;
        this.am_pm = am_pm;
        this.hour = hour;
        this.minute = minute;
        if(pickerView != null) pickerView.setData(am_pm,hour,minute);
        setTitle(am_pm,hour,minute);
    }

    public void setTitle(int am_pm, int hour, int minute){
        String time = hour+" : "+(minute<10?"0":"")+minute;
        setDialogStatusTitle(am_pmStrings[am_pm]);
        setDialogTitle(time);
    }

    public void setOnAcceptListener(OnAcceptListener acceptListener) {
        this.onAcceptListener = acceptListener;
        super.callBackAcceptListener = new CallBackAcceptListener() {
            @Override
            public void callBack(boolean isAccept) {
                if (onTimeSet != null) onTimeSet.onTime(isAccept, hour, minute, am_pm);
            }

            @Override
            public void accept() {
                TimePicker.this.onAcceptListener.accept();
            }
        };
    }

    public void setOnTimeSet(OnTimeSet onTimeSet) {
        this.onTimeSet = onTimeSet;
        super.callBackAcceptListener = new CallBackAcceptListener() {
            @Override
            public void callBack(boolean isAccept) {
                TimePicker.this.onTimeSet.onTime(isAccept, hour, minute, am_pm);
            }

            @Override
            public void accept() {
                if (TimePicker.this.onAcceptListener != null) onAcceptListener.accept();
            }
        };
    }

    public void setOnDeclineListener(final OnDeclineListener declineListener) {
        super.callBackDeclineListener = new CallBackDeclineListener() {
            @Override
            public void callBack(boolean isAccept) {
                if (onTimeSet != null) onTimeSet.onTime(isAccept, hour, minute, am_pm);
            }

            @Override
            public void decline() {
                declineListener.decline();
            }
        };
    }

    public void setMode(int viewMode){
        this.viewMode = viewMode;
        pickerView.changeViewMode(viewMode);
    }

    public int getMode(){
        return viewMode;
    }

}
