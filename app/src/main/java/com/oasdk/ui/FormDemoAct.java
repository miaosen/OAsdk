package com.oasdk.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.oahttp.NetRequest;
import com.oahttp.callback.StringCallBack;
import com.oasdk.R;
import com.oaui.annotation.InjectReader;
import com.oaui.annotation.ViewInject;
import com.oaui.data.RowObject;
import com.oaui.form.Form;
import com.oaui.utils.JsonUtils;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @git http://git.oschina.net/miaosen/MyUtils
 * @CreateDate 2017-08-14  14:45
 * @Descrition
 */

public class FormDemoAct extends Activity implements View.OnClickListener {

    Form form;

    @ViewInject
    Button fillForm, getForm;
    @ViewInject
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_demo_act);
        InjectReader.injectAllFields(this);
        form = new Form(this);
        fillForm.setOnClickListener(this);
        getForm.setOnClickListener(this);
    }

    private void getData() {
        String url = "http://wthrcdn.etouch.cn/weather_mini?citykey=101010100";
        NetRequest request = new NetRequest(url);
        request.setCallback(new StringCallBack() {
            @Override
            public void onSuccess(String text) {
                form.fill(text);
                Log.i("logtag", "text======" + text);
                //String expression="data.forecast[0].fengli";
                //String expression="data.forecast[5]";
                //String expression="data.yesterday.date";
                String expression = "data.ganmao";
                //String expression="status[0]";
                //String expression="data";
                RowObject rowObject = JsonUtils.jsonToRow(text);
                // RowObject layerData = (RowObject) rowObject.getLayerData(expression);
                Log.i("logtag", "layerData======" + rowObject.getLayerData(expression));
            }
            @Override
            protected void onFail(Exception e) {
                super.onFail(e);
                e.printStackTrace();
            }
        });
        request.sendByGet();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.getForm) {
            Log.i("logtag", "getContentValue======" + form.getContentValue().size());
            textView.setText(form.getContentValue().toString());
        } else if (v.getId() == R.id.fillForm) {
            getData();
        }
    }
}
