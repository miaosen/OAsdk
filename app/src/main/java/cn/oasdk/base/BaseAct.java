package cn.oasdk.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import cn.oaui.annotation.InjectReader;
import cn.oaui.form.Form;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2018-01-12  11:53
 * @Descrition
 */

public abstract class BaseAct extends Activity {

    public Context context;

    public boolean isFirstLoading=true;

    Form form;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 横屏
        context=this;
        initConfig();
        setContentView(getContentView());
        InjectReader.injectAllFields(this);

    }

    public abstract void initConfig();

    /**
     * @return 布局文件
     */
    public abstract int getContentView();
    /**
     * 首次运行在onWindowFocusChanged之后，在这里查找view或者填充数据速度更快
     */
    public abstract void initData();

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus&&isFirstLoading){
            initData();
            isFirstLoading=false;
        }
    }





}
