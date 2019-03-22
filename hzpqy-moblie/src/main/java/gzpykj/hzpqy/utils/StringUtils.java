package gzpykj.hzpqy.utils;

import android.annotation.SuppressLint;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Created by eaglesoft.org
 * @author yangjincheng
 * @Date 2013-8-15
 */
public class StringUtils {

	/**
	 * MD5加密程序
	 * 
	 * @param src
	 * @return
	 * @throws Exception
	 */
	@SuppressLint("DefaultLocale")
	public static String md5(String src) {
		java.security.MessageDigest md5;
		try {
			md5 = java.security.MessageDigest.getInstance("MD5");
			md5.update(src.getBytes());
			src = byte2hex(md5.digest()).toUpperCase();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return src;
	}

	/**
	 * SHA1加密程序
	 * 
	 * @param src
	 * @return
	 * @throws Exception
	 */
	public static String sha1(String src) {
		java.security.MessageDigest sha1;
		try {
			sha1 = java.security.MessageDigest.getInstance("SHA1");
			sha1.update(src.getBytes());
			src = byte2hex(sha1.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return src;
	}

	public static String escape(String src) {
		int i;
		char j;
		StringBuffer tmp = new StringBuffer();
		tmp.ensureCapacity(src.length() * 6);
		for (i = 0; i < src.length(); i++) {
			j = src.charAt(i);
			if (Character.isDigit(j) || Character.isLowerCase(j)
					|| Character.isUpperCase(j))
				tmp.append(j);
			else if (j < 256) {
				tmp.append("%");
				if (j < 16)
					tmp.append("0");
				tmp.append(Integer.toString(j, 16));
			} else {
				tmp.append("%u");
				tmp.append(Integer.toString(j, 16));
			}
		}
		return tmp.toString();
	}

	public static String unescape(String src) {
		StringBuffer tmp = new StringBuffer();
		tmp.ensureCapacity(src.length());
		int lastPos = 0, pos = 0;
		char ch;
		while (lastPos < src.length()) {
			pos = src.indexOf("%", lastPos);
			if (pos == lastPos) {
				if (src.charAt(pos + 1) == 'u') {
					ch = (char) Integer.parseInt(
							src.substring(pos + 2, pos + 6), 16);
					tmp.append(ch);
					lastPos = pos + 6;
				} else {
					ch = (char) Integer.parseInt(
							src.substring(pos + 1, pos + 3), 16);
					tmp.append(ch);
					lastPos = pos + 3;
				}
			} else {
				if (pos == -1) {
					tmp.append(src.substring(lastPos));
					lastPos = src.length();
				} else {
					tmp.append(src.substring(lastPos, pos));
					lastPos = pos;
				}
			}
		}
		return tmp.toString();
	}

	/**
	 * 
	 * @param obj
	 * @return
	 */
	public static String toString(Object obj) {
		if (obj == null) {
			return null;
		} else {
			return obj.toString();
		}
	}

	/**
	 * 
	 * @param obj
	 * @return
	 */
	public static String trimToEmpty(Object obj) {
		if (obj == null) {
			return "";
		}
		return toString(obj).trim();
	}

	/**
	 * 
	 * @param obj
	 * @return
	 */
	public static String trimToNull(Object obj) {
		if (obj == null) {
			return null;
		}
		return toString(obj);
	}

	/**
	 * 判断字符串空
	 * 
	 * @param obj
	 * @return
	 */
	public static boolean isEmpty(Object obj) {
		if (obj == null) {
			return true;
		}
		String str = obj.toString();
		return ((str == null) || (str.length() == 0));
	}

	/**
	 * 判断字符串不为空
	 * 
	 * @param obj
	 * @return
	 */
	public static boolean isNotEmpty(Object obj) {
		return !isEmpty(toString(obj));
	}

	private static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
		}
		return hs;
	}

	public static String encodeURI(String str) {
		return encodeURI(str, "UTF-8");
	}

	public static String encodeURI(String str, String charset) {
		if (str == null) {
			return "";
		}
		String outStr = null;
		try {
			if (isNotEmpty(str)) {
				outStr = URLEncoder.encode(str, charset);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outStr;
	}

	public static String decodeURI(String str) {
		return decodeURI(str, "UTF-8");
	}

	public static String decodeURI(String str, String charset) {
		if (str == null) {
			return "";
		}
		String outStr = null;
		try {
			if (isNotEmpty(str)) {
				outStr = URLDecoder.decode(str, charset);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return outStr;
	}

	/**
	 * 合并集合类型中的数据项为字符串并用指它的分隔符隔开
	 * 
	 * @param c
	 * @param separator
	 * @return
	 */
	public static String join(Collection c, String separator, String packChar) {
		if (c == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		boolean start = false;
		for (Iterator iterator = c.iterator(); iterator.hasNext();) {
			Object obj = iterator.next();
			if (start) {
				sb.append(separator);
			} else {
				start = true;
			}
			sb.append(packChar + obj + packChar);
		}
		return sb.toString();
	}

	public static String join(Collection c, String separator) {
		return join(c, separator, "");
	}

	/**
	 * 合并数组类型中的数据项为字符串并用指它的分隔符隔开
	 * 
	 * @param array
	 * @param separator
	 * @return
	 */
	public static String join(Object[] array, String separator, String packChar) {
		if (array == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		boolean start = false;
		for (Object obj : array) {
			if (start) {
				sb.append(separator);
			} else {
				start = true;
			}
			sb.append(packChar + obj + packChar);

		}
		return sb.toString();
	}

	public static String join(Object[] array, String separator) {
		return join(array, separator, "");
	}

	/**
	 * Base64编码
	 * 
	 * @param str
	 * @param charset
	 * @return
	 */
	public static String encodeBase64(String str, String charset) {
		try {
			return Base64.encodeToString(str.getBytes(charset), Base64.DEFAULT);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Base64解码
	 * 
	 * @param str
	 * @param charset
	 * @return
	 */
	public static String decodeBase64(String str, String charset) {
		try {
			return new String(Base64.decode(str, Base64.DEFAULT), charset);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String substring(String str, int len) {
		if (str == null) {
			return null;
		}

		int sl = str.length();
		if (sl < len) {
			len = sl;
		}

		return str.substring(0, len);
	}

	private final static Pattern emailer = Pattern
			.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");

	private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};

	private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd");
		}
	};

	/**
	 * 将字符串转为日期类型
	 * 
	 * @param sdate
	 * @return
	 */
	public static Date toDate(String sdate) {
		try {
			return dateFormater.get().parse(sdate);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 获取时间
	 * 
	 * @param format
	 * @return
	 */

	public static String getCurrentTime(String format) {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		String currentTime = sdf.format(date);
		return currentTime;
	}

	public static String getCurrentTime() {
		return getCurrentTime("yyyy-MM-dd  HH:mm:ss");
	}


	/**
	 * 判断给定字符串时间是否为今日
	 * 
	 * @param sdate
	 * @return boolean
	 */
	public static boolean isToday(String sdate) {
		boolean b = false;
		Date time = toDate(sdate);
		Date today = new Date();
		if (time != null) {
			String nowDate = dateFormater2.get().format(today);
			String timeDate = dateFormater2.get().format(time);
			if (nowDate.equals(timeDate)) {
				b = true;
			}
		}
		return b;
	}

	/**
	 * list转json对象
	 * 
	 * @param list
	 * @return
	 */
	public static String funConversion(List<String> list) {
		JSONArray jsonArr = new JSONArray(list);
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put("data", jsonArr);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObj.toString();
	}

	/**
	 * 返回long类型的今天的日期
	 * 
	 * @return
	 */
	public static long getToday() {
		Calendar cal = Calendar.getInstance();
		String curDate = dateFormater2.get().format(cal.getTime());
		curDate = curDate.replace("-", "");
		return Long.parseLong(curDate);
	}

	/**
	 * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
	 * 
	 * @param input
	 * @return boolean
	 */
	public static boolean isEmpty(String input) {
		if (input == null || "".equals(input))
			return true;

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断是不是一个合法的电子邮件地址
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		if (email == null || email.trim().length() == 0)
			return false;
		return emailer.matcher(email).matches();
	}

	/**
	 * 字符串转整数
	 * 
	 * @param str
	 * @param defValue
	 * @return
	 */
	public static int toInt(String str, int defValue) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
		}
		return defValue;
	}

	/**
	 * 对象转整数
	 * 
	 * @param obj
	 * @return 转换异常返回 0
	 */
	public static int toInt(Object obj) {
		if (obj == null)
			return 0;
		return toInt(obj.toString(), 0);
	}

	/**
	 * 对象转整数
	 * 
	 * @param obj
	 * @return 转换异常返回 0
	 */
	public static long toLong(String obj) {
		try {
			return Long.parseLong(obj);
		} catch (Exception e) {
		}
		return 0;
	}

	/**
	 * 字符串转布尔值
	 * 
	 * @param b
	 * @return 转换异常返回 false
	 */
	public static boolean toBool(String b) {
		try {
			return Boolean.parseBoolean(b);
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * 将一个InputStream流转换成字符串
	 * 
	 * @param is
	 * @return
	 */
	public static String toConvertString(InputStream is) {
		StringBuffer res = new StringBuffer();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader read = new BufferedReader(isr);
		try {
			String line;
			line = read.readLine();
			while (line != null) {
				res.append(line);
				line = read.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != isr) {
					isr.close();
					isr.close();
				}
				if (null != read) {
					read.close();
					read = null;
				}
				if (null != is) {
					is.close();
					is = null;
				}
			} catch (IOException e) {
			}
		}
		return res.toString();
	}

	/**
	 * 将字符串提取数字
	 * 
	 * @param str
	 * @return
	 */

	public static String toFilterString(String str) {
		String s = "";
		str = str.trim();
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) >= 48 && str.charAt(i) <= 57) {
				s += str.charAt(i);
			}
		}
		if (str.indexOf("℃") != -1) {
			StringBuffer strBuf = new StringBuffer(s);
			strBuf.insert(2, ".");
			s = strBuf.toString();
		}
		return s;
	}

	/**
	 * 判断是否有对应的json数据项
	 * 
	 * @param json
	 * @param itemStr
	 * @return
	 * @throws JSONException
	 */
	public static String processingJsonItem(JSONObject json, String itemStr)
			throws JSONException {
		String item = null;
		if (!json.isNull(itemStr) && !json.getString(itemStr).isEmpty()) {
			item = json.getString(itemStr);
		} else {
			return "暂无数据";
		}
		return item;
	}

	/**
	 * 
	 */
	public static String subString(String str) {
		if (str.length() > 50) {
			return str.substring(0, 100) + "……";
		}
		return str;
	}

	/**
	 * 
	 * 首字母变小写
	 * 
	 * @param str
	 * @return
	 */
	public static String firstCharToLower(String str) {
		if (str == null) {
			return str;
		}
		try {
			str = str.substring(0, 1).toLowerCase() + str.substring(1);
			return str;
		} catch (Exception usex) {
			return str;
		}
	}

	/**
	 * 
	 * 首字母变大写
	 * 
	 * @param str
	 * @return
	 */
	public static String firstCharToUpper(String str) {
		if (str == null) {
			return str;
		}
		try {
			str = str.substring(0, 1).toUpperCase() + str.substring(1);
			return str;
		} catch (Exception usex) {
			return str;
		}
	}

	/**
	 * 获取UUID
	 * 
	 * @return
	 */
	public static String getUUID() {
		return UUID.randomUUID().toString().trim().replaceAll("-", "");
	}

	/**
	 * 判断字符串是否为数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	
	/**
	 * 深度复制数组
	 * @param src
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> deepCopyList(List<T> src) {
		List<T> dest = null;
		try {
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(byteOut);
			out.writeObject(src);
			ByteArrayInputStream byteIn = new ByteArrayInputStream(
					byteOut.toByteArray());
			ObjectInputStream in = new ObjectInputStream(byteIn);
			dest = (List<T>) in.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return dest;
	}
	
	  /** 
     * 深度拷贝 
     * @param obj 
     * @return 
     */  
    @SuppressWarnings("unchecked")
    public static <T> T deepCopyObject(T obj) {  
        try {  
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(obj);  
              
            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream in =new ObjectInputStream(byteIn);
              
            return (T)in.readObject();  
        } catch (IOException e) {
            e.printStackTrace();  
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  
        }  
        return null;  
    } 
    
    

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    public static String formetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

	/**
	 * arry转list
	 * @param arry
	 * @return
     */
	public static List<String> toArrays(String[] arry) {
		List<String> list=new ArrayList<String>();
		for (int i = 0; i < arry.length; i++) {
			list.add(arry[i]);
		}
		return list;
	}



}
