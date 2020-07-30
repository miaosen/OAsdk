package cn.oasdk.dlna.tbs;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.tencent.smtt.sdk.TbsReaderView;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.item.Item;

import java.io.File;
import java.util.List;

import cn.oahttp.HandlerQueue;
import cn.oasdk.dlna.R;
import cn.oasdk.dlna.base.BaseActivity;
import cn.oasdk.dlna.dmc.DMCControl;
import cn.oasdk.dlna.dmc.PlayerCallback;
import cn.oasdk.dlna.dms.DLNAService;
import cn.oasdk.dlna.dms.FileServer;
import cn.oasdk.dlna.image.view.ImageEditPannel;
import cn.oaui.L;
import cn.oaui.annotation.ViewInject;
import cn.oaui.data.JSONSerializer;
import cn.oaui.data.Row;
import cn.oaui.utils.AppUtils;
import cn.oaui.utils.BitmapUtils;
import cn.oaui.utils.FileUtils;
import cn.oaui.utils.JsonUtils;
import cn.oaui.utils.ViewUtils;
import cn.oaui.view.dialog.FrameDialog;
import cn.oaui.view.listview.BaseFillAdapter;
import cn.oaui.view.listview.DataListView;
import cn.oaui.view.tiplayout.TipLayout;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2019-11-15  16:59
 * @Descrition
 */
public class TBSWebviewAct extends BaseActivity {


    @ViewInject
    LinearLayout ln;

    private TbsReaderView mTbsReaderView;

    //设备选择框
    FrameDialog fdl_player_device;
    DataListView dlv_player_device;
    @ViewInject
    ImageEditPannel image_edit_pannel;
    Row rowNodata = new Row();

    @Override
    public void initConfig() {
        if (DLNAService.playerDevice == null) {
            DLNAService.bindService(new DefaultRegistryListener() {
                public void remoteDeviceRemoved(Registry registry, final RemoteDevice device) {
                    //移除设备
                    if (device.getType().getNamespace().equals("schemas-upnp-org")
                            && device.getType().getType().equals("MediaRenderer")) {
                        HandlerQueue.onResultCallBack(new Runnable() {
                            @Override
                            public void run() {
                                String jsonString = JSONSerializer.toJSONString(device.getDetails());
                                Row row = JsonUtils.jsonToRow(jsonString);
                                row.put("device", device);
                                dlv_player_device.removeItem(row);
                                List<Row> rows = dlv_player_device.getFillApdater().getRows();
                                if (rows.size() == 0) {
                                    dlv_player_device.addItem(rowNodata);
                                }
                            }
                        });
                    }
                }

                @Override
                public void deviceAdded(Registry registry, final Device device) {
                    //查找设备
                    DeviceDetails details = device.getDetails();
                    if (device.getType().getNamespace().equals("schemas-upnp-org")
                            && device.getType().getType().equals("MediaServer")) {
                        HandlerQueue.onResultCallBack(new Runnable() {
                            @Override
                            public void run() {
                                String jsonString = JSONSerializer.toJSONString(device.getDetails());
                                Row row = JsonUtils.jsonToRow(jsonString);
                                row.put("device", device);
                                //dlv_sevice_device.addItem(rowObject);
                            }
                        });
                    }
                    if (device.getType().getNamespace().equals("schemas-upnp-org")
                            && device.getType().getType().equals("MediaRenderer")) {
                        HandlerQueue.onResultCallBack(new Runnable() {
                            @Override
                            public void run() {
                                String jsonString = JSONSerializer.toJSONString(device.getDetails());
                                Row row = JsonUtils.jsonToRow(jsonString);
                                row.put("device", device);
                                dlv_player_device.addItem(row);
                                List<Row> rows = dlv_player_device.getFillApdater().getRows();
                                if (rows.contains(rowNodata)) {
                                    rows.remove(rowNodata);
                                }
                            }
                        });
                    }
                }
            }, TBSWebviewAct.this, handler);
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    public int getContentView() {
        return R.layout.tbs_webview_act;
    }

    @Override
    public void onViewCreate() {

        mTbsReaderView = new TbsReaderView(this, new TbsReaderView.ReaderCallback() {
            @Override
            public void onCallBackAction(Integer integer, Object o, Object o1) {
                L.i("============onCallBackAction==========="+integer);
                L.i("============onCallBackAction===========" + o);
                if(o1 instanceof Bundle){
                    Bundle bundle= (Bundle) o1;
                    bundle.putCharSequence("aaa","aaaa");
                    if( bundle.getBoolean("finish")&&integer==bundle.getInt("totalpage")){

                    }
                    L.i("============onCallBackAction===========" + o1);
                }

            }


        });
        ln.addView(mTbsReaderView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        String filePath = getIntent().getStringExtra("filePath");
        L.i("============onViewCreate===========" + filePath);
        displayFile(new File(filePath));
        //投屏设备弹窗
        fdl_player_device = new FrameDialog(context, R.layout.device_list_view);

        dlv_player_device = (DataListView) fdl_player_device.findViewById(R.id.dataListView);
        dlv_player_device.setOnTipListener(new TipLayout.OnTipListener() {
            @Override
            public void onRefresh() {
                dlv_player_device.clearData();
                dlv_player_device.endRefresh();
                DLNAService.reSearchDevice();
            }

            @Override
            public void onLoadMore() {
            }
        });

        rowNodata.put("friendlyName", "暂无设备");
        dlv_player_device.addItem(rowNodata);
        dlv_player_device.setEnableLoadMore(false);
        dlv_player_device.setOnItemClickListener(new BaseFillAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View convertView, Row row, int position, BaseFillAdapter.ViewHolder viewHolder) {
                Device device = (Device) row.get("device");
                L.i("============onItemClick===========" + device);
                DLNAService.playerDevice = device;
                fdl_player_device.dismiss();
                startPaly();

            }
        });
    }

    @Override
    public void initData() {
        if (DLNAService.playerDevice != null) {
            startPaly();
        } else {
            ViewUtils.toast("请先选择投屏设备！");
            fdl_player_device.showAsDown(image_edit_pannel);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        L.i("============onWindowFocusChanged==========="+hasFocus);
        if(hasFocus){

        }
    }

    private void startPaly() {
        Bitmap bitmap = BitmapUtils.screenShotWithoutStatusBar(this);
        String picPath = FileUtils.getAppDirPath() + "/temp.png";
        L.i("============startPaly==========="+picPath);
        BitmapUtils.saveBitmapToPathAsPng(bitmap, picPath);
        Item curItem = FileServer.fileToItem(picPath);
        List<Res> resources = curItem.getResources();
        String url = "";
        for (int i = 0; i < resources.size(); i++) {
            url = resources.get(i).getValue();
        }
        DMCControl.stopAndPaly(new PlayerCallback() {
            @Override
            public void onResult(final String msg) {
                HandlerQueue.onResultCallBack(new Runnable() {
                    @Override
                    public void run() {
                        ViewUtils.toast(msg);
                    }
                });
            }
        }, curItem, url);
    }

    /**
     * 加载显示文件内容
     */
    private void displayFile(File file) {
        Bundle bundle = new Bundle();
        bundle.putString("filePath", file.getAbsolutePath());
        bundle.putString("tempPath", Environment.getExternalStorageDirectory()
                .getPath());
        L.i("============displayFile===========" + parseFormat(file.getAbsolutePath()));
        boolean result = mTbsReaderView.preOpen(parseFormat(file.getAbsolutePath()), false);
        if (result) {
            mTbsReaderView.openFile(bundle);
        } else {
            AppUtils.openFileBySystemApp(context, file.getAbsolutePath());
        }
    }

    private String parseFormat(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTbsReaderView.onStop();
        //if (mDownloadObserver != null) {
        //    getContentResolver().unregisterContentObserver(mDownloadObserver);
        //}
    }
}
