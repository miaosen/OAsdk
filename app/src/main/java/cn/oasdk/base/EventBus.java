package cn.oasdk.base;

import java.util.HashMap;
import java.util.Map;


/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2019-04-09  11:16
 * @Descrition
 */
public class EventBus {


    public static Map<String,Object> mapBus=new HashMap<>();



    public static void setObject(Object object,String key){
        mapBus.put(key,object);
    }

    public static Object getObject(String key){
        return mapBus.get(key);
    }

    public static void removeObject(String key){
        mapBus.remove(key);
    }
    public interface Map11<W,Q> {

    }
}
