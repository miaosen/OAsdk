package com.oaui.view.colorpick;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.oaui.R;
import com.oaui.annotation.InjectReader;
import com.oaui.annotation.ViewInject;
import com.oaui.view.CustomLayout;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2018-03-13  15:02
 * @Descrition
 */

public class ColorPickerLayout extends CustomLayout {

    @ViewInject
    ColorPickerView colorPickerView;

    @ViewInject
    View view_current_color,view_checked_color;
    @ViewInject
    EditText ed;

    @ViewInject
    Button btn;

    OnColorCheckedListener onColorCheckedListener;

    public ColorPickerLayout(Context context) {
        super(context);
    }

    public ColorPickerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void initData() {
        InjectReader.injectAllFields(this);
        int color = colorPickerView.getColor();
        view_current_color.setBackgroundColor(color);
        setCheckedColor(color);
        colorPickerView.setOnColorChangedListener(new ColorPickerView.OnColorChangedListener() {
            @Override
            public void onColorChanged(int newColor) {
                setCheckedColor(newColor);

            }
        });
        btn.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(onColorCheckedListener!=null){
                    onColorCheckedListener.onChecked(colorPickerView.getColor());
                }
                return false;
            }
        });
    }

    private void setCheckedColor(int newColor) {
        view_checked_color.setBackgroundColor(newColor);
        String s = Integer.toHexString(newColor);
        String substring ="#"+ s.substring(2, s.length());
        ed.setText(substring);

    }

    @Override
    protected void onCreateView() {

    }

    @Override
    public int setXmlLayout() {
        return R.layout.ui_view_colorpick_layout;
    }


    public interface OnColorCheckedListener{
        void onChecked(int color);
    }

    public OnColorCheckedListener getOnColorCheckedListener() {
        return onColorCheckedListener;
    }

    public void setOnColorCheckedListener(OnColorCheckedListener onColorCheckedListener) {
        this.onColorCheckedListener = onColorCheckedListener;
    }
}
