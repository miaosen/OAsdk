package cn.oaui.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;

import cn.oaui.L;
import cn.oaui.R;
import cn.oaui.ResourceHold;
import cn.oaui.utils.AppUtils;
import cn.oaui.utils.StringUtils;
import cn.oaui.utils.ViewUtils;


public class ClearableEditText extends EditText
        implements EditText.OnFocusChangeListener {

    public static final String TAG = "ClearableEditText";

    public ClearableEditText(Context context) {
        this(context, null);
    }

    public ClearableEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public ClearableEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }
    Drawable drawableRight;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ClearableEditText(Context context, AttributeSet attrs, int defStyleAttr, int
            defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Right Drawable 是否可见
     */
    private boolean mIsClearVisible;

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int
            defStyleRes) {
         drawableRight = ResourceHold.getDrawable(R.mipmap.icon_clear);

        final Resources.Theme theme = context.getTheme();

        TypedArray a = theme.obtainStyledAttributes(attrs, R.styleable.ClearableEditText,
                defStyleAttr, defStyleRes);

        int rightDrawableColor = a.getColor(R.styleable.ClearableEditText_right_drawable_color,
                Color.BLACK);
        a.recycle();
        // 给mRightDrawable上色
        //DrawableCompat.setTint(drawableRight, rightDrawableColor);
        drawableRight.setBounds(0, 0, AppUtils.dp2px(20), AppUtils.dp2px(20));
        setCompoundDrawables(null, null, drawableRight, null);
        setOnFocusChangeListener(this);
        // 添加TextChangedListener
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged " + s);
                if(isFocused()){
                    setClearDrawableVisible(s.length() > 0);
                }

            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        // 第一次隐藏
        setClearDrawableVisible(false);
        setKeyboardListener();
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtils.showKeyboard(ClearableEditText.this);
            }
        });
    }


    private void setKeyboardListener() {
        Activity activity= (Activity) getContext();
        final View rootView = activity.getWindow().getDecorView();
        final int[] rootViewVisibleHeight = {0};
        //监听视图树中全局布局发生改变或者视图树中的某个视图的可视状态发生改变
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //获取当前根视图在屏幕上显示的大小
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int visibleHeight = r.height();
                if (rootViewVisibleHeight[0] == 0) {
                    rootViewVisibleHeight[0] = visibleHeight;
                    return;
                }
                //根视图显示高度没有变化，可以看作软键盘显示／隐藏状态没有改变
                if (rootViewVisibleHeight[0] == visibleHeight) {
                    return;
                }
                //根视图显示高度变小超过200，可以看作软键盘显示了
                if (rootViewVisibleHeight[0] - visibleHeight > 200) {
                    rootViewVisibleHeight[0] = visibleHeight;
                    requestFocus();
                    setFocusable(true);
                    setCursorVisible(true);
                    String text = getText()+"";
                    if(StringUtils.isNotEmpty(text)){
                        setSelection(text.length());
                    }
                    return;
                }
                //根视图显示高度变大超过200，可以看作软键盘隐藏了
                if (visibleHeight - rootViewVisibleHeight[0] > 200) {
                    rootViewVisibleHeight[0] = visibleHeight;
                    clearFocus();
                    setFocusable(false);
                    setCursorVisible(false);
                    setClearDrawableVisible(false);
                    return;
                }
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // error drawable 不显示 && clear drawable 显示 && action up
        if (getError() == null && mIsClearVisible && event.getAction() == MotionEvent.ACTION_UP) {

            float x = event.getX();
            if (x >= getWidth() - getTotalPaddingRight() && x <= getWidth() - getPaddingRight()) {
                Log.d(TAG, "点击清除按钮！");
                clearText();

            }
        }
        return super.onTouchEvent(event);
    }


    /**
     * 清空输入框
     */
    private void clearText() {
        if (getText().length() > 0) {
            setText("");
        }
    }


    /**
     * 设置Right Drawable是否可见
     *
     * @param isVisible true for visible , false for invisible
     */
    public void setClearDrawableVisible(boolean isVisible) {
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1],
                isVisible ? drawableRight : null, getCompoundDrawables()[3]);
        mIsClearVisible = isVisible;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        // error drawable 不显示的时候
        if (getError() == null) {
            if (hasFocus) {
                if (getText().length() > 0) {
                    setClearDrawableVisible(true);
                }
            } else {
                setClearDrawableVisible(false);
            }
        }
    }

    @Override
    public void setError(CharSequence error, Drawable icon) {
        if (error != null) {
            setClearDrawableVisible(true);
        }
        super.setError(error, icon);
    }
}