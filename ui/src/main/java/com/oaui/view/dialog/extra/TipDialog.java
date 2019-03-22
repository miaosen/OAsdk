package com.oaui.view.dialog.extra;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oaui.R;
import com.oaui.annotation.InjectReader;
import com.oaui.annotation.ViewInject;
import com.oaui.view.dialog.FrameDialog;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2018-06-21  17:17
 * @Descrition
 */

public class TipDialog extends FrameDialog {

    DialogInterface.OnDismissListener onDismissListener;

    WindowTipDialog.OnSureListener onSureListener;

    @ViewInject
    public View line_inbtn;

    @ViewInject
    public TextView tv_title,tv_content;

    @ViewInject
    public Button btn_sure,btn_cancle;

    @ViewInject
    public LinearLayout ln_content;

    public TipDialog(Context context) {
        super(context,R.layout.ui_dialog_extra_tipdlg);
        InjectReader.injectAllFields(this);
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onSureListener!=null){
                    onSureListener.onSure(TipDialog.this);
                }else{
                    dismiss();
                }
            }
        });
        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onDismissListener!=null){
                    onDismissListener.onDismiss(TipDialog.this);
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

    public DialogInterface.OnDismissListener getOnDismissListener() {
        return onDismissListener;
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public WindowTipDialog.OnSureListener getOnSureListener() {
        return onSureListener;
    }

    public void setOnSureListener(WindowTipDialog.OnSureListener onSureListener) {
        this.onSureListener = onSureListener;
    }

    public TextView getTv_title() {
        return tv_title;
    }

    public void setTv_title(TextView tv_title) {
        this.tv_title = tv_title;
    }

    public TextView getTv_content() {
        return tv_content;
    }

    public void setTv_content(TextView tv_content) {
        this.tv_content = tv_content;
    }
}
