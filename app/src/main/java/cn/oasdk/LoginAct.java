package cn.oasdk;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gzpykj.im.ImGlobal;

import java.io.IOException;

import cn.oahttp.HandlerQueue;
import cn.oahttp.HttpRequest;
import cn.oahttp.callback.StringCallBack;
import cn.oahttp.cookies.CookieManager;
import cn.oasdk.base.BaseAct;
import cn.oasdk.base.GlobalConst;
import cn.oasdk.im.ImDemoAct;
import cn.oaui.L;
import cn.oaui.annotation.ViewInject;
import cn.oaui.data.Row;
import cn.oaui.utils.IntentUtils;
import cn.oaui.utils.JsonUtils;
import cn.oaui.utils.StringUtils;
import cn.oaui.utils.ViewUtils;
import okhttp3.Call;

public class LoginAct extends BaseAct {

    @ViewInject
    EditText password,ed_img_code, name;


    @ViewInject
    TextView tv_sys_name;
    @ViewInject
    private Button btn_login;
    private  String strName = "";
    //private static String strHttpAddr="";
    private String strPwd = "";

    AlertDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        name.setText("GD010300002");
        password.setText("8888");
        //LoadingDialog.Builder loadBuilder =  new LoadingDialog.Builder(context)
        //        .setMessage("登陆中...")
        //        .setCancelable(true)//返回键是否可点击
        //        .setCancelOutside(false);//窗体外是否可点击
        //loadingDialog = loadBuilder.create();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //HttpRequest request1 = new HttpRequest(GlobalConst.IM_SERVICE_PATH+"/actions/rsLoginAction/prepareCheck/a?captcha="+ed_img_code.getText());
                //request1.setCallback(new StringCallBack() {
                //    @Override
                //    public void onSuccess(String text) {
                //        if(StringUtils.isNotEmpty(text)&&text.contains("true")){
               // loadingDialog.show();
                CookieManager.clearAll(getApplication());
                strName = name.getText()+"";
                strPwd = StringUtils.sha1(password.getText()+"");
                if(StringUtils.isNotEmpty(strName)&&StringUtils.isNotEmpty(strPwd)){
                    //String url =GlobalConst.IM_SERVICE_PATH+"/actions/rsLoginAction/login/"+strName+"/"+strPwd+"/";

                    String url = GlobalConst.SERVICE_ROOT +"/actions/sysLoginAction/login/"+strName+"/"+strPwd;
                    L.i("============onClick==========="+url);
                    HttpRequest request = new HttpRequest(url);
                    request.setCallback(new StringCallBack() {
                        @Override
                        public void onSuccess(String text) {
                            //loadingDialog.dismiss();
                            L.i("============onSuccess==========="+text);
                            if(StringUtils.isNotEmpty(text)&&text.contains("true")){
                                Row row = JsonUtils.jsonToRow(text);
                                ImGlobal.NAME= (String) row.getLayerData("data.name");
                                ImGlobal.USER_ID= (String) row.getLayerData("data.mainId");
                                ImGlobal.USER_NAME= (String) row.getLayerData("data.userName");
                                IntentUtils.jump(context, ImDemoAct.class);
                                //    Row row = JSONSerializer.getRow(text);
                                //    String name =row.getLayerData("data.NAME")+"";
                                //    if(StringUtils.isNotEmpty(name)){
                                //        GlobalConst.USER_NAME=row.getLayerData("data.USERNAME") + "";
                                //        GlobalConst.NAME=name;
                                //        GlobalConst.USER_ID=row.getLayerData("data.MAINID")+"";
                                //    }else{
                                //        GlobalConst.USER_NAME=row.getLayerData("data.LICENSE_NO") + "";
                                //        GlobalConst.NAME= row.getLayerData("data.COMPANY_NAME") + "";
                                //        GlobalConst.USER_ID=row.getLayerData("data.MAINID")+"";
                                //    }
                                //    new Thread(networkTask).start();
                            }else{
                                ViewUtils.toast("登陆失败！");
                            }
                        }

                        @Override
                        public void onFailure(Call call, final IOException e) {
                            super.onFailure(call, e);
                            e.printStackTrace();
                            //loadingDialog.dismiss();
                            HandlerQueue.onResultCallBack(new Runnable() {
                                @Override
                                public void run() {
                                    ViewUtils.toast(e.getMessage());
                                }
                            });

                        }
                    });
                    request.sendAsync();
                }else{
                    ViewUtils.toast("账号名称或者密码不能为空！");
                }
                //        }
                //    }
                //});
                //request1.sendAsync();

            }
        });
        ImGlobal.HOST= GlobalConst.SERVICE_ROOT;

        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(
                "com.gzpykj.rcms",
                "com.gzpykj.rcms.activity.SupervisionRecordActivity");
        intent.setComponent(componentName);
        Bundle bundle = new Bundle();
        bundle.putString("mainId", "11111");
        intent.putExtras(bundle);
        startActivity(intent);
        //sendMyRtmp();
    }


    private void sendMyRtmp() {
   /*     new Thread(new Runnable() {
            @Override
            public void run() {*/
        //XMLProtocol.Talk one = new XMLProtocol.Talk();
        //one.we = GlobalConst.NAME;
        //one.talkType = XMLProtocol.Talk.TalkType.VIDEO;
        //one.talkOther = otherName;
        //one.talkMsg = buildJsonMsg();
        ////one.talkMsg = callOrAnswer + rtmpUrl;
        //one.strTimeStamp = DataTimeUtil.getTimeStamp();
        //m_sendInterface.sendTalk(one);

        HttpRequest httpRequest=ImGlobal.createRequest("/actions/imTalkAction/talk");
        httpRequest.addParam("msg","111111111111111111");
        httpRequest.addParam("talkType","4");
        //httpRequest.addParam("talkto_id",otherName);
        Row row=new Row();
        row.put("name",111);
        row.put("mainId",2222);
        httpRequest.addParam("talkTo",row.toJsonString());
        httpRequest.setCallback(new StringCallBack() {
            @Override
            public void onSuccess(String s) {
                L.i("============onSuccess==========="+s);
            }
        });
        L.i("============onSuccess==========="+httpRequest.getParamMap());
        httpRequest.sendAsync();

       /*     }
        }).start();*/

    }

    @Override
    public void initConfig() {

    }

    @Override
    public int getContentView() {
        return  R.layout.login_act;
    }

    @Override
    public void initData() {
        logicTvSize();
    }

    private void logicTvSize() {
        int measuredWidth = name.getMeasuredWidth();
        CharSequence text = tv_sys_name.getText();
        float textSize=measuredWidth*1.00f/text.length();
        tv_sys_name.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
    }



}