package cn.oasdk.fileview.view;

import android.content.Context;
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

public class QuickView extends CustomLayout {

    Integer[] names = new Integer[]{
            R.string.file_type_img, R.string.file_type_video, R.string.file_type_doc,
            R.string.file_type_music, R.string.file_type_app, R.string.file_type_rar,
    };

    Integer[] imgs = new Integer[]{
            R.mipmap.icon_image, R.mipmap.icon_video, R.mipmap.icon_doc,
            R.mipmap.icon_music, R.mipmap.icon_apk, R.mipmap.icon_rar,
    };

    @ViewInject
    GridView gridView;
    private LinkedList<RowObject> rows;
    public ListAdapter listAdapter;

    public QuickView(Context context) {
        super(context);
    }

    public QuickView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void initData() {
        rows.clear();
        for (int i = 0; i < names.length; i++) {
            RowObject rowObject = new RowObject();
            Integer name = names[i];
            rowObject.put("name", ResourceHold.getString(name));
            rowObject.put("img", imgs[i]);
            rows.add(rowObject);
        }
        listAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreateView() {
        InjectReader.injectAllFields(this);
        rows= new LinkedList<>();
        listAdapter=new  ListAdapter(context,rows,R.layout.quick_sort_item);
        gridView.setAdapter(listAdapter);
    }

    @Override
    public int setXmlLayout() {
        return R.layout.quick_view;
    }

    public class ListAdapter extends BaseFillAdapter {
        public ListAdapter(Context context, LinkedList<RowObject> rows, int layout) {
            super(context, rows, layout);
        }

        @Override
        public void setItem(final View convertView, final RowObject row, final int position, final ViewHolder holder) {
            ImageView img = (ImageView) holder.views.get("img");
            img.setBackground(ResourceHold.getDrawable(row.getInteger("img")));
        }
    }
}
