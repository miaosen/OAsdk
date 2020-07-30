package cn.oaui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.List;

import cn.oaui.R;
import cn.oaui.data.Row;
import cn.oaui.view.listview.BaseFillAdapter;


/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @git http://git.oschina.net/miaosen/MyUtils
 * @CreateDate 2016-10-18 14:58
 * @Descrition
 */
public class FlowLayout extends ViewGroup {

    //TODO 间距不正常
    //TODO 居中属性,子view隐藏时不计算空间

    ////行距离
    //int lineMargin =MetricsUtils.dip2px( 10);
    ////控件之间的距离
    //int columnMargin =MetricsUtils.dip2px( 10);
    //行距离
    int lineMargin = 10;
    //控件之间的距离
    int columnMargin = 10;
    //int viewHeight=0;
    //int viewWidth=0;

    int paddingLeft, paddingTop, paddingRight, paddingBottom;

    BaseFillAdapter adapter;

    OnItemClickListener onItemClickListener;

    List<Row> rowsPosition = new LinkedList<>();

    int gravity = Gravity.NO_GRAVITY;


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
        //columnMargin= MetricsUtils.dip2px(columnMargin);
        //lineMargin= MetricsUtils.dip2px(lineMargin);
        typeArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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
        int childX = columnMargin + paddingLeft;
        //子View Y坐标
        int childY = lineMargin + paddingTop;

        paddingLeft = getPaddingLeft();
        paddingTop = getPaddingTop();
        paddingRight = getPaddingRight();
        paddingBottom = getPaddingBottom();

        int childCount = getChildCount();
        int lastLineHeight = 0;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            //当前子View的宽度和高度
            int childWith = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            if (i == 0) {
                lastLineHeight = childHeight;
                childsHeight = childY + childHeight + lineMargin;
                childsWith = childX + childWith;
            } else if (childsWith + childWith + (1.5 * lineMargin) <= getMeasuredWidth()) {//累加宽度不超过ViewGroup的宽度，排在当前行后面
                if (childHeight > lastLineHeight) {
                    childsHeight = childsHeight - lastLineHeight + childHeight;
                    lastLineHeight = childHeight;
                }
                childX = childsWith + lineMargin;
                childsWith = childX + childWith;
            } else {//切换到下一行
                childX = columnMargin + paddingLeft;
                childsWith = childX + childWith;
                childY = childY + lastLineHeight + lineMargin;
                childsHeight = childHeight + childY + lineMargin;
                lastLineHeight = childHeight;
            }
            if (i == childCount - 1) {
                childsHeight = childsHeight + paddingBottom;
                childsWith = childsWith + paddingRight;
            }
        }

        setMeasuredDimension((modeWidth == MeasureSpec.EXACTLY) ? sizeWidth
                : childsWith, (modeHeight == MeasureSpec.EXACTLY) ? sizeHeight
                : childsHeight);

    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //一行子view累加宽度
        int childsWith = 0;
        //一列子view累加高度
        int childsHeight = 0;
        //子View X坐标
        int childX = columnMargin + paddingLeft;
        //子View Y坐标
        int childY = lineMargin + paddingTop;
        int childCount = getChildCount();
        int lastLineHeight = 0;
        rowsPosition.clear();
        int line = 0;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            //当前子View的宽度和高度
            int childWith = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            Row row = new Row();
            if (i == 0) {
                lastLineHeight = childHeight;
                childsHeight = childY + childHeight;
                childsWith = childX + childWith;
            } else if (childsWith + childWith + (1.5 * lineMargin) <= getMeasuredWidth()) {//累加宽度不超过ViewGroup的宽度，排在当前行后面
                if (childHeight > lastLineHeight) {
                    childsHeight = childsHeight - lastLineHeight + childHeight;
                    lastLineHeight = childHeight;
                }
                childsHeight = childY + childHeight;
                childX = childsWith + lineMargin;
                childsWith = childX + childWith;
            } else {//切换到下一行
                line = line + 1;
                childX = columnMargin + paddingLeft;
                childsWith = childX + childWith;
                childY = childY + lastLineHeight + lineMargin;
                childsHeight = childHeight + childY;
                lastLineHeight = childHeight;
            }
            row.put("line", line);
            row.put("child", child);
            row.put("childX", childX);
            row.put("childY", childY);
            row.put("childsWith", childsWith);
            row.put("childsHeight", childsHeight);
            rowsPosition.add(row);
        }
        setGravity();
        for (int i = 0; i < rowsPosition.size(); i++) {
            Row row = rowsPosition.get(i);
            View child = (View) row.get("child");
            int x = (int) row.get("childX");
            int y = (int) row.get("childY");
            int w = (int) row.get("childsWith");
            int h = (int) row.get("childsHeight");
            int li = (int) row.get("line");
            //L.i("=========onLayout==============" + x);
            child.layout(x, y, w, h);
            //final int finalI = i;
            //if (onItemClickListener != null) {
            //    child.setOnClickListener(new OnClickListener() {
            //        @Override
            //        public void onClick(View v) {
            //            onItemClickListener.onItemClick(finalI);
            //        }
            //    });
            //}
        }
    }

    private void setGravity() {
        //按照第一行居中，主要是针对九宫格对齐的情况
        if (gravity == Gravity.CENTER_HORIZONTAL) {
            int width = getMeasuredWidth();
            int lastLi=0;
            int index=0;
            Integer  space=0;
            for (int i = 0; i < rowsPosition.size(); i++) {
                Row row = rowsPosition.get(i);
                int w = (int) row.get("childsWith");
                int li = (int) row.get("line");
                if (li != lastLi) {
                    //上一行的缩进长度
                    //for (int j = i-index; j < i; j++) {
                    //    RowObject row = rowsPosition.get(j);
                    //    row.put("paddingSpace",space);
                    //}
                    //lastLi=li;
                    //index=1;
                    //rowObject.put("paddingSpace",space);
                }else{
                    //rowObject.put("paddingSpace",space);
                    space=width-w;
                    //index=index+1;
                }
                //L.i("=========setGravity=============="+li+"  "+index+"  "+i+"  "+space);
            }
            //width=width-lastW;
            for (int i = 0; i < rowsPosition.size(); i++) {
                Row row = rowsPosition.get(i);
                //L.i("=========setGravity=============="+i);
                //int paddingSpace = (int) rowObject.get("paddingSpace");
                int x = (int) row.get("childX");
                row.put("childX", x + space/2);
                int w = (int) row.get("childsWith");
                row.put("childsWith", w + space/2);
            }
        }
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

    public void setAdapter(BaseFillAdapter adapter) {
        if (this.adapter == null) {
            adapter.registerDataSetObserver(mObserver);
        }
        this.adapter = adapter;
        addAllView();
        //adapter.notifyDataSetChanged();
    }

    private void addAllView() {
        this.removeAllViews();
        for (int i = 0; i < adapter.getCount(); i++) {
            View v = adapter.getView(i, null, null);
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

    public int getGravity() {
        return gravity;
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }
}
