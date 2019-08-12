package cn.oaui.view.tiplayout;

import android.content.Context;
import android.util.AttributeSet;

import cn.oaui.R;
import cn.oaui.view.CustomLayout;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2018-01-25  16:01
 * @Descrition
 */

public class TipFooterLayout extends CustomLayout {


    public TipFooterLayout(Context context) {
        super(context);
    }

    public TipFooterLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onCreateView() {

    }

    @Override
    public int setXmlLayout() {
        return R.layout.tip_layout_footer;
    }
}
