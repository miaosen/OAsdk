package cn.oasdk.data.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import cn.oasdk.R;
import cn.oasdk.dlna.base.BaseActivity;
import cn.oaui.L;
import cn.oaui.annotation.ViewInject;
import cn.oaui.data.RowObject;
import cn.oaui.utils.IntentUtils;
import cn.oaui.utils.RowUtils;
import cn.oaui.utils.ViewUtils;
import cn.oaui.view.dialog.extra.TipDialog;
import cn.oaui.view.dialog.extra.WindowTipDialog;
import cn.oaui.view.listview.BaseFillAdapter;
import cn.oaui.view.listview.DataListView;
import cn.oaui.view.tiplayout.TipLayout;

import java.util.LinkedList;
import java.util.List;

import static android.bluetooth.BluetoothDevice.ACTION_PAIRING_REQUEST;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2019-03-08  15:05
 * @Descrition
 */

public class BluetoothDemoAct extends BaseActivity {


    List<RowObject> rows = new LinkedList<RowObject>();

    Context context;


    @ViewInject
    DataListView dataListView;


    @ViewInject
    TextView name,mac;

    BluetoothAdapter btAdapter;

    BluetoothReceive bluetoothReceive;

    @ViewInject
    TextView btn_socket_service,tv_service_status;


    WindowTipDialog tipDialog;

    @Override
    public void initConfig() {
        //BlueToothUtils.getLocation();
        checkLocationPermission();
    }




    protected void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }
    }

    @Override
    public int getContentView() {
        return R.layout.bluetooth_act;
    }

    @Override
    public void onViewCreate() {
        context = this;
        registerReceiver();
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        dataListView.setCustomData(true);
        dataListView.setOnItemModifylistenert(new DataListView.OnItemModifylistenert() {
            @Override
            public void setItemView(View convertView, RowObject row, int position, BaseFillAdapter.ViewHolder holder) {
                TextView isBond = (TextView) holder.views.get("isBond");
                if(BluetoothDevice.BOND_BONDED==row.getInteger("bondState")) {
                    isBond.setTextColor(getResources().getColor(R.color.blue));
                    isBond.setText("已配对");
                }else {
                    isBond.setTextColor(getResources().getColor(R.color.red));
                    isBond.setText("未配对");
                }
                TextView isConnect = (TextView) holder.views.get("isConnect");
                if(row.getBoolean("connected")) {
                    isConnect.setTextColor(getResources().getColor(R.color.blue));
                    isConnect.setText("已连接");
                }else {
                    isConnect.setTextColor(getResources().getColor(R.color.red));
                    isConnect.setText("未连接");
                }
            }
        });
        dataListView.getTipLayout().setEanableLoadMore(false);
        dataListView.setOnTipListener(new TipLayout.OnTipListener() {
            @Override
            public void onRefresh() {
                scanBluetooth();
            }
            @Override
            public void onLoadMore() {
            }
        });
        dataListView.setOnItemClickListener(new BaseFillAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View convertView, RowObject row, int position) {
                Intent intent=new Intent(context,BlueToothSocketAct.class);
                IntentUtils.addRow(intent,row,"bluetooth_info");
                startActivity(intent);
            }
        });
        btn_socket_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocketManager.startService(handler);
            }
        });

        //tipDialog.show();
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==SocketManager.MSG_TYPE.START){
                tv_service_status.setText("服务已启动");
            }else if(msg.what==SocketManager.MSG_TYPE.RECEIVE_MSG){
                L.i("=========handleMessage=============="+msg.obj);

            }
        }
    };


    private void registerReceiver() {
        // 注册广播：找到远程蓝牙设备
        IntentFilter intentFilter = new IntentFilter();
        //发现设备
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        //设备配对状态改变
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        //蓝牙设备状态改变
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        //开始扫描
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        //结束扫描
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        //其它设备请求配对
        intentFilter.addAction(ACTION_PAIRING_REQUEST);
        bluetoothReceive = new BluetoothReceive();
        registerReceiver(bluetoothReceive, intentFilter);
    }


    private void checkBlueTooth() {
        if (btAdapter.isEnabled()) {
            //ViewUtils.toast("正在搜索......");
            // 启动搜索，获得搜索结果
            scanBluetooth();
            showBluetoothInfo();
        } else {
            //ViewUtils.toast("请打开蓝牙......");
            TipDialog tipDialog=new TipDialog(context);
            tipDialog.setTitle("提示");
            tipDialog.setText("蓝牙未打卡，是否打开蓝牙？");
            tipDialog.setOnSureListener(new WindowTipDialog.OnSureListener() {
                @Override
                public void onSure(DialogInterface dialog) {
                    dialog.dismiss();
                    btAdapter.enable();

                }
            });
            tipDialog.show();
        }
    }

    private void showBluetoothInfo() {
        name.setText(btAdapter.getName());
        mac.setText(BlueToothUtils.getAddress());
    }

    private void scanBluetooth() {
        btAdapter.startDiscovery();
        dataListView.getTipLayout().setRefreshText("正在搜索蓝牙设备...");
        dataListView.refresh();
    }
    @Override
    public void initData() {
        checkBlueTooth();
        if(SocketManager.serSocket!=null){
            tv_service_status.setText("服务已启动");
        }
    }



    class BluetoothReceive extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {// 结束扫描
                ViewUtils.toast("搜索完成");
                dataListView.endRefresh();
                //saAdapter.notifyDataSetChanged();
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                short rssi = intent.getExtras().getShort(
                        BluetoothDevice.EXTRA_RSSI);
                System.out.println(device.getName());
                dataListView.notifyDataSetChanged();
                RowObject rowObject = RowUtils.entityToRow(device);
                L.i("=========onReceive=============="+rowObject);
                dataListView.getFillApdater().addRow(rowObject);
                //dataListView.getFillApdater().addRows();
            } else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {//蓝牙开关状态
                //    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int statue = btAdapter.getState();
                switch (statue) {
                    case BluetoothAdapter.STATE_OFF:
                        L.i("蓝牙状态：,蓝牙关闭");
                        //ClsUtils.closeDiscoverableTimeout(mBluetoothAdapter);
                        break;
                    case BluetoothAdapter.STATE_ON:
                        L.i("蓝牙状态：,蓝牙打开");
                        ViewUtils.toast("蓝牙已打开！");
                        //ClsUtils.setDiscoverableTimeout(1000 * 60, mBluetoothAdapter);
                        scanBluetooth();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        L.i("蓝牙状态：,蓝牙正在关闭");
                        //mBluetoothAdapter.cancelDiscovery();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        L.i("蓝牙状态：,蓝牙正在打开");
                        break;
                }
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bluetoothReceive);
    }
}
