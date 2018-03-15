package com.osui.data;

import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import com.oshttp.NetRequest;
import com.oshttp.callback.StringCallBack;
import com.osui.L;
import com.osui.R;
import com.osui.utils.JsonUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2018-01-16  11:52
 * @Descrition 数据创建处理器
 */
public class DataProvider  {

    //设置这个地址会覆盖掉框架的请求地址
    String url;
    String urlSuffix;
    //获取数据成功后提示
    String successMsg;
    //获取数据失败后提示
    String failMsg;
    //获取数据中提示
    String loadingMsg;

    //默认为true
    boolean autoInvoke;
    //默认为false
    boolean isDataFromSql;
    //默认为false
    boolean isGetParamFromThis;
    //默认为false
    boolean isFillThis;
    //默认为false
    boolean isByGet;
    //指定获取表单的区域的View的id
    int paramArea;
    //指定填充数据的区域的View的id
    int fillArea;
    //id 通过点击当前view的某个控件去调用数据请求
    int invokeByClick;

    //获取到数据后向目标输出的view
    View dataView;
    //实现DataCreateProcess接口的view
    View observerView;

    Map<String, Object> mapParam=new LinkedHashMap<>();


    //public DataListView(Context context, AttributeSet attrs) {
    //    super(context, attrs);
    //    readAttr(attrs);
    //}

    //public DataListView(Context context) {
    //    super(context);
    //}

    //@Override
    //protected void onLayout(boolean changed, int l, int t, int r, int b) {
    //    if(getChildCount()>0){
    //        dataView=this;
    //    }
    //}

    public DataProvider(View view,AttributeSet attrs){
        TypedArray typedarray=view.getContext().obtainStyledAttributes(attrs, R.styleable.DataListView);
        url = typedarray.getString(R.styleable.DataListView_url);
        urlSuffix= typedarray.getString(R.styleable.DataListView_urlSuffix);
        String param= typedarray.getString(R.styleable.DataListView_param);
        readParam(param);
        autoInvoke=typedarray.getBoolean(R.styleable.DataListView_autoInvoke,true);
        isDataFromSql=typedarray.getBoolean(R.styleable.DataListView_isDataFromSql,false);
        //isGetParamFromThis=typedarray.getBoolean(R.styleable.DataCreator_isGetParamFromThis,false);
        //isByGet=typedarray.getBoolean(R.styleable.DataCreator_isByGet,false);
        //isFillThis=typedarray.getBoolean(R.styleable.DataCreator_isFillThis,false);
        //paramArea = typedarray.getResourceId(R.styleable.DataCreator_paramArea, -1);
        //fillArea = typedarray.getResourceId(R.styleable.DataCreator_fillArea, -1);
        //invokeByClick = typedarray.getResourceId(R.styleable.DataCreator_invokeByClick, -1);
        typedarray.recycle();
    }


    private void readParam(String param) {
        if(param!=null){
            //转成json
            param= param.replaceAll(",","\",\"");
            param= param.replaceAll("=","\":\"");
            param="{\""+ param+"\"}";
            L.i("====readParam========"+param);
            int i=0;
            if(JsonUtils.isValidateJson(param)){
                RowObject rowObject = JsonUtils.jsonToRow(param);
                for(String key: rowObject.keySet()){
                    mapParam.put(key,rowObject.get(key));
                }
                L.i("====readParam========"+mapParam);
            }else{
                L.e("====出错了========参数格式异常！");
            }
        }
    }

    //@Override
    //protected void onAttachedToWindow() {
    //    super.onAttachedToWindow();
    //    if (autoInvoke) {
    //        getData();
    //    }
    //}

    private void getData() {
        NetRequest request;
        if(urlSuffix!=null){
             request=new NetRequest(url+urlSuffix);
        }else{
             request=new NetRequest(url);
        }
        request.addParam(mapParam);
        request.setCallback(new StringCallBack() {
            @Override
            public void onSuccess(String text) {
                L.i("=========onSuccess=============="+text);
            }
        });
        request.send();
        
    }





    public void init(){

    }

    public void refresh(){

    }

    public void loading(){

    }
    public void complate(){

    }
    public void error(){

    }


}
