package com.oaui.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.oaui.UIGlobal;

import java.util.ArrayList;
import java.util.List;

import static com.oaui.utils.AppUtils.getScreenHeight;
import static com.oaui.utils.AppUtils.getScreenWidth;


public class ViewUtils {


    /**
     * 获取activity下的最顶层view,包括依赖activity的dialog的view
     *
     * @param activity
     * @return
     */
    public static View getDecorView(Activity activity) {
        return activity.getWindow().getDecorView();
    }

    /**
     * 获取activity加载的layout目录下的xml布局
     *
     * @param context
     * @return
     */
    public static ViewGroup getContentView(Context context) {
        return (ViewGroup) ((ViewGroup) getDecorView(context).findViewById(android.R.id.content)).getChildAt(0);
    }


    public static View getDecorView(Context context) {
        return ((Activity) context).getWindow().getDecorView();
    }

    /**
     * @view 获取一个View下所有的view
     */
    public static List<View> getAllChildViews(View view) {
        List<View> allchildren = new ArrayList<View>();
        if (view instanceof ViewGroup) {
            ViewGroup vp = (ViewGroup) view;
            for (int i = 0; i < vp.getChildCount(); i++) {
                View viewchild = vp.getChildAt(i);
                if(viewchild instanceof ViewGroup){
                    allchildren.addAll(getAllChildViews(viewchild));
                }else{
                    allchildren.add(viewchild);
                }
            }
        }
        allchildren.add(view);
        return allchildren;
    }


    /**
     * 填充RadioGroup 根据传入的值和RadioButton的值是否对应来选中radiobutton
     *
     * @param rg    包裹radiogroup里面的布局
     * @param value 对应着RadioGroup中RadioButton的text
     */
    public static void fillRadioGroup(View rg, String value) {
        if (rg != null) {
            List<View> rgViews = ViewUtils.getAllChildViews(rg);
            for (int i = 0; i < rgViews.size(); i++) {
                if (rgViews.get(i) instanceof RadioButton) {
                    RadioButton radioButton = (RadioButton) rgViews.get(i);
                    String text = "";
                    //优先选tag对应
                    if (radioButton.getTag() != null) {
                        text = (String) radioButton.getTag();
                    } else {
                        text = radioButton.getText() + "";
                    }
                    if (text.equals(value)) {
                        radioButton.setChecked(true);
                    }
                }
            }
        }
    }


    /**
     * 通过资源文件id获取View
     *
     * @param context
     * @param layout
     * @return
     */
    public static View inflatView(Context context, int layout) {
        return LayoutInflater.from(context).inflate(layout, null);

    }


    /**
     * 通过资源文件id获取View
     *
     * @param context
     * @param layout
     * @return
     */
    public static View inflatView(Context context, int layout,ViewGroup root) {
        return LayoutInflater.from(context).inflate(layout, root);

    }

    /**
     * 通过资源文件id获取View
     *
     * @param context
     * @param layout
     * @return
     */
    public static View inflatView(Context context, int layout,ViewGroup root, boolean attachToRoot) {
        return LayoutInflater.from(context).inflate(layout, root,attachToRoot);

    }


    /**
     * 以RadioGroup的id名称为key，并获取其选中的值为value，返回一个HashMap
     *
     * @param rg
     * @return
     */
    public static String getRadioGroupValue(View rg) {
        List<View> rgViews = ViewUtils.getAllChildViews(rg);
        String text = null;
        for (int i = 0; i < rgViews.size(); i++) {
            if (rgViews.get(i) instanceof RadioButton) {
                RadioButton radioButton = (RadioButton) rgViews.get(i);
                if(radioButton.isChecked()){
                    i=rgViews.size();
                   if(radioButton.getTag()!=null){
                       text=radioButton.getTag().toString();
                   }else{
                       text=radioButton.getText()+"";
                   }
                }
            }
        }
        return text;
    }


    /**
     * 弹出提示
     */
    public static void toast(String text) {
        Toast.makeText(UIGlobal.getApplication(), text, Toast.LENGTH_SHORT).show();
    }


    /**
     * 弹出提示
     */
    public static void toastNOInUIThread(String text) {
        Looper.prepare();
        Toast.makeText(UIGlobal.getApplication(), text, Toast.LENGTH_SHORT).show();
        Looper.loop();
    }


    /**
     * 点击view结束当前activity
     *
     * @param view
     */
    public static void finishByClick(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                //String name = context.getClass().getName();
                //L.i("=========onClick=============="+name);
                if (context instanceof Activity) {
                    ((Activity) context).finish();
                } else if (context instanceof ContextWrapper) {
                    Context baseContext = ((ContextWrapper) context).getBaseContext();
                    ((Activity) baseContext).finish();
                }
            }
        });
    }


    public static Drawable getDrawable(int icon_dir) {
        return UIGlobal.getApplication().getResources().getDrawable(icon_dir);

    }


    /**
     * 获取当前屏幕截图，包含状态栏
     *
     * @param view
     * @return
     */
    public static Bitmap getScreenShot(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        int width = getScreenWidth();
        int height = getScreenHeight();
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
        view.destroyDrawingCache();
        return bp;
    }

    /**
     * 获取当前屏幕截图，不包含状态栏
     *
     * @param activity
     * @return
     */
    public static Bitmap snapShotWithoutStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        int width = getScreenWidth();
        int height = getScreenHeight();
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height
                - statusBarHeight);
        view.destroyDrawingCache();
        return bp;

    }


    /**
     * 弹出键盘并对焦
     * @param alertEd
     */
    public static void showKeyboard(EditText alertEd) {
        if(alertEd!=null){
            //设置可获得焦点
            alertEd.setFocusable(true);
            alertEd.setFocusableInTouchMode(true);
            //请求获得焦点
            alertEd.requestFocus();
            //调用系统输入法
            InputMethodManager inputManager = (InputMethodManager) alertEd
                    .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(alertEd, 0);
        }
    }


    /**
     * 弹出键盘
     * @param alertEd
     */
    public static void hideKeyboard(EditText alertEd) {
        if(alertEd!=null){
            //设置可获得焦点
            alertEd.setFocusable(true);
            alertEd.setFocusableInTouchMode(true);
            //请求获得焦点
            alertEd.requestFocus();
            //调用系统输入法
            InputMethodManager inputManager = (InputMethodManager) alertEd
                    .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(alertEd.getWindowToken(), 0);
        }
    }



    /**
     * View或者ViewGroup内的所有子view能不能点击
     *
     * @param viewOrViewGroup
     * @param b
     */
    public static void setClickable(View viewOrViewGroup, boolean b) {
        List<View> allChildViews = getAllChildViews(viewOrViewGroup);
        for (int i = 0; i < allChildViews.size(); i++) {
            View view = allChildViews.get(i);
            view.setClickable(b);
            if (view instanceof EditText) {
                EditText ed = (EditText) view;
                ed.setEnabled(b);
            }
        }
    }

}
