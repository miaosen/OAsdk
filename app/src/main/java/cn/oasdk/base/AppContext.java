package cn.oasdk.base;

import android.app.Application;
import android.os.StrictMode;

import java.util.concurrent.TimeUnit;

import cn.oahttp.ClientFactory;
import cn.oahttp.cookies.CookieManager;
import cn.oaui.UIGlobal;
import cn.oaui.view.listview.DataListView;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

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
        DataListView.GLOBAL_URL= GlobalConst.HOST;
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
        buidler.connectTimeout(60000, TimeUnit.SECONDS);
        buidler.readTimeout(60000, TimeUnit.SECONDS);
        buidler.writeTimeout(60000, TimeUnit.SECONDS);
        buidler.cookieJar(new CookieManager(this));
        //buidler.addInterceptor(new LogInterceptor("logtag"));
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {

            }
        });//创建拦截对象
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);//这一句一定要记得写，否则没有数据输出
        buidler .addInterceptor(logInterceptor);
        client = buidler.build();
        ClientFactory.setClient(client);

    }


}
