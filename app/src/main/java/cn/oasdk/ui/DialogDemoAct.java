package cn.oasdk.ui;

import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cn.oasdk.R;
import cn.oasdk.dlna.base.BaseActivity;
import cn.oaui.L;
import cn.oaui.annotation.ViewInject;
import cn.oaui.utils.ViewUtils;
import cn.oaui.view.dialog.FrameDialog;
import cn.oaui.view.dialog.WindowDialog;
import cn.oaui.view.dialog.extra.WindowTipDialog;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2018-01-12  11:52
 * @Descrition
 */

public class DialogDemoAct extends BaseActivity {

    @ViewInject
    Button showasdown,show,showasup,cover_up,cover_down,fullscreen,btn_windows_dialog,btn_windows_tip_dialog;

    FrameDialog frameDialog;
    @ViewInject
    EditText search;


    @Override
    public void initConfig() {

    }

    @Override
    public int getContentView() {
        return R.layout.ui_dialog_demo_act;
    }

    @Override
    public void onViewCreate() {
        //全屏测试
        //getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
        //        WindowManager.LayoutParams. FLAG_FULLSCREEN);
        frameDialog=new FrameDialog(context,R.layout.ui_dialog_view);
        //frameDialog.setShadow(false);
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //默认居中，设置为其他后可以通过这个设置回来
                frameDialog.setShowType(FrameDialog.SHOW_TYPE.CENTER);
                frameDialog.show();
            }
        });
        showasup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameDialog.showAsUp(showasup);
            }
        });
        showasdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameDialog.showAsDown(showasdown);
            }
        });
        cover_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameDialog.showAsCoverUp(cover_up);
            }
        });
        cover_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameDialog.showAsCoverDown(cover_down);
            }
        });
        fullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameDialog.fullscreen();
            }
        });
        combo();
        final WindowDialog windowDialog=new WindowDialog(R.layout.ui_dialog_view);
        btn_windows_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windowDialog.show();
            }
        });
        final WindowTipDialog windowTipDialog= createWindowTipDialog();
        btn_windows_tip_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windowTipDialog.show();
            }
        });
    }

    /**
     * 创建全局提示弹窗
     */
    private WindowTipDialog createWindowTipDialog() {
        WindowTipDialog windowDialog=new WindowTipDialog();
        //windowDialog.hideCancleButton();
        windowDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                ViewUtils.toast("你点击了取消键");
                dialog.dismiss();
            }
        });
        windowDialog.setOnSureListener(new WindowTipDialog.OnSureListener() {
            @Override
            public void onSure(DialogInterface dialog) {
                ViewUtils.toast("你点击了确定键");
                dialog.dismiss();
            }
        });
        windowDialog.setText("");
        //windowDialog.setContentView(ViewUtils.inflatView(context,R.layout.ui_dialog_view));
        windowDialog.setText("全局提示弹窗，依赖Application创建，可以不依赖activity弹出！");
        return windowDialog;
    }

    private void combo() {
        final FrameDialog  combo=new FrameDialog(context,R.layout.ui_dialog_view);
        final TextView tv_content= (TextView) combo.findViewById(R.id.tv_content);
        search.setFocusable(false);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 ViewUtils.showKeyboard(search);
                combo.showAsDown(search);
            }
        });
        combo.setFocuse(false);
        combo.setShadow(false);
        combo.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                search.setFocusable(false);
                ViewUtils.hideKeyboard(search);
            }
        });
        combo.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
            }
        });
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                search.setFocusable(true);
                search.setFocusableInTouchMode(true);
                combo.showAsDown(search);
                 }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                L.i("=========beforeTextChanged=============="+s);
                tv_content.setText(""+s);
            }
        });
    }

    @Override
    public void initData() {
    }
}
