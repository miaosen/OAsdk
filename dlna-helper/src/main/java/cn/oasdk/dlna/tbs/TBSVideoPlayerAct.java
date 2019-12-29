package cn.oasdk.dlna.tbs;

import android.graphics.PixelFormat;
import android.view.View;

import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;

import cn.oasdk.dlna.R;
import cn.oasdk.dlna.base.BaseActivity;
import cn.oaui.L;
import cn.oaui.annotation.ViewInject;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2019-11-18  11:01
 * @Descrition
 */
public class TBSVideoPlayerAct extends BaseActivity {

    @ViewInject
    X5WebView x5WebView;


    @Override
    public void initConfig() {

    }

    @Override
    public int getContentView() {
        return R.layout.tbs_video_player_act;
    }

    @Override
    public void onViewCreate() {

    }

    @Override
    public void initData() {
        String filePath = getIntent().getStringExtra("filePath");
        L.i("============initData==========="+filePath);
        startPlay("file://"+filePath);
    }

    /**
     * 使用自定义webview播放视频
     * @param vedioUrl 视频地址
     */
    private void startPlay(String vedioUrl) {
        x5WebView.loadUrl(vedioUrl);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        x5WebView.getView().setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        x5WebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView webView, int i) {
                super.onProgressChanged(webView, i);
                L.i("============onProgressChanged==========="+i);
            }


        });
    }

}
