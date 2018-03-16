package com.oahttp.callback;

import android.os.Environment;
import android.util.Log;

import com.oahttp.HandlerQueue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.URLDecoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @git http://git.oschina.net/miaosen/MyUtils
 * @CreateDate 2017-08-08  16:16
 * @Descrition
 */

public abstract class FileCallBack implements Callback {

    String fileName = null;

    String saveDir = Environment.getExternalStorageDirectory().getAbsolutePath();

    public FileCallBack() {
    }

    public FileCallBack(String saveDir) {
        this.saveDir = saveDir;
    }


    public FileCallBack(String saveDir, String fileName) {
        this.saveDir = saveDir;
        this.fileName = fileName;
    }

    @Override
    public void onFailure(final Call call, final IOException e) {
        Log.i("logtag", "onProgress======onFailure");
        HandlerQueue.onResultCallBack(new Runnable() {
            @Override
            public void run() {
                onFail(new Exception(e));
            }
        });
    }

    @Override
    public void onResponse(final Call call, final Response response) throws IOException {
        try {
            if (response.isSuccessful()) {
                //获取文件名称
                if (fileName == null) {
                    fileName = getFileName(response, call.request().url());
                }
                //文件长度
                final long totalSize = response.body().contentLength();
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                is = response.body().byteStream();
                Log.i("logtag", "byteStream======" + fileName);
                final File file = new File(saveDir, fileName);
                fos = new FileOutputStream(file);
                long sum = 0;
                while ((len = is.read(buf)) != -1) {
                    sum += len;
                    fos.write(buf, 0, len);
                    final long currentSize = sum;
                    HandlerQueue.onResultCallBack(new Runnable() {
                        @Override
                        public void run() {
                            double percent = (currentSize * 1.0) / (1.0 * totalSize) * 100;
                            onProgress(totalSize, currentSize, percent);
                        }
                    });
                }
                fos.flush();
                is.close();
                HandlerQueue.onResultCallBack(new Runnable() {
                    @Override
                    public void run() {
                        onSuccess(file);
                    }
                });
            } else {
                HandlerQueue.onResultCallBack(new Runnable() {
                    @Override
                    public void run() {
                        onFail(new Exception("网络请求失败！错误代码：" + response.code() + " 信息：" + response.message()));
                    }
                });
            }
        } catch (SocketException e) {
            e.printStackTrace();
            HandlerQueue.onResultCallBack(new Runnable() {
                @Override
                public void run() {
                    onFail(new Exception("下载请求被取消：tag : " + call.request().tag()));
                }
            });
        }


    }

    /**
     * 从头部获取文件名称信息，没有则判断url中有没有包含文件名称，没有取系统时间作为名称
     *
     * @param response
     * @param url
     * @return
     */
    public String getFileName(Response response, HttpUrl url) {
        fileName = System.currentTimeMillis() + "";
        String cd = response.header("Content-Disposition");
        if (cd != null) {
            String[] split = cd.split(";");
            for (int i = 0; i < split.length; i++) {
                String str = split[i];
                if (str.indexOf("filename=") > -1) {
                    fileName = str.replace("filename=", "");
                    fileName = decodeForResponse(response, fileName);
                }
            }
        } else if (setNameIfInUrl(url) != null) {
            fileName = setNameIfInUrl(url);
            fileName = decodeForResponse(response, fileName);
        }
        return fileName;
    }


    /**
     * 从网页获取编码，然后转换
     *
     * @param response
     * @param url
     * @return
     */
    public String decodeForResponse(Response response, String url) {
        String encode = "UTF-8";
        if (response.body().contentType().charset() != null) {
            //检查头部contentType有没有编码信息，没有则默认为UTF-8
            encode = response.body().contentType().charset().name();
        }
        try {
            url = URLDecoder.decode(url, encode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * 从URL中获取文件名，从左到右找到最后一个“/”字符，再从这个字符开始找到第一个“?”字符，
     * 如果没有这个字符则整串为文件名；如果有则取“/”与“?”之间的字符串,如果字符串包含“.”，
     * 就返回该字符串，没有则返回null
     *
     * @param url
     */
    private String setNameIfInUrl(HttpUrl url) {
        String s = url.toString();
        s = s.substring(s.lastIndexOf("/") + 1, s.length());
        if (s.contains("?")) {
            s = s.substring(0, s.lastIndexOf("?"));
        }
        if (s.contains(".")) {
            return s;
        }
        return null;
    }

    ;

    public abstract void onSuccess(File file);

    protected void onFail(Exception e) {

    }

    protected void onProgress(long totalSize, long currentSizepercent, double percent) {

    }
}
