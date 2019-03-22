package com.oasdk.data.bluetooth;

import android.bluetooth.BluetoothAdapter;

import com.oaui.UIGlobal;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2019-03-12  11:27
 * @Descrition
 */

public class BlueToothUtils  {

    /**
     * 获取具体位置的经纬度,解决个别机型注册了定位权限但是实际没有定位权限问题，比如小米6
     */
    /*public static void getLocation() {
        // 获取位置管理服务
        LocationManager locationManager;
        String serviceName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) UIGlobal.getApplication().getSystemService(serviceName);
        // 查找到服务信息
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW); // 低功耗
        String provider = locationManager.getBestProvider(criteria, true); // 获取GPS信息
        *//** 这段代码不需要深究，是locationManager.getLastKnownLocation(provider)自动生成的，不加会出错 **//*
        if (ActivityCompat.checkSelfPermission(UIGlobal.getApplication(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(UIGlobal.getApplication(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider); // 通过GPS获取位置
    }*/

    public static String getAddress(){
        String address = BluetoothAdapter.getDefaultAdapter().getAddress();
        if("02:00:00:00:00:00".equals(address)){
            address = android.provider.Settings.Secure.getString(UIGlobal.getApplication().getContentResolver(), "bluetooth_address");
        }
        return address;
    }

}
