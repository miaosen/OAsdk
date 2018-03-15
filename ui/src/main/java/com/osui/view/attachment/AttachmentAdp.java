package com.osui.view.attachment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.osui.ImageFactory;
import com.osui.L;
import com.osui.R;
import com.osui.data.RowObject;
import com.osui.utils.AppUtils;
import com.osui.view.listview.BaseFillAdapter;

import java.util.List;
import java.util.Map;

public class AttachmentAdp extends BaseFillAdapter {

    private Context context;


    public AttachmentAdp(Context context, List<RowObject> rows, int layout) {
        super(context, rows, layout);
        this.context = context;
    }

    @SuppressLint("NewApi")
    @Override
    public void setItem(View convertView, final RowObject row, int position,
                        ViewHolder holder) {
        Map<String, View> views=holder.views;
        View rl_item = views.get("rl_item");
        final String path=row.getString("path");
        rl_item.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                L.i("row=====" + row.toString());
                AppUtils.openFileBySystemApp(getContext(),path);
            }
        });
        //图片样式
        ImageView img_file = (ImageView) views.get("img_file");
        //中间的隐藏图标
        ImageView img_paly = (ImageView) views.get("img_paly");
        ImageView img_delete = (ImageView) views.get("img_delete");
        img_delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getRows().remove(row);
                notifyDataSetChanged();
            }
        });
        String type = row.getString("type") + "";
        //TODO 自动识别后缀名加载
        if (type.equals("voice")) {
            //录音
            img_paly.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.icon_music));
            img_paly.setVisibility(View.VISIBLE);
        } else if (type.equals("pic")||type.equals("sign")) {
            //图片
            //Bitmap bitmap=BitmapUtils.getBitmapByPath(path);
            //Bitmap bitmap1 = BitmapUtils.createImageThumbnail(bitmap,100,100,100);
            //Bitmap roundedCornerBitmap = BitmapUtils.getRoundedCornerBitmap(bitmap1,20);
            //BitmapUtils.saveBitmapToPathAsJpg(roundedCornerBitmap,"/storage/emulated/0/OSSDk/saaaaaaaa.jpg");
            //img_file.setImageBitmap(roundedCornerBitmap);
            ImageFactory.loadImageByCornerAndZoomSize(img_file,path);
        }else if (type.equals("video")) {
            //视频
            //Bitmap videoThumbnail = BitmapUtils.getVideoThumbnail(path, 100, 100, 1);
            //Bitmap roundedCornerBitmap = BitmapUtils.getRoundedCornerBitmap(videoThumbnail,20);
            //img_file.setImageBitmap(roundedCornerBitmap);
            ImageFactory.loadImageByCornerAndZoomSize(img_file,path);
            img_paly.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.icon_play));
            img_paly.setVisibility(View.VISIBLE);
        } else {
            //文件
            img_paly.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.icon_upload));
            img_paly.setVisibility(View.VISIBLE);
        }

    }



}
