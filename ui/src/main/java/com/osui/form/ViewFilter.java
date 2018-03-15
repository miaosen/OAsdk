//package com.esayui.form;
//
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//
//import java.util.LinkedList;
//import java.util.List;
//
///**
// * @author zengmiaosen
// * @email 1510809124@qq.com
// * @git http://git.oschina.net/miaosen/MyUtils
// * @CreateDate 2016/9/7 11:28
// * @Descrition View过滤器,主要负责在数据填充和收集时把一些不需要的view去掉
// */
//public class ViewFilter {
//
//
//    /**
//     * 整体控制
//     **/
//    //只需要过滤当前View的类名，加入到这个数组
//    private List<Class> disableViews =new LinkedList<Class>();
//    Class<?> disableClass[] = new Class[]{
//            TextView.class
//    };
//
//    //需要过滤View及其子类的类名，加入到这个数组
//    private List<Class> disableSubViews =new LinkedList<Class>();
//    Class<?> disableSubclass[] = new Class[]{
//            Button.class
//    };
//
//    /**
//     * 细节控制（优先级高，不受前面2个影响）
//     **/
//    //父类加入disableSubViews，又想其中某个子类添加例外，子类加入到这个数组
//    private List<Class> enableViews = new LinkedList<Class>();
//
//    public static ViewFilter instance;
//
//    public static ViewFilter getInstance(){
//        if(instance==null){
//            instance=new ViewFilter();
//        }
//        return instance;
//    }
//
//    private ViewFilter() {
//        for (Class<?> clz:
//                disableClass ) {
//            disableViews.add(clz);
//        }
//        for (Class<?> clz:
//                disableSubclass ) {
//            disableSubViews.add(clz);
//        }
//    }
//
//
//    /**
//     * 过滤方法
//     * @param view
//     * @return 符合过滤条件返回true
//     */
//    public boolean filter(View view) {
//        if (enableViews.contains(view.getClass())) {
//            return false;
//        }
//        if (disableViews.contains(view.getClass()) ) {
//            return true;
//        }
//        boolean succes=false;
//        for (int i = 0; i <disableSubViews.size() ; i++) {
//            Class cls=disableSubViews.get(i);
//            if(cls.isInstance(view)){
//                succes= true;
//                i=disableSubViews.size();
//            }
//        }
//        return succes;
//    }
//
//
//    /**
//     * 设置不需要获取的view
//     * @param clz
//     */
//    public void setDisableView(Class clz) {
//        this.disableViews.add(clz);
//    }
//
//    public void setDisableView(List<Class> cls) {
//        this.disableViews.addAll(cls);
//    }
//
//    public void setEnableView(Class clz) {
//        this.enableViews.add(clz);
//    }
//
//    public void setEnableView(List<Class> disableType) {
//        this.enableViews.addAll(disableType);
//    }
//
//
//    public void setDisableSubView(Class clz) {
//        this.disableSubViews.add(clz);
//    }
//
//    public void setDisableSubView(List<Class> cls) {
//        this.disableSubViews.addAll(cls);
//    }
//
//
//}
