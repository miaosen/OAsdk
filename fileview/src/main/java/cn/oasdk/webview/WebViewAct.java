package cn.oasdk.webview;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.SafeBrowsingResponse;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.danikula.videocache.HttpProxyCacheServer;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import cn.oahttp.HttpRequest;
import cn.oahttp.callback.FileCallBack;
import cn.oasdk.base.AppContext;
import cn.oasdk.base.BaseAct;
import cn.oasdk.fileview.R;
import cn.oasdk.webview.view.CWebview;
import cn.oasdk.webview.view.CollectView;
import cn.oasdk.webview.view.VideoPalyView;
import cn.oaui.L;
import cn.oaui.ResourceHold;
import cn.oaui.annotation.InjectReader;
import cn.oaui.annotation.ViewInject;
import cn.oaui.data.JSONSerializer;
import cn.oaui.data.RowObject;
import cn.oaui.utils.AppUtils;
import cn.oaui.utils.FileUtils;
import cn.oaui.utils.StringUtils;
import cn.oaui.utils.ViewUtils;
import cn.oaui.view.dialog.FrameDialog;
import cn.oaui.view.listview.BaseFillAdapter;
import okhttp3.Headers;
import okhttp3.Response;


public class WebViewAct extends BaseAct {

    public final static String KEY_URl = "key_url";

    private CWebview webview;

    @ViewInject
    EditText ed;
    @ViewInject
    TextView tv_menu;
    FrameDialog fdlMenu;

    @ViewInject
    ImageView img_forword, img_back;
    Context context;

    final String homepage = "https://baidu.com";
    //final String homepage = "http://www.audio699.com/book/522/205.html";
    //final String homepage = "https://www.5tps.com/play/20904_48_1_3.html";
    //final String homepage=  "https://i.y.qq.com/n2/m/share/details/taoge.html?ADTAG=myqq&from=myqq&channel=10007100&id=7256912512";
    //final String homepage = "http://rcjgqyd.gzfda.gov.cn:8080/spjg/jsp/mobile/login.jsp";
    @ViewInject
    View dlg_line;
    @ViewInject
    LinearLayout ln_back, ln_forward, ln_down, ln_homepage;


    @ViewInject
    ImageView leftBtn;

    @ViewInject
    ProgressBar progressBar;

    @ViewInject
    CollectView collectView;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_act);
        context = this;
        InjectReader.injectAllFields(this);
        ViewUtils.finishByClick(leftBtn);
        initView();
        initPermission();
        webview.initWebview();
        initWebview();
        String url = getIntent().getStringExtra(KEY_URl);
        L.i("======onCreate===== " + url);
        if (StringUtils.isNotEmpty(url)) {
            if (!url.startsWith("http") && !url.startsWith("file://")) {
                url = "http://" + url;
            }
            ed.setText(url);
            webview.loadUrl(url);
        } else {
            //webview.loadUrl(homepage);
        }
        ed.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String s = textView.getText() + "";
                if (EditorInfo.IME_ACTION_DONE == i && StringUtils.isNotEmpty(s)) {
                    loadInputUrl();
                }
                return true;
            }
        });
        initTailView();
        collectView.listAdapter.setOnItemClickListener(new BaseFillAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View convertView, RowObject row, int position, BaseFillAdapter.ViewHolder holder) {
                if (StringUtils.isEmpty(row.getString("url"))) {
                    //Intent  intent = new Intent("cn.oasdk.webview.WebViewAct");
                    Intent intent = new Intent();
                    ComponentName componentName = new ComponentName(
                            "com.gzpykj.rcms",
                            "com.gzpykj.rcms.lpyx.LpyxBackgroundLoginActivity");
                    intent.setComponent(componentName);
                    Bundle bundle = new Bundle();
                    bundle.putCharSequence("aa", "test");
                    intent.putExtras(bundle);
                    intent.putExtra("mainId", "-------------------");
                    startActivity(intent);
                } else {
                    webview.loadUrl(row.getString("url"));
                    collectView.setVisibility(View.GONE);
                }

            }
        });
    }

    private void initWebview() {
        webview.setWebViewClient(new WebViewClient() {

            @Override
            public void onSafeBrowsingHit(WebView view, WebResourceRequest request, int threatType, SafeBrowsingResponse callback) {
                super.onSafeBrowsingHit(view, request, threatType, callback);
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                final String url = request.getUrl().toString();
                L.i("============shouldInterceptRequest===========" + request.getUrl());
                //WebResourceResponse webResourceResponse1 = dealRes(view, request);
                //if (webResourceResponse1 != null) {
                //    return webResourceResponse1;
                //} else {
                //final Map<String, String> requestHeaders = request.getRequestHeaders();
                //request=  new WebResourceRequest() {
                //    @Override
                //    public Uri getUrl() {
                //        HttpProxyCacheServer proxy = AppContext.getProxy();
                //        String proxyUrl = proxy.getProxyUrl(url);
                //        return Uri.parse(proxyUrl);
                //    }
                //
                //    @SuppressLint("NewApi")
                //    @Override
                //    public boolean isForMainFrame() {
                //        return true;
                //    }
                //
                //    @Override
                //    public boolean isRedirect() {
                //        return false;
                //    }
                //
                //    @SuppressLint("NewApi")
                //    @Override
                //    public boolean hasGesture() {
                //        return true;
                //    }
                //
                //    @SuppressLint("NewApi")
                //    @Override
                //    public String getMethod() {
                //        return "GET";
                //    }
                //
                //    @SuppressLint("NewApi")
                //    @Override
                //    public Map<String, String> getRequestHeaders() {
                //        return requestHeaders;
                //    }
                //};
                return super.shouldInterceptRequest(view, request);
                //}
            }


            // 在加载页面资源时会调用，每一个资源（比如图片）的加载都会调用一次。
            @Override
            public void onLoadResource(WebView view, String url) {
                //HttpProxyCacheServer proxy = AppContext.getProxy();
                //String proxyUrl = proxy.getProxyUrl(url);
                //view.loadUrl(url);

                L.i("============onLoadResource===========" + view.getUrl());
                super.onLoadResource(view, url);
                //return;
            }

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                super.onPageCommitVisible(view, "aa");
            }

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                HttpProxyCacheServer proxy = AppContext.getProxy();
                String proxyUrl = proxy.getProxyUrl(url);
                L.i("============shouldInterceptRequest===========" + proxyUrl);
                if (proxy.isCached(url)) {
                    L.i("============generate===========已缓存");
                } else {
                    L.i("============generate===========未缓存");
                }
                return super.shouldInterceptRequest(view, proxyUrl);
            }


            // 在点击请求的是链接时才会调用，不跳到浏览器那边。
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http") || url.startsWith("https")) {
                    L.i("============shouldOverrideUrlLoading 2===========" + url);
                    return super.shouldOverrideUrlLoading(view, url);
                } else {
                    return true;
                }
            }

            // 在页面加载开始时调用。
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                ed.setText(url);
                L.i("============onPageStarted===========");
                //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                //    webview.evaluateJavascript("javaScript:function getAdPosition() { var postions=''; var audio = document.getElementsByTagName('audio'); if(audio.length>0){ for (var i = 0; i < audio.length; i++) { postions=postions+','+audio[i] } } var  iframe= document.getElementsByTagName('iframe'); console.log(\"=========\"+ iframe) if(iframe.length>0){ var audio2= iframe[0].contentWindow.document.getElementsByTagName('audio'); if(audio2!='undefined'){ for (var i = 0; i < audio2.length; i++) { postions=postions+','+audio2[i] } } }  return postions; }; window.onload = function(){ getAdPosition() }",
                //            new ValueCallback<String>() {
                //                @Override
                //                public void onReceiveValue(String value) {
                //                    L.i("============onReceiveValue==========="+value);
                //                    if(StringUtils.isNotEmpty(value)&&!"null".equals(value)){
                //                        int TV_HEIGHT=100;
                //                        final TextView textView = getTextView(TV_HEIGHT);
                //                        textView.setTranslationY(AppUtils.dp2px(Float.parseFloat(value)));
                //                        webview.addView(textView);
                //                        webview.loadUrl("javaScript: function setAdHeight(height) { var TV_HEIGHT=100; var audio = document.getElementsByTagName('audio'); audio[0].style.marginTop=height+\"px\"; };setAdHeight(200) ");
                //
                //                    }
                //                }
                //            });
                //}
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onPageFinished(WebView view, String url) {
                L.i("======onPageFinished===== " + url);
                logicTail();
                super.onPageFinished(view, url);
                String aa="{\"postions\":\"296\",\"src\":\"http://q2.audio699.com/asdasdasd/522/205/1595239200/4db853141c650c52f86e5673f7848300/18a315c150037ca3bae35d8044c2978f.m4a\"}";

                String js = "javascript: function getMediaInfo() { var postions=''; var src=''; var audio = document.getElementsByTagName('audio');  if(audio.length>0){ for (var i = 0; i < audio.length; i++) { var url=audio[i].getElementsByTagName('source')[0].src; if(postions.length>0){ postions=postions+','+audio[i].offsetTop; src=src+','+url; }else{ postions=audio[i].offsetTop; src=url; } } } var  iframe= document.getElementsByTagName('iframe');  if(iframe.length>0){ var audio2= iframe[0].contentWindow.document.getElementsByTagName('audio'); if(audio2.length>0){ for (var i = 0; i < audio2.length; i++) { var url=audio[i].getElementsByTagName('source')[0].src; if(postions.length>0){ postions=postions+','+audio2[i].offsetTop; src=src+','+url; }else{ postions=audio2[i].offsetTop; src=url; } } } } var json='{\"postions\":\"'+postions+'\",\"src\":\"'+src+'\"}'; return json; } getMediaInfo();";
                webview.evaluateJavascript("javaScript: " + js, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {

                        value=value.replace("\\","");
                        value=value.replaceFirst("\"","");
                        value=value.substring(0,value.length()-1);
                        String aa="aaaaaaaaaaaaaaaaaaaaa";
                        L.i("============onReceiveValue==========="+aa);
                        L.i("============onReceiveValue===========" + value);
                        RowObject row = JSONSerializer.JSONToObject(value,RowObject.class);
                        String postions = row.getString("postions");
                        String src = row.getString("src");
                        HttpProxyCacheServer proxy = AppContext.getProxy();
                        String proxyUrl = proxy.getProxyUrl(src);
                        if (StringUtils.isNotEmpty(postions) && !"null".equals(postions)) {
                            int TV_HEIGHT = 100;
                            final VideoPalyView videoPalyView = getVideoPalyView(TV_HEIGHT);
                            videoPalyView.setTranslationY(AppUtils.dp2px(Float.parseFloat(postions)));
                            Uri uri = Uri.parse(proxyUrl);
                            videoPalyView.prepare(uri);
                            webview.addView(videoPalyView);
                            webview.loadUrl("javaScript: function setAdHeight(height) { var TV_HEIGHT=100; var audio = document.getElementsByTagName('audio'); audio[0].style.marginTop=height+\"px\"; };setAdHeight(200) ");
                        }
                    }
                });
            }

            // 重写此方法才能够处理在浏览器中的按键事件。
            @Override
            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                return super.shouldOverrideKeyEvent(view, event);
            }
        });

        webview.setWebChromeClient(new WebChromeClient() {


            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);

                }
                progressBar.setProgress(newProgress);

            }
        });
    }


    @NonNull
    private TextView getTextView(int TV_HEIGHT) {
        final TextView textView = new TextView(getApplication());
        textView.setTextColor(Color.GRAY);
        textView.setTextSize(20f);
        textView.setBackgroundColor(Color.YELLOW);
        textView.setText("WebActivity TextView ");
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, AppUtils.dp2px(TV_HEIGHT)));
        return textView;
    }



    @NonNull
    private VideoPalyView getVideoPalyView(int TV_HEIGHT) {
      VideoPalyView videoPalyView = new VideoPalyView(getApplication());
        videoPalyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, AppUtils.dp2px(TV_HEIGHT)));
        return videoPalyView;
    }

    private void initPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            String[] PERMISSIONS_CAMERA_AND_STORAGE = {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_CAMERA_AND_STORAGE,
                        11);
            }
        }
    }

    private void initTailView() {
        ln_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (webview.canGoBack()) {
                    back();
                }
            }
        });
        ln_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (webview.canGoForward()) {
                    forword();
                }
            }
        });
        //ln_down.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //    }
        //});
        ln_homepage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //goHomePage();
                collectView.setVisibility(View.VISIBLE);
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void logicTail() {
        Drawable drawable = ResourceHold.getDrawable(R.mipmap.icon_left);
        if (webview.canGoBack()) {
            ViewUtils.tintDrawable(drawable, ResourceHold.getColor(R.color.black));
        } else {
            ViewUtils.tintDrawable(drawable, ResourceHold.getColor(R.color.grey_lt));
        }
        img_back.setBackground(drawable);
        Drawable drawable2 = ResourceHold.getDrawable(R.mipmap.icon_right);
        if (webview.canGoForward()) {
            ViewUtils.tintDrawable(drawable2, ResourceHold.getColor(R.color.black));
        } else {
            ViewUtils.tintDrawable(drawable2, ResourceHold.getColor(R.color.grey_lt));
        }
        img_forword.setBackground(drawable2);
    }

    private void loadInputUrl() {
        String url = ed.getText() + "";
        if (!url.startsWith("http")) {
            url = "http://" + url;
        }
        webview.loadUrl(url);
    }


    private void initView() {
        webview = findViewById(R.id.webview);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private WebResourceResponse dealRes(WebView view, WebResourceRequest webResourceRequest) {
        //同步执行会话请求
        final String url = webResourceRequest.getUrl().toString();
        L.i("============dealRes===========" + url);
        HttpRequest httpRequest = HttpRequest.setUrl(url);
        Map<String, String> requestHeaders = webResourceRequest.getRequestHeaders();
        if (requestHeaders != null) {
            httpRequest.setHeaders(requestHeaders);
        }
        String method = webResourceRequest.getMethod();
        if (StringUtils.isNotEmpty(method)) {
            httpRequest.setMethod(method);
        } else {
            httpRequest.setMethod(HttpRequest.HttpMethod.GET);
        }
        final Response response = httpRequest.send();
        if (response != null) {
            Headers headers = response.headers();
            Map<String, String> responseHeaders = new HashMap<>();
            final long totalSize = response.body().contentLength();
            for (int i = 0; i < headers.size(); i++) {
                responseHeaders.put(headers.name(i), headers.value(i));
            }
            String contentType = headers.get("Content-Type") + "";
            L.i("============dealRes===========" + contentType);
            if (contentType.contains("audio")
                    || (contentType.contains("image"))
                    || (contentType.contains("video") && totalSize > 50 * 1024)) {
                String[] type = contentType.split("/");

                String s = FileCallBack.saveDir + "/" + FileCallBack.getFileName(response, url);
                if (contentType.contains("image") && type.length > 1 && !s.endsWith(type[1])) {
                    s = s + "." + type[1];
                }
                final File file = new File(s);
                InputStream fileInputStream = null;
                try {
                    File file1 = new File(FileCallBack.saveDir);
                    if (!file1.exists()) {
                        boolean mkdirs = file1.mkdirs();
                    }
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (file.exists() && file.length() == totalSize) {
                    L.i("============dealRes===========" + url);
                    try {
                        fileInputStream = new FileInputStream(file);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    return new WebResourceResponse(response.header("Content-Type"), "UTF-8", fileInputStream);
                } else {
                    try {

                        final PipedOutputStream out = new PipedOutputStream();
                        final PipedInputStream[] pipedInputStream = {new PipedInputStream(out)};
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    InputStream is = response.body().byteStream();
                                    FileOutputStream fos = new FileOutputStream(file);
                                    byte[] b = new byte[1024];
                                    int n;
                                    while ((n = is.read(b)) != -1) {
                                        out.write(b, 0, n);
                                        fos.write(b, 0, n);
                                    }
                                    out.close();
                                    fos.close();
                                    is.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                        //FileInputStream fileInputStream = null;
                        //try {
                        //    file.createNewFile();
                        //    fileInputStream = new FileInputStream(file);
                        //} catch (FileNotFoundException e) {
                        //    e.printStackTrace();
                        //} catch (IOException e) {
                        //    e.printStackTrace();
                        //}
                        //webview.post(new Runnable() {
                        //    @Override
                        //    public void run() {
                        //        HttpRequest request = new HttpRequest(url);
                        //        request.setCallback(new FileCallBack() {
                        //            @Override
                        //            public void onSuccess(File file) {
                        //                L.i("============onSuccess===========" + file.getAbsolutePath());
                        //                try {
                        //                    FileInputStream fis = new FileInputStream(file);
                        //                    byte[] b = new byte[1024];
                        //                    int n;
                        //                    while ((n = fis.read(b)) != -1) {
                        //                        out.write(b, 0, n);
                        //                    }
                        //
                        //                    //out.write(fileToBytes(file));
                        //                    out.close();
                        //
                        //
                        //                } catch (IOException e) {
                        //                    e.printStackTrace();
                        //                } catch (Exception e) {
                        //                    e.printStackTrace();
                        //                }
                        //            }
                        //
                        //            @Override
                        //            protected void onProgress(long totalSize, long currentSize, double percent) {
                        //                super.onProgress(totalSize, currentSize, percent);
                        //            }
                        //
                        //            @Override
                        //            protected void onFail(Exception e) {
                        //            }
                        //        });
                        //        request.sendByGetAsync();
                        //    }
                        //});

                        //L.i("============contains===========" + responseHeaders);
                        //L.i("============contains===========" + response.code());
                        //WebResourceResponse xresponse = new WebResourceResponse(response.header("Content-Type"), "UTF-8", pipedInputStream);
                        //Map<String, String> h = new HashMap<>();
                        //h.put("Access-Control-Allow-Origin", "*");
                        //xresponse.setResponseHeaders(h);

                        //return xresponse;
                        return new WebResourceResponse(response.header("Content-Type"), "UTF-8", pipedInputStream[0]);
                        //return new WebResourceResponse(response.header("Content-Type"), "UTF-8",response.code(),"aa",responseHeaders ,fileInputStream);
                        //} catch (IOException e) {
                        //    e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        return null;
    }

    public static byte[] fileToBytes(File tradeFile) {
        byte[] buffer = null;
        try {
            FileInputStream fis = new FileInputStream(tradeFile);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            //fis.close();
            //bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }


    private int getNumberByType(String header) {
        if (header.contains("image")) {
            return 1;
        } else if (header.contains("video")) {
            return 2;
        } else {
            return -1;
        }

    }

    public static String inputStream2String(InputStream in_st) {
        BufferedReader in = new BufferedReader(new InputStreamReader(in_st));
        StringBuffer buffer = new StringBuffer();
        String line = "";
        try {
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return buffer.toString();
    }


    private String getFileTypeByNum(int num) {
        if (num == 1) {
            return "image";
        } else if (num == 2) {
            return "video";
        } else {
            return "";
        }
    }


    @Override
    public void onBackPressed() {
        if (webview.canGoBack()) {
            back();
        } else if (collectView.getVisibility() == View.GONE) {
            collectView.setVisibility(View.VISIBLE);
            ed.setText("");
        } else {
            super.onBackPressed();
        }

    }

    private void back() {
        String url = webview.getUrl();
        ed.setText(url);
        webview.goBack();
    }

    private void forword() {
        String url = webview.getUrl();
        ed.setText(url);
        webview.goForward();
    }


    private void goHomePage() {
        ed.setText(homepage);
        webview.loadUrl(homepage);
    }


}
