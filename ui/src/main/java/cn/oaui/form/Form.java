package cn.oaui.form;

import android.content.Context;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import cn.oaui.data.Row;
import cn.oaui.utils.JsonUtils;
import cn.oaui.utils.StringUtils;
import cn.oaui.utils.ViewUtils;


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
    private Map<String, View> mapView = new HashMap<String, View>();


    private Row row = null;

    private OnFillLisener onFillLisener;

    /**
     * View的信息最终转成mapView
     *
     * @param mapView
     */
    public Form(Map<String, View> mapView) {
        init();
        this.mapView.putAll(mapView);
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


    public void fill(Row row) {
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
        if(row!=null){
            for (String idName : mapView.keySet()) {
                Object value = row.get(idName);
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
        }
        if (onFillLisener != null) {
            onFillLisener.onBefore(mapView, row);
        }

    }



    public interface OnFillLisener {
        void onBefore(Map<String, View> formViews, Row row);

        boolean onFill(View view, String name, Object value);

        void onComplate(Map<String, View> formViews, Row row);
    }


    public Row getRow() {
        return row;
    }

    public void setRow(Row row) {
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
