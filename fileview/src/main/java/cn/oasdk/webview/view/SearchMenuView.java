package cn.oasdk.webview.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.LinkedList;

import androidx.annotation.RequiresApi;
import cn.oasdk.fileview.R;
import cn.oaui.ResourceHold;
import cn.oaui.annotation.InjectReader;
import cn.oaui.annotation.ViewInject;
import cn.oaui.data.Row;
import cn.oaui.view.CustomLayout;
import cn.oaui.view.listview.BaseFillAdapter;

public class SearchMenuView extends CustomLayout {

    static String[] names = new String[]{
            "百度", "必应",
            "360", "搜狗",
            "谷歌","yandex",
            "自定义"
    };

    static Integer[] imgs = new Integer[]{
            R.mipmap.logo_baidu, R.mipmap.logo_bing, R.mipmap.logo_so,
            R.mipmap.logo_sogou, R.mipmap.logo_google, R.mipmap.logo_yandex,R.mipmap.icon_add,
    };

    static String[] urls = new String[]{
           "https://www.baidu.com",
            "https://bing.com",
            "https://www.so.com",
            "https://www.sogou.com",
            "https:www.google.com",
            "https://yandex.com",
            ""
    };

    static String[] param1s = new String[]{
            "/s?wd=",
            "/search?q=",
            "/s?q=",
            "/web?query=",
            "",
            "/search/?text=",
            ""
    };

    @ViewInject
    public ListView listView;
    public LinkedList<Row> rows;
    public ListAdapter listAdapter;

    public SearchMenuView(Context context) {
        super(context);
    }

    public SearchMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static String buildUrl(Row rowSearchParam, String text) {
        String url = rowSearchParam.getString("url")+rowSearchParam.getString("param1")+text;
        return url;
    }

    @Override
    public void initData() {
        rows.clear();
        for (int i = 0; i < names.length; i++) {
            Row row =getParamRow(i);
            rows.add(row);
        }
        listAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onCreateView() {
        InjectReader.injectAllFields(this);
        rows= new LinkedList<>();
        listAdapter=new  ListAdapter(context,rows,R.layout.search_menu_view_item);
        listView.setAdapter(listAdapter);
    }

    @Override
    public int setXmlLayout() {
        return R.layout.search_menu_view;
    }

    public class ListAdapter extends BaseFillAdapter {
        public ListAdapter(Context context, LinkedList<Row> rows, int layout) {
            super(context, rows, layout);
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void setItem(final View convertView, final Row row, final int position, final ViewHolder holder) {
            ImageView img = (ImageView) holder.views.get("img");
            img.setImageDrawable(ResourceHold.getDrawable(row.getInteger("img")));
        }
    }


    public static Row getParamRow(int index){
        Row row=new Row();
        row.put("name",names[index]);
        row.put("url",urls[index]);
        row.put("img",imgs[index]);
        row.put("param1",param1s[index]);
        return row;
    }




}
