package cn.oasdk.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cn.oahttp.HttpRequest;
import cn.oahttp.callback.StringCallBack;
import cn.oasdk.R;
import cn.oaui.annotation.InjectReader;
import cn.oaui.annotation.ViewInject;
import cn.oaui.data.RowObject;
import cn.oaui.form.Form;
import cn.oaui.utils.JsonUtils;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
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
        HttpRequest request = new HttpRequest(url);
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
            //Log.i("logtag", "getContentValue======" + form.getContentValue().size());
            //textView.setText(form.getContentValue().toString());
        } else if (v.getId() == R.id.fillForm) {
            getData();
        }
    }
}
