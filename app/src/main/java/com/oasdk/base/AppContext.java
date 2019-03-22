package com.oasdk.base;

import android.app.Application;
import android.os.StrictMode;

import com.oahttp.ClientFactory;
import com.oahttp.LogInterceptor;
import com.oahttp.cookies.CookieManager;
import com.oaui.UIGlobal;
import com.oaui.view.listview.DataListView;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2017-08-03  16:16
 * @Descrition
 */

public class AppContext extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        UIGlobal.setApplication(this);
        initHttpConfig();
        DataListView.GLOBAL_URL=Global.HOST;
        // android 7.0系统解决拍照的问题
        if(android.os.Build.VERSION.SDK_INT >=18){
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();
        }
    }

    private void initHttpConfig() {
        ClientFactory.setApplication(this);
        OkHttpClient client = ClientFactory.getClient();
        OkHttpClient.Builder buidler = client.newBuilder();
        // 连接超时120秒
        buidler.connectTimeout(120, TimeUnit.SECONDS);
        buidler.readTimeout(120, TimeUnit.SECONDS);
        buidler.writeTimeout(120, TimeUnit.SECONDS);
        buidler.cookieJar(new CookieManager(this));
        buidler.addInterceptor(new LogInterceptor("logtag"));
        client = buidler.build();
        ClientFactory.setClient(client);

    }


}
