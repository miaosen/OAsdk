package cn.oaui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.oaui.R;
import cn.oaui.annotation.InjectReader;
import cn.oaui.annotation.ViewInject;
import cn.oaui.utils.StringUtils;
import cn.oaui.utils.ViewUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2018-03-20  16:15
 * @Descrition 折叠布局
 */
public class FoldingLayout extends ViewGroup implements View.OnClickListener {

    int headLayout = R.layout.ui_folding_layout_header;
    View headView;
    int footerLayout = R.layout.ui_folding_layout_footer;
    View footerView;

    int contentLayout = R.layout.ui_folding_layout_content;
    LinearLayout contentView;
    List<View> childs = new LinkedList<View>();
    List<View> parentChilds = new LinkedList<View>();

    @ViewInject
    ImageView img_jintou, img_icon;
    @ViewInject
    TextView tv_footer, tv_header_title;

    String title;
    Drawable icon;

    //List<View> contentViews=new LinkedList<View>();

    int contentViewHeight = 0;

    public FoldingLayout(Context context) {
        super(context);
    }

    public FoldingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttr(attrs);
        headView = ViewUtils.inflatView(context, headLayout);
        footerView = ViewUtils.inflatView(context, footerLayout);
        contentView = (LinearLayout) ViewUtils.inflatView(context, contentLayout);

        addView(headView);
        addView(contentView);
        addView(footerView);
        if (!isInEditMode()) {
            InjectReader.injectAllFields(this);
            headView.setOnClickListener(this);
            footerView.setOnClickListener(this);
            if (StringUtils.isNotEmpty(title)) {
                tv_header_title.setText(title);
            }
            if (icon != null) {
                img_icon.setImageDrawable(icon);
            }
        }

    }

    private void parseAttr(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.FoldingLayout);
        title = typedArray.getString(R.styleable.FoldingLayout_title);
        icon = typedArray.getDrawable(R.styleable.FoldingLayout_icon);
        typedArray.recycle();
    }

    @Override
    public void addView(View child) {
        super.addView(child);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        //contentViews.clear();
        if (childs.size() == 0) {
            for (int i = 0; i < getChildCount(); i++) {
                View childAt = getChildAt(i);
                if (childAt != headView && childAt != footerView&&childAt!=contentView) {
                    //removeView(childAt);
                    //contentView.addView(childAt);
                    childs.add(childAt);
                    contentViewHeight = childAt.getMeasuredHeight();
                }

            }
            for (int i = 0; i < childs.size(); i++) {
                View childAt = childs.get(i);
                //L.i("=========onMeasure=============="+childAt);
                removeView(childAt);
                contentView.addView(childAt);
            }
            parentChilds.add(headView);
            parentChilds.add(contentView);
            parentChilds.add(footerView);
        }
        int height = 0, width = 0;
        //L.i("=========onMeasure=============="+childs.size());
        for (int i = 0; i < parentChilds.size(); i++) {
            View child = parentChilds.get(i);
            if (child.getVisibility() == VISIBLE) {
                int childWidth = child.getMeasuredWidth();
                //不测量高度可能为空或者不准确
                child.measure(widthMeasureSpec,heightMeasureSpec);
                int childHeight = child.getMeasuredHeight();
                if (heightMode != MeasureSpec.EXACTLY) {
                    height = height + childHeight;
                }else{
                    height = height + heightSize;
                }
                if (widthMode != MeasureSpec.EXACTLY) {
                    if (childWidth > width) {
                        width = childWidth;
                    }
                }
            }
        }
        setMeasuredDimension((widthMode == MeasureSpec.EXACTLY) ? widthSize
                : width, (heightMode == MeasureSpec.EXACTLY) ? heightSize
                : height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int height = 0;

        for (int i = 0; i < parentChilds.size(); i++) {
            View childAt = parentChilds.get(i);
            if (childAt.getVisibility() == VISIBLE) {
                int childHeight = childAt.getMeasuredHeight();
                childAt.layout(0, height, getMeasuredWidth(), height + childHeight);
                height = height + childHeight;
            }
        }
    }

    @Override
    public void onClick(View v) {
        //L.i("============onClick===========1");
        if (v == footerView || v == headView) {
            //L.i("============onClick===========2");
            //for (int i = 0; i < parentChilds.size(); i++) {
            //    View childAt = parentChilds.get(i);
            //    if (childAt != headView && childAt != footerView) {
                    if (contentView.getVisibility() == View.VISIBLE) {
                        //hideViewAnm(contentView, 0);
                        contentView.setVisibility(GONE);
                        //for (int j = 0; j < contentViews.size(); j++) {
                        //    contentViews.get(j).setVisibility(GONE);
                        //}
                        tv_footer.setText("展开");
                        img_jintou.setImageDrawable(getResources().getDrawable(R.drawable.icon_jiantouxia));
                    } else {
                        contentView.setVisibility(VISIBLE);
                        //showViewAnm(contentView, 0);
                        //for (int j = 0; j < contentViews.size(); j++) {
                        //    contentViews.get(j).setVisibility(VISIBLE);
                        //}
                        tv_footer.setText("收回");
                        img_jintou.setImageDrawable(getResources().getDrawable(R.drawable.icon_jiantoushang));
                    }
            //    }
            //}
        }
    }


    public void hideViewAnm(final View view, final int index) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                final ViewGroup.LayoutParams params = view.getLayoutParams();
                params.height = view.getMeasuredHeight() - index;
                view.requestLayout();
                if (view.getMeasuredHeight() - index > 0) {
                    hideViewAnm(view, index + 50);
                } else {
                    params.height = 0;
                    view.setVisibility(GONE);
                    view.requestLayout();

                }
            }
        }, 2);
    }

    public void showViewAnm(final View view, final int index) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                final ViewGroup.LayoutParams params = view.getLayoutParams();
                params.height = index;
                view.requestLayout();
                if (contentViewHeight - index > 0) {
                    showViewAnm(view, index + 100);
                } else {
                    params.height = contentViewHeight;
                    view.setVisibility(VISIBLE);
                    view.requestLayout();

                }
            }
        }, 2);
    }


}