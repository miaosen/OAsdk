package cn.oasdk.webview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import cn.oasdk.fileview.R;
import cn.oaui.annotation.InjectReader;
import cn.oaui.annotation.ViewInject;
import cn.oaui.view.ClearableEditText;
import cn.oaui.view.CustomLayout;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2021-07-18  21:44
 * @Descrition 联想词，剪切板，搜索引擎选择，链接检测，复制标题
 *
 */
public class SearchView extends CustomLayout {

    @ViewInject
    public View ln_back,ln_search_btn;

    @ViewInject
    public ClearableEditText clearableEditText;

    public SearchView(Context context) {
        super(context);
    }

    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onCreateView() {
        InjectReader.injectAllFields(this);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

    }

    @Override
    public int setXmlLayout() {
        return R.layout.search_view;
    }
}
