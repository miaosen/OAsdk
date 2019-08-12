package cn.oasdk.data.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import cn.oaui.L;
import cn.oaui.data.RowObject;
import cn.oaui.utils.ViewUtils;
import cn.oaui.view.dialog.extra.WindowTipDialog;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2019-03-11  15:18
 * @Descrition
 */
public class SocketManager {

    //Context context;

    public static BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();;

    //public static BluetoothSocket clientSocket;

    public static Map<String,BluetoothSocket> mapClientSocket=new LinkedHashMap<String,BluetoothSocket>();

    //public static BluetoothSocket clientSocket;

    public static BluetoothServerSocket serSocket;

    //使用spp协议连接？
    public static UUID SPP_UUID=UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    public static WindowTipDialog tipDialog=new WindowTipDialog();

    public interface MSG_TYPE{
        //服务启动
        int START=0;
        //等待连接
        int WAIT=1;
        //连接中
        int CONNECTING=2;
        //已连接
        int CONNECTED=3;
        //连接失败
        int CONNECT_FAIL=4;
        //连接断开
        int CONNECT_BREAK=5;
        //连接断开
        int RECEIVE_MSG=6;
    }



    public static void startService(Handler handler){
        new ServiceThread(handler).start();
    }


    public static void clientConnect(Handler handler, RowObject rowDevice){
        new ClientThread(handler,rowDevice).start();
    }
    
    /**
     * 服务端，接收连接的线程
     *
     * @author Administrator
     *
     */
    static class ServiceThread extends Thread {

        Handler handler;

        public ServiceThread(Handler handler) {
            this.handler=handler;
        }

        @Override
        public void run() {
            try {
                // 先用本地蓝牙适配器创建一个serversocket
                    serSocket = bluetoothAdapter
                            .listenUsingRfcommWithServiceRecord(
                                    bluetoothAdapter.getName(),SPP_UUID
                                    );
                // 等待连接，该方法阻塞
                handler.sendEmptyMessage(MSG_TYPE.START);
                BluetoothSocket accept = serSocket.accept();
                String address = accept.getRemoteDevice().getAddress();
                mapClientSocket.put(address,accept);
                //if(!mapClientSocket.containsKey(address)){
                //    mapClientSocket.put(address,accept);
                //}else{
                //
                //}
                handler.sendEmptyMessage(MSG_TYPE.CONNECTED);
                //ViewUtils.toast("连接成功");
                new ReadMsg(handler, accept).start();
            } catch (IOException e) {
                ViewUtils.toast("连接失败");
                e.printStackTrace();
            }
        }
    }

    /**
     * 循环读取信息的线程
     *
     * @author Administrator
     *
     */
    static class ReadMsg extends Thread {

        Handler handler;

        BluetoothSocket clientSocket;

        public ReadMsg(Handler handler, BluetoothSocket clientSocket) {
            this.handler=handler;
            this.clientSocket=clientSocket;
        }
        @Override
        public void run() {
            byte[] buffer = new byte[1024];// 定义字节数组装载信息
            int bytes;// 定义长度变量
            InputStream in = null;
            try {
                // 使用socket获得输入流
                in = clientSocket.getInputStream();
                // 一直循环接收处理消息
                while (true) {
                    if ((bytes = in.read(buffer)) != 0) {
                        byte[] buf_data = new byte[bytes];
                        for (int i = 0; i < bytes; i++) {
                            buf_data[i] = buffer[i];
                        }
                        String msg = new String(buf_data);// 最后得到String类型消息
                        L.i("=========run=============="+msg);

                        tipDialog.setTitle("收到消息");
                        tipDialog.setText(msg+"");
                        tipDialog.show();
                        //ViewUtils.toastNOInUIThread(msg);
                        //if(StringUtils.isNotEmpty(msg)){
                        //    Message message=new Message();
                        //    message.what=MSG_TYPE.RECEIVE_MSG;
                        //    String name = clientSocket.getRemoteDevice().getName();
                        //    if(StringUtils.isEmpty(name)){
                        //        name=clientSocket.getRemoteDevice().getAddress();
                        //    }
                        //    message.obj=name+":"+msg;
                        //    handler.sendMessage(message);
                        //}
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                //ViewUtils.toastNOInUIThread("连接已断开");
                handler.sendEmptyMessage(MSG_TYPE.CONNECT_BREAK);
                mapClientSocket.remove(clientSocket.getRemoteDevice().getAddress());
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 发送消息
     *
     * @param msg
     *            ：发送的消息
     */
    public static void sendMessage(String address,String msg) {
        BluetoothSocket clientSocket = mapClientSocket.get(address);
        if (clientSocket == null) {
            ViewUtils.toast("未建立连接");
            return;
        }// 防止未连接就发送信息
        try {
            // 使用socket获得outputstream
            OutputStream out = clientSocket.getOutputStream();
            out.write(msg.getBytes());// 将消息字节发出
            out.flush();// 确保所有数据已经被写出，否则抛出异常
            ViewUtils.toast( "发送:" + msg);
        } catch (IOException e) {
            e.printStackTrace();
            ViewUtils.toast("发送失败");
        }
    }

    /**
     * 客户端，进行连接的线程
     *
     * @author Administrator
     *
     */
    static class ClientThread extends Thread {

        Handler handler;
        RowObject rowDevice;
        public ClientThread(Handler handler, RowObject rowDevice) {
            this.handler=handler;
            this.rowDevice=rowDevice;
        }

        @Override
        public void run() {
            String address = rowDevice.getString("address");
            try {
                // 创建一个socket尝试连接，UUID用正确格式的String来转换而成

                BluetoothSocket clientSocket;
                if(mapClientSocket.containsKey(address)){
                    clientSocket=mapClientSocket.get(address);
                }else{
                    BluetoothDevice remoteDevice = bluetoothAdapter.getRemoteDevice(address);
                    clientSocket = remoteDevice.createRfcommSocketToServiceRecord(SPP_UUID);
                    mapClientSocket.put(address,clientSocket);
                }
                L.i("=========run=============="+clientSocket.isConnected());
                // 该方法阻塞，一直尝试连接
                if(!clientSocket.isConnected()){
                    handler.sendEmptyMessage(MSG_TYPE.CONNECTING);
                    clientSocket.connect();
                }
                handler.sendEmptyMessage(MSG_TYPE.CONNECTED);
                //Utils.sonUiStateMsg("连接成功");
                // 进行接收线程
                new ReadMsg(handler,clientSocket).start();
            } catch (IOException e) {
                //Utils.sonUiStateMsg("连接失败");
                mapClientSocket.remove(address);
                handler.sendEmptyMessage(MSG_TYPE.CONNECT_FAIL);
                e.printStackTrace();
            }
        }
    }


}
