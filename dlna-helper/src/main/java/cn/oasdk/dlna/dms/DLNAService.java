package cn.oasdk.dlna.dms;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;

import java.util.LinkedList;
import java.util.List;

import cn.oaui.UIGlobal;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2019-07-04  15:02
 * @Descrition
 */
public class DLNAService {


    public static List<Device> devices = new LinkedList<>();

    //充当服务的设备
    public static LocalDevice serviceDevice;

    //播放设备
    public static Device playerDevice;

    public static AndroidUpnpService upnpService;


    public static void searchDevice() {
        upnpService.getControlPoint().search();
    }


    public static void reSearchDevice() {
        upnpService.getRegistry().removeAllRemoteDevices();
        upnpService.getRegistry().removeAllLocalDevices();
        upnpService.getControlPoint().search();
    }


    public static void bindService(final DefaultRegistryListener defaultRegistryListener,
                                   final Activity activity,
                                   final Handler handler
    ) {
        UIGlobal.getApplication().bindService(
                new Intent(UIGlobal.getApplication(), AndroidUpnpServiceImpl.class),
                new ServiceConnection() {

                    @Override
                    public void onServiceConnected(ComponentName className, IBinder service) {
                        upnpService = (AndroidUpnpService) service;
                        // Get ready for future device advertisements
                        Registry registry = upnpService.getRegistry();
                        registry.addListener(defaultRegistryListener);
                        try {
                            //启动媒体服务
                            final MediaServer mediaServer = new MediaServer(activity);
                            serviceDevice = mediaServer.getLocalDevice();
                            DLNAService.upnpService.getRegistry()
                                    .addDevice(serviceDevice);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    MediaServer.initMediaData(activity);
                                    handler.sendEmptyMessage(1);
                                    //mediaServer.prepareMediaServer(handler);
                                }
                            }).start();
                        } catch (ValidationException e) {
                            e.printStackTrace();
                        }
                        searchDevice();
                    }

                    public void onServiceDisconnected(ComponentName className) {
                        upnpService = null;
                    }
                },
                Context.BIND_AUTO_CREATE
        );
    }

}
