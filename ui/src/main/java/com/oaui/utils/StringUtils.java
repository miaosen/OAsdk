package com.oaui.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
	
	/**
	 * String[]转成value为null的HashMap
	 * @param args
	 * @return
	 */
	public static HashMap<String,Object> argsToMap(String[] args) {
		HashMap<String,Object> map=new HashMap<String, Object>();
		for (int i = 0; i < args.length; i++) {
			map.put(args[i],null);
		}
		return map;
	}
	
	/**
	 * 判断一个数组是否包含某个对象
	 * @return
	 */
	public static boolean isInArray(Object[] objects, Object object){
		for (int i = 0; i < objects.length; i++) {
			if(object.toString().equals(objects[i])){
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取utf-8字符的字节长度
	 * @return
	 */
	public static int getByteLength(String text){
		 int length =0;
		try {
			//L.i("=========setItem=============="+text+" 长度==="+text.getBytes("utf-8").length);
			length=text.getBytes("utf-8").length;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return length;
	}

	/**
	 * 判断字符串是否为数字
	 * @param str
	 * @return
     */
	public static boolean isNumeric(String str){
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if( !isNum.matches() ){
			return false;
		}
		return true;
	}


	public static byte[] objectToByte(Object obj) {
		byte[] bytes = null;
		try {
			// object to bytearray
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream oo = new ObjectOutputStream(bo);
			oo.writeObject(obj);

			bytes = bo.toByteArray();

			bo.close();
			oo.close();
		} catch (Exception e) {
			System.out.println("translation" + e.getMessage());
			e.printStackTrace();
		}
		return bytes;
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
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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

}
