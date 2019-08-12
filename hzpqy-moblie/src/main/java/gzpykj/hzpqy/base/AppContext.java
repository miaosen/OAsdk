package gzpykj.hzpqy.base;

import android.app.Application;
import android.os.StrictMode;

import cn.oahttp.ClientFactory;
import cn.oahttp.LogInterceptor;
import cn.oahttp.cookies.CookieManager;
import cn.oaui.UIGlobal;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2017-08-03  16:16
 * @Descrition
 */

public class AppContext extends Application {

    private static AppContext instance;

    @Override
    public void onCreate() {
        super.onCreate();
        UIGlobal.setApplication(this);
        initHttpConfig();
        instance=this;
        HzDataListView.GLOBAL_URL=Global.HOST;
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
        // android 7.0系统解决拍照的问题
        if(android.os.Build.VERSION.SDK_INT >=18){
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();
        }
    }

    public static AppContext getInstance() {
        return instance;
    }

    public static void setInstance(AppContext instance) {
        AppContext.instance = instance;
    }
}
