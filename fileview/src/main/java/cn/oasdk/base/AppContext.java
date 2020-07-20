package cn.oasdk.base;

import android.app.Application;

import com.danikula.videocache.HttpProxyCacheServer;

import java.io.File;
import java.util.concurrent.TimeUnit;

import cn.oahttp.ClientFactory;
import cn.oahttp.cookies.CookieManager;
import cn.oasdk.fileview.data.FileData;
import cn.oaui.L;
import cn.oaui.UIGlobal;
import cn.oaui.utils.FileUtils;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;


/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2017-08-03  16:16
 * @Descrition
 */

public class AppContext extends Application {


    public static AppContext application;

    public static AppContext getApplication() {
        return application;
    }



    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        UIGlobal.setApplication(this);
//       FileData.initData();
        initHttpConfig();
    }



    public void initData() {
        FileData.initData();
    }

    private void initHttpConfig() {
        //创建okHttpClient对象
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger());
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        ClientFactory.setApplication(this);
        OkHttpClient client = ClientFactory.getClient();
        OkHttpClient.Builder buidler = client.newBuilder();
        // 连接超时120秒
        buidler.connectTimeout(60000, TimeUnit.SECONDS);
        buidler.readTimeout(60000, TimeUnit.SECONDS);
        buidler.writeTimeout(60000, TimeUnit.SECONDS);
        buidler.cookieJar(new CookieManager(this));
        //buidler.addInterceptor(logInterceptor);
        //buidler.addInterceptor(new LogInterceptor("logtag"));
        client = buidler.build();
        ClientFactory.setClient(client);

    }

    private HttpProxyCacheServer proxy;

    public static HttpProxyCacheServer getProxy() {
        return getApplication().proxy == null ? (getApplication().proxy = getApplication().newProxy()) : getApplication().proxy;
    }



    private HttpProxyCacheServer newProxy() {
        String appDirPath = FileUtils.getSDCardPath()+"/"+AppContext.getApplication().getPackageName();
        File file = new File(appDirPath);
        if(!file.exists()){
            file.mkdirs();
        }
        L.i("============newProxy==========="+appDirPath);
        return new HttpProxyCacheServer.Builder(this)
                .cacheDirectory(file)
                //最大缓存200M
                .maxCacheSize(300 * 1024 * 1024)
                .build();
    }



    private class HttpLogger implements HttpLoggingInterceptor.Logger {
        private StringBuilder mMessage = new StringBuilder();
        @Override
        public void log(String message) {
            // 请求或者响应开始
            if (message.startsWith("--> POST")) {
                mMessage.setLength(0);
            }
            mMessage.append(message.concat("\n"));
            // 请求或者响应结束，打印整条日志
            if (message.startsWith("<-- END HTTP")) {
                L.i(mMessage.toString());
            }
        }
    }



}
