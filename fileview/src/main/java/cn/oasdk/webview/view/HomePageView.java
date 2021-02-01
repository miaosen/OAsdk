package cn.oasdk.webview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.oasdk.fileview.R;
import cn.oasdk.webview.WebViewAct;
import cn.oaui.annotation.InjectReader;
import cn.oaui.annotation.ViewInject;
import cn.oaui.utils.StringUtils;
import cn.oaui.view.ClearableEditText;
import cn.oaui.view.CustomLayout;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2020-07-29  15:21
 * @Descrition
 */
public class HomePageView extends CustomLayout {


    @ViewInject
    BookMarkView collectView;

    @ViewInject
    ImageView img_forword, img_back;
    @ViewInject
    LinearLayout ln_search_check, ln_go, ln_web_head;
    @ViewInject
    ClearableEditText ed;



    public HomePageView(Context context) {
        super(context);
    }

    public HomePageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onCreateView() {
        InjectReader.injectAllFields(this);
        initHead();

    }

    private void initHead() {
        ed.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String s = textView.getText() + "";
                if (EditorInfo.IME_ACTION_DONE == i && StringUtils.isNotEmpty(s)) {
                    loadWebview();
                }
                return true;
            }
        });
        ed.setcOnFocusChangeListener(new ClearableEditText.COnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ln_web_head.setVisibility(View.GONE);
                } else {
                    ln_web_head.setVisibility(View.VISIBLE);
                }
            }
        });
        ln_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               loadWebview();
            }
        });
    }

    private void loadWebview() {
        setVisibility(GONE);
        WebViewAct activity= (WebViewAct) getContext();
        activity.currentWebFrameView.ed.setText(ed.getText());
        activity.currentWebFrameView.loadUrlOrSearch();
    }

    @Override
    public int setXmlLayout() {
        return R.layout.homepage_view;
    }

    public BookMarkView getCollectView() {
        return collectView;
    }

    public void setCollectView(BookMarkView collectView) {
        this.collectView = collectView;
    }
}
