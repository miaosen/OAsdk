
package cn.oasdk.dlna.dmc;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.avtransport.callback.SetAVTransportURI;

import cn.oaui.L;

public abstract class SetAVTransportURIActionCallback extends SetAVTransportURI {



    public SetAVTransportURIActionCallback(Service paramService, String paramString1,
                                           String paramString2) {
        super(paramService, paramString1, paramString2);
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
