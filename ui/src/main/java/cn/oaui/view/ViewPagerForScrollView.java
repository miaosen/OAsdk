package cn.oaui.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 自动适应高度的ViewPager
 *
 * @author
 */
public class ViewPagerForScrollView extends ViewPager {
    //是否可以滑动
    private boolean canScroll = true;

    public ViewPagerForScrollView(Context context) {
        super(context);
    }

    public ViewPagerForScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if ((MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED)) {
            int height = 0;
            int width=0;
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                int h = child.getMeasuredHeight();
                if (h > height) {
                    height = h;
                }
                int w = child.getMeasuredWidth();
                if (w > width) {
                    width = w;
                }
            }
            //widthMeasureSpec= MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
            heightMeasureSpec= MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if(!canScroll){
            return false;
        }else{
            return super.onInterceptTouchEvent(arg0);
        }

    }



    public boolean isCanScroll() {
        return canScroll;
    }

    public void setCanScroll(boolean canScroll) {
        this.canScroll = canScroll;
    }
}