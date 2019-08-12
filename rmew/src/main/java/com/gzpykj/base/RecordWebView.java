package com.gzpykj.base;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.List;

import cn.oahttp.cookies.CookiePersistence;
import cn.oaui.L;
import cn.oaui.UIGlobal;
import okhttp3.Cookie;

public class RecordWebView extends WebView {

    //WebViewFactory webViewFactory;

    public RecordWebView(Context context) {
        super(context);
    }

    public RecordWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        //webViewFactory = new WebViewFactory(this);
        //addJavascriptInterface(webViewFactory, "app");
        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        // 设置可以支持缩放
        webSettings.setSupportZoom(true);
        // 设置出现缩放工具
        webSettings.setBuiltInZoomControls(true);
        //扩大比例的缩放
        webSettings.setUseWideViewPort(true);
        //自适应屏幕
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
        // 禁止系统复制栏出现
        setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });


        //webSettings.setBuiltInZoomControls(false);
        //webSettings.setDisplayZoomControls(false);
        setInitialScale(180);
        setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.loadUrl(request.getUrl().toString());
                } else {
                    view.loadUrl(request.toString());
                }
                L.i("============shouldOverrideUrlLoading==========="+request.getUrl());
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

        });
        //int   screenDensity   = getResources().getDisplayMetrics(). densityDpi ;
        //WebSettings.ZoomDensity   zoomDensity   = WebSettings.ZoomDensity. MEDIUM ;
        //L.i("=========init=============="+zoomDensity);
        //switch (screenDensity){
        //    case   DisplayMetrics.DENSITY_LOW :
        //        zoomDensity = WebSettings.ZoomDensity.CLOSE;
        //        break ;
        //    case   DisplayMetrics.DENSITY_MEDIUM :
        //        zoomDensity = WebSettings.ZoomDensity.MEDIUM;
        //        break ;
        //    case   DisplayMetrics.DENSITY_HIGH :
        //        zoomDensity = WebSettings.ZoomDensity.FAR;
        //        break ;
        //}
        //webSettings.setDefaultZoom(zoomDensity) ;
        setCookie(getContext(), "47.97.191.166:8080");
    }


    public static void setCookie(Context context, String url) {
        try {
            CookieSyncManager.createInstance(context);
            CookieManager cookieManager = CookieManager.getInstance();

            cookieManager.setAcceptCookie(true);
            cookieManager.removeSessionCookie();//移除
            cookieManager.removeAllCookie();
            StringBuilder sbCookie = new StringBuilder();
            List<Cookie> formCache = CookiePersistence.getFormCache(UIGlobal.getApplication(), url);
            L.i("============setCookie===========" + formCache);
            //.***.com为api地址或者项目域名
            for (int i = 0; i < formCache.size(); i++) {
                sbCookie.append(formCache.get(i));
            }
            String cookieValue = sbCookie.toString();
            cookieManager.setCookie(url, cookieValue);
            CookieSyncManager.getInstance().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    //public WebViewFactory getWebViewFactory() {
    //    return webViewFactory;
    //}
    //
    //public void setWebViewFactory(WebViewFactory webViewFactory) {
    //    this.webViewFactory = webViewFactory;
    //}
    //

}
