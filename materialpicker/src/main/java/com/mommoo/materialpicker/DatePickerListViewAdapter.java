package com.mommoo.materialpicker;

import android.content.Context;

/**
 * Created by mommoo on 2016-07-29.
 */
class DatePickerListViewAdapter extends NotifyListViewAdapter {
    /**
     * @YearPick :  1900 ~ 2100
     * @MonthPick :  1 ~ 12
     * @DatePick : 1 ~ [28 - 31]
     */
    public static final int MIN_YEAR = 1900;
    public static final int MAX_YEAR = 2100;

    public final static int PICK_YEAR_DATA_SET_INDEX = 0;
    public final static int PICK_MONTH_DATA_SET_INDEX = 1;
    public final static int PICK_DATE_DATA_SET_INDEX = 2;



    public DatePickerListViewAdapter(Context context, int _dataSetIndex) {
        super(context,_dataSetIndex);
        adapterData = new int[3][];
        int blankSpace = 4;

        if (dataSetIndex == PICK_YEAR_DATA_SET_INDEX) {
            int year = MIN_YEAR;
            adapterData[dataSetIndex] = new int[MAX_YEAR-MIN_YEAR + blankSpace];
            for (int i = 0, size = MAX_YEAR-MIN_YEAR + blankSpace; i < size; i++) {
                if (i < 2 || i > size - 3) adapterData[dataSetIndex][i] = -1;
                else adapterData[dataSetIndex][i] = year++;
            }
        } else if (dataSetIndex == PICK_MONTH_DATA_SET_INDEX) {
            adapterData[dataSetIndex] = new int[12 + blankSpace];
            int month = 1;
            for (int i = 0, size = 12 + blankSpace; i < size; i++) {
                if (i < 2 || i > size - 3) adapterData[dataSetIndex][i] = -1;
                else adapterData[dataSetIndex][i] = month++;
            }
        } else if (dataSetIndex == PICK_DATE_DATA_SET_INDEX) {
            adapterData[dataSetIndex] = new int[31 + blankSpace];
            int date = 1;
            for (int i = 0, size = 31 + blankSpace; i < size; i++) {
                if (i < 2 || i > size - 3) adapterData[dataSetIndex][i] = -1;
                else adapterData[dataSetIndex][i] = date++;
            }
        }
        decoData = new DecoData() {
            @Override
            public String deco(int position) {
                String year = "";
                if (adapterData[dataSetIndex][position] > 0) {
                    year = Integer.toString(adapterData[dataSetIndex][position]);
                    if (dataSetIndex == PICK_DATE_DATA_SET_INDEX && adapterData[dataSetIndex][position] < 10) {
                        year = "0" + year;
                    }
                }
                return year;
            }
        };
    }

    public void setFitDateSet(final NotifyListView targetListView, int year, int month) {
        int totalDate = CalendarCalculator.getTotalMonthDate(year, month);

        if (adapterData[dataSetIndex].length - 4 != totalDate) {
            if (dataSetIndex == PICK_DATE_DATA_SET_INDEX) {
                int blankSpace = 4;
                adapterData[dataSetIndex] = new int[totalDate + blankSpace];
                int date = 1;
                for (int i = 0, size = totalDate + blankSpace; i < size; i++) {
                    if (i < 2 || i > size - 3) adapterData[dataSetIndex][i] = -1;
                    else adapterData[dataSetIndex][i] = date++;
                }
            }
            notifyDataSetChanged();
            if (totalDate - 1 < firstVisiblePosition) {
                firstVisiblePosition = totalDate - 1;
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        targetListView.setSelection(firstVisiblePosition);
                    }
                }.start();
                if (firstVisiblePosition > -1) notifyDataSetChanged(firstVisiblePosition);
            }
        }
    }


}
