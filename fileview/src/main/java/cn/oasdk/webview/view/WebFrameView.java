package cn.oasdk.webview.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import androidx.annotation.RequiresApi;
import cn.oasdk.fileview.R;
import cn.oasdk.webview.WebHistoryAct;
import cn.oasdk.webview.WebViewAct;
import cn.oasdk.webview.data.WebResoureDeal;
import cn.oaui.L;
import cn.oaui.ResourceHold;
import cn.oaui.annotation.InjectReader;
import cn.oaui.annotation.ViewInject;
import cn.oaui.data.Row;
import cn.oaui.utils.BitmapUtils;
import cn.oaui.utils.DateTimeUtils;
import cn.oaui.utils.SPUtils;
import cn.oaui.utils.StringUtils;
import cn.oaui.utils.URIUtils;
import cn.oaui.utils.ViewUtils;
import cn.oaui.view.CustomLayout;
import cn.oaui.view.dialog.FrameDialog;
import cn.oaui.view.listview.BaseFillAdapter;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2020-08-01  23:54
 * @Descrition
 */
public class WebFrameView extends CustomLayout {


    String webFrameId = StringUtils.getUUID();

    @ViewInject
    public CWebview webview;

    WebViewAct webViewAct;


    @ViewInject
    public EditText ed;
    @ViewInject
    TextView tv_title;
    @ViewInject
    public LinearLayout ln_search_check, ln_go, ln_web_head, ln_content;
    @ViewInject
    ImageView img_search_logo, web_logo;


    //@ViewInject
    //MediaGallerView mediaGallerView;
    @ViewInject
    ProgressBar progressBar;
    FrameDialog searchMenuDialog;
    SearchMenuView searchMenuView;

    Row rowSearchParam;
    public static final String KEY_SEARCH = "key_search";
    public static final String FILE_SEARCH = "key_search";
    public String title, url, lastUrl;
    public static final String FILE_CACHE_URL = "file_cache_url";
    public static final String KEY_CACHE_URL = "key_cache_url";

    List<Row> rowsHistory;

    public Bitmap webIcon;

    OnPageChangeListener onPageChangeListener;
    @ViewInject
    public HomePageView homePageView;

    @ViewInject
    public ResourceListView resourceListView;

    SearchView searchView = null;

    FrameDialog searchDialog;


    public WebFrameView(Context context) {
        super(context);
    }

    public WebFrameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void initData() {
    }

    @Override
    protected void onCreateView() {
        InjectReader.injectAllFields(this);
        webview.initSetting();
        initWebview();
        webViewAct = (WebViewAct) getContext();
        //ed.setOnEditorActionListener(new TextView.OnEditorActionListener() {
        //    @Override
        //    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        //        String s = textView.getText() + "";
        //        if (EditorInfo.IME_ACTION_DONE == i && StringUtils.isNotEmpty(s)) {
        //            loadUrlOrSearch();
        //        }
        //        return true;
        //    }
        //});
        //ed.setcOnFocusChangeListener(new ClearableEditText.COnFocusChangeListener() {
        //    @Override
        //    public void onFocusChange(View v, boolean hasFocus) {
        //        if (hasFocus) {
        //            ln_web_head.setVisibility(View.GONE);
        //        } else {
        //            ln_web_head.setVisibility(View.VISIBLE);
        //        }
        //    }
        //});
       logicSearch(ed);
        ln_search_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchMenuDialog == null) {
                    searchMenuView = new SearchMenuView(context);
                    searchMenuDialog = new FrameDialog(searchMenuView);
                    searchMenuDialog.setShadow(false);
                }
                searchMenuView.listAdapter.setOnItemClickListener(new BaseFillAdapter.OnItemClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onItemClick(View convertView, Row row, int position, BaseFillAdapter.ViewHolder holder) {
                        rowSearchParam = row;
                        setSearchLogo();
                        SPUtils.saveRow(FILE_SEARCH, KEY_SEARCH, rowSearchParam);
                        searchMenuDialog.dismiss();
                        if (URLUtil.isNetworkUrl(ed.getText() + "")) {
                            ed.setText("");
                        }
                        //ViewUtils.showKeyboard(ed);
                    }
                });
                searchMenuDialog.showAsDown(progressBar);
                setSearchLogo();
                ln_web_head.setVisibility(View.GONE);

            }
        });
        rowSearchParam = SPUtils.getRow(FILE_SEARCH, KEY_SEARCH);
        if (rowSearchParam == null) {
            rowSearchParam = SearchMenuView.getParamRow(0);
            SPUtils.saveRow(FILE_SEARCH, KEY_SEARCH, rowSearchParam);
        }
        setSearchLogo();
        ln_web_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //tv_title.setVisibility(View.GONE);
                ln_web_head.setVisibility(View.GONE);
                ed.callOnClick();
                //ViewUtils.showKeyboard(ed);
            }
        });
        rowsHistory = SPUtils.getRows(WebHistoryAct.FILE_HISTORY, WebHistoryAct.KEY_HISTORY);
        if (rowsHistory == null) {
            rowsHistory = new LinkedList<>();
        }
        homePageView.getCollectView().listAdapter.setOnItemClickListener(new BaseFillAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View convertView, Row row, int position, BaseFillAdapter.ViewHolder holder) {
                loadUrl(row.getString("url"));
                homePageView.setVisibility(View.GONE);
            }
        });
        logicSearch(homePageView.ed);
    }

    private void logicSearch(EditText ed) {
        ed.setFocusable(false);
        ed.setFocusableInTouchMode(false);
        ed.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchDialog == null) {
                    searchView = new SearchView(context);
                    searchDialog = new FrameDialog(context, searchView);

                }
                searchDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        ViewUtils.showKeyboard(searchView.clearableEditText,searchDialog);

                    }
                });
                searchDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        ViewUtils.hideKeyboard(searchView.clearableEditText,searchDialog);
                    }
                });
                searchView.ln_back.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        searchDialog.dismiss();

                    }
                });
                searchView.ln_search_btn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadUrlOrSearch(searchView.clearableEditText);
                        searchDialog.dismiss();

                    }
                });
                searchDialog.fullscreen();
            }
        });
    }

    @Override
    public int setXmlLayout() {
        return R.layout.web_frame_view;
    }


    private void initWebview() {
        webview.setWebViewClient(new WebViewClient() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                WebResourceResponse webResourceResponse1 = null;
                //L.i("============shouldInterceptRequest==========="+request.getUrl());
                try {
                    webResourceResponse1 = WebResoureDeal.dealRes(view, request, WebFrameView.this, url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                WebFrameView.this.url = url;
                showWebHead();
                webViewAct.logicTail();
                hideMediaView();
                title = null;
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageStart(view, url);
                }
                Row row = SPUtils.getRow(FILE_CACHE_URL, KEY_CACHE_URL);
                row.put(webFrameId, url);
                saveHistoryUrl(row);
                resourceListView.clearRes();
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //L.i("============onPageFinished==========="+url);
                WebFrameView.this.url = url;
                webViewAct.logicTail();
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageFinish(view, url);
                }
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
                return super.onConsoleMessage(consoleMessage);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                WebFrameView.this.url = url;
                WebFrameView.this.title = title;
                showWebHead();
                saveHistory();

            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
                if (icon != null) {
                    webIcon = icon;
                    web_logo.setImageBitmap(icon);
                    BitmapUtils.saveBitmapToPathAsPng(icon, BookMarkView.getLogoPath(url));
                }
                if (onPageChangeListener != null) {
                    onPageChangeListener.onReceivedIcon(view, icon);
                }
            }
        });
    }

    public static void saveHistoryUrl(Row row) {
        SPUtils.saveRow(FILE_CACHE_URL, KEY_CACHE_URL, row);
    }


    public static Row getHistoryUrl() {
        return SPUtils.getRow(WebFrameView.FILE_CACHE_URL, WebFrameView.KEY_CACHE_URL);
    }


    private void showWebHead() {
        web_logo.setImageDrawable(ResourceHold.getDrawable(R.mipmap.icon_web));
        ln_web_head.setVisibility(View.VISIBLE);
        tv_title.setText(StringUtils.isNotEmpty(title) ? title : url);
    }


    public void loadUrlOrSearch(EditText ed) {
        String text = ed.getText() + "";
        L.i("============loadUrlOrSearch===========" + text);
        title = null;
        String tempUrl = text;
        if (!tempUrl.startsWith("http")) {
            tempUrl = "http://" + text;
        }
        if (URIUtils.isUrl(tempUrl)) {
            loadUrl(tempUrl);
        } else {
            searchByParam(text);

        }
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
        SPUtils.saveRows(WebHistoryAct.FILE_HISTORY, WebHistoryAct.KEY_HISTORY, rowsHistory);
    }

    private void hideMediaView() {
        //if (videoPalyView.player.isPlayingAd()) {
        //    videoPalyView.stop();
        //}
        //mediaGallerView.setVisibility(View.GONE);
    }


    public void loadUrl(String url) {

        ed.setText(url);
        webview.loadUrl(url);
    }

    //@NonNull
    //private VideoPalyView getVideoPalyView(int videoHeight) {
    //    if (videoPalyView == null) {
    //        videoPalyView = new VideoPalyView(getContext());
    //        videoPalyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, AppUtils.dp2px(videoHeight)));
    //    }
    //    return videoPalyView;
    //}

    public void playMedia(String url, String positionY) {
        int TV_HEIGHT = 100;
        //videoPalyView = getVideoPalyView(TV_HEIGHT);
        //videoPalyView.setTranslationY(AppUtils.dp2px(Float.parseFloat(positionY)));
        //mediaGallerView.setVisibility(View.VISIBLE);

        //L.i("============playMedia==========="+videoPalyView);
        //HttpProxyCacheServer proxy = AppContext.getProxy();
        //String proxyUrl = proxy.getProxyUrl(url);
        //Uri uri = Uri.parse(proxyUrl);
        //videoPalyView.stop();
        //videoPalyView.prepare(uri);
        //if(videoPalyView.getParent()==null){
        //    webview.addView(videoPalyView);
        //}


    }

    private void searchByParam(String text) {
        url = SearchMenuView.buildUrl(rowSearchParam, text);
        L.i("============searchByParam==========="+url);
        loadUrl(url);
    }


    @SuppressLint("NewApi")
    private void setSearchLogo() {
        Integer img = rowSearchParam.getInteger("img");
        L.i("============setSearchLogo===========" + img);
        if (img > 0) {
            img_search_logo.setImageDrawable(ResourceHold.getDrawable(img));
        }

    }


    public interface OnPageChangeListener {
        void onPageStart(WebView view, String url);

        void onPageFinish(WebView view, String url);

        void onReceivedIcon(WebView view, Bitmap icon);
    }

    public OnPageChangeListener getOnPageChangeListener() {
        return onPageChangeListener;
    }

    public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
    }

    public String getWebFrameId() {
        return webFrameId;
    }

    public void setWebFrameId(String webFrameId) {
        this.webFrameId = webFrameId;
    }
}
