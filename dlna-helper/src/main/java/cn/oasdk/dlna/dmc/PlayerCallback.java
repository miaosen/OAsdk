package cn.oasdk.dlna.dmc;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.avtransport.callback.Play;

import cn.oaui.L;

public abstract class PlayerCallback extends Play {


	public PlayerCallback(Service paramService) {
		super(paramService);
	}

	@Override
	public void failure(ActionInvocation paramActionInvocation,
						UpnpResponse paramUpnpResponse, String paramString) {
		String msg="播放失败:"+paramString+paramUpnpResponse.getStatusMessage()+paramActionInvocation.getFailure().getMessage();
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
		L.e( "播放成功");
		onResult("播放成功！");
	}

	public abstract void onResult(String msg);

}
