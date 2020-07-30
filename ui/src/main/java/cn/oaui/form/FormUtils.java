package cn.oaui.form;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.oaui.L;
import cn.oaui.ResourceHold;
import cn.oaui.data.Row;
import cn.oaui.utils.StringUtils;
import cn.oaui.utils.ViewUtils;
import cn.oaui.view.calendar.DateButton;

import static android.R.attr.value;

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
            if (tag != null&& tagNotContainKeyWord(tag)) {
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
            if (childtag != null&& tagNotContainKeyWord(childtag)) {
                String strChildtag = childtag.toString();
                allchildren.put(strChildtag, view);
            }else if(view.getId()>0){
                String key = ResourceHold.getNameById(view.getId());
                allchildren.put(key, view);
            }
        }
        return allchildren;
    }

    private static boolean tagNotContainKeyWord(Object tag) {
        return !tag.equals("not_null")&&!tag.equals("necessary");

    }

    private static boolean isIgnore(View vp) {
       if(vp instanceof FormAdpater && ((FormAdpater) vp).isScanAsOne()){
           return true;
       }else if(vp instanceof RadioGroup){
           return true;
       }
        return false;
    }

    public static Map<String, Object> getContentMap(View view) {
        return getContentMap(ViewUtils.getAllChildViews(view));
    }



    public static Map<String, Object> getContentMap(List<View> allChildViews) {
        Map<String, Object> submitMap = new LinkedHashMap<String, Object>();
        for (int i = 0; i < allChildViews.size(); i++) {
            View view = allChildViews.get(i);
            if (view.getId() > 0) {
                getContentMap(view, submitMap);
            }
        }
        return submitMap;
    }

    public static Row getContentRow(Map<String, View> viewWithIdName) {
        Row row = new Row();
        for (String tagName : viewWithIdName.keySet()) {
            View view = viewWithIdName.get(tagName);
            Object contentValue = getContentValue(view);
            if(StringUtils.isNotEmpty(contentValue)){
                row.put(tagName, value);
            }
        }
        return row;
    }

    public static Row getContentRow(View viewGroup) {
        Map<String, View> mapView = getMapView(viewGroup);
        Row row = new Row();
        for (String tagName : mapView.keySet()) {
            View view = mapView.get(tagName);
            Object value = getContentValue(view);
            L.i("============getContentRow==========="+value);
            if(StringUtils.isNotEmpty(value)){
                row.put(tagName, value);
            }
        }
        return row;
    }



    public static Map<String, Object> getContentMap(View view,
                                                    Map<String, Object> contentValue) {
        String idName = ResourceHold.getNameById(view.getId());
        getContentValue(view, idName, contentValue);
        return contentValue;
    }

    public static Map<String, Object> getContentValue(View view, String idName,
                                                               Map<String, Object> contentValue) {
        if (StringUtils.isNotEmpty(idName)) {
            //L.i("=========getContentMap=============="+idName);
            if ( view instanceof EditText || view instanceof FormAdpater||view instanceof DateButton
                    || view instanceof RadioGroup || (view.getTag() != null )){
                Object contentValue1 = getContentValue(view);
                contentValue.put(idName, contentValue1);
            }
        }
        return contentValue;
    }


    public static Object getContentValue(View view) {
        Object value = null;
        if (view instanceof FormAdpater) {
            FormAdpater fv = (FormAdpater) view;
            value = fv.getValue();
        } else if (view instanceof EditText) {
            EditText ed = (EditText) view;
            value = ed.getText() + "";
        } else if (view instanceof DateButton) {
            DateButton dbtn = (DateButton) view;
            value = dbtn.getText() + "";
        } else if (view instanceof RadioGroup) {
            RadioGroup rg = (RadioGroup) view;
            value = ViewUtils
                    .getRadioGroupValue(rg);
        } else if (view.getTag() != null ) {
            //以form_rg_tag为tag包裹的多选按钮组
            if (view instanceof ViewGroup) {
                boolean firstCb = true;
                List<View> allChildViews = getAllFormView(view);
                for (int i = 0; i < allChildViews.size(); i++) {
                    View view1 = allChildViews.get(i);
                    if (view1 instanceof CheckBox) {
                        String text;
                        CheckBox cb = (CheckBox) view1;
                        if (cb.isChecked()) {
                            if (cb.getTag() != null) {
                                text = cb.getTag() + "";
                            } else {
                                text = cb.getText() + "";
                            }
                            if (firstCb) {
                                value = text;
                                firstCb = false;
                            } else {
                                value = value + "," + text;
                            }
                        }
                    } else if (view1 instanceof FormAdpater) {
                        FormAdpater fv = (FormAdpater) view1;
                        if (firstCb) {
                            value = fv.getValue();
                            firstCb = false;
                        } else {
                            value = value + "," + fv.getValue();
                        }
                    }
                }
            }
        }
        return value;
    }


    public static void setContentValues(View views, Row row) {
        List<View> allChildViews = ViewUtils.getAllChildViews(views);
        for (int i = 0; i < allChildViews.size(); i++) {
            View view = allChildViews.get(i);
            if(view.getId()>0){
                String nameById = ResourceHold.getNameById(view.getId());
                String value = row.getString(nameById);
                if(StringUtils.isNotEmpty(nameById)&&StringUtils.isNotEmpty(value)){
                    setContentValue(view,value);
                }
            }
        }
    }

    public static void setContentValue(View view, Object value) {
        if (view instanceof FormAdpater) {
            FormAdpater fv = (FormAdpater) view;
            if (StringUtils.isNotEmpty(value)) {
                fv.setValue(value);
            }
        } else if (view instanceof DateButton) {
            DateButton dbtn = (DateButton) view;
            dbtn.setText(value + "");
        } else if (view instanceof CheckBox) {
            CheckBox cb = (CheckBox) view;
            cb.setChecked(true);
        } else if (view instanceof RadioGroup) {
            RadioGroup rg = (RadioGroup) view;
            ViewUtils.fillRadioGroup(rg, value.toString());
        } else if (view instanceof TextView) {
            TextView ed = (TextView) view;
            ed.setText(value + "");
        } else if (view.getTag() != null && tagNotContainKeyWord(view.getTag())) {
            //以form_rg_tag为tag包裹的多选按钮组
            if (view instanceof ViewGroup && value != null) {
                String[] split = value.toString().split(",");
                List<String> strings = StringUtils.toArrays(split);
                List<View> allChildViews = getAllFormView(view);
                FormAdpater FormAdpater = null;
                Iterator<View> iterator = allChildViews.iterator();
                while (iterator.hasNext()) {
                    View view1 = iterator.next();
                    if (view1 instanceof CheckBox) {
                        CheckBox cb = (CheckBox) view1;
                        String targetValue = "";
                        if (cb.getTag() != null) {
                            targetValue = cb.getTag() + "";
                        } else {
                            targetValue = cb.getText() + "";
                        }
                        if (strings.contains(targetValue)) {
                            cb.setChecked(true);
                            strings.remove(targetValue);
                        }
                    } else if (view1 instanceof FormAdpater) {
                        FormAdpater = (FormAdpater) view1;
                    }
                }
                if (FormAdpater != null && strings.size() > 0) {
                    String valueOther = "";
                    for (int i = 0; i < strings.size(); i++) {
                        if (i == 0) {
                            valueOther = strings.get(i);
                        } else {
                            valueOther = valueOther + "," + strings.get(i);
                        }
                    }
                    FormAdpater.setValue(valueOther);
                }
            }
        }
    }


    /**
     * @note 获取一个View下所有的view
     */
    public static List<View> getAllFormView(View view) {
        List<View> allchildren = new ArrayList<View>();
        if (view instanceof ViewGroup) {
            ViewGroup vp = (ViewGroup) view;
            allchildren.add(vp);
            for (int i = 0; i < vp.getChildCount(); i++) {
                View viewchild = vp.getChildAt(i);
                if (viewchild instanceof ViewGroup) {
                    if (viewchild instanceof FormAdpater) {
                        FormAdpater FormAdpater = (FormAdpater) viewchild;
                        if (!FormAdpater.isScanAsOne()) {
                            allchildren.addAll(getAllFormView(viewchild));
                        } else {
                            allchildren.add(viewchild);
                        }
                    } else {
                        allchildren.addAll(getAllFormView(viewchild));
                    }
                } else if (viewchild.getId() > 0) {
                    allchildren.add(viewchild);
                }
            }
        } else if (view.getId() > 0) {
            allchildren.add(view);
        }
        return allchildren;
    }

}
