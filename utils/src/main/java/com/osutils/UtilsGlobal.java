package com.osutils;

import android.app.Application;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @git http://git.oschina.net/miaosen/MyUtils
 * @CreateDate 2017-08-04  11:24
 * @Descrition
 */

public class UtilsGlobal {

    private static Application application;


    public static Application getApplication() {
        return application;
    }

    public static void setApplication(Application application) {
        UtilsGlobal.application = application;
    }
}
