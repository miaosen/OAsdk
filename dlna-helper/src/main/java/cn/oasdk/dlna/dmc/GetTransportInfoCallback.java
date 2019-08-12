
package cn.oasdk.dlna.dmc;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.avtransport.callback.GetTransportInfo;
import org.fourthline.cling.support.model.TransportInfo;

public class GetTransportInfoCallback extends GetTransportInfo {




    public GetTransportInfoCallback(Service paramService) {
        super(paramService);
    }

    @Override
    public void failure(ActionInvocation paramActionInvocation, UpnpResponse paramUpnpResponse,
                        String paramString) {
    }

    @Override
    public void received(ActionInvocation paramActionInvocation, TransportInfo paramTransportInfo) {
        //TODO
        //XGF
        
    }



}
