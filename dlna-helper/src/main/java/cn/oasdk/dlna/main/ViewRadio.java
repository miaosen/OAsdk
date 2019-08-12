package cn.oasdk.dlna.main;

import android.content.Context;
import android.util.AttributeSet;

import cn.oasdk.dlna.R;
import cn.oaui.view.CustomLayout;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2019-08-05  14:58
 * @Descrition
 */

public class ViewRadio extends CustomLayout {


    public ViewRadio(Context context) {
        super(context);
    }

    public ViewRadio(Context context, AttributeSet attrs) {
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
        return R.layout.view_data_list;
    }
}
