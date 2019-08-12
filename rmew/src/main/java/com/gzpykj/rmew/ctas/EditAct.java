package com.gzpykj.rmew.ctas;

import android.os.Bundle;

import com.gzpykj.base.BaseActivity;
import com.gzpykj.rmew.R;

import cn.oaui.L;
import cn.oaui.annotation.ViewInject;
import cn.oaui.data.RowObject;
import cn.oaui.form.Form;
import cn.oaui.utils.IntentUtils;
import cn.oaui.view.TailView;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2019-03-19  15:49
 * @Descrition
 */

public class EditAct extends BaseActivity {

    @ViewInject
    TailView tailView;

    @Override
    public void initConfig() {

    }

    @Override
    public int getContentView() {
        return R.layout.ctas_edit_act;
    }

    @Override
    public void onViewCreate(Bundle savedInstanceState) {

    }

    @Override
    public void initData() {
        RowObject row = IntentUtils.getRow(getIntent(), ListAct.ROW_ITEM);
        L.i("=========initData=============="+row);
        Form form=new Form(context);
        form.fill(row);
        tailView.addTailItemListener("确定", new TailView.OnTailItemListener() {
            @Override
            public void onClickItem() {
                finish();
            }
        });
    }
}
