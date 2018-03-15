package com.osui.view.tiplayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
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

public class TipHeadLayout extends CustomLayout {

    @ViewInject
    TextView tv_head,tv_error;
    @ViewInject
    public LinearLayout error_in_head,header;

    public TipHeadLayout(Context context) {
        super(context);
    }

    public TipHeadLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    public void initData() {
        start();
    }

    @Override
    protected void onCreateView() {
        InjectReader.injectAllFields(this);
    }


    @Override
    public int setXmlLayout() {
        return R.layout.tip_layout_head;
    }

    public boolean attached(){
        return !isFirstLoad;
    }

    public void refreshing(){
        error_in_head.setVisibility(View.GONE);
        header.setVisibility(View.VISIBLE);
        tv_head.setText("获取中...");
    }

    public void start(){
        error_in_head.setVisibility(View.GONE);
        header.setVisibility(View.VISIBLE);
        tv_head.setText("下拉释放刷新");
    }

    public void error(){
        error_in_head.setVisibility(View.VISIBLE);
        header.setVisibility(View.GONE);
    }

    public void error(String text){
        error_in_head.setVisibility(View.VISIBLE);
        header.setVisibility(View.GONE);
        tv_error.setText(text);
    }

}
