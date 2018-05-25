package com.mommoo.materialpicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by mommoo on 2016-07-18.
 */
class CalendarCalculator {
    private int[] monthArray = new int[13];
    private int year,month;
    public CalendarCalculator(int year, int month){
        this.year= year;
        this.month = month;
        monthArray[0] = 0;
        monthArray[1] = 31;
        monthArray[2] = 28;
        monthArray[3] = 31;
        monthArray[4] = 30;
        monthArray[5] = 31;
        monthArray[6] = 30;
        monthArray[7] = 31;
        monthArray[8] = 31;
        monthArray[9] = 30;
        monthArray[10] = 31;
        monthArray[11] = 30;
        monthArray[12] = 31;
    }

    public int getTodayDate(){
        Calendar cal = Calendar.getInstance();
        if(year==cal.get(Calendar.YEAR)&&month==cal.get(Calendar.MONTH)+1) return cal.get(Calendar.DATE);
        else return -1;
    }
    /**
     *
     *  @return 1년부터 현재까지의 총 일수
     *
     */
    public int getAllDay(){
        int allDay = (int) ((year - 1) * 365 + Math.floor((year - 1) / 4) - Math.floor((year - 1) / 100) + Math.floor((year - 1) / 400));
        if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) monthArray[2] = 29;
        else monthArray[2] = 28;
        for (int i = 0; i < month; i++) allDay += monthArray[i];
        return allDay + 1;//1년마다 1일씩 증가하므로 덧셈.
    }

    /**
     *
     *  @return 현재 달의 1일의 시작 인덱스.
     *
     */
    public int getStartWeekCount(){
        return (getAllDay()%7);
    }

    public int getNeedRowCount(){
        if (monthArray[month] + getStartWeekCount() > 35) return 6;
        else return 5;
    }

    public int getTotalMonthDate(){
        return monthArray[month];
    }

    public static int getTotalMonthDate(int year, int month){
        int[] monthArray = new int[13];
        monthArray[0] = 0;
        monthArray[1] = 31;
        monthArray[2] = 28;
        monthArray[3] = 31;
        monthArray[4] = 30;
        monthArray[5] = 31;
        monthArray[6] = 30;
        monthArray[7] = 31;
        monthArray[8] = 31;
        monthArray[9] = 30;
        monthArray[10] = 31;
        monthArray[11] = 30;
        monthArray[12] = 31;
        if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) monthArray[2] = 29;
        else monthArray[2] = 28;
        return monthArray[month];
    }

    public int getBeforeTotalMonthDate(){
        if(month-1 ==0) return monthArray[12];
        else return monthArray[month-1];
    }
    public int getAfterTotalMonthDate(){
        if(month+1 == 13) return monthArray[1];
        else return monthArray[month+1];
    }

    public static String[] getAM_PM(){
        String[] am_pm = new String[2];
        SimpleDateFormat dateFormat = new SimpleDateFormat("aa", Locale.getDefault());
        Calendar cc = Calendar.getInstance();
        for(int i=0; i<2; i++){
            cc.set(Calendar.AM_PM,i);
            am_pm[i] = dateFormat.format(cc.getTime());
        }
        return am_pm;
    }

    public static String[] getDays(){
        String[] days = new String[7];
        SimpleDateFormat dateFormat = new SimpleDateFormat("E", Locale.getDefault());
        Calendar cc = Calendar.getInstance();
        for(int i=0; i<7; i++){
            cc.set(Calendar.DAY_OF_WEEK,i+1);
            days[i] = dateFormat.format(cc.getTime());
        }
        return days;
    }

    public static String[] getFullDays(){
        String[] days = new String[7];
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        Calendar cc = Calendar.getInstance();
        for(int i=0; i<7; i++){
            cc.set(Calendar.DAY_OF_WEEK,i+1);
            days[i] = dateFormat.format(cc.getTime());
        }
        return days;
    }

    public static String[] getMonths(){
        String[] months = new String[12];
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE", Locale.getDefault());
        Calendar cc = Calendar.getInstance();
        for(int i=0; i<12; i++){
            cc.set(Calendar.MONTH,i);
            months[i] = dateFormat.format(cc.getTime());
        }
        return months;
    }
    public static int transHourToAngle(int hour){
        int standardHour = 3;
        int standardAngle = 0;
        if(hour<13){
            for(int i=0; i<12; i++){
                if(hour == standardHour--) return standardAngle;
                if(standardHour == 0) standardHour = 12;
                standardAngle = (30*(i+1));
            }
        }
        return -1;
    }

    public static int transAngleToHour(int angle){
        int standardHour = 3;
        int standardAngle = 0;
        if(angle<361){
            for(int i=0; i<360; i++){
                if(angle == standardAngle++) return standardHour;
                if(standardAngle%30==0) standardHour--;
                if(standardHour == 0) standardHour = 12;
            }
        }
        return -1;
    }

    public static int transMinuteToAngle(int minute){
        int standardMinute = 15;
        int standardAngle = 0;
        if(minute<61){
            for(int i=0; i<60; i++){
                if(minute == (standardMinute--)) return standardAngle;
                if(standardMinute == 0){
                    if(minute == 0) return 90;
                    standardMinute = 60;
                }
                standardAngle = (6*(i+1));
            }
        }
        return -1;
    }

    public static int transAngleToMinute(int angle){
        int standardMinute = 15;
        int standardAngle = 0;
        if(angle<361){
            for(int i=0; i<360; i++){
                if(angle == (standardAngle)) return standardMinute;
                if(standardAngle++%6==0) standardMinute--;
                if(standardMinute == 0) standardMinute = 60;
            }
        }
        return -1;
    }
}
