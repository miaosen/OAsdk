package cn.oasdk.fileview.view;

import android.content.Context;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.LinkedList;

import cn.oasdk.fileview.R;
import cn.oaui.ResourceHold;
import cn.oaui.annotation.InjectReader;
import cn.oaui.annotation.ViewInject;
import cn.oaui.data.Row;
import cn.oaui.view.CustomLayout;
import cn.oaui.view.listview.BaseFillAdapter;


/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2019-12-29  20:35
 * @Descrition 排序菜单
 */
public class EditMenuListView extends CustomLayout {


    @ViewInject
    ListView listView;
    private LinkedList<Row> rows;
    public ListAdapter listAdapter;

    Integer[] names = new Integer[]{
            R.string.modify,
    };

    Integer[] imgs = new Integer[]{
            R.mipmap.icon_rename,
    };




    public EditMenuListView(Context context) {
        super(context);
    }

    @Override
    public void initData() {
        refresh(names);

    }

    public void refresh(Integer[] names) {
        rows.clear();
        for (int i = 0; i < names.length; i++) {
            Row row = new Row();
            Integer name = names[i];
            row.put("name", ResourceHold.getString(name));
            row.put("img",imgs[i]);
            rows.add(row);
        }
        listAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreateView() {
        InjectReader.injectAllFields(this);
        rows= new LinkedList<>();
        listAdapter=new ListAdapter(context,rows,R.layout.edit_menu_list_dlg_item);
        listView.setAdapter(listAdapter);

    }

    @Override
    public int setXmlLayout() {
        return R.layout.edit_menu_list_dlg;
    }


    public class ListAdapter extends BaseFillAdapter {
        public ListAdapter(Context context, LinkedList<Row> rows, int layout) {
            super(context, rows, layout);
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void setItem(final View convertView, final Row row, final int position, final ViewHolder holder) {
            ImageView img = (ImageView) holder.views.get("img");
            img.setBackground(ResourceHold.getDrawable(row.getInteger("img")));
        }
    }

    public ListView getListView() {
        return listView;
    }

    public void setListView(ListView listView) {
        this.listView = listView;
    }

    public LinkedList<Row> getRows() {
        return rows;
    }

    public void setRows(LinkedList<Row> rows) {
        this.rows = rows;
    }

    public ListAdapter getListAdapter() {
        return listAdapter;
    }

    public void setListAdapter(ListAdapter listAdapter) {
        this.listAdapter = listAdapter;
    }

    public Integer[] getNames() {
        return names;
    }

    public void setNames(Integer[] names) {
        this.names = names;
    }

    public Integer[] getImgs() {
        return imgs;
    }

    public void setImgs(Integer[] imgs) {
        this.imgs = imgs;
    }
}
