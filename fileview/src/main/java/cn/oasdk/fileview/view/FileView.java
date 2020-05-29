package cn.oasdk.fileview.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.LinkedList;

import cn.oasdk.fileview.data.FileData;
import cn.oasdk.fileview.data.FileEntity;
import cn.oasdk.fileview.FileListAdapter;
import cn.oasdk.fileview.R;
import cn.oasdk.fileview.data.FileTree;
import cn.oaui.L;
import cn.oaui.ResourceHold;
import cn.oaui.annotation.InjectReader;
import cn.oaui.annotation.ViewInject;
import cn.oaui.data.RowObject;
import cn.oaui.utils.AppUtils;
import cn.oaui.utils.FileUtils;
import cn.oaui.utils.RowUtils;
import cn.oaui.utils.ViewUtils;
import cn.oaui.view.CustomLayout;
import cn.oaui.view.dialog.FrameDialog;
import cn.oaui.view.dialog.extra.LoadingDialog;
import cn.oaui.view.listview.BaseFillAdapter;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2020-01-17  15:09
 * @Descrition
 */
public class FileView extends CustomLayout {

    public static final String DIR_KEY = "dir_key";
    public static final String KEY_PATH = "path";
    @ViewInject
    public ListView list_view;
    public FileListAdapter listAdapter;

    //编辑状态
    public Boolean isEditMode = false;
    //移动状态
    public Boolean isPasteMode = false;
    //选择状态
    public Boolean isCheckMode = false;
    @ViewInject
    public LinearLayout ln_search,ln_tip, ln_header, ln_edit_right,ln_cancle,ln_sure;
    @ViewInject
    LinearLayout ln_share, ln_move, ln_edit_tail, ln_analyse, ln_more, ln_copy, ln_delete;
//    @ViewInject
//    ImageView img_check_all;
//    @ViewInject
//    LinearLayout ln_tip;
    @ViewInject
Button btn_add;
    FrameDialog sortDialog;
    @ViewInject
    View view_dialog;
    @ViewInject
    View leftBtn;
//    @ViewInject
//    CheckBox cb_check;

    @ViewInject
    public TextView title, tv_move, tv_copy, tv_edit_title, tv_check_num;

    public String strTitleName;
    public EditMenuListView editMenuListView;
    public FrameDialog editMenuDialog;
    @ViewInject
    View view_dlg_line;

    public LinkedList<RowObject> rowsChecked;

    public String curCollectDir = FileUtils.getSDCardPath();

    //是否通过地址加载列表
    public boolean isLoadByPath = true;

    public static final String KEY_CUR_COLLECT_DIR = "curcollectdir_key";
    public LinkedList<RowObject> rows;

    private int itemLayoutId = R.layout.file_list_item;

    public LoadingDialog dialog;

    public OnModeChangeListen onModeChangeListen;

    public OnFileCheckedListen onFileCheckedListener;

    OnSucessHandlerListen onSucessHandlerListen;

    @ViewInject
    FilePathView filePathView;




    public FileView(Context context) {
        super(context);
    }

    public FileView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void initData() {
        loadData(curCollectDir);
    }

    public boolean back() {
        File file = new File(curCollectDir);
        String parent = file.getParent();
        if (curCollectDir.equals(FileUtils.getSDCardPath())
                || (curCollectDir + "/").equals(FileUtils.getSDCardPath())) {
            if (isPasteMode) {
                cancleEdit();
            } else {
                ((Activity) context).finish();
            }
            return false;
        } else {
            curCollectDir = parent;
            loadData(curCollectDir);
            return logicTitle(parent);
        }
    }

    public boolean logicTitle(String path) {
        if (path.equals(strTitleName) || (curCollectDir + "/").equals(FileUtils.getSDCardPath())) {
            title.setText(strTitleName);
            return false;
        } else {
            File file = new File(path);
            title.setText(file.getName() + "");
            return true;
        }
    }


    public void loadData(String path) {
        LinkedList<RowObject> rowsFromPath = new LinkedList<>();
        LinkedList<FileEntity> listCl = FileData.readFileFromDir(path);
        if (listCl != null) {
            rowsFromPath = RowUtils.listEntityToRows(listCl);
        }
        for (int i = 0; i < rowsFromPath.size(); i++) {
            RowObject rowObject = rowsFromPath.get(i);
            String path1 = rowObject.getString("path");
            for (int j = 0; j < rowsChecked.size(); j++) {
                RowObject rowObject1 = rowsChecked.get(j);
                String path2 = rowObject1.getString("path");
                if (path1.equals(path2)) {
                    rowObject.put(FileListAdapter.CHECK_KEY, true);
//                    rowObject.put(FileListAdapter.LOCK_KEY, true);
                }
            }
        }
        loadData(rowsFromPath);
    }

    public void loadData(LinkedList<RowObject> rowsData) {
        rows.clear();
        for (int i = 0; i < rowsData.size(); i++) {
            RowObject rowObject = rowsData.get(i);
            String path1 = rowObject.getString("path");
            for (int j = 0; j < rowsChecked.size(); j++) {
                RowObject rowObject1 = rowsChecked.get(j);
                String path2 = rowObject1.getString("path");
                if (path1.equals(path2)) {
                    rowObject.put(FileListAdapter.CHECK_KEY, true);
//                    rowObject.put(FileListAdapter.LOCK_KEY, true);
                }
            }
        }
        rows.addAll(rowsData);
        listAdapter.notifyDataSetChanged();
        logicNull();
    }

    public void logicNull() {
        filePathView.setPath(curCollectDir);
        if (rows.size() > 0) {
            list_view.setVisibility(VISIBLE);
            ln_tip.setVisibility(GONE);
        } else {
            list_view.setVisibility(GONE);
            ln_tip.setVisibility(VISIBLE);
        }
    }


//    @Override
//    public void onWindowFocusChanged(boolean hasWindowFocus) {
//        super.onWindowFocusChanged(hasWindowFocus);
//        if (hasWindowFocus) {
//            if(curCollectDir!=null){
//                loadData(curCollectDir);
//            }
//        }
//    }


    @Override
    protected void onCreateView() {
        InjectReader.injectAllFields(this);
        rowsChecked = new LinkedList<>();
        rows = new LinkedList<>();
        //testData();
        listAdapter = new FileListAdapter(getContext(), rows, getItemLayoutId());
        listAdapter.setOnItemClickListener(new BaseFillAdapter.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onItemClick(View convertView, RowObject row, int position, BaseFillAdapter.ViewHolder holder) {
                FileView.this.onItemClick(convertView, row, position, holder);
            }
        });
        list_view.setAdapter(listAdapter);
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
        listAdapter.setOnItemModifyListener(new FileListAdapter.OnItemModifyListener() {
            @Override
            public void setItem(View convertView, RowObject row, int position, BaseFillAdapter.ViewHolder holder) {
                onItemModifyListener(convertView,row,position,holder);
            }
        });
        listAdapter.setOnLongClickListener(new FileListAdapter.OnLongClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View convertView, RowObject row, int position, BaseFillAdapter.ViewHolder holder) {
                checkOne(row,holder);
            }
        });
//        ln_share.setOnClickListener(new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
//            @Override
//            public void onClick(View view) {
//                ArrayList<String> list=new ArrayList<>();
//                for (int i = 0; i < rowsChecked.size(); i++) {
//                    RowObject rowObject = rowsChecked.get(i);
//                    String path = rowObject.getString("path");
//                    list.add(path);
//                }
//                if(list.size()==1){
//                    AppUtils.shareFileBySystemApp(getContext(),list.get(0));
//                }else{
//                    AppUtils.shareFilesBySystemApp(getContext(),list);
//                }
//            }
//        });
//        ln_move.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                logicMoveFile();
//            }
//        });
//        ln_cancle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                cancleEdit();
//            }
//        });
//        ln_sure.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(onFileCheckedListener!=null){
//                    onFileCheckedListener.onChecked(rowsChecked);
//                }
//            }
//        });
//        ln_copy.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                copyOrPaste();
//            }
//        });
        ln_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rowsChecked.size()==0){
                    ViewUtils.toast(ResourceHold.getString(R.string.tip_not_check_data));
                }else {
                    showDeleteDialog();
                }
            }
        });
        editMenuListView = new EditMenuListView(context);
        editMenuDialog = new FrameDialog(context, editMenuListView);
//        ln_more.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                editMenuDialog.setShadow(false);
//                editMenuDialog.showAsUp(view_dlg_line);
//                editMenuListView.getListAdapter().setOnItemClickListener(new BaseFillAdapter.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(View convertView, RowObject row, int position, BaseFillAdapter.ViewHolder holder) {
//                        onEditMenuClick(convertView,row,position,holder);
//                        editMenuDialog.dismiss();
//                    }
//                });
//            }
//        });

        SortDialogListView sortDialogListView=new SortDialogListView(getContext());
        sortDialog=new FrameDialog(getContext(),sortDialogListView);
//        ln_sort.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                sortDialog.showAsDown(view_dialog);
//            }
//        });
        sortDialogListView.setOnItemClickListener(new BaseFillAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View convertView, RowObject row, int position, BaseFillAdapter.ViewHolder holder) {
                sortDialog.dismiss();
                sort(row);
            }
        });
//        cb_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                checkAll(b);
//                listAdapter.notifyDataSetChanged();
//            }
//        });
        ln_analyse.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FileTree.scanFile(curCollectDir);
                    }
                }).start();

            }
        });
    }

    public void onItemModifyListener(View convertView, final RowObject row, int position, final BaseFillAdapter.ViewHolder holder) {
        ImageView img_tip_check= (ImageView) holder.views.get("img_tip_check");
        img_tip_check.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                L.i("======onClick===== "+row);
                checkOne(row,holder);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void checkOne(RowObject row, BaseFillAdapter.ViewHolder holder) {
        if (!isCheckMode) {
            switchEditMode(true);
            ImageView img_check= (ImageView) holder.views.get("img_check");
            row.put(FileListAdapter.CHECK_KEY,true);
            rowsChecked.add(row);
            img_check.setBackground(ResourceHold.getDrawable(R.mipmap.icon_file_checked02));
        }
    }

    public void onEditMenuClick(View convertView, RowObject row, int position, BaseFillAdapter.ViewHolder holder)  {
        String name = row.getString("name");
        if (ResourceHold.getString(R.string.share).equals(name)) {
            FileData.shareFileByRows(context, rowsChecked);
        } else if (ResourceHold.getString(R.string.modify).equals(name)) {
            if (rowsChecked.size() == 1) {
                RowObject rowObject = rowsChecked.get(0);
                showModifyDialog(rowObject);
            } else {
            }
        }

    }

    private void showDeleteDialog() {
        AlertDialog alertDialog1 = ViewUtils.showAlertDialog(getContext(),
                ResourceHold.getString(R.string.tip),
                ResourceHold.getString(R.string.delete_tip)+
                        rowsChecked.size()+
                        ResourceHold.getString(R.string.one)+
                        ResourceHold.getString(R.string.file),
                ResourceHold.getString(R.string.sure),
                ResourceHold.getString(R.string.cancle),
                new DialogInterface.OnClickListener() {//添加"Yes"按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        delete();
                    }
                }, new DialogInterface.OnClickListener() {//添加取消
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        alertDialog1.show();
    }

    public void sort(RowObject row) {

    }


    public void showModifyDialog(RowObject rowObject) {
    }

    public void cancleEdit() {
        isPasteMode = false;
        listAdapter.setPasteMode(isPasteMode);
//        ln_cancle.setVisibility(GONE);
        tv_move.setText(ResourceHold.getString(R.string.move));
        switchEditMode(false);
    }

    /**
     * handler用于在主线程刷新ui
     */
    public final Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0) {
            } else if (msg.what == 1) {
               sucessHandler();
            } else if (msg.what == -1) {
                ViewUtils.toast(msg.obj + "");
            }
        }
    };

    public void sucessHandler() {
        if(dialog!=null&&dialog.isShowing()){
            dialog.dismiss();//数据加载完成取消弹窗
        }
        isPasteMode = false;
        switchEditMode(false);
        tv_copy.setText(ResourceHold.getString(R.string.copy));
//        tv_edit_title.setText(ResourceHold.getString(R.string.edit));
        loadData(curCollectDir);
        if(onSucessHandlerListen!=null){
            onSucessHandlerListen.onSucess();
        }
    }

    private void copyOrPaste() {
        if (ResourceHold.getString(R.string.copy).equals(tv_copy.getText())) {
            for (int i = 0; i < rowsChecked.size(); i++) {
                RowObject rowObject = rowsChecked.get(i);
                rowObject.remove(FileListAdapter.LOCK_KEY);
            }
            tv_copy.setText(ResourceHold.getString(R.string.paste));
            isPasteMode = true;
            tv_edit_title.setText(ResourceHold.getString(R.string.copyTo));
        } else {
           copyFile();
        }
        listAdapter.notifyDataSetChanged();
    }

    public void copyFile() {
//        //加载弹窗
//        LoadingDialog.Builder loadBuilder = new LoadingDialog.Builder(context)
//                .setMessage(ResourceHold.getString(R.string.copying_file) + "...")
//                .setCancelable(true)//返回键是否可点击
//                .setCancelOutside(false);//窗体外是否可点击
//        dialog = loadBuilder.create();
//        dialog.show();//显示弹窗
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileData.copyFileByRows(rowsChecked, curCollectDir, handler);
            }
        }).start();

    }


    public void logicMoveFile() {
        if (ResourceHold.getString(R.string.move).equals(tv_move.getText())) {
            for (int i = 0; i < rowsChecked.size(); i++) {
                RowObject rowObject = rowsChecked.get(i);
                rowObject.put(FileListAdapter.LOCK_KEY, true);
            }
            tv_move.setText(ResourceHold.getString(R.string.paste));
            isPasteMode = true;
//            ln_cancle.setVisibility(VISIBLE);
            tv_edit_title.setText(ResourceHold.getString(R.string.moveTo));
        } else {
            moveFile();
            switchEditMode(false);
            isPasteMode = false;
            tv_move.setText(ResourceHold.getString(R.string.move));
            tv_edit_title.setText(ResourceHold.getString(R.string.edit));
            loadData(curCollectDir);
        }
        listAdapter.setPasteMode(isPasteMode);
        listAdapter.notifyDataSetChanged();

    }

    public void moveFile() {
        FileData.moveFileByRows(rowsChecked, curCollectDir, handler);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void checkAll(Boolean check) {
//        if (check) {
//            img_check_all.setBackground(getResources().getDrawable(R.mipmap.icon_file_uncheck));
//        } else {
//            img_check_all.setBackground(getResources().getDrawable(R.mipmap.icon_file_checked));
//        }
        rowsChecked.clear();
        for (int i = 0; i < rows.size(); i++) {
            RowObject rowObject = rows.get(i);
            rowObject.put(FileListAdapter.CHECK_KEY, check);
        }
        rowsChecked.addAll(rows);
        listAdapter.notifyDataSetChanged();
    }

    public void delete() {
        FileData.deleteFileByRows(rowsChecked, handler);
    }

    public boolean isRootDir() {
        return curCollectDir.equals(FileUtils.getSDCardPath());
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onItemClick(View convertView, RowObject row, int position, BaseFillAdapter.ViewHolder holder) {
        String path = row.getString("path");
        if (isEditMode && !isPasteMode) {
            checkitem(row,holder);
            logicEditMenu();
        } else {
            if (row.getBoolean("isDir")) {
                openDir(row, path);
            } else {
                if(isCheckMode){
                    checkitem(row,holder);
                    tv_check_num.setText("("+rowsChecked.size()+")");
                }else{
                    openFile(row, path);
                }

            }
        }

    }

    public void logicEditMenu() {
            Integer[] names = editMenuListView.getNames();
            editMenuListView.refresh(names);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void checkitem(RowObject row, BaseFillAdapter.ViewHolder holder) {
        ImageView img_check = (ImageView) holder.views.get("img_check");
        Boolean isCheck = !row.getBoolean(FileListAdapter.CHECK_KEY);
        if (isCheck) {
            rowsChecked.add(row);
            img_check.setBackground(ResourceHold.getDrawable(R.mipmap.icon_file_checked02));
        } else {
            rowsChecked.remove(row);
            img_check.setBackground(ResourceHold.getDrawable(R.mipmap.icon_file_uncheck02));
        }
        row.put(FileListAdapter.CHECK_KEY, isCheck);
    }


    public void openDir(RowObject row, String path) {
        curCollectDir = path;
        logicTitle(curCollectDir);
        loadData(curCollectDir);
    }

    public void openFile(RowObject row, String path) {
        if(path.startsWith("/system/")||path.startsWith("/data/")&&path.endsWith(".apk")){
            ViewUtils.toast(ResourceHold.getString(R.string.tip_install_app_cannot_open));
        }else {
            AppUtils.openFileBySystemApp(getContext(), path);
        }
    }

    @Override
    public int setXmlLayout() {
        return R.layout.file_view;
    }


    public void switchEditMode(Boolean mode) {
        isEditMode = mode;
        if(onModeChangeListen!=null){
            onModeChangeListen.onModeChange(mode);
        }
        rowsChecked.clear();
        for (int i = 0; i < rows.size(); i++) {
            RowObject rowObject = rows.get(i);
            rowObject.remove(FileListAdapter.CHECK_KEY);
            rowObject.remove(FileListAdapter.LOCK_KEY);
        }
        if (isEditMode) {
            ln_edit_tail.setVisibility(View.VISIBLE);
//            ln_edit_head.setVisibility(View.VISIBLE);
//            ln_header.setVisibility(View.GONE);
            listAdapter.setEditMode(true);
            listAdapter.notifyDataSetChanged();
        } else {
            isPasteMode = false;
            listAdapter.setEditMode(false);
            listAdapter.notifyDataSetChanged();
            ln_edit_tail.setVisibility(View.GONE);
//            ln_edit_head.setVisibility(View.GONE);
//            ln_header.setVisibility(View.VISIBLE);
        }
    }


    public void swictCheckMode(Boolean mode) {
        tv_edit_title.setText(ResourceHold.getString(R.string.check));
        this.isCheckMode = mode;
        for (int i = 0; i < rows.size(); i++) {
            RowObject rowObject = rows.get(i);
            rowObject.remove(FileListAdapter.CHECK_KEY);
            rowObject.remove(FileListAdapter.LOCK_KEY);
        }
        if (isCheckMode) {
            rowsChecked.clear();
//            ln_sure.setVisibility(VISIBLE);
//            ln_edit_right.setVisibility(GONE);
//            ln_edit_head.setVisibility(View.VISIBLE);
//            ln_header.setVisibility(View.GONE);
            listAdapter.setCheckMode(true);
            listAdapter.notifyDataSetChanged();
        } else {
            isPasteMode = false;
            listAdapter.setCheckMode(false);
            listAdapter.notifyDataSetChanged();
//            ln_sure.setVisibility(GONE);
//            ln_edit_right.setVisibility(VISIBLE);
//            ln_edit_head.setVisibility(View.GONE);
//            ln_header.setVisibility(View.VISIBLE);
        }
    }

    public interface OnModeChangeListen{
       void onModeChange(boolean mode);
    }
    public interface OnFileCheckedListen{
        void onChecked(LinkedList<RowObject> rows);
    }

    public interface OnSucessHandlerListen{
        void onSucess();
    }



    public Boolean getEditMode() {
        return isEditMode;
    }

    public void setEditMode(Boolean editMode) {
        isEditMode = editMode;
    }

    public TextView getTitle() {
        return title;
    }

    public void setTitle(TextView title) {
        this.title = title;
    }

    public boolean isLoadByPath() {
        return isLoadByPath;
    }

    public void setLoadByPath(boolean loadByPath) {
        isLoadByPath = loadByPath;
    }

    public String getCurCollectDir() {
        return curCollectDir;
    }

    public void setCurCollectDir(String curCollectDir) {
        this.curCollectDir = curCollectDir;
    }

    public String getStrTitleName() {
        return strTitleName;
    }

    public void setStrTitleName(String strTitleName) {
        this.strTitleName = strTitleName;
        if (title != null) {
            title.setText(strTitleName);
        }
    }



    public OnModeChangeListen getOnModeChangeListen() {
        return onModeChangeListen;
    }

    public void setOnModeChangeListen(OnModeChangeListen onModeChangeListen) {
        this.onModeChangeListen = onModeChangeListen;
    }

    public OnSucessHandlerListen getOnSucessHandlerListen() {
        return onSucessHandlerListen;
    }

    public void setOnSucessHandlerListen(OnSucessHandlerListen onSucessHandlerListen) {
        this.onSucessHandlerListen = onSucessHandlerListen;
    }

    public View getLeftBtn() {
        return leftBtn;
    }

    public void setLeftBtn(View leftBtn) {
        this.leftBtn = leftBtn;
    }

    public int getItemLayoutId() {
        return R.layout.file_list_item;
    }

    public void setItemLayoutId(int itemLayoutId) {
        this.itemLayoutId = itemLayoutId;
    }




    public Button getBtn_add() {
        return btn_add;
    }

    public void setBtn_add(Button btn_add) {
        this.btn_add = btn_add;
    }

    public OnFileCheckedListen getOnFileCheckedListener() {
        return onFileCheckedListener;
    }

    public void setOnFileCheckedListener(OnFileCheckedListen onFileCheckedListener) {
        this.onFileCheckedListener = onFileCheckedListener;
    }

    public FrameDialog getEditMenuDialog() {
        return editMenuDialog;
    }

    public void setEditMenuDialog(FrameDialog editMenuDialog) {
        this.editMenuDialog = editMenuDialog;
    }

    public LinkedList<RowObject> getRowsChecked() {
        return rowsChecked;
    }

    public void setRowsChecked(LinkedList<RowObject> rowsChecked) {
        this.rowsChecked = rowsChecked;
    }
}
