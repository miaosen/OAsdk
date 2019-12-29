package cn.oasdk.dlna.main;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import cn.oasdk.dlna.R;
import cn.oasdk.dlna.dms.MediaServer;
import cn.oasdk.dlna.util.Utils;
import cn.oaui.ImageFactory;
import cn.oaui.L;
import cn.oaui.annotation.InjectReader;
import cn.oaui.annotation.ViewInject;
import cn.oaui.data.RowObject;
import cn.oaui.utils.FileUtils;
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


    OnFileClickListener onFileClickListener;

    static interface FILE_TYPE {
        String VIDEO = "video";
        String RADIO = "radio";
        String IMAGE = "image";
        String NET = "net";
    }


    String fileType = "";


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

                LinearLayout ln_duration = (LinearLayout) holder.views.get("ln_duration");
                Long size;
                TextView file_size = (TextView) holder.views.get("file_size");
                if (row.getBoolean("isDir")) {//文件夹
                    img_file_type.setImageDrawable(getResources().getDrawable(R.mipmap.icon_folder));
                    ln_duration.setVisibility(GONE);
                    size = row.getLong("file_size");

                } else {//文件
                    img_file_type.setImageDrawable(getResources().getDrawable(R.mipmap.icon_file));
                    ImageFactory.loadImageCorner(img_file_type, filePath);
                    TextView duration = (TextView) holder.views.get("duration");
                    Long d = row.getLong("duration");

                    size = row.getLong("_size");
                    if (row.getString("type") != null &&
                            (row.getString("type").equals("video") ||
                                    row.getString("type").equals("radio"))) {
                        if (d != null) {
                            String s = Utils.formatDuration(d);
                            duration.setText(s);
                        } else {
                            duration.setText("");
                        }
                        ln_duration.setVisibility(VISIBLE);
                    } else {
                        ln_duration.setVisibility(GONE);

                    }
                }
                if (size != null) {
                    String s = FileUtils.formetFileSize(size);
                    file_size.setText(s);
                } else {
                    file_size.setText("");
                }
                onItemModify(convertView, row, position, holder);
            }
        });
        dl_file.setOnItemClickListener(new BaseFillAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View convertView, final RowObject row, int position) {
                L.i("============onItemClick===========" + row);
                if (row.getBoolean("isDir")) {
                    String name = row.getString("name");
                    listFilePath.add(name);
                    curFilePath = curFilePath + name + "/";
                    showInPath(curFilePath);
                } else {
                    if(onFileClickListener!=null){
                        onFileClickListener.onFileClick(row);
                    }
                }
            }
        });
    }


    public void onItemModify(View convertView, RowObject row, int position, BaseFillAdapter.ViewHolder holder) {
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
        fileType = type;
        if (type.equals(FILE_TYPE.VIDEO)) {
            setRowsAndSort(MediaServer.rowsVideo);
        } else if (type.equals(FILE_TYPE.RADIO)) {
            setRowsAndSort(MediaServer.rowsRadio);
        } else if (type.equals(FILE_TYPE.IMAGE)) {
            setRowsAndSort(MediaServer.rowsImage);
        } else {
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
        this.curFilePath = curFilePath;
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
                    if(size==null){
                        size=0l;
                    }
                    if (tempRowDir.getRow(nextFileName) != null && tempRowDir.getRow(nextFileName).getLong("file_size") != null) {
                        Long aLong = tempRowDir.getRow(nextFileName).getLong("file_size");
                        size = size + aLong;
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


    public interface OnFileClickListener{
        void onFileClick(RowObject row);
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

    public OnFileClickListener getOnFileClickListener() {
        return onFileClickListener;
    }

    public void setOnFileClickListener(OnFileClickListener onFileClickListener) {
        this.onFileClickListener = onFileClickListener;
    }
}
