package com.ossdk.ui;

import com.ossdk.R;
import com.ossdk.base.BaseActivity;
import com.osui.L;
import com.osui.annotation.ViewInject;
import com.osui.data.RowObject;
import com.osui.view.TailView;
import com.osui.view.attachment.AttachmentView;

import java.util.List;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2018-03-07  15:04
 * @Descrition
 */

public class BaseViewDemoAct extends BaseActivity {

    //@ViewInject
    //ColorPickerView colorPickerView;

    @ViewInject
    AttachmentView attachmentView;
    @ViewInject
    TailView tailView;
    
    @Override
    public void initConfig() {

    }

    @Override
    public int getContentView() {
        return R.layout.ui_baseview_demo_act;
    }

    @Override
    public void onViewCreate() {
        //String alphaSliderText = colorPickerView.getAlphaSliderText();
        //int color = colorPickerView.getColor();
        //String s = Integer.toHexString(color);
        //L.i("=========onViewCreate=============="+s);
        tailView.addTailItemListener("获取", new TailView.OnTailItemListener() {
            @Override
            public void onClickItem() {
                List<RowObject> data = attachmentView.getData();
                L.i("=========onViewCreate=============="+data);
            }
        });
    }

    @Override
    public void initData() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
