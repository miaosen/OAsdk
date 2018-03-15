package com.osui.form;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @git http://git.oschina.net/miaosen/MyUtils
 * @CreateDate 2017-01-04  16:42
 * @Descrition 表单与自定义View适配器,
 * 自定义View实现此接口并实现里面的方法就可以被表单工具识别到
 */

public interface FormAdpater {

    /**
     * 数据填充时调用
     *
     * @param object
     */
    void setValue(Object object);

    /**
     * 数据收集时调用
     *
     * @return
     */
    Object getValue();


    /**
     * Form扫描View时是否作为一个整体被识别，比如自定义ListView里面包含许多Editext等控件，我不需要这些被收集到或者填充到就
     * 设置为true
     */
    boolean isScanAsOne();


}
