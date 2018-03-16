package com.oaui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.oaui.R;


/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @git http://git.oschina.net/miaosen/MyUtils
 * @CreateDate 2016-10-18 14:58
 * @Descrition
 */
public class FlowLayout extends ViewGroup {


    //行距离
    int lineMargin = 10;
    //列距离
    int columnMargin = 10;

    //int viewHeight=0;
    //int viewWidth=0;

    BaseAdapter adapter;

    OnItemClickListener onItemClickListener;


    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttributes(context, attrs);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /**
     * 解析从XML传递给视图的属性
     *
     * @param context
     * @param attrs
     */
    private void parseAttributes(Context context, AttributeSet attrs) {
        TypedArray typeArray = context.obtainStyledAttributes(attrs,
                R.styleable.FlowLayout);
        lineMargin = typeArray.getDimensionPixelOffset(R.styleable.FlowLayout_lineMargin, lineMargin);
        columnMargin = typeArray.getDimensionPixelOffset(R.styleable.FlowLayout_columnMargin, columnMargin);
        typeArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //L.i("=========onMeasure==============");
        //调用子view测量方法
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        // 获得它的父容器为它设置的测量模式和大小
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        //TODO 作用还不是很清楚，如果不进行下面操作，scrollview中会显示异常
        //一行子view累加宽度
        int childsWith = 0;
        //一列子view累加高度
        int childsHeight = 0;
        //子View X坐标
        int childX = lineMargin;
        //子View Y坐标
        int childY = columnMargin;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if(child.getVisibility()==VISIBLE){
                //当前子View的宽度和高度
                int childWith = child.getMeasuredWidth();
                // L.i("=========onMeasure==============" + childWith + "  " + getMeasuredWidth());
                //TODO 如果子view是RelativeLayout布局并且高度为wrap_content时，childHeight的高度异常
                int childHeight = child.getMeasuredHeight();
                // L.i("=========childHeight=============getMeasuredHeight===" + childHeight + child.getTag());
                if (childsWith + childWith + lineMargin <= getMeasuredWidth()) {//累加宽度不超过ViewGroup的宽度，排在当前行后面
                    childX = childsWith + lineMargin;
                    childsWith = childX + childWith;
                    childsHeight = childY + childHeight;
                } else {//切换到下一行
                    // L.i("=========onMeasure==============" + i);
                    childX = lineMargin;
                    childsWith = childX + childWith;
                    childY = childY + childHeight + columnMargin;
                    childsHeight = childY + childHeight;
                }
            }
        }
        //L.i("=========onMeasure==============" + childsHeight);
        setMeasuredDimension((modeWidth == MeasureSpec.EXACTLY) ? sizeWidth
                : childsWith, (modeHeight == MeasureSpec.EXACTLY) ? sizeHeight
                : childsHeight);


    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //L.i("=========onLayout==============" + changed);
        //一行子view累加宽度
        int childsWith = 0;
        //一列子view累加高度
        int childsHeight = 0;
        //子View X坐标
        int childX = lineMargin;
        //子View Y坐标
        int childY = columnMargin;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if(child.getVisibility()==VISIBLE){
                //MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();
                //L.i("=========view=============="+t+" "+l+" "+r+" "+b+" "+layoutParams.rightMargin);
                //当前子View的宽度和高度
                int childWith = child.getMeasuredWidth();
                int childHeight = child.getMeasuredHeight();
                // L.i("=========childHeight==============" + childHeight);
                if (childsWith + childWith + (1.5 * lineMargin) <= getWidth()) {//累加宽度不超过ViewGroup的宽度，排在当前行后面
                    childX = childsWith + lineMargin;
                    childsWith = childX + childWith;
                    childsHeight = childY + childHeight;
                } else {//切换到下一行

                    childX = lineMargin;
                    childsWith = childX + childWith;
                    childY = childY + childHeight + columnMargin;
                    childsHeight = childY + childHeight;
                }
                child.layout(childX, childY, childsWith, childsHeight);
                final int finalI = i;
                if (onItemClickListener != null) {
                    child.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onItemClickListener.onItemClick(finalI);
                        }
                    });
                }
            }
        }
        //viewWidth=getMeasuredWidth();
        //viewHeight=childsHeight;

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    //使notifyDataSetChanged生效
    private DataSetObserver mObserver = new DataSetObserver() {
        public void onChanged() {
            addAllView();
        }
    };

    public void setAdapter(BaseAdapter adapter) {
        if (this.adapter == null) {
            adapter.registerDataSetObserver(mObserver);
        }
        this.adapter = adapter;
        addAllView();
    }

    private void addAllView() {
        this.removeAllViews();
        for (int i = 0; i < adapter.getCount(); i++) {
            View v = adapter.getView(i, null, null);
            final int finalI = i;
            //L.i("=========setAdapter==============v" + v);
            v.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(finalI);
                    }
                }
            });
            addView(v);
        }
    }


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
