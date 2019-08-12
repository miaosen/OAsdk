package cn.oaui.view.dialog.extra;

import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.oaui.R;
import cn.oaui.annotation.InjectReader;
import cn.oaui.annotation.ViewInject;
import cn.oaui.view.dialog.WindowDialog;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2018-01-18  11:12
 * @Descrition
 */

public class WindowTipDialog extends WindowDialog {

    OnDismissListener onDismissListener;

    OnSureListener onSureListener;

    @ViewInject
    public View line_inbtn;

    @ViewInject
    public TextView tv_title,tv_content;

    @ViewInject
    public Button btn_sure,btn_cancle;

    @ViewInject
    public LinearLayout ln_content;

    public WindowTipDialog() {
       super(R.layout.ui_dialog_extra_windowtipdlg);
        InjectReader.injectAllFields(this,dialogLayout);
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onSureListener!=null){
                    onSureListener.onSure(WindowTipDialog.this);
                }else{
                    dismiss();
                }
            }
        });
        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onDismissListener!=null){
                    onDismissListener.onDismiss(WindowTipDialog.this);
                }else{
                    dismiss();
                }
            }
        });

    }

    public void hideCancleButton(){
        btn_cancle.setVisibility(View.GONE);
        line_inbtn.setVisibility(View.GONE);
    }

    /**
     * 设置标题
     * @param text
     */
    public void setTitle(String text){
        tv_title.setText(text);
    }


    public void setText(String text){
        tv_content.setText(text);
    }

    /**
    * 移除弹窗中间部分的view并且加一个view进去
     * @param view
     */
    public void setContentView(View view){
        ln_content.removeAllViews();
        ln_content.addView(view);
    }


    public interface OnSureListener {
        /**
         * 点击确定键回调次方法
         * @param dialog
         */
         void onSure(DialogInterface dialog);
    }

    public OnDismissListener getOnDismissListener() {
        return onDismissListener;
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public OnSureListener getOnSureListener() {
        return onSureListener;
    }

    public void setOnSureListener(OnSureListener onSureListener) {
        this.onSureListener = onSureListener;
    }
}
