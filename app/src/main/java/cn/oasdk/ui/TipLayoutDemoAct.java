package cn.oasdk.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.LinkedList;
import java.util.List;

import cn.oasdk.R;
import cn.oaui.L;
import cn.oaui.annotation.InjectReader;
import cn.oaui.annotation.ViewInject;
import cn.oaui.data.Row;
import cn.oaui.utils.DateTimeUtils;
import cn.oaui.view.listview.BaseFillAdapter;
import cn.oaui.view.tiplayout.TipLayout;

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


    List<Row> rows=new LinkedList<Row>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_view_tiplayout_act);
        InjectReader.injectAllFields(this);
        setData("名称");
        baseFillAdapter=new BaseFillAdapter(TipLayoutDemoAct.this,rows,R.layout.ui_view_listview_item) {
            @Override
            public void setItem(View convertView, Row row, int position, ViewHolder holder) {

            }
        };
        listview1.setAdapter(baseFillAdapter);
        baseFillAdapter.setOnItemClickListener(new BaseFillAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View convertView, Row row, int position, BaseFillAdapter.ViewHolder holder) {
            }
        });
        tipLayout.setOnTipListener(new TipLayout.OnTipListener() {
            int i=0;
            @Override
            public void onRefresh() {
                L.i("============onRefresh===========");
                i=i+1;
                rows.clear();
                setData("刷新"+i);
                baseFillAdapter.notifyDataSetChanged();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tipLayout.endRefresh();
                    }
                },1000);
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
                tipLayout.showLoad();
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tipLayout.endRefresh();
            }
        },1000);
    }

    public void setData(String name){
        for (int i = 0; i < 10; i++) {
            Row row=new Row();
            row.put("NAME",name);
            row.put("time", DateTimeUtils.getCurrentTime());
            rows.add(row);
        }
    }
}
