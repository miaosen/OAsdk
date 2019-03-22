package com.oaui.data;


import com.oaui.utils.JsonUtils;
import com.oaui.utils.StringUtils;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author zms
 * @Created by com.gzpykj.com
 * @Date 2016-3-4
 * @Descrition 键值对(key-value)实体类, 注意 : RowObject在json序列化时重写了RowObject的序列化方法，
 * value的取值只有3种String,RowObject,LinkedList<RowObject>
 */
public class RowObject extends LinkedHashMap<String, Object> implements Serializable {

    private static final long serialVersionUID = 7561153740565098648L;

    private String valueNoKey;


    public RowObject() {
        super();
    }

    public RowObject(String jsonObject) {
        super();
        this.putAll(JsonUtils.jsonToRow(jsonObject));
    }


    public RowObject getRow(String key) {
        RowObject row = (RowObject) get(key);
        if (row == null) {
            return null;
        }
        return row;
    }

    public LinkedList<RowObject> getRows(String key) {
        LinkedList<RowObject> rows = (LinkedList<RowObject>) get(key);
        return rows;
    }

    public List<String> getStringList(String key) {
        LinkedList<String> list = (LinkedList<String>) get(key);
        return list;
    }

    public String getString(String key) {
        Object obj = get(key);
        if (obj == null) {
            return null;
        }
        return obj.toString();
    }

    public Integer getInteger(String key) {
        Object obj = get(key);
        if (obj == null) {
            return null;
        }
        if (obj instanceof String) {
            return Double.valueOf((String) obj).intValue();
        }
        return (Integer) obj;
    }


    public Boolean getBoolean(String key) {
        Object obj = get(key);
        if (obj == null) {
            return false;
        }
        if (obj instanceof String) {
            return obj.toString().equals("true");
        }
        if (obj instanceof Boolean) {
            return (Boolean) obj;
        }
        return false;
    }


    /**
     * 通过表达式获取数据，对象之间用.隔开，数组用[数组]获取；示例：
     * { "data":{ "forecast":[ { "date":"11点", "high":"10米", "fengli":"结果1" }, { "date":"12点", "high":"20米", "fengli":"结果2" } ],"wendu":"29" }, "status":1000, "desc":"OK" }
     *
     * @param expression 获取其中fengli的值：data.forecast[0].fengli
     * @return
     */
    public Object getLayerData(String expression) {
        Object result = null;
        String[] keys = expression.split("\\.");
        RowObject layerRow = StringUtils.deepCopyObject(this);
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            int index = getIndexInExpression(key);
            boolean isGetList = key.contains("[") && key.endsWith("]");
            //Log.i("logtag", "key======" +key+"  "+isGetList);
            if (index != -1) {
                key = key.substring(0, key.lastIndexOf("["));
            }
            Object o = layerRow.get(key);
            if (o != null) {
                if (o instanceof List) {
                    List list = (List) o;
                    if (list.size() > index) {
                        if (index >= 0) {
                            result = list.get(index);
                        } else {
                            result = o;
                        }
                        if (result != null && result instanceof RowObject) {
                            layerRow.putAll((Map<String, Object>) result);
                        } else {
                            layerRow.clear();
                        }
                    } else {
                        i = keys.length;
                        layerRow.clear();
                        result = null;
                    }
                } else if (!(o instanceof List) && isGetList) {
                    result = null;
                    layerRow.clear();
                } else if (o instanceof RowObject) {
                    layerRow.putAll((Map<String, Object>) o);
                    result = o;
                } else {
                    result = o;
                    layerRow.clear();
                }
            } else {
                i = keys.length;
                layerRow.clear();
                result = null;
            }

        }
        return result;
    }

    private int getIndexInExpression(String key) {
        if (key.endsWith("]") && key.contains("[")) {
            String index = key.substring(key.lastIndexOf("[") + 1, key.length() - 1);
            if (StringUtils.isNumeric(index)) {
                return Integer.parseInt(index);
            } else {
                return -1;
            }
        }
        return -1;
    }

    public String getValueNoKey() {
        return valueNoKey;
    }

    public void addValueNoKey(String valueNoKey) {
        if(valueNoKey==null){
            this.valueNoKey = valueNoKey;
        }else{
            this.valueNoKey = this.valueNoKey+","+ valueNoKey;
        }
    }
}
