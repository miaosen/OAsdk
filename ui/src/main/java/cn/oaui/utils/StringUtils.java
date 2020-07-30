package cn.oaui.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
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

	/**
	 * 获取UUID
	 *
	 * @return
	 */
	public static String getUUID() {
		return UUID.randomUUID().toString().trim().replaceAll("-", "");
	}


	public static String getMd5(File file) {
		InputStream inputStream = null;
		byte[] buffer = new byte[2048];
		int numRead;
		MessageDigest md5;
		try {
			inputStream = new FileInputStream(file);
			md5 = MessageDigest.getInstance("MD5");
			while ((numRead = inputStream.read(buffer)) > 0) {
				md5.update(buffer, 0, numRead);
			}
			inputStream.close();
			inputStream = null;
			return md5ToString(md5.digest());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static String md5ToString(byte[] md5Bytes) {
		StringBuilder hexValue = new StringBuilder();
		for (byte b : md5Bytes) {
			int val = ((int) b) & 0xff;
			if (val < 16) {
				hexValue.append("0");
			}
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
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

    public static String removeUnnecessarySpace(String s) {
		return s.replaceAll("\\s{2,}", " ");
    }
}
