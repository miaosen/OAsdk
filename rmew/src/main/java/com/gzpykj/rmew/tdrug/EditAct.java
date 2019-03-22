package com.gzpykj.rmew.tdrug;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.gzpykj.base.BaseActivity;
import com.gzpykj.rmew.R;
import com.oaui.L;
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
        return R.layout.tdrug_edit_act;
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
        ViewUtils.setClickable(ln_content,false);
        tailView.addTailItemListener("确定", new TailView.OnTailItemListener() {
            @Override
            public void onClickItem() {
                finish();
            }
        });
    }
}
