package com.gzpykj.rmew.riskcomp;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.gzpykj.base.BaseActivity;
import com.gzpykj.base.Global;
import com.gzpykj.base.JsonHandler;
import com.gzpykj.rmew.R;

import cn.oahttp.HttpRequest;
import cn.oahttp.callback.StringCallBack;
import cn.oaui.annotation.ViewInject;
import cn.oaui.data.RowObject;
import cn.oaui.form.Form;
import cn.oaui.utils.IntentUtils;
import cn.oaui.utils.ViewUtils;
import cn.oaui.view.FoldingLayout;
import cn.oaui.view.TailView;

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

    @ViewInject
    FoldingLayout fd_nclist,fd_cflist,fd_cylist,fd_tslist;



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
                JsonHandler jsonHandler=new JsonHandler(text);
                Form form=new Form(context);
                RowObject data = jsonHandler.getAsRow().getRow("data");
                form.fill(data.getRow("compay_info"));
                form.fill(data);
                if(data.getRows("nclist")!=null&&data.getRows("nclist").size()>0){
                    fd_nclist.setVisibility(View.VISIBLE);
                }
                if(data.getRows("cflist")!=null&&data.getRows("cflist").size()>0){
                    fd_cflist.setVisibility(View.VISIBLE);
                }
                if(data.getRows("cylist")!=null&&data.getRows("cylist").size()>0){
                    fd_cylist.setVisibility(View.VISIBLE);
                }
                if(data.getRows("tslist")!=null&&data.getRows("tslist").size()>0){
                    fd_tslist.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected void onFail(Exception e) {
                super.onFail(e);
                ViewUtils.toast("获取信息错误："+e.getMessage());
            }
        });
        request.send();

        //ViewUtils.setClickable(ln_content,false);
        tailView.addTailItemListener("确定", new TailView.OnTailItemListener() {
            @Override
            public void onClickItem() {
                finish();
            }
        });
    }
}
