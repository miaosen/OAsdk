package com.oaui.view.calendar;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oaui.L;
import com.oaui.R;
import com.oaui.annotation.InjectReader;
import com.oaui.annotation.ViewInject;
import com.oaui.data.RowObject;
import com.oaui.utils.StringUtils;
import com.oaui.utils.ViewUtils;
import com.oaui.view.CustomLayout;
import com.oaui.view.ViewPagerForScrollView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2018-04-15  11:38
 * @Descrition
 */

public class CalendarLayout extends CustomLayout {

    @ViewInject
    ViewPagerForScrollView viewPager;
    PagerAdapter adapter;

    int day = 0, month = CalendarDataFactory.getCurMonth(), year =CalendarDataFactory.getCurYear();

    //List<DateView> dateViews=new LinkedList<>();
    Map<Integer, DateView> mapDateView = new HashMap<Integer, DateView>();
    int showIndex = 1000;
    int curPosition = -1;
    @ViewInject
    TextView tv_year, tv_month, tv_day;
    //是否初始化今天
    boolean isSetToday = false;

    public CalendarLayout(Context context) {
        super(context);
        //initData();
    }

    public CalendarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        //initData();
    }

    @Override
    public void initData() {
        //addView(ViewUtils.inflatView(getContext(), R.layout.ui_view_calendar_layout));
        //setOrientation(LinearLayout.VERTICAL);
        //InjectReader.injectAllFields(this);
        for (int i = 0; i < 5; i++) {
            //DateView dateView = new DateView(getContext());
            DateView dateView = (DateView) ViewUtils.inflatView(getContext(), R.layout.ui_view_calendar_date_view, viewPager, false);
            //dateView.setBackgroundColor(Color.GRAY);
            mapDateView.put(showIndex + i, dateView);
        }
        adapter = new MyPagerAdapter();
        //viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(final int position) {
                DateView dateView = mapDateView.get(position);
                dateView.setSelectDay(day);
                RowObject selectItemData = dateView.getSelectItemData();
                if (selectItemData != null) {
                    setDateText(dateView.getSelectItemData());

                }
                initCacheView();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(showIndex + 2);
        tv_year.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DateView dateView = mapDateView.get(viewPager.getCurrentItem());
                dateView.showYearView();
                initCacheView();
            }
        });
        tv_month.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DateView dateView = mapDateView.get(viewPager.getCurrentItem());
                dateView.showMonthView();
                viewPager.setCanScroll(false);
            }
        });

    }

    private void initCacheView() {
        DateView dateView = mapDateView.get(viewPager.getCurrentItem());
        DateView dateView1 = mapDateView.get(viewPager.getCurrentItem() + 1);
        DateView dateView2 = mapDateView.get(viewPager.getCurrentItem() - 1);
        if( dateView.getShowType()==2){
           dateView1.showYearView();
           dateView2.showYearView();
            dateView1.setCurrentMonthInfo(
                    CalendarDataFactory.getNextMonthPageInfo(year+12, month));
            dateView2.setCurrentMonthInfo(
                    CalendarDataFactory.getLastMonthPageInfo(year-12, month));
       }else{
            dateView1.showDateView();
            dateView2.showDateView();
            dateView1.setCurrentMonthInfo(
                    CalendarDataFactory.getNextMonthPageInfo(year, month));
            dateView2.setCurrentMonthInfo(
                    CalendarDataFactory.getLastMonthPageInfo(year, month));
        }


    }


    @Override
    protected void onCreateView() {
        InjectReader.injectAllFields(this);

    }

    @Override
    public int setXmlLayout() {
        return R.layout.ui_view_calendar_layout;
    }

    class MyPagerAdapter extends PagerAdapter {
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        //对超出范围的资源进行销毁
        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            //构建链条
            DateView view = mapDateView.get(position);
            container.removeView(view);
            if (position < viewPager.getCurrentItem()) {//左滑
                //mapDateView.remove(position-1);
                mapDateView.put(position + 4, view);
            } else {//右滑
                //mapDateView.remove(position+1);
                mapDateView.put(position - 4, view);
            }
            L.i("=destroyItem===" + position + "  " + viewPager.getCurrentItem() + mapDateView);
            //dateViews.remove(view);
            //dateViews.add(view);
        }

        //对显示的资源进行初始化
        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            //DateView view = dateViews.get(position);
            DateView dateView = mapDateView.get(position);
            //int sum = position - 2 - showIndex;
            int direct = curPosition - position;
            //L.i("=========instantiateItem==============" + sum + "  " + direct);
            //List<RowObject> pageCalendarInfoRangeMonth = CalendarDataFactory.getPageCalendarInfoRangeMonth(sum);
          if(position==showIndex+2){
              List<RowObject> pageCalendarInfoRangeMonth = CalendarDataFactory.getPageCalendarInfo(year,month);
              dateView.setCurrentMonthInfo(pageCalendarInfoRangeMonth);
              if (CalendarDataFactory.isToday(year,month,CalendarDataFactory.getCurDay())) {
                  dateView.setSelectDay(Calendar.getInstance().get(Calendar.DATE));
                  setDateText(pageCalendarInfoRangeMonth.get(dateView.getSelectIndex()));
                  isSetToday = true;
              }
          }
            dateView.setOnDateCheckedListener(new DateView.OnDateCheckedListener() {
                @Override
                public void onChecked(DateView dateView, RowObject checkedDateInfo) {
                    day = checkedDateInfo.getInteger("day");
                    setDateText(checkedDateInfo);
                    String type = checkedDateInfo.getString("type");
                    if (StringUtils.isNotEmpty(type) && "nextMonth".equals(type)) {
                        viewPager.setCurrentItem(position + 1);
                        mapDateView.get(position + 1).setSelectIndexNextMonth(day);
                    } else if (StringUtils.isNotEmpty(type) && "lastMonth".equals(type)) {
                        viewPager.setCurrentItem(position - 1);
                        mapDateView.get(position - 1).setSelectIndexlastMonth(day);
                    }
                    initCacheView();
                    viewPager.setCanScroll(true);
                }
            });
            curPosition = position;
            container.addView(dateView);
            return dateView;
        }
    }

    private void setDateText(RowObject checkedDateInfo) {
        year = checkedDateInfo.getInteger("year");
        month = checkedDateInfo.getInteger("month");
        day = checkedDateInfo.getInteger("day");
        tv_year.setText(year + "");
        tv_month.setText(month < 10 ? "0" + month : month + "");
        tv_day.setText(day < 10 ? "0" + day : day + "");

    }

    public String getDateText() {
        return tv_year.getText() + "-" + tv_month.getText() + "-" + tv_day.getText();
    }
}
