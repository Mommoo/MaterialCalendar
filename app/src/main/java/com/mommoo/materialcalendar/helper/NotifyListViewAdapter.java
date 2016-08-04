package com.mommoo.materialcalendar.helper;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.BaseAdapter;

import com.mommoo.materialcalendar.R;


/**
 * Created by mommoo on 2016-08-04.
 */
public abstract class NotifyListViewAdapter extends BaseAdapter {
    protected int firstVisiblePosition = 0;
    protected int dataSetIndex;
    protected final int PRIMARY_TEXT_COLOR;
    protected final int SECONDARY_TEXT_COLOR;
    protected final int HINT_TEXT_COLOR;

    public NotifyListViewAdapter(Context context,int dataSetIndex){
        PRIMARY_TEXT_COLOR = ContextCompat.getColor(context, R.color.primaryText);
        SECONDARY_TEXT_COLOR = ContextCompat.getColor(context, R.color.secondaryText);
        HINT_TEXT_COLOR = ContextCompat.getColor(context, R.color.hintText);

        this.dataSetIndex = dataSetIndex;
    }

    public void setFirstVisiblePosition(int firstVisiblePosition) {
        this.firstVisiblePosition = firstVisiblePosition;
    }

    public abstract void notifyDataSetChanged(int firstVisiblePosition);
}
