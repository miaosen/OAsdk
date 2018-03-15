package com.osui.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ThumbnailUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @email 1510809124@qq.com
 * @author zengmiaosen
 * @CreateDate 2018/3/8 14:28
 * @Descrition 图片处理工具类
 */
public class BitmapUtils {


    /**
     * 获取视频的缩略图 必须在2.2及以上版本使用，因为其中使用了ThumbnailUtils这个类
     * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
     * @param videoPath 视频的路径
     * @param width 指定输出视频缩略图的宽度
     * @param height 指定输出视频缩略图的高度度
     * @param kind 参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。
     *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
     * @return 指定大小的视频缩略图
     */
    public static Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }


    /**
     * bitmap处理成圆角
     * 注意：jpg不支持透明通道，保存后透明的部分会变成黑色的，保存成png可以解决这个问题，但是体积可能会变大数倍
     * @param bitmap
     * @param roundPx 圆角半径 单位：像素点
     * @return
     */
    public static Bitmap  getRoundedCornerBitmap(Bitmap bitmap,float roundPx) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        //canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.WHITE);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return output;
    }


    /**
     * 将图片处理成一种颜色
     * @param bitmap
     * @param dfCheckImageColor
     * @return
     */
    public static Bitmap createRGBImage(Bitmap bitmap,
                                              String dfCheckImageColor) {
        int color = Color.parseColor(dfCheckImageColor);
        int bitmap_w = bitmap.getWidth();
        int bitmap_h = bitmap.getHeight();
        int[] arrayColor = new int[bitmap_w * bitmap_h];
        int count = 0;
        for (int i = 0; i < bitmap_h; i++) {
            for (int j = 0; j < bitmap_w; j++) {
                int color1 = bitmap.getPixel(j, i);
                // 获取原图透明度 A
                int alpha = color1 & 0xFF000000;
                // 获取传入颜色的RGB
                int red = (color & 0x00FF0000) >> 16;
                int green = (color & 0x0000FF00) >> 8;
                int blue = (color & 0x000000FF);
                 //合成颜色
                int newColor = alpha | (red << 16) | (green << 8) | blue;
                System.out.println("A1: " + alpha + "  R1: " + red + "  G1: "
						+ green + "  B1: " + blue);
                //像素点加入颜色
                arrayColor[count] = newColor;
                count++;
            }
        }
        bitmap = Bitmap.createBitmap(arrayColor, bitmap_w, bitmap_h,
                Bitmap.Config.ARGB_8888);
        return bitmap;
    }


    /**
     * 生成缩略图
     * @param bitmap
     * @return
     */
    public static Bitmap createImageThumbnail(Bitmap bitmap) {
        return createImageThumbnail(bitmap,0,0,100);
    }

    /**
     * 生成缩略图
     * @param bitmap
     * @param width  缩略图宽度
     * @param height 缩略图高度
     * @param sizeKb 大小多少kb以下
     * @return
     */
    public static Bitmap createImageThumbnail(Bitmap bitmap,int width,int height,int sizeKb) {
        if(width<=0&&height<=0){//没有设置宽高时，按宽度为128等比例压缩
            width=128;
            // 缩放成宽度为128的bitmap
            Double h=(double)width/bitmap.getWidth()*bitmap.getHeight();
            height=h.intValue();
        }else if(width>0&&height<=0){//只设置宽度
            Double h=(double)width/bitmap.getWidth()*bitmap.getHeight();
            height=h.intValue();
        }else if(width<=0&&height>0){//只设置高度
            Double w=(double)height/bitmap.getHeight()*bitmap.getWidth();
            width=w.intValue();
        }
        Bitmap thb_bitmap = zoomBitmap(bitmap, width,
                height);
        return compressBitmap(thb_bitmap,sizeKb);
    }

    /**
     * 获取缩略图不进行质量压缩
     * @param bitmap
     * @param width  缩略图宽度
     * @param height 缩略图高度
     * @return
     */
    public static Bitmap createImageThumbnail(Bitmap bitmap,int width,int height) {
        if(width<=0&&height<=0){//没有设置宽高时，按宽度为128等比例压缩
            width=128;
            // 缩放成宽度为128的bitmap
            Double h=(double)width/bitmap.getWidth()*bitmap.getHeight();
            height=h.intValue();
        }else if(width>0&&height<=0){//只设置宽度
            Double h=(double)width/bitmap.getWidth()*bitmap.getHeight();
            height=h.intValue();
        }else if(width<=0&&height>0){//只设置高度
            Double w=(double)height/bitmap.getHeight()*bitmap.getWidth();
            width=w.intValue();
        }
        return  zoomBitmap(bitmap, width,
                height);
    }

    /**
     * 质量压缩方法,
     * @param bitmap
     * @param sizeKb 压缩到多少k以下
     * @return
     */
    public static Bitmap compressBitmap(Bitmap bitmap,int sizeKb) {
        return compressBitmap(bitmap,sizeKb,Bitmap.CompressFormat.JPEG);
    }

    /**
     * 质量压缩方法,
     * @param bitmap
     * @param sizeKb 压缩到多少k以下
     * @return
     */
    public static Bitmap compressBitmap(Bitmap bitmap,int sizeKb,Bitmap.CompressFormat format) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(format, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int optionsSize = 90;
        while (baos.toByteArray().length / 1024 > sizeKb) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            bitmap.compress(format, optionsSize, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            optionsSize -= 10;// 每次都减少10
        }
        // 把压缩后的数据baos存放到ByteArrayInputStream中
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        // 把ByteArrayInputStream数据生成图片
        return BitmapFactory.decodeStream(isBm, null,null);
    }


    /**
     * 放大缩小图片
     *
     * @param bitmap
     * @param w
     * @param h
     * @return
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
        Bitmap newbmp = null;
        if (bitmap != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Matrix matrix = new Matrix();
            float scaleWidht = ((float) w / width);
            float scaleHeight = ((float) h / height);
            matrix.postScale(scaleWidht, scaleHeight);
            newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix,
                    true);
        }
        return newbmp;
    }


    /**
     * 根据路径 转bitmap
     * @param urlpath
     * @return
     */
    public static Bitmap getBitmapByPath(String urlpath) {
        Bitmap map = null;
        try {
            URL url = new URL("file://" + urlpath);
            URLConnection conn = url.openConnection();
            conn.connect();
            InputStream in;
            in = conn.getInputStream();
            map = BitmapFactory.decodeStream(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }


    /**
     * 把bitmap保存为图片
     * @param bitmap
     * @param filePath
     * @throws IOException
     */
    public static boolean saveBitmapToPathAsJpg(Bitmap bitmap, String filePath){
        return saveBitmapToPathAsJpg(bitmap,filePath,Bitmap.CompressFormat.JPEG);
    }

    /**
     * 把bitmap保存为图片
     * @param bitmap
     * @param filePath
     * @throws IOException
     */
    public static boolean saveBitmapToPathAsPng(Bitmap bitmap, String filePath){
        return saveBitmapToPathAsJpg(bitmap,filePath,Bitmap.CompressFormat.PNG);
    }


    /**
     * 把bitmap保存为图片
     * @param bitmap
     * @param filePath
     * @param format 格式，建议保存为Bitmap.CompressFormat.JPEG，占内存较小,缺点是不支持透明度
     * @throws IOException
     */
    public static boolean saveBitmapToPathAsJpg(Bitmap bitmap, String filePath, Bitmap.CompressFormat format){
        boolean compress=true;
        File orCreateFile = FileUtils.getOrCreateFile(filePath);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(orCreateFile);
            compress = bitmap.compress(format, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (Exception e) {
            compress=false;
            e.printStackTrace();
        }
        return compress;
    }



    public static String getImageType(String path){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        return options.outMimeType;
    }
}
