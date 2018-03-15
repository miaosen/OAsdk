package com.osui.view.tiplayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.osui.R;
import com.osui.view.dialog.FrameDialog;


/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @git http://git.oschina.net/miaosen/MyUtils
 * @CreateDate 2017-11-23  15:04
 * @Descrition
 */

public class TipLayout extends ViewGroup {

    int y = 0;

    int juli = 0;

    /**
     * 如果包裹最外层不是可以滚动的view(比如listview被外面一层自定义view包裹起来),
     * 可以将里面的listview设置为canScrollView就能兼容滑动事件了
     */
    View canScrollView;

    TipHeadLayout headView;
    FrameDialog tipLayoutFooter;


    View tailView, contentView;

    //下拉阻力系统，下拉距离越远阻力越小
    int speedRatio = 3;

    boolean autoLoadMore = true;

    boolean isRefreshing = false;


    boolean isLoadMoreNow = false;


    boolean noHasFooterView = true;

    //public OnRefreshListener onRefreshListener;
    //
    //public OnLoadMoreListener onLoadMoreListener;

    public OnTipListener onTipListener;


    public TipLayout(Context context) {
        super(context);

    }

    public TipLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        //headView = ViewUtils.inflatView(getContext(), R.layout.tip_layout_head);
        headView = new TipHeadLayout(getContext());
        headView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        this.addView(headView);
        //tailView = ViewUtils.inflatView(getContext(), R.layout.sp_view_mid_product_item);
        //this.addView(tailView, 1);
        tipLayoutFooter = new FrameDialog(getContext(), R.layout.tip_layout_footer);
        tipLayoutFooter.dialogView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        tipLayoutFooter.setShadow(false);
        tipLayoutFooter.setFocuse(false);
        tipLayoutFooter.setBackgroundColor(getResources().getColor(R.color.transparent));
        tipLayoutFooter.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int height = 0, width = 0;
        View child = getChildAt(getChildCount() - 1);
        int childWidth = child.getMeasuredWidth();
        int childHeight = child.getMeasuredHeight();
        if (heightMode == MeasureSpec.EXACTLY) {// match_parent或者具体值
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {// wrap_content
            height = childHeight;
        }
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = childWidth;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //头部
        int headHeight = headView.getMeasuredHeight();
        //头部
        //int tailHeight = tailView.getMeasuredHeight();
        //子项布局
        if (contentView == null) {
            contentView = getChildAt(1);
        }
        if (canScrollView == null) {
            canScrollView = contentView;
        }
        int measuredHeight = contentView.getMeasuredHeight();
        if ((juli >= 0 && juli > headHeight)) {
            juli = headHeight;
        }
        //if (juli <= 0 && Math.abs(juli) > tailHeight) {
        //    juli = -tailHeight;
        //}
        headView.layout(l, -headHeight + juli, r, juli);
        contentView.layout(l, juli, r, measuredHeight + juli);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                y = (int) event.getY();
                //L.i("=========ACTION_DOWN==============" + y);
                break;
            case MotionEvent.ACTION_CANCEL:
                //L.i("=========ACTION_CANCEL==============");
                sendCancelEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:
                //必须要在MOVE中return有效果，在这里return后UP事件也会被拦截
                int mJuli = (int) ((event.getY() - y) / speedRatio);
                speedRatio = speedRatio - (mJuli / 100);
                //L.i("=========ACTION_MOVE==============" + mJuli + " " + speedRatio);
                y = (int) event.getY();
                if ((!canChildScrollUp(canScrollView) && mJuli > 0) || isRefreshing) {//下拉
                    juli = juli + mJuli;
                    requestLayout();
                    if (juli <= 0) {
                        isRefreshing = false;
                    } else {
                        isRefreshing = true;
                    }
                    sendDownEvent(event);
                } else if ((!canChildScrollDown(canScrollView) && mJuli < 0)) {
                    if (!isLoadMoreNow && !isRefreshing) {
                        if (onTipListener != null) {
                            onTipListener.onLoadMore();
                            load();
                        }
                        isLoadMoreNow = true;
                    }
                } else {
                    if (juli > 0) {
                        requestLayout();
                    }
                    return super.dispatchTouchEvent(event);
                }
                return super.dispatchTouchEvent(event);
            case MotionEvent.ACTION_UP:
                if (juli <= headView.getMeasuredHeight() / 4 * 3) {
                    endRefresh();
                } else {
                    refresh();
                    if (onTipListener != null) {
                        onTipListener.onRefresh();
                    }
                }
                speedRatio = 3;
        }
        return super.dispatchTouchEvent(event);
    }

    public void addFooterView() {
        if (noHasFooterView) {
            if (canScrollView instanceof ListView) {
                final ListView listview = (ListView) canScrollView;
                listview.setOnScrollListener(new AbsListView.OnScrollListener() {
                    int scrollState;
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                        this.scrollState=scrollState;
                    }
                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        int lastVisiblePosition = view.getLastVisiblePosition();
                        if (totalItemCount != 0
                                && totalItemCount > visibleItemCount
                                && totalItemCount == lastVisiblePosition + 1) {
                            if (!isLoadMoreNow && !isRefreshing) {
                                if (onTipListener != null) {
                                    onTipListener.onLoadMore();
                                    load();
                                }
                                isLoadMoreNow = true;
                            }
                        }
                    }
                });
            }
            noHasFooterView = false;
        }
    }

    public void load() {
        tipLayoutFooter.showAsCoverUp(this);
    }

    public void error() {
        error("出错了！点击重试");
    }

    public void error(String text) {
        isRefreshing = true;
        showHead();
        headView.error(text);

    }

    public void end() {
        endLoadMore();
        endRefresh();
    }


    public void endLoadMore() {
        isLoadMoreNow = false;
        tipLayoutFooter.dismiss();
    }

    /**
     * 结束刷新
     */
    public void endRefresh() {
        //L.i("=========endRefresh==============");
        isRefreshing = false;
        if (juli < 0) {
            juli = 0;
        } else if (juli != 0) {
            juli = juli - 10;
        }
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (juli > 0 && !isRefreshing) {
                    requestLayout();
                    endRefresh();
                }
            }
        }, 10);
        if (headView.attached()) {
            headView.start();
        }
    }

    /**
     * 开始刷新
     */
    public void refresh() {
        isRefreshing = true;
        showHead();
        if (headView.attached()) {
            headView.refreshing();
        }

    }


    public void showHead() {
        if (juli > headView.getMeasuredHeight()) {
            juli = headView.getMeasuredHeight();
        } else if (juli != headView.getMeasuredHeight()) {
            juli = juli + 10;
        }
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (juli < headView.getMeasuredHeight() && isRefreshing) {
                    requestLayout();
                    showHead();
                }
            }
        }, 10);
    }


    private void sendDownEvent(MotionEvent mLastMoveEvent) {
        final MotionEvent last = mLastMoveEvent;
        MotionEvent e = MotionEvent.obtain(last.getDownTime(), last.getEventTime(), MotionEvent.ACTION_DOWN, last.getX(), last.getY(), last.getMetaState());
        super.dispatchTouchEvent(e);
    }

    private void sendCancelEvent(MotionEvent event) {
        if (event == null) {
            return;
        }
        MotionEvent last = event;
        MotionEvent e = MotionEvent.obtain(last.getDownTime(), last.getEventTime() + ViewConfiguration.getLongPressTimeout(), MotionEvent.ACTION_CANCEL, last.getX(), last.getY(), last.getMetaState());
        super.dispatchTouchEvent(e);
    }

    public boolean canChildScrollUp(View view) {
        return view.canScrollVertically(-1);
    }

    /**
     * 超出范围停止移动
     *
     * @param mJuli
     * @param event
     */
    protected void stopMove(int mJuli, MotionEvent event) {
        if ((canChildScrollDown(canScrollView) && mJuli < 0 && juli < 0) || (canChildScrollUp(canScrollView) && mJuli > 0 && juli > 0)) {
            onInterceptTouchEvent(event);
        }
    }

    //public interface OnRefreshListener {
    //    void onRefresh();
    //}
    //
    //public interface OnLoadMoreListener {
    //    void onLoadMore();
    //}

    public interface OnTipListener {
        void onRefresh();

        void onLoadMore();
    }


    public boolean canChildScrollDown(View view) {
        return view.canScrollVertically(1);
    }


    public View getCanScrollView() {
        return canScrollView;
    }

    public void setCanScrollView(View canScrollView) {
        this.canScrollView = canScrollView;
    }

    public TipHeadLayout getHeadView() {
        return headView;
    }

    public void setHeadView(TipHeadLayout headView) {
        this.headView = headView;
    }

    public View getTailView() {
        return tailView;
    }

    public void setTailView(View tailView) {
        this.tailView = tailView;
    }

    //public OnRefreshListener getOnRefreshListener() {
    //    return onRefreshListener;
    //}
    //
    //public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
    //    this.onRefreshListener = onRefreshListener;
    //}
    //
    //public OnLoadMoreListener getOnLoadMoreListener() {
    //    return onLoadMoreListener;
    //}
    //
    //public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
    //    this.onLoadMoreListener = onLoadMoreListener;
    //}


    public OnTipListener getOnTipListener() {
        return onTipListener;
    }

    public void setOnTipListener(OnTipListener onTipListener) {
        this.onTipListener = onTipListener;
    }

    public void setOnErrorViewCilckListener(OnClickListener onClickListener) {
        headView.error_in_head.setOnClickListener(onClickListener);
    }
}
