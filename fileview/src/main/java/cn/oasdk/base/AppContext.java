package cn.oasdk.base;

import android.app.Application;

import cn.oasdk.fileview.data.FileData;
import cn.oaui.UIGlobal;


/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2017-08-03  16:16
 * @Descrition
 */

public class AppContext extends Application {


    public static AppContext application;

    public static AppContext getApplication() {
        return application;
    }



    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        UIGlobal.setApplication(this);
//       FileData.initData();
    }



    public void initData() {
        FileData.initData();
    }

    private void initHttpConfig() {

    }


}
