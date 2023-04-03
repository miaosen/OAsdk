package cn.oahttp.cookies;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.webkit.CookieSyncManager;

import java.util.LinkedHashMap;
import java.util.LinkedList;
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
       String key= url.host()+":"+url.port();
        for (int i = 0; i < cookies.size(); i++) {
            Cookie cookie = cookies.get(i);
        }
        if(cookies.size()>0&&!allCookies.containsKey(key)){
            allCookies.put(key,cookies);
            //保存到缓存
            CookiePersistence.saveToCache(context,key,cookies);
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        String key= url.host()+":"+url.port();
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

    public static List<Cookie> getCookies(HttpUrl url){
        String key= url.host()+":"+url.port();
        return  allCookies.get(key);
    }

    public static List<Cookie> setCookies(Application context,HttpUrl url,String cookiesValue){
        String key= url.host()+":"+url.port();
        String[] split = cookiesValue.trim().split(";");
        List<Cookie> cookies=new LinkedList<>();
        for (int i = 0; i < split.length; i++) {
            String s = split[i];
            if(s.contains("=")){
                String[] split1 = s.split("=");
                Cookie.Builder builder = new Cookie.Builder();
                builder = builder.name(split1[0]);
                builder = builder.value(split1[1]);
                builder.domain(url.host());
                builder.path("/spjg");
                builder.httpOnly();
               // Log.i("logtag", "============path==========="+url.encodedPath());
                Cookie cookie = builder.build();
                cookies.add(cookie);
            }
        }
      CookiePersistence.saveToCache(context,key,cookies);
        return  allCookies.put(key,cookies);
    }






    /**
     * APP中cookies传到webview
     * @param url
     * @param context
     */
    public static void syncWebviewCookies(String url, Context context) {
        HttpUrl parse = HttpUrl.parse(url);
        List<Cookie> cookies = CookieManager.getCookies(parse);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(context);
        }
        android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeAllCookie();
        if(cookies!=null){
            for (Cookie cookie : cookies) {
                StringBuilder sbCookie = new StringBuilder();//创建一个拼接cookie的容器,
                sbCookie.append(cookie.name() + "=" + cookie.value());
                sbCookie.append(";domain=" + cookie.domain());
                sbCookie.append(";path=" + cookie.path());
                String cookieValue = sbCookie.toString();
                cookieManager.setCookie(url, cookieValue);//为url设置cookie
            }
            CookieSyncManager.getInstance().sync();//同步cookie
        }

    }


    /**
     * webview中cookies传到APP
     * @param url
     */
    public static void syncAppCookies(Application context,String url) {
        HttpUrl parse = HttpUrl.parse(url);
        android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        String cookie1 = cookieManager.getCookie(url);
        setCookies(context,parse,cookie1);

    }

}
