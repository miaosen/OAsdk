package cn.oasdk.webview.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;

import androidx.annotation.RequiresApi;
import cn.oahttp.HandlerQueue;
import cn.oahttp.HttpUtils;
import cn.oasdk.fileview.R;
import cn.oasdk.webview.data.SortRowData;
import cn.oaui.ImageFactory;
import cn.oaui.L;
import cn.oaui.ResourceHold;
import cn.oaui.annotation.InjectReader;
import cn.oaui.annotation.ViewInject;
import cn.oaui.data.Row;
import cn.oaui.view.CustomLayout;
import cn.oaui.view.listview.BaseFillAdapter;
import cn.oaui.view.listview.HorizontalListView;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2020-08-06  14:36
 * @Descrition
 */
public class ResourceListView extends CustomLayout {

    @ViewInject
    public HorizontalListView horizontalListView;
    public LinkedList<Row> rows;
    public ListAdapter listAdapter;


    @ViewInject
    TextView tv_sum;

    Row rowTempUrl=new Row();

    public long startTime,endTime;
    boolean isRun=true,isCanRefresh=false;



    public ResourceListView(Context context) {
        super(context);
    }

    public ResourceListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onCreateView() {
        InjectReader.injectAllFields(this);
        rows= new LinkedList<>();
        listAdapter=new ListAdapter(context,rows,R.layout.resource_list_view_item);
        listAdapter.setOnItemClickListener(new BaseFillAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View convertView, Row row, int position, BaseFillAdapter.ViewHolder holder) {
                L.i("============onItemClick==========="+row);
            }
        });
        horizontalListView.setAdapter(listAdapter);
        //horizontalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        //    @Override
        //    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //        L.i("============onItemClick==========="+position);
        //    }
        //});
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRun){
                    endTime=System.currentTimeMillis();
                    //L.i("============run==========="+startTime);
                    //L.i("============run==========="+endTime);
                    //L.i("============run==========="+isCanRefresh);
                    if(endTime-startTime>5000&&isCanRefresh){
                        startTime=System.currentTimeMillis();
                        isCanRefresh=false;
                        HandlerQueue.onResultCallBack(new Runnable() {
                            @Override
                            public void run() {
                                listAdapter.notifyDataSetChanged();
                                tv_sum.setText(rows.size()+"");
                            }
                        });
                    }
                }
            }
        });
        //thread.start();
    }


    public void addRes(String url,String type,long fileSize){
        if(!rowTempUrl.containsKey(url)&&!rowTempUrl.containsValue(HttpUtils.getFileName(url))){
            Row row=new Row();
            row.put("name", HttpUtils.getFileName(url));
            row.put("type", type);
            row.put("fileSize", fileSize);
            row.put("url", url);
            rows.add(row);
            SortRowData.sortRowByFileSize(rows);
            refresh();
        }
        rowTempUrl.put(url,HttpUtils.getFileName(url));
        //listAdapter.addRow(row);
    }

    /**
     * 每10个资源刷新一次，否则单个资源10后刷新
     */
    private void refresh() {
        listAdapter.notifyDataSetChanged();
        if(tv_sum!=null){
            tv_sum.setText(rows.size()+"");
        }
        //if(rows.size()%10==0){
        //    isCanRefresh=false;
        //    listAdapter.notifyDataSetChanged();
        //    if(tv_sum!=null){
        //        tv_sum.setText(rows.size()+"");
        //    }
        //}else{//等10秒再刷新
        //    if(!isCanRefresh){
        //        isCanRefresh=true;
        //        startTime = System.currentTimeMillis();
        //    }
        //}
    }




    public void clearRes(){
        rows.clear();
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public int setXmlLayout() {
        return R.layout.resoures_list_view;
    }




    public class ListAdapter extends BaseFillAdapter {
        public ListAdapter(Context context, LinkedList<Row> rows, int layout) {
            super(context, rows, layout);
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void setItem(final View convertView, final Row row, final int position, final BaseFillAdapter.ViewHolder holder) {
            ImageView img = (ImageView) holder.views.get("img");
            String url = row.getString("url");
            String type = row.getString("type");
            if("audio".equals(type)){
                ImageFactory.loadImageCorner(img, ResourceHold.getDrawable(R.mipmap.icon_file_music));
            }else{
                ImageFactory.loadImageCorner(img, url);
            }



        }
    }

}
