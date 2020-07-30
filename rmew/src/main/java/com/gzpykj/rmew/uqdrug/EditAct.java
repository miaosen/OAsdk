package com.gzpykj.rmew.uqdrug;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.gzpykj.base.BaseActivity;
import com.gzpykj.rmew.R;

import cn.oaui.L;
import cn.oaui.annotation.ViewInject;
import cn.oaui.data.Row;
import cn.oaui.form.Form;
import cn.oaui.utils.IntentUtils;
import cn.oaui.utils.ViewUtils;
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

    @Override
    public void initConfig() {

    }

    @Override
    public int getContentView() {
        return R.layout.uqdrug_edit_act;
    }

    @Override
    public void onViewCreate(Bundle savedInstanceState) {

    }

    @Override
    public void initData() {
        Row row = IntentUtils.getRow(getIntent(), ListAct.ROW_ITEM);
        L.i("=========initData=============="+row);
        Form form=new Form(context);
        form.fill(row);
        ViewUtils.setClickable(ln_content,false);
        tailView.addTailItemListener("确定", new TailView.OnTailItemListener() {
            @Override
            public void onClickItem() {
                finish();
            }
        });
    }
}
