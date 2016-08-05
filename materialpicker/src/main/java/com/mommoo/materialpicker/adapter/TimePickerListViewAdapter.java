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

    private int itemHeight;
    private int[][] timeData;
    private String[] AM_PM_Strings = CalendarCalculator.getAM_PM();
    private int firstVisiblePosition = 0;
    public final static int PICK_AM_PM_DATA_SET_INDEX = 0;
    public final static int PICK_HOUR_DATA_SET_INDEX = 1;
    public final static int PICK_MINUTE_DATA_SET_INDEX = 2;

    private DataChanged dataChanged;

    public interface DataChanged {
        public void callBack(int tag, int value);
    }

    public void setDataChanged(DataChanged dataChanged) {
        this.dataChanged = dataChanged;
    }
    
    public TimePickerListViewAdapter(Context context, int dataSetIndex) {
        super(context,dataSetIndex);

        this.dataSetIndex = dataSetIndex;
        timeData = new int[3][];
        int blankSpace = 4;
        if (dataSetIndex == PICK_AM_PM_DATA_SET_INDEX) {
            int am_pm = 0;
            timeData[dataSetIndex] = new int[2 + blankSpace];
            for (int i = 0, size = 2 + blankSpace; i < size; i++) {
                if (i < 2 || i > size - 3) timeData[dataSetIndex][i] = -1;
                else timeData[dataSetIndex][i] = am_pm++;
            }
        } else if (dataSetIndex == PICK_HOUR_DATA_SET_INDEX) {
            timeData[dataSetIndex] = new int[12 + blankSpace];
            int hour = 1;
            for (int i = 0, size = 12 + blankSpace; i < size; i++) {
                if (i < 2 || i > size - 3) timeData[dataSetIndex][i] = -1;
                else timeData[dataSetIndex][i] = hour++;
            }
        } else if (dataSetIndex == PICK_MINUTE_DATA_SET_INDEX) {
            timeData[dataSetIndex] = new int[60 + blankSpace];
            int minute = 0;
            for (int i = 0, size = 60 + blankSpace; i < size; i++) {
                if (i < 2 || i > size - 3) timeData[dataSetIndex][i] = -1;
                else timeData[dataSetIndex][i] = minute++;
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
        return timeData[dataSetIndex].length;
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
            dataChanged.callBack(dataSetIndex, timeData[dataSetIndex][firstVisiblePosition + 2]);
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
        String time = "";
        if (timeData[dataSetIndex][position] > -1) {
            time = Integer.toString(timeData[dataSetIndex][position]);
            if (dataSetIndex == PICK_MINUTE_DATA_SET_INDEX && timeData[dataSetIndex][position] < 10) {
                time = "0" + time;
            }else if(dataSetIndex == PICK_AM_PM_DATA_SET_INDEX && timeData[dataSetIndex][position] != -1){
                time = AM_PM_Strings[timeData[dataSetIndex][position]];
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
        viewHolder.content.setText(time);
        return convertView;
    }
}
