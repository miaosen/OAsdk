package cn.oasdk.base;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import cn.oaui.utils.StatusBarUtil;


public class BaseAct extends AppCompatActivity {

    public Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //状态栏设置为主题色
        StatusBarUtil.StatusBarLightMode(this);
        super.onCreate(savedInstanceState);
        context=this;

    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(getContext(context));
    }

    public Context getContext(Context context) {
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // 8.0需要使用createConfigurationContext处理
        //    return updateResources(context);
        //} else {
            return context;
        //}
    }

    //@TargetApi(Build.VERSION_CODES.N)
    //private Context updateResources(Context context) {
    //    Resources resources = context.getResources();
    //    Locale locale = LanguageSwitchAct.getSettLocle();// getSetLocale方法是获取新设置的语言
    //    Configuration configuration = resources.getConfiguration();
    //    configuration.setLocale(locale);
    //    configuration.setLocales(new LocaleList(locale));
    //    return context.createConfigurationContext(configuration);
    //}

}
