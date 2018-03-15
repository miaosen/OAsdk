package com.osui;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.osui.utils.BitmapUtils;

import java.io.ByteArrayOutputStream;


/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2018-03-12  15:45
 * @Descrition
 */

public class ImageFactory {


    public static DrawableRequestBuilder getCommentManager(Context context, Object showFile) {
        return Glide.with(context)
                .load(showFile)//"http://ww4.sinaimg.cn/large/610dc034gw1f96kp6faayj20u00jywg9.jpg"
                .dontAnimate()
                //.placeholder(R.drawable.loading) //加载中图片
                .fitCenter()
                .error(R.mipmap.icon_image_load_fail);
    }

    /**
     * 加载图片
     * @param imageView
     * @param path
     */
    public static void loadImage(ImageView imageView, String path) {
        getCommentManager(imageView.getContext(), path).into(imageView);
    }

    public static void loadImageByCornerAndZoomSize(ImageView imageView, String path) {
        getCommentManager(imageView.getContext(), path).transform(new BitmapTransformation(imageView.getContext()) {
            @Override
            protected Bitmap transform(BitmapPool bitmapPool, Bitmap bitmap, int outWidth, int outHeight) {
                if(bitmap!=null){
                    Bitmap imageThumbnail = BitmapUtils.createImageThumbnail(bitmap, 100, 100);
                    return BitmapUtils.getRoundedCornerBitmap(imageThumbnail,20);
                }
                return null;
            }

            @Override
            public String getId() {
                return getClass().getName();
            }
        }).into(imageView);
    }


    public static void loadImage(ImageView imageView, Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        getCommentManager(imageView.getContext(), bytes).into(imageView);
    }
}
