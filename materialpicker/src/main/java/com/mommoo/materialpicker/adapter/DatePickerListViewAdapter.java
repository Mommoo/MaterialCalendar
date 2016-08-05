package com.mommoo.materialpicker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import com.mommoo.materialpicker.R;
import com.mommoo.materialpicker.helper.NotifyListViewAdapter;
import com.mommoo.materialpicker.toolkit.CalendarCalculator;
import com.mommoo.materialpicker.widget.NotifyListView;


/**
 * Created by mommoo on 2016-07-29.
 */
public class DatePickerListViewAdapter extends NotifyListViewAdapter {
    /**
     * @YearPick :  1900 ~ 2100
     * @MonthPick :  1 ~ 12
     * @DatePick : 1 ~ [28 - 31]
     */
    public static final int MIN_YEAR = 1900;
    public static final int MAX_YEAR = 2100;
    private int itemHeight;

    private int[][] calendarData;

    public final static int PICK_YEAR_DATA_SET_INDEX = 0;
    public final static int PICK_MONTH_DATA_SET_INDEX = 1;
    public final static int PICK_DATE_DATA_SET_INDEX = 2;

    private DataChanged dataChanged;

    public interface DataChanged {
        public void callBack(int tag, int value);
    }

    public void setDataChanged(DataChanged dataChanged) {
        this.dataChanged = dataChanged;
    }

    public DatePickerListViewAdapter(Context context, int dataSetIndex) {
        super(context,dataSetIndex);
        this.dataSetIndex = dataSetIndex;
        calendarData = new int[3][];
        int blankSpace = 4;

        if (dataSetIndex == PICK_YEAR_DATA_SET_INDEX) {
            int year = MIN_YEAR;
            calendarData[dataSetIndex] = new int[201 + blankSpace];
            for (int i = 0, size = 201 + blankSpace; i < size; i++) {
                if (i < 2 || i > size - 3) calendarData[dataSetIndex][i] = -1;
                else calendarData[dataSetIndex][i] = year++;
            }
        } else if (dataSetIndex == PICK_MONTH_DATA_SET_INDEX) {
            calendarData[dataSetIndex] = new int[12 + blankSpace];
            int month = 1;
            for (int i = 0, size = 12 + blankSpace; i < size; i++) {
                if (i < 2 || i > size - 3) calendarData[dataSetIndex][i] = -1;
                else calendarData[dataSetIndex][i] = month++;
            }
        } else if (dataSetIndex == PICK_DATE_DATA_SET_INDEX) {
            calendarData[dataSetIndex] = new int[31 + blankSpace];
            int date = 1;
            for (int i = 0, size = 31 + blankSpace; i < size; i++) {
                if (i < 2 || i > size - 3) calendarData[dataSetIndex][i] = -1;
                else calendarData[dataSetIndex][i] = date++;
            }
        }

    }

    public void setFitDateSet(final NotifyListView targetListView, int year, int month) {
        int totalDate = CalendarCalculator.getTotalMonthDate(year, month);

        if (calendarData[dataSetIndex].length - 4 != totalDate) {
            if (dataSetIndex == PICK_DATE_DATA_SET_INDEX) {
                int blankSpace = 4;
                calendarData[dataSetIndex] = new int[totalDate + blankSpace];
                int date = 1;
                for (int i = 0, size = totalDate + blankSpace; i < size; i++) {
                    if (i < 2 || i > size - 3) calendarData[dataSetIndex][i] = -1;
                    else calendarData[dataSetIndex][i] = date++;
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

    public class ViewHolder {
        private TextView content;

        public ViewHolder(View view) {
            this.content = (TextView) view.findViewById(R.id.content);
        }
    }

    @Override
    public int getCount() {
        return calendarData[dataSetIndex].length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void notifyDataSetChanged(int firstVisiblePosition) {
        this.firstVisiblePosition = firstVisiblePosition;
        if (dataChanged != null){
            dataChanged.callBack(dataSetIndex, calendarData[dataSetIndex][firstVisiblePosition + 2]);
        }
        super.notifyDataSetChanged();

    }

    public int getItemHeight() {
        return itemHeight;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.year_picker_item_view, viewGroup, false);
            if (itemHeight == 0) itemHeight = (int) Math.ceil((double) viewGroup.getHeight() / 5.0);
            convertView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, itemHeight));
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String year = "";
        if (calendarData[dataSetIndex][position] > 0) {
            year = Integer.toString(calendarData[dataSetIndex][position]);
            if (dataSetIndex == PICK_DATE_DATA_SET_INDEX && calendarData[dataSetIndex][position] < 10) {
                year = "0" + year;
            }
        }
        TextView textView = viewHolder.content;
        if (firstVisiblePosition + 2 == position) {
            textView.setTextSize(itemHeight / 6);
            textView.setTextColor(PRIMARY_TEXT_COLOR);
        } else if (firstVisiblePosition == position || firstVisiblePosition + 4 == position) {
            textView.setTextSize(itemHeight / 11);
            textView.setTextColor(HINT_TEXT_COLOR);
        } else {
            textView.setTextSize(itemHeight / 9);
            textView.setTextColor(SECONDARY_TEXT_COLOR);
        }
        viewHolder.content.setText(year);
        return convertView;
    }
}
