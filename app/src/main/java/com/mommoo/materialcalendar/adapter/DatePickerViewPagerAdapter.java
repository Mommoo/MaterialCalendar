package com.mommoo.materialcalendar.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.mommoo.materialcalendar.view.DatePickerView;

import java.util.Calendar;

/**
 * Created by mommoo on 2016-07-27.
 */
public class DatePickerViewPagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener, DatePickerView.NotifyClickedData{
    @SuppressWarnings("unused")
    private Context context;
    final static int BASE_YEAR = 2015;
    final static int BASE_MONTH = Calendar.MARCH;
    final static int PAGES = 5;
    final static int LOOPS = 1000;
    final static int BASE_POSITION = PAGES * LOOPS / 2;
    final Calendar BASE_CAL;
    private NotifyChangeData notifyChangeData;
    private boolean once;
    private int year,month,date;

    public interface NotifyChangeData{
        void notifyChangeDate(int year, int month, int date, int position);
    }

    public DatePickerViewPagerAdapter(Context context) {
        this.context = context;
        Calendar base = Calendar.getInstance();
        base.set(BASE_YEAR, BASE_MONTH, 1);
        BASE_CAL = base;
    }

    public void setNotifyDataChange(NotifyChangeData notifyDataChange){
         notifyChangeData = notifyDataChange;
    }

    @Override
    public int getItemPosition(Object object) {
        System.out.println(object.toString());
        return POSITION_NONE;
    }

    public int getPosition(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1);
        return BASE_POSITION + howFarFromBase(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH));
    }

    public int howFarFromBase(int year, int month) {
        int disY = (year - BASE_YEAR) * 12;
        int disM = month - BASE_MONTH;
        return disY + disM;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int howFarFromBase = position - BASE_POSITION;
        Calendar cal = (Calendar) BASE_CAL.clone();
        cal.add(Calendar.MONTH, howFarFromBase-1);
        int mYear2 = cal.get(Calendar.YEAR);
        int mMonth2 = cal.get(Calendar.MONTH) +1;

        final DatePickerView cv = new DatePickerView(context, new DatePickerView.CalendarInfo(mYear2, mMonth2));
        cv.setNotifyDataChange(this.notifyChangeData);
        cv.setNotifyClickedData(this);
        if(!once){
            once = true;
            cv.setClickedState(cal.get(Calendar.DATE));
        }else{
            if(mYear2 == year && mMonth2 == month){
                cv.setClickedState(date);
            }
        }
        container.addView(cv);
        return cv;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((DatePickerView)object).destroy();
        container.removeView((View) object);
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
        Calendar cal = (Calendar) BASE_CAL.clone();
        cal.add(Calendar.MONTH, howFarFromBase-1);
        int month = cal.get(Calendar.MONTH)+1;
        int year = cal.get(Calendar.YEAR);
        notifyChangeData.notifyChangeDate(year,month,-1,position);
    }

    public void setData(int year, int month, int date){
        this.year = year;
        this.month = month;
        this.date = date;
    }

    @Override
    public void notify(int year, int month, int date) {
        this.year = year;
        this.month = month;
        this.date = date;
    }
}