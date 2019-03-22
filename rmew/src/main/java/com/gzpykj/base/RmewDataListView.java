package com.gzpykj.base;

import android.content.Context;
import android.util.AttributeSet;

import com.oaui.view.listview.DataListView;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2018-06-15  09:48
 * @Descrition
 */

public class RmewDataListView extends DataListView {


    public RmewDataListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RmewDataListView(Context context) {
        super(context);
        init();
    }



    private void init(){
        setUrl(Global.HOST);
        setOnDataListChange(new DataListView.OnDataListChange() {
            @Override
            public void onRefresh() {
                addParam("pageNum",1);
                addParam("pageSize",15);
            }

            @Override
            public void onLoadMore(int pageIndex) {
                addParam("pageNum",pageIndex);
            }

            @Override
            public void onLoadCompalte() {
            }
        });
    }
}
