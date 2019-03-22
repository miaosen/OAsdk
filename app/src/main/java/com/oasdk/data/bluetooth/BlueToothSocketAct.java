package com.oasdk.data.bluetooth;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.oasdk.R;
import com.oasdk.base.BaseActivity;
import com.oaui.annotation.ViewInject;
import com.oaui.data.RowObject;
import com.oaui.utils.IntentUtils;
import com.oaui.utils.StringUtils;
import com.oaui.view.HeaderView;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2019-03-12  10:31
 * @Descrition
 */

public class BlueToothSocketAct extends BaseActivity {

    RowObject rowDeviece;

    Context context;

    @ViewInject
    HeaderView headerView;

    @ViewInject
    TextView tv_connect_status,btn_socket_client,tv_send_msg;

    @ViewInject
    EditText msg;

    String address;

    @Override
    public void initConfig() {
        rowDeviece = IntentUtils.getRow(getIntent(), "bluetooth_info");
    }

    @Override
    public int getContentView() {
        return R.layout.bluetooth_socket_act;
    }

    @Override
    public void onViewCreate() {
        context=this;
        if(rowDeviece!=null){
            address=rowDeviece.getString("address");
            String name = rowDeviece.getString("name");
            if(StringUtils.isNotEmpty(name)){
                headerView.setTitleText(name);
            }else{
                headerView.setTitleText(address);
            }
        }
        SocketManager.clientConnect(handler,rowDeviece);
        btn_socket_client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocketManager.clientConnect(handler,rowDeviece);
            }
        });
        tv_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocketManager.sendMessage(address,msg.getText()+"");
            }
        });
    }




     Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
             if(msg.what==SocketManager.MSG_TYPE.CONNECTING){
                tv_connect_status.setText("连接中...");
            }else if(msg.what==SocketManager.MSG_TYPE.CONNECTED){
                tv_connect_status.setText("已连接！");
            }else if(msg.what==SocketManager.MSG_TYPE.CONNECT_FAIL){
                tv_connect_status.setText("连接失败！");
            }else if(msg.what==SocketManager.MSG_TYPE.RECEIVE_MSG){
                tv_connect_status.setText("连接失败！");
            }else if(msg.what==SocketManager.MSG_TYPE.CONNECT_BREAK){
                tv_connect_status.setText("连接已断开！");
            }
        }
    };


    @Override
    public void initData() {

    }
}
