package com.gzpykj.rmew.riskcomp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.gzpykj.base.BaseActivity;
import com.gzpykj.rmew.R;

import java.util.Map;

import cn.oaui.annotation.ViewInject;
import cn.oaui.data.Row;
import cn.oaui.form.FormUtils;
import cn.oaui.utils.IntentUtils;
import cn.oaui.utils.ViewUtils;
import cn.oaui.view.HeaderView;
import cn.oaui.view.dialog.FrameDialog;
import cn.oaui.view.listview.BaseFillAdapter;
import cn.oaui.view.listview.DataListView;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2018-06-14  16:57
 * @Descrition
 */

public class ListAct extends BaseActivity {



    @ViewInject
    HeaderView headerView;

    @ViewInject
    DataListView dataListView;

    FrameDialog dialogSearch;

    // 广播名称
    public static String BC_TEXT="action.refresh";
    // 子项数据传递
    public static String ROW_ITEM="rowItem";
    @Override
    public void initConfig() {

    }

    @Override
    public int getContentView() {
        return R.layout.riskcomp_list_act;
    }

    @Override
    public void onViewCreate(Bundle savedInstanceState) {
        refreshlist();
        headerView.getRightBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSearch.showAsDown(findViewById(R.id.header));
            }
        });
        dataListView.setOnItemClickListener(new BaseFillAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View convertView, Row row, int position, BaseFillAdapter.ViewHolder holder) {
                //Intent in=new Intent(context,FeedDeatailActivity.class);
                //IntentUtils.addRow(in,row,FEED_DETAIL_ROW);
                //startActivity(in);
            }
        });

        dialogSearch = new FrameDialog(ListAct.this,
                R.layout.riskcom_record_search_act);
        dialogSearch.setShadow(false);
        dialogSearch.setFillwidth(true);
        View shadow = dialogSearch.findViewById(R.id.shadow);
        shadow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSearch.dismiss();
            }
        });
        final EditText keyword = (EditText) dialogSearch.findViewById(R.id.keyword);
        View tv_senior_search = dialogSearch.findViewById(R.id.tv_senior_search);
        tv_senior_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> contentValue = FormUtils.getContentMap(dialogSearch);
                if("全部".equals(contentValue.get("riskType"))){
                    contentValue.remove("riskType");
                    dataListView.removeParam("riskType");
                }
                dataListView.addParam(contentValue);
                dataListView.refresh();
                dialogSearch.dismiss();
                ViewUtils.hideKeyboard(keyword);
            }
        });
        dataListView.setOnItemClickListener(new BaseFillAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View convertView, Row row, int position, BaseFillAdapter.ViewHolder holder) {
                Intent intent=new Intent(context,EditAct.class);
                IntentUtils.addRow(intent,row,ROW_ITEM);
                startActivity(intent);
            }
        });
    }

    @Override
    public void initData() {

    }


    /**
     * 注册广播，刷新列表
     */
    public void refreshlist() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BC_TEXT);
        registerReceiver(mRefreshBroadcastReceiver, intentFilter);
    }


    // 广播
    private BroadcastReceiver mRefreshBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BC_TEXT)) {
                dataListView.refresh();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mRefreshBroadcastReceiver);

    }
}
