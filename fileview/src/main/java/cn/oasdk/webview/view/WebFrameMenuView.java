package cn.oasdk.webview.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.LinkedList;

import androidx.annotation.RequiresApi;
import cn.oasdk.fileview.R;
import cn.oaui.ResourceHold;
import cn.oaui.annotation.InjectReader;
import cn.oaui.annotation.ViewInject;
import cn.oaui.data.Row;
import cn.oaui.utils.StringUtils;
import cn.oaui.view.CustomLayout;
import cn.oaui.view.listview.BaseFillAdapter;

public class WebFrameMenuView extends CustomLayout {

    @ViewInject
    public ListView listView;
    public LinkedList<Row> rows;
    public ListAdapter listAdapter;

    public int checkPosition=0;

    @ViewInject
    public LinearLayout ln_add_webframe;

    OnWindowCheckListener onWindowCheckListener;

    public WebFrameMenuView(Context context) {
        super(context);
    }

    public WebFrameMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static String buildUrl(Row rowSearchParam, String text) {
        String url = rowSearchParam.getString("url")+rowSearchParam.getString("param1")+text;
        return url;
    }

    @Override
    public void initData() {
        listAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreateView() {
        InjectReader.injectAllFields(this);
        rows= new LinkedList<>();
        listAdapter=new  ListAdapter(context,rows,R.layout.webframe_menu_view_item);
        listView.setAdapter(listAdapter);

    }

    @Override
    public int setXmlLayout() {
        return R.layout.webframe_menu_view;
    }

    public void setListWebFrame(LinkedList<WebFrameView> listWebFrame, int position) {
        rows.clear();
        for (int i = 0; i < listWebFrame.size(); i++) {
            WebFrameView webFrameView = listWebFrame.get(i);
            Row row=new Row();
            row.put("name",webFrameView.title);
            row.put("url",webFrameView.url);
            row.put("img",webFrameView.webIcon);
            rows.add(row);
        }
        listAdapter.notifyDataSetChanged();
        checkPosition=position;
    }


    public class ListAdapter extends BaseFillAdapter {
        public ListAdapter(Context context, LinkedList<Row> rows, int layout) {
            super(context, rows, layout);
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void setItem(final View convertView, final Row row, final int position, final ViewHolder holder) {
            ImageView img = (ImageView) holder.views.get("img");
            Bitmap webIcon= (Bitmap) row.get("img");
            if(webIcon!=null) {
                img.setImageBitmap(webIcon);
            }else{
                img.setImageDrawable(ResourceHold.getDrawable(R.mipmap.icon_web));
            }
            View ln_window = holder.views.get("ln_window");
            if(onWindowCheckListener!=null){
                ln_window.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onWindowCheckListener.onCheck(convertView,row,position,holder);
                    }
                });

            }
            TextView name = (TextView) holder.views.get("name");
            TextView url = (TextView) holder.views.get("url");
            ImageView img_colse = (ImageView) holder.views.get("img_colse");
            if(position==checkPosition){
                name.setTextColor(ResourceHold.getColor(R.color.blue));
                url.setTextColor(ResourceHold.getColor(R.color.blue));
                ln_window.setBackground(ResourceHold.getDrawable(R.drawable.sel_radius_blue));
                img_colse.setImageDrawable(ResourceHold.getDrawable(R.mipmap.icon_colse_blue));
            }else{
                name.setTextColor(ResourceHold.getColor(R.color.text_color));
                url.setTextColor(ResourceHold.getColor(R.color.text_color));
                ln_window.setBackground(ResourceHold.getDrawable(R.drawable.sel_radius_grey_lt));
                img_colse.setImageDrawable(ResourceHold.getDrawable(R.mipmap.icon_colse_grey));
            }
            if(StringUtils.isEmpty(row.getString("url"))){
                url.setText("");
            }  else{
                //url.setText();
            }
            if(onWindowCheckListener!=null){
                img_colse.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onWindowCheckListener.onColse(convertView,row,position,holder);
                    }
                });
            }
        }
    }


    public interface OnWindowCheckListener{
        public void onCheck(View convertView, Row row, int position, BaseFillAdapter.ViewHolder holder) ;
        public void onColse(View convertView, Row row, int position, BaseFillAdapter.ViewHolder holder) ;
    }


    public OnWindowCheckListener getOnWindowCheckListener() {
        return onWindowCheckListener;
    }

    public void setOnWindowCheckListener(OnWindowCheckListener onWindowCheckListener) {
        this.onWindowCheckListener = onWindowCheckListener;
    }
}
