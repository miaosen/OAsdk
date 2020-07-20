package cn.oasdk.fileview.view;

import android.content.Context;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.view.View;
import android.widget.ListView;

import java.util.LinkedList;

import cn.oasdk.fileview.R;
import cn.oaui.annotation.InjectReader;
import cn.oaui.annotation.ViewInject;
import cn.oaui.data.RowObject;
import cn.oaui.view.CustomLayout;
import cn.oaui.view.listview.BaseFillAdapter;


/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2019-12-29  20:35
 * @Descrition 排序菜单
 */
public class SortDialogListView extends CustomLayout {

    public static final String KEY_SORT_TYPE="key_sort_type";



    @ViewInject
    ListView listView;

    private LinkedList<RowObject> rows;

    BaseFillAdapter.OnItemClickListener onItemClickListener;


    Integer[] names = new Integer[]{
            R.string.sort_crate_time,
            R.string.sort_file_size,
            R.string.sort_file_type,
    };

    public ListAdapter listAdapter;


    public SortDialogListView(Context context) {
        super(context);
    }

    @Override
    public void initData() {
        rows = new LinkedList<>();
        for (int i = 0; i < names.length; i++) {
            RowObject rowObject = new RowObject();
            Integer name = names[i];
            rowObject.put("name", getResources().getString(name));
            rows.add(rowObject);
        }
        listAdapter=new ListAdapter(context,rows,R.layout.download_dlg_sort_item);
        if(onItemClickListener!=null){
            listAdapter.setOnItemClickListener(onItemClickListener);
        }
        listView.setAdapter(listAdapter);

    }

    @Override
    protected void onCreateView() {
        InjectReader.injectAllFields(this);
    }

    @Override
    public int setXmlLayout() {
        return R.layout.file_sort_dlg_list;
    }

    public void setOnItemClickListener(BaseFillAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        if(listAdapter!=null){
            listAdapter.setOnItemClickListener(onItemClickListener);
        }
    }

    public BaseFillAdapter.OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }


    public class ListAdapter extends BaseFillAdapter {
        public ListAdapter(Context context, LinkedList<RowObject> rows, int layout) {
            super(context, rows, layout);
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void setItem(final View convertView, final RowObject row, final int position, final ViewHolder holder) {


        }
    }

    public ListView getListView() {
        return listView;
    }

    public void setListView(ListView listView) {
        this.listView = listView;
    }

    public LinkedList<RowObject> getRows() {
        return rows;
    }

    public void setRows(LinkedList<RowObject> rows) {
        this.rows = rows;
    }

    public ListAdapter getListAdapter() {
        return listAdapter;
    }

    public void setListAdapter(ListAdapter listAdapter) {
        this.listAdapter = listAdapter;
    }
}
