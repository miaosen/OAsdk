package cn.oasdk.webview;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import cn.oasdk.base.BaseAct;
import cn.oasdk.fileview.R;
import cn.oasdk.webview.view.WebFrameMenuView;
import cn.oasdk.webview.view.WebFrameView;
import cn.oasdk.webview.view.WebMuneView;
import cn.oaui.L;
import cn.oaui.ResourceHold;
import cn.oaui.annotation.InjectReader;
import cn.oaui.annotation.ViewInject;
import cn.oaui.data.Row;
import cn.oaui.utils.AppUtils;
import cn.oaui.utils.IntentUtils;
import cn.oaui.utils.SPUtils;
import cn.oaui.utils.StringUtils;
import cn.oaui.utils.URIUtils;
import cn.oaui.utils.ViewUtils;
import cn.oaui.view.dialog.FrameDialog;
import cn.oaui.view.listview.BaseFillAdapter;


public class WebViewAct extends BaseAct {

    public final static String KEY_URl = "key_url";

    private LinkedList<WebFrameView> listWebFrame = new LinkedList<>();

    public WebFrameView currentWebFrameView;

    @ViewInject
    FrameLayout frameLayout;

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
    LinearLayout ln_back, ln_forward, ln_menu, ln_homepage, ln_edit_tail;
    @ViewInject
    FrameLayout fr_tab;

    FrameDialog menuDialog;

    FrameDialog webFrameDialog;
    WebFrameMenuView webFrameMenuView;

    @ViewInject
    TextView tv_frame_num;





    @ViewInject
    com.google.android.material.appbar.AppBarLayout appBarLayout;

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_act);
        context = this;
        InjectReader.injectAllFields(this);
        Row row =WebFrameView.getHistoryUrl();
        if (row.size() > 0) {
            for (String webFrameId : row.keySet()) {
                addWebFrame(row.getString(webFrameId), webFrameId);
            }
        } else {
            addWebFrame(null, null);
        }
        initPermission();
        initTailView();

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
                                in.putExtra("name", StringUtils.isEmpty(currentWebFrameView.title) ? currentWebFrameView.url : currentWebFrameView.title);
                                in.putExtra("url", currentWebFrameView.url);
                                startActivity(in);
                                menuDialog.dismiss();
                            } else if ("刷新".equals(name)) {
                                currentWebFrameView.webview.reload();
                                menuDialog.dismiss();
                            } else if ("退出".equals(name)) {
                                finish();
                                AppUtils.quit();

                            }else if (ResourceHold.getString(R.string.setting).equals(name)) {
                                IntentUtils.jump(context,SettingAct.class);
                            } else {

                            }
                        }
                    });
                }
                menuDialog.showAsCoverUp(ln_edit_tail);
            }
        });

    }

    private void addWebFrame(String url, String webFrameId) {
        WebFrameView webFrameView = new WebFrameView(context);
        currentWebFrameView = webFrameView;
        FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT, 1);
        frameLayout.addView(webFrameView, listWebFrame.size(), param);
        listWebFrame.add(webFrameView);
        tv_frame_num.setText(listWebFrame.size() + "");
        if (StringUtils.isNotEmpty(url)) {
            webFrameView.loadUrl(url);
            if (StringUtils.isNotEmpty(webFrameId)) {
                webFrameView.setWebFrameId(webFrameId);
            }
            //logicUrl();
        } else {
            loadHomePage();
        }

    }

    private void logicUrl() {
        String url = getIntent().getStringExtra(KEY_URl);
        if (StringUtils.isNotEmpty(url)) {
            if (!url.startsWith("http") && !url.startsWith("file://")) {
                url = "http://" + url;
            }
            currentWebFrameView.loadUrl(url);
        } else {
            url = SPUtils.getText(WebFrameView.FILE_CACHE_URL, WebFrameView.KEY_CACHE_URL);
            if (StringUtils.isNotEmpty(url) && URIUtils.isUrl(url)) {
                currentWebFrameView.loadUrl(url);
            }
        }
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
                if (currentWebFrameView.homePageView.getVisibility() == View.VISIBLE) {
                    currentWebFrameView.homePageView.setVisibility(View.GONE);
                    logicTail();
                } else if (currentWebFrameView.webview.canGoBack()) {
                    back();

                }
            }
        });
        ln_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentWebFrameView.webview.canGoForward()) {
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
                loadHomePage();
            }
        });
        fr_tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webFrameDialog == null) {
                    webFrameMenuView = new WebFrameMenuView(context);
                    webFrameDialog = new FrameDialog(webFrameMenuView);
                }
                webFrameMenuView.ln_add_webframe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addWebFrame(null, null);
                        webFrameDialog.dismiss();

                    }
                });
                webFrameMenuView.setListWebFrame(listWebFrame, listWebFrame.indexOf(currentWebFrameView));
                webFrameMenuView.setOnWindowCheckListener(new WebFrameMenuView.OnWindowCheckListener() {
                    @Override
                    public void onCheck(View convertView, Row row, int position, BaseFillAdapter.ViewHolder holder) {
                        L.i("============onItemClick===========" + row);
                        webFrameDialog.dismiss();
                        showWebFrame(position);
                    }

                    @Override
                    public void onColse(View convertView, Row row, int position, BaseFillAdapter.ViewHolder holder) {
                        //
                        webFrameDialog.dismiss();
                        WebFrameView webFrameView = listWebFrame.get(position);
                        frameLayout.removeView(webFrameView);
                        Row rowUrl =WebFrameView.getHistoryUrl();
                        rowUrl.remove(webFrameView.getWebFrameId());
                        WebFrameView.saveHistoryUrl(rowUrl);
                        if (listWebFrame.indexOf(currentWebFrameView) == position) {
                            listWebFrame.remove(position);
                            if (listWebFrame.size() == 0) {
                                addWebFrame(null, null);
                            } else {
                                showWebFrame(position-1);
                                tv_frame_num.setText(listWebFrame.size() + "");
                            }
                        } else {
                            listWebFrame.remove(position);
                            tv_frame_num.setText(listWebFrame.size() + "");
                        }

                    }
                });
                webFrameDialog.showAsUp(ln_edit_tail);
            }
        });
    }

    private void showWebFrame(int position) {
        if (listWebFrame.indexOf(currentWebFrameView) != position) {
            for (int i = position + 1; i < listWebFrame.size(); i++) {
                listWebFrame.get(i).setVisibility(View.GONE);
            }
            currentWebFrameView = listWebFrame.get(position);
            currentWebFrameView.setVisibility(View.VISIBLE);

        }
        logicTail();
    }

    private void loadHomePage() {
        if (currentWebFrameView.homePageView.getVisibility() == View.GONE) {
            currentWebFrameView.homePageView.setVisibility(View.VISIBLE);
            currentWebFrameView.title = "首页";
            currentWebFrameView.lastUrl = currentWebFrameView.url;
            currentWebFrameView.url = "首页";
            //SPUtils.saveText(WebFrameView.FILE_CACHE_URL, WebFrameView.KEY_CACHE_URL, currentWebFrameView.url);
            logicTail();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void logicTail() {
        //打开首页
        if (currentWebFrameView.homePageView.getVisibility() == View.VISIBLE && (StringUtils.isNotEmpty(currentWebFrameView.lastUrl) || "首页".equals(currentWebFrameView.lastUrl))) {
            Drawable drawable = ResourceHold.getDrawable(R.mipmap.icon_left);
            ViewUtils.tintDrawable(drawable, ResourceHold.getColor(R.color.black));
            img_back.setBackground(drawable);
            Drawable drawable2 = ResourceHold.getDrawable(R.mipmap.icon_right);
            ViewUtils.tintDrawable(drawable2, ResourceHold.getColor(R.color.grey_lt));
            img_forword.setBackground(drawable2);
        } else {
            Drawable drawable = ResourceHold.getDrawable(R.mipmap.icon_left);
            if (currentWebFrameView.webview.canGoBack()) {
                ViewUtils.tintDrawable(drawable, ResourceHold.getColor(R.color.black));
            } else {
                ViewUtils.tintDrawable(drawable, ResourceHold.getColor(R.color.grey_lt));
            }
            img_back.setBackground(drawable);
            Drawable drawable2 = ResourceHold.getDrawable(R.mipmap.icon_right);
            if (currentWebFrameView.webview.canGoForward()) {
                ViewUtils.tintDrawable(drawable2, ResourceHold.getColor(R.color.black));
            } else {
                ViewUtils.tintDrawable(drawable2, ResourceHold.getColor(R.color.grey_lt));
            }
            img_forword.setBackground(drawable2);
        }

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

    boolean isCanBack=false;
    Handler handler = new Handler();
    Toast toast;


    @Override
    public void onBackPressed() {
        if (currentWebFrameView.homePageView.getVisibility() == View.VISIBLE) {
            currentWebFrameView.homePageView.setVisibility(View.GONE);
        } else if (currentWebFrameView.webview.canGoBack()) {
            back();
        } else {

            if(isCanBack){
                if(toast!=null){
                    toast.cancel();
                }
                super.onBackPressed();
            }else{
                toast = ViewUtils.toast("再次点击将退出浏览器");
            }
            isCanBack=true;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isCanBack=false;
                }
            },3000);

        }
    }




    private void back() {
        currentWebFrameView.lastUrl = currentWebFrameView.url;
        L.i("============back===========" + currentWebFrameView.lastUrl);
        currentWebFrameView.webview.goBack();
        logicTail();


    }

    private void forword() {
        currentWebFrameView.lastUrl = currentWebFrameView.url;
        currentWebFrameView.webview.goForward();
        logicTail();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WebHistoryAct.CODE_HISTORY & requestCode == WebHistoryAct.CODE_HISTORY && data != null) {
            String url = data.getStringExtra("url");
            if (currentWebFrameView.homePageView.getVisibility() == View.VISIBLE) {
                currentWebFrameView.homePageView.setVisibility(View.GONE);
            }
            currentWebFrameView.loadUrl(url);
        }
    }

    public void logicPageStart(WebView view, String url, Bitmap favicon) {

    }
}
