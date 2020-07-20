package cn.oahttp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2017-08-04  10:46
 * @Descrition 网络请求，基于okhttp3.8.1,只处理了post，get方法的请求，另外6这种方法不常用，暂不处理
 * 如果要处理另外6种方法，调用setMethod方法后自行处理requestBuilder即可
 */

public class HttpRequest {

    private OkHttpClient client;
    //请求地址
    private String url;
    //请求参数
    private Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
    //临时请求地址
    private Map<String, Object> tempParamMap = new LinkedHashMap<String, Object>();
    //请求体
    private Request.Builder requestBuilder = null;
    private Request request = null;
    // 默认post提交方式,否则为get方式
    private String method = HttpMethod.POST;
    //回调
    private Callback callback;
    //文件
    private List<FileInput> files = new ArrayList<FileInput>();
    //文件类型
    private String fileMediaType = null;
    //请求标签
    private String tag = null;

    String[] namesAndValues=new String[]{};
    Headers headers =Headers.of(namesAndValues);

    /**
     * 请求方法
     */
    public static interface HttpMethod {
        String POST = "POST";
        String GET = "GET";
        String PUT = "PUT";
        String DELETE = "DELETE";
        String HEAD = "HEAD";
        String OPTIONS = "OPTIONS";
        String TRACE = "TRACE";
        String CONNECT = "CONNECT";
    }

    public HttpRequest() {
        init();

    }

    public HttpRequest(String url) {
        this.url = url;
        init();

    }

    public static HttpRequest setUrl(String url) {
        return new HttpRequest(url);
    }

    private void init() {
        client = ClientFactory.getClient();
    }


    public void sendByGetAsync() {
        method = HttpMethod.GET;
        sendAsync();
    }

    public Response sendByGet() {
        method = HttpMethod.GET;
        return send();
    }

    /**
     * 发送请求
     */
    public void sendAsync() {
        //网络未连接
        if (ClientFactory.getApplication() != null && !HttpUtils.isConnected()) {
            if (callback != null) {
                callback.onFailure(null, new IOException("网络未连接！"));
            }
        } else {
            buildRequest();
            if (callback != null) {
                Call call = client.newCall(request);
                call.enqueue(callback);
            }
            tempParamMap.clear();
        }
    }

    /**
     * 发送请求
     */
    public Response send() {
        Response response = null;
        //网络未连接
        if (ClientFactory.getApplication() != null && !HttpUtils.isConnected()) {
            if (callback != null) {
                callback.onFailure(null, new IOException("网络未连接！"));
            }
        } else {
            buildRequest();
            Call call = client.newCall(request);
            try {
                response = call.execute();
                if (callback != null) {
                    callback.onResponse(call, response);
                }
            } catch (IOException e) {
                if (callback != null) {
                    callback.onFailure(call, e);
                }
                e.printStackTrace();
            }
            tempParamMap.clear();
        }
        return response;
    }

    private Request buildRequest() {
        requestBuilder = new Request.Builder();
        if (!files.isEmpty()) {//post，文件输出加参数
            //这种请求体的参数是放在流里面的，无法通过request.getparam来获取
            MultipartBody.Builder multiBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            MediaType parse = null;
            if (fileMediaType != null) {
                parse = MediaType.parse(fileMediaType);
            }
            for (int i = 0; i < files.size(); i++) {
                FileInput file = files.get(i);
                multiBodyBuilder.addFormDataPart(file.key, file.filename, RequestBody.create(parse, file.file));
            }
            if (paramMap != null && paramMap.size() > 0) {
                for (String key : paramMap.keySet()) {
                    multiBodyBuilder.addFormDataPart(key, paramMap.get(key) + "");
                }
            }
            if (tempParamMap != null && tempParamMap.size() > 0) {
                for (String key : tempParamMap.keySet()) {
                    multiBodyBuilder.addFormDataPart(key, tempParamMap.get(key) + "");
                }
            }
            RequestBody requestBody = multiBodyBuilder.build();
            //地址在前面，否是会变成获取的method是get方式
            requestBuilder.url(url).post(requestBody);
        } else if (method.equalsIgnoreCase(HttpMethod.POST)) {
            //构造请求体
            //Log.i("logtag","POST===");
            FormBody.Builder formBody = new FormBody.Builder();
            if (paramMap != null && paramMap.size() > 0) {
                for (String key : paramMap.keySet()) {
                    formBody.add(key, paramMap.get(key) + "");
                }
            }
            if (tempParamMap != null && tempParamMap.size() > 0) {
                for (String key : tempParamMap.keySet()) {
                    formBody.add(key, tempParamMap.get(key) + "");
                }
            }
            RequestBody requestBody = formBody.build();
            requestBuilder.url(url).post(requestBody);
        } else if (method.equalsIgnoreCase(HttpMethod.GET)) {
            String sendUrl = url;
            //拼接地址
            if (paramMap.size() > 0) {
                int i = 0;
                for (String key : paramMap.keySet()) {
                    if (i == 0) {
                        sendUrl = sendUrl + "?" + key + "="
                                + paramMap.get(key).toString();
                        i = 1;
                    } else {
                        sendUrl = sendUrl + "&" + key + "="
                                + paramMap.get(key).toString();
                    }
                }
            }
            if (tempParamMap.size() > 0) {
                int i = 0;
                for (String key : tempParamMap.keySet()) {
                    if (i == 0) {
                        sendUrl = sendUrl + "?" + key + "="
                                + tempParamMap.get(key).toString();
                        i = 1;
                    } else {
                        sendUrl = sendUrl + "&" + key + "="
                                + tempParamMap.get(key).toString();
                    }
                }
            }
            requestBuilder.url(sendUrl);
        }
        if (tag != null) {
            requestBuilder.tag(tag);
        }
        requestBuilder.removeHeader("User-Agent").addHeader("User-Agent", ClientFactory.getUserAgent());
        requestBuilder.removeHeader("Range").addHeader("Range", "bytes=0-");
        if(headers!=null){
            requestBuilder.headers(headers);
        }

        //requestBuilder.removeHeader("Connection").addHeader("Connection", "keep-alive");

        request = requestBuilder.build();
        return request;
    }


    public HttpRequest setHeaders(Headers headers) {
       this.headers=headers;
       return this;
    }

    public HttpRequest setHeaders(Map<String, String> mapHeaders) {
        this.headers=Headers.of(mapHeaders);
        return this;
    }



    /**
     * 添加参数
     *
     * @param key
     * @param value
     */
    public void addParam(String key, Object value) {
        paramMap.put(key, value);
    }

    /**
     * 移除参数
     *
     * @param key
     */
    public void removeParam(String key) {
        paramMap.remove(key);
    }

    /**
     * 添加临时参数
     *
     * @param key
     * @param value
     */
    public void addTempParam(String key, Object value) {
        tempParamMap.put(key, value);
    }


    /**
     * 添加文件
     *
     * @param key
     * @param fileName
     * @param file
     */
    public void addFile(String key, String fileName, File file) {
        FileInput fileInput = new FileInput(key, fileName, file);
        files.add(fileInput);
    }

    /**
     * 添加文件,自动获取文件名称
     *
     * @param key
     * @param file
     */
    public void addFile(String key, File file) {
        addFile(key, file.getName(), file);
    }

    /**
     * 添加文件
     *
     * @param fileInput
     */
    public void addFile(FileInput fileInput) {
        files.add(fileInput);
    }

    /**
     * 添加文件
     *
     * @param files
     */
    public void addFiles(List<FileInput> files) {
        files.addAll(files);
    }

    /**
     * 添加参数
     *
     * @param map
     */
    public void addParam(Map<String, Object> map) {
        paramMap.putAll(map);
    }


    public String getUrl() {
        return url;
    }


    public static class FileInput {
        public String key;
        public String filename;
        public File file;

        public FileInput(String name, String filename, File file) {
            this.key = name;
            this.filename = filename;
            this.file = file;
        }

        @Override
        public String toString() {
            return "FileInput{" +
                    "key='" + key + '\'' +
                    ", filename='" + filename + '\'' +
                    ", file=" + file +
                    '}';
        }
    }

    public Map<String, Object> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String, Object> paramMap) {
        this.paramMap = paramMap;
    }

    public Request.Builder getRequestBuilder() {
        return requestBuilder;
    }

    public void setRequestBuilder(Request.Builder requestBuilder) {
        this.requestBuilder = requestBuilder;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }


    public Callback getCallback() {
        return callback;
    }

    public HttpRequest setCallback(Callback callback) {
        this.callback = callback;
        return this;
    }


    public String getFileMediaType() {
        return fileMediaType;
    }

    public void setFileMediaType(String fileMediaType) {
        this.fileMediaType = fileMediaType;
    }

    public String getMethod() {
        return method;
    }

    public HttpRequest setMethod(String method) {
        this.method = method;
        return this;
    }

    public List<FileInput> getFiles() {
        return files;
    }

    public void setFiles(List<FileInput> files) {
        this.files = files;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
