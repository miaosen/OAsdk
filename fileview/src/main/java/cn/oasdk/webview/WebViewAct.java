package cn.oasdk.webview;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.webkit.ConsoleMessage;
import android.webkit.SafeBrowsingResponse;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import cn.oahttp.HandlerQueue;
import cn.oahttp.HttpRequest;
import cn.oasdk.base.AppContext;
import cn.oasdk.base.BaseAct;
import cn.oasdk.fileview.R;
import cn.oasdk.webview.view.BookMarkView;
import cn.oasdk.webview.view.CWebview;
import cn.oasdk.webview.view.SearchMenuView;
import cn.oasdk.webview.view.VideoPalyView;
import cn.oasdk.webview.view.WebMuneView;
import cn.oaui.L;
import cn.oaui.ResourceHold;
import cn.oaui.annotation.InjectReader;
import cn.oaui.annotation.ViewInject;
import cn.oaui.data.Row;
import cn.oaui.utils.AppUtils;
import cn.oaui.utils.DateTimeUtils;
import cn.oaui.utils.SPUtils;
import cn.oaui.utils.StringUtils;
import cn.oaui.utils.URIUtils;
import cn.oaui.utils.ViewUtils;
import cn.oaui.view.ClearableEditText;
import cn.oaui.view.dialog.FrameDialog;
import cn.oaui.view.listview.BaseFillAdapter;
import okhttp3.Headers;
import okhttp3.Response;


public class WebViewAct extends BaseAct {

    public final static String KEY_URl = "key_url";

    private CWebview webview;

    @ViewInject
    ClearableEditText ed;
    @ViewInject
    LinearLayout ln_search_check, ln_go;
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
    LinearLayout ln_back, ln_forward, ln_menu, ln_down, ln_homepage, ln_edit_tail;

    @ViewInject
    ProgressBar progressBar;

    @ViewInject
    BookMarkView collectView;

    String FILE_CACHE_URL = "file_cache_url";

    String KEY_CACHE_URL = "key_cache_url";

    @ViewInject
    VideoPalyView videoPalyView;


    FrameDialog menuDialog;


    FrameDialog searchMenuDialog;
    SearchMenuView searchMenuView;

    List<Row> rowsHistory;


    String title, url;
    List<String> listStep = new LinkedList<>();

    Row rowSearchParam;
    public static final String KEY_SEARCH="key_search";
    public static final String FILE_SEARCH="key_search";
    @ViewInject
    ImageView img_search_logo;


    @ViewInject
    com.google.android.material.appbar.AppBarLayout appBarLayout;

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_act);
        context = this;
        rowsHistory = SPUtils.getRows(WebHistoryAct.FILE_HISTORY, WebHistoryAct.KEY_HISTORY);
        if (rowsHistory == null) {
            rowsHistory = new LinkedList<>();
        }
        InjectReader.injectAllFields(this);
        initView();
        initPermission();
        webview.initWebview();
        initWebview();
        url = getIntent().getStringExtra(KEY_URl);
        L.i("======onCreate===== " + url);
        if (StringUtils.isNotEmpty(url)) {
            if (!url.startsWith("http") && !url.startsWith("file://")) {
                url = "http://" + url;
            }
            ed.setText(url);
            loadUrl(url);
        } else {
            url = SPUtils.getText(FILE_CACHE_URL, KEY_CACHE_URL);
            if (StringUtils.isNotEmpty(url)) {
                loadUrl(url);
            } else {
                loadUrl(homepage);
            }

        }
        ed.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String s = textView.getText() + "";
                if (EditorInfo.IME_ACTION_DONE == i && StringUtils.isNotEmpty(s)) {
                    loadUrlOrSearch();

                }
                return true;
            }
        });
        ed.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                L.i("============onFocusChange==========="+hasFocus);
                if(hasFocus){
                    ed.setText(url);
                }else{
                    if(StringUtils.isNotEmpty(title)){
                        ed.setText(title);
                    }
                }
            }
        });
        ln_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadUrlOrSearch();
            }
        });
        initTailView();
        collectView.listAdapter.setOnItemClickListener(new BaseFillAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View convertView, Row row, int position, BaseFillAdapter.ViewHolder holder) {
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
                    loadUrl(row.getString("url"));
                    collectView.setVisibility(View.GONE);
                }
            }
        });
        ln_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menuDialog == null) {
                    WebMuneView webMuneView = new WebMuneView(context);
                    menuDialog = new FrameDialog(webMuneView);
                    View ln_hide_menu = menuDialog.findViewById(R.id.ln_hide_menu);
                    ln_hide_menu.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            menuDialog.dismiss();
                        }
                    });
                    webMuneView.getListAdapter().setOnItemClickListener(new BaseFillAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View convertView, Row row, int position, BaseFillAdapter.ViewHolder holder) {
                            String name = row.getString("name");
                            if ("历史".equals(name)) {
                                Intent in = new Intent(context, WebHistoryAct.class);
                                startActivityForResult(in, WebHistoryAct.CODE_HISTORY);
                                menuDialog.dismiss();
                            } else if ("书签".equals(name)) {
                                Intent in = new Intent(context, WebCollectAct.class);
                                startActivityForResult(in, WebCollectAct.CODE_COLLECTION);
                                menuDialog.dismiss();
                            } else if ("添加书签".equals(name)) {
                                Intent in = new Intent(context, WebAddCollectAct.class);
                                in.putExtra("name", StringUtils.isEmpty(title) ? url : title);
                                in.putExtra("url", url);
                                startActivity(in);
                                menuDialog.dismiss();
                            } else if ("刷新".equals(name)) {
                                webview.reload();
                                menuDialog.dismiss();
                            } else if ("退出".equals(name)) {
                                finish();
                            } else {

                            }
                        }
                    });
                }
                menuDialog.showAsCoverUp(ln_edit_tail);
            }
        });
        ln_search_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchMenuDialog == null) {
                    searchMenuView = new SearchMenuView(context);
                    searchMenuDialog = new FrameDialog(searchMenuView);
                }
                searchMenuView.listAdapter.setOnItemClickListener(new BaseFillAdapter.OnItemClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onItemClick(View convertView, Row row, int position, BaseFillAdapter.ViewHolder holder) {
                        rowSearchParam=row;
                        SPUtils.saveRow(FILE_SEARCH,KEY_SEARCH,rowSearchParam);
                        Integer img = row.getInteger("img");
                        img_search_logo.setImageDrawable(getDrawable(img));
                        searchMenuDialog.dismiss();
                    }
                });
                searchMenuDialog.showAsDown(progressBar);

            }
        });
        rowSearchParam=SPUtils.getRow(FILE_SEARCH,KEY_SEARCH);
        if(rowSearchParam==null){
            rowSearchParam=SearchMenuView.getParamRow(0);
            SPUtils.saveRow(FILE_SEARCH,KEY_SEARCH,rowSearchParam);
        }
        Integer img = rowSearchParam.getInteger("img");
        img_search_logo.setImageDrawable(getDrawable(img));
    }

    private void loadUrl(String url) {
        webview.loadUrl(url);
        listStep.add(url);
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
                L.i("============shouldInterceptRequest===========" + request.getUrl());
                WebResourceResponse webResourceResponse1 = dealRes(view, request);
                if (webResourceResponse1 != null) {
                    return webResourceResponse1;
                } else {
                    return super.shouldInterceptRequest(view, request);
                }
            }

            // 在加载页面资源时会调用，每一个资源（比如图片）的加载都会调用一次。
            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }

            // 在点击请求的是链接时才会调用，不跳到浏览器那边。
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http") || url.startsWith("https")) {
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
                hideMediaView();

            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(StringUtils.isEmpty(title)){
                    ed.setText(url);
                }
                WebViewAct.this.url = url;
                L.i("======onPageFinished===== " + url);
                logicTail();
                SPUtils.saveText(FILE_CACHE_URL, KEY_CACHE_URL, url);
                //CoordinatorLayout.Behavior behavior =
                //        ((CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams()).getBehavior();
                //if (behavior instanceof AppBarLayout.Behavior) {
                //    AppBarLayout.Behavior appBarLayoutBehavior = (AppBarLayout.Behavior) behavior;
                //    int topAndBottomOffset = appBarLayoutBehavior.getTopAndBottomOffset();
                //    if (topAndBottomOffset != 0) {
                //        appBarLayoutBehavior.setTopAndBottomOffset(0);
                //    }
                //}

                //InputStream inputStream = FileUtils.readFromAssets("audio.js");
                //String s = FileUtils.toString(inputStream);
                //s = StringUtils.removeUnnecessarySpace(s);
                //L.i("============onPageFinished==========="+s);
                //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                //    webview.evaluateJavascript("javaScript:" + s, new ValueCallback<String>() {
                //        @Override
                //        public void onReceiveValue(String value) {
                //            value = value.replace("\\", "");
                //            value = value.replaceFirst("\"", "");
                //            value = value.substring(0, value.length() - 1);
                //            if (JsonUtils.isJSONObject(value)) {
                //                Row row = JsonUtils.jsonToRow(value);
                //                String postions = row.getString("postions");
                //                String src = row.getString("src");
                //
                //                if (StringUtils.isNotEmpty(postions) && !"null".equals(postions)) {
                //
                //                   playMedia(src,postions);
                //                        webview.loadUrl("javaScript: function setAdHeight(height) { var TV_HEIGHT=100; var audio = document.getElementsByTagName('audio'); audio[0].style.marginTop=height+\"px\"; };setAdHeight(200) ");
                //                }
                //            }
                //        }
                //    });
                //}
            }

            // 重写此方法才能够处理在浏览器中的按键事件。
            @Override
            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                return super.shouldOverrideKeyEvent(view, event);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
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

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                //L.i("============onConsoleMessage===========" + consoleMessage.messageLevel() + ":" + consoleMessage.message());
                return super.onConsoleMessage(consoleMessage);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                WebViewAct.this.url = url;
                WebViewAct.this.title = title;
                if(StringUtils.isNotEmpty(title)){
                    ed.setText(title);
                }
                saveHistory();
                SPUtils.saveText(FILE_CACHE_URL, KEY_CACHE_URL, url);
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
                L.i("============onReceivedIcon===========" + icon);
                //BitmapUtils.saveBitmapToPathAsPng(icon,AppUtils.getDefaultDirectory()+"/"+view.getUrl().replaceAll("/","")+".png");
            }
        });
    }

    private void hideMediaView() {
        if (videoPalyView.player.isPlayingAd()) {
            videoPalyView.stop();
        }
        videoPalyView.setVisibility(View.GONE);
    }

    private void playMedia(String url, String positionY) {
        int TV_HEIGHT = 100;
        //videoPalyView = getVideoPalyView(TV_HEIGHT);
        //videoPalyView.setTranslationY(AppUtils.dp2px(Float.parseFloat(positionY)));
        videoPalyView.setVisibility(View.VISIBLE);
        HttpProxyCacheServer proxy = AppContext.getProxy();
        String proxyUrl = proxy.getProxyUrl(url);
        Uri uri = Uri.parse(proxyUrl);
        videoPalyView.stop();
        videoPalyView.prepare(uri);
        //if(videoPalyView.getParent()==null){
        //    webview.addView(videoPalyView);
        //}


    }

    private void saveHistory() {
        Row row = new Row();
        row.put("url", url);
        if (StringUtils.isEmpty(title)) {
            title = URIUtils.getIP(url);
        }
        row.put("title", title);
        row.put("datetime", DateTimeUtils.getCurrentTime());
        rowsHistory.add(0, row);
        //L.i("============saveHistory==========="+rowsHistory);
        SPUtils.saveRows(WebHistoryAct.FILE_HISTORY, WebHistoryAct.KEY_HISTORY, rowsHistory);
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
    private VideoPalyView getVideoPalyView(int videoHeight) {
        if (videoPalyView == null) {
            videoPalyView = new VideoPalyView(getApplication());
            videoPalyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, AppUtils.dp2px(videoHeight)));
        }
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
                if (collectView.getVisibility() == View.VISIBLE) {
                    collectView.setVisibility(View.GONE);
                } else if (webview.canGoBack()) {
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

                hideMediaView();
                collectView.setVisibility(View.VISIBLE);
                logicTail();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void logicTail() {
        if (collectView.getVisibility() == View.VISIBLE) {
            Drawable drawable = ResourceHold.getDrawable(R.mipmap.icon_left);
            ViewUtils.tintDrawable(drawable, ResourceHold.getColor(R.color.black));
            img_back.setBackground(drawable);
            Drawable drawable2 = ResourceHold.getDrawable(R.mipmap.icon_right);
            ViewUtils.tintDrawable(drawable2, ResourceHold.getColor(R.color.grey_lt));
            img_forword.setBackground(drawable2);
        } else {
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

    }

    private void loadUrlOrSearch() {
        String text = ed.getText() + "";
        if(URLUtil.isNetworkUrl(text)){
            if (!text.startsWith("http")) {
                url = "http://" + text;
            }
            loadUrl(url);
        }else{
            searchByParam(text);

        }
        ViewUtils.hideKeyboard(ed);
    }

    private void searchByParam(String text) {
        ed.setText(text);
        url=SearchMenuView.buildUrl(rowSearchParam,text);
        webview.loadUrl(url);
    }


    private void initView() {
        webview = findViewById(R.id.webview);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private WebResourceResponse dealRes(WebView view, WebResourceRequest webResourceRequest) {
        //同步执行会话请求
        final String url = webResourceRequest.getUrl().toString();
        if (StringUtils.isNotEmpty(url)) {
            HttpRequest httpRequest = HttpRequest.setUrl(url);
            Map<String, String> requestHeaders = webResourceRequest.getRequestHeaders();
            if (requestHeaders != null) {
                httpRequest.setHeaders(requestHeaders);
            }
            String method = webResourceRequest.getMethod();
            if (StringUtils.isNotEmpty(method)) {
                httpRequest.setMethod(method);
            }
            final Response response = httpRequest.send();
            if (response != null) {
                Headers headers = response.headers();
                Map<String, String> responseHeaders = new HashMap<>();
                final long totalSize = response.body().contentLength();
                for (int i = 0; i < headers.size(); i++) {
                    responseHeaders.put(headers.name(i), headers.value(i));
                }
                String mimeType = "";
                String contentType = headers.get("Content-Type") + "";
                String s1 = headers.get("content-type");
                if (StringUtils.isEmpty(contentType) && StringUtils.isNotEmpty(s1)) {
                    contentType = s1 + "";
                }
                if (StringUtils.isEmpty(contentType)) {
                    contentType = "text/html";
                }

                if (contentType.contains(";")) {
                    mimeType = mimeType.split(";")[0];
                } else {
                    mimeType = contentType;
                }
                if (mimeType.contains("audio")
                        || mimeType.contains("video")) {
                    L.i("============dealRes===========" + mimeType + "  " + url);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HandlerQueue.onResultCallBack(new Runnable() {
                                @Override
                                public void run() {
                                    playMedia(url, 50 + "");
                                }
                            });
                        }
                    }).start();
                    return new WebResourceResponse(mimeType, "UTF-8", 206, "aa", responseHeaders, new InputStream() {
                        @Override
                        public int read() throws IOException {
                            return 0;
                        }
                    });
                } else if (mimeType.contains("image")) {
                    //String[] type = mimeType.split("/");
                    //String s = FileCallBack.saveDir + "/" + HttpUtils.getFileName(response, url);
                    //if (mimeType.contains("image") && type.length > 1 && !s.endsWith(type[1])) {
                    //    s = s + "." + type[1];
                    //}
                    //final File file = new File(s);
                    //InputStream fileInputStream = null;
                    //try {
                    //    File file1 = new File(FileCallBack.saveDir);
                    //    if (!file1.exists()) {
                    //        boolean mkdirs = file1.mkdirs();
                    //    }
                    //    if (!file.exists()) {
                    //        file.createNewFile();
                    //    }
                    //} catch (IOException e) {
                    //    e.printStackTrace();
                    //}
                    //if (file.exists() && file.length() == totalSize) {
                    //    L.i("============dealRes===========" + url);
                    //    try {
                    //        fileInputStream = new FileInputStream(file);
                    //    } catch (FileNotFoundException e) {
                    //        e.printStackTrace();
                    //    }
                    //    return new WebResourceResponse(mimeType, "UTF-8", fileInputStream);
                    //} else {
                    //    try {
                    //        final PipedOutputStream out = new PipedOutputStream();
                    //        final PipedInputStream[] pipedInputStream = {new PipedInputStream(out)};
                    //        new Thread(new Runnable() {
                    //            @Override
                    //            public void run() {
                    //                try {
                    //                    InputStream is = response.body().byteStream();
                    //                    FileOutputStream fos = new FileOutputStream(file);
                    //                    byte[] b = new byte[1024];
                    //                    int n;
                    //                    while ((n = is.read(b)) != -1) {
                    //                        out.write(b, 0, n);
                    //                        fos.write(b, 0, n);
                    //                    }
                    //                    out.close();
                    //                    fos.close();
                    //                    is.close();
                    //                } catch (IOException e) {
                    //                    e.printStackTrace();
                    //                } catch (Exception e) {
                    //                    e.printStackTrace();
                    //                }
                    //            }
                    //        }).start();
                    //        L.i("============dealRes==========="+mimeType);
                    //        return new WebResourceResponse(mimeType, "UTF-8", pipedInputStream[0]);
                    //    } catch (Exception e) {
                    //        e.printStackTrace();
                    //    }
                    //
                    //}
                } else {

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
        L.i("============onBackPressed===========");
        if (collectView.getVisibility() == View.VISIBLE) {
            collectView.setVisibility(View.GONE);
        } else if (webview.canGoBack()) {
            back();
        } else {
            super.onBackPressed();
        }

    }

    private void back() {
        webview.goBack();

    }

    private void forword() {
        webview.goForward();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WebHistoryAct.CODE_HISTORY & requestCode == WebHistoryAct.CODE_HISTORY && data != null) {
            url = data.getStringExtra("url");
            if (collectView.getVisibility() == View.VISIBLE) {
                collectView.setVisibility(View.GONE);
            }
            loadUrl(url);
        }
    }
}
