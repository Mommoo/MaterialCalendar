package com.mommoo.materialpicker;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import java.util.Calendar;

/**
 * Created by mommoo on 2016-07-27.
 */
class DatePickerViewPagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener, DatePickerView.NotifyClickedData{

    private Context context;
    private final static int BASE_YEAR = 2015;
    private final static int BASE_MONTH = Calendar.MARCH;
    private final static int PAGES = 5;
    private final static int LOOPS = 1000;
    private final static int BASE_POSITION = PAGES * LOOPS / 2;
    private final Calendar BASE_CAL;
    private final Calendar POSITION_CALENDAR;
    private final static Calendar CLONE_CALENDAR = Calendar.getInstance();
    public final static int ONLY_MOVE_SCROLL = -1;
    private NotifyChangeData notifyChangeData;
    private boolean once;
    private int year,month,date,clickedPosition;
    private SparseArray<DatePickerView> datePickerViewCacheMap = new SparseArray<>();

    public interface NotifyChangeData{
        void notifyChangeDate(int year, int month, int date, int position);
    }

    public DatePickerViewPagerAdapter(Context context) {
        this.context = context;
        BASE_CAL = Calendar.getInstance();
        BASE_CAL.set(BASE_YEAR, BASE_MONTH, 1);
        POSITION_CALENDAR = Calendar.getInstance();
    }

    public void setNotifyDataChange(NotifyChangeData notifyDataChange){
         notifyChangeData = notifyDataChange;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public int getPosition(int year, int month, int date) {
        POSITION_CALENDAR.set(year, month, 1);
        this.date = date;
        return BASE_POSITION + howFarFromBase(POSITION_CALENDAR.get(Calendar.YEAR), POSITION_CALENDAR.get(Calendar.MONTH));
    }

    public int howFarFromBase(int year, int month) {
        int disY = (year - BASE_YEAR) * 12;
        int disM = month - BASE_MONTH;
        return disY + disM;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int howFarFromBase = position - BASE_POSITION;
        CLONE_CALENDAR.setTimeInMillis(BASE_CAL.getTimeInMillis());
        CLONE_CALENDAR.add(Calendar.MONTH, howFarFromBase-1);
        final int mYear2 = CLONE_CALENDAR.get(Calendar.YEAR);
        final int mMonth2 = CLONE_CALENDAR.get(Calendar.MONTH) +1;
        final DatePickerView dpv = new DatePickerView(context, mYear2,mMonth2);
        dpv.setNotifyDataChange(DatePickerViewPagerAdapter.this.notifyChangeData);
        dpv.setNotifyClickedData(DatePickerViewPagerAdapter.this);
        dpv.setTag(position);
        if(!once ||(mYear2 == year && mMonth2 == month)){
            once = true;
            dpv.setDate(DatePickerViewPagerAdapter.this.date);
            dpv.setCheckedDate(true);
            clickedPosition = position;
        }
        datePickerViewCacheMap.put(position,dpv);
        container.addView(dpv);
        return dpv;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view =(View) object;
        datePickerViewCacheMap.delete((int)view.getTag());
        ((DatePickerView)view).destroy();
        container.removeView(view);
    }

    @Override
    public int getCount() {
        return PAGES * LOOPS;
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        switch (state) {
            case ViewPager.SCROLL_STATE_IDLE:
                break;
            case ViewPager.SCROLL_STATE_DRAGGING:
                break;
            case ViewPager.SCROLL_STATE_SETTLING:
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        int howFarFromBase = position - BASE_POSITION;
        CLONE_CALENDAR.setTimeInMillis(BASE_CAL.getTimeInMillis());
        CLONE_CALENDAR.add(Calendar.MONTH, howFarFromBase-1);
        int year = CLONE_CALENDAR.get(Calendar.YEAR); int month = CLONE_CALENDAR.get(Calendar.MONTH)+1;
        notifyChangeData.notifyChangeDate(year,month,ONLY_MOVE_SCROLL,position);
    }

    public void setData(int year, int month, int date){
        this.year = year;
        this.month = month;
        this.date = date;
    }

    @Override
    public void notify(DatePickerView dpv) {
        this.year = dpv.getYear();
        this.month = dpv.getMonth();
        this.date = dpv.getDate();
        DatePickerView datePickerView = datePickerViewCacheMap.get(clickedPosition);
        if(datePickerView != null && (datePickerView.getYear()!=year || datePickerView.getMonth() != month)) {
            datePickerView.setCheckedDate(false);
        }
        clickedPosition = (int)dpv.getTag();
    }
}
