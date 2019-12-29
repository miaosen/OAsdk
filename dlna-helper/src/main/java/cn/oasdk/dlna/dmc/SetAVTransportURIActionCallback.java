
package cn.oasdk.dlna.dmc;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.support.avtransport.callback.SetAVTransportURI;

import cn.oasdk.dlna.dms.DLNAService;
import cn.oaui.L;

public abstract class SetAVTransportURIActionCallback extends SetAVTransportURI {



    public SetAVTransportURIActionCallback(String paramString1,
                                           String paramString2) {
        super(DLNAService.playerDevice
                .findService(new UDAServiceType("AVTransport")), paramString1, paramString2);
    }

    @Override
    public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
        L.i("============failure==========="+invocation.getFailure().getMessage());
        onResult("SetAVTransportURI failed");
    }

    @Override
    public void success(ActionInvocation paramActionInvocation) {
        super.success(paramActionInvocation);
        L.i("============success===========");
        onResult("SetAVTransportURI success");
    }



    public abstract void onResult(String msg);

}
