package cn.oahttp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.webkit.WebSettings;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import okhttp3.Response;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2018-01-31  11:34
 * @Descrition
 */

public class HttpUtils {

    /**
     * 判断网络是否连接
     *
     * @return
     */
    public static boolean isConnected() {
        ConnectivityManager connectivity = (ConnectivityManager) ClientFactory.getApplication()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != connectivity) {

            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (null != info && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 从头部获取文件名称信息，没有则判断url中有没有包含文件名称，没有取系统时间作为名称
     *
     * @param response
     * @param url
     * @return
     */
    public static String getFileName(Response response, String url) {
        String fileName = System.currentTimeMillis() + "";
        String cd = response.header("Content-Disposition");
        if (cd != null) {
            String[] split = cd.split(";");
            for (int i = 0; i < split.length; i++) {
                String str = split[i];
                if (str.indexOf("filename=") > -1) {
                    fileName = str.replace("filename=", "");
                    fileName = decodeForResponse(response, fileName);
                }
            }
        } else if (getNameInUrl(url) != null) {
            fileName = decodeForResponse(response, fileName);
            fileName = getNameInUrl(url);
        }
        return fileName;
    }


    /**
     * 从头部获取文件名称信息，没有则判断url中有没有包含文件名称，没有取系统时间作为名称
     *
     * @param url
     * @return
     */
    public static String getFileName(String url) {
        String fileName = System.currentTimeMillis() + "";
        String encode = "UTF-8";
        try {
            if (getNameInUrl(url) != null) {
                fileName = URLDecoder.decode(fileName, encode);
                fileName = getNameInUrl(url);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileName;
    }

    /**
     * 从网页获取编码，然后转换
     *
     * @param response
     * @param url
     * @return
     */
    public static String decodeForResponse(Response response, String url) {
        String encode = "UTF-8";
        if (response.body().contentType() != null && response.body().contentType().charset() != null) {
            //检查头部contentType有没有编码信息，没有则默认为UTF-8
            encode = response.body().contentType().charset().name();
        }
        try {
            url = URLDecoder.decode(url, encode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * 从URL中获取文件名，从左到右找到最后一个“/”字符，再从这个字符开始找到第一个“?”字符，
     * 如果没有这个字符则整串为文件名；如果有则取“/”与“?”之间的字符串,如果字符串包含“.”，
     * 就返回该字符串，没有则返回null
     *
     * @param url
     */
    private static String getNameInUrl(String url) {
        String s = url;
        s = s.substring(s.lastIndexOf("/") + 1, s.length());
        if (s.contains("?")) {
            String[] split = s.split("[?]");
            for (int i = 0; i < split.length; i++) {
                String s1 = split[i];
                if (s1.contains(".")&&s1.indexOf(".")+1<s1.length()) {
                    return s1;
                }
            }
        }else if (s.contains(".")) {
            return s;
        }
        return null;
    }

    public static String getUserAgent() {
        String userAgent = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                userAgent = WebSettings.getDefaultUserAgent(ClientFactory.getApplication());
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
