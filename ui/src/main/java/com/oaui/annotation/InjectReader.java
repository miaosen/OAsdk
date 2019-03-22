package com.oaui.annotation;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;

import com.oaui.ResourceHold;

import java.lang.reflect.Field;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2016/9/7 11:08
 * @Descrition View相关的注解读取
 */
public class InjectReader {

    /**
     * 读取activity类及其子类的注解,查找activity加载的view并给变量赋值
     *
     * @param activity
     */
    public static void injectAllFields(Activity activity) {
        // 读取activity父类注解
        for (Class<?> clazz = activity.getClass(); clazz != Activity.class; clazz = clazz
                .getSuperclass()) {
            try {
                injectAllFields(activity, null, clazz);
            } catch (Exception e) {
                // 这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
                // 如果这里的异常打印或者往外抛，则就不会执行clazz =
                // clazz.getSuperclass(),最后就不会进入到父类中了
            }
        }
    }


    /**
     * 解析object类及其子类的注解,查找injectView并给变量赋值
     *
     * @param object     变量所在的类
     * @param injectView 变量所在的view
     */
    public static void injectAllFields(Object object, View injectView) {
        // 读取对象父类注解
        if (object.getClass().getSuperclass() != null) {
            for (Class<?> clazz = object.getClass(); clazz != Object.class; clazz = clazz
                    .getSuperclass()) {
                try {
                    injectAllFields(object, injectView, clazz);
                } catch (Exception e) {
                    // 这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
                    // 如果这里的异常打印或者往外抛，则就不会执行clazz =
                    // clazz.getSuperclass(),最后就不会进入到父类中了
                }
            }
        }
        //读取当前对象注解
        injectAllFields(object, injectView, null);
    }

    /**
     * 解析view类的注解,并查找view给注解的变量赋值,适应于自定义view
     *
     * @param view
     */
    public static void injectAllFields(View view) {
        injectAllFields(view, view);
    }


    /**
     * 解析注解基础方法
     */
    public static void injectAllFields(Object object, View injectView,
                                       Class<?> clazz) {
        try {
            if (clazz == null) {
                clazz = object.getClass();
            }
            Field[] fields = clazz.getDeclaredFields();// 获得Activity中声明的字段
            for (Field field : fields) {
                // 查看这个字段是否有我们自定义的注解类标志的
                if (field.isAnnotationPresent(ViewInject.class)) {
                    field.setAccessible(true);
                    ViewInject inject = field.getAnnotation(ViewInject.class);
                    int id = inject.value();
                    String tag = inject.tag();
                    //L.i("=========injectAllFields=============="+field.getName());
                    if (object instanceof Activity) {
                        Activity activity = (Activity) object;
                        injectView = activity.getWindow().getDecorView();
                    }  else if (object instanceof View) {
                        injectView = (View) object;
                    }
                    if (TextUtils.isEmpty(tag)) {
                        tag = field.getName();
                    }
                    if (injectView.findViewWithTag(tag) != null) {//优先通过tag查找
                        // 给我们要找的变量赋值
                        field.set(object, injectView.findViewWithTag(tag));
                    } else {//通过id查找
                        if (id == 0) {
                            id = ResourceHold.getIdByName(field.getName());
                        }
                        if (injectView.findViewById(id) != null) {
                            // 给我们要找的变量赋值
                            field.set(object, injectView.findViewById(id));
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

}
