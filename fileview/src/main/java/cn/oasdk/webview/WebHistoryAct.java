package cn.oasdk.webview;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.LinkedList;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import cn.oasdk.base.BaseAct;
import cn.oasdk.fileview.R;
import cn.oaui.annotation.InjectReader;
import cn.oaui.annotation.ViewInject;
import cn.oaui.data.Row;
import cn.oaui.utils.SPUtils;
import cn.oaui.utils.ViewUtils;
import cn.oaui.view.listview.BaseFillAdapter;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2020-07-23  10:51
 * @Descrition
 */
public class WebHistoryAct extends BaseAct {

    public final static String FILE_HISTORY="file_history";
    public final static String KEY_HISTORY="key_history";

    public final static int CODE_HISTORY=1;

    @ViewInject
    ListView listView;
    public ListAdapter listAdapter;

    @ViewInject
    LinearLayout ln_back;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_act);
        InjectReader.injectAllFields(this);
        ViewUtils.finishByClick(ln_back);

    }


    @Override
    protected void onStart() {
        super.onStart();
        LinkedList<Row> rows = SPUtils.getRows(FILE_HISTORY, KEY_HISTORY);
        if(rows==null){

        }else{
            listAdapter=new ListAdapter(context,rows,R.layout.history_view_item);
            listAdapter.setOnItemClickListener(new BaseFillAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View convertView, Row row, int position, BaseFillAdapter.ViewHolder holder) {
                    Intent intent=new Intent();
                    intent.putExtra("url",row.getString("url"));
                    setResult(CODE_HISTORY,intent);
                    finish();
                }
            });
            listView.setAdapter(listAdapter);
        }
    }

    public class ListAdapter extends BaseFillAdapter {
        public ListAdapter(Context context, LinkedList<Row> rows, int layout) {
            super(context, rows, layout);
        }
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void setItem(final View convertView, final Row row, final int position, final BaseFillAdapter.ViewHolder holder) {
        }
    }
}
