package cn.oasdk.webview;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import cn.oasdk.base.BaseAct;
import cn.oasdk.fileview.R;
import cn.oaui.ResourceHold;
import cn.oaui.annotation.InjectReader;
import cn.oaui.annotation.ViewInject;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2020-08-18  11:40
 * @Descrition
 */
public class SettingAct extends BaseAct {

    @ViewInject
    LinearLayout ln_back;

    @ViewInject
    TextView tv_title;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_act);
        InjectReader.injectAllFields(this);
        ln_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_title.setText(ResourceHold.getString(R.string.setting));



    }
}
