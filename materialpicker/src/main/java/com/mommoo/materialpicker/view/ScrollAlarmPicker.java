package com.mommoo.materialpicker.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.mommoo.materialpicker.helper.ScrollPickerView;
import com.mommoo.materialpicker.widget.ClipAnimLayout;

/**
 * Created by mommoo on 2016-08-10.
 */
public class ScrollAlarmPicker extends FrameLayout{

    public ScrollAlarmPicker(Context context) {
        super(context);
        initialize(context);
    }

    public ScrollAlarmPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public ScrollAlarmPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    @TargetApi(21)
    public ScrollAlarmPicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context);
    }

    public void initialize(Context context){

    }

}
