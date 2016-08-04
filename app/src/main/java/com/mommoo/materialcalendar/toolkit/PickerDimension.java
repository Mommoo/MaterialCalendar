package com.mommoo.materialcalendar.toolkit;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.mommoo.materialcalendar.manager.DIPManager;


/**
 * Created by mommoo on 2016-07-28.
 */
public class PickerDimension {

    private static PickerDimension cal = new PickerDimension();
    private int contentHeight,contentWidth,radius;
    private float contentX,contentY;

    public static PickerDimension getInstance(){
        return cal;
    }

    private PickerDimension(){

    }

    private void setContentHeight(int contentHeight){
        this.contentHeight = contentHeight;
    }

    public int getContentHeight(){
        return contentHeight;
    }

    public void setContentWidth(Context context,int contentWidth){
        this.contentWidth = contentWidth;
        // datePicker의 toolbar 와 캘린더뷰의 비율이 3:11 이다.
        setContentHeight((int)(((getElementAreaSize(context) *6) + (getPadding(context)*7))*14)/11);
    }

    public int getContentWidth(){
        return this.contentWidth;
    }

    public void setContentX(float x){
        this.contentX = x;
    }

    public float getContentX(){
        return contentX;
    }

    public void setContentY(float y){
        this.contentY = y;
    }

    public float getContentY(){
        return  contentY;
    }

    public int getPadding(Context context){
        return DIPManager.dip2px(16,context);
    }

    public float getElementAreaSize(Context context){
        return (contentWidth- getPadding(context)*8)/7;
    }

    public float getTextPxSize(Context context){
        return 13*getElementAreaSize(context)/20;
    }

    public float getTextDpSize(Context context){
        Resources r = context.getResources();
        DisplayMetrics metrics = r.getDisplayMetrics();
        return getTextPxSize(context) / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public int getPickBtnAnimCircleColor(){
        return android.graphics.Color.parseColor("#20000000");
    }

    public void setPickBtnAnimCircleRadius(int radius){
        this.radius = radius;
    }

    public int getPickBtnAnimCircleRadius(){
        return this.radius;
    }
}
