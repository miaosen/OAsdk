package gzpykj.hzpqy.feed;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import cn.oaui.annotation.ViewInject;
import cn.oaui.data.RowObject;
import cn.oaui.utils.IntentUtils;
import cn.oaui.view.HeaderView;
import cn.oaui.view.listview.BaseFillAdapter;
import cn.oaui.view.listview.DataListView;

import gzpykj.hzpqy.R;
import gzpykj.hzpqy.base.BaseActivity;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2018-06-14  16:57
 * @Descrition
 */

public class FeedListActivity extends BaseActivity {

    public static String FEED_DETAIL_ROW="feed_detail_row";


    @ViewInject
    HeaderView headerView;

    @ViewInject
    DataListView dataListView;


    // 广播名称
    public static String BC_TEXT="action.refresh";

    @Override
    public void initConfig() {

    }

    @Override
    public int getContentView() {
        return R.layout.feed_list_act;
    }

    @Override
    public void onViewCreate(Bundle savedInstanceState) {
        refreshlist();
        dataListView.setOnDataListChange(new DataListView.OnDataListChange() {
            @Override
            public void onRefresh() {
                dataListView.addParam("pageNum",1);
                dataListView.addParam("pageSize",15);
            }

            @Override
            public void onLoadMore(int pageIndex) {
                dataListView.addParam("pageNum",pageIndex);
            }

            @Override
            public void onLoadCompalte() {

            }
        });
        dataListView.setOnItemClickListener(new BaseFillAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View convertView, RowObject row, int position) {
                Intent in=new Intent(context,FeedDeatailActivity.class);
                IntentUtils.addRow(in,row,FEED_DETAIL_ROW);
                startActivity(in);
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
