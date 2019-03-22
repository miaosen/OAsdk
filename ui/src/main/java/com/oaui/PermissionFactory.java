package com.oaui;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2018-02-02  15:08
 * @Descrition
 */

public class PermissionFactory {


    public static void requestPerssion(Activity activity, String s) {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE },
                    111);
        }
    }
}
