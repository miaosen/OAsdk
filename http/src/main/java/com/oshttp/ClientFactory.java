package com.oshttp;

import android.app.Application;

import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;


public class ClientFactory {

    public static Application application;



    public static OkHttpClient getClient(){
        return Client.okHttpClient;
    }

    public static void setClient(OkHttpClient okHttpClient){
         Client.okHttpClient=okHttpClient;
    }

    private static class Client{
        volatile static OkHttpClient okHttpClient=new OkHttpClient();
    }

    public static void callAllRequest(){
        getClient().dispatcher().cancelAll();

    }

    public static void callRequestByTag(String tag){
        List<Call> calls = getClient().dispatcher().runningCalls();
        for (int i = 0; i < calls.size(); i++) {
            Call call = calls.get(i);
            String tagInRq = call.request().tag()+"";
            if(tag.equals(tagInRq)){
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
}
