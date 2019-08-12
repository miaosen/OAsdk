package cn.oasdk.dlna.main;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.item.Item;

import java.util.LinkedList;
import java.util.List;

import cn.oahttp.HandlerQueue;
import cn.oasdk.dlna.R;
import cn.oasdk.dlna.dmc.DMCControl;
import cn.oasdk.dlna.dmc.GenerateXml;
import cn.oasdk.dlna.dmc.PlayerCallback;
import cn.oasdk.dlna.dmc.SetAVTransportURIActionCallback;
import cn.oasdk.dlna.dmc.StopCallback;
import cn.oasdk.dlna.dms.DLNAService;
import cn.oasdk.dlna.dms.MediaServer;
import cn.oasdk.dlna.util.Utils;
import cn.oaui.ImageFactory;
import cn.oaui.L;
import cn.oaui.annotation.InjectReader;
import cn.oaui.annotation.ViewInject;
import cn.oaui.data.RowObject;
import cn.oaui.utils.FileUtils;
import cn.oaui.utils.ViewUtils;
import cn.oaui.view.CustomLayout;
import cn.oaui.view.listview.BaseFillAdapter;
import cn.oaui.view.listview.DataListView;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2019-08-05  14:58
 * @Descrition
 */

public class FileView extends CustomLayout {

    @ViewInject
    DataListView dl_file;
    LinkedList<RowObject> rows = new LinkedList<>();
    String curFilePath = FileUtils.getSDCardPath();
    List<String> listFilePath = new LinkedList<>();

    static interface FILE_TYPE{
        String VIDEO="video";
        String RADIO="radio";
        String IMAGE="image";
        String NET="net";
    }


    String fileType="";


    public FileView(Context context) {
        super(context);
    }

    public FileView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void initData() {
        dl_file.setOnItemModifylistenert(new DataListView.OnItemModifylistenert() {
            @Override
            public void setItemView(View convertView, RowObject row, int position, BaseFillAdapter.ViewHolder holder) {
                //L.i("============setItemView===========" + row);
                String filePath = row.getString("filePath");
                ImageView img_file_type = (ImageView) holder.views.get("img_file_type");

                LinearLayout ln_duration= (LinearLayout) holder.views.get("ln_duration");
                if (row.getBoolean("isDir")) {//文件夹
                    img_file_type.setImageDrawable(getResources().getDrawable(R.mipmap.icon_folder));
                    ln_duration.setVisibility(GONE);
                } else {//文件
                    img_file_type.setImageDrawable(getResources().getDrawable(R.mipmap.icon_file));
                    ImageFactory.loadImageCorner(img_file_type, filePath);
                    TextView duration= (TextView) holder.views.get("duration");
                    Long d = row.getLong("duration");
                    if(d!=null){
                        String s = Utils.formatDuration(d);
                        duration.setText(s);
                    }else{
                        duration.setText("");
                    }
                    ln_duration.setVisibility(VISIBLE);
                }
                TextView file_size= (TextView) holder.views.get("file_size");
                Long size = row.getLong("_size");
                if(size!=null){
                    String s = FileUtils.formetFileSize(size);
                    file_size.setText(s);
                }else{
                    file_size.setText("");
                }

            }
        });
        dl_file.setOnItemClickListener(new BaseFillAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View convertView, final RowObject row, int position) {
                L.i("============onItemClick==========="+row);
                if (row.getBoolean("isDir")) {
                    String name = row.getString("name");
                    listFilePath.add(name);
                    curFilePath = curFilePath + name + "/";
                    showInPath(curFilePath);
                } else {
                    if (DLNAService.playerDevice != null) {
                        try {
                            final Service localService = DLNAService.playerDevice
                                    .findService(new UDAServiceType("AVTransport"));
                            if (localService != null && DLNAService.playerDevice != null) {
                                DLNAService.upnpService.getControlPoint().execute(
                                        new StopCallback(localService) {
                                            @Override
                                            public void onResult(String msg) {
                                                try {
                                                    Item curItem=null;
                                                    if ("video".equals(row.getString("type"))) {
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
                                                    String generate = new GenerateXml().generate(curItem, null);
                                                    DMCControl.setAvURL(new SetAVTransportURIActionCallback(localService,
                                                            url, generate) {
                                                        @Override
                                                        public void onResult(String msg) {
                                                            DMCControl.play(new PlayerCallback(localService) {
                                                                @Override
                                                                public void onResult(final String msg) {
                                                                    HandlerQueue.onResultCallBack(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            ViewUtils.toast(msg);
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }
                                                    }, curItem);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                            } else {
                                ViewUtils.toast("获取设备服务失败！");
                            }
                        } catch (Exception localException) {
                            localException.printStackTrace();
                        }
                    } else {
                        ViewUtils.toast("请先选择投屏设备！");
                    }
                }
            }
        });
    }

    @Override
    protected void onCreateView() {
        InjectReader.injectAllFields(this);
    }

    @Override
    public int setXmlLayout() {
        return R.layout.view_data_list;
    }

    public void setFileType(String type) {
        fileType=type;
        if(type.equals(FILE_TYPE.VIDEO)){
            setRowsAndSort(MediaServer.rowsVideo);
        }else if(type.equals(FILE_TYPE.RADIO)){
            setRowsAndSort(MediaServer.rowsRadio);
        }else if(type.equals(FILE_TYPE.IMAGE)){
            setRowsAndSort(MediaServer.rowsImage);
        }else{
            setRows(MediaServer.rowsNet);
        }
    }


    public void setRowsAndSort(LinkedList<RowObject> rows) {
        //LinkedList<RowObject> rowObjects = StringUtils.deepCopyObject(rows);
        this.rows.addAll(rows);
        dl_file.addItems(sortAsRows(rows, curFilePath));
    }

    public void setRows(LinkedList<RowObject> rows) {
        dl_file.addItems(rows);
    }


    public void showInPath(String curFilePath) {
        this.curFilePath=curFilePath;
        dl_file.clearData();
        dl_file.addItems(sortAsRows(rows, curFilePath));

    }


    /**
     * 根据当前文件路径列出所有文件夹和文件
     *
     * @param rows
     * @param curFilePath
     */
    private LinkedList<RowObject> sortAsRows(List<RowObject> rows, String curFilePath) {
        RowObject tempRowDir = new RowObject();
        RowObject tempRowFile = new RowObject();
        for (int i = 0; i < rows.size(); i++) {
            RowObject row = rows.get(i);
            String filePath = row.getString("filePath");
            if (filePath.startsWith(curFilePath)) {
                //RowObject row = StringUtils.deepCopyObject(row);
                String nextFileName = filePath.substring(curFilePath.length(), filePath.length());
                if (nextFileName.indexOf("/") > 0) {
                    nextFileName = nextFileName.substring(0, nextFileName.indexOf("/"));
                    row.put("name", nextFileName);
                    Long size = row.getLong("_size");
                    if(tempRowDir.getRow(nextFileName)!=null&&tempRowDir.getRow(nextFileName).getLong("file_size")!=null){
                        Long aLong = tempRowDir.getRow(nextFileName).getLong("file_size");
                        size=size+aLong;
                    }
                    L.i("============sortAsRows==========="+row.getString("type"));
                    if(row.getString("type").equals(MediaServer.FILE_TYPE.VIDEO)){
                        L.i("============sortAsRows==========="+nextFileName+"   "+FileUtils.formetFileSize(size));
                    }
                    row.put("file_size", size);
                    row.put("isDir", true);
                    tempRowDir.put(nextFileName, row);
                } else {
                    row.put("isDir", false);
                    row.put("name", row.getString("_display_name"));
                    tempRowFile.put(row.getString("_id"), row);
                }
            }
        }
        LinkedList<RowObject> tempRowsFile = new LinkedList<>();
        for (Object row : tempRowDir.values()) {
            tempRowsFile.add((RowObject) row);
        }
        for (Object row : tempRowFile.values()) {
            tempRowsFile.add((RowObject) row);
        }
        return tempRowsFile;
    }

    public String getCurFilePath() {
        return curFilePath;
    }

    public void setCurFilePath(String curFilePath) {
        this.curFilePath = curFilePath;
    }

    public List<String> getListFilePath() {
        return listFilePath;
    }

    public void setListFilePath(List<String> listFilePath) {
        this.listFilePath = listFilePath;
    }

    public String getFileType() {
        return fileType;
    }
}
