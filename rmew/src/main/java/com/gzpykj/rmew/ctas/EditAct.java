package com.gzpykj.rmew.ctas;

import android.os.Bundle;

import com.gzpykj.base.BaseActivity;
import com.gzpykj.rmew.R;
import com.oaui.L;
import com.oaui.annotation.ViewInject;
import com.oaui.data.RowObject;
import com.oaui.form.Form;
import com.oaui.utils.IntentUtils;
import com.oaui.view.TailView;

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
