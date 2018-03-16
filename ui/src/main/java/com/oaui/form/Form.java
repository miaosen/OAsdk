package com.oaui.form;

import android.content.Context;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.oaui.data.RowObject;
import com.oaui.utils.JsonUtils;
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

    private OnFillMessageListener onFillMessageListener;

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
        if (row != null && row.size() > 0) {
            for (String key : mapView.keySet()) {
                View view = mapView.get(key);
                //Log.i("logtag","key==========="+key+"  view==="+view);
                if (view != null) {
                    Object value = row.getLayerData(key);
                    //Log.i("logtag","value==========="+value);
                    if (value != null) {
                        fillMessage(view, key, value);
                    }
                }
            }
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


    /**
     * 数据填充到View
     *
     * @param view
     * @param key
     * @param value
     */
    protected void fillMessage(View view, String key, Object value) {
        boolean success = false;
        if (onFillMessageListener != null) {//填充监听
            success = onFillMessageListener.fillMessage(view, key, value);
        }
        if (!success) {
            //Log.i("logtag", "view===========" + view + "  tag:" + view.getTag());
            //success = customFillMessage(view, key, value);
            if (view instanceof FormAdpater) {//实现ViewValueAdpater接口的自定义View填充
                ((FormAdpater) view).setValue(value);
            } else if (view instanceof TextView) {// 填充TextView或者TextView的子类
                ((TextView) view).setText(value.toString());
            } else if (view instanceof RadioGroup) {// 填充RadioGroup
                RadioGroup rg = (RadioGroup) view;
                ViewUtils.fillRadioGroup(rg, value.toString());
            }
        }
    }


    /**
     * 数据补充填充接口
     */
    public interface OnFillMessageListener {
        boolean fillMessage(View view, String key, Object value);
    }





    public RowObject getRow() {
        return row;
    }

    public void setRow(RowObject row) {
        this.row = row;
    }

    public OnFillMessageListener getOnFillMessageListener() {
        return onFillMessageListener;
    }

    public void setOnFillMessageListener(OnFillMessageListener onFillMessageListener) {
        this.onFillMessageListener = onFillMessageListener;
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
