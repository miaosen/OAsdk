package cn.oaui.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.view.Display;
import android.view.WindowManager;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.oaui.UIGlobal;

/**
 * @Created by com.gzpykj.com
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
		PackageManager pManager = UIGlobal.getApplication().getPackageManager();
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
		Context context=UIGlobal.getApplication();
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
		ConnectivityManager connectivity = (ConnectivityManager) UIGlobal.getApplication()
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
		ConnectivityManager cm = (ConnectivityManager) UIGlobal.getApplication()
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
		final float scale =UIGlobal.getApplication().getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}


	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(float dpValue) {
		final float scale =  UIGlobal.getApplication().getResources().getDisplayMetrics().density;
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
		WindowManager windowManager = (WindowManager) UIGlobal.getApplication().getSystemService(Context.WINDOW_SERVICE);
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
			statusHeight = UIGlobal.getApplication().getResources()
					.getDimensionPixelSize(height);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusHeight;
	}



	/**
	 * @param context
	 * @return 判断当前手机是否是全屏
	 */
	public static boolean isFullScreen(Context context) {
		Activity activity= (Activity) context;
		int flag =activity.getWindow().getAttributes().flags;
		if((flag & WindowManager.LayoutParams.FLAG_FULLSCREEN)
				== WindowManager.LayoutParams.FLAG_FULLSCREEN) {
			return true;
		}else {
			return false;
		}
	}

	/**
	 * 调用手机上的应用打开app,以打开新app的方式运行，在任务管理器会出现该app的进程
	 * @param path
     */
	public static void openFileBySystemApp(String path) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse("file://" +path),FileUtils.getMIMEType(path));
		UIGlobal.getApplication().startActivity(intent);
	}

	/**
	 * 调用手机上的应用打开app,在当前app进程下运行,在任务管理器不会出现该app的进程
	 * @param path
	 */
	public static void openFileBySystemApp(Context context,String path) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse("file://" +path),FileUtils.getMIMEType(path));
		context.startActivity(intent);
	}



	/**
	 * 获取本机ip
	 * @return
	 * @throws UnknownHostException
	 */
	public static InetAddress getLocalIpAddress()  {
		WifiManager wifiManager = (WifiManager) UIGlobal.getApplication().getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		try {
			return InetAddress.getByName(String.format(Locale.getDefault(), "%d.%d.%d.%d",
                    (ipAddress & 0xff),
                    (ipAddress >> 8 & 0xff),
                    (ipAddress >> 16 & 0xff),
                    (ipAddress >> 24 & 0xff)));
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return null;
		}
	}

}
