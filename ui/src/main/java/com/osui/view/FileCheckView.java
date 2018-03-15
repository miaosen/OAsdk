package com.osui.view;

import android.content.Context;
import android.util.AttributeSet;

import com.osui.R;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2018-03-14  15:13
 * @Descrition
 */

public class FileCheckView extends CustomLayout{


    public FileCheckView(Context context) {
        super(context);
    }

    public FileCheckView(Context context, AttributeSet attrs) {
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
        return R.layout.ui_view_file_check_view;
    }
}
