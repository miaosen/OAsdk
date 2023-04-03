package cn.oasdk.base;

import cn.oahttp.HttpRequest;
import cn.oaui.data.Row;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2018-06-14  16:29
 * @Descrition
 */

public class GlobalConst {

    //192.168.0.100   ,
    //public static String HOST="http://192.168.0.100:8080/hzpqyd";
    //public final static String HOST ="http://10.0.0.11";
    public final static String HOST ="http://192.168.1.177";
    public final static String PORT ="8081";
    public final static String CONTEXT_PATH ="/spjg_xzf";


    //public final static String HOST ="https://www.gzpgkj.com";
    //public final static String PORT ="443";
    //public final static String CONTEXT_PATH ="/spjg";

    public final static String SERVICE_ROOT=HOST+":"+PORT+CONTEXT_PATH;



    public static Row userInfo;

    public static HttpRequest createRequest(String actionPath){
        HttpRequest request = new HttpRequest(HOST +actionPath);
        return request;
    }


    public static void addJsonParam(HttpRequest request){
        request.addParam("__RENDER_MODE__","json");
        request.addParam("__RETURN_TYPE__","data");
    }

    public static void saveUserInfo(Row userInfo){
        GlobalConst.userInfo=userInfo;
    }

    public static Row getUserInfo(){
        return GlobalConst.userInfo;
    }
}
