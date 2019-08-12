package cn.oasdk.wifi;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import cn.oasdk.R;
import cn.oasdk.dlna.base.BaseActivity;
import cn.oaui.L;
import cn.oaui.annotation.ViewInject;
import cn.oaui.data.JSONSerializer;
import cn.oaui.data.RowObject;
import cn.oaui.utils.FileUtils;
import cn.oaui.utils.JsonUtils;
import cn.oaui.utils.StringUtils;
import cn.oaui.view.listview.BaseFillAdapter;
import cn.oaui.view.listview.DataListView;
import cn.oaui.view.tiplayout.TipLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2019-01-10  09:48
 * @Descrition
 */

public class WifiAct extends BaseActivity {

    private WifiP2pManager mWifiP2pManager;
    private WifiP2pManager.Channel mChannel;

    @ViewInject
    DataListView dataListView;


    String groupOwnerAddress="";

    ProgressDialog progressDialog;

    @Override
    public void initConfig() {
        mWifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mWifiP2pManager.initialize(this, getMainLooper(), new WifiP2pManager.ChannelListener() {
            @Override
            public void onChannelDisconnected() {
                findWifi();
            }
        });
        WifiListenBroadcast broadcastReceiver = new WifiListenBroadcast(mWifiP2pManager, mChannel);
        registerReceiver(broadcastReceiver, broadcastReceiver.getIntentFilter());
        findWifi();

        //mWifiP2pManager.requestPeers(mChannel, new WifiP2pManager.PeerListListener() {
        //    @Override
        //    public void onPeersAvailable(WifiP2pDeviceList peers) {
        //        L.i("=========onPeersAvailable=============="+peers.getDeviceList());
        //    }
        //});
    }

    private void findWifi() {
        mWifiP2pManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int reasonCode) {
            }
        });
    }

    @Override
    public int getContentView() {
        return R.layout.wifi_act;
    }

    @Override
    public void onViewCreate() {
        dataListView.setOnTipListener(new TipLayout.OnTipListener() {
            @Override
            public void onRefresh() {
                findWifi();
            }

            @Override
            public void onLoadMore() {

            }
        });
        dataListView.setOnItemClickListener(new BaseFillAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View convertView, final RowObject row, int position) {
                if(WifiP2pDevice.CONNECTED==row.getInteger("status")){
                    //Message msg=new Message();
                    //msg.obj=row;
                    //handler.sendMessage(msg);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            sendFile(groupOwnerAddress);
                        }
                    }).start();
                }else{
                    connect(row);
                }

            }
        });
        dataListView.setOnItemModifylistenert(new DataListView.OnItemModifylistenert() {
            @Override
            public void setItemView(View convertView, RowObject row, int position, BaseFillAdapter.ViewHolder holder) {
                TextView isConnect= (TextView) holder.views.get("isConnect");
                if(WifiP2pDevice.AVAILABLE==row.getInteger("status")){
                    isConnect.setText("可用的");
                    isConnect.setTextColor(getResources().getColor(R.color.text_color));
                }else  if(WifiP2pDevice.INVITED==row.getInteger("status")){
                    isConnect.setText("邀请中...");
                    isConnect.setTextColor(getResources().getColor(R.color.text_color));
                }else  if(WifiP2pDevice.CONNECTED==row.getInteger("status")){
                    isConnect.setText("已连接");
                    isConnect.setTextColor(getResources().getColor(R.color.blue));
                }else  if(WifiP2pDevice.FAILED==row.getInteger("status")){
                    isConnect.setText("连接失败！");
                    isConnect.setTextColor(getResources().getColor(R.color.red));
                }else{
                    isConnect.setText("未知");
                    isConnect.setTextColor(getResources().getColor(R.color.red));
                }
            }
        });
        initProgress();
    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int progress= (int) msg.obj;
            progressDialog.setProgress(progress);
            L.i("=========handleMessage=============="+progress);
            if(progress==100){
                progressDialog.dismiss();
            }else{
                if(!progressDialog.isShowing()){
                    progressDialog.show();
                }
            }
        }
    };

    private void connect(RowObject deviceInfo) {
        WifiP2pConfig config = new WifiP2pConfig();
        if (config.deviceAddress != null && deviceInfo != null) {
            config.deviceAddress = deviceInfo.getString("deviceAddress");
            config.wps.setup = WpsInfo.PBC;
            //showLoadingDialog("正在连接 " + mWifiP2pDevice.deviceName);
            mWifiP2pManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    L.e("connect onSuccess");
                }
                @Override
                public void onFailure(int reason) {
                    L.i("=========onFailure=============="+reason);
                    //showToast("连接失败 " + reason);
                    //dismissLoadingDialog();
                }
            });
        }
    }

    private static final int PORT = 4786;
    private static final String TAG = "logtag";
    protected Boolean sendFile(String... strings) {
        //File file = new File(FileUtils.getSDCardPath()+"/icon_tocommit.png");
        //File file = new File(FileUtils.getSDCardPath()+"/test.docx");
        File file = new File(FileUtils.getSDCardPath()+"/fly.mp4");

        L.i("=========sendFile=============="+file.exists()+file.getAbsolutePath());
        L.i("文件的MD5码值是：" + StringUtils.getMd5(file));
        Socket socket = null;
        OutputStream outputStream = null;
        ObjectOutputStream objectOutputStream = null;
        InputStream inputStream = null;
        try {
            socket = new Socket();
            socket.bind(null);
            socket.connect((new InetSocketAddress(strings[0], PORT)), 10000);
            outputStream = socket.getOutputStream();
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(file);
            inputStream = new FileInputStream(file);
            long fileSize = file.length();
            long total = 0;
            byte buf[] = new byte[1024];
            int len = 0;

            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
                total += len;
                int progress = (int) ((total * 100) / fileSize);
                L.i("文件发送进度：" + progress);
                Message msg=new Message();
                msg.obj=progress;
                handler.sendMessage(msg);
            }
            Message msg=new Message();
            msg.obj=100;
            handler.sendMessage(msg);
            outputStream.close();
            objectOutputStream.close();
            inputStream.close();
            socket.close();
            outputStream = null;
            objectOutputStream = null;
            inputStream = null;
            socket = null;
            Log.e(TAG, "文件发送成功");
            return true;
        } catch (Exception e) {
            //e.printStackTrace();
            Log.e(TAG, "文件发送异常 Exception: " + e.getMessage());
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //if (objectOutputStream != null) {
            //    try {
            //        objectOutputStream.close();
            //    } catch (IOException e) {
            //        e.printStackTrace();
            //    }
            //}
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private void initProgress() {
         progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("正在发送文件");
        progressDialog.setMax(100);
    }

    @Override
    public void initData() {
    }

    class WifiListenBroadcast extends BroadcastReceiver {

        private static final String TAG = "WifiListenBroadcast";

        private WifiP2pManager mWifiP2pManager;

        private WifiP2pManager.Channel mChannel;


        public WifiListenBroadcast(WifiP2pManager wifiP2pManager, WifiP2pManager.Channel channel) {
            mWifiP2pManager = wifiP2pManager;
            mChannel = channel;
        }

        public  IntentFilter getIntentFilter() {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
            return intentFilter;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "接收到广播： " + intent.getAction());
            if (!TextUtils.isEmpty(intent.getAction())) {
                switch (intent.getAction()) {
                    // 用于指示 Wifi P2P 是否可用
                    case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION: {
                        int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                        if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                            //mDirectActionListener.wifiP2pEnabled(true);
                        } else {
                            //mDirectActionListener.wifiP2pEnabled(false);
                            List<WifiP2pDevice> wifiP2pDeviceList = new ArrayList<>();
                            //mDirectActionListener.onPeersAvailable(wifiP2pDeviceList);
                        }
                        break;
                    }
                    // 对等节点列表发生了变化
                    case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION: {
                        mWifiP2pManager.requestPeers(mChannel, new WifiP2pManager.PeerListListener() {
                            @Override
                            public void onPeersAvailable(WifiP2pDeviceList peers) {
                                L.i("=========onPeersAvailable==============" + peers.getDeviceList());
                                L.i("=========onPeersAvailable=============="+ JSONSerializer.toJSONString(peers));
                                if(peers.getDeviceList()!=null){
                                    List<RowObject> rows = JsonUtils.jsonToRows(JSONSerializer.toJSONString(peers.getDeviceList()));
                                    dataListView.getRows().clear();
                                   L.i("=========onPeersAvailable=============="+rows);
                                    dataListView.setValue(rows);
                                    //dataListView.notifyDataSetChanged();
                                }

                                //mDirectActionListener.onPeersAvailable(peers.getDeviceList());
                            }
                        });
                        break;
                    }
                    // Wifi P2P 的连接状态发生了改变
                    case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION: {
                        NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                        if (networkInfo.isConnected()) {
                            mWifiP2pManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
                                @Override
                                public void onConnectionInfoAvailable(WifiP2pInfo info) {
                                    //mDirectActionListener.onConnectionInfoAvailable(info);
                                  L.i("=========onConnectionInfoAvailable=============="+JSONSerializer.toJSONString(mChannel));
                                    L.i("=========onConnectionInfoAvailable=============="+JSONSerializer.toJSONString(info));
                                    groupOwnerAddress=info.groupOwnerAddress.getHostAddress();
                                }
                            });
                            Log.e(TAG, "已连接p2p设备");
                        } else {
                            //mDirectActionListener.onDisconnection();
                            Log.e(TAG, "与p2p设备已断开连接");
                        }
                        break;
                    }
                    //本设备的设备信息发生了变化
                    case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION: {
                        //mDirectActionListener.onSelfDeviceAvailable((WifiP2pDevice) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));
                        break;
                    }
                }
            }
        }
    }
}
