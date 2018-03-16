package com.oaui;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.oaui.data.RowObject;
import com.oaui.utils.BitmapUtils;
import com.oaui.utils.DateTimeUtils;
import com.oaui.utils.FileUtils;
import com.oaui.utils.URIUtils;
import com.oaui.view.attachment.VideoRecordActivity;

import java.io.File;


/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2018-03-02  11:50
 * @Descrition
 */

public class IntentFactory {

    /**
     * 拍照请求码
     */
    public static final int REQUEST_CODE_TAKE_PIC = 10001;

    /**
     * 拍照请求码
     */
    public static final int REQUEST_CODE_OPEN_FILE = 10002;


    public static String imagePath = "";


    /**
     * 开始照相
     */
    public static void takePicture(Fragment fragment) {
        fragment.startActivityForResult(getTakePicIntent(), REQUEST_CODE_TAKE_PIC);
    }


    /**
     * 开始照相
     */
    public static void takePicture(Activity activity) {
        activity.startActivityForResult(getTakePicIntent(), REQUEST_CODE_TAKE_PIC);
    }


    /**
     * 拍照
     * @return
     */
    public static Intent getTakePicIntent() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imagePath = FileUtils.getAppDirPath() + "/" + DateTimeUtils.getCurrentDay() + DateTimeUtils.getCurrentTime() + ".jpg";
        File tempFile = FileUtils.createFile(imagePath);
        Uri imageUri = Uri.fromFile(tempFile);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        return openCameraIntent;
    }


    /**
     * 录像
     * @param activity
     */
    public static void movieRecord(Activity activity) {
        if (isCameraPermission(activity, VideoRecordActivity.REQUEST_CAMERA_PERMISSION_CODE)){
            Intent intent = new Intent(activity, VideoRecordActivity.class);
            String videoPath = FileUtils.getAppDirPath() + "/" + DateTimeUtils.getCurrentDay() + DateTimeUtils.getCurrentTime() + ".mp4";
            intent.putExtra(VideoRecordActivity.KEY_VIDEO_FILE_PATH,videoPath);
            activity.startActivityForResult(intent, VideoRecordActivity.REQUEST_CODE_VIDEO);
        }
    }


    /**
     * 录像
     * @param fragment
     */
    public static void movieRecord(Fragment fragment) {
        Activity activity=fragment.getActivity();
        if (isCameraPermission(activity,VideoRecordActivity.REQUEST_CAMERA_PERMISSION_CODE)){
            Intent intent = new Intent(activity, VideoRecordActivity.class);
            String videoPath = FileUtils.getAppDirPath() + "/" + DateTimeUtils.getCurrentDay() + DateTimeUtils.getCurrentTime() + ".mp4";
            intent.putExtra(VideoRecordActivity.KEY_VIDEO_FILE_PATH,videoPath);
            fragment.startActivityForResult(intent, VideoRecordActivity.REQUEST_CODE_VIDEO);
        }
    }


    public static boolean isCameraPermission(Activity context, int requestCode){
        if (Build.VERSION.SDK_INT >= 23) {
            String[] PERMISSIONS_CAMERA_AND_STORAGE = {
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CAMERA};
            int recordAudioPermission = ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.RECORD_AUDIO);
            int cameraPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
            if (recordAudioPermission != PackageManager.PERMISSION_GRANTED || cameraPermission!= PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(context, PERMISSIONS_CAMERA_AND_STORAGE,
                        requestCode);
                return false;
            }
        }
        return true;
    }


    /**
     * 调用系统文件管理器
     * 无类型限制
     * @param fragment
     */
    public static void openFileExplore(Fragment fragment){
        openFileExplore(fragment,"*/*");
    }

    /**
     *
     * 图片：image/*
     * 音频：audio/*
     * 视频：video/*
     * 同时选择视频和图片：video/*;image/*

     * @param fragment
     */
    public static void openFileExplore(Fragment fragment,String fileType){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(fileType);//无类型限制
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        fragment.startActivityForResult(intent, REQUEST_CODE_OPEN_FILE);
    }

    /**
     * 返回图片路径
     *
     * @param requestCode
     * @param resultCode
     * @param intent
     * @return
     */
    public static RowObject onActivityResult(int requestCode,
                                             int resultCode, Intent intent) {
        if (requestCode == REQUEST_CODE_TAKE_PIC) {
            if (resultCode == Activity.RESULT_OK) {
                RowObject result = new RowObject();
                result.put("type", "pic");
                result.put("path", imagePath);
                //result.put("thumbnailPath" ,fmThumbPic.getPath());
                return result;
            }
        } else if (requestCode == VideoRecordActivity.REQUEST_CODE_VIDEO) {
            if (resultCode == Activity.RESULT_OK) {
                String videoPath = intent.getStringExtra(VideoRecordActivity.KEY_VIDEO_FILE_PATH);
                RowObject result = new RowObject();
                result.put("type", "video");
                result.put("path", videoPath);
                return result;
            }
        }else if (requestCode == REQUEST_CODE_OPEN_FILE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = intent.getData();
                String path = URIUtils.getPath(uri);
                RowObject result = new RowObject();
                if( BitmapUtils.getImageType(path)!=null){
                    result.put("type", "pic");
                }
                result.put("path", path);
                return result;
            }
        }
        return null;
    }





}
