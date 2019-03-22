package com.oahttp;

import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2016/8/9 16:52
 * @Descrition 日志打印
 */
public class LogInterceptor implements Interceptor {

    String tag;

    public LogInterceptor(String tag) {
        this.tag = tag;
    }


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        logForRequest(request);
        Response response = chain.proceed(request);
        return logForResponse(response);
    }


    protected void logForRequest(Request request) {
        String method = request.method();
        if ("POST".equals(method)) {
            Map<String, Object> param = getParam(request.body());
            String strParamUrl = "";
            boolean breaki = true;
            for (String key : param.keySet()) {
                Object value = param.get(key);
                if (breaki) {
                    strParamUrl = strParamUrl + "?" + key + "=" + value;
                } else {
                    strParamUrl = strParamUrl + "&" + key + "=" + value;
                }
                Log.i(tag, "参数: key==" + key + "   value=" + value);
                breaki = false;
            }
            Log.i(tag, "请求地址===" + request.url().toString() + strParamUrl);
        } else {
            Log.i(tag, "请求地址===" + request.url().toString());
        }


    }


    /**
     * 反射获取post请求体参数
     *
     * @param body
     * @return
     */
    private Map<String, Object> getParam(RequestBody body) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        //反向生成class对象
        Class cls = null;
        try {
            cls = Class.forName(body.getClass().getName());
            //将对象实例化
            //System.out.println(cls.getName());
            //得到对象的所有私有属性
            Field[] fields = cls.getDeclaredFields();
            String names = "", values = "";
            for (int i = 0; i < fields.length; i++) {
                //设置私有属性允许访问
                fields[i].setAccessible(true);
                //得到属性值
                String name = fields[i].getName();
                if ("encodedNames".equals(name)) {
                    names = fields[i].get(body).toString();
                }
                if ("encodedValues".equals(name)) {
                    values = fields[i].get(body).toString();
                }
                //System.out.println(names + ":" + values);
            }
            if (!names.endsWith("[]")) {
                names = names.replace("[", "").replace("]", "");
                values = values.replace("[", "").replace("]", "");
                //注意：逗号后面有空格
                String[] argNames = names.split(", ");
                String[] argValues = values.split(", ");
                for (int i = 0; i < argNames.length; i++) {
                    map.put(argNames[i], argValues[i]);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 打印返回数据
     *
     * @param response
     */
    protected Response logForResponse(Response response) {
        try {
            Response.Builder builder = response.newBuilder();
            Response clone = builder.build();
            ResponseBody body = clone.body();
            if (body != null) {
                MediaType mediaType = body.contentType();
                if (mediaType != null) {
                    if (isText(mediaType)) {
                        String string = body.string();
                        Log.i(tag, "返回的数据===" + string+"  返回码："+response.code());
                        body = ResponseBody.create(mediaType, string);
                        return response.newBuilder().body(body).build();
                    } else {
                        Log.i(tag, "返回的数据===" + mediaType.subtype() + "类型数据");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }


    /**
     * 判断结果是否文字类型
     *
     * @param mediaType
     * @return
     */
    private boolean isText(MediaType mediaType) {
        if (mediaType.type() != null && mediaType.type().equals("text")) {
            return true;
        }
        if (mediaType.subtype() != null) {
            if (mediaType.subtype().equals("json") ||
                    mediaType.subtype().equals("xml") ||
                    mediaType.subtype().equals("html") ||
                    mediaType.subtype().equals("webviewhtml")
                    )
                return true;
        }
        return false;
    }
}
