package cn.oaui.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.LinkedList;
import java.util.Set;

import cn.oaui.L;
import cn.oaui.data.RowObject;
import cn.oaui.data.SerializableRowObject;

public class IntentUtils {

    public static void jump(Context context, Class<?> cls) {
        Intent in = new Intent(context, cls);
        context.startActivity(in);
    }

    /**
     * 打印intent的所有内容
     *
     * @param in
     */
    public static void logAllIntent(Intent in) {
        Bundle bundle = in.getExtras();
        Set<String> keySet = bundle.keySet(); // 获取所有的Key,
        for (String key : keySet) {
            L.i("logAllIntent========key===" + key + "   value==" + bundle.get(key));
        }
    }

    /**
     * 添加RowObject对象
     *
     * @param in
     * @param row
     * @param key
     * @return
     */
    public static Intent addRow(Intent in, RowObject row, String key) {
        SerializableRowObject serializableRowObject = new SerializableRowObject(row);
        Bundle bundle = new Bundle();
        bundle.putSerializable(key, serializableRowObject);
        in.putExtras(bundle);
        return in;
    }

    /**
     * 获取RowObject对象
     *
     * @param in
     * @param key
     * @return
     */
    public static RowObject getRow(Intent in, String key) {
        SerializableRowObject serializableRowObject = (SerializableRowObject) in
                .getSerializableExtra(key);
        if (serializableRowObject != null) {
            return serializableRowObject.getRowObject();
        } else {
            return null;
        }
    }

    /**
     * 添加RowObject对象
     *
     * @param in
     * @param rows
     * @param key
     * @return
     */
    public static Intent addRows(Intent in, LinkedList<RowObject> rows, String key) {
        SerializableRowObject serializableRowObject = new SerializableRowObject(rows);
        Bundle bundle = new Bundle();
        bundle.putSerializable(key, serializableRowObject);
        in.putExtras(bundle);
        return in;
    }

    /**
     * 获取RowObject对象
     *
     * @param in
     * @param key
     * @return
     */
    public static LinkedList<RowObject> getRows(Intent in, String key) {
        SerializableRowObject serializableRowObject = (SerializableRowObject) in
                .getSerializableExtra(key);
        if (serializableRowObject != null) {
            return serializableRowObject.getRows();
        } else {
            return null;
        }
    }


}
