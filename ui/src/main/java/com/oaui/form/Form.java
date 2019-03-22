package com.oaui.form;

import android.content.Context;
import android.view.View;

import com.oaui.data.RowObject;
import com.oaui.utils.JsonUtils;
import com.oaui.utils.StringUtils;
import com.oaui.utils.ViewUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @git http://git.oschina.net/miaosen/MyUtils
 * @CreateDate 2016/8/18 10:09
 * @Descrition 数据填充模块
 */
public class Form {

    private Context context;

    //内容布局
    private View contentView = null;
    //tag不为空的view,以该view的tag为key
    private Map<String, View> mapView=new HashMap<String, View>();


    private RowObject row = null;

    private OnFillLisener onFillLisener;

    /**
     * View的信息最终转成mapView
     *
     * @param mapView
     */
    public Form(Map<String, View> mapView) {
        init();
        this.mapView .putAll(mapView);
    }

    public Form(Context context) {
        init();
        this.context = context;
        this.contentView = ViewUtils.getContentView(context);
        mapView.putAll(FormUtils.getMapView(contentView));
        //L.i("=========Form=============="+mapView);
    }


    public Form(View view) {
        init();
        mapView.putAll(FormUtils.getMapView(view));
    }

    public void init() {

    }


    public void fill(RowObject row) {
        this.row = row;
        fill();
    }

    /**
     * json数据填充
     */
    public void fill(String json) {
        if (JsonUtils.isCanToRow(json)) {
            this.row = JsonUtils.jsonToRow(json);
        }
        fill();
    }


    /**
     * 开始填充
     */
    public void fill() {
        if (onFillLisener != null) {
            onFillLisener.onBefore(mapView, row);
        }
        for (String idName : mapView.keySet()) {
            String value = row.getString(idName);
            if (StringUtils.isNotEmpty(value)) {
                View view = mapView.get(idName);
                boolean needToContinue = true;
                if (onFillLisener != null) {
                    needToContinue = onFillLisener.onFill(view, idName, value);
                }
                if (needToContinue) {
                    FormUtils.setContentValue(view, value);
                }
            }
        }
        if (onFillLisener != null) {
            onFillLisener.onBefore(mapView, row);
        }

    }


    /**
     * 获取要表单的内容
     *
     * @return
     */
    public RowObject getContentValue() {
        return FormUtils.getContentValues(mapView);
    }







    public interface OnFillLisener {
        void onBefore(Map<String, View> formViews, RowObject row);

        boolean onFill(View view, String name, String value);

        void onComplate(Map<String, View> formViews, RowObject row);
    }


    public RowObject getRow() {
        return row;
    }

    public void setRow(RowObject row) {
        this.row = row;
    }

    public OnFillLisener getOnFillLisener() {
        return onFillLisener;
    }

    public void setOnFillLisener(OnFillLisener onFillLisener) {
        this.onFillLisener = onFillLisener;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public View getContentView() {
        return contentView;
    }

    public void setContentView(View contentView) {
        this.contentView = contentView;
    }

    public Map<String, View> getMapView() {
        return mapView;
    }

    public void setMapView(Map<String, View> mapView) {
        this.mapView = mapView;
    }

}
