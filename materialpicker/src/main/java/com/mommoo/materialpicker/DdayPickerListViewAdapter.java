package com.mommoo.materialpicker;

import android.content.Context;

/**
 * Created by mommoo on 2016-08-10.
 */
class DdayPickerListViewAdapter extends NotifyListViewAdapter {
    public DdayPickerListViewAdapter(Context context) {
        super(context, 0);
        adapterData = new int[1][205];
        int day = 100;
        boolean flag = false;
        for(int i=0;i<205;i++){
            if (i < 2 || i > 205 - 3) adapterData[0][i] = -1;
            else {
                if(!flag) adapterData[0][i] = day--;
                else adapterData[0][i] = day++;
                if(day==0) flag = true;
            }
        }

        setTargetSize(NotifyTextSize.DEFAULT_TEXT_SIZE);

        decoData = new DecoData() {
            @Override
            public String deco(int position) {
                String d_day = null;
                if(adapterData[0][position] ==0) d_day ="Today";
                else if(adapterData[0][position] <0) d_day ="";
                else if(position>102) d_day = "D+"+adapterData[0][position];
                else if(position<102) d_day = "D-"+adapterData[0][position];
                return d_day;
            }
        };


    }

    @Override
    public void notifyDataSetChanged(int firstVisiblePosition) {
        this.firstVisiblePosition = firstVisiblePosition;
        if (dataChanged != null){
            int value =0;
            if(firstVisiblePosition<100) value = -adapterData[0][firstVisiblePosition + 2];
            else if(firstVisiblePosition>100) value = adapterData[0][firstVisiblePosition + 2];
            dataChanged.callBack(3, value);
        }
        super.notifyDataSetChanged();
    }

//    @Override
//    public int getCount() {
//        return adapterData[1].length;
//    }
}
