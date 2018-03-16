package com.oahttp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2018-01-31  11:34
 * @Descrition
 */

public class HttpUtils {

    /**
     * 判断网络是否连接
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
}
