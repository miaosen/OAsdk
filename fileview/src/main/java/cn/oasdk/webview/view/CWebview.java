package cn.oasdk.webview.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.util.Map;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.ViewCompat;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2020-07-13  09:57
 * @Descrition
 */
public class CWebview extends WebView implements NestedScrollingChild {

    private int mLastMotionY;

    private final int[] mScrollOffset = new int[2];
    private final int[] mScrollConsumed = new int[2];

    private int mNestedYOffset;

    private NestedScrollingChildHelper mChildHelper;


    public CWebview(Context context) {
        super(context);
        initSetting();
    }

    public CWebview(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSetting();
    }

    public CWebview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSetting();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CWebview(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initSetting();
    }

    public CWebview(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
        initSetting();
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void initSetting() {
        requestFocus();
        WebSettings setting =getSettings();
        setting.setDefaultTextEncodingName("utf-8");//设置网页默认编码
        setting.setLoadWithOverviewMode(true);
        setting.setBlockNetworkImage(false);//解决图片不显示
        setting.setUseWideViewPort(true);
        setting.setJavaScriptEnabled(true); // 设置Webview支持JS代码
        setting.setJavaScriptCanOpenWindowsAutomatically(true);//JS互调
        setting.setDatabaseEnabled(true);   // 开启 database storage API 功能
        setting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
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
        mChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
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
        }

        @android.webkit.JavascriptInterface
        public void open(final String[] imgs, final int index) {
        }

    }

    @Override
    public void loadUrl(String url) {

        //getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        super.loadUrl(url);
    }

    @Override
    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        //getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        super.loadUrl(url, additionalHttpHeaders);
    }

    @Override
    public void loadDataWithBaseURL(@Nullable String baseUrl, String data, @Nullable String mimeType, @Nullable String encoding, @Nullable String historyUrl) {
        //getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        super.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
    }

    @Override
    public void goBack() {
        //getSettings().setCacheMode(WebSettings.LOAD_CACHE_ONLY);
        super.goBack();
    }

    @Override
    public void goForward() {
        //getSettings().setCacheMode(WebSettings.LOAD_CACHE_ONLY);
        super.goForward();
    }

    private void downloadByBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(url));
        Activity activity= (Activity) getContext();
        activity.startActivity(intent);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = false;
        MotionEvent trackedEvent = MotionEvent.obtain(event);
        final int action = MotionEventCompat.getActionMasked(event);
        if (action == MotionEvent.ACTION_DOWN) {
            mNestedYOffset = 0;
        }

        int y = (int) event.getY();
        event.offsetLocation(0, mNestedYOffset);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = y;

                int nestedScrollAxis = ViewCompat.SCROLL_AXIS_VERTICAL;
                nestedScrollAxis |= ViewCompat.SCROLL_AXIS_HORIZONTAL;  //按位或运算

//                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
                startNestedScroll(nestedScrollAxis);
                result = super.onTouchEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaY = mLastMotionY - y;

                if (dispatchNestedPreScroll(0, deltaY, mScrollConsumed, mScrollOffset)) {
                    deltaY -= mScrollConsumed[1];
                    trackedEvent.offsetLocation(0, mScrollOffset[1]);
                    mNestedYOffset += mScrollOffset[1];
                }

                int oldY = getScrollY();
                mLastMotionY = y - mScrollOffset[1];
                int newScrollY = Math.max(0, oldY + deltaY);
                deltaY -= newScrollY - oldY;
                if (dispatchNestedScroll(0, newScrollY - deltaY, 0, deltaY, mScrollOffset)) {
                    mLastMotionY -= mScrollOffset[1];
                    trackedEvent.offsetLocation(0, mScrollOffset[1]);
                    mNestedYOffset += mScrollOffset[1];
                }
                if (mScrollConsumed[1] == 0 && mScrollOffset[1] == 0) {
                    trackedEvent.recycle();
                    result = super.onTouchEvent(trackedEvent);
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                stopNestedScroll();
                result = super.onTouchEvent(event);
                break;
        }
        return result;
    }

    // NestedScrollingChild

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {//设置嵌套滑动是否可用
        mChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {//嵌套滑动是否可用
        return mChildHelper.isNestedScrollingEnabled();
    }

    /**
     * 开始嵌套滑动,
     *
     * @param axes 表示方向 有一下两种值
     *             ViewCompat.SCROLL_AXIS_HORIZONTAL 横向滑动
     *             ViewCompat.SCROLL_AXIS_VERTICAL 纵向滑动
     */
    @Override
    public boolean startNestedScroll(int axes) {
        return mChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {//停止嵌套滑动
        mChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {//是否有父View 支持 嵌套滑动,  会一层层的网上寻找父View
        return mChildHelper.hasNestedScrollingParent();
    }

    /**
     * 在处理滑动之后 调用
     *
     * @param dxConsumed     x轴上 被消费的距离
     * @param dyConsumed     y轴上 被消费的距离
     * @param dxUnconsumed   x轴上 未被消费的距离
     * @param dyUnconsumed   y轴上 未被消费的距离
     * @param offsetInWindow view 的移动距离
     * @return
     */
    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    /**
     * 一般在滑动之前调用, 在ontouch 中计算出滑动距离, 然后调用该方法, 就给支持的嵌套的父View 处理滑动事件
     *
     * @param dx             x 轴上滑动的距离, 相对于上一次事件, 不是相对于 down事件的 那个距离
     * @param dy             y 轴上滑动的距离
     * @param consumed       一个数组, 可以传 一个空的 数组,  表示 x 方向 或 y 方向的事件 是否有被消费
     * @param offsetInWindow 支持嵌套滑动到额父View 消费 滑动事件后 导致 本 View 的移动距离
     * @return 支持的嵌套的父View 是否处理了 滑动事件
     */
    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    /**
     * @param velocityX x 轴上的滑动速度
     * @param velocityY y 轴上的滑动速度
     * @param consumed  是否被消费
     * @return
     */
    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    /**
     * @param velocityX x 轴上的滑动速度
     * @param velocityY y 轴上的滑动速度
     * @return
     */
    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }
}
