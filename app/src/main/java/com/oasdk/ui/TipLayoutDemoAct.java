package com.oasdk.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.oasdk.R;
import com.oaui.annotation.InjectReader;
import com.oaui.annotation.ViewInject;
import com.oaui.data.RowObject;
import com.oaui.utils.DateTimeUtils;
import com.oaui.view.listview.BaseFillAdapter;
import com.oaui.view.tiplayout.TipLayout;

import java.util.LinkedList;
import java.util.List;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @git http://git.oschina.net/miaosen/MyUtils
 * @CreateDate 2018-01-05  10:34
 * @Descrition
 */

public class TipLayoutDemoAct extends Activity {

    @ViewInject
    ListView listview;
    @ViewInject
    ListView listview1;

    BaseFillAdapter baseFillAdapter;

    @ViewInject
    TipLayout tipLayout;
    @ViewInject
    Button btn_refresh,btn_load,btn_error,btn_end;


    List<RowObject> rows=new LinkedList<RowObject>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_view_tiplayout_act);
        InjectReader.injectAllFields(this);
        setData("名称");
        baseFillAdapter=new BaseFillAdapter(TipLayoutDemoAct.this,rows,R.layout.ui_view_listview_item) {
            @Override
            public void setItem(View convertView, RowObject row, int position, ViewHolder holder) {

            }
        };
        listview1.setAdapter(baseFillAdapter);
        baseFillAdapter.setOnItemClickListener(new BaseFillAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View convertView, RowObject row, int position) {
            }
        });
        tipLayout.setOnTipListener(new TipLayout.OnTipListener() {
            int i=0;
            @Override
            public void onRefresh() {
                i=i+1;
                rows.clear();
                setData("刷新"+i);
                baseFillAdapter.notifyDataSetChanged();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tipLayout.endRefresh();
                    }
                },3000);
            }

            @Override
            public void onLoadMore() {

            }
        });
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipLayout.refresh();
            }
        });
        btn_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipLayout.load();
            }
        });
        btn_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipLayout.error();
            }
        });
        btn_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipLayout.end();
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        tipLayout.refresh();
    }

    public void setData(String name){
        for (int i = 0; i < 10; i++) {
            RowObject row=new RowObject();
            row.put("NAME",name);
            row.put("time", DateTimeUtils.getCurrentTime());
            rows.add(row);
        }
    }
}
