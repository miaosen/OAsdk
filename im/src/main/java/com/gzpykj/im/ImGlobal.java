package com.gzpykj.im;

import android.app.Application;

import cn.oahttp.HttpRequest;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2018-06-14  16:29
 * @Descrition
 */

public class ImGlobal {
    public  static String HOST ="";

    //public  static String HOST ="http://192.168.1.177:8081/hzpqyd";


    //public  static String RTMP_HOST ="rtmp://192.168.1.177:1935/live/home";
    public  static String RTMP_HOST ="rtmp://www.gzpgkj.com:1935/live/home";

    //登录名
    public static  String USER_NAME="";
    //昵称
    public static  String NAME="";
    public static  String USER_ID="";
    public static  String PASSWORD="";


    public static HttpRequest createRequest(String actionPath){
        return  new HttpRequest(HOST +actionPath);
    }


    public static void addJsonParam(HttpRequest request){
        request.addParam("__RENDER_MODE__","json");
        request.addParam("__RETURN_TYPE__","data");
    }




    public static Application application;


    public static Application getApplication() {
        return application;
    }

    public static void setApplication(Application application) {
        ImGlobal.application = application;
    }
}
