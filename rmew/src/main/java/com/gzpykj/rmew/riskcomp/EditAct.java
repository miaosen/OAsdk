package com.gzpykj.rmew.riskcomp;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.gzpykj.base.BaseActivity;
import com.gzpykj.base.Global;
import com.gzpykj.base.JsonHandler;
import com.gzpykj.rmew.R;
import com.oahttp.HttpRequest;
import com.oahttp.callback.StringCallBack;
import com.oaui.annotation.ViewInject;
import com.oaui.data.RowObject;
import com.oaui.form.Form;
import com.oaui.utils.IntentUtils;
import com.oaui.utils.ViewUtils;
import com.oaui.view.TailView;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2019-03-19  15:49
 * @Descrition
 */

public class EditAct extends BaseActivity {

    @ViewInject
    LinearLayout ln_content;

    @ViewInject
    TailView tailView;

    @Override
    public void initConfig() {

    }

    @Override
    public int getContentView() {
        return R.layout.riskcomp_edit_act;
    }

    @Override
    public void onViewCreate(Bundle savedInstanceState) {

    }

    @Override
    public void initData() {
        RowObject row = IntentUtils.getRow(getIntent(), ListAct.ROW_ITEM);
        //L.i("=========initData=============="+row);
        HttpRequest request = Global.createRequest("/jsp/rmew/mobile/riskenterprise.jsp?getInfoById");
        request.addParam("mainId",row.getString("mainId"));
        request.setCallback(new StringCallBack() {
            @Override
            public void onSuccess(String text) {
                //L.i("=========onSuccess=============="+text);
                JsonHandler jsonHandler=new JsonHandler(text);
                Form form=new Form(context);
                form.fill(jsonHandler.getAsRow().getRow("data").getRow("compay_info"));
            }

            @Override
            protected void onFail(Exception e) {
                super.onFail(e);
                ViewUtils.toast("获取信息错误："+e.getMessage());
            }
        });
        request.send();

        ViewUtils.setClickable(ln_content,false);
        tailView.addTailItemListener("确定", new TailView.OnTailItemListener() {
            @Override
            public void onClickItem() {
                finish();
            }
        });
    }
}
