package cn.oasdk.webview;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import cn.oasdk.base.BaseAct;
import cn.oasdk.fileview.R;
import cn.oaui.annotation.InjectReader;
import cn.oaui.annotation.ViewInject;
import cn.oaui.data.Row;
import cn.oaui.utils.StringUtils;
import cn.oaui.utils.ViewUtils;
import cn.oaui.view.ClearableEditText;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2020-07-28  11:40
 * @Descrition
 */
public class WebAddCollectAct extends BaseAct {


    @ViewInject
    LinearLayout ln_back;
    @ViewInject
    ClearableEditText name,url;



    @ViewInject
    Button btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collect_add_act);
        InjectReader.injectAllFields(this);
        ViewUtils.finishByClick(ln_back);
        String strName=getIntent().getStringExtra("name");
        String strUrl=getIntent().getStringExtra("url");
        name.setText(strName);
        url.setText(strUrl);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(StringUtils.isEmpty(name.getText())){
                   ViewUtils.toast("名称不能为空！");
               }else if(StringUtils.isEmpty(url.getText())){
                   ViewUtils.toast("链接不能为空！");
               }else{
                   Row row=new Row();
                   row.put("name",name.getText()+"");
                   row.put("url",url.getText()+"");
                   WebCollectAct.saveCollection(row);
                   ViewUtils.toast("收藏成功！");
                   finish();
               }

            }
        });
    }

}
