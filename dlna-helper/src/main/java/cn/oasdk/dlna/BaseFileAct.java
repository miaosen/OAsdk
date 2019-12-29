package cn.oasdk.dlna;

import android.annotation.SuppressLint;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import cn.oasdk.dlna.base.BaseActivity;
import cn.oasdk.dlna.dms.FileServer;
import cn.oasdk.dlna.util.Utils;
import cn.oaui.ImageFactory;
import cn.oaui.L;
import cn.oaui.annotation.ViewInject;
import cn.oaui.data.RowObject;
import cn.oaui.utils.FileUtils;
import cn.oaui.view.FlowLayout;
import cn.oaui.view.listview.BaseFillAdapter;
import cn.oaui.view.listview.DataListView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2019-10-25  14:17
 * @Descrition
 */

public abstract class BaseFileAct extends BaseActivity {

    @ViewInject
    public DataListView dataListView;
    public String curFilePath = FileUtils.getSDCardPath();
    public List<String> listFilePath = new LinkedList<>();


    public LinkedList<RowObject> rows;

    @ViewInject
    ImageButton leftBtn;

    public boolean isEditMode = false;

    @ViewInject
    FlowLayout flowLayout;
    MAdapter mAdapter;
    LinkedList<RowObject> rowsCurPath = new LinkedList<>();

    @ViewInject
    LinearLayout ln_edit_tail,ln_cancle;



    @Override
    public void onViewCreate() {
        dataListView.setOnItemModifylistenert(new DataListView.OnItemModifylistenert() {
            @Override
            public void setItemView(View convertView, RowObject row, int position, BaseFillAdapter.ViewHolder holder) {
                L.i("============setItemView===========" + row);
                String filePath = row.getString("filePath");
                ImageView img_file_type = (ImageView) holder.views.get("img_file_type");

                LinearLayout ln_duration = (LinearLayout) holder.views.get("ln_duration");
                Long size;
                TextView file_size = (TextView) holder.views.get("file_size");
                Boolean isDir = row.getBoolean("isDir");
                if (isDir) {//文件夹
                    img_file_type.setImageDrawable(getResources().getDrawable(R.mipmap.icon_folder));
                    ln_duration.setVisibility(GONE);
                    size = row.getLong("file_size");

                } else {//文件

                    setFileIcon(filePath, img_file_type);
                    //
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
                    TextView sum = (TextView) holder.views.get("sum");
                    if (row.getInteger("sum") == 0) {
                        sum.setText("");
                    }
                }
                if (size != null) {
                    String s = FileUtils.formetFileSize(size);
                    file_size.setText(s);
                } else {
                    file_size.setText("");
                }
                RadioButton rb = (RadioButton) holder.views.get("rb");
                if (isEditMode) {
                    rb.setVisibility(View.VISIBLE);
                } else {
                    rb.setVisibility(View.GONE);
                }
                if (row.getBoolean("checked")) {
                    rb.setChecked(true);
                } else {
                    rb.setChecked(false);
                }
                convertView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        openEditMode();
                        L.i("============onLongClick===========");
                        return true;
                    }
                });
            }


        });
        dataListView.setOnItemClickListener(new BaseFillAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View convertView, final RowObject row, int position) {
                L.i("============onItemClick===========" + row);
                if (isEditMode) {
                    row.put("checked",!row.getBoolean("checked"));
                    dataListView.notifyDataSetChanged();
                } else {
                    if (row.getBoolean("isDir")) {
                        String name = row.getString("name");
                        listFilePath.add(name);
                        curFilePath = curFilePath + name + "/";
                        showInPath(curFilePath);
                        showPath();
                    } else {
                        onListItemClick(convertView,row,position);
                    }
                }
            }
        });
        mAdapter = new MAdapter(rowsCurPath, R.layout.image_path_item);
        mAdapter.setOnItemClickListener(new BaseFillAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View convertView, RowObject row, int position) {
                Iterator<String> iterator = listFilePath.iterator();
                int sum = 0;
                curFilePath = FileUtils.getSDCardPath();
                while (iterator.hasNext()) {
                    sum = sum + 1;
                    String name = iterator.next();
                    if (sum > position) {
                        iterator.remove();
                    } else {
                        curFilePath = curFilePath + name + "/";
                    }
                }
                showPath();
                showInPath(curFilePath);

            }
        });
        flowLayout.setAdapter(mAdapter);
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });
        initTailEditView();
    }

    private void initTailEditView() {
        ln_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeEditMode();
            }
        });

    }

    public void openEditMode() {
        isEditMode = true;
        dataListView.notifyDataSetChanged();
        ln_edit_tail.setVisibility(VISIBLE);
    }

    public void closeEditMode() {
        isEditMode=false;
        dataListView.notifyDataSetChanged();
        ln_edit_tail.setVisibility(GONE);
        //
        LinkedList<RowObject> rows = dataListView.getRows();
        for (int i = 0; i <rows.size(); i++) {
            RowObject rowObject = rows.get(i);
            rowObject.put("checked",false);
        }
    }

    protected abstract void onListItemClick(View convertView, RowObject row, int position);


    public void setFileIcon(String filePath, ImageView img_file_type) {
        ImageFactory.loadImageCorner(img_file_type, filePath);
    }

    public void showPath() {
        rowsCurPath.clear();
        RowObject r = new RowObject();
        r.put("NAME", "存储");
        rowsCurPath.add(r);
        L.i("============showPath===========" + listFilePath);
        for (int i = 0; i < listFilePath.size(); i++) {
            RowObject row = new RowObject();
            String s = listFilePath.get(i);
            row.put("NAME", "/" + s);
            rowsCurPath.add(row);
        }

        mAdapter.notifyDataSetChanged();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return back();
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean back() {
        boolean b = true;
        if (isEditMode) {
           closeEditMode();
        } else {
            if (!curFilePath.equals(FileUtils.getSDCardPath())) {
                curFilePath = FileUtils.getSDCardPath();
                for (int i = 0; i < listFilePath.size() - 1; i++) {
                    String name = listFilePath.get(i);
                    curFilePath = curFilePath + name + "/";
                }
                listFilePath.remove(listFilePath.size() - 1);
                showInPath(curFilePath);
                b = false;
            } else if (curFilePath.equals(FileUtils.getSDCardPath())) {
                //TipDialog tipDialog = new TipDialog(context);
                //tipDialog.setTitle("提示");
                //tipDialog.setText("是否退出浏览？");
                //tipDialog.setOnSureListener(new WindowTipDialog.OnSureListener() {
                //    @Override
                //    public void onSure(DialogInterface dialog) {
                //        finish();
                //    }
                //});
                //tipDialog.show();
                finish();
                b = false;
            }
            showPath();
        }
        return b;
    }


    public void showInPath(String curFilePath) {
        this.curFilePath = curFilePath;
        dataListView.clearData();
        dataListView.addItems(FileServer.sortRows(rows, curFilePath));

    }

    class MAdapter extends BaseFillAdapter {

        public MAdapter(List<RowObject> rows, int layout) {
            super(BaseFileAct.this, rows, layout);
        }

        @SuppressLint("NewApi")
        @Override
        public void setItem(View convertView, RowObject row, int position, ViewHolder holder) {
            L.i("============setItem===========" + holder.views);
            //TextView NAME = (TextView) holder.views.get("NAME");
            //NAME.setText("!!!!");
            //View img = holder.views.get("img");
            //img.setBackground(getResources().getDrawable((Integer) row.get("ICON")));

        }
    }

    @Override
    public void initData() {
        showInPath(FileUtils.getSDCardPath());
        showPath();

    }
}
