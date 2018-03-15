package com.oshttp.callback;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.oshttp.HandlerQueue;

import java.io.IOException;
import java.io.InputStream;

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

public abstract class BitmapCallBack implements Callback {


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
            try {
                InputStream inputStream = response.body().byteStream();
                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                HandlerQueue.onResultCallBack(new Runnable() {
                    @Override
                    public void run() {
                        onSuccess(bitmap);
                    }
                });
            } catch (final Exception e) {
                e.printStackTrace();
                HandlerQueue.onResultCallBack(new Runnable() {
                    @Override
                    public void run() {
                        onFail(e);
                    }
                });
            }
        } else {
            HandlerQueue.onResultCallBack(new Runnable() {
                @Override
                public void run() {
                    onFail(new Exception("网络请求失败！错误代码：" + response.code() + " 信息：" + response.message()));
                }
            });
        }
    }

    public abstract void onSuccess(Bitmap bitmap);

    protected void onFail(Exception e) {
    }


}
