package com.mommoo.materialpicker;

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
abstract class NotifyListViewAdapter extends BaseAdapter {

    protected int[][] adapterData;
    protected int firstVisiblePosition = 0;
    protected final int dataSetIndex;
    private double itemHeight;

    private final int PRIMARY_TEXT_COLOR;
    private final int SECONDARY_TEXT_COLOR;
    private final int HINT_TEXT_COLOR;

    protected enum NotifyTextSize{MAXIMUM_TEXT_SIZE,DEFAULT_TEXT_SIZE,MINIMUM_TEXT_SIZE}
    private NotifyTextSize targetSize = NotifyTextSize.MAXIMUM_TEXT_SIZE;
    private static double[][] ratioStorage = new double[3][3];

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
        ratioStorage[0][0] = 1.5; ratioStorage[0][1] = 2.5; ratioStorage[0][2] = 3.5;
        ratioStorage[1][0] = 2; ratioStorage[1][1] = 3; ratioStorage[1][2] = 4;
        ratioStorage[2][0] = 2.5; ratioStorage[2][1] = 3.5; ratioStorage[2][2] = 4.5;
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

    public double getItemHeight() {
        return itemHeight;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if(convertView ==null){
            convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.year_picker_item_view, viewGroup, false);
            if (itemHeight == 0) {
                itemHeight =  Math.ceil((double) viewGroup.getHeight() / 5.0);
                PRIMARY_TEXT_SIZE = DIPManager.px2dip((int)(itemHeight/ratioStorage[targetSize.ordinal()][0]),convertView.getContext());
                SECONDARY_TEXT_SIZE = DIPManager.px2dip((int)(itemHeight/ratioStorage[targetSize.ordinal()][1]),convertView.getContext());
                HINT_TEXT_SIZE = DIPManager.px2dip((int)(itemHeight/ratioStorage[targetSize.ordinal()][2]),convertView.getContext());
            }
            convertView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, (int)itemHeight));
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
