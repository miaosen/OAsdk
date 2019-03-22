package com.oaui.view.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.oaui.L;
import com.oaui.data.RowObject;
import com.oaui.utils.AppUtils;
import com.oaui.utils.StringUtils;

import java.util.LinkedList;
import java.util.List;


/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2018-04-12  11:54
 * @Descrition 日期显示页面
 * 农历资料 https://iask.sina.com.cn/b/7176174.html
 */

public class DateView extends View {

    //默认天字体大小18dp
    int textSize = AppUtils.dip2px(18);
    //默认农历字体大小12dp
    int tsNongLi = AppUtils.dip2px(10);
    //默认内边距
    int padding = AppUtils.dip2px(1);
    //选中日期指针
    int selectIndex = -1;
    //今天日期指针
    int todayIndex = -1;
    //今天日期指针
    int selectMonthIndex = -1;
    //今天日期指针
    int selectYearIndex = -1;
    //日期显示画笔
    Paint paintDay;
    //非本月日期显示画笔
    Paint paintDayNoThisMonth;
    //被选中时日期显示画笔
    Paint paintDayCover;
    //选中后被覆盖的圆画笔
    Paint paintCircle;
    //今天圆环画笔
    Paint paintCircleRing;
    //农历字体画笔
    Paint paintNongli;
    //单个格子的高度和宽度
    int gridWidth = 0, gridHeight = 0;

    OnDateCheckedListener onDateCheckedListener;

    int showType = 0;//0为日视图，1，月视图，2为年视图

    boolean firstLoad = true;

    int curYear = -1, curMonth = -1, curDay = -1;

    //数据源
    List<RowObject> currentMonthInfo = new LinkedList<>();

    int colorGrey = Color.parseColor("#B6B6B6");

    int themeColor = Color.parseColor("#3FAEF2");

    float touchX,touchY;

    public DateView(Context context) {
        super(context);
        init();
    }

    public DateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public DateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        paintDay = getPaintDay();
        paintDayCover = getPaintDay();
        paintDayCover.setColor(Color.WHITE);
        paintDayNoThisMonth = getPaintDay();
        paintDayNoThisMonth.setColor(colorGrey);
        paintCircle = new Paint();
        paintCircle.setAntiAlias(true);
        paintCircle.setColor(themeColor);
        //农历字体样式
        paintNongli = new Paint();
        paintNongli.setAntiAlias(true);
        paintNongli.setColor(colorGrey);
        Typeface font1 = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL);
        paintNongli.setTypeface(font1);
        paintNongli.setTextSize(tsNongLi);
        paintCircleRing = new Paint();
        paintCircleRing.setAntiAlias(true);
        paintCircleRing.setColor(colorGrey);
        paintCircleRing.setStyle(Paint.Style.STROKE);
        paintCircleRing.setStrokeWidth(3);
        //setBackgroundColor(Color.BLUE);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getIndex(touchX,touchY);
                if(showType!=0){
                    if(showType==2&&(selectYearIndex==0||selectYearIndex==24)){
                        if(selectYearIndex==0){
                            selectYearIndex=12;
                            curYear=curYear-12;
                        }else{
                            selectYearIndex=12;
                            curYear=curYear+12;
                        }
                    }else{
                        showType=0;
                    }
                    currentMonthInfo.clear();
                    currentMonthInfo.addAll(CalendarDataFactory.getPageCalendarInfo(curYear,curMonth));
                }
                if (onDateCheckedListener != null) {
                    onDateCheckedListener.onChecked(DateView.this, currentMonthInfo.get(selectIndex));
                }
                invalidate();
            }
        });
    }


    private Paint getPaintDay() {
        Paint paintDay = new Paint();
        //抗锯齿
        paintDay.setAntiAlias(true);
        Typeface font = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL);
        paintDay.setTypeface(font);
        paintDay.setTextSize(textSize);
        paintDay.setColor(Color.parseColor("#191919"));
        return paintDay;
    }

    //@Override
    //protected void onLayout(boolean changed, int l, int t, int r, int b) {
    //    CalendarDataFactory.getCurrentMonthInfo();
    //    //CalendarDataFactory.getCurrentPageCalendarInfo();
    //   // CalendarDataFactory.getPageCalendarInfo(2018,5);
    //   //L.i("=========onLayout=============="+ChinaDate.today());
    //}


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        int screenWidth = AppUtils.getScreenWidth();
        int screenHeight = AppUtils.getScreenHeight();
        //取最小值
        if (screenWidth > screenHeight) {
            screenWidth = screenHeight;
        }
        if (sizeWidth > sizeHeight && sizeHeight != 0) {
            sizeWidth = sizeHeight;
        }
        //只看宽度的属性
        setMeasuredDimension((modeWidth == MeasureSpec.EXACTLY) ? sizeWidth
                : screenWidth, (modeWidth == MeasureSpec.EXACTLY) ? sizeWidth
                : screenWidth);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (currentMonthInfo.size() == 0) {
            List<RowObject> currentPageCalendarInfo = CalendarDataFactory.getCurrentPageCalendarInfo();
            currentMonthInfo.addAll(currentPageCalendarInfo);
            curYear= CalendarDataFactory.getCurYear();
            curMonth= CalendarDataFactory.getCurMonth();
            curDay= CalendarDataFactory.getCurDay();
            selectIndex=CalendarDataFactory.getSelectIndexInMonth(currentPageCalendarInfo,curDay);
            selectMonthIndex=curMonth-1;
            selectYearIndex=curYear;
        }
        if (showType == 0) {
            drawDateView(canvas);
        } else if (showType == 1) {
            drawMonthView(canvas);
        } else if (showType == 2) {
            drawYearView(canvas);
        }


    }

    private void drawYearView(Canvas canvas) {
        gridHeight = (getMeasuredHeight() - (2 * padding)) / 5;
        gridWidth = (getMeasuredHeight() - (2 * padding)) / 5;
        int radius = gridHeight / 2;
        L.i("=========drawMonthView=============="+gridWidth+"  "+gridHeight);
        int x = padding;
        int y = gridHeight + padding;
        for (int i = 0; i < 25; i++) {
            if (i % 5 == 0 && i != 0) {
                x = padding;
                y = y + gridHeight;
            }
            x = x + gridWidth;
            String year=(curYear-12+i)+"";
            if (i==0||i==24) {
                drawTextCenter(year, x - (gridWidth / 2), y - (gridHeight / 2), canvas, paintDayNoThisMonth);
            }else if (i==12) {
                canvas.drawCircle(x - (gridWidth / 2), y - (gridHeight / 2), radius, paintCircle);
                drawTextCenter(year, x - (gridWidth / 2), y - (gridHeight / 2), canvas, paintDayCover);
            } else {
                drawTextCenter(year, x - (gridWidth / 2), y - (gridHeight / 2), canvas, paintDay);
            }
        }

    }

    /**
     * 绘制月份视图,高度固定，3x4
     *
     * @param canvas
     */
    protected void drawMonthView(Canvas canvas) {
        gridHeight = (getMeasuredHeight() - (2 * padding)) / 4;
        gridWidth = (getMeasuredHeight() - (2 * padding)) / 3;
        int radius = gridHeight / 2;
        L.i("=========drawMonthView=============="+gridWidth+"  "+gridHeight);
        int x = padding;
        int y = gridHeight + padding;
        for (int i = 0; i < 12; i++) {
            if (i % 3 == 0 && i != 0) {
                x = padding;
                y = y + gridHeight;
            }
            x = x + gridWidth;
            String month=i+1+"月份";
            if (i == selectMonthIndex) {
                canvas.drawCircle(x - (gridWidth / 2), y - (gridHeight / 2), radius, paintCircle);
                drawTextCenter(month, x - (gridWidth / 2), y - (gridHeight / 2), canvas, paintDayCover);
            } else {
                drawTextCenter(month, x - (gridWidth / 2), y - (gridHeight / 2), canvas, paintDay);
            }
        }
    }

    /**
     * 绘制日期视图,高度固定，7x5或者7x6或者7x4
     *
     * @param canvas
     */
    protected void drawDateView(Canvas canvas) {
        //确定单个方格宽度、高度
        gridWidth = (getMeasuredWidth() - (2 * padding)) / 7;
        if (currentMonthInfo.size() == 35) {
            gridHeight = (getMeasuredHeight() - (2 * padding)) / 5;
        } else if (currentMonthInfo.size() == 28) {
            gridHeight = (getMeasuredHeight() - (2 * padding)) / 4;
        }else{
            gridHeight = (getMeasuredHeight() - (2 * padding)) / 6;
        }
        int x = padding;
        int y = gridHeight + padding;
        initCurPageInfo(currentMonthInfo);
        for (int i = 0; i < currentMonthInfo.size(); i++) {
            RowObject rowObject = currentMonthInfo.get(i);
            int yaer = rowObject.getInteger("year");
            int month = rowObject.getInteger("month");
            int day = rowObject.getInteger("day");
            String strDay = day + "";
            String day_yingli = rowObject.getString("day_yingli");
            String type = rowObject.getString("type");
            if (i % 7 == 0 && i != 0) {
                x = padding;
                y = y + gridHeight;
            }
            x = x + gridWidth;
            //canvas.drawCircle(x - (gridWidth / 2), y - (gridHeight / 2), gridWidth / 2, paintCircle);
            int radius = gridWidth / 2;
            if (i == selectIndex) {
                canvas.drawCircle(x - (gridWidth / 2), y - (gridHeight / 2), radius, paintCircle);
                drawTextCenter(strDay, x - (gridWidth / 2), y - (gridHeight / 2), canvas, paintDayCover);
            } else if (CalendarDataFactory.isToday(yaer, month, day)) {
                canvas.drawCircle(x - (gridWidth / 2), y - (gridHeight / 2), radius - 3, paintCircleRing);
                drawTextCenter(strDay, x - (gridWidth / 2), y - (gridHeight / 2), canvas, paintDay);
            } else {
                if (StringUtils.isNotEmpty(type)) {
                    drawTextCenter(strDay, x - (gridWidth / 2), y - (gridHeight / 2), canvas, paintDayNoThisMonth);
                } else {
                    drawTextCenter(strDay, x - (gridWidth / 2), y - (gridHeight / 2), canvas, paintDay);
                }
                if (StringUtils.isNotEmpty(day_yingli)) {
                    drawTextCenter(day_yingli, x - (gridWidth / 2), y - (gridHeight / 2) + (paintDay.measureText(day_yingli) / 2), canvas, paintNongli);
                }
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_DOWN:
                touchX = event.getX();
                touchY = event.getY();

        }
        return super.onTouchEvent(event);
    }

    private void getIndex(float x, float y) {
        if (x < padding || x > getMeasuredWidth() - padding || y < padding || y > getMeasuredHeight() - padding) {
            //点到内边距区域指针不变
        } else {
            if(showType==0){
                int line = (int) ((y - padding) / gridHeight);
                int column = (int) ((x - padding) / gridWidth);
                selectIndex = line * 7 + column;
            }else if(showType==1){
                int line = (int) ((y - padding) / gridHeight);
                int column = (int) ((x - padding) / gridWidth);
                selectMonthIndex = line * 3 + column;
                curMonth=selectMonthIndex+1;
            }else if(showType==2){
                int line = (int) ((y - padding) / gridHeight);
                int column = (int) ((x - padding) / gridWidth);
                selectYearIndex = line * 5 + column;
                L.i("=========getIndex=============="+selectYearIndex+"  "+curYear);
                curYear=curYear+selectYearIndex-12;
            }

        }
    }

    /**
     * 文字在x,y坐标的中心绘制
     *
     * @param text
     * @param x
     * @param y
     * @param canvas
     * @param paint
     */
    private void drawTextCenter(String text, float x, float y, Canvas canvas, Paint paint) {
        //字体居中
        Paint.FontMetricsInt fm = paint.getFontMetricsInt();
        float textWSpac = (paint.measureText(text)) / 2;
        float textHSpac = (fm.bottom - fm.top) / 2 - fm.descent;
        canvas.drawText(text, x - textWSpac, y + textHSpac, paint);
    }

    public List<RowObject> getCurrentMonthInfo() {
        return currentMonthInfo;
    }

    public void setCurrentMonthInfo(List<RowObject> currentMonthInfo) {
        this.currentMonthInfo.clear();
        this.currentMonthInfo.addAll(currentMonthInfo);
        initCurPageInfo(currentMonthInfo);
        invalidate();
    }

    private void initCurPageInfo(List<RowObject> currentMonthInfo) {
        for (int i = 0; i < currentMonthInfo.size(); i++) {
            RowObject rowObject = currentMonthInfo.get(i);
            int yaer = rowObject.getInteger("year");
            int month = rowObject.getInteger("month");
            String type = rowObject.getString("type");
            if(StringUtils.isEmpty(type)){
                curYear=yaer;
                curMonth=month;
                i=currentMonthInfo.size();
            }
        }
    }

    public interface OnDateCheckedListener {
        void onChecked(DateView dateView, RowObject checkedDateInfo);
    }

    public OnDateCheckedListener getOnDateCheckedListener() {
        return onDateCheckedListener;
    }

    public void setOnDateCheckedListener(final OnDateCheckedListener onDateCheckedListener) {
        this.onDateCheckedListener = onDateCheckedListener;
    }

    public int getSelectIndex() {
        return selectIndex;
    }

    public void setSelectDay(int day) {
        this.selectIndex = CalendarDataFactory.getSelectIndexInMonth(currentMonthInfo, day);
        invalidate();
    }


    public RowObject getSelectItemData() {
        if (currentMonthInfo != null && currentMonthInfo.size() > 0) {
            return currentMonthInfo.get(selectIndex);
        } else {
            return null;
        }

    }

    public void showYearView(){
        showType=2;
        invalidate();
    }

    public void showMonthView(){
        showType=1;
        invalidate();
    }

    public void showDateView(){
        showType=0;
        invalidate();
    }


    public void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
        invalidate();
    }

    public void setSelectIndexlastMonth(int day) {
        this.selectIndex = CalendarDataFactory.getSelectedIndexLastMonth(currentMonthInfo, day);
        invalidate();
    }

    public void setSelectIndexNextMonth(int day) {
        this.selectIndex = CalendarDataFactory.getSelectIndexNextMonth(currentMonthInfo, day);
        invalidate();
    }

    public int getShowType() {
        return showType;
    }

    public void setShowType(int showType) {
        this.showType = showType;
    }

    public int getCurDay() {
        return curDay;
    }

    public void setCurDay(int curDay) {
        this.curDay = curDay;
    }

    public int getCurYear() {
        return curYear;
    }

    public void setCurYear(int curYear) {
        this.curYear = curYear;
    }

    public int getCurMonth() {
        return curMonth;
    }

    public void setCurMonth(int curMonth) {
        this.curMonth = curMonth;
    }
}
