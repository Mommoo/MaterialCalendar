package com.mommoo.materialpicker.helper;


import android.app.Dialog;
import android.content.Context;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mommoo.materialpicker.R;
import com.mommoo.materialpicker.animation.AnimationCallBack;
import com.mommoo.materialpicker.animation.ClipAnimation;
import com.mommoo.materialpicker.animation.CurveAnimation;
import com.mommoo.materialpicker.animation.CurveTransitionAnimation;
import com.mommoo.materialpicker.animation.PickerAnimation;
import com.mommoo.materialpicker.manager.DIPManager;
import com.mommoo.materialpicker.manager.ScreenManager;
import com.mommoo.materialpicker.toolkit.Color;
import com.mommoo.materialpicker.toolkit.PickerDimension;
import com.mommoo.materialpicker.widget.CircleImageView;
import com.mommoo.materialpicker.widget.ClipAnimLayout;

import java.util.ArrayList;


/**
 * Created by mommoo on 2016-07-26.
 * @author mommoo , blank
 *
 * basic view for DatePicker n TimePicker
 *
 *
 */
public abstract class Picker extends Dialog implements View.OnClickListener{

    private ScreenManager sm = new ScreenManager(getContext());
    private final int DEFAULT_WIDTH = DIPManager.dip2px(280,getContext());
    private final int MINIMUM_WIDTH = DIPManager.dip2px(220,getContext());
    private final int MAXIMUM_WIDTH = 9*sm.getScreenWidth()/10;
    private static boolean isShowing;
    private int dialogWidth,themeColor,unit;
    private float textSize;
    private final int DEFAULT_COLOR = ContextCompat.getColor(getContext(), R.color.colorAccent);
    private final int CONTENT_VIEW_ADD_INDEX = 2;
    private LinearLayout mainBody;
    private TextView primaryTitle,primaryDarkTitle,decline,accept;
    private View contentView;
    private ArrayList<View> cacheViewArray = new ArrayList<>();
    protected CallBackAcceptListener callBackAcceptListener;
    protected CallBackDeclineListener callBackDeclineListener;
    protected PickerDimension pickerDimension;
    private PickerAnimation animation;
    private ClipAnimLayout rootView;
    protected Vibrator vibrator;
    protected boolean isVibrate = true,once,pickBtnClick;
    protected ImageView pickBtn;
    private OnPickBtnListener onPickBtnListener;
    private OnDialogWidthChanged dialogWidthChanged;

    public interface OnDialogWidthChanged{
        void changed(int width);
    }

    public void setOnDialogWidthChanged(OnDialogWidthChanged onDialogWidthChanged){
        this.dialogWidthChanged = onDialogWidthChanged;
    }

    public interface OnAcceptListener{
        void accept();
    }
    
    protected interface CallBackAcceptListener extends OnAcceptListener{
        void callBack(boolean isAccept);
    }
    
    public interface OnDeclineListener{
        void decline();
    }

    protected interface CallBackDeclineListener extends OnDeclineListener{
        void callBack(boolean isAccept);
    }

    protected interface OnPickBtnListener{
        public void onClick(View view, FrameLayout decoView);
    }

    protected void setOnPickBtnListener(OnPickBtnListener pickBtnListener){
        this.onPickBtnListener = pickBtnListener;
    }

    public Picker(Context context) {
        super(context,android.R.style.Theme_Translucent_NoTitleBar);
        initialize(context);
    }

    public Picker(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected Picker(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    private void initialize(Context context){
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.3f;
        this.getWindow().setAttributes(lpWindow);
        this.setContentView(R.layout.picker_view);
        this.setCancelable(false);

        pickerDimension = PickerDimension.getInstance();
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        initWidget();
        dialogWidth = DEFAULT_WIDTH;
        setDialogWidth(dialogWidth);
        setThemeColor(DEFAULT_COLOR);
    }

    private void initWidget(){
        mainBody = (LinearLayout)findViewById(R.id.mainBody);
        primaryTitle = (TextView)findViewById(R.id.primaryTitle);
        primaryDarkTitle = (TextView)findViewById(R.id.primaryDarkTitle);
        decline = (TextView)findViewById(R.id.decline);
        decline.setOnClickListener(this);
        accept = (TextView)findViewById(R.id.accept);
        accept.setOnClickListener(this);
        rootView = (ClipAnimLayout)findViewById(R.id.rootView);
        pickBtn = (ImageView)findViewById(R.id.pickBtn);
        pickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!pickBtnClick && onPickBtnListener !=null){
                    pickBtnClick = true;
                    startPickBtnAnim(view);
                }
            }
        });

    }

    protected void startPickBtnAnim(final View view){
        final CircleImageView tempImageView = new CircleImageView(getContext());
        final int viewWidth = pickBtn.getWidth();
        final int viewHeight = pickBtn.getHeight();
        int statusbarHeight = sm.getStatusBarHeight();

        if(!once){
            once = true;
            int[] location = new int[2];
            this.contentView.getLocationOnScreen(location);
            pickerDimension.setContentX(location[0]);
            pickerDimension.setContentY(location[1] -statusbarHeight);
        }

        final FrameLayout decoView = ((FrameLayout)rootView.getParent());
        decoView.addView(tempImageView, new FrameLayout.LayoutParams(viewWidth,viewHeight));
        int[] location = new int[2];
        pickBtn.getLocationOnScreen(location);
        tempImageView.setX(location[0]);
        tempImageView.setY(location[1] - statusbarHeight);
        tempImageView.setCircleBackgroundColor(pickerDimension.getPickBtnAnimCircleColor());
        tempImageView.setCirclePadding(DIPManager.dip2px(10,getContext()));
        pickerDimension.setPickBtnAnimCircleRadius((viewWidth - DIPManager.dip2px(10,getContext()))/2);
        final float fromX = location[0],fromY = location[1] - statusbarHeight;
        final float toX = (sm.getScreenWidth()- viewWidth)/2,toY = pickerDimension.getContentY() +pickerDimension.getContentHeight()/2;
        new CurveTransitionAnimation(tempImageView,toX,toY +(fromY-toY)/2,fromX,toX,fromY,toY)
                .setInterpolator(new AccelerateDecelerateInterpolator()).start(250, new AnimationCallBack() {
            @Override
            public void callBack() {
                decoView.removeView(tempImageView);
                onPickBtnListener.onClick(view,decoView);
            }
        });
    }

    /**
     *
     * @param view
     *   Content of Picker :  ex) Date, Time ...
     *
     */
    public void setDialogContentView(View view){
        if(this.contentView != null) {
            this.contentView.setTop(0);
            this.contentView.setLeft(0);
            this.contentView.setX(pickerDimension.getContentX());
            this.contentView.setY(pickerDimension.getContentY());
            mainBody.removeViewAt(CONTENT_VIEW_ADD_INDEX);
            view.setX(0);
            view.setY(0);
        }else{
            pickerDimension.setContentWidth(getContext(),getDialogWidth());
            view.setTag(0);
            view.setLayoutParams(new LinearLayout.LayoutParams(pickerDimension.getContentWidth(),pickerDimension.getContentHeight()));
        }
        if(view.getTag()==null) view.setTag(cacheViewArray.size());
        mainBody.addView(view,CONTENT_VIEW_ADD_INDEX);
        this.contentView = view;
    }

    protected View getContentView(){
        return contentView;
    }

    protected void saveContentView(){
        if((int)this.contentView.getTag()>=cacheViewArray.size()) cacheViewArray.add(this.contentView);
    }

    protected View getSavedContentView(int index){
        if(index>=cacheViewArray.size()) return null;
        return cacheViewArray.get(index);
    }

    public void setDialogWidth(int width){
        if(MINIMUM_WIDTH<=width && width<= MAXIMUM_WIDTH) {
            mainBody.getLayoutParams().width = width;
            pickerDimension.setContentWidth(getContext(),width);
            if(this.contentView != null) {
                contentView.getLayoutParams().width = width;
                contentView.getLayoutParams().height = pickerDimension.getContentHeight();
            }
            dialogWidth = width;
            if(dialogWidthChanged != null) dialogWidthChanged.changed(width);
        } else Log.e("Picker","Don't change width , you can change width from "+MINIMUM_WIDTH+" to "+MAXIMUM_WIDTH);
    }

    public void setMinimumWidth(){
        setDialogWidth(MINIMUM_WIDTH);
    }

    public void setMaximumWidth(){
        setDialogWidth(MAXIMUM_WIDTH);
    }



    public int getDialogWidth(){
        return dialogWidth;
    }

    public void setThemeColor(int color){
        themeColor = color;
        primaryTitle.setBackgroundColor(color);
        primaryDarkTitle.setBackgroundColor(Color.darker(color));
        pickBtn.setBackgroundColor(color);
    }

    public int getThemeColor(){
        return themeColor;
    }

    protected void setDialogTitle(String title){
        primaryTitle.setText(title);
    }

    protected void setDialogTitleSize(int unit,float textSize){
        primaryTitle.setTextSize(unit,textSize);
    }

    public String getDialogTitle(){
        return primaryTitle.getText().toString();
    }

    protected void setDialogStatusTitle(String title){
        primaryDarkTitle.setText(title);
    }

    public String getDialogStatusTitle(){
        return primaryDarkTitle.getText().toString();
    }

    public void setAnimation(PickerAnimation pickerAnimation){
        this.animation = pickerAnimation;
    }

    public void setVibration(boolean vibration){
        this.isVibrate = vibration;
    }

    public boolean isVibrate(){
        return isVibrate;
    }

    protected void doVibration(){
        if (isVibrate) vibrator.vibrate(30);
    }

    @Override
    public void show() {
        if(!isShowing){
            super.show();
            isShowing = true;
            if(animation ==null ){
                super.show();
                rootView.getBuilder().setAnimDuration(1);
                rootView.startAnim();
                rootView.setBackgroundColor(android.graphics.Color.TRANSPARENT);
            }else{
                if(animation instanceof CurveAnimation) startCurveAnim();
                else if(animation instanceof ClipAnimation) startClipAnim();
                else{
                    rootView.getBuilder().setAnimDuration(1);
                    rootView.setBackgroundColor(android.graphics.Color.TRANSPARENT);
                    rootView.startAnim();
                }
            }
        }
    }

    private void startCurveAnim(){
        CurveAnimation curveAnimation = (CurveAnimation)animation;
        curveAnimation.setTargetView(rootView);
        float[] startLocation = curveAnimation.getEndLocation();
        rootView.getBuilder().setAnimDuration(curveAnimation.getDuration()*2)
                .setStartLocation(startLocation[0],startLocation[1]).setStartRadius(curveAnimation.getStartRadius())
                .setAnimDelay(curveAnimation.getDuration());
        rootView.startAnim();
        rootView.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        curveAnimation.start();
    }

    private void startClipAnim(){
        ClipAnimation clipAnimation = (ClipAnimation)animation;
        clipAnimation.setTargetView(rootView);
        int duration = clipAnimation.getAnimDuration() ==0 ? clipAnimation.getDuration() : clipAnimation.getAnimDuration();
        rootView.getBuilder().setTimeInterpolator(clipAnimation.getTimeInterpolator())
                .setAnimDuration(duration).setStartLocation(clipAnimation.getFromX(),clipAnimation.getFromY());
        rootView.startAnim();
        rootView.setBackgroundColor(android.graphics.Color.TRANSPARENT);
    }

    @Override
    public void onClick(View view) {
        dismiss();
        if(view.getId()==R.id.decline){
            if(callBackDeclineListener != null){
                callBackDeclineListener.decline();
                callBackDeclineListener.callBack(false);
            }
        }
        if(view.getId()==R.id.accept){
            if(callBackAcceptListener != null) {
                callBackAcceptListener.accept();
                callBackAcceptListener.callBack(true);
            }
        }
    }

    @Override
    public void dismiss() {
        isShowing = false;
        super.dismiss();
    }
}
