package com.mommoo.materialcalendar;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.mommoo.materialpicker.AlarmPicker;
import com.mommoo.materialpicker.DatePicker;
import com.mommoo.materialpicker.TimePicker;
import com.mommoo.materialpicker.animation.ClipAnimation;
import com.mommoo.materialpicker.animation.CurveAnimation;
import com.mommoo.materialpicker.animation.PickerAnimation;
import com.mommoo.materialpicker.helper.Picker;
import com.mommoo.materialpicker.manager.DIPManager;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePicker datePicker = new DatePicker(TestActivity.this,2016,0,21);
                int[] location = new int[2];
                view.getLocationOnScreen(location);
                float fromX = location[0], fromY = location[1];
                CurveAnimation curveAnimation = new CurveAnimation(new CurveAnimation.Builder()
                        .setStartRadius(DIPManager.dip2px(20,TestActivity.this)).setStartLocation(fromX,fromY).setAnimCallBack(new PickerAnimation.AnimCallBack() {
                            @Override
                            public void callBack() {
                                System.out.println("callBack");
                            }
                        }));

                datePicker.setAnimation(curveAnimation);
                datePicker.setOnAcceptListener(new Picker.OnAcceptListener() {
                    @Override
                    public void accept() {
                        System.out.println("accept");
                    }
                });
                datePicker.setOnDeclineListener(new Picker.OnDeclineListener() {
                    @Override
                    public void decline() {
                        System.out.println("decline");
                    }
                });
                datePicker.setOnDateSet(new DatePicker.OnDateSet() {
                    @Override
                    public void onDate(boolean accept, int year, int month, int date) {
                        System.out.println("isAccept : "+accept+" , year : "+year +" , month : " +month+" , date : "+date);
                        TimePicker timePicker =new TimePicker(TestActivity.this,10,55,1);
                        timePicker.show();
                    }
                });


                datePicker.show();
            }
        });
        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePicker timePicker = new TimePicker(TestActivity.this);
                //timePicker.setMode(TimePicker.MINUTE_MODE);
                //timePicker.setMaximumWidth();
                //timePicker.setMinimumWidth();
                int[] location = new int[2];
                view.getLocationOnScreen(location);
                ClipAnimation clipAnimation = new ClipAnimation(location[0],location[1]);
                clipAnimation.setAnimDuration(700);
                timePicker.setAnimation(clipAnimation);
                timePicker.setOnTimeSet(new TimePicker.OnTimeSet() {
                    @Override
                    public void onTime(boolean isAccept, int hour, int minute, int am_pm) {
                        System.out.println("isAccept : "+isAccept+" , hour : "+hour + " , minute : "+minute + " , am_pm : "+am_pm);
                    }
                });
                timePicker.show();
            }
        });
        findViewById(R.id.btn3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmPicker alarmPicker = new AlarmPicker(TestActivity.this,-10,1,10,12);
                alarmPicker.setOnAlarmSet(new AlarmPicker.OnAlarmSet() {
                    @Override
                    public void onAlarm(boolean isAccept, int dDay, int am_pm, int hour, int minute) {
                        System.out.println(isAccept+","+dDay+","+am_pm+","+hour+","+minute);
                    }
                });
                alarmPicker.show();
            }
        });
    }
}
