package com.oaui.view.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.oaui.R;
import com.oaui.utils.DateTimeUtils;
import com.oaui.view.dialog.FrameDialog;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2018-04-17  16:52
 * @Descrition
 */

public class DateButton extends Button {

    FrameDialog frameDialog;

    Button btn_sure,btn_cancle;

    CalendarLayout calendarLayout;

    public DateButton(Context context) {
        super(context);
        init();
    }


    public DateButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DateButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        frameDialog=new FrameDialog(getContext(), R.layout.ui_view_calendar_dialog_layout);
        frameDialog.setClickShadowToNoDismiss();
        btn_sure= (Button) frameDialog.findViewById(R.id.btn_sure);
        btn_cancle= (Button) frameDialog.findViewById(R.id.btn_cancle);
        calendarLayout= (CalendarLayout) frameDialog.findViewById(R.id.calendarLayout);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                frameDialog.show();
            }
        });
        btn_sure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setText(calendarLayout.getDateText());
                frameDialog.dismiss();
            }
        });
        btn_cancle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                frameDialog.dismiss();
            }
        });

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setText(DateTimeUtils.getCurrentDay());
    }
}
