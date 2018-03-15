package com.osui.data;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2018-01-16  15:17
 * @Descrition 数据处理器的生命周期，DataCreator的数据
 */
public interface DataProviderObserver {

    /**
     * 初始化
     * @param dataCreator
     */
    void init(DataProvider dataCreator);
    /**
     * 刷新
     */
    void refresh();
    /**
     * 进行中
     */
    void loading();
    /**
     * 完成
     */
    void complate();
    /**
     * 异常
     */
    void error();

}
