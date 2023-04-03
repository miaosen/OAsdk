package cn.oasdk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cn.oahttp.HttpRequest;
import cn.oahttp.callback.StringCallBack;
import cn.oasdk.R;
import cn.oasdk.base.BaseAct;
import cn.oasdk.base.EventBus;
import cn.oaui.annotation.ViewInject;
import cn.oaui.data.Row;
import cn.oaui.utils.JsonUtils;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2017-08-04  15:09
 * @Descrition
 */

public class UIDemoAct extends BaseAct {
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                //MainActivity activity = (MainActivity) EventBus.getObject("refresh");
                //activity.refresh("给我刷新！");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.removeObject("refresh");
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
                Row row = JsonUtils.jsonToRow(text);
               // RowObject layerData = (RowObject) rowObject.getLayerData(expression);
                Log.i("logtag", "layerData======" + row.getLayerData(expression));
                textView.setText(row.getLayerData(expression)+"");
            }
            @Override
            protected void onFail(Exception e) {
                super.onFail(e);
                e.printStackTrace();
            }
        });
        request.sendByGetAsync();
    }


}

