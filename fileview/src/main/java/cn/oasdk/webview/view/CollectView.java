package cn.oasdk.webview.view;

import android.content.Context;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.LinkedList;

import cn.oasdk.fileview.R;
import cn.oaui.ResourceHold;
import cn.oaui.annotation.InjectReader;
import cn.oaui.annotation.ViewInject;
import cn.oaui.data.RowObject;
import cn.oaui.view.CustomLayout;
import cn.oaui.view.listview.BaseFillAdapter;

public class CollectView extends CustomLayout {

    String[] names = new String[]{
            "静听网", "评书网",
            "百度", "食品监管","qq音乐", "添加",
    };

    Integer[] imgs = new Integer[]{
            R.mipmap.icon_image, R.mipmap.icon_video, R.mipmap.icon_doc,
            R.mipmap.icon_music, R.mipmap.icon_apk, R.mipmap.icon_add,
    };

    String[] urls = new String[]{
           "http://www.audio699.com/book/522/205.html",
            "https://www.5tps.com/play/20904_48_1_3.html",
            "https://www.baidu.com",
            "http://rcjgqyd.gzfda.gov.cn:8080/spjg/jsp/mobile/login.jsp",
            "https://i.y.qq.com/n2/m/index.html",
            ""
    };

    @ViewInject
    GridView gridView;
    private LinkedList<RowObject> rows;
    public ListAdapter listAdapter;

    public CollectView(Context context) {
        super(context);
    }

    public CollectView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void initData() {
        rows.clear();
        for (int i = 0; i < names.length; i++) {
            RowObject rowObject = new RowObject();
            String name = names[i];
            rowObject.put("name", name);
            rowObject.put("img", imgs[i]);
            rowObject.put("url", urls[i]);
            rows.add(rowObject);
        }
        listAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreateView() {
        InjectReader.injectAllFields(this);
        rows= new LinkedList<>();
        listAdapter=new  ListAdapter(context,rows,R.layout.collect_view_item);
        gridView.setAdapter(listAdapter);
    }

    @Override
    public int setXmlLayout() {
        return R.layout.collect_view;
    }

    public class ListAdapter extends BaseFillAdapter {
        public ListAdapter(Context context, LinkedList<RowObject> rows, int layout) {
            super(context, rows, layout);
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void setItem(final View convertView, final RowObject row, final int position, final ViewHolder holder) {
            ImageView img = (ImageView) holder.views.get("img");
            img.setBackground(ResourceHold.getDrawable(row.getInteger("img")));
        }
    }




}
