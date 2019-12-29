package cn.oasdk.dlna.dmc;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.support.avtransport.callback.Play;

import cn.oasdk.dlna.dms.DLNAService;
import cn.oaui.L;

public abstract class PlayerCallback extends Play {

	public static String SUCCESS_TIP="投屏成功！";



	public PlayerCallback() {
		super(DLNAService.playerDevice
				.findService(new UDAServiceType("AVTransport")));
	}

	@Override
	public void failure(ActionInvocation paramActionInvocation,
						UpnpResponse paramUpnpResponse, String paramString) {
		String msg="投屏失败:"+paramString+paramUpnpResponse.getStatusMessage()+paramActionInvocation.getFailure().getMessage();
		L.e(msg);
		onResult(msg);
	}

	@Override
	public void run() {
		super.run();
	}


	@Override
	public void success(ActionInvocation paramActionInvocation) {
		super.success(paramActionInvocation);
		onResult(SUCCESS_TIP);
	}

	public abstract void onResult(String msg);

}
