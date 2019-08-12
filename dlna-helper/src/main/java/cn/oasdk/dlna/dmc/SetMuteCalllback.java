package cn.oasdk.dlna.dmc;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.renderingcontrol.callback.SetMute;

public abstract class SetMuteCalllback extends SetMute {


	public SetMuteCalllback(Service paramService) {
		super(paramService,true);
	}

	public void failure(ActionInvocation paramActionInvocation,
						UpnpResponse paramUpnpResponse, String paramString) {
		onResult("设置静音失败："+paramActionInvocation.getFailure().getMessage());
}

	public void success(ActionInvocation paramActionInvocation) {
		onResult("设置静音成功：");
	}

	public abstract void onResult(String msg);
}
