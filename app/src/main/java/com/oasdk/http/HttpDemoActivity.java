package com.oasdk.http;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.oahttp.ClientFactory;
import com.oahttp.NetRequest;
import com.oahttp.callback.BitmapCallBack;
import com.oahttp.callback.FileCallBack;
import com.oahttp.callback.StringCallBack;
import com.oahttp.cookies.CookieManager;
import com.oasdk.R;
import com.oaui.L;
import com.oaui.annotation.InjectReader;
import com.oaui.annotation.ViewInject;

import java.io.File;
import java.text.DecimalFormat;

import okhttp3.MediaType;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2017-08-04  15:09
 * @Descrition
 */

public class HttpDemoActivity extends Activity {

    @ViewInject
    Button get, post, uploadfile, downloadfile, keepcookies,loadImg,cancleCall;

    @ViewInject
    ProgressBar progressBar;
    @ViewInject
    TextView textView;
    @ViewInject
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.http_demo_act);
        InjectReader.injectAllFields(this);
        CookieManager.clearAll(getApplication());
        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendByGet();
            }
        });
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendByPost();
            }
        });
        downloadfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFile();
            }
        });
        loadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                L.i("=========onClick=============="+Thread.currentThread());
                loadImg();
            }
        });
        keepcookies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keepcookies();
            }
        });
        uploadfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });
        cancleCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClientFactory.callRequestByTag("下载文件");
                ClientFactory.callRequestByTag("请求图片");
            }
        });
    }


    private void testContentType() {
        String url="http://192.168.43.18:8083/rcms/EagleActions/mpMpTestAction?action=showError";
        //String url="http://imgsrc.baidu.com/imgad/pic/item/267f9e2f07082838b5168c32b299a9014c08f1f9.jpg";
        NetRequest request = new NetRequest(url);
        request.setCallback(new BitmapCallBack() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                if(bitmap!=null){
                    Log.i("logtag", "text======" +  bitmap.getConfig());
                    img.setImageBitmap(bitmap);
                }
            }
        });
        MediaType.parse("application/json; charset=utf-8");
        request.setTag("请求图片");
        request.send();
    }

    private void loadImg() {
        Toast.makeText(HttpDemoActivity.this, "下载成功！ 文件名称:" , Toast.LENGTH_LONG).show();
        String url="https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502367487658&di=622ebe719ea245223dcc08edab83ef9e&imgtype=0&src=http%3A%2F%2Fimg2.niutuku.com%2Fdesk%2F1208%2F1543%2Fntk-1543-40044.jpg";
        //String url="http://imgsrc.baidu.com/imgad/pic/item/267f9e2f07082838b5168c32b299a9014c08f1f9.jpg";
        NetRequest request = new NetRequest(url);
        request.setCallback(new BitmapCallBack() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                if(bitmap!=null){
                    //Log.i("logtag", "text======" +  bitmap.getConfig());
                    //img.setImageBitmap(bitmap);
                    img.setImageDrawable(new BitmapDrawable(bitmap));
                }
            }
        });
        request.setTag("请求图片");
        request.sendByGet();
    }

    private void sendByPost() {
        NetRequest request = new NetRequest("http://120.24.64.145/hcsmstest/a/MobileService/bs/olderInfo/id");
        request.setCallback(new StringCallBack() {
            @Override
            public void onSuccess(String text) {
                Log.i("logtag", "text======" + text);
                textView.setText(text);
            }
            @Override
            protected void onFail(Exception e) {
                super.onFail(e);
                e.printStackTrace();
            }
        });
        request.addParam("id","a12412f7cb5849b7a0f11e79e3ce94f9");
        request.send();

    }

    private void sendByGet() {
        NetRequest request = new NetRequest("http://gank.io/api/data/Android/10/1");
        request.setCallback(new StringCallBack() {
            @Override
            public void onSuccess(String text) {
                Log.i("logtag", "text======" + text);
                textView.setText(text);
            }

            @Override
            protected void onFail(Exception e) {
                super.onFail(e);
                e.printStackTrace();
            }
        });
        request.sendByGet();
    }

    private void keepcookies() {
        CookieManager.clearAll(getApplication());
        NetRequest request = new NetRequest("http://120.24.64.145/hcsmstest/a/login?__ajax=true");
        request.setCallback(new StringCallBack() {
            @Override
            public void onSuccess(String text) {
                Log.i("logtag", "text======" + text);
                textView.setText(text);
            }
            @Override
            protected void onFail(Exception e) {
                super.onFail(e);
                e.printStackTrace();
            }
        });
        request.addParam("mobileLogin", "true");
        request.addParam("olderType", "N");
        request.addParam("username", "thinkgem");
        request.addParam("password", "123456");
        request.send();
    }

    public void uploadFile() {
        NetRequest request = new NetRequest("http://192.168.1.193:8083/rcms/EagleActions/mpMpUpdateErrorDataAction");
        request.setCallback(new StringCallBack() {
            @Override
            public void onSuccess(String text) {
                Log.i("logtag", "text======" + text);
            }
            @Override
            protected void onFail(Exception e) {
                super.onFail(e);
                e.printStackTrace();
            }
        });
        request.addParam("action", "getUploadFileAction");
        request.addParam("parent_id", "0");
        request.addParam("mobileLogin", "true");
        request.addParam("olderType", "N");
        request.addFile("key", "name", new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/00001.vcf"));
        request.addFile("key", new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/00001.vcf"));
        request.send();
    }

    public void downloadFile() {
        String url="http://ds.devstore.cn/dev_store/user/resource/file/201702221510243541cc5yop2/配色设计原理.pdf?c4d2ac79c1f2fe30f82558de63d8dbd4e41378415df0dfc31145a2092f4cbf83ceb0c698cec9bd9249fe4b434ebe1d84b2f6ae4cf2895bbae3dbaba80ea5e55313c0cb38ee82db0d3ed291ea85f57e05";
        //String url="http://172.17.7.54:8081/yjfile/yjjg_ydjg/zp/1499914518155.jpg";
        //String url = "http://www.scp.edu.cn/pantoschoolzz/BG/Bord/Message/DownloadMessageAttachment.aspx?ID=215";
        NetRequest request = new NetRequest(url);
        request.setCallback(new FileCallBack() {
            @Override
            public void onSuccess(File file) {
                Toast.makeText(HttpDemoActivity.this, "下载成功！ 文件名称:" + file.getName(), Toast.LENGTH_LONG).show();
                textView.setText("下载成功！ 文件名称:"+file.getName());
            }
            @Override
            protected void onProgress(long totalSize, long currentSize, double percent) {
                super.onProgress(totalSize, currentSize, percent);
                //Log.i("logtag", "onProgress======百分比： " + percent);
                if (percent == 100) {
                    Log.i("logtag", "onProgress======文件大小：" + formetFileSize(totalSize));
                }
                progressBar.setProgress((int) percent);
            }

            @Override
            protected void onFail(Exception e) {
                textView.setText("下载失败！:"+e.getMessage());
            }
        });

        request.setTag("下载文件");
        request.sendByGet();
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    public String formetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

}

