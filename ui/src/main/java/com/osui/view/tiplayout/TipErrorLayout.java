package com.osui.view.tiplayout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.osui.R;
import com.osui.annotation.InjectReader;
import com.osui.annotation.ViewInject;
import com.osui.view.CustomLayout;


/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2018-01-10  15:55
 * @Descrition
 */

public class TipErrorLayout extends CustomLayout {

    @ViewInject
    TextView tv_head;

    public TipErrorLayout(Context context) {
        super(context);
    }

    public TipErrorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    public void initData() {
        InjectReader.injectAllFields(this);
        start();
    }

    @Override
    protected void onCreateView() {

    }


    @Override
    public int setXmlLayout() {
        return R.layout.tip_layout_head;
    }

    public boolean attached(){
        return !isFirstLoad;
    }

    public void refreshing(){
        tv_head.setText("获取中...");
    }

    public void start(){
        tv_head.setText("下拉释放刷新");
    }

    public void error(){
        tv_head.setText("获取数据异常，点击重试");
    }

}
