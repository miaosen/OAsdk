package com.osui.view.dialog;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.osui.utils.AppUtils;
import com.osui.utils.ViewUtils;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2017-02-22 15:24
 * @Descrition 依赖activity的弹窗，本质是ViewGroup,会阻塞ui线程
 */

public class FrameDialog extends ViewGroup implements DialogInterface {

    public View dialogView;

    // 窗体外边距
    int dialogMargin = AppUtils.dip2px(50);

    int screenHeight = AppUtils.getScreenHeight();
    int screenWidth = AppUtils.getScreenWidth();
    int statusHeight = AppUtils.getStatusHeight();

    FrameLayout rootLayout;

    /**
     * 是否附加到activity的布局中
     */
    boolean isAttachToWindow = false;

    //相对位置向上或者向下时，dialogview的显示宽度范围默认是从相对的控件开始的,设置为true是从0开始
    boolean isFillwidth = false;

    //边框阴影
    View borderView;
    int borderWidth =2;

    int modeWidth = 0;
    int modeHeight = 0;
    OnDismissListener onDismissListener;
    OnShowListener onShowListener;

    //弹窗是否获取焦点，按返回键关闭弹窗需要焦点，组合下拉框必须去除焦点
    boolean isFocuse=true;

    public static interface SHOW_TYPE {
        int CENTER = 0;// 居中
        int FULL = 1;// 全屏
        int AS_UP = 2;// 在view上方
        int AS_DOWN = 3;// 在view下方
    }

    int[] viewLocation = new int[]{0, 0};

    public int showType = SHOW_TYPE.CENTER;

    public FrameDialog(Context context, View dialogView) {
        super(context);
        this.dialogView = dialogView;
        init();
    }

    public FrameDialog( View dialogView) {
        super(dialogView.getContext());
        this.dialogView = dialogView;
        init();
    }

    public FrameDialog(Context context, int dialogLayout) {
        super(context);
        this.dialogView = ViewUtils.inflatView(context, dialogLayout);
        init();
    }

    public FrameDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @SuppressLint("NewApi")
    protected void init() {
        //全屏时状态栏高度为0
        if(AppUtils.isFullScreen(getContext())){
            statusHeight=0;
        }
        setShadow(true);
        setBorder();
        addView(dialogView);
        onInitDialog(dialogView);
        setClickShadowToDismiss();
        coverOnClickListener();
    }

    @SuppressLint("NewApi")
    private void setBorder() {
        //borderView =new View(getContext());
        ////TODO 阴影效果
        //borderView.setBackground(getResources().getDrawable(R.drawable.shape_dlg_shadow));
        //addView(borderView);
    }

    /**
     * 覆盖view的监听
     */
    public void coverOnClickListener() {
        dialogView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    protected void onInitDialog(View dialogView) {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        //TODO widthMeasureSpec和viewLocation单位不一致
        // 上级容器大小
        setMeasuredDimension(sizeWidth, sizeHeight);
        // 调用子view测量方法
        if (showType == SHOW_TYPE.CENTER) {
            measureChildren(widthMeasureSpec - (dialogMargin * 2),
                    heightMeasureSpec - (dialogMargin * 2));
        } else if (showType == SHOW_TYPE.AS_UP) {
            if (isFillwidth) {
                measureChildren(widthMeasureSpec,
                        heightMeasureSpec - (screenHeight - viewLocation[1]-statusHeight));
            } else {
                measureChildren(widthMeasureSpec - viewLocation[0],
                        heightMeasureSpec - (screenHeight - viewLocation[1]-statusHeight));
            }
        } else if (showType == SHOW_TYPE.AS_DOWN) {
            if (isFillwidth) {
                measureChildren(widthMeasureSpec,
                        heightMeasureSpec - viewLocation[1]);
            } else {
                measureChildren(widthMeasureSpec - viewLocation[0],
                        heightMeasureSpec - viewLocation[1]);
            }
        } else {
            measureChildren(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //View childAt = getChildAt(0);
        int childHeight = dialogView.getMeasuredHeight();
        int childWidth = dialogView.getMeasuredWidth();
        int heightStart = 0;
        int widthStart = 0;
        if (showType == SHOW_TYPE.CENTER) {
            // 如果页面能占满屏幕，给它一个外边距
            if (childHeight >= screenHeight - statusHeight) {
                heightStart = dialogMargin;
            } else {
                heightStart = (screenHeight - childHeight - statusHeight) / 2;
            }
            // 如果页面能占满屏幕，给它一个外边距
            if (childWidth >= screenWidth) {
                widthStart = dialogMargin;
            } else {
                widthStart = (screenWidth - childWidth) / 2;
            }
        } else if (showType == SHOW_TYPE.AS_UP) {
            heightStart = viewLocation[1] - childHeight ;
            if (isFillwidth) {
                widthStart = 0;
            } else {
                widthStart = viewLocation[0];
            }
        } else if (showType == SHOW_TYPE.AS_DOWN) {
            heightStart = viewLocation[1];
            if (isFillwidth) {
                widthStart = 0;
            } else {
                widthStart = viewLocation[0];
            }
        } else {// showType==SHOW_TYPE.FULL

        }
        // if(modeWidth== MeasureSpec.EXACTLY){
        // childAt.layout(widthStart, heightStart, screenWidth, heightStart +
        // childHeight);
        // }else{
        dialogView.layout(widthStart, heightStart, widthStart + childWidth,
                heightStart + childHeight);
        //borderView.layout(widthStart- borderWidth, heightStart- borderWidth, widthStart + childWidth+ borderWidth,
        //        heightStart + childHeight+ borderWidth);
        // }

    }

    @Override
    public void cancel() {

    }

    @Override
    public void dismiss() {
        setVisibility(View.GONE);
        clearFocus();
        if (onDismissListener != null) {
            onDismissListener.onDismiss(this);
        }
    }

    public void show() {
        addToContentViewOrInvalidate();
        setVisibility(View.VISIBLE);
        if (onShowListener != null) {
            onShowListener.onShow(this);
        }

    }


    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if(isFocuse){
            //TODO 在弹窗之上建立弹窗，返回键监听不到
            // 使物理按键监听生效
            requestFocus();
            setFocusableInTouchMode(true);
        }

    }

    public void fullscreen() {
        showType = SHOW_TYPE.FULL;
        dialogView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
        show();
    }

    public void showAsUp(View view) {
        view.getLocationInWindow(viewLocation);
        viewLocation[1] = viewLocation[1] - statusHeight ;
        showType = SHOW_TYPE.AS_UP;
        show();
    }

    public void showAsCoverUp(View view) {
        view.getLocationInWindow(viewLocation);
        viewLocation[1] = viewLocation[1] + view.getMeasuredHeight()
                - statusHeight ;
        showType = SHOW_TYPE.AS_UP;
        show();
    }

    public void showAsDown(View view) {
        view.getLocationInWindow(viewLocation);
        viewLocation[1] = viewLocation[1] + view.getMeasuredHeight()
                - statusHeight;
        showType = SHOW_TYPE.AS_DOWN;
        show();
    }


    public void showAsCoverDown(View view) {
        view.getLocationInWindow(viewLocation);
        viewLocation[1] = viewLocation[1] - statusHeight;
        showType = SHOW_TYPE.AS_DOWN;
        show();
    }




    /**
     * 是否设置阴影
     *
     * @param isShowShadow
     */
    public void setShadow(boolean isShowShadow) {
        if (isShowShadow) {
            // 半透明
            setBackgroundColor(Color.parseColor("#66000000"));
        } else {
            // 半透明
            setBackgroundColor(Color.TRANSPARENT);
        }
    }

    /**
     * 点击阴影消失
     */
    public void setClickShadowToDismiss() {
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                dismiss();
                return true;
            }
        });
        // setOnClickListener(new View.OnClickListener() {
        // @Override
        // public void onClick(View v) {
        // Logger.i("setClickShadowToDismiss=========================");
        // dismiss();
        // }
        // });
    }

    public void addToContentViewOrInvalidate() {
        if (!isAttachToWindow) {
            rootLayout = (FrameLayout) ViewUtils.getContentView(getContext())
                    .getParent();
            rootLayout.addView(this);
            isAttachToWindow = true;
            setVisibility(View.GONE);
        } else {
            invalidate();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode()== KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                dismiss();
                return true;
            }
            return false;
        }
        return super.dispatchKeyEvent(event);
    }

    public boolean isShowing() {
        return getVisibility() == View.VISIBLE;
    }

    public OnDismissListener getOnDismissListener() {
        return onDismissListener;
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public boolean isFillwidth() {
        return isFillwidth;
    }

    public void setFillwidth(boolean isFillwidth) {
        this.isFillwidth = isFillwidth;
    }

    public OnShowListener getOnShowListener() {
        return onShowListener;
    }

    public void setOnShowListener(OnShowListener onShowListener) {
        this.onShowListener = onShowListener;
    }

    public int getShowType() {
        return showType;
    }

    public void setShowType(int showType) {
        this.showType = showType;
    }

    public boolean isFocuse() {
        return isFocuse;
    }

    public void setFocuse(boolean focuse) {
        isFocuse = focuse;
    }
}
