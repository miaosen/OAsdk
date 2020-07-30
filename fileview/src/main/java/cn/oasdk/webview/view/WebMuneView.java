package cn.oasdk.webview.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.LinkedList;

import androidx.annotation.RequiresApi;
import cn.oasdk.fileview.R;
import cn.oaui.ResourceHold;
import cn.oaui.annotation.InjectReader;
import cn.oaui.annotation.ViewInject;
import cn.oaui.data.Row;
import cn.oaui.view.CustomLayout;
import cn.oaui.view.listview.BaseFillAdapter;

public class WebMuneView extends CustomLayout {

    String[] names = new String[]{
            "文件管理", "添加书签", "历史","刷新",
            "分享", "设置","书签", "退出",
    };

    Integer[] imgs = new Integer[]{
            R.mipmap.icon_file, R.mipmap.icon_add_bookmark, R.mipmap.icon_history,R.mipmap.icon_refresh,
            R.mipmap.icon_share, R.mipmap.icon_setting,R.mipmap.icon_bookmark,  R.mipmap.icon_out,
    };

    String[] urls = new String[]{
           "http://www.audio699.com/book/522/205.html",
            "https://www.5tps.com/play/20904_48_1_3.html",
            "https://www.baidu.com",
            "http://rcjgqyd.gzfda.gov.cn:8080/spjg/jsp/mobile/login.jsp",
            "https://i.y.qq.com/n2/m/index.html",
            "",
            "",
            ""
    };

    @ViewInject
    GridView gridView;
    private LinkedList<Row> rows;
    public ListAdapter listAdapter;




    public WebMuneView(Context context) {
        super(context);
    }

    public WebMuneView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void initData() {
        rows.clear();
        for (int i = 0; i < names.length; i++) {
            Row row = new Row();
            String name = names[i];
            row.put("name", name);
            row.put("img", imgs[i]);
            row.put("url", urls[i]);
            rows.add(row);
        }
        listAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreateView() {
        InjectReader.injectAllFields(this);
        rows= new LinkedList<>();
        listAdapter=new  ListAdapter(context,rows,R.layout.menu_view_item);
        gridView.setAdapter(listAdapter);
    }

    @Override
    public int setXmlLayout() {
        return R.layout.menu_view;
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

    public ListAdapter getListAdapter() {
        return listAdapter;
    }

    public void setListAdapter(ListAdapter listAdapter) {
        this.listAdapter = listAdapter;
    }
}
