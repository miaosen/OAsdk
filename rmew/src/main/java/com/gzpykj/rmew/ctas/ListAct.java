package com.gzpykj.rmew.ctas;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.gzpykj.base.BaseActivity;
import com.gzpykj.rmew.R;
import com.oaui.annotation.ViewInject;
import com.oaui.data.RowObject;
import com.oaui.form.FormUtils;
import com.oaui.utils.IntentUtils;
import com.oaui.utils.ViewUtils;
import com.oaui.view.HeaderView;
import com.oaui.view.calendar.DateButton;
import com.oaui.view.dialog.FrameDialog;
import com.oaui.view.listview.BaseFillAdapter;
import com.oaui.view.listview.DataListView;

import java.util.Map;

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
        return R.layout.ctas_list_act;
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
            public void onItemClick(View convertView, RowObject row, int position) {
                //Intent in=new Intent(context,FeedDeatailActivity.class);
                //IntentUtils.addRow(in,row,FEED_DETAIL_ROW);
                //startActivity(in);
            }
        });

        dialogSearch = new FrameDialog(ListAct.this,
                R.layout.ctas_record_search_act);
        dialogSearch.setShadow(false);
        dialogSearch.setFillwidth(true);
        View shadow = dialogSearch.findViewById(R.id.shadow);
        DateButton startDate= (DateButton) dialogSearch.findViewById(R.id.startDate);
        DateButton endDate= (DateButton) dialogSearch.findViewById(R.id.endDate);
        startDate.setText("");
        endDate.setText("");
        shadow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSearch.dismiss();
            }
        });
        View tv_senior_search = dialogSearch.findViewById(R.id.tv_senior_search);
        final EditText keyword = (EditText) dialogSearch.findViewById(R.id.keyword);
        tv_senior_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> contentValue = FormUtils.getContentValues(dialogSearch);
                //L.i("=========onClick=============="+contentValue);
                //Iterator<String> it = contentValue.keySet().iterator();
                //while (it.hasNext()) {
                //    String key = it.next();
                //    if ("全部".equals(contentValue.get(key))) {
                //        // it.remove();
                //        contentValue.remove(key);
                //    }
                //}
                //if("检品名称".equals(contentValue.get("fieldText"))){
                //    contentValue.put("field","SAMPLE_NAME");
                //}else  if("抽样编号".equals(contentValue.get("fieldText"))){
                //    contentValue.put("field","SAMPLE_NO");
                //}else  if("生产单位/委托方".equals(contentValue.get("fieldText"))){
                //    contentValue.put("field","MANUFACTURER");
                //}
                dataListView.addParam(contentValue);
                dataListView.refresh();
                dialogSearch.dismiss();
                ViewUtils.hideKeyboard(keyword);
            }
        });
        dataListView.setOnItemClickListener(new BaseFillAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View convertView, RowObject row, int position) {
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
