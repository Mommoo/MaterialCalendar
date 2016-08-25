package com.mommoo.materialpicker;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.*;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by mommoo on 2016-08-26.
 */
public class MonthPicker extends Picker{

    private Calendar calendar = Calendar.getInstance();
    private CircleImageView[] imageViews;
    private TextView[] textViews;
    private int year,month;

    public MonthPicker(Context context) {
        super(context);
        init(context);
    }

    public MonthPicker(Context context,int year, int month){
        super(context);
        calendar.set(year,month,1);
        init(context);
    }
    private MonthPicker(Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    protected MonthPicker(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

//    public void setThemeColor(int color){
//        super.setThemeColor(color);
//        pickBtn.setBackgroundColor(android.graphics.Color.TRANSPARENT);
//    }

    private void init(Context context){
        preventChangeWidth(true);
        setThemeColor(ContextCompat.getColor(context,R.color.colorAccent));
        pickBtn.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        View view = LayoutInflater.from(context).inflate(R.layout.month_picker_view,null,false);
        super.forceChangeContentHeight(DIPManager.dip2px(210,context));
        setDialogContentView(view);

        final TextView year = (TextView)view.findViewById(R.id.year);
        MonthPicker.this.year = calendar.get(Calendar.YEAR);
        String yearText =""+MonthPicker.this.year;
        year.setText(yearText);
        setDialogStatusTitle(yearText);

        month = calendar.get(Calendar.MONTH);
        String monthText = (month+1)<10?"0"+(month+1):""+(month+1);
        setDialogTitle(monthText);
        int testSize = DIPManager.px2dip(getDialogWidth()/4,getContext());
        setDialogTitleSize(TypedValue.COMPLEX_UNIT_SP,testSize);

        imageViews = new CircleImageView[12];
        imageViews[0] = (CircleImageView)findViewById(R.id.background1);
        imageViews[1] = (CircleImageView)findViewById(R.id.background2);
        imageViews[2] = (CircleImageView)findViewById(R.id.background3);
        imageViews[3] = (CircleImageView)findViewById(R.id.background4);
        imageViews[4] = (CircleImageView)findViewById(R.id.background5);
        imageViews[5] = (CircleImageView)findViewById(R.id.background6);
        imageViews[6] = (CircleImageView)findViewById(R.id.background7);
        imageViews[7] = (CircleImageView)findViewById(R.id.background8);
        imageViews[8] = (CircleImageView)findViewById(R.id.background9);
        imageViews[9] = (CircleImageView)findViewById(R.id.background10);
        imageViews[10] = (CircleImageView)findViewById(R.id.background11);
        imageViews[11] = (CircleImageView)findViewById(R.id.background12);

        textViews = new TextView[12];
        textViews[0] = (TextView)findViewById(R.id.text1);
        textViews[1] = (TextView)findViewById(R.id.text2);
        textViews[2] = (TextView)findViewById(R.id.text3);
        textViews[3] = (TextView)findViewById(R.id.text4);
        textViews[4] = (TextView)findViewById(R.id.text5);
        textViews[5] = (TextView)findViewById(R.id.text6);
        textViews[6] = (TextView)findViewById(R.id.text7);
        textViews[7] = (TextView)findViewById(R.id.text8);
        textViews[8] = (TextView)findViewById(R.id.text9);
        textViews[9] = (TextView)findViewById(R.id.text10);
        textViews[10] = (TextView)findViewById(R.id.text11);
        textViews[11] = (TextView)findViewById(R.id.text12);

        for(int i=0; i<12; i++){
            final int position = i;
            imageViews[i].setCirclePadding(DIPManager.dip2px(10,getContext()));
            if(i == month) {
                imageViews[i].setCircleBackgroundColor(getThemeColor());
                textViews[i].setTextColor(Color.WHITE);
            }
            imageViews[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    doVibration();
                    initWidgetState();
                    month = position;
                    textViews[position].setTextColor(Color.WHITE);
                    imageViews[position].setCircleBackgroundColor(getThemeColor());
                    String date = position+1<10?"0"+(position+1):""+(position+1);
                    setDialogTitle(date);
                }
            });
        }


        view.findViewById(R.id.left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doVibration();
                calendar.add(Calendar.YEAR,-1);
                MonthPicker.this.year = calendar.get(Calendar.YEAR);
                String yearText =""+MonthPicker.this.year;
                year.setText(yearText);
                setDialogStatusTitle(yearText);
            }
        });
        view.findViewById(R.id.right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doVibration();
                calendar.add(Calendar.YEAR,+1);
                MonthPicker.this.year = calendar.get(Calendar.YEAR);
                String yearText =""+MonthPicker.this.year;
                year.setText(yearText);
                setDialogStatusTitle(yearText);
            }
        });
    }
    public void initWidgetState(){
        for(int i=0; i<12; i++){
            textViews[i].setTextColor(Color.BLACK);
            imageViews[i].setCircleBackgroundColor(Color.WHITE);
        }
    }

    private OnDateSet onDateSet;
    private OnAcceptListener onAcceptListener;
    private OnDeclineListener onDeclineListener;

    public void setOnAcceptListener(OnAcceptListener acceptListener) {
        this.onAcceptListener = acceptListener;
        super.callBackAcceptListener = new CallBackAcceptListener() {
            @Override
            public void callBack(boolean isAccept) {
                if (onDateSet != null) onDateSet.onDate(isAccept, year, month);
            }

            @Override
            public void accept() {
                MonthPicker.this.onAcceptListener.accept();
            }
        };
    }

    public interface OnDateSet{
        public void onDate(boolean isAccept,int year,int month);
    }



    public void setOnDateSet(OnDateSet dateSet) {
        this.onDateSet = dateSet;
        super.callBackAcceptListener = new CallBackAcceptListener() {
            @Override
            public void callBack(boolean isAccept) {
                onDateSet.onDate(isAccept, year, month);
            }

            @Override
            public void accept() {
                if (MonthPicker.this.onAcceptListener != null) onAcceptListener.accept();
            }
        };
    }

    public void setOnDeclineListener(final OnDeclineListener declineListener) {
        super.callBackDeclineListener = new CallBackDeclineListener() {
            @Override
            public void callBack(boolean isAccept) {
                if (onDateSet != null) onDateSet.onDate(isAccept, year, month);
            }

            @Override
            public void decline() {
                declineListener.decline();
            }
        };
    }
}
