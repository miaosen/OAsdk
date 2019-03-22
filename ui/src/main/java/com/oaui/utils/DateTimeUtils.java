package com.oaui.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/** 
 * 字符串转换为java.util.Date<br> 
 * 支持格式为 yyyy.MM.dd G 'at' hh:mm:ss z 如 '2002-1-1 AD at 22:10:59 PSD'<br> 
 * yy/MM/dd HH:mm:ss 如 '2002/1/1 17:55:00'<br> 
 * yy/MM/dd HH:mm:ss pm 如 '2002/1/1 17:55:00 pm'<br> 
 * yy-MM-dd HH:mm:ss 如 '2002-1-1 17:55:00' <br> 
 * yy-MM-dd HH:mm:ss am 如 '2002-1-1 17:55:00 am' <br> 
 * @param time String 字符串<br> 
 * @return Date 日期<br> 
 */

/**
 * @Created by com.gzpykj.com
 * @author zms
 * @Date 2015-12-15
 * @Descrition 时间处理工具
 */
public class DateTimeUtils {

	/**
	 * 获取系统当前时间
	 * 
	 * @return
	 */
	public static String getCurrentTime() {
		String format = "yyyy-MM-dd HH:mm:ss";
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		String currentTime = sdf.format(date);
		return currentTime;
	}
	
	/**
	 * 获取系统当前时间
	 * @param format
	 * @return
	 */
	public static String getCurrentTime(String format) {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		String currentTime = sdf.format(date);
		return currentTime;
	}
	
	/**
	 * 获取系统当前天
	 * @return
	 */
	public static String getCurrentDay() {
		String format = "yyyy-MM-dd";
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		String currentTime = sdf.format(date);
		return currentTime;
	}
	
	
	/**
	 * 获取系统当前月的第一天
	 * @return
	 */
	public static String getFirstDay() {
		String format = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		 //获取当前月第一天：
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天
		return sdf.format(c.getTime());
	}
	
	/**
	 * 获取系统当前年份
	 * @return
	 */
	public static int getYear() {
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.YEAR);
	}
	
	/**
	 * 获取系统当前月份
	 * @return
	 */
	public static int getMonth() {
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.MONTH);
	}
	
	/**
	 * 获取系统当前天
	 * @return
	 */
	public static int getDay() {
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.DAY_OF_MONTH);
	}
	
	
	/**
	 * 根据年和月获取这个月的总天数
	 * @param year
	 * @param month
	 * @return 
	 */
	public static int getDayByMonth(int year,int month) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR,year);
		c.set(Calendar.MONTH, month-1);//Java月份从0开始算
		int sumDay = c.getActualMaximum(Calendar.DATE);//当前月的总天数
		return sumDay;
	}
	
	

	/**
	 * 获取系统当前天
	 * @return
	 */
	public static String getDigitalTime(long digitalTime) {
		//时间戳转化为Sting或Date
		SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String strTime = format.format(digitalTime);
		return strTime;
	}

	/**
	 * 获取系统当前天
	 * @return
	 */
	public static String formatSecordTime(long secordTime) {
		//时间戳转化为Sting或Date
		String time="";
		long hour = secordTime / 3600;
		if(hour>0){
			time=hour+"时";
			secordTime=secordTime%3600;
		}
		long minute = secordTime / 60;
		if(minute>0){
			time=time+minute+"分";
			secordTime=secordTime%60;
		}
		time=time+secordTime+"秒";
		return time;
	}

	public static String getCurrentTimeMillis(){
		long time=System.currentTimeMillis()/1000;//获取系统时间的10位的时间戳
		return String.valueOf(time);

	}


}
