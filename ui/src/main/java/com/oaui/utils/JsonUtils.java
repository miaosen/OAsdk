package com.oaui.utils;

import com.oaui.data.RowObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class JsonUtils {


    /**
     * 判断字符串是否为json格式
     *
     * @param json
     * @return
     */
    public static boolean isValidateJson(String json) {
        try {
            if (json.startsWith("{")) {
                new JSONObject(json);
            } else if (json.startsWith("[")) {
                new JSONArray(json);
            } else {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断字符串是否为json格式
     *
     * @param json
     * @return
     */
    public static boolean isJsonObject(String json) {
        try {
            new JSONObject(json);
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static JSONObject putAll(JSONObject jObj, JSONObject... argjObj) {
        for (int i = 0; i < argjObj.length; i++) {
            JSONObject obj = argjObj[i];
            Iterator it = obj.keys();
            while (it.hasNext()) {
                try {
                    String key = (String) it.next();
                    String value = obj.getString(key);
                    jObj.put(key, value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return jObj;
    }

    /**
     * json字符串转对象
     *
     * @param jsonStr
     * @return
     */
    public static RowObject jsonToRow(String jsonStr) {
        RowObject row = new RowObject();
        if (isValidateJson(jsonStr)) {
            if (jsonStr.startsWith("{")) {
                try {
                    JSONObject jObject = new JSONObject(jsonStr);
                    jsonObjectToRow(row, jObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return row;
    }

    /**
     * 判断json字符串是否是JSONArray
     *
     * @param jsonStr
     * @return
     */
    public static boolean isJSONObject(String jsonStr) {
        if (isValidateJson(jsonStr)) {
            if (jsonStr.startsWith("[")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断json字符串是否是JSONArray
     *
     * @param jsonStr
     * @return
     */
    public static boolean isJSONArray(String jsonStr) {
        if (isValidateJson(jsonStr)) {
            if (jsonStr.startsWith("[")) {
                return true;
            }
        }
        return false;
    }

    /**
     * JSONArray转List<RowObject>
     *
     * @param jsonStr
     * @return
     */
    public static List<RowObject> jsonToRows(String jsonStr) {
        List<RowObject> rows = new LinkedList<RowObject>();
        if (isJSONArray(jsonStr)) {
            try {
                JSONArray jArray = new JSONArray(jsonStr);
                jsonArrayToRows(rows, jArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return rows;
    }


    public static void jsonObjectToRow(RowObject row, JSONObject jObject) {
        try {
            Iterator it = jObject.keys();
            while (it.hasNext()) {
                String key = it.next().toString();
                Object value = jObject.get(key);
                if (value instanceof JSONArray) {
                    JSONArray ja = (JSONArray) value;
                    if (isListString(ja)) {
                        row.put(key, jsonArrayToList(ja));
                    } else {
                        List<RowObject> rows = new LinkedList<RowObject>();
                        jsonArrayToRows(rows, ja);
                        row.put(key, rows);
                    }
                } else if (value instanceof JSONObject) {
                    RowObject mRow = new RowObject();
                    jsonObjectToRow(mRow, (JSONObject) value);
                    row.put(key, mRow);
                } else if (value != null) {
                    row.put(key, value.toString());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void jsonArrayToRows(List<RowObject> rows, JSONArray jArray) {
        try {
            for (int i = 0; i < jArray.length(); i++) {
                Object object = jArray.get(i);
                if (object instanceof JSONObject) {
                    RowObject row = new RowObject();
                    jsonObjectToRow(row, (JSONObject) object);
                    rows.add(row);
                }/* else if (object instanceof RowObject) {
                    //TODO 好像没有这种情况
					rows.add((RowObject) object);
					ViewUtils.toast("jsonArrayToRows object instanceof RowObjet");
				}else{
					RowObject aa = new RowObject();
					aa.put(null, "aaaa");
					aa.put(null, object);
					L.i("=========jsonArrayToRows=============="+object);
					rows.add(aa);
				}*/
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public static List<Object> jsonArrayToList(JSONArray jArray) {
        List<Object> list = new LinkedList<Object>();
        try {
            for (int i = 0; i < jArray.length(); i++) {
                Object o = jArray.get(i);
                list.add(o);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 判断字符串是否可以转Row
     *
     * @param json
     * @return
     */
    public static boolean isCanToRow(String json) {
        if (isValidateJson(json) && json.startsWith("{") && json.endsWith("}")) {
            return true;
        }
        return false;
    }

    /**
     * 判断字符串是否可以转Row
     *
     * @param json
     * @return
     */
    public static boolean isCanToRows(String json) {
        if (isValidateJson(json) && json.startsWith("[") && json.endsWith("]")) {
            return true;
        }
        return false;
    }


    ///**
    // * json字符串转对象
    // *
    // * @param jsonStr
    // * @return
    // */
    //public static Map<String, Object> jsonToMap(String jsonStr) {
    //    Map<String, Object> map = new LinkedHashMap<String, Object>();
    //    if (isValidateJson(jsonStr)) {
    //        if (jsonStr.startsWith("{")) {
    //            try {
    //                JSONObject jObject = new JSONObject(jsonStr);
    //                jsonObjectToMap(map, jObject);
    //            } catch (JSONException e) {
    //                e.printStackTrace();
    //            }
    //        }
    //    }
    //    return map;
    //}


    //public static void jsonObjectToMap(Map<String, Object> map, JSONObject jObject) {
    //    try {
    //        Iterator it = jObject.keys();
    //        while (it.hasNext()) {
    //            String key = it.next().toString();
    //            Object value = jObject.get(key);
    //            if (value instanceof JSONArray) {
    //                List<RowObject> rows = new LinkedList<RowObject>();
    //                jsonArrayToRows(rows, (JSONArray) value);
    //                map.put(key, rows);
    //            } else if (value instanceof JSONObject) {
    //                RowObject mRow = new RowObject();
    //                jsonObjectToMap(mRow, (JSONObject) value);
    //                map.put(key, mRow);
    //            } else {
    //                map.put(key, value);
    //            }
    //        }
    //    } catch (JSONException e) {
    //        e.printStackTrace();
    //    }
    //}


    public static boolean isListString(String json) {
        if (isJSONArray(json)) {
            if (json.contains("{")) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public static boolean isListString(JSONArray jsonArray) {
        if (jsonArray.toString().contains("{")) {
            return false;
        } else {
            return true;
        }
    }

}
