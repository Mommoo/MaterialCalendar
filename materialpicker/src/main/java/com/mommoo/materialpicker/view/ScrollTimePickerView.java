package com.mommoo.materialpicker.view;

import android.content.Context;
import android.util.AttributeSet;

import com.mommoo.materialpicker.adapter.TimePickerListViewAdapter;
import com.mommoo.materialpicker.helper.ScrollPickerView;
import com.mommoo.materialpicker.widget.ClipAnimLayout;

import java.util.Calendar;

/**
 * Created by mommoo on 2016-08-04.
 */
public class ScrollTimePickerView extends ScrollPickerView implements TimePickerListViewAdapter.DataChanged{
    
    private int amPm,hour,minute;
    private TimePickerListViewAdapter amPmAdapter,hourAdapter,minuteAdapter;

    public interface PickChanged{
        public void dataSet(int amPm, int hour, int minute);
    }

    private PickChanged pickChanged;

    public void setPickChanged(PickChanged pickChanged){
        this.pickChanged = pickChanged;
    }

    public ScrollTimePickerView(Context context, ClipAnimLayout.Builder builder) {
        super(context, builder);
        initialize(context,null);
    }

    public ScrollTimePickerView(Context context, Calendar cal , Builder builder) {
        super(context, builder);
        initialize(context, cal);
    }

    public ScrollTimePickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context,null);
    }

    public ScrollTimePickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context,null);
    }

    public ScrollTimePickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context,null);
    }

    private void initialize(Context context, Calendar cal) {
        if (cal == null) cal = Calendar.getInstance();

        amPmAdapter = new TimePickerListViewAdapter(context, TimePickerListViewAdapter.PICK_AM_PM_DATA_SET_INDEX);
        pickListView[0].setAdapter(amPmAdapter);
        this.amPm = cal.get(Calendar.AM_PM);
        pickListView[0].setSelection(amPm);
        amPmAdapter.notifyDataSetChanged(amPm);

        hourAdapter = new TimePickerListViewAdapter(context, TimePickerListViewAdapter.PICK_HOUR_DATA_SET_INDEX);
        pickListView[1].setAdapter(hourAdapter);
        this.hour = cal.get(Calendar.HOUR);
        pickListView[1].setSelection(hour-1);
        hourAdapter.notifyDataSetChanged(hour-1);

        minuteAdapter = new TimePickerListViewAdapter(context, TimePickerListViewAdapter.PICK_MINUTE_DATA_SET_INDEX);
        pickListView[2].setAdapter(minuteAdapter);
        this.minute = cal.get(Calendar.MINUTE);
        pickListView[2].setSelection(minute);
        minuteAdapter.notifyDataSetChanged(minute);

        amPmAdapter.setDataChanged(this);
        hourAdapter.setDataChanged(this);
        minuteAdapter.setDataChanged(this);

    }

    public void setData(int am_pm, int hour, int minute){
        pickListView[0].setSelection(am_pm);
        amPmAdapter.notifyDataSetChanged(am_pm);
        pickListView[1].setSelection(hour-1);
        hourAdapter.notifyDataSetChanged(hour-1);
        pickListView[2].setSelection(minute);
        minuteAdapter.notifyDataSetChanged(minute);
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
        if(tag == TimePickerListViewAdapter.PICK_AM_PM_DATA_SET_INDEX) this.amPm = value;
        else if(tag == TimePickerListViewAdapter.PICK_HOUR_DATA_SET_INDEX) this.hour =value;
        else if(tag == TimePickerListViewAdapter.PICK_MINUTE_DATA_SET_INDEX) this.minute = value;
        pickChanged.dataSet(amPm,hour,minute);
    }
}
