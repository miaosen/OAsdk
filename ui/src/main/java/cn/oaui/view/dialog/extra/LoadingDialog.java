package cn.oaui.view.dialog.extra;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import cn.oaui.view.dialog.FrameDialog;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2018-06-20  10:40
 * @Descrition
 */

public class LoadingDialog extends FrameDialog {


    public LoadingDialog(Context context, View dialogView) {
        super(context, dialogView);
    }

    public LoadingDialog(View dialogView) {
        super(dialogView);
    }

    public LoadingDialog(Context context, int dialogLayout) {
        super(context, dialogLayout);
    }

    public LoadingDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
