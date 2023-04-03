package cn.oahttp.cookies;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import okhttp3.Cookie;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2017-08-09  15:33
 * @Descrition Cookies持久化
 */
public class CookiePersistence {

    //缓存文件名称
    private final static String PRE_NAME = "cookiesCache";


    public static void saveToCache(Application context, String key, List<Cookie> cookies) {
        String serialize = serialize(cookies);
        Log.i("logtag", "saveToCache======" + serialize);
        SharedPreferences sp = context.getSharedPreferences(PRE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, serialize);
        editor.commit();
    }


    public static List<Cookie> getFormCache(Application context, String key) {
        SharedPreferences sp = context.getSharedPreferences(PRE_NAME, Context.MODE_PRIVATE);
        String string = sp.getString(key, null);
        //Log.i("logtag", "getFormCache======" + string);
        return deserialize(string);
    }

    public static void clear(Application context) {
        SharedPreferences sp = context.getSharedPreferences(PRE_NAME, Context.MODE_PRIVATE);
        sp.edit().clear().commit();

    }


    /**
     * 序列化为字符串
     * @param cookies
     */
    public static String serialize(List<Cookie> cookies) {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < cookies.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            Cookie cookie = cookies.get(i);
            try {
                jsonObject.put("name", cookie.name() + "");
                jsonObject.put("value", cookie.value() + "");
                jsonObject.put("expiresAt", cookie.expiresAt());
                jsonObject.put("domain", cookie.domain() + "");
                jsonObject.put("path", cookie.path() + "");
                jsonObject.put("secure", cookie.secure());
                jsonObject.put("hostOnly", cookie.hostOnly());
                jsonObject.put("httpOnly", cookie.httpOnly());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArray.toString();
    }


    /**
     * 从文件中反序列化
     *
     * @param jsonStr
     * @return
     */
    public static List<Cookie> deserialize(String jsonStr) {
        List<Cookie> cookies = new LinkedList<Cookie>();
        if (jsonStr!=null&&jsonStr.length()>0) {
            try {
                //Log.i("logtag", "deserialize======jsonStr===" + jsonStr);
                JSONArray jsonArray = new JSONArray(jsonStr);
                //Log.i("logtag", "deserialize======" + jsonArray.length());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String name = jsonObject.getString("name");
                    String value = jsonObject.getString("value");
                    long expiresAt = jsonObject.getLong("expiresAt");
                    String domain = jsonObject.getString("domain");
                    String path = jsonObject.getString("path");
                    boolean secure = jsonObject.getBoolean("secure");
                    boolean hostOnly = jsonObject.getBoolean("hostOnly");
                    boolean httpOnly = jsonObject.getBoolean("httpOnly");
                    Cookie.Builder builder = new Cookie.Builder();
                    builder = builder.name(name);
                    builder = builder.value(value);
                    builder = builder.expiresAt(expiresAt);
                    builder = hostOnly ? builder.hostOnlyDomain(domain) : builder.domain(domain);
                    builder = builder.path(path);
                    builder = secure ? builder.secure() : builder;
                    builder = httpOnly ? builder.httpOnly() : builder;
                    Cookie cookie = builder.build();
                    cookies.add(cookie);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return cookies;
    }


}
