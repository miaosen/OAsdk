package cn.oaui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cn.oaui.R;
import cn.oaui.annotation.InjectReader;
import cn.oaui.annotation.ViewInject;
import cn.oaui.utils.ViewUtils;


/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @git http://git.oschina.net/miaosen/MyUtils
 * @CreateDate 2017-01-12  10:52
 * @Descrition 通用头部
 */
public class HeaderView extends CustomLayout {

    View contentview;
    @ViewInject
    public Button leftBtn, rightBtn;

    @ViewInject
    TextView title;


    String titleText, leftText, rightText;

    boolean clickLeftFinish, clickRightFinish, isHideLeft, isHideRight;

    int leftDrawable, rightDrawable;

    public HeaderView(Context context) {
        super(context);
    }

    public HeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            analysisAttr(attrs);
        }
    }

    @Override
    public int setXmlLayout() {
        return R.layout.ui_view_header;
    }



    @Override
    public void initData() {

        if (!TextUtils.isEmpty(titleText)) {
            title.setText(titleText);
        }
        if (!TextUtils.isEmpty(leftText)) {
            leftBtn.setText(leftText);
        }
        if (!TextUtils.isEmpty(rightText)) {
            rightBtn.setText(rightText);
        }
        if (clickLeftFinish) {
            ViewUtils.finishByClick(leftBtn);
        }
        if (clickRightFinish) {
            ViewUtils.finishByClick(rightBtn);
        }
        if (isHideLeft) {
            leftBtn.setVisibility(INVISIBLE);
        } else {
            leftBtn.setVisibility(VISIBLE);
        }
        if (isHideRight) {
            rightBtn.setVisibility(INVISIBLE);
        } else {
            rightBtn.setVisibility(VISIBLE);
        }

        if (leftDrawable > 0) {
            Drawable drawable = getResources().getDrawable(leftDrawable);// 获取图片资源icon_date
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//设置图片的x轴，y轴，宽度，高度
            leftBtn.setCompoundDrawables(drawable, null, null, null);
        }
        if (rightDrawable > 0) {
            Drawable drawable = getResources().getDrawable(rightDrawable);// 获取图片资源icon_date
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//设置图片的x轴，y轴，宽度，高度
            rightBtn.setCompoundDrawables(null, null, drawable, null);
        }
    }

    @Override
    protected void onCreateView() {
        if(!isInEditMode()){
            InjectReader.injectAllFields(HeaderView.this, contentview);
        }
    }


    private void analysisAttr(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.HeaderView);
        titleText = typedArray.getString(R.styleable.HeaderView_title);
        leftText = typedArray.getString(R.styleable.HeaderView_leftText);
        if(leftText==null){
            leftText="返回";
        }
        rightText = typedArray.getString(R.styleable.HeaderView_rightText);
        clickLeftFinish = typedArray.getBoolean(R.styleable.HeaderView_clickLeftFinish, true);
        clickRightFinish = typedArray.getBoolean(R.styleable.HeaderView_clickRightFinish, false);
        isHideLeft = typedArray.getBoolean(R.styleable.HeaderView_isHideLeft, false);
        isHideRight = typedArray.getBoolean(R.styleable.HeaderView_isHideRight, false);
        leftDrawable = typedArray.getResourceId(R.styleable.HeaderView_leftDrawable, -1);
        rightDrawable = typedArray.getResourceId(R.styleable.HeaderView_rightDrawable, -1);
        typedArray.recycle();

    }



    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
        title.setText(titleText);
    }

    public String getLeftText() {
        return leftText;
    }

    public void setLeftText(String leftText) {
        this.leftText = leftText;
        leftBtn.setText(leftText);
    }

    public String getRightText() {
        return rightText;
    }

    public void setRightText(String rightText) {
        this.rightText = rightText;
        rightBtn.setText(rightText);
    }

    public boolean isClickLeftFinish() {
        return clickLeftFinish;
    }

    public void setClickLeftFinish(boolean clickLeftFinish) {
        this.clickLeftFinish = clickLeftFinish;
    }

    public boolean isClickRightFinish() {
        return clickRightFinish;
    }

    public void setClickRightFinish(boolean clickRightFinish) {
        this.clickRightFinish = clickRightFinish;
    }

    public Button getLeftBtn() {
        return leftBtn;
    }

    public void setLeftBtn(Button leftBtn) {
        this.leftBtn = leftBtn;
    }

    public Button getRightBtn() {
        return rightBtn;
    }

    public void setRightBtn(Button rightBtn) {
        this.rightBtn = rightBtn;
    }
}
