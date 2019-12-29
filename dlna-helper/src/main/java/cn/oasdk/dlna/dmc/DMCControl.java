package cn.oasdk.dlna.dmc;

import android.util.Log;

import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.support.avtransport.callback.GetPositionInfo;
import org.fourthline.cling.support.avtransport.callback.GetTransportInfo;
import org.fourthline.cling.support.avtransport.callback.Pause;
import org.fourthline.cling.support.avtransport.callback.Seek;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.item.Item;

import java.util.List;

import cn.oasdk.dlna.dms.DLNAService;
import cn.oaui.L;
import cn.oaui.utils.ViewUtils;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2019-08-06  10:00
 * @Descrition
 */

public class DMCControl {


    public static void getProtocolInfos(GetProtocolInfoCallback getProtocolInfoCallback) {
        try {
            Service localService = DLNAService.playerDevice
                    .findService(new UDAServiceType("ConnectionManager"));
            if (localService != null) {
                DLNAService.upnpService.getControlPoint().execute(
                        getProtocolInfoCallback);
            } else {
            }
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }


    public static void getTransportInfo(GetTransportInfo getTransportInfo) {
        try {
            Service localService = DLNAService.playerDevice
                    .findService(new UDAServiceType("AVTransport"));
            if (localService != null) {
                DLNAService.upnpService.getControlPoint().execute(getTransportInfo);
            } else {
            }
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }


    public static void setAvURL(SetAVTransportURIActionCallback setAVTransportURIActionCallback, Item item) {
        try {
            if (DLNAService.playerDevice != null) {
                Service localService = DLNAService.playerDevice
                        .findService(new UDAServiceType("AVTransport"));
                if (localService != null) {
                    List<Res> resources = item.getResources();
                    String url = "";
                    for (int i = 0; i < resources.size(); i++) {
                        url = resources.get(i).getValue();
                        L.i("============setAvURL===========" + url);
                    }
                    String generate = new GenerateXml().generate(item, null);
                    L.i("============setAvURL===========" + generate);
                    DLNAService.upnpService.getControlPoint().execute(
                            setAVTransportURIActionCallback);
                } else {
                    L.e("localService is null");
                }
            } else {
                ViewUtils.toast("请先选择投屏设备！");
            }
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    public static void play(PlayerCallback playerCallback) {
        try {
            DLNAService.upnpService.getControlPoint().execute(
                    playerCallback);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    public static void pause(Pause pause) {
        try {
            DLNAService.upnpService.getControlPoint().execute(
                    pause);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }


    public static void stop(StopCallback stopCallback) {
        try {
            final Service localService = DLNAService.playerDevice
                    .findService(new UDAServiceType("AVTransport"));
            if (localService != null && DLNAService.playerDevice != null) {
                DLNAService.upnpService.getControlPoint().execute(stopCallback);
            } else {
                Log.e("null", "null");
            }
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }


    /**
     * 设置静音
     */
    public static void setMute(SetMuteCalllback setMuteCalllback) {
        try {
            ControlPoint localControlPoint = DLNAService.upnpService
                    .getControlPoint();
            localControlPoint.execute(setMuteCalllback);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }


    public static void getPositionInfo(GetPositionInfo getPositionInfo) {
        try {
            DLNAService.upnpService.getControlPoint().execute(
                    getPositionInfo);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    public static void seek(Seek seek) {
        try {
            DLNAService.upnpService.getControlPoint().execute(
                    seek);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    public static void stopAndPaly(final PlayerCallback playerCallback, final Item curItem, final String url) {
        stop(new StopCallback() {
            @Override
            public void onResult(String msg) {
                try {

                    String generate = new GenerateXml().generate(curItem, null);
                    setAvURL(new SetAVTransportURIActionCallback(
                            url, generate) {
                        @Override
                        public void onResult(String msg) {
                            play(playerCallback);
                        }
                    }, curItem);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
