package cn.oasdk.webview.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.util.LinkedList;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import cn.oasdk.fileview.R;
import cn.oasdk.webview.WebCollectAct;
import cn.oaui.ImageFactory;
import cn.oaui.L;
import cn.oaui.ResourceHold;
import cn.oaui.annotation.InjectReader;
import cn.oaui.annotation.ViewInject;
import cn.oaui.data.DoubleCache;
import cn.oaui.data.Row;
import cn.oaui.utils.AppUtils;
import cn.oaui.utils.URIUtils;
import cn.oaui.view.CustomLayout;
import cn.oaui.view.listview.BaseFillAdapter;

public class BookMarkView extends CustomLayout {

    String[] names = new String[]{
            "静听网", "评书网",
            "百度", "食品监管食品监管","qq音乐", "添加",
    };

    Integer[] imgs = new Integer[]{
            R.mipmap.icon_add_bookmark, R.mipmap.icon_video, R.mipmap.icon_doc,
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
    private LinkedList<Row> rows;
    public ListAdapter listAdapter;

    public BookMarkView(Context context) {
        super(context);
    }

    public BookMarkView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void initData() {

        L.i("============initData==========="+ this.rows);
        //for (int i = 0; i < names.length; i++) {
        //    Row row = new Row();
        //    String name = names[i];
        //    row.put("name", name);
        //    row.put("img", imgs[i]);
        //    row.put("url", urls[i]);
        //    rows.add(row);
        //}
    }



    public static String getLogoPath(String url){
        String ip = URIUtils.getIP(url).replaceAll("/","");
        String path = AppUtils.getDefaultDirectory() + "/.icon/" + ip + ".png";
        return path;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if(visibility==View.VISIBLE){
            rows.clear();
            LinkedList<Row> rowsCache = DoubleCache.getRows(WebCollectAct.FILE_COLLECTION);
            if(rowsCache.size()>0){
                rows.addAll(rowsCache);
            }
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreateView() {
        InjectReader.injectAllFields(this);
        rows= new LinkedList<>();
        listAdapter=new  ListAdapter(context,rows,R.layout.book_mark_view_item);
        gridView.setAdapter(listAdapter);
    }

    @Override
    public int setXmlLayout() {
        return R.layout.book_mark_view;
    }

    public class ListAdapter extends BaseFillAdapter {
        public ListAdapter(Context context, LinkedList<Row> rows, int layout) {
            super(context, rows, layout);
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void setItem(final View convertView, final Row row, final int position, final ViewHolder holder) {
            ImageView img = (ImageView) holder.views.get("img");
            Integer img1 = row.getInteger("img");
            String url = row.getString("url");
            String logoPath = getLogoPath(url);
            File file=new File(logoPath);
            if(file.exists()){
                ImageFactory.loadImage(img,logoPath);
            }else{
                img.setImageDrawable(ResourceHold.getDrawable(R.mipmap.ic_launcher));
            }
            //
            //if(img1>0){
            //    img.setBackground(ResourceHold.getDrawable(img1));
            //}

        }
    }




}
