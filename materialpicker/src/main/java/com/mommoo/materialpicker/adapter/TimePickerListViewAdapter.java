package com.mommoo.materialpicker.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mommoo.materialpicker.helper.NotifyListViewAdapter;
import com.mommoo.materialpicker.toolkit.CalendarCalculator;

/**
 * Created by mommoo on 2016-08-04.
 */
public class TimePickerListViewAdapter extends NotifyListViewAdapter {
    /**
     *
     * @AM_PM_Adapter : 0, 1
     * @HourAdapter : 1 ~ 12
     * @MinuteAdapter : 00~59
     *
     */

    private String[] AM_PM_Strings = CalendarCalculator.getAM_PM();
    public final static int PICK_AM_PM_DATA_SET_INDEX = 0;
    public final static int PICK_HOUR_DATA_SET_INDEX = 1;
    public final static int PICK_MINUTE_DATA_SET_INDEX = 2;

    public TimePickerListViewAdapter(Context context, int dataSetIndex) {
        super(context,dataSetIndex);
        adapterData = new int[3][];
        int blankSpace = 4;
        if (dataSetIndex == PICK_AM_PM_DATA_SET_INDEX) {
            int am_pm = 0;
            adapterData[dataSetIndex] = new int[2 + blankSpace];
            for (int i = 0, size = 2 + blankSpace; i < size; i++) {
                if (i < 2 || i > size - 3) adapterData[dataSetIndex][i] = -1;
                else adapterData[dataSetIndex][i] = am_pm++;
            }
        } else if (dataSetIndex == PICK_HOUR_DATA_SET_INDEX) {
            adapterData[dataSetIndex] = new int[12 + blankSpace];
            int hour = 1;
            for (int i = 0, size = 12 + blankSpace; i < size; i++) {
                if (i < 2 || i > size - 3) adapterData[dataSetIndex][i] = -1;
                else adapterData[dataSetIndex][i] = hour++;
            }
        } else if (dataSetIndex == PICK_MINUTE_DATA_SET_INDEX) {
            adapterData[dataSetIndex] = new int[60 + blankSpace];
            int minute = 0;
            for (int i = 0, size = 60 + blankSpace; i < size; i++) {
                if (i < 2 || i > size - 3) adapterData[dataSetIndex][i] = -1;
                else adapterData[dataSetIndex][i] = minute++;
            }
        }
        decoData = new DecoData() {
            @Override
            public String deco(int position) {
                String text = null;
                if (adapterData[TimePickerListViewAdapter.this.dataSetIndex][position] > -1) {
                    text = Integer.toString(adapterData[TimePickerListViewAdapter.this.dataSetIndex][position]);
                    if (TimePickerListViewAdapter.this.dataSetIndex == PICK_MINUTE_DATA_SET_INDEX && adapterData[TimePickerListViewAdapter.this.dataSetIndex][position] < 10) {
                        text = "0" + text;
                    }else if(TimePickerListViewAdapter.this.dataSetIndex == PICK_AM_PM_DATA_SET_INDEX && adapterData[TimePickerListViewAdapter.this.dataSetIndex][position] != -1){
                        text = AM_PM_Strings[adapterData[TimePickerListViewAdapter.this.dataSetIndex][position]];
                    }
                }
                return text;
            }
        };
    }
}
