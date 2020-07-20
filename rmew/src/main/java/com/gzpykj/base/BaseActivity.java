package com.gzpykj.base;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import android.view.Window;
import android.view.WindowManager;

import com.gzpykj.rmew.R;

import cn.oaui.annotation.InjectReader;
import cn.oaui.form.Form;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2018-01-12  11:53
 * @Descrition
 */

public abstract class BaseActivity extends Activity {

    public Context context;

    public boolean isFirstLoading=true;

    Form form;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 横屏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.blue));
            //底部导航栏
            //window.setNavigationBarColor(activity.getResources().getColor(colorResId));
        }
        context=this;
        initConfig();
        setContentView(getContentView());
        InjectReader.injectAllFields(this);
        onViewCreate(savedInstanceState);

    }

    public abstract void initConfig();

    /**
     * @return 布局文件
     */
    public abstract int getContentView();
    /**
     * 执行于onCreate方法
     * @param savedInstanceState
     */
    public abstract void onViewCreate(Bundle savedInstanceState);
    /**
     * 首次运行在onWindowFocusChanged之后，在这里查找view或者填充数据速度更快
     */
    public abstract void initData();

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            initData();
            isFirstLoading=false;
        }
    }





}
