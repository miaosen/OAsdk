package cn.oasdk.dlna.main;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.item.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import cn.oahttp.HandlerQueue;
import cn.oasdk.dlna.R;
import cn.oasdk.dlna.base.BaseActivity;
import cn.oasdk.dlna.dmc.DMCControl;
import cn.oasdk.dlna.dmc.PlayerCallback;
import cn.oasdk.dlna.dms.DLNAService;
import cn.oasdk.dlna.dms.FileServer;
import cn.oasdk.dlna.dms.MediaServer;
import cn.oaui.L;
import cn.oaui.annotation.ViewInject;
import cn.oaui.data.JSONSerializer;
import cn.oaui.data.RowObject;
import cn.oaui.utils.FileUtils;
import cn.oaui.utils.JsonUtils;
import cn.oaui.utils.ViewUtils;
import cn.oaui.view.CustomRadioGroup;
import cn.oaui.view.ViewPagerForScrollView;
import cn.oaui.view.dialog.FrameDialog;
import cn.oaui.view.dialog.extra.TipDialog;
import cn.oaui.view.dialog.extra.WindowTipDialog;
import cn.oaui.view.listview.BaseFillAdapter;
import cn.oaui.view.listview.DataListView;
import cn.oaui.view.tiplayout.TipLayout;

import static cn.oasdk.dlna.dms.DLNAService.serviceDevice;

public class MainActivity extends BaseActivity implements FileView.OnFileClickListener{


    @ViewInject
    ControlView controlView;

    //设备选择框
    FrameDialog fdl_player_device, fdl_sevice_device;

    DataListView dlv_player_device, dlv_sevice_device;
    RowObject rowNodata=new RowObject();



    @ViewInject
    TextView tv_service, tv_player, tv_settings;

    @ViewInject
    LinearLayout ln_header;

    @ViewInject
    CustomRadioGroup crg_type;
    @ViewInject
    RadioButton rb_video, rb_radio, rb_pic, rb_net;




    @ViewInject
    ViewPagerForScrollView viewPager;
    Map<Integer, View> mapFgmView = new HashMap<Integer, View>();
    PagerAdapter adapter;
    int showIndex = 0;

    FileView videoView;
    FileView radioView;
    FileView imageView;
    FavoritesView netView;


    @Override
    public void initConfig() {
        if (Build.VERSION.SDK_INT >= 23) {
            String[] PERMISSIONS_CAMERA_AND_STORAGE = {
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
            int recordAudioPermission = ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.RECORD_AUDIO);
            int cameraPermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA);
            if (recordAudioPermission != PackageManager.PERMISSION_GRANTED
                    || cameraPermission != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_CAMERA_AND_STORAGE,
                        11);
            }
        }
        DLNAService.bindService(new DefaultRegistryListener() {
            public void remoteDeviceRemoved(Registry registry, final RemoteDevice device) {
                //移除设备
                if (device.getType().getNamespace().equals("schemas-upnp-org")
                        && device.getType().getType().equals("MediaServer")) {
                    HandlerQueue.onResultCallBack(new Runnable() {
                        @Override
                        public void run() {
                            String jsonString = JSONSerializer.toJSONString(device.getDetails());
                            RowObject rowObject = JsonUtils.jsonToRow(jsonString);
                            rowObject.put("device", device);
                            dlv_sevice_device.removeItem(rowObject);
                        }
                    });
                }
                if (device.getType().getNamespace().equals("schemas-upnp-org")
                        && device.getType().getType().equals("MediaRenderer")) {
                    HandlerQueue.onResultCallBack(new Runnable() {
                        @Override
                        public void run() {
                            String jsonString = JSONSerializer.toJSONString(device.getDetails());
                            RowObject rowObject = JsonUtils.jsonToRow(jsonString);
                            rowObject.put("device", device);
                            dlv_player_device.removeItem(rowObject);
                            List<RowObject> rows = dlv_player_device.getFillApdater().getRows();
                            if(rows.size()==0){
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
                            RowObject rowObject = JsonUtils.jsonToRow(jsonString);
                            rowObject.put("device", device);
                            dlv_sevice_device.addItem(rowObject);
                        }
                    });
                }
                if (device.getType().getNamespace().equals("schemas-upnp-org")
                        && device.getType().getType().equals("MediaRenderer")) {
                    HandlerQueue.onResultCallBack(new Runnable() {
                        @Override
                        public void run() {
                            String jsonString = JSONSerializer.toJSONString(device.getDetails());
                            RowObject rowObject = JsonUtils.jsonToRow(jsonString);
                            rowObject.put("device", device);
                            dlv_player_device.addItem(rowObject);
                            List<RowObject> rows = dlv_player_device.getFillApdater().getRows();
                            if(rows.contains(rowNodata)){
                                rows.remove(rowNodata);
                            }
                        }
                    });
                }
            }

            @Override
            protected void finalize() throws Throwable {
                //super.finalize();
                L.i("============finalize===========");
            }
        }, MainActivity.this, handler);
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            videoView.setFileType(FileView.FILE_TYPE.VIDEO);
            radioView.setFileType(FileView.FILE_TYPE.RADIO);
            imageView.setFileType(FileView.FILE_TYPE.IMAGE);
            netView.setFileType(FileView.FILE_TYPE.NET);
            tv_service.setText(serviceDevice.getDetails().getFriendlyName());
        }
    };


    @Override
    public int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    public void onViewCreate() {
        initPageView();
        tv_player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fdl_player_device.showAsDown(ln_header);
            }
        });
        tv_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fdl_sevice_device.showAsDown(tv_service);
            }
        });
        rb_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(showIndex);
            }
        });
        rb_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(showIndex + 1);

            }
        });
        rb_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(showIndex + 2);
            }
        });
        rb_net.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(showIndex +3);
            }
        });


        //tv_settings.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
                //curFilePath = FileUtils.getSDCardPath();
                //dl_file.clearData();
                //listFilePath.clear();
                ////rowsCur = StringUtils.deepCopyObject(MediaServer.rowsImage);
                //dl_file.addItems(sortRows(MediaServer.rowsImage, curFilePath));
            //}
        //});


    }

    private void initPageView() {
        videoView = new FileView(context);
        radioView = new FileView(context);
        imageView = new FileView(context);
        videoView.setOnFileClickListener(this);
        radioView.setOnFileClickListener(this);
        imageView.setOnFileClickListener(this);
        netView = new FavoritesView(context);
        netView.setOnFileClickListener(this);
        mapFgmView.put(showIndex, videoView);
        mapFgmView.put(showIndex + 1, radioView);
        mapFgmView.put(showIndex + 2, imageView);
        mapFgmView.put(showIndex + 3, netView);
        adapter = new MyPagerAdapter();
        viewPager.setCanScroll(true);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(final int position) {
                L.i("============onPageSelected===========" + position);
                View view = mapFgmView.get(position);
                if (view != null) {
                    if(view instanceof FileView){
                      FileView fileView= (FileView) view;
                        String type = fileView.getFileType();
                        if (type.equals(FileView.FILE_TYPE.VIDEO)) {
                            crg_type.setCheck("视频");
                        } else if (type.equals(FileView.FILE_TYPE.RADIO)) {
                            crg_type.setCheck("音乐");
                        } else if (type.equals(FileView.FILE_TYPE.IMAGE)) {
                            crg_type.setCheck("图片");
                        }
                    }else if(view instanceof FavoritesView){
                        crg_type.setCheck("收藏");
                    } else {
                        crg_type.setCheck("视频");
                    }
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        viewPager.setAdapter(adapter);
        //缓存个数
        viewPager.setOffscreenPageLimit(1);
        viewPager.setCurrentItem(showIndex);

    }

    @Override
    public void initData() {
        FileServer.scanFile(FileUtils.getSDCardPath());
        //服务设备弹窗
        fdl_sevice_device = new FrameDialog(context, R.layout.device_list_view);
        dlv_sevice_device = (DataListView) fdl_sevice_device.findViewById(R.id.dataListView);
        dlv_sevice_device.setOnTipListener(new TipLayout.OnTipListener() {
            @Override
            public void onRefresh() {
                dlv_sevice_device.clearData();
                DLNAService.reSearchDevice();
                DLNAService.upnpService.getRegistry()
                        .addDevice(DLNAService.serviceDevice);
                dlv_sevice_device.endRefresh();
            }

            @Override
            public void onLoadMore() {
            }
        });
        dlv_sevice_device.setEnableLoadMore(false);
        dlv_sevice_device.setOnItemClickListener(new BaseFillAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View convertView, RowObject row, int position,BaseFillAdapter.ViewHolder holder) {
                LocalDevice device = (LocalDevice) row.get("device");
                serviceDevice = device;
                tv_service.setText(row.getString("friendlyName"));
                fdl_sevice_device.dismiss();
                //showDeviceFile(device);
            }
        });

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

        rowNodata.put("friendlyName","暂无设备");
        dlv_player_device.addItem(rowNodata);
        dlv_player_device.setEnableLoadMore(false);
        dlv_player_device.setOnItemClickListener(new BaseFillAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View convertView, RowObject row, int position,BaseFillAdapter.ViewHolder holder) {
                Device device = (Device) row.get("device");
                L.i("============onItemClick==========="+device);
                DLNAService.playerDevice = device;
                tv_player.setText(row.getString("friendlyName"));
                fdl_player_device.dismiss();

            }
        });
        //fdl_player_device.showAsDown(tv_player);
        //服务文件列表
        //initFileDataListView();

    }

    @Override
    public void onFileClick(final RowObject row) {
        if (DLNAService.playerDevice != null) {
            Item curItem = null;
            if ("video".equals(row.getString("type"))||FavoritesView.ITEM_TYPE.equals(row.getString("type"))) {
                curItem = MediaServer.buildVideoItem(row);
            } else if ("radio".equals(row.getString("type"))) {
                //curItem=MediaServer.buildPlaylistItem(rowsCur);
                curItem = MediaServer.buildRadioItem(row);
            } else if ("image".equals(row.getString("type"))) {
                curItem = MediaServer.buildImageItem(row);
            }
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
                            if(msg.equals(PlayerCallback.SUCCESS_TIP)
                                    &&!"net_video".equals(row.getString("type"))){
                                controlView.start(row);
                            }
                        }
                    });
                }
            },curItem,url);
        } else {
            ViewUtils.toast("请先选择投屏设备！");
            fdl_player_device.showAsDown(ln_header);
        }
    }


    class MyPagerAdapter extends PagerAdapter {
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return mapFgmView.size();
        }

        //对超出范围的资源进行销毁
        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
        }
        //对显示的资源进行初始化
        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = mapFgmView.get(position);
            if (view != null) {
                container.removeView(view);
                container.addView(view);
            }
            return view;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            int currentItem = viewPager.getCurrentItem();
            FileView view = (FileView) mapFgmView.get(currentItem);
            String curFilePath=view.getCurFilePath();
            if( !curFilePath.equals(FileUtils.getSDCardPath())){
                curFilePath = FileUtils.getSDCardPath();
                List<String> listFilePath = view.getListFilePath();
                for (int i = 0; i < listFilePath.size() - 1; i++) {
                    String name = listFilePath.get(i);
                    curFilePath = curFilePath + name + "/";
                }
                listFilePath.remove(listFilePath.size() - 1);
                view.showInPath(curFilePath);
                return false;
            }
        } else if (keyCode == KeyEvent.KEYCODE_BACK){
            TipDialog tipDialog = new TipDialog(context);
            tipDialog.setTitle("提示");
            tipDialog.setText("是否退出应用？");
            tipDialog.setOnSureListener(new WindowTipDialog.OnSureListener() {
                @Override
                public void onSure(DialogInterface dialog) {
                    finish();
                }
            });
            tipDialog.show();
            return false;
        }
        return super.onKeyDown(keyCode,event);
    }
}
