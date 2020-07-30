package cn.oaui.utils;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.oaui.data.JSONSerializer;
import cn.oaui.data.Row;

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
	public static List<Row> argToRows(String key, Object[] args) {
		List<Row> rows = new ArrayList<Row>();
		for (int i = 0; i < args.length; i++) {
			Row row = new Row();
			row.put(key, args[i]);
			rows.add(row);
		}
		return rows;
	}


	public static Row entityToRow(Object object){
		String jsonString = JSONSerializer.toJSONString(object);
		return JsonUtils.jsonToRow(jsonString);
	}


	public static List<Row> cursorToRows(Cursor cursor){
		List<Row> rows=new LinkedList<>();
		if(cursor.moveToFirst()){//判断数据表里有数据
			while(cursor.moveToNext()){//遍历数据表中的数据
				Row row=new Row();
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
	public static String getlayerData(Row row, String[] args) {
		String value = null;
		if (args.length < 2) {
			return value;
		} else {
			//避免引用影响数据源
			Row rowNew = new Row();
			rowNew.putAll(row);
			for (int i = 0; i < args.length; i++) {
				if (rowNew.get(args[i]) != null) {
					if (i == args.length - 1) {
						value = rowNew.getString(args[i]);
					} else {
						Object obj = rowNew.get(args[i]);
						if(obj!=null&&obj instanceof Row){
							rowNew.putAll((Row) obj);
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


	public static Map<String,Object> rowToMap(Row row) {
		Map<String,Object> map=new HashMap<>();
		for (String key: row.keySet()){
			map.put(key, row.getString(key));
		}
		return map;
	}




	public static ContentValues rowToContentValues(Row row) {
		ContentValues contentValues=new ContentValues();
		for (String key: row.keySet()){
			contentValues.put(key, row.getString(key));
		}
		return contentValues;
	}
	public static  LinkedList<Row>  listEntityToRows(Object object){
		String jsonString = JSONSerializer.toJSONString(object);
		return JsonUtils.jsonToRows(jsonString);
	}

}
