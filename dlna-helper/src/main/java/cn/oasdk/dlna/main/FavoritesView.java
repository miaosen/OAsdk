package cn.oasdk.dlna.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import cn.oasdk.dlna.R;
import cn.oasdk.dlna.dms.DLNAService;
import cn.oasdk.dlna.dms.MediaServer;
import cn.oaui.IntentFactory;
import cn.oaui.L;
import cn.oaui.annotation.InjectReader;
import cn.oaui.annotation.ViewInject;
import cn.oaui.data.RowObject;
import cn.oaui.form.FormUtils;
import cn.oaui.utils.AppUtils;
import cn.oaui.utils.FileUtils;
import cn.oaui.utils.SPUtils;
import cn.oaui.utils.StringUtils;
import cn.oaui.utils.ViewUtils;
import cn.oaui.view.CustomLayout;
import cn.oaui.view.FlowLayout;
import cn.oaui.view.TempFragment;
import cn.oaui.view.dialog.FrameDialog;
import cn.oaui.view.listview.BaseFillAdapter;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2019-08-08  15:15
 * @Descrition 收藏
 */

public class FavoritesView extends CustomLayout {


    @ViewInject
    TextView tv_book, tv_net,tv_edit,tv_delete,tv_checkall,tv_reve_check,tv_import;
    @ViewInject
    View ln_edit_left,ln_edit_right;

    FrameDialog fdl_net;
    View btn_sure, btn_cancle,ln_name;
    EditText ed_name,ed_filePath;

    String fileType="";

    @ViewInject
    FlowLayout flowLayout;
    FlowAdapter flowAdapter;

    TempFragment tempFragmentView;
    
   public static final String ITEM_TYPE="net_video";


    FileView.OnFileClickListener onFileClickListener;
    @ViewInject
    LinearLayout ln_edit;

    public FavoritesView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public FavoritesView(Context context) {
        super(context);
    }



    @Override
    public void onCreateView() {
        InjectReader.injectAllFields(this);
        ln_edit.setVisibility(VISIBLE);
        fdl_net = new FrameDialog(context, R.layout.dlg_net_edit);
        btn_sure = fdl_net.findViewById(R.id.btn_sure);
        btn_cancle = fdl_net.findViewById(R.id.btn_cancle);
        ln_name=fdl_net.findViewById(R.id.ln_name);
         ed_name=fdl_net.findViewById(R.id.name);
        ed_filePath=fdl_net.findViewById(R.id.filePath);
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RowObject contentRow = FormUtils.getContentRow(fdl_net);
                //添加收藏
                if(ln_name.getVisibility()==View.VISIBLE){

                    if(StringUtils.isEmpty(contentRow.getString("name"))||
                            StringUtils.isEmpty(contentRow.getString("filePath")) ){
                        ViewUtils.toast("名称和链接不能为空");
                    }else{
                        String mainid = contentRow.getString("_id");
                        if(StringUtils.isEmpty(mainid)){//添加收藏
                            contentRow.put("_id", StringUtils.getUUID());
                            contentRow.put("type", ITEM_TYPE);
                            contentRow.put("title", contentRow.getString("name"));
                            MediaServer.rowsNet.add(contentRow);
                        }else{//修改收藏
                            for (int i = 0; i <MediaServer.rowsNet.size() ; i++) {
                                RowObject rowObject = MediaServer.rowsNet.get(i);
                                if(mainid.equals(rowObject.getString("mainid"))){
                                    rowObject.putAll(contentRow);
                                    i=MediaServer.rowsNet.size();
                                }
                            }

                        }
                        SPUtils.saveRows(MediaServer.FILE_TYPE.NET, "url_list", MediaServer.rowsNet);
                        flowAdapter.notifyDataSetChanged();
                        fdl_net.dismiss();
                    }
                }else{//直接投屏
                        contentRow.put("type",ITEM_TYPE);
                        contentRow.put(MediaStore.Video.Media._ID,StringUtils.getUUID());
                        if(onFileClickListener!=null){
                            onFileClickListener.onFileClick(contentRow);
                        }
                        fdl_net.dismiss();
                }

            }
        });
        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fdl_net.dismiss();
            }
        });

        tv_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ln_name.setVisibility(VISIBLE);
                fdl_net.fullscreen();
                ed_name.setText("");
                ed_filePath.setText("");
            }
        });
        mClick mClick = new mClick();
        tv_net.setOnClickListener(mClick);
        tv_edit.setOnClickListener(mClick);
        tv_delete.setOnClickListener(mClick);
        tv_checkall.setOnClickListener(mClick);
        tv_reve_check.setOnClickListener(mClick);
        tv_import.setOnClickListener(mClick);
        tempFragmentView= TempFragment.addHideFragment((Activity) context,"attachment");
        tempFragmentView.setOnActivityResultListener(new TempFragment.OnActivityResultListener() {
            @Override
            public void onActivityResult(int requestCode, int resultCode, Intent data) {
                RowObject rowObject = IntentFactory.onActivityResult(requestCode, resultCode, data);
                String path = rowObject.getString("path");
                RowObject rowResult = analysisDpl(path);
                for(Object row:rowResult.values()){
                    MediaServer.rowsNet.add((RowObject) row);
                }
                SPUtils.saveRows(MediaServer.FILE_TYPE.NET, "url_list", MediaServer.rowsNet);
                flowAdapter.notifyDataSetChanged();
            }
        });
    }


    class mClick implements OnClickListener{
        @Override
        public void onClick(View view) {
            if(view==tv_edit){
                if("编  辑".equals(tv_edit.getText())){
                    ln_edit_left.setVisibility(GONE);
                    ln_edit_right.setVisibility(VISIBLE);
                    tv_edit.setText("完  成");
                    flowAdapter.setEditMode(true);
                }else{
                    ln_edit_left.setVisibility(VISIBLE);
                    ln_edit_right.setVisibility(GONE);
                    tv_edit.setText("编  辑");
                    flowAdapter.setEditMode(false);
                }
                flowAdapter.notifyDataSetChanged();
            }else  if(view==tv_delete){

                List<RowObject> rows = flowAdapter.getRows();
                Iterator<RowObject> iterator = rows.iterator();
                while (iterator.hasNext()) {
                    RowObject row=iterator.next();
                    Boolean isCheck = row.getBoolean("isCheck");
                    if(isCheck){
                        iterator.remove();
                    }
                }
                SPUtils.clear(MediaServer.FILE_TYPE.NET);
                SPUtils.saveRows(MediaServer.FILE_TYPE.NET, "url_list", MediaServer.rowsNet);
                flowAdapter.notifyDataSetChanged();
            }else  if(view==tv_checkall){
                List<RowObject> rows = flowAdapter.getRows();
                Iterator<RowObject> iterator = rows.iterator();
                while (iterator.hasNext()) {
                    RowObject row=iterator.next();
                    Boolean isCheck = row.getBoolean("isCheck");
                    if(!isCheck){
                        row.put("isCheck",true);
                    }
                }
                flowAdapter.notifyDataSetChanged();
            }else  if(view==tv_reve_check){
                List<RowObject> rows = flowAdapter.getRows();
                Iterator<RowObject> iterator = rows.iterator();
                while (iterator.hasNext()) {
                    RowObject row=iterator.next();
                    Boolean isCheck = row.getBoolean("isCheck");
                    if(isCheck){
                        row.put("isCheck",false);
                    }else{
                        row.put("isCheck",true);
                    }
                }
                flowAdapter.notifyDataSetChanged();
            }else  if(view==tv_import){
                IntentFactory.openFileExplore(tempFragmentView,"text/*");
            }else  if(view==tv_net){
                if(DLNAService.playerDevice==null){
                    //选择设备
                    if(onFileClickListener!=null){
                        onFileClickListener.onFileClick(new RowObject());
                    }
                }else {
                    ln_name.setVisibility(GONE);
                    fdl_net.fullscreen();
                    ed_name.setText("");
                    ed_filePath.setText("");
                }
            }
        }
    }

    private RowObject analysisDpl(String path) {
        File file=new File(path);
        String s = FileUtils.readFile(file);
        String[] line = s.split("\n");
        RowObject rowResult=new RowObject();
        String index="";
        for (int i = 0; i < line.length; i++) {
            String s1 = line[i];
            if(s1.contains("*")){
                String[] lineText = s1.split("\\*");
                index = lineText[0];
                RowObject row;
                if(rowResult.containsKey(index)){
                    row= rowResult.getRow(index);
                }else{
                    row=new RowObject();
                    row.put("type",ITEM_TYPE);
                    row.put(MediaStore.Video.Media._ID,StringUtils.getUUID());
                    rowResult.put(index,row);
                }
                String key = lineText[1];
                String value = lineText[2];
                if(StringUtils.isNotEmpty(key)&&StringUtils.isNotEmpty(value)){
                    if(key.equals("title")){
                        key="name";
                    }else if(key.equals("file")){
                        key="filePath";
                    }
                    row.put(key,value);
                }
            }
        }
        return rowResult;
    }


    @Override
    public void initData() {
        flowLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        flowAdapter = new FlowAdapter(MediaServer.rowsNet, R.layout.view_favorites_item);
        flowLayout.setAdapter(flowAdapter);
        flowAdapter.setOnItemClickListener(new BaseFillAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View convertView, RowObject row, int position) {
               L.i("============onItemClick==========="+row);
                if(flowAdapter.isEditMode){
                    Boolean isCheck = row.getBoolean("isCheck");
                    if(isCheck){
                        row.put("isCheck",false);
                    }else{
                        row.put("isCheck",true);
                    }
                    flowAdapter.notifyDataSetChanged();
                }else{
                    if(onFileClickListener!=null){
                        onFileClickListener.onFileClick(row);
                    }
                }
            }
        });
    }



    @Override
    public int setXmlLayout() {
        return R.layout.view_favorites_list;
    }


    class FlowAdapter extends BaseFillAdapter {

        boolean isEditMode=false;

        public FlowAdapter(List<RowObject> rows, int layout) {
            super(context, rows, layout);
        }

        @Override
        public void setItem(View convertView, final RowObject row, int position, ViewHolder holder) {
            View ln_bg = holder.views.get("ln_bg");
            setRoundedColor(ln_bg, 30, getColor(position));
            View ln_edit= holder.views.get("ln_edit");
            if(isEditMode){
                ln_edit.setVisibility(VISIBLE);
            }else{
                ln_edit.setVisibility(GONE);
            }
            Boolean isCheck = row.getBoolean("isCheck");
            CheckBox cb= (CheckBox) holder.views.get("cb");
            if(isCheck){
                cb.setChecked(true);
            }else{
                cb.setChecked(false);
            }

            //
            ImageView img_edit_item= (ImageView) holder.views.get("img_edit_item");
            img_edit_item.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    fdl_net.fullscreen();
                    FormUtils.setContentValues(fdl_net,row);
                }
            });
            convertView.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ln_edit_left.setVisibility(GONE);
                    ln_edit_right.setVisibility(VISIBLE);
                    tv_edit.setText("完  成");
                    flowAdapter.setEditMode(true);
                    return false;
                }
            });
        }

        public boolean isEditMode() {
            return isEditMode;
        }

        public void setEditMode(boolean editMode) {
            isEditMode = editMode;
        }
    }

    private String getColor(int position) {
        String[] argsColor = new String[]{
                "#FE0303", "#4795E9",  "#F8B51A", "#3CCF52"
        };
        String color=null;
        if(position%4==0){
            color=argsColor[0];
        }else  if(position%3==0){
            color=argsColor[1];
        }else  if(position%2==0){
            color=argsColor[2];
        }else{
            color=argsColor[3];
        }
        return color;
    }


    @SuppressLint("NewApi")
    public static Bitmap setRoundedColor(View view, float roundPx, String color) {
        Bitmap output = Bitmap.createBitmap(AppUtils.dip2px(90), AppUtils.dip2px(90), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, AppUtils.dip2px(90), AppUtils.dip2px(90));
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor(color));
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(output, rect, rect, paint);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(output);
        view.setBackground(bitmapDrawable);
        return output;
    }


    public void onItemModify(View convertView, RowObject row, int position, BaseFillAdapter.ViewHolder holder) {
        LinearLayout ln_msg= (LinearLayout) holder.views.get("ln_msg");
        ln_msg.setVisibility(GONE);
    }

    public FileView.OnFileClickListener getOnFileClickListener() {
        return onFileClickListener;
    }

    public void setOnFileClickListener(FileView.OnFileClickListener onFileClickListener) {
        this.onFileClickListener = onFileClickListener;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
