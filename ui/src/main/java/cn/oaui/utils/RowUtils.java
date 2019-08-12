package cn.oaui.utils;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.oaui.data.JSONSerializer;
import cn.oaui.data.RowObject;

/**
 * @email 1510809124@qq.com
 * @author zengmiaosen
 * @git http://git.oschina.net/miaosen/MyUtils
 * @CreateDate 2017/3/13 10:30
 * @Descrition 
 */ 
public class RowUtils {


	/**
	 * 数组转Row
	 * @param key
	 * @param args
     * @return
     */
	public static List<RowObject> argToRows(String key, Object[] args) {
		List<RowObject> rows = new ArrayList<RowObject>();
		for (int i = 0; i < args.length; i++) {
			RowObject row = new RowObject();
			row.put(key, args[i]);
			rows.add(row);
		}
		return rows;
	}


	public static RowObject entityToRow(Object object){
		String jsonString = JSONSerializer.toJSONString(object);
		return JsonUtils.jsonToRow(jsonString);
	}


	public static List<RowObject> cursorToRows(Cursor cursor){
		List<RowObject> rows=new LinkedList<>();
		if(cursor.moveToFirst()){//判断数据表里有数据
			while(cursor.moveToNext()){//遍历数据表中的数据
				RowObject row=new RowObject();
				for (int i = 0; i < cursor.getColumnCount(); i++) {
					row.put(cursor.getColumnName(i),cursor.getString(i));
				}
				rows.add(row);
			}
			cursor.close();
		}
		return rows;
	}


	/**
	 * 获取RowObject层级数据,比如aa对象下有key为bb的字段,则返回row下的aa的RowObject对象中的key为bb的值
	 * 
	 * @param row
	 * @param args aa,bb
	 * @return
	 */
	public static String getlayerData(RowObject row, String[] args) {
		String value = null;
		if (args.length < 2) {
			return value;
		} else {
			//避免引用影响数据源
			RowObject rowNew = new RowObject();
			rowNew.putAll(row);
			for (int i = 0; i < args.length; i++) {
				if (rowNew.get(args[i]) != null) {
					if (i == args.length - 1) {
						value = rowNew.getString(args[i]);
					} else {
						Object obj = rowNew.get(args[i]);
						if(obj!=null&&obj instanceof RowObject){
							rowNew.putAll((RowObject) obj);
						} else {
							i = args.length;
						}
					}
				} else {
					i = args.length;
				}
			}
			return value;
		}
	}


	public static Map<String,Object> rowToMap(RowObject rowObject) {
		Map<String,Object> map=new HashMap<>();
		for (String key:rowObject.keySet()){
			map.put(key,rowObject.getString(key));
		}
		return map;
	}




	public static ContentValues rowToContentValues(RowObject rowObject) {
		ContentValues contentValues=new ContentValues();
		for (String key:rowObject.keySet()){
			contentValues.put(key,rowObject.getString(key));
		}
		return contentValues;
	}

}
