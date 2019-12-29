
package cn.oasdk.dlna.dmc;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.support.avtransport.callback.Stop;

import cn.oasdk.dlna.dms.DLNAService;

public abstract class StopCallback extends Stop {

    public StopCallback() {
        super(DLNAService.playerDevice
                .findService(new UDAServiceType("AVTransport")));
    }

    public void failure(ActionInvocation paramActionInvocation, UpnpResponse paramUpnpResponse,
                        String paramString) {
        onResult("停止失败！");
    }

    public void success(ActionInvocation paramActionInvocation) {
        super.success(paramActionInvocation);
        onResult("停止成功！");
    }

    public abstract void onResult(String msg);

}
