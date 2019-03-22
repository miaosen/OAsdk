package com.oasdk.ui;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.oahttp.HttpRequest;
import com.oahttp.callback.StringCallBack;
import com.oasdk.R;
import com.oasdk.base.BaseActivity;
import com.oaui.annotation.ViewInject;
import com.oaui.data.RowObject;
import com.oaui.utils.JsonUtils;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2017-08-04  15:09
 * @Descrition
 */

public class UIDemoAct extends BaseActivity {
    //变量名和id相同
    @ViewInject
    Button analysisBtn,formBtn,dialogBtn,refresh;

    @ViewInject
    TextView textView;
    //变量名和id不同
    @ViewInject(R.id.holderBtn)
    Button holder;


    @Override
    public void initConfig() {

    }

    @Override
    public int getContentView() {
        return R.layout.ui_demo_act;
    }

    @Override
    public void onViewCreate() {
        analysisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendByPost();
                Intent intent=new Intent(context,DataDemoAct.class);
                startActivity(intent);
            }
        });
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        formBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UIDemoAct.this, FormDemoAct.class);
                startActivity(intent);
            }
        });

        dialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,DialogDemoAct.class);
                startActivity(intent);
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UIDemoAct.this, TipLayoutDemoAct.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void initData() {

    }


    private void sendByPost() {
        String url="http://wthrcdn.etouch.cn/weather_mini?citykey=101010100";
        HttpRequest request = new HttpRequest(url);
        request.setCallback(new StringCallBack() {
            @Override
            public void onSuccess(String text) {
                Log.i("logtag", "text======" + text);
                //String expression="data.forecast[0].fengli";
                //String expression="data.forecast[5]";
                //String expression="data.yesterday.date";
                String expression="data.yesterday";
                //String expression="status[0]";
                //String expression="data";
                RowObject rowObject = JsonUtils.jsonToRow(text);
               // RowObject layerData = (RowObject) rowObject.getLayerData(expression);
                Log.i("logtag", "layerData======" + rowObject.getLayerData(expression));
                textView.setText(rowObject.getLayerData(expression)+"");
            }
            @Override
            protected void onFail(Exception e) {
                super.onFail(e);
                e.printStackTrace();
            }
        });
        request.sendByGet();
    }


}

