package cn.oaui.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.RequiresApi;
import cn.oaui.L;
import cn.oaui.UIGlobal;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2021-01-18  14:56
 * @Descrition
 */
public class ScreenShotUtils {


    private MediaProjectionManager mMediaProjectionManager;
    private WindowManager wm;
    private int displayWidth;
    private int displayHeight;
    private DisplayMetrics metrics;
    private int dpi;
    private ImageReader mImageReader;
    public static final int REQUEST_CODE = 1111;
    private MediaProjection project;
    private VirtualDisplay virtualDisplay;
    String pathImage = ""; // 存储路径


    Activity activity;


    ScreenEventListener screenEventListener;


    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ScreenShotUtils(Activity context){
        this.activity=context;
        // MediaProjectionManager对象,屏幕宽高及单位像素点,ImageReader对象
        mMediaProjectionManager = (MediaProjectionManager) UIGlobal.getApplication().getSystemService(
                Context.MEDIA_PROJECTION_SERVICE);
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        displayWidth = wm.getDefaultDisplay().getWidth();
        displayHeight = wm.getDefaultDisplay().getHeight();
        metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        dpi = metrics.densityDpi;// 英寸点数
        mImageReader = ImageReader.newInstance(displayWidth, displayHeight, PixelFormat.RGBA_8888, 1);

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startScreenShot(){
        if(screenEventListener!=null){
            screenEventListener.onStart();
        }
        // 申请用户授权
        Intent intent = mMediaProjectionManager.createScreenCaptureIntent();
        activity.startActivityForResult(intent, REQUEST_CODE);
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode != Activity.RESULT_OK) { // 取消授权,结束进程
                //activity.finish();
            } else { // 表示确认授权,并取得屏幕截图的bitmap
                if (project == null) {
                    project = mMediaProjectionManager.getMediaProjection(resultCode, data);
                }
                // 获取存储屏幕截图的虚拟显示器
                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        virtualDisplay = project.createVirtualDisplay("screen-mirror",
                                displayWidth, displayHeight, dpi,
                                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                                mImageReader.getSurface(), null, null);
                    }
                }, 50);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startCapture();
                    }
                }, 200);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void startCapture() {
        Image image = mImageReader.acquireLatestImage();
        int width = image.getWidth();
        int height = image.getHeight();
        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height,
                Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        //对屏幕截图bitmap进行截图
        // bitmap = Bitmap.createBitmap(bitmap, pointX, pointY, widthArea,
        // heightArea);
        image.close();
        if (bitmap != null) {
            try {
                File pictureFileDir =new File(AppUtils.getDefaultDirectory()) ;
                L.i("============onClick==========="+AppUtils.getDefaultDirectory()+"   "+pictureFileDir.exists()+"   "+pictureFileDir.mkdirs());
                if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {
                    return;
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
                String date = dateFormat.format(new Date());

                String photoFile = "Picture_" + date + ".jpg";
                pathImage = pictureFileDir.getPath() + File.separator + photoFile;
                File fileImage = new File(pathImage);
                if (!fileImage.exists()) {
                    fileImage.createNewFile();
                }
                FileOutputStream out = new FileOutputStream(fileImage);
                if (out != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG,20, out);
                    out.flush();
                    out.close();
                    if (fileImage.isFile() && fileImage.length() > 0) {
                        //ViewUtils.toast("图片位置："+pathImage);
                        virtualDisplay.release(); // 释放存放屏幕截图的虚拟显示器
                        // 退出截屏
                        //activity.finish();
                        if(screenEventListener!=null){
                            screenEventListener.onFinish(fileImage.getAbsolutePath());
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public interface ScreenEventListener{
        void onStart();
        void onFinish(String path);
    }


    public ScreenEventListener getScreenEventListener() {
        return screenEventListener;
    }

    public void setScreenEventListener(ScreenEventListener screenEventListener) {
        this.screenEventListener = screenEventListener;
    }
}
