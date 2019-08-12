package cn.oaui.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.List;

import cn.oaui.form.FormAdpater;
import cn.oaui.utils.ViewUtils;

/**
 * @author zms
 * @Created by com.gzpykj.com
 * @Date 2016-9-6
 * @Descrition 仿照RadioGroup, 解决RadioButton只能横向或者竖向布局, 并且支持多选和被表单模块识别
 */
public class CustomRadioGroup extends LinearLayout implements FormAdpater {


    private List<RadioButton> listRb = new ArrayList<RadioButton>();

    /**
     * 选中的RadioButton
     */
    private List<RadioButton> listCheckedRb = new ArrayList<RadioButton>();

    String strCheckValue = "";
    String strCheckBtnIdName = "";

    OnCheckedChangeListener onCheckedChangeListener;

    // 是否多选
    //boolean isMultiCheck = false;

    public CustomRadioGroup(Context context) {
        super(context);
    }

    public CustomRadioGroup(Context context, AttributeSet attr) {
        super(context, attr);
    }

    // 显示
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isInEditMode()) {
            init();
        }
    }

    protected void init() {
        //Logger.i("setClickable===========init");
        List<View> allChildViews = ViewUtils.getAllChildViews(this);
        for (int i = 0; i < allChildViews.size(); i++) {
            View view = allChildViews.get(i);
            if (view instanceof RadioButton) {
                RadioButton radioButton = (RadioButton) view;
                radioButton.setOnCheckedChangeListener(new mCheckedChange());
                listRb.add(radioButton);
                String text = "";
                if (radioButton.getTag() != null) {
                    text = (String) radioButton.getTag();
                } else {
                    text = radioButton.getText() + "";
                }
                if (text.equals(strCheckValue)) {
                    radioButton.setChecked(true);
                }
            }
        }
    }

    public void setClickable(boolean clickable) {
        for (int i = 0; i < listRb.size(); i++) {
            RadioButton radioButton = listRb.get(i);
            radioButton.setClickable(clickable);
        }
    }

    class mCheckedChange implements
            CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            RadioButton rb = (RadioButton) buttonView;
            unCheckAll();
            if (isChecked) {
                onCheck(rb);
                //Logger.i("rb===========true");
            } else {
                rb.setChecked(false);
                //Logger.i("rb===========false");
            }
            if (onCheckedChangeListener != null) {
                onCheckedChangeListener.onCheckedChanged(CustomRadioGroup.this,
                        rb.getId());
            }
        }
    }


    protected void onCheck(RadioButton rb) {
        strCheckValue = rb.getText() + "";
        rb.setChecked(true);
    }

    /**
     * 清除所有被选择按钮
     */
    protected void unCheckAll() {
        //Logger.info("unCheckAll===========" + listRb.size());
        for (int i = 0; i < listRb.size(); i++) {
            RadioButton radioButton = listRb.get(i);
            if (radioButton.isChecked()) {
                radioButton.setChecked(false);
            }
        }
    }

    /**
     * view加载完成是调用此方法来设置选中的值
     *
     * @param value
     */
    public void setCheck(String value) {
        strCheckValue = value;
        String[] split = strCheckValue.split(",");
        for (int i = 0; i < split.length; i++) {
            String string = split[i];
            for (int j = 0; j < listRb.size(); j++) {
                RadioButton radioButton = listRb.get(j);
                String text = "";
                if (radioButton.getTag() != null) {
                    text = (String) radioButton.getTag();
                } else {
                    text = radioButton.getText() + "";
                }
                if (text.equals(string)) {
                    radioButton.setChecked(true);
                    j = listRb.size();
                }
            }
        }

    }

    /**
     * view没有加载完成是调用此方法来设置选中的值
     *
     * @param value
     */
    public void setCheckValue(String value) {
        strCheckValue = value;

    }

    /**
     * 获取选中的值
     *
     * @return
     */
    @Override
    public String getValue() {
        String text = "";
        for (int i = 0; i < listRb.size(); i++) {
            RadioButton radioButton = listRb.get(i);
            if (radioButton.isChecked()) {
                // radioButton.setChecked(false);
                String str = "";
                if (radioButton.getTag() != null) {
                    str = (String) radioButton.getTag();
                } else {
                    str = radioButton.getText() + "";
                }
                if (TextUtils.isEmpty(text)) {
                    text = str;
                } else {
                    text = text + "," + str;
                }
            }
        }
        return text;
    }


    /**
     * 单选模式下获取选中的RadioButton
     *
     * @return
     */
    public RadioButton getCheckedRadioButton() {
        RadioButton rb = null;
        for (int i = 0; i < listRb.size(); i++) {
            RadioButton radioButton = listRb.get(i);
            if (radioButton.isChecked()) {
                rb = radioButton;
                i = listRb.size();
            }
        }
        return rb;
    }


    public interface OnCheckedChangeListener {
        void onCheckedChanged(CustomRadioGroup radiogroup, int id);
    }

    public OnCheckedChangeListener getOnCheckedChangeListener() {
        return onCheckedChangeListener;
    }

    public void setOnCheckedChangeListener(
            OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }


    @Override
    public void setValue(Object object) {
        setCheck(object + "");
    }

    @Override
    public boolean isScanAsOne() {
        return true;
    }


}
