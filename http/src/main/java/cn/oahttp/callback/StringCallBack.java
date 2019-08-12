package cn.oahttp.callback;

import android.util.Log;

import cn.oahttp.HandlerQueue;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @git http://git.oschina.net/miaosen/MyUtils
 * @CreateDate 2017-08-04  14:35
 * @Descrition
 */

public abstract class StringCallBack implements Callback {


    @Override
    public void onFailure(Call call, final IOException e) {
        HandlerQueue.onResultCallBack(new Runnable() {
            @Override
            public void run() {
                onFail(new Exception(e));
            }
        });
    }

    @Override
    public void onResponse(final Call call, final Response response) throws IOException {
        if (response.isSuccessful()) {
            final String result = response.body().string();
            Log.i("logtag","=========onResponse==============");
            HandlerQueue.onResultCallBack(new Runnable() {
                @Override
                public void run() {
                    onSuccess(result);
                }
            });
        }else if(call.isCanceled()){
            HandlerQueue.onResultCallBack(new Runnable() {
                @Override
                public void run() {
                    onFail(new Exception("下载请求被取消：tag : "+call.request().tag()));
                }
            });
        }else{
            HandlerQueue.onResultCallBack(new Runnable() {
                @Override
                public void run() {
                    onFail(new Exception("网络请求失败！错误代码："+response.code()+" 信息："+response.message()));
                }
            });
        }
    }

    public abstract void onSuccess(String text);

    protected void onFail(Exception e) {

    }


}
