package com.oaui.form;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.oaui.ResourceHold;
import com.oaui.data.RowObject;
import com.oaui.utils.ViewUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @git http://git.oschina.net/miaosen/MyUtils
 * @CreateDate 2017-08-11  14:44
 * @Descrition
 */

public class FormUtils {

    /**
     * 获取一个View下所有的id不为空的view,并返回一个map
     *
     * @param view
     * @return 以id名称为key的，value为对应的View
     */
    public static Map<String, View> getMapView(View view) {
        Map<String, View> allchildren = new LinkedHashMap<String, View>();
        if (view instanceof ViewGroup) {
            ViewGroup vp = (ViewGroup) view;
            Object tag = vp.getTag();
            if (tag != null) {
                String key = tag.toString();
                allchildren.put(key, vp);
            }else if(vp.getId()>0){
                String key = ResourceHold.getNameById(vp.getId());
                allchildren.put(key, vp);
            }
            if (!isIgnore(view)) {
                for (int i = 0; i < vp.getChildCount(); i++) {
                    View viewchild = vp.getChildAt(i);
                    Object childtag = viewchild.getTag();
                    if (childtag != null) {
                        String strChildtag = childtag.toString();
                        allchildren.put(strChildtag, viewchild);
                    }else if(viewchild.getId()>0){
                        String key = ResourceHold.getNameById(viewchild.getId());
                        allchildren.put(key, viewchild);
                    }
                    allchildren.putAll(getMapView(viewchild));
                }
            }
        } else {
            Object childtag = view.getTag();
            if (childtag != null) {
                String strChildtag = childtag.toString();
                allchildren.put(strChildtag, view);
            }else if(view.getId()>0){
                String key = ResourceHold.getNameById(view.getId());
                allchildren.put(key, view);
            }
        }
        return allchildren;
    }

    private static boolean isIgnore(View vp) {
       if(vp instanceof FormAdpater && ((FormAdpater) vp).isScanAsOne()){
           return true;
       }else if(vp instanceof RadioGroup){
           return true;
       }
        return false;
    }




    public static RowObject getContentValues(Map<String, View> viewWithIdName) {
        RowObject contentMap = new RowObject();
        for (String tagName : viewWithIdName.keySet()) {
            View view = viewWithIdName.get(tagName);
            if (view instanceof FormAdpater) {
                //实现ViewValueAdpater接口的自定义View填充
                Object value = ((FormAdpater) view).getValue();
                if (value != null) {
                    contentMap.put(tagName, value);
                }
            } else if (view instanceof TextView) {
                //获取TextView或者TextView的子类文字
                TextView tv = (TextView) view;
                if (!TextUtils.isEmpty(tv.getText())) {
                    contentMap.put(tagName, tv.getText());
                }
            } else if (view instanceof RadioGroup) {
                RadioGroup rg = (RadioGroup) view;
                String value = ViewUtils.getRadioGroupValue(rg);
                if (value != null) {
                    contentMap.put(tagName, value);
                }
            }
        }
        return contentMap;
    }


}
