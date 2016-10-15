package com.mommoo.materialpicker;

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

import java.util.Calendar;

/**
 * Created by mommoo on 2016-07-27.
 */
public class DatePicker extends Picker implements DatePickerViewPagerAdapter.NotifyChangeData {

    private final Calendar cal = Calendar.getInstance();
    private int year, month, date, position;
    private OnDateSet onDateSet;
    private OnAcceptListener onAcceptListener;
    private TextView yearTextView, monthTextView;
    private TextView[] dayTextViews = new TextView[7];
    private String[] fullDays = CalendarCalculator.getFullDays();
    private String[] days = CalendarCalculator.getDays();
    private boolean isDate;
    private ViewPager viewPager;
    private DatePickerViewPagerAdapter adapter;

    public interface OnDateSet {
        void onDate(boolean isAccept, int year, int month, int date);
    }

    public DatePicker(Context context) {
        super(context);
        initialize(context, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),cal.get(Calendar.DATE));
    }

    public DatePicker(Context context, int year, int month, int date) {
        super(context);
        initialize(context, year, month, date);
    }

    private void initialize(Context context, final int year, int month, int date) {
        int testSize = DIPManager.px2dip(getDialogWidth()/7,context);
        setDialogTitleSize(TypedValue.COMPLEX_UNIT_SP,testSize);
        setOnDialogWidthChanged(new OnDialogWidthChanged() {
            @Override
            public void changed(int width) {
                int testSize = DIPManager.px2dip(width/7,getContext());
                setDialogTitleSize(TypedValue.COMPLEX_UNIT_SP,testSize);
            }
        });
        pickBtn.setImageResource(R.mipmap.swap);
        cal.set(year,month,date);
        this.year = cal.get(Calendar.YEAR);
        this.month = cal.get(Calendar.MONTH) +1;
        this.date = cal.get(Calendar.DATE);
        setTitle(this.year,this.month,this.date);

        View view = LayoutInflater.from(context).inflate(R.layout.date_picker_view, null);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);

        yearTextView = (TextView) view.findViewById(R.id.yearText);
        monthTextView = (TextView) view.findViewById(R.id.monthText);

        adapter = new DatePickerViewPagerAdapter(context);
        adapter.setThemeColor(getThemeColor());

        adapter.setNotifyDataChange(this);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(adapter);

        position = adapter.getPosition(this.year, this.month, this.date);
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
                    int position = viewPagerAdapter.getPosition(DatePicker.this.year,DatePicker.this.month,DatePicker.this.date);
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
                            @Override
                            public void dataSet(int year, int month, int date) {

                                DatePicker.this.year = year;
                                DatePicker.this.month = month;
                                DatePicker.this.date = date;
                                setTitle(year,month,date);
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

    @Override
    public void setThemeColor(int color) {
        super.setThemeColor(color);
        if(adapter!=null)adapter.setThemeColor(color);
    }

    public void setDate(int year, int month, int date){
        this.year = year;
        this.month = month+1;
        this.date = date;
        position = adapter.getPosition(this.year, this.month, this.date);
        viewPager.setCurrentItem(position);
        setTitle(this.year,this.month,this.date);
    }

    private void setTitle(int year, int month, int date){
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
            setTitle(year,month,date);
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
