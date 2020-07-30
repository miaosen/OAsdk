package cn.oaui.view.calendar;

import cn.oaui.data.Row;
import cn.oaui.utils.StringUtils;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;


/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2018-04-12  14:01
 * @Descrition
 */

public class CalendarDataFactory {


    public static List<Row> getCurrentMonthInfo() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);// 获取年份
        int month = c.get(Calendar.MONTH) + 1;// 获取月份
        return getMonthInfo(year, month);
    }

    public static List<Row> getLastMonthInfo(int year, int month) {
        if(month==1){
            month=12;
            year=year-1;
        }else{
            month=month-1;
        }
       return getMonthInfo(year,month);
    }

    public static List<Row> getLastMonthPageInfo(int year, int month) {
        if(month==1){
            month=12;
            year=year-1;
        }else{
            month=month-1;
        }
        return getPageCalendarInfo(year,month);
    }

    /**
     *
     * @param year
     * @param month
     * @return
     */
    public static List<Row> getNextMonthInfo(int year, int month) {
        if(month==12){
            month=1;
            year=year+1;
        }else{
            month=month+1;
        }
        return getMonthInfo(year,month);
    }

    /**
     *
     * @param year
     * @param month
     * @return
     */
    public static List<Row> getNextMonthPageInfo(int year, int month) {
        if(month==12){
            month=1;
            year=year+1;
        }else{
            month=month+1;
        }
        return getPageCalendarInfo(year,month);
    }

    /**
     * 获取一个月信息
     * @param year
     * @param month
     * @return
     */
    public static List<Row> getMonthInfo(int year, int month) {
        List<Row> rows = new LinkedList<>();
        int actualMaximum = getMonthDaysCount(year, month);
        for (int i = 0; i < actualMaximum; i++) {
            Row row = new Row();
            int day=i+1;
            row.put("year", year);
            row.put("month", month);
            row.put("day", day);
            String chinaDate;
            try {
                 chinaDate = ChinaDate.getChinaDate(year, month, day);
            }catch (Exception e){
                //TODO 一个字时位置异常
                chinaDate="暂无";
            }
            row.put("day_yingli",chinaDate);
            //TODO 节日，假期
            //传统节日
            row.put("festival_a", day);
            rows.add(row);
        }
        //L.i("=========getMonthInfo=============="+rows);
        return rows;
    }

    public static List<Row> getNewlyThreeMonth() {
        List<Row> rows = new LinkedList<>();
        return rows;
    }

    public static List<Row> getCurrentPageCalendarInfo() {
        Calendar cal = Calendar.getInstance();
        return getPageCalendarInfo(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1);
    }


    public static int getCurYear() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.YEAR);
    }

    public static int getCurMonth() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.MONTH)+1;
    }

    public static int getCurDay() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.DATE);
    }
    /**
     * 获取距离当前月份相差几个月的日历日期
     * @param monthCount
     * @return
     */
    public static List<Row> getPageCalendarInfoRangeMonth(int monthCount) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH)+1;
        int rangeYear = monthCount / 12;
        int rangeMonth = monthCount % 12;
        year=year+rangeYear;
        if(month+rangeMonth>12){
            year=year+1;
            month=month+rangeMonth-12;
        }else if(month+rangeMonth<1){
            year=year-1;
            month=12+month+rangeMonth;
        }else{
            month=month+rangeMonth;
        }
        return getPageCalendarInfo(year, month);
    }

    /**
     * 获取一页日历的数据
     * @param year
     * @param month
     * @return
     */
    public static List<Row> getPageCalendarInfo(int year, int month) {
        List<Row> rows = new LinkedList<>();
        Calendar cal = Calendar.getInstance();
        cal.set(year, month-1, 1);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if(dayOfWeek!=0){
            List<Row> lastMonthInfo = getLastMonthInfo(year, month);
            for (int i = lastMonthInfo.size()-dayOfWeek; i < lastMonthInfo.size(); i++) {
                Row row = lastMonthInfo.get(i);
                row.put("type","lastMonth");
                rows.add(row);
            }
        }
        List<Row> monthInfo = getMonthInfo(year, month);
        rows.addAll(monthInfo);
        int monthDaysCount = getMonthDaysCount(year, month);
        cal.set(year, month-1, monthDaysCount);
        int dayOfWeekAfter = 7 - cal.get(Calendar.DAY_OF_WEEK);
        if(dayOfWeekAfter!=0){
            List<Row> nextMonthInfo = getNextMonthInfo(year, month);
            for (int i = 0; i < dayOfWeekAfter; i++) {
                Row row = nextMonthInfo.get(i);
                row.put("type","nextMonth");
                rows.add(row);
            }
        }
        return rows;
    }
    
    public static int getSelectedIndexLastMonth(List<Row> currentMonthInfo, int day){
        int sum=0,selectIndex=0;
        for (int i = currentMonthInfo.size()-1; i >=0 ; i--) {
            Row row = currentMonthInfo.get(i);
            Integer day1 = row.getInteger("day");
            sum=sum+1;
            if(day1==day){
                i=-1;
                selectIndex=currentMonthInfo.size()-sum;
            }
        }
        return selectIndex;
    }

    public static int getSelectIndexNextMonth(List<Row> currentMonthInfo, int day){
        int sum=0,selectIndex=0;
        for (int i = 0; i <  currentMonthInfo.size(); i++) {
            Row row = currentMonthInfo.get(i);
            Integer day1 = row.getInteger("day");
            sum=sum+1;
            if(day1==day){
                i= currentMonthInfo.size();
                selectIndex=sum-1;
            }
        }
        return selectIndex;
    }

    public static int getSelectIndexInMonth(List<Row> currentMonthInfo, int day){
        int sum=0,selectIndex=0;
        for (int i = 0; i <  currentMonthInfo.size(); i++) {
            Row row = currentMonthInfo.get(i);
            Integer day1 = row.getInteger("day");
            String type= row.getString("type");
            sum=sum+1;
            if(day1==day&& StringUtils.isEmpty(type)){
                i= currentMonthInfo.size();
                selectIndex=sum-1;
            }
        }
        return selectIndex;
    }


    /**
     * 获取某月的天数
     *
     * @param year  年
     * @param month 月
     * @return 某月的天数
     */
    static int getMonthDaysCount(int year, int month) {
        int count = 0;
        //判断大月份
        if (month == 1 || month == 3 || month == 5 || month == 7
                || month == 8 || month == 10 || month == 12) {
            count = 31;
        }
        //判断小月
        if (month == 4 || month == 6 || month == 9 || month == 11) {
            count = 30;
        }
        //判断平年与闰年
        if (month == 2) {
            if (isLeapYear(year)) {
                count = 29;
            } else {
                count = 28;
            }
        }
        return count;
    }


    /**
     * 是否是闰年
     *
     * @param year year
     * @return return
     */
    static boolean isLeapYear(int year) {
        return ((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0);
    }



    /**
     * 是否是闰年
     *
     * @param year year
     * @return return
     */
    static boolean isToday(int year,int month,int day) {
        Calendar cal = Calendar.getInstance();
        int tyear = cal.get(Calendar.YEAR);
        int tmonth = cal.get(Calendar.MONTH)+1;
        int tday = cal.get(Calendar.DATE);
        return year ==tyear && tmonth ==month&&tday== day;
    }

}
