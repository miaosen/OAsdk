package com.oshttp;

import android.os.Handler;
import android.os.Looper;

/**
 * @email 1510809124@qq.com
 * @author zengmiaosen
 * @CreateDate 2016/8/6 10:07
 * @Descrition UI线程操作队列,单例(静态内部类)
 */ 
public class HandlerQueue {


    /**
     * 回调消息发送到Ui线程
     * @param r
     */
    public static void onResultCallBack(Runnable r){
         Queue.handler.post(r);
    }

    private static class Queue{
        /**
         * UI线程操作
         * 不是主线程的话,需要使用Looper.prepare(); Handler handler = new Handler();Looper.loop()结构;
         * 或者Handler handler = new Handler(Looper.getMainLooper());
         */
        private static Handler handler= new Handler(Looper.getMainLooper());

    }


}
