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

import com.mommoo.materialpicker.helper.Picker;
import com.mommoo.materialpicker.manager.DIPManager;
import com.mommoo.materialpicker.toolkit.CalendarCalculator;
import com.mommoo.materialpicker.view.ScrollTimePickerView;
import com.mommoo.materialpicker.view.TimePickerView;
import com.mommoo.materialpicker.widget.ClipAnimLayout;

import java.util.Calendar;

/**
 * Created by mommoo on 2016-08-01.
 */
public class TimePicker extends Picker {

    private Calendar calendar = Calendar.getInstance();
    private int hour,minute,am_pm;
    private String[] am_pmStrings = CalendarCalculator.getAM_PM();
    public static final int HOUR_MODE = 0;
    public static final int MINUTE_MODE = 1;
    private int viewMode;
    private TimePickerView pickerView;
    private OnAcceptListener onAcceptListener;
    private OnTimeSet onTimeSet;
    private boolean isTime;

    public interface OnTimeSet{
        public void onTime(boolean isAccept, int hour, int minute, int am_pm);
    }

    public TimePicker(Context context) {
        super(context);
        initialize(context);
    }

    public TimePicker(Context context, int themeResId) {
        super(context, themeResId);
        initialize(context);
    }

    protected TimePicker(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initialize(context);
    }

    private void initialize(Context context){
        setDialogTitleSize(TypedValue.COMPLEX_UNIT_SP,getDialogWidth()/18);
        setOnDialogWidthChanged(new OnDialogWidthChanged() {
            @Override
            public void changed(int width) {
                System.out.println(width);
                setDialogTitleSize(TypedValue.COMPLEX_UNIT_SP,width/18);
            }
        });
        pickBtn.setImageResource(R.mipmap.swap);
        this.am_pm = calendar.get(Calendar.AM_PM);
        this.hour = calendar.get(Calendar.HOUR);
        this.minute = calendar.get(Calendar.MINUTE);
        pickerView = new TimePickerView(context, HOUR_MODE, calendar);
        TimePickerView.NotifyChanged notifyChanged = new TimePickerView.NotifyChanged() {
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
        };
        pickerView.setNotifyChanged(notifyChanged);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(new View(context),new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DIPManager.dip2px(16,context)));
        linearLayout.addView(pickerView);
        ClipAnimLayout layout = new ClipAnimLayout(context,new ClipAnimLayout.Builder());
        layout.addView(linearLayout);
        setDialogContentView(layout);
        this.hour = calendar.get(Calendar.HOUR)==0?12:calendar.get(Calendar.HOUR);
        this.minute = calendar.get(Calendar.MINUTE);
        inputDialogTitle(calendar.get(Calendar.AM_PM),hour,minute);

        setOnPickBtnListener(new OnPickBtnListener() {
            @Override
            public void onClick(View view, final FrameLayout decoView) {
                int resId = 0;
                if (isTime) resId = R.mipmap.swap;
                else resId = R.mipmap.time;
                ((ImageView) view).setImageResource(resId);
                boolean isSavedView = getSavedContentView(1) != null;
                ClipAnimLayout layout = null;

                if (isTime) {
                    layout = (ClipAnimLayout) getSavedContentView(0);
                    pickerView.setData(am_pm,hour,minute);
                } else {
                    if (!isSavedView) {
                        int radius = pickerDimension.getPickBtnAnimCircleRadius();
                        ClipAnimLayout.Builder builder = new ClipAnimLayout.Builder();
                        builder.setAnimDuration(400).setStartLocation((pickerDimension.getContentWidth()) / 2
                                , pickerDimension.getContentHeight() / 2)
                                .setStartRadius(radius).setTimeInterpolator(new AccelerateInterpolator())
                                .setAnimListener(new ClipAnimLayout.Builder.AnimListener() {
                                    @Override
                                    public void onAnimEnd(View view) {
                                        decoView.removeView(view);
                                        saveContentView();
                                        setDialogContentView(view);
                                        pickBtnClick = false;
                                    }
                                });
                        int contentHeight = pickerDimension.getContentHeight();
                        int contentWidth = pickerDimension.getContentWidth();
                        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(contentWidth, contentHeight);
                        Calendar tempCalendar = Calendar.getInstance();
                        tempCalendar.set(Calendar.AM_PM,TimePicker.this.am_pm);
                        tempCalendar.set(Calendar.HOUR,TimePicker.this.hour);
                        tempCalendar.set(Calendar.MINUTE,TimePicker.this.minute);
                        layout = new ScrollTimePickerView(decoView.getContext(),tempCalendar ,builder);
                        ((ScrollTimePickerView)layout).setPickChanged(new ScrollTimePickerView.PickChanged() {
                            @Override
                            public void dataSet(int am_pm, int hour, int minute) {
                                TimePicker.this.am_pm = am_pm;
                                TimePicker.this.hour = hour;
                                TimePicker.this.minute = minute;
                                inputDialogTitle(am_pm,hour,minute);
                            }
                        });
                        layout.setLayoutParams(params);
                        layout.setX(pickerDimension.getContentX());
                        layout.setY(pickerDimension.getContentY());
                        ((ClipAnimLayout) getContentView()).setBuilder(builder);
                    } else {
                        layout = (ClipAnimLayout) getSavedContentView(1);
                        ((ScrollTimePickerView)layout).setData(TimePicker.this.am_pm,TimePicker.this.hour,TimePicker.this.minute);
                    }
                }
                decoView.addView(layout);
                layout.startAnim();

                isTime = !isTime;
            }
        });
    }

    @Override
    public void setThemeColor(int color) {
        super.setThemeColor(color);
        if(pickerView != null) pickerView.setThemeColor(color);
    }

    public void inputDialogTitle(int am_pm, int hour, int minute){
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
