package cn.oaui.view.attachment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import java.util.List;
import java.util.Map;

import cn.oaui.ImageFactory;
import cn.oaui.R;
import cn.oaui.data.RowObject;
import cn.oaui.utils.AppUtils;
import cn.oaui.view.listview.BaseFillAdapter;

public class AttachmentAdp extends BaseFillAdapter {

    public String urlKey="path";


    public AttachmentAdp(Context context, List<RowObject> rows, int layout) {
        super(context, rows, layout);
    }

    @SuppressLint("NewApi")
    @Override
    public void setItem(View convertView, final RowObject row, int position,
                        ViewHolder holder) {
        Map<String, View> views=holder.views;
        View rl_item = views.get("rl_item");
        final String path=row.getString(urlKey);
        rl_item.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
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
        if (isVoice(path)) {
            //录音
            img_paly.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.icon_music));
            img_paly.setVisibility(View.VISIBLE);
        } else if (isImage(path)) {
            //图片
            //Bitmap bitmap=BitmapUtils.getBitmapByPath(path);
            //Bitmap bitmap1 = BitmapUtils.createImageThumbnail(bitmap,100,100,100);
            //Bitmap roundedCornerBitmap = BitmapUtils.getRoundedCornerBitmap(bitmap1,20);
            //BitmapUtils.saveBitmapToPathAsJpg(roundedCornerBitmap,"/storage/emulated/0/OSSDk/saaaaaaaa.jpg");
            //img_file.setImageBitmap(roundedCornerBitmap);
            ImageFactory.loadImageCorner(img_file,path);
        }else if (isVideo(path)) {
            //视频
            //Bitmap videoThumbnail = BitmapUtils.getVideoThumbnail(path, 100, 100, 1);
            //Bitmap roundedCornerBitmap = BitmapUtils.getRoundedCornerBitmap(videoThumbnail,20);
            //img_file.setImageBitmap(roundedCornerBitmap);
            ImageFactory.loadImageCorner(img_file,path);
            img_paly.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.icon_play));
            img_paly.setVisibility(View.VISIBLE);
        } else {
            //文件
            img_paly.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.icon_upload));
            img_paly.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 判断是否图片
     * @param urlPath
     * @return
     */
    private boolean isImage(String urlPath) {
       String path=urlPath.toLowerCase();
        return path.endsWith(".jpg")||path.endsWith(".png")||path.endsWith(".jpeg")
                ||path.endsWith(".gif")||path.endsWith(".bmp")||path.endsWith(".wbmp");
    }

    /**
     * 判断是否音频
     * @param urlPath
     * @return
     */
    private boolean isVoice(String urlPath) {
        String path=urlPath.toLowerCase();
        return path.endsWith(".mp3")||path.endsWith(".amr")||path.endsWith(".flac")
                ||path.endsWith(".ogg")||path.endsWith(".wav")||path.endsWith(".ape");
    }
    /**
     * 判断是否视频
     * @param urlPath
     * @return
     */
    private boolean isVideo(String urlPath) {
        String path=urlPath.toLowerCase();
        return path.endsWith(".mp4")||path.endsWith(".mpg")||path.endsWith(".3gp")
                ||path.endsWith(".rmvb")||path.endsWith(".avi")||path.endsWith(".rm");
    }

    public String getUrlKey() {
        return urlKey;
    }

    public void setUrlKey(String urlKey) {
        this.urlKey = urlKey;
    }


}
