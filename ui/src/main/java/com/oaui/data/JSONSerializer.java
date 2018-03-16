package com.oaui.data;

import com.alibaba.fastjson.JSON;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2018-01-24  10:00
 * @Descrition
 */

public class JSONSerializer {

    public static String toJSONString(Object object) {
        return JSON.toJSONString(object);
    }

    public static <T> T JSONToObject(String jsonStr, Class<T> tClass) {
        return JSON.parseObject(jsonStr, tClass);
    }
}
