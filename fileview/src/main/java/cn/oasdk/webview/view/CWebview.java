package cn.oasdk.webview.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;

import cn.oaui.L;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2020-07-13  09:57
 * @Descrition
 */
public class CWebview extends WebView {


    public CWebview(Context context) {
        super(context);
        initWebview();
    }

    public CWebview(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWebview();
    }

    public CWebview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWebview();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CWebview(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initWebview();
    }

    public CWebview(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
        initWebview();
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void initWebview() {
        requestFocus();
        WebSettings setting =getSettings();
        setting.setDefaultTextEncodingName("utf-8");//设置网页默认编码
        setting.setLoadWithOverviewMode(true);
        setting.setBlockNetworkImage(false);//解决图片不显示
        setting.setUseWideViewPort(true);
        setting.setJavaScriptEnabled(true); // 设置Webview支持JS代码
        setting.setJavaScriptCanOpenWindowsAutomatically(true);//JS互调
        setting.setDatabaseEnabled(true);   // 开启 database storage API 功能
        setting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        // 设置数据库缓存路径
        setting.setDomStorageEnabled(true); // 开启 DOM storage API 功能
//         开启Application Cache功能
        setting.setAppCacheEnabled(true);//开启 Application Caches 功能
        setting.setAllowContentAccess(true); // 是否可访问Content Provider的资源，默认值 true
        setting.setAllowFileAccess(true);// 设置允许访问文件数据
//         是否允许通过file url加载的Javascript读取本地文件，默认值 false
        setting.setAllowFileAccessFromFileURLs(true);
//         是否允许通过file url加载的Javascript读取全部资源(包括文件,http,https)，默认值 false
        setting.setAllowUniversalAccessFromFileURLs(true);
//         支持缩放
        setting.setSupportZoom(true);
        setting.setTextZoom(100);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW); // 解决http及https混合情况下页面加载问题
        }
        setting.setAppCacheMaxSize(1024 * 1024 * 1024);//b k m
        Activity activity= (Activity) getContext();
        addJavascriptInterface(new MJavascriptInterface(activity), "imageListener");
        setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                //downloadBySystem(url,contentDisposition,mimeType);
                downloadByBrowser(url);
            }
        });
    }



    public class MJavascriptInterface {

        private Activity mActivity;

        public MJavascriptInterface(Activity activity) {
            mActivity = activity;
        }
        /**
         *
         * @param imgs
         * @param img
         */
        @android.webkit.JavascriptInterface
        public void open(final String[] imgs, final String img) {
            L.i("============open==========="+imgs);
            L.i("============open==========="+img);
        }

        @android.webkit.JavascriptInterface
        public void open(final String[] imgs, final int index) {
            L.i("============open==========="+imgs);
            L.i("============open==========="+index);
        }

    }

    private void downloadByBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(url));
        Activity activity= (Activity) getContext();
        activity.startActivity(intent);
    }
}
