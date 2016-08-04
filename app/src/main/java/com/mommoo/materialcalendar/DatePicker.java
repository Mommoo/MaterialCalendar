package com.mommoo.materialcalendar;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mommoo.materialcalendar.adapter.DatePickerViewPagerAdapter;
import com.mommoo.materialcalendar.helper.Picker;
import com.mommoo.materialcalendar.toolkit.CalendarCalculator;
import com.mommoo.materialcalendar.view.DatePickerView;
import com.mommoo.materialcalendar.view.ScrollDatePickerView;
import com.mommoo.materialcalendar.widget.ClipAnimLayout;

import java.util.Calendar;

/**
 * Created by mommoo on 2016-07-27.
 */
public class DatePicker extends Picker implements DatePickerViewPagerAdapter.NotifyChangeData {

    private int year, month, date, position;
    private OnDateSet onDateSet;
    private OnAcceptListener onAcceptListener;
    private TextView yearTextView, monthTextView;
    private TextView[] dayTextViews = new TextView[7];
    private String[] fullDays = CalendarCalculator.getFullDays();
    private String[] days = CalendarCalculator.getDays();
    private boolean isDate;
    private ViewPager viewPager;

    public interface OnDateSet {
        void onDate(boolean isAccept, int year, int month, int date);
    }

    public DatePicker(Context context) {
        super(context);
        Calendar cal = Calendar.getInstance();
        initialize(context, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1);
    }

    public DatePicker(Context context, int year, int month) {
        super(context);
        initialize(context, year, month + 1);
    }

    private void initialize(Context context, final int year, int month) {
        setDialogTitleSize(TypedValue.COMPLEX_UNIT_SP,getDialogWidth()/28);
        setOnDialogWidthChanged(new OnDialogWidthChanged() {
            @Override
            public void changed(int width) {
                setDialogTitleSize(TypedValue.COMPLEX_UNIT_SP,width/28);
            }
        });
        pickBtn.setImageResource(R.mipmap.swap);
        Calendar cal = Calendar.getInstance();
        this.year = year;
        this.month = month;
        this.date = cal.get(Calendar.DATE);
        inputDialogTitle(year,month,date);

        View view = LayoutInflater.from(context).inflate(R.layout.date_picker_view, null);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);

        yearTextView = (TextView) view.findViewById(R.id.yearText);
        monthTextView = (TextView) view.findViewById(R.id.monthText);

        DatePickerViewPagerAdapter adapter = new DatePickerViewPagerAdapter(context);

        adapter.setNotifyDataChange(this);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(adapter);

        position = adapter.getPosition(year, month);
        viewPager.setCurrentItem(position);
        viewPager.setOffscreenPageLimit(5);

        setDialogContentView(view);

        findViewById(R.id.leftBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(--position, true);
            }
        });

        findViewById(R.id.rightBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(++position, true);
            }
        });

        float textSize = pickerDimension.getTextDpSize(context);
        yearTextView.setTextSize(textSize * 1.2f);
        monthTextView.setTextSize(textSize * 1.2f);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout);
        for (int i = 0; i < 7; i++) {
            dayTextViews[i] = new TextView(context);
            dayTextViews[i].setText(days[i]);
            dayTextViews[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            dayTextViews[i].setGravity(Gravity.CENTER);
            if (i == 0) dayTextViews[i].setTextColor(Color.RED);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
            params.weight = 1;
            linearLayout.addView(dayTextViews[i], params);
        }

        setOnPickBtnListener(new OnPickBtnListener() {
            @Override
            public void onClick(View view, final FrameLayout decoView) {
                int resId = 0;
                if (isDate) resId = R.mipmap.swap;
                else resId = R.mipmap.calendar;
                ((ImageView) view).setImageResource(resId);
                boolean isSavedView = getSavedContentView(1) != null;
                ClipAnimLayout layout = null;

                if (isDate) {
                    layout = (ClipAnimLayout) getSavedContentView(0);
                    DatePickerViewPagerAdapter viewPagerAdapter =((DatePickerViewPagerAdapter)viewPager.getAdapter());
                    int position = viewPagerAdapter.getPosition(DatePicker.this.year,DatePicker.this.month);
                    viewPager.setCurrentItem(position,true);

                    setDatePickViewClickedState(false);
                    boolean success = setDatePickViewClickedState(true);
                    if(!success){
                        viewPagerAdapter.setData(DatePicker.this.year,DatePicker.this.month,DatePicker.this.date);
                        viewPagerAdapter.notifyDataSetChanged();
                    }
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
                        tempCalendar.set(DatePicker.this.year,DatePicker.this.month - 1,DatePicker.this.date);
                        layout = new ScrollDatePickerView(decoView.getContext(),tempCalendar ,builder);
                        ((ScrollDatePickerView)layout).setPickChanged(new ScrollDatePickerView.PickChanged() {
                            public Calendar cal = Calendar.getInstance();
                            @Override
                            public void dataSet(int year, int month, int date) {
                                cal.set(year,month-1,date);
                                DatePicker.this.year = year;
                                DatePicker.this.month = month;
                                DatePicker.this.date = date;
                                inputDialogTitle(year,month,date);
                            }
                        });
                        layout.setLayoutParams(params);
                        layout.setX(pickerDimension.getContentX());
                        layout.setY(pickerDimension.getContentY());
                        ((ClipAnimLayout) getContentView()).setBuilder(builder);
                    } else {
                        layout = (ClipAnimLayout) getSavedContentView(1);
                        ((ScrollDatePickerView)layout).setData(DatePicker.this.year,DatePicker.this.month,DatePicker.this.date);
                    }
                }
                decoView.addView(layout);
                layout.startAnim();

                isDate = !isDate;
            }
        });
    }

    private void inputDialogTitle(int year,int month, int date){
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, date);
        String yearString = Integer.toString(year);
        String monthString = Integer.toString(month);
        String dateString = Integer.toString(date);
        String addText = yearString + ". " + monthString + ". " + dateString;
        super.setDialogTitle(addText);
        super.setDialogStatusTitle(fullDays[cal.get(Calendar.DAY_OF_WEEK) - 1]);
    }

    public void setOnAcceptListener(OnAcceptListener acceptListener) {
        this.onAcceptListener = acceptListener;
        super.callBackAcceptListener = new CallBackAcceptListener() {
            @Override
            public void callBack(boolean isAccept) {
                if (onDateSet != null) onDateSet.onDate(isAccept, year, month, date);
            }

            @Override
            public void accept() {
                DatePicker.this.onAcceptListener.accept();
            }
        };
    }

    public void setOnDateSet(OnDateSet dateSet) {
        this.onDateSet = dateSet;
        super.callBackAcceptListener = new CallBackAcceptListener() {
            @Override
            public void callBack(boolean isAccept) {
                onDateSet.onDate(isAccept, year, month, date);
            }

            @Override
            public void accept() {
                if (DatePicker.this.onAcceptListener != null) onAcceptListener.accept();
            }
        };
    }

    public void setOnDeclineListener(final OnDeclineListener declineListener) {
        super.callBackDeclineListener = new CallBackDeclineListener() {
            @Override
            public void callBack(boolean isAccept) {
                if (onDateSet != null) onDateSet.onDate(isAccept, year, month, date);
            }

            @Override
            public void decline() {
                declineListener.decline();
            }
        };
    }

    @Override
    public void notifyChangeDate(int year, int month, int date, int position) {
        String yearString = Integer.toString(year);
        String monthString = Integer.toString(month);

        if(date >0){
            this.year = year;
            this.month = month;
            this.date = date;
            inputDialogTitle(year,month,date);
            setDatePickViewClickedState(false);
            setDatePickViewClickedState(true);
        }else{
            yearTextView.setText(yearString);
            monthTextView.setText(monthString);
        }
        if (position > 0) this.position = position;
        else doVibration();
    }

    private boolean setDatePickViewClickedState(boolean clicked){
        boolean success = false;
        for(int i=0,size = viewPager.getChildCount();i<size;i++){
            DatePickerView datePickerView = ((DatePickerView)viewPager.getChildAt(i));
            if(clicked){
                if(datePickerView.getCalendarInfo().getYear() == DatePicker.this.year && datePickerView.getCalendarInfo().getMonth() == DatePicker.this.month){
                    datePickerView.setClickedState(DatePicker.this.date);
                    success = true;
                    break;
                }
            }else{
                if(datePickerView.isClicked()){
                    datePickerView.setNotClickedState();
                    success = true;
                    break;
                }
            }
        }
        return success;
    }
}
