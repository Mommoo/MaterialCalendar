package com.mommoo.materialcalendar.helper;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.mommoo.materialcalendar.R;
import com.mommoo.materialcalendar.manager.DIPManager;
import com.mommoo.materialcalendar.toolkit.PickerDimension;
import com.mommoo.materialcalendar.widget.ClipAnimLayout;
import com.mommoo.materialcalendar.widget.NotifyListView;

/**
 * Created by mommoo on 2016-08-04.
 */
public abstract class ScrollPickerView extends ClipAnimLayout {

    private final int count = 3;
    protected NotifyListView[] pickListView = new NotifyListView[count];

    public ScrollPickerView(Context context, ClipAnimLayout.Builder builder) {
        super(context, builder);
        initialize(context);
    }

    public ScrollPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public ScrollPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    public ScrollPickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context);
    }

    private void initialize(Context context){
        PickerDimension pickerDimension = PickerDimension.getInstance();
        int padding = DIPManager.dip2px(40,getContext());
        int viewWidth = pickerDimension.getContentWidth()/3;
        int itemHeight = (pickerDimension.getContentHeight() - (padding*2))/5;
        for(int i=0;i<count;i++){
            pickListView[i] = new NotifyListView(context);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(viewWidth, pickerDimension.getContentHeight()-(padding*2));
            params.setMargins(0,padding,0,0);
            pickListView[i].setLayoutParams(params);
            addView(pickListView[i]);
        }
        pickListView[0].setX(viewWidth/5);
        pickListView[1].setX(viewWidth);
        pickListView[2].setX(9*viewWidth/5);

        View[] lines = new View[6];
        int index = 0;
        for(View line : lines){
            lines[index] = getLineAppearance(viewWidth);
            addView(lines[index]);
            if(index<3) {
                lines[index].setY((itemHeight*2)+padding- DIPManager.dip2px(4,getContext()));
                lines[index].setX(viewWidth/4 + (viewWidth*index) +(index==0?viewWidth/5:0) -(index==2?viewWidth/5:0));
            }
            else{
                lines[index].setY((itemHeight*3)+padding + DIPManager.dip2px(4,getContext()));
                lines[index].setX(viewWidth/4 + (viewWidth*(index-3))+(index==3?viewWidth/5:0)-(index==5?viewWidth/5:0));
            }
            index++;
        }
    }
    private View getLineAppearance(int viewWidth){
        View view = new View(getContext());
        view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.dividerColor));
        view.setLayoutParams(new FrameLayout.LayoutParams(viewWidth/2, DIPManager.dip2px(1,getContext())));
        return view;
    }
}
