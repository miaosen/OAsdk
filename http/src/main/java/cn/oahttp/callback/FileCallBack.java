package cn.oahttp.callback;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

import cn.oahttp.ClientFactory;
import cn.oahttp.HandlerQueue;
import cn.oahttp.HttpUtils;
import okhttp3.Call;
import okhttp3.Callback;
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

    public static String saveDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + ClientFactory.getApplication().getPackageName();

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
                    fileName = HttpUtils.getFileName(response, call.request().url().toString());
                }
                //文件长度
                final long totalSize = response.body().contentLength();
                InputStream is = null;
                byte[] buf = new byte[1024];
                int len = 0;
                FileOutputStream fos = null;
                is = response.body().byteStream();
                Log.i("logtag", "byteStream======" + fileName);
                final File file = new File(saveDir, fileName);
                if(file.getParent()==null){
                Log.i("logtag", "byteStream======" + (file.length() == totalSize));
                new File(saveDir).mkdirs();
                }
                if (file.exists() && file.length() == totalSize) {
                    is.close();
                    onSuccess(file);
                } else {
                    file.createNewFile();
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                    is.close();
                    HandlerQueue.onResultCallBack(new Runnable() {
                        @Override
                        public void run() {
                    onSuccess(file);
                        }
                    });

                }
            } else {
                //HandlerQueue.onResultCallBack(new Runnable() {
                //    @Override
                //    public void run() {
                        onFail(new Exception("网络请求失败！错误代码：" + response.code() + " 信息：" + response.message()));
                //    }
                //});
            }
        } catch (SocketException e) {
            e.printStackTrace();
            //HandlerQueue.onResultCallBack(new Runnable() {
            //    @Override
            //    public void run() {
                    onFail(new Exception("下载请求被取消：tag : " + call.request().tag()));
                //}
            //});
        }


    }


    public abstract void onSuccess(File file);

    protected void onFail(Exception e) {

    }

    protected void onProgress(long totalSize, long currentSizepercent, double percent) {

    }
}
