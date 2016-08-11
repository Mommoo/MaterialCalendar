package com.mommoo.materialpicker.helper;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mommoo.materialpicker.R;


/**
 * Created by mommoo on 2016-08-04.
 */
public abstract class NotifyListViewAdapter extends BaseAdapter {

    protected int[][] adapterData;
    protected int firstVisiblePosition = 0;
    protected final int dataSetIndex;
    private int itemHeight;

    private final int PRIMARY_TEXT_COLOR;
    private final int SECONDARY_TEXT_COLOR;
    private final int HINT_TEXT_COLOR;

    protected enum NotifyTextSize{MAXIMUM_TEXT_SIZE,DEFAULT_TEXT_SIZE,MINIMUM_TEXT_SIZE}
    private NotifyTextSize targetSize = NotifyTextSize.MAXIMUM_TEXT_SIZE;
    private static int[][] ratioStorage = new int[3][3];

    private int PRIMARY_TEXT_SIZE;
    private int SECONDARY_TEXT_SIZE;
    private int HINT_TEXT_SIZE;

    protected DecoData decoData;

    protected interface DecoData{
        public String deco(int position);
    }

    protected DataChanged dataChanged;

    public interface DataChanged {
        public void callBack(int tag, int value);
    }

    public void setDataChanged(DataChanged dataChanged) {
        this.dataChanged = dataChanged;
    }

    public void setTargetSize(NotifyTextSize targetSize){
        this.targetSize = targetSize;
        NotifyTextSize.valueOf(targetSize.toString());
    }

    public NotifyListViewAdapter(Context context,int dataSetIndex){
        PRIMARY_TEXT_COLOR = ContextCompat.getColor(context, R.color.primaryText);
        SECONDARY_TEXT_COLOR = ContextCompat.getColor(context, R.color.secondaryText);
        HINT_TEXT_COLOR = ContextCompat.getColor(context, R.color.hintText);
        ratioStorage[0][0] = 6; ratioStorage[0][1] = 10; ratioStorage[0][2] = 12;
        ratioStorage[1][0] = 7; ratioStorage[1][1] = 11; ratioStorage[1][2] = 13;
        ratioStorage[2][0] = 8; ratioStorage[2][1] = 12; ratioStorage[2][2] = 14;
        this.dataSetIndex = dataSetIndex;
    }

    public void setFirstVisiblePosition(int firstVisiblePosition) {
        this.firstVisiblePosition = firstVisiblePosition;
    }

    public void notifyDataSetChanged(int firstVisiblePosition) {
        this.firstVisiblePosition = firstVisiblePosition;
        if (dataChanged != null){
            dataChanged.callBack(dataSetIndex, adapterData[dataSetIndex][firstVisiblePosition + 2]);
        }
        super.notifyDataSetChanged();
    }

    public class ViewHolder {
        public TextView content;
        public ViewHolder(View view) {
            this.content = (TextView) view.findViewById(R.id.content);
        }
    }

    public int getItemHeight() {
        return itemHeight;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if(convertView ==null){
            convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.year_picker_item_view, viewGroup, false);
            if (itemHeight == 0) {
                itemHeight = (int) Math.ceil((double) viewGroup.getHeight() / 5.0);
                PRIMARY_TEXT_SIZE = itemHeight/ratioStorage[targetSize.ordinal()][0];
                SECONDARY_TEXT_SIZE = itemHeight/ratioStorage[targetSize.ordinal()][1];
                HINT_TEXT_SIZE = itemHeight/ratioStorage[targetSize.ordinal()][2];
            }
            convertView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, itemHeight));
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else viewHolder = (ViewHolder) convertView.getTag();

        String text;
        if(decoData !=null )text = decoData.deco(position);
        else text = Integer.toString(adapterData[dataSetIndex][position]);
        TextView textView = viewHolder.content;
        setTextViewAppearance(textView,position);
        textView.setText(text);
        return convertView;
    }

    protected void setTextViewAppearance(TextView textView,int position){
        if (firstVisiblePosition + 2 == position) {
            textView.setTextSize(PRIMARY_TEXT_SIZE);
            textView.setTextColor(PRIMARY_TEXT_COLOR);
        } else if (firstVisiblePosition == position || firstVisiblePosition + 4 == position) {
            textView.setTextSize(HINT_TEXT_SIZE);
            textView.setTextColor(HINT_TEXT_COLOR);
        } else {
            textView.setTextSize(SECONDARY_TEXT_SIZE);
            textView.setTextColor(SECONDARY_TEXT_COLOR);
        }
    }

    @Override
    public int getCount() {
        return adapterData[dataSetIndex].length;
    }
    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
