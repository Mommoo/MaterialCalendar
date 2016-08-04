package com.mommoo.materialcalendar.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;

import com.mommoo.materialcalendar.adapter.DatePickerListViewAdapter;
import com.mommoo.materialcalendar.helper.ScrollPickerView;
import com.mommoo.materialcalendar.widget.ClipAnimLayout;

import java.util.Calendar;

/**
 * Created by mommoo on 2016-07-29.
 */
public class ScrollDatePickerView extends ScrollPickerView implements DatePickerListViewAdapter.DataChanged{

    private int year,month, date;
    private DatePickerListViewAdapter yearAdapter,monthAdapter,dateAdapter;

    public interface PickChanged{
        public void dataSet(int year, int month, int date);
    }

    private PickChanged pickChanged;

    public void setPickChanged(PickChanged pickChanged){
        this.pickChanged = pickChanged;
    }

    public ScrollDatePickerView(Context context, ClipAnimLayout.Builder builder) {
        super(context, builder);
        initialize(context,null);
    }

    public ScrollDatePickerView(Context context, Calendar cal , ClipAnimLayout.Builder builder) {
        super(context, builder);
        initialize(context,cal);
    }

    public ScrollDatePickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context,null);
    }

    public ScrollDatePickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context,null);
    }

    @TargetApi(21)
    public ScrollDatePickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context,null);
    }

    private void initialize(Context context, Calendar cal) {
        if (cal == null) cal = Calendar.getInstance();

        yearAdapter = new DatePickerListViewAdapter(context, DatePickerListViewAdapter.PICK_YEAR_DATA_SET_INDEX);
        pickListView[0].setAdapter(yearAdapter);
        pickListView[0].setSelection(cal.get(Calendar.YEAR)-yearAdapter.MIN_YEAR);

        monthAdapter = new DatePickerListViewAdapter(context, DatePickerListViewAdapter.PICK_MONTH_DATA_SET_INDEX);
        pickListView[1].setAdapter(monthAdapter);
        this.month = cal.get(Calendar.MONTH) + 1;
        pickListView[1].setSelection(month - 1);

        dateAdapter = new DatePickerListViewAdapter(context, DatePickerListViewAdapter.PICK_DATE_DATA_SET_INDEX);
        pickListView[2].setAdapter(dateAdapter);
        this.date = cal.get(Calendar.DATE);
        pickListView[2].setSelection(date - 1);

        yearAdapter.setDataChanged(this);
        monthAdapter.setDataChanged(this);
        dateAdapter.setDataChanged(this);
    }

    public void setData(int year, int month, int date){
        int howFarFrom = year-yearAdapter.MIN_YEAR;
        pickListView[0].setSelection(howFarFrom);
        yearAdapter.notifyDataSetChanged(howFarFrom);
        pickListView[1].setSelection(month-1);
        monthAdapter.notifyDataSetChanged(month-1);
        pickListView[2].setSelection(date-1);
        dateAdapter.notifyDataSetChanged(date-1);
    }

    @Override
    protected void setRadius(float radius) {
        super.setRadius(radius);
    }

    @Override
    protected float getRadius() {
        return super.getRadius();
    }

    @Override
    public void callBack(int tag,int value) {
        if(tag == DatePickerListViewAdapter.PICK_YEAR_DATA_SET_INDEX) this.year = value;
        else if(tag == DatePickerListViewAdapter.PICK_MONTH_DATA_SET_INDEX) this.month =value;
        else if(tag == DatePickerListViewAdapter.PICK_DATE_DATA_SET_INDEX) this.date = value;
        if(tag != DatePickerListViewAdapter.PICK_DATE_DATA_SET_INDEX && month != 0) dateAdapter.setFitDateSet(pickListView[2],year,month);
        pickChanged.dataSet(year,month,date);
    }
}
