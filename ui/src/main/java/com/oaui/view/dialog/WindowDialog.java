package com.oaui.view.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.oaui.UIGlobal;
import com.oaui.utils.AppUtils;
import com.oaui.utils.ViewUtils;


/**
 * @author zms
 * @Created by gzpykj.com
 * @Date 2016-1-24
 * @Descrition 使用WindowManager 添加view建立弹窗，依赖于application,可跨activity使用
 */
public class WindowDialog extends FrameLayout implements DialogInterface {

    private static final String TAG = "WindowDialog";

    public int screenWidth;
    public int screenHeight;

    private WindowManager windowManager;
    private WindowManager.LayoutParams param;
    private boolean isShowing = false;

    public View dialogLayout = null;


    public WindowDialog(View dialogLayout) {
        super(dialogLayout.getContext());
        this.dialogLayout = dialogLayout;
        initDisplay();
    }

    public WindowDialog(int layoutId) {
        super(UIGlobal.getApplication());
        this.dialogLayout = ViewUtils.inflatView(UIGlobal.getApplication(),layoutId);
        initDisplay();

        // 退出app时隐藏弹窗
        //AppFactory.getAppContext().setOnTrimMemoryListenr(TAG,
        //		new OnTrimMemoryListenr() {
        //			@Override
        //			public void onTrimMemory() {
        //				dismiss();
        //			}
        //		});

    }


    /**
     * 初始化屏幕
     */
    private void initDisplay() {
        addView(dialogLayout);
        windowManager = (WindowManager) UIGlobal.getApplication()
                .getSystemService(Context.WINDOW_SERVICE);
        // 窗口宽度
        screenWidth = AppUtils.getScreenWidth();
        screenHeight = AppUtils.getScreenHeight();
        param = new WindowManager.LayoutParams();
        param.height = screenHeight;
        param.width = screenWidth;
        param.format = 1;
        param.flags = android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
                | android.view.WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        param.type = android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

        // 设置dialog高度等于宽度
        // if (screenWidth > screenHeight) {
        // screenWidth = screenHeight;
        // }
        // 设置弹窗大小

    }


    /**
     * 显示
     */
    public void show() {
        if (!isShowing()) {
            windowManager.addView(this, param);
        }
        isShowing = true;
    }

    /**
     * 隐藏
     */
    @Override
    public void dismiss() {
        if (isShowing()) {
            try {
                windowManager.removeView(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        isShowing = false;
    }


    /**
     * 点击返回键事件处理
     */
    public View.OnKeyListener backlistener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                dismiss();
                return true; // 已处理
            }
            return false;
        }
    };

    /**
     * view监听事件
     *
     */
    class mClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v == dialogLayout) {
                dismiss();
            }
        }

    }


    public boolean isShowing() {
        return isShowing;
    }

    public void setShowing(boolean isShowing) {
        this.isShowing = isShowing;
    }

    @Override
    public void cancel() {
        // TODO Auto-generated method stub

    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                dismiss();
                return true;
            }
            return false;
        }
        return super.dispatchKeyEvent(event);
    }


}
