package com.mommoo.materialpicker;


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

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Created by mommoo on 2016-07-26.
 * @author mommoo , blank
 *
 * basic view for DatePicker n TimePicker
 */
abstract class Picker extends Dialog implements View.OnClickListener{

    private ScreenManager sm = new ScreenManager(getContext());
    private final int DEFAULT_WIDTH = DIPManager.dip2px(280,getContext());
    private final int MINIMUM_WIDTH = DIPManager.dip2px(220,getContext());
    private final int MAXIMUM_WIDTH = 9*sm.getScreenWidth()/10;
    private static boolean isShowing;
    private int dialogWidth,themeColor;
    private final int DEFAULT_COLOR = ContextCompat.getColor(getContext(), R.color.colorAccent);
    private final int CONTENT_VIEW_ADD_INDEX = 2;
    private int forceHeight;
    private LinearLayout mainBody;
    private TextView primaryTitle,primaryDarkTitle,decline,accept;
    private View contentView;
    private ArrayList<View> cacheViewArray = new ArrayList<>();
    protected CallBackAcceptListener callBackAcceptListener;
    protected CallBackDeclineListener callBackDeclineListener;
    protected PickerDimension pickerDimension;
    private PickerAnimation animation;
    private ClipAnimLayout rootView;
    private Vibrator vibrator;
    protected boolean isVibrate = true,once,pickBtnClick,prevent,scrollMode;
    protected ImageView pickBtn;
    private OnPickBtnListener onPickBtnListener;
    private OnDialogWidthChanged dialogWidthChanged;

    public interface OnDialogWidthChanged{
        void changed(int width);
    }

    public void setOnDialogWidthChanged(OnDialogWidthChanged onDialogWidthChanged){
        this.dialogWidthChanged = onDialogWidthChanged;
    }
    
    protected interface CallBackAcceptListener extends OnAcceptListener{
        void callBack(boolean isAccept);
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
        changeDialogWidth(dialogWidth);
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

    protected FrameLayout getDecoView(){
        return (FrameLayout)rootView.getParent();
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


    protected void forceChangeContentHeight(int contentHeight){
        this.forceHeight = contentHeight;
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
            view.setLayoutParams(new LinearLayout.LayoutParams(pickerDimension.getContentWidth(),forceHeight==0?pickerDimension.getContentHeight():forceHeight));
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

    protected void saveContentView(int position,View view){
        if(position <= cacheViewArray.size()-1) cacheViewArray.set(position,view);
        else if(position == cacheViewArray.size()){
            cacheViewArray.add(view);
        }else{
            System.out.println("ArrayOutOfIndex Error");
        }
    }

    protected View getSavedContentView(int index){
        if(index>=cacheViewArray.size()) return null;
        return cacheViewArray.get(index);
    }

    protected void preventChangeWidth(boolean prevent){
        this.prevent = prevent;
    }

    public void setDialogWidth(int width){
        if(!prevent) changeDialogWidth(width);
    }

    private void changeDialogWidth(int width){
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

    public void setScrollMode(boolean scrollMode){
        this.scrollMode = scrollMode;
        saveContentView();
        setDialogContentView(getSavedContentView(scrollMode?1:0));
    }

    public boolean isScrollMode(){
        return this.scrollMode;
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
        if (isVibrate) {
            try{
                vibrator.vibrate(30);
            }catch(SecurityException se) {
                Log.i("error", "please check vibrate permission");
            }
        }
    }

    public void changeAcceptButtonText(String text){
        accept.setText(text);
    }

    public String getAcceptButtonText(){
        return accept.getText().toString();
    }

    public void changeDeclineButtonText(String text){
        decline.setText(text);
    }

    public String getDeclineButtonText(){
        return decline.getText().toString();
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
        rootView.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        rootView.startAnim();
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
