package com.oaui.view.signature;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

import com.oaui.L;
import com.oaui.R;
import com.oaui.annotation.InjectReader;
import com.oaui.annotation.ViewInject;
import com.oaui.view.CustomLayout;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2018-03-13  17:09
 * @Descrition
 */

public class SigntureBoldPickerView extends CustomLayout {

    @ViewInject
    SeekBar seekBar,seekBar2;

    public int seekBarNum=0,seekBar2Num=0;
    @ViewInject
    EditText ed_signture_bold;
    @ViewInject
    Button btn;

    OnBoldPickListener onBoldPickListener;


    public SigntureBoldPickerView(Context context) {
        super(context);
    }

    public SigntureBoldPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onCreateView() {
        InjectReader.injectAllFields(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                L.i("=========onProgressChanged=============="+progress);
                seekBarNum=progress;
                setProgress();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                L.i("=========onProgressChanged2=============="+progress);
                seekBar2Num=progress;
                setProgress();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        btn.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(onBoldPickListener!=null){
                    String text = ed_signture_bold.getText()+"";
                    onBoldPickListener.onPick(Float.parseFloat(text));
                }
                return false;
            }
        });
    }

    public void setBold(String text){
      ed_signture_bold.setText(text);
    }

    private void setProgress() {
        ed_signture_bold.setText(seekBarNum+"."+seekBar2Num);

    }

    @Override
    public int setXmlLayout() {
        return R.layout.ui_view_signature_boldpick_view;
    }


    public interface OnBoldPickListener{
        void onPick(float progress);
    }

    public OnBoldPickListener getOnBoldPickListener() {
        return onBoldPickListener;
    }

    public void setOnBoldPickListener(OnBoldPickListener onBoldPickListener) {
        this.onBoldPickListener = onBoldPickListener;
    }


}
