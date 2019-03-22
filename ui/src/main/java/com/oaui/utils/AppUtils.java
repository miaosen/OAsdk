package com.oaui.utils;

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
import android.view.Display;
import android.view.WindowManager;

import com.oaui.UIGlobal;

import java.util.ArrayList;
import java.util.List;

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
		intent.setDataAndType(Uri.parse("file://" +path),getMIMEType(path));
		UIGlobal.getApplication().startActivity(intent);
	}

	/**
	 * 调用手机上的应用打开app,在当前app进程下运行,在任务管理器不会出现该app的进程
	 * @param path
	 */
	public static void openFileBySystemApp(Context context,String path) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse("file://" +path),getMIMEType(path));
		context.startActivity(intent);
	}

	/**
	 * 获取文件类型
	 * @param fName
	 * @return
     */
	public static String getMIMEType(String fName) {
		String type="*/*";
		//获取后缀名前的分隔符"."在fName中的位置。
		int dotIndex = fName.lastIndexOf(".");
		if(dotIndex < 0){
			return type;
		}
        /* 获取文件的后缀名*/
		String end=fName.substring(dotIndex,fName.length()).toLowerCase();
		if(end=="")return type;
		//在MIME和文件类型的匹配表中找到对应的MIME类型。
		for(int i=0;i<MIME_MapTable.length;i++){ //MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？
			if(end.equals(MIME_MapTable[i][0]))
				type = MIME_MapTable[i][1];
		}
		return type;
	}
	public static final String[][] MIME_MapTable = {
			// {后缀名，MIME类型}
			{ ".3gp", "video/3gpp" },
			{ ".apk", "application/vnd.android.package-archive" },
			{ ".asf", "video/x-ms-asf" },
			{ ".avi", "video/x-msvideo" },
			{ ".bin", "application/octet-stream" },
			{ ".bmp", "image/bmp" },
			{ ".c", "text/plain" },
			{ ".class", "application/octet-stream" },
			{ ".conf", "text/plain" },
			{ ".cpp", "text/plain" },
			{ ".doc", "application/msword" },
			{ ".docx",
					"application/vnd.openxmlformats-officedocument.wordprocessingml.document" },
			{ ".xls", "application/vnd.ms-excel" },
			{ ".xlsx",
					"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" },
			{ ".exe", "application/octet-stream" },
			{ ".gif", "image/gif" },
			{ ".gtar", "application/x-gtar" },
			{ ".gz", "application/x-gzip" },
			{ ".h", "text/plain" },
			{ ".htm", "text/html" },
			{ ".html", "text/html" },
			{ ".jar", "application/java-archive" },
			{ ".java", "text/plain" },
			{ ".jpeg", "image/jpeg" },
			{ ".jpg", "image/jpeg" },
			{ ".js", "application/x-javascript" },
			{ ".log", "text/plain" },
			{ ".m3u", "audio/x-mpegurl" },
			{ ".m4a", "audio/mp4a-latm" },
			{ ".m4b", "audio/mp4a-latm" },
			{ ".m4p", "audio/mp4a-latm" },
			{ ".m4u", "video/vnd.mpegurl" },
			{ ".m4v", "video/x-m4v" },
			{ ".mov", "video/quicktime" },
			{ ".mp2", "audio/x-mpeg" },
			{ ".mp3", "audio/x-mpeg" },
			{ ".mp4", "video/mp4" },
			{ ".mpc", "application/vnd.mpohun.certificate" },
			{ ".mpe", "video/mpeg" },
			{ ".mpeg", "video/mpeg" },
			{ ".mpg", "video/mpeg" },
			{ ".mpg4", "video/mp4" },
			{ ".mpga", "audio/mpeg" },
			{ ".msg", "application/vnd.ms-outlook" },
			{ ".ogg", "audio/ogg" },
			{ ".pdf", "application/pdf" },
			{ ".png", "image/png" },
			{ ".pps", "application/vnd.ms-powerpoint" },
			{ ".ppt", "application/vnd.ms-powerpoint" },
			{ ".pptx",
					"application/vnd.openxmlformats-officedocument.presentationml.presentation" },
			{ ".prop", "text/plain" }, { ".rc", "text/plain" },
			{ ".rmvb", "audio/x-pn-realaudio" }, { ".rtf", "application/rtf" },
			{ ".sh", "text/plain" }, { ".tar", "application/x-tar" },
			{ ".tgz", "application/x-compressed" }, { ".txt", "text/plain" },
			{ ".wav", "audio/x-wav" }, { ".wma", "audio/x-ms-wma" },
			{ ".wmv", "audio/x-ms-wmv" },
			{ ".wps", "application/vnd.ms-works" }, { ".xml", "text/plain" },
			{ ".z", "application/x-compress" },
			{ ".zip", "application/x-zip-compressed" },
			{ ".amr", "audio/*" },{ ".flac", "audio/*" },{ ".ape", "audio/*" }, { "", "*/*" } };

}
