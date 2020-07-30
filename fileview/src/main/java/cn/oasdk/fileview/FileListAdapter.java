package cn.oasdk.fileview;

import android.content.Context;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.LinkedList;

import cn.oasdk.fileview.data.FileData;
import cn.oaui.ImageFactory;
import cn.oaui.L;
import cn.oaui.ResourceHold;
import cn.oaui.data.Row;
import cn.oaui.utils.AppUtils;
import cn.oaui.utils.StringUtils;
import cn.oaui.view.listview.BaseFillAdapter;


public class FileListAdapter extends BaseFillAdapter {

    OnLongClickListener onLongClickListener;

    public final static String CHECK_KEY="isCheck";
    public final static String LOCK_KEY="isLock";
    //编辑模式
    boolean isEditMode=false;
    //选中粘贴模式
    boolean isPasteMode =false;


    //选择模式
    boolean isCheckMode=false;


    private OnItemModifyListener onItemModifyListener;


    public FileListAdapter(Context context, LinkedList<Row> rows, int layout) {
        super(context, rows, layout);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void setItem(final View convertView, final Row row, final int position, final BaseFillAdapter.ViewHolder holder) {
       L.i("======setItem===== "+row);
        ImageView img_type= (ImageView) holder.views.get("img_type");
        LinearLayout ln_dir= (LinearLayout) holder.views.get("ln_dir");
        String path = row.getString("path");
        Boolean isDir = row.getBoolean("isDir");
        TextView tvFileName= (TextView) holder.views.get("fileName");
        String fileName=row.getString("fileName")+"";
        if(isDir){
            String dir = row.getString("dir");
            if(StringUtils.isNotEmpty(dir)){
                tvFileName.setText(dir);
            }else{
                tvFileName.setText(fileName);
            }
            ln_dir.setVisibility(View.VISIBLE);
            ImageFactory.loadImageCorner(img_type, ResourceHold.getDrawable(R.mipmap.icon_folder));

        }else{
            ln_dir.setVisibility(View.GONE);
            if(path.endsWith(".doc")||path.endsWith(".docx")){
                img_type.setImageDrawable(ResourceHold.getDrawable(R.mipmap.icon_word));
            }else if(path.endsWith(".xls")||path.endsWith(".xlsx")){
                img_type.setImageDrawable(ResourceHold.getDrawable(R.mipmap.icon_excel));
            }else if(path.endsWith(".pdf")){
                img_type.setImageDrawable(ResourceHold.getDrawable(R.mipmap.icon_pdf));
            }else if(path.endsWith(".html")||path.endsWith(".mht")){
                img_type.setImageDrawable(ResourceHold.getDrawable(R.mipmap.icon_html));
            }else if(path.endsWith(".apk")){
//                img_type.setImageDrawable(AppUtils.getApkIcon(path));
                ImageFactory.loadImageCorner(img_type, AppUtils.getApkIcon(path));
            }else if(FileData.isSuffixOf(path, FileData.RAR_SUFFIX)){
                img_type.setImageDrawable(ResourceHold.getDrawable(R.mipmap.icon_rar));
            }else if(FileData.isSuffixOf(path, FileData.VIDEO_SUFFIX)
                    ||FileData.isSuffixOf(path, FileData.IMAGE_SUFFIX)){
                ImageFactory.loadImageCorner(img_type, path);
            }else if(FileData.isSuffixOf(path, FileData.MUSIC_SUFFIX)){
//                img_type.setImageDrawable(ResourceHold.getDrawable(R.mipmap.icon_file_music));
                ImageFactory.loadImageCorner(img_type, ResourceHold.getDrawable(R.mipmap.icon_file_music));
            }else {
                ImageFactory.loadImageCorner(img_type, path);
//                img_type.setImageDrawable(ResourceHold.getDrawable(R.mipmap.icon_file));
            }
        }
        ImageView img_check= (ImageView) holder.views.get("img_check");
        ImageView img_tip_check= (ImageView) holder.views.get("img_tip_check");
        if(isEditMode||isCheckMode){
            img_check.setVisibility(View.VISIBLE);
            if(row.getBoolean(CHECK_KEY)){
                img_check.setBackground(ResourceHold.getDrawable(R.mipmap.icon_file_checked02));
            }else {
                img_check.setBackground(ResourceHold.getDrawable(R.mipmap.icon_file_uncheck02));
            }
            if(isPasteMode &&!row.getBoolean(LOCK_KEY)){
                img_check.setVisibility(View.GONE);
            }else{
                img_check.setVisibility(View.VISIBLE);
            }
            if((isCheckMode&&isDir)||(isPasteMode&&isDir)){
                img_check.setVisibility(View.GONE);
            }else{
                img_check.setVisibility(View.VISIBLE);
            }
            img_tip_check.setVisibility(View.GONE);
        }else{
            img_check.setVisibility(View.GONE);
            img_tip_check.setVisibility(View.VISIBLE);
        }
        if(row.getBoolean(LOCK_KEY)){
            convertView.setEnabled(false);
            convertView.setBackgroundColor(ResourceHold.getColor(R.color.grey_lt));
        }else{
            convertView.setEnabled(true);
            convertView.setBackgroundColor(ResourceHold.getColor(R.color.white));
        }
        if(onLongClickListener!=null){
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onLongClickListener.onClick(convertView,row,position,holder);
                    return true;
                }
            });
        }
        if(onItemModifyListener!=null){
            onItemModifyListener.setItem(convertView,row,position,holder);
        }
    }

    public interface OnItemModifyListener {
         void setItem(final View convertView, final Row row, final int position, final BaseFillAdapter.ViewHolder holder);
    }

    public interface OnLongClickListener {
        void onClick(final View convertView, final Row row, final int position, final BaseFillAdapter.ViewHolder holder);
    }

    public boolean isPasteMode() {
        return isPasteMode;
    }

    public void setPasteMode(boolean pasteMode) {
        isPasteMode = pasteMode;
    }

    public OnLongClickListener getOnLongClickListener() {
        return onLongClickListener;
    }

    public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    public boolean isEditMode() {
        return isEditMode;
    }

    public void setEditMode(boolean editMode) {
        isEditMode = editMode;
    }


    public OnItemModifyListener getOnItemModifyListener() {
        return onItemModifyListener;
    }

    public void setOnItemModifyListener(OnItemModifyListener onItemModifyListener) {
        this.onItemModifyListener = onItemModifyListener;
    }

    public boolean isCheckMode() {
        return isCheckMode;
    }

    public void setCheckMode(boolean checkMode) {
        isCheckMode = checkMode;
    }
}
