package com.osui.view.listview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

import com.oshttp.NetRequest;
import com.oshttp.callback.StringCallBack;
import com.osui.L;
import com.osui.R;
import com.osui.annotation.InjectReader;
import com.osui.annotation.ViewInject;
import com.osui.data.RowObject;
import com.osui.form.FormAdpater;
import com.osui.utils.JsonUtils;
import com.osui.utils.ViewUtils;
import com.osui.view.CustomLayout;
import com.osui.view.tiplayout.TipLayout;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2018-01-16  10:54
 * @Descrition
 */

public class DataListView extends CustomLayout implements FormAdpater{

    List<RowObject> rows=new LinkedList<RowObject>();

    @ViewInject
    ListView listView;

    @ViewInject
    LinearLayoutForListView linearLayoutForListView;

    FillApdater fillApdater;

    int itemLayoutId;
    @ViewInject
    TipLayout tipLayout ;

    OnItemModifylistenert onItemModifylistenert;

    OnDataListChange onDataListChange;

    NetRequest request;
    String url,urlSuffix,actionClass,actionName,dataExpression,tempUrlSuffix;
    boolean autoInvoke,isEnablePage;
    Map<String, Object> mapParam=new LinkedHashMap<>();

    int pageIndex=1,pageNum=15;
   public static int DEFAUT_PAGE_INDEX=1;


    public DataListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        readAttr(attrs);
        init();
    }


    public DataListView(Context context) {
        super(context);
        init();
    }


    @Override
    public int setXmlLayout() {
        return R.layout.ui_view_autolistview;
    }


    private void init() {
        fillApdater=new FillApdater(getContext(),rows, itemLayoutId);
        createHttpRequest();
    }


    private void readAttr(AttributeSet attrs) {
        TypedArray typedarray=getContext().obtainStyledAttributes(attrs, R.styleable.DataListView);
        itemLayoutId = typedarray.getResourceId(R.styleable.DataListView_itemLayout, -1);
        url=typedarray.getString(R.styleable.DataListView_url);
        urlSuffix=typedarray.getString(R.styleable.DataListView_urlSuffix);
        tempUrlSuffix=typedarray.getString(R.styleable.DataListView_tempUrlSuffix);
        actionClass=typedarray.getString(R.styleable.DataListView_actionClass);
        actionName=typedarray.getString(R.styleable.DataListView_actionName);
        dataExpression=typedarray.getString(R.styleable.DataListView_dataExpression);
        autoInvoke= typedarray.getBoolean(R.styleable.DataListView_autoInvoke,true);
        isEnablePage= typedarray.getBoolean(R.styleable.DataListView_isEnablePage,true);
        String param= typedarray.getString(R.styleable.DataListView_param);
        readParam(param);
        typedarray.recycle();
    }

    private void readParam(String param) {
        if(param!=null){
            //转成json
            param= param.replaceAll(",","\",\"");
            param= param.replaceAll("=","\":\"");
            param="{\""+ param+"\"}";
            int i=0;
            if(JsonUtils.isValidateJson(param)){
                RowObject rowObject = JsonUtils.jsonToRow(param);
                for(String key: rowObject.keySet()){
                    mapParam.put(key,rowObject.get(key));
                }
            }else{
                L.e("======参数格式错误======"+param);
            }
        }

    }

    @Override
    public void initData() {
        notifyDataSetChanged();
        tipLayout.setOnTipListener(new TipLayout.OnTipListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
            @Override
            public void onLoadMore() {
                pageIndex=pageIndex+1;
                if(onDataListChange!=null){
                    onDataListChange.onLoadMore(pageIndex);
                }
                getData();
            }
        });
        tipLayout.setOnErrorViewCilckListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
        if(autoInvoke){
            if(onDataListChange!=null){
                onDataListChange.onRefresh();
            }
            tipLayout.refresh();
            getData();
        }
    }

    public void refresh(){
        rows.clear();
        notifyDataSetChanged();
        pageIndex=DEFAUT_PAGE_INDEX;
        if(onDataListChange!=null){
            onDataListChange.onRefresh();
        }
        tipLayout.refresh();
        getData();
    }



    private void getData() {
        if(tempUrlSuffix!=null){
            request.setUrl(url+tempUrlSuffix);
            tempUrlSuffix=null;
        }else{
            if(urlSuffix!=null){
                url=url+urlSuffix;
                urlSuffix=null;
            }
            request.setUrl(url);
        }
        request.send();
    }

    @Override
    protected void onCreateView() {
        InjectReader.injectAllFields(this);
        tipLayout.setCanScrollView(listView);
    }

    public void createHttpRequest(){
        request = new NetRequest();
        request.setCallback(new StringCallBack() {
            @Override
            public void onSuccess(String text) {
                tipLayout.end();
                if(dataExpression!=null){
                    RowObject rowObject=JsonUtils.jsonToRow(text);
                    if(rowObject.getLayerData(dataExpression) instanceof List){
                        List<RowObject> results= (List<RowObject>) rowObject.getLayerData(dataExpression);
                        setValue(results);
                    }
                }else if(JsonUtils.isCanToRows(text)){
                    List<RowObject> results = JsonUtils.jsonToRows(text);
                    setValue(results);
                }

            }
            @Override
            protected void onFail(Exception e) {
                super.onFail(e);
                ViewUtils.toast(e.getMessage());
                e.printStackTrace();
                tipLayout.error();
            }
        });
        if(mapParam.size()>0){
            request.addParam(mapParam);
        }
    }



    /**
     * 切换为不滚动不回收的listview
     */
    public void noScroll(){
        listView.setVisibility(View.GONE);
        linearLayoutForListView.setVisibility(View.VISIBLE);
    }





    public void notifyDataSetChanged(){
        if(listView.getVisibility()==View.VISIBLE){
            if(listView.getAdapter()==null){
                tipLayout.addFooterView();
                listView.setAdapter(fillApdater);
                L.i("=========notifyDataSetChanged==============");
            }else{
                fillApdater.notifyDataSetChanged();
            }
        }else if(linearLayoutForListView.getVisibility()==View.VISIBLE){
            if(linearLayoutForListView.getAdapter()==null){
                linearLayoutForListView.setAdapter(fillApdater);
            }else{
                fillApdater.notifyDataSetChanged();
            }
        }
    }


    @Override
    public void setValue(Object object) {
        //object可以是List<RowObject>也可以是json数组
        if(object!=null){
            if(object instanceof List){
                List<RowObject> list= (List<RowObject>) object;
                rows.addAll(list);
            }else  if(JsonUtils.isCanToRows(object.toString())){
                List<RowObject> list = JsonUtils.jsonToRows(object.toString());
                rows.addAll(list);
            }
        }
        if(isAttached){
            notifyDataSetChanged();
        }
    }

    public void addUrlSuffix(String suffix){
        url=url+suffix;
    }


    public void addTempUrlSuffix(String suffix){
        tempUrlSuffix=suffix;
    }

    @Override
    public Object getValue() {
        return rows;
    }

    @Override
    public boolean isScanAsOne() {
        return true;
    }


    class FillApdater extends BaseFillAdapter{

        public FillApdater(Context context,List<RowObject> rows, int layout) {
            super(context,rows, layout);
        }
        @Override
        public void setItem(View convertView, RowObject row, int position, ViewHolder holder) {
            if(onItemModifylistenert!=null){
                onItemModifylistenert.setItemView(convertView,row,position,holder);
            }
        }
    }

    public interface OnDataListChange {
        void onRefresh();
        void onLoadMore(int pageIndex);

    }

    /**
     * 添加参数
     *
     * @param key
     * @param value
     */
    public void addParam(String key, Object value) {
        request.addParam(key, value);
    }
    /**
     * 添加临时参数
     *
     * @param key
     * @param value
     */
    public void addTempParam(String key, Object value) {
        request.addTempParam(key, value);
    }



    public interface OnItemModifylistenert {
        void setItemView(View convertView, RowObject row, int position, BaseFillAdapter.ViewHolder holder);
    }

    public void setOnItemClickListener(BaseFillAdapter.OnItemClickListener onItemClickListener) {
        fillApdater.setOnItemClickListener(onItemClickListener);
    }

    public FillApdater getFillApdater() {
        return fillApdater;
    }

    public void setFillApdater(FillApdater fillApdater) {
        this.fillApdater = fillApdater;
    }

    public List<RowObject> getRows() {
        return rows;
    }

    public void setRows(List<RowObject> rows) {
        this.rows = rows;
    }

    public OnDataListChange getOnDataListChange() {
        return onDataListChange;
    }

    public void setOnDataListChange(OnDataListChange onDataListChange) {
        this.onDataListChange = onDataListChange;
    }

    public NetRequest getRequest() {
        return request;
    }

    public void setRequest(NetRequest request) {
        this.request = request;
    }
}
