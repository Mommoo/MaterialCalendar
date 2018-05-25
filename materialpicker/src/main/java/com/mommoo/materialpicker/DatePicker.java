package com.mommoo.materialpicker;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.util.Log;
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
    private boolean isDate = true;
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
        int textSize = DIPManager.px2dip(getDialogWidth()/7,context);
        setDialogTitleSize(TypedValue.COMPLEX_UNIT_SP,textSize);
        /** if dialog width changed, execute this listener */
        setOnDialogWidthChanged(new OnDialogWidthChanged() {
            @Override
            public void changed(int width) {
                int textSize = DIPManager.px2dip(width/7,getContext());
                setDialogTitleSize(TypedValue.COMPLEX_UNIT_SP,textSize);
            }
        });
        pickBtn.setImageResource(R.mipmap.swap);

        cal.set(year,month,date);
        setData(cal);
        setTitle(this.year,this.month,this.date);

        View view = LayoutInflater.from(context).inflate(R.layout.date_picker_view, null);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);

        yearTextView = (TextView) view.findViewById(R.id.yearText);
        monthTextView = (TextView) view.findViewById(R.id.monthText);

        adapter = new DatePickerViewPagerAdapter(context);

        adapter.setNotifyDataChange(this);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(adapter);

        position = adapter.getPosition(this.year, this.month, this.date);
        viewPager.setCurrentItem(position);
        viewPager.setOffscreenPageLimit(3);

        setDialogContentView(view);
        saveContentView();

        setMoveBtnListener();


        float textFloatSize = pickerDimension.getTextDpSize(context);
        yearTextView.setTextSize(textFloatSize * 1.2f);
        monthTextView.setTextSize(textFloatSize * 1.2f);

        makeDayTextView(view);

        ClipAnimLayout.Builder builder = makeBuilder();
        ScrollDatePickerView scrollDatePickerView = makeScrollDatePickerView(context);
        scrollDatePickerView.setBuilder(builder);
        saveContentView(1,scrollDatePickerView);
        ((ClipAnimLayout)view).setBuilder(builder);


        setOnPickBtnListener(new OnPickBtnListener() {
            private int swapResId = R.mipmap.swap;
            private int calendarResId = R.mipmap.calendar;
            @Override
            public void onClick(View view, final FrameLayout decoView) {


                ClipAnimLayout layout = null;

                if (!isDate) {
                    layout = (ClipAnimLayout) getSavedContentView(0);
                    DatePickerViewPagerAdapter viewPagerAdapter =((DatePickerViewPagerAdapter)viewPager.getAdapter());
                    int position = viewPagerAdapter.getPosition(DatePicker.this.year,DatePicker.this.month,DatePicker.this.date);
                    viewPager.setCurrentItem(position,true);
                    viewPagerAdapter.setData(DatePicker.this.year,DatePicker.this.month,DatePicker.this.date);
                    viewPagerAdapter.notifyDataSetChanged();
                } else {
                    layout = (ClipAnimLayout) getSavedContentView(1);
                    ((ScrollDatePickerView)layout).setData(DatePicker.this.year,DatePicker.this.month,DatePicker.this.date);
                }
                layout.setX(pickerDimension.getContentX());
                layout.setY(pickerDimension.getContentY());

                if ( layout.getParent() == null){
                    decoView.addView(layout);
                }


                layout.startAnim();
                isDate = !isDate;
                ((ImageView) view).setImageResource(isDate?swapResId:calendarResId);
            }
        });
    }

    private void setMoveBtnListener(){
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(position+((int)view.getTag()), true);
            }
        };
        View leftBtn = findViewById(R.id.leftBtn); leftBtn.setTag(-1);
        View rightBtn = findViewById(R.id.rightBtn); rightBtn.setTag(1);
        leftBtn.setOnClickListener(onClickListener);
        rightBtn.setOnClickListener(onClickListener);
    }

    private void makeDayTextView(View targetView){
        float textFloatSize = pickerDimension.getTextDpSize(targetView.getContext());
        LinearLayout linearLayout = (LinearLayout) targetView.findViewById(R.id.linearLayout);
        for (int i = 0; i < 7; i++) {
            dayTextViews[i] = new TextView(targetView.getContext());
            dayTextViews[i].setText(days[i]);
            dayTextViews[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, textFloatSize);
            dayTextViews[i].setGravity(Gravity.CENTER);
            if (i == 0) dayTextViews[i].setTextColor(Color.RED);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
            params.weight = 1;
            linearLayout.addView(dayTextViews[i], params);
        }
    }

    private ClipAnimLayout.Builder makeBuilder(){
        ClipAnimLayout.Builder builder = new ClipAnimLayout.Builder();
        int radius = pickerDimension.getPickBtnAnimCircleRadius();
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

    private ScrollDatePickerView makeScrollDatePickerView(Context context){
        int contentHeight = pickerDimension.getContentHeight();
        int contentWidth = pickerDimension.getContentWidth();
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(contentWidth, contentHeight);
        Calendar tempCalendar = Calendar.getInstance();
        tempCalendar.set(DatePicker.this.year,DatePicker.this.month - 1,DatePicker.this.date);
        ScrollDatePickerView scrollDatePickerView = new ScrollDatePickerView(context,tempCalendar);
        scrollDatePickerView.setPickChanged(new ScrollDatePickerView.PickChanged() {
            @Override
            public void dataSet(int year, int month, int date) {
                DatePicker.this.year = year;
                DatePicker.this.month = month;
                DatePicker.this.date = date;
                setTitle(year,month,date);
            }
        });
        scrollDatePickerView.setLayoutParams(params);
        scrollDatePickerView.setX(pickerDimension.getContentX());
        scrollDatePickerView.setY(pickerDimension.getContentY());
        return scrollDatePickerView;
    }

    private void setData(Calendar cal){
        this.year = cal.get(Calendar.YEAR);
        this.month = cal.get(Calendar.MONTH)+1;
        this.date = cal.get(Calendar.DATE);
    }

    @Override
    public void setThemeColor(int color) {
        super.setThemeColor(color);
        DatePickerView.setThemeColor(color);
    }

    @Override
    public void setScrollMode(boolean scrollMode){
        super.setScrollMode(scrollMode);
        isDate = !scrollMode;
        pickBtn.setImageResource(isScrollMode()? R.mipmap.calendar : R.mipmap.swap);
    }

    public void setDate(int year, int month, int date){
        this.year = year;
        this.month = month+1;
        this.date = date;
        position = adapter.getPosition(this.year, this.month, this.date);
        adapter.setData(this.year,this.month,this.date);
        adapter.notifyDataSetChanged();
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
        }

        if(date == DatePickerViewPagerAdapter.ONLY_MOVE_SCROLL){
            yearTextView.setText(yearString);
            monthTextView.setText(monthString);
        }
        if (position > 0) this.position = position;
        else doVibration();
    }
}
