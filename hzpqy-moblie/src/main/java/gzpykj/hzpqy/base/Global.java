package gzpykj.hzpqy.base;

import cn.oahttp.HttpRequest;
import cn.oaui.data.RowObject;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2018-06-14  16:29
 * @Descrition
 */

public class Global {

    //192.168.0.100   ,
    //public static String HOST="http://192.168.0.100:8080/hzpqyd";
    public final static String HOST ="http://121.8.227.140:8080/hzpqyd";


    public static RowObject userInfo;

    public static HttpRequest createRequest(String actionPath){
        HttpRequest request = new HttpRequest(HOST +actionPath);
        return request;
    }


    public static void addJsonParam(HttpRequest request){
        request.addParam("__RENDER_MODE__","json");
        request.addParam("__RETURN_TYPE__","data");
    }

    public static void saveUserInfo(RowObject userInfo){
        Global.userInfo=userInfo;
    }

    public static RowObject getUserInfo(){
        return Global.userInfo;
    }
}
