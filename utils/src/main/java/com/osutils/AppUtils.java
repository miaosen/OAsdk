package com.osutils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Display;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @Created by gzpykj.com
 * @author zms
 * @Date 2016-3-12
 * @Descrition 获取应用(Application)相关信息工具类
 */
public class AppUtils {

	/**
	 * 查询手机内非系统应用
	 * @return
	 */
	public static List<PackageInfo> getAllApps() {
		List<PackageInfo> apps = new ArrayList<PackageInfo>();
		PackageManager pManager = UtilsGlobal.getApplication().getPackageManager();
		// 获取手机内所有应用
		List<PackageInfo> paklist = pManager.getInstalledPackages(0);
		for (int i = 0; i < paklist.size(); i++) {
			PackageInfo pak = (PackageInfo) paklist.get(i);
			// 判断是否为非系统预装的应用程序
			if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {
				// customs applications
				apps.add(pak);
			}
		}
		return apps;
	}


	
	/**
	 * 获取应用名称
	 * @return
	 */
	public  static String getAppName() {
		PackageManager packageManager = null;
		ApplicationInfo applicationInfo = null;
		Context context=UtilsGlobal.getApplication();
		try {
			packageManager = context.getPackageManager();
			applicationInfo = packageManager.getApplicationInfo(
					context.getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			applicationInfo = null;
		}
		String applicationName = (String) packageManager
				.getApplicationLabel(applicationInfo);
		return applicationName;

	}

	/**
	 * 判断网络是否连接
	 * @return
	 */
	public static boolean isConnected() {
		ConnectivityManager connectivity = (ConnectivityManager) UtilsGlobal.getApplication()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (null != connectivity) {

			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (null != info && info.isConnected()) {
				if (info.getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 判断是否是wifi连接
	 */
	public static boolean isWifi() {
		ConnectivityManager cm = (ConnectivityManager) UtilsGlobal.getApplication()
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (cm == null)
			return false;
		return cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
	}

	/**
	 * 打开网络设置界面
	 */
	public static void openSetting(Activity activity) {
		Intent intent = new Intent("/");
		ComponentName cm = new ComponentName("com.android.settings",
				"com.android.settings.WirelessSettings");
		intent.setComponent(cm);
		intent.setAction("android.intent.action.VIEW");
		activity.startActivityForResult(intent, 0);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(float pxValue) {
		final float scale =UtilsGlobal.getApplication().getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}


	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(float dpValue) {
		final float scale =  UtilsGlobal.getApplication().getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}



	/**
	 * 获取屏幕宽度
	 *
	 * @return
	 */
	public static int getScreenWidth() {
		return getDisplay().getWidth();
	}

	/**
	 * 获取屏幕宽度
	 *
	 * @return
	 */
	public static int getScreenHeight() {
		return getDisplay().getHeight();
	}

	/**
	 * 获取屏幕显示参数
	 *
	 * @return
	 */
	public static Display getDisplay() {
		WindowManager windowManager = (WindowManager) UtilsGlobal.getApplication().getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		return display;
	}

	/**
	 * 获得状态栏的高度
	 * @return
	 */
	public static int getStatusHeight() {
		int statusHeight = -1;
		try {
			Class<?> clazz = Class.forName("com.android.internal.R$dimen");
			Object object = clazz.newInstance();
			int height = Integer.parseInt(clazz.getField("status_bar_height")
					.get(object).toString());
			statusHeight = UtilsGlobal.getApplication().getResources()
					.getDimensionPixelSize(height);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusHeight;
	}


}
