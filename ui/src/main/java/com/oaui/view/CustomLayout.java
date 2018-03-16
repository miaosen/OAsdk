package com.oaui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.oaui.utils.ViewUtils;


/**
 * @Created by gzpykj.com
 * @author zms
 * @Date 2017-5-10
 * @Descrition 自定义布局
 */
public abstract class CustomLayout extends ViewGroup {

	public View contentView;

	public Context context;
	//是否第一次显示
	public Boolean isFirstLoad = true;
	//子view是否被加载
	public Boolean isAttached = false;

	AttributeSet attrs;

	public CustomLayout(Context context) {
		super(context);
		this.context=context;
		addLayout();
	}

	public CustomLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;
		this.attrs=attrs;
		addLayout();
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (isFirstLoad&&!isInEditMode()) {
			initData();
		}
		isFirstLoad = false;
	}

	public abstract void initData();

	private void addLayout() {
		int setXmlLayout = setXmlLayout();
		if (setXmlLayout > 0) {
			contentView = ViewUtils.inflatView(getContext(), setXmlLayout);
			addView(contentView);
			contentView.setLayoutParams(new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT));
			isAttached=true;
			onCreateView();
		} else {
			// 抛异常
		}
	}


	/**
	 * 一刀构造器就执行，可能高度和宽度还是为0
	 */
	protected abstract void onCreateView();

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		measureChildren(widthMeasureSpec, heightMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int height = 0, width = 0;
		View child = getChildAt(0);
		int childWidth = child.getMeasuredWidth();
		int childHeight = child.getMeasuredHeight();

		if (heightMode == MeasureSpec.EXACTLY) {// match_parent或者具体值
			height = heightSize;
		} else  {// wrap_content if (heightMode == MeasureSpec.AT_MOST)
			height = childHeight;
		}
		if (widthMode == MeasureSpec.EXACTLY) {
			width = widthSize;
		} else { //if (widthMode == MeasureSpec.AT_MOST)
			width = childWidth;
		}
		//当前容器大小
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		View childAt = getChildAt(0);
		int childHeight = childAt.getMeasuredHeight();
		int childWidth = childAt.getMeasuredWidth();
		childAt.layout(0, 0, childWidth, childHeight);
	}

	public abstract int setXmlLayout();

	public View getContentView() {
		return contentView;
	}

	public AttributeSet getAttrs() {
		return attrs;
	}

	public void setAttrs(AttributeSet attrs) {
		this.attrs = attrs;
	}
}
