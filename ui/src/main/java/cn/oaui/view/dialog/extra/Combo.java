package cn.oaui.view.dialog.extra;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import cn.oaui.L;
import cn.oaui.utils.ViewUtils;
import cn.oaui.view.dialog.FrameDialog;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2018-06-21  09:55
 * @Descrition
 */

public class Combo extends EditText {

    FrameDialog combo;
    public Combo(Context context) {
        super(context);
    }

    public Combo(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Combo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        combo=new FrameDialog(getContext(), cn.oaui.R.layout.ui_dialog_extra_combo);
        setFocusable(false);
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtils.showKeyboard(Combo.this);
                combo.showAsDown(Combo.this);
            }
        });
        combo.setFocuse(false);
        combo.setShadow(false);
        combo.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                setFocusable(false);
                ViewUtils.hideKeyboard(Combo.this);
            }
        });
        combo.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
            }
        });
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                setFocusable(true);
                setFocusableInTouchMode(true);
                combo.showAsDown(Combo.this);
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                L.i("=========beforeTextChanged=============="+s);
                //setText(""+s);
            }
        });
    }
}
