package com.gzpykj.rmew.riskmap;

import android.os.Bundle;

import com.gzpykj.base.BaseActivity;
import com.gzpykj.base.Global;
import com.gzpykj.base.RecordWebView;
import com.gzpykj.rmew.R;

import cn.oaui.annotation.ViewInject;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2019-03-26  14:24
 * @Descrition
 */

public class RiskCompanyMapAct extends BaseActivity {

    @ViewInject
    RecordWebView recordWebView;


    @Override
    public void initConfig() {

    }

    @Override
    public int getContentView() {
        return R.layout.riskcomp_map_act;
    }

    @Override
    public void onViewCreate(Bundle savedInstanceState) {

    }

    @Override
    public void initData() {
        //recordWebView.loadUrl("http://47.97.191.166:8080/rmew");
        String url= Global.HOST+"/jsp/rmew/gis/enterprise_map.jsp";
        recordWebView.loadUrl(url);
        //recordWebView.loadUrl("https://map.baidu.com/");
        //recordWebView.loadUrl("file:///android_asset/activity_cp_fly_check.html");
    }


}
