package com.oahttp.cookies;

import android.app.Application;
import android.util.Log;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2017-08-04  15:37
 * @Descrition cookie管理
 */

public class CookieManager implements CookieJar {

    private Application context;

    public CookieManager(Application context){
        this.context=context;
    }

    //cookies缓存到内存
    public static Map<String,List<Cookie>> allCookies=new LinkedHashMap<String,List<Cookie>>();

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        //根据ip/域名和端口来保存一组cookies
       String key= url.host()+url.port();
        if(cookies.size()>0){
            Log.i("logtag","text======"+cookies.toString());
            allCookies.put(key,cookies);
            //保存到缓存
            CookiePersistence.saveToCache(context,key,cookies);
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        String key= url.host()+url.port();
        List<Cookie> cookies = allCookies.get(key);
        if(cookies==null){
            //从缓存取
            return CookiePersistence.getFormCache(context,key);
        }else{
            return cookies;
        }
    }


    /**
     * 清空内存和缓存的cookie
     */
    public static void clearAll(Application context) {
        allCookies.clear();
        CookiePersistence.clear(context);
    }




}
