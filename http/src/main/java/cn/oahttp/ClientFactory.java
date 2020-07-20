package cn.oahttp;

import android.app.Application;
import android.os.Build;
import android.webkit.WebSettings;

import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;


public class ClientFactory {

    public static Application application;


    public static OkHttpClient getClient() {
        return Client.okHttpClient;
    }

    public static void setClient(OkHttpClient okHttpClient) {
        Client.okHttpClient = okHttpClient;
    }

    private static class Client {
        volatile static OkHttpClient okHttpClient = new OkHttpClient();

    }

    public static void callAllRequest() {
        getClient().dispatcher().cancelAll();

    }

    public static void callRequestByTag(String tag) {
        List<Call> calls = getClient().dispatcher().runningCalls();
        for (int i = 0; i < calls.size(); i++) {
            Call call = calls.get(i);
            String tagInRq = call.request().tag() + "";
            if (tag.equals(tagInRq)) {
                call.cancel();
            }
        }
    }


    public static Application getApplication() {
        return application;
    }

    public static void setApplication(Application application) {
        ClientFactory.application = application;
    }


    public static String getUserAgent() {
        String userAgent = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                userAgent = WebSettings.getDefaultUserAgent(application);
            } catch (Exception e) {
                userAgent = System.getProperty("http.agent");
            }
        } else {
            userAgent = System.getProperty("http.agent");
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0, length = userAgent.length(); i < length; i++) {
            char c = userAgent.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
        //return "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36";
    }

}
