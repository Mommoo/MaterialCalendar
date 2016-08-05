package com.mommoo.materialpicker.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.mommoo.materialpicker.helper.NotifyListViewAdapter;


/**
 * Created by mommoo on 2016-07-30.
 */
public class NotifyListView extends ListView {

    private int lastFirstVisibleItem = -1;
    private boolean notify,flag;
    private NotifyListViewAdapter pickAdapter;

    public NotifyListView(Context context) {
        super(context);
        initialize(context);
    }

    public NotifyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public NotifyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    @TargetApi(21)
    public NotifyListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context);
    }

    @Override
    public void setSelection(int position) {
        super.setSelection(position);
        pickAdapter.setFirstVisiblePosition(position);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        this.pickAdapter = (NotifyListViewAdapter)adapter;
    }


    public void initialize(Context context){
        setDivider(null);
        setVerticalScrollBarEnabled(false);
        setSelector(new ColorDrawable(Color.TRANSPARENT));
        setOverScrollMode(OVER_SCROLL_NEVER);

        setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {

                if(scrollState == 0){
                    final View view = absListView.getChildAt(0);
                    if(view !=null){
                        setSelection(lastFirstVisibleItem);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(lastFirstVisibleItem == -1) lastFirstVisibleItem = firstVisibleItem;
                else if(lastFirstVisibleItem != firstVisibleItem) {
                    lastFirstVisibleItem = firstVisibleItem;
                    pickAdapter.notifyDataSetChanged(firstVisibleItem);
                }
            }
        });
    }

}
