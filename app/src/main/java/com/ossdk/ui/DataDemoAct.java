package com.ossdk.ui;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ossdk.R;
import com.ossdk.base.BaseActivity;
import com.osui.L;
import com.osui.ResourceHold;
import com.osui.annotation.ViewInject;
import com.osui.data.RowObject;
import com.osui.utils.DateTimeUtils;
import com.osui.view.listview.DataListView;

import java.util.LinkedList;
import java.util.List;


/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2018-01-16  14:41
 * @Descrition
 */

public class DataDemoAct extends BaseActivity {

    @ViewInject
    Button btn_hold;
    @ViewInject
    TextView tv;
    @ViewInject
    DataListView autoListView;

    @Override
    public void initConfig() {

    }

    @Override
    public int getContentView() {
        return R.layout.ui_data_demo_act;
    }

    @Override
    public void onViewCreate() {

    }

    @Override
    public void initData() {
        final List<RowObject> rows=new LinkedList<RowObject>();
        for (int i = 0; i < 10; i++) {
            RowObject row=new RowObject();
            row.put("NAME","名称"+i);
            row.put("time", DateTimeUtils.getCurrentTime());
            rows.add(row);
        }
        //autoListView.noScroll();
        autoListView.setValue(rows);
        btn_hold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                L.i("=========onClick==============");
                //根据名称获取id
                int analysisBtn = ResourceHold.getIdByName("analysisBtn");
                //根据id获取名称
                String nameById = ResourceHold.getNameById(R.id.analysisBtn);
                Log.i("logtag", "id======" + analysisBtn);
                Log.i("logtag", "nameById======" + nameById);
                tv.setText(ResourceHold.name2idResounceMap.toString());
                L.i("=========onClick=============="+autoListView.getRows());
                autoListView.notifyDataSetChanged();
            }
        });

    }
}
