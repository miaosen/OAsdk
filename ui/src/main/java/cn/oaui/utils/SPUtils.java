package cn.oaui.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.oaui.UIGlobal;
import cn.oaui.data.JSONSerializer;
import cn.oaui.data.RowObject;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2017-10-30  15:22
 * @Descrition 缓存工具
 */

public class SPUtils {

    /**
     * 获取文本
     * @param fileName
     * @param key
     * @return
     */
    public static String getText(String fileName, String key){
        SharedPreferences preferences = UIGlobal.getApplication().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return preferences.getString(key, "");
    }

    /**
     * 保存文本
     * @param fileName
     * @param key
     * @param value
     * @return
     */
    public static boolean saveText(String fileName, String key, String value){
        SharedPreferences preferences = UIGlobal.getApplication().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    /**
     * 获取json文本后转rowObject
     * @param fileName
     * @param key
     * @return
     */
    public static RowObject getRow(String fileName, String key){
        SharedPreferences preferences = UIGlobal.getApplication().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        String string = preferences.getString(key, "");
        if(JsonUtils.isCanToRow(string)){
            return JsonUtils.jsonToRow(string);
        }else{
            return null;
        }
    }

    /**
     * RowObject转成json后保存为文本
     * @param fileName
     * @param key
     * @param value
     * @return
     */
    public static boolean saveRow(String fileName, String key, RowObject value){
        SharedPreferences preferences = UIGlobal.getApplication().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value.toJsonString());
        return editor.commit();
    }


    /**
     * RowObject转成json后保存为文本
     * @param fileName
     * @param key
     * @param value
     * @return
     */
    public static boolean saveRows(String fileName, String key, List<RowObject> value){
        SharedPreferences preferences = UIGlobal.getApplication().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, JSONSerializer.toJSONString(value));
        return editor.commit();
    }


    /**
     * RowObject转成json后保存为文本
     * @param fileName
     * @param key
     * @param value
     * @return
     */
    public static boolean saveList(String fileName, String key, LinkedList<String> value){
        SharedPreferences preferences = UIGlobal.getApplication().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        String jsonString = JSONSerializer.toJSONString(value);
        editor.putString(key, jsonString);
        return editor.commit();
    }

    /**
     * 获取json文本后转rowObject
     * @param fileName
     * @param key
     * @return
     */
    public static List<String> getList(String fileName, String key){
        SharedPreferences preferences = UIGlobal.getApplication().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        String string = preferences.getString(key, "");
        if(string.startsWith("[")){
            return  JSONSerializer.JSONToObject(string, List.class);
        }else{
            return null;
        }
    }

    /**
     * 获取json文本后转rowObject
     * @param fileName
     * @param key
     * @return
     */
    public static LinkedList<RowObject> getRows(String fileName, String key){
        SharedPreferences preferences = UIGlobal.getApplication().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        String string = preferences.getString(key, "");
        if(JsonUtils.isCanToRows(string)){
            return JsonUtils.jsonToRows(string);
        }else{
            return null;
        }
    }

    /**
     * 清空内容
     * @param fileName
     * @return
     */
    public static boolean clear(String fileName){
        SharedPreferences preferences = UIGlobal.getApplication().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        return editor.commit();
    }


    /**
     * Map转成json文本后保存
     * @param fileName
     * @param key
     * @param value
     * @return
     */
    public static boolean saveMap(String fileName, String key, Map<String,Object> value){
        SharedPreferences preferences = UIGlobal.getApplication().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, JSONSerializer.toJSONString(value));
        return editor.commit();
    }


    /**
     * 获取json文本后转Map
     * @param fileName
     * @param key
     * @return
     */
    public static Map<String,Object> getMap(String fileName, String key){
        SharedPreferences preferences = UIGlobal.getApplication().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        String string = preferences.getString(key, "");
        if(JsonUtils.isCanToRow(string)){
            return JsonUtils.jsonToMap(string);
        }else{
            return null;
        }
    }

}
