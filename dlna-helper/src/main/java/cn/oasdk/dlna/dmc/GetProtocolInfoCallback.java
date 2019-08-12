package cn.oasdk.dlna.dmc;

import android.util.Log;

import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.connectionmanager.callback.GetProtocolInfo;
import org.fourthline.cling.support.model.ProtocolInfos;

public abstract class GetProtocolInfoCallback extends GetProtocolInfo {

	private String TAG = "GetProtocolInfoCallback";



	public GetProtocolInfoCallback(Service paramService,
								   ControlPoint paramControlPoint) {
		super(paramService, paramControlPoint);
	}

	public void failure(ActionInvocation paramActionInvocation,
						UpnpResponse paramUpnpResponse, String paramString) {
		Log.e("DMC", "GetProtocolInfo  failure:"+paramActionInvocation.getFailure().getMessage());
		onResult("GetProtocolInfoCallback failed");
		//this.handler.sendEmptyMessage(DMCControlMessage.CONNECTIONFAILED);
	}

	public void received(ActionInvocation paramActionInvocation,
						 ProtocolInfos paramProtocolInfos1, ProtocolInfos paramProtocolInfos2) {
		//this.handler.sendEmptyMessage(DMCControlMessage.CONNECTIONSUCESSED);
		// TODO
		onResult("GetProtocolInfoCallback success");
	}
	public abstract void onResult(String msg);

}
