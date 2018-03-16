package com.oaui.view.attachment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.Toast;

import com.oaui.L;
import com.oaui.R;
import com.oaui.utils.DateTimeUtils;
import com.oaui.utils.FileUtils;
import com.oaui.utils.StringUtils;
import com.oaui.utils.ViewUtils;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * Created by pc on 2017/3/20.
 *
 * @author liuzhongjun
 */
@SuppressLint("NewApi")
public class VideoRecordActivity extends Activity implements
        View.OnClickListener {
    public static final int REQUEST_CODE_VIDEO = 3;

    public static final int REQUEST_CAMERA_PERMISSION_CODE = 10;
    private static final String TAG = "VideoRecordActivity";
    public static final int CONTROL_CODE = 1;
    public static final String KEY_VIDEO_FILE_PATH = "videoPath";
    // UI
    private ImageView mRecordControl;
    private ImageView mPauseRecord;
    private SurfaceView surfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Chronometer mRecordTime;

    // DATA
    private boolean isRecording;// 标记，判断当前是否正在录制
    private boolean isPause; // 暂停标识
    private long mPauseTime = 0; // 录制暂停时间间隔

    // 存储文件
    private Camera mCamera;
    private MediaRecorder mediaRecorder;
    private String saveVideoPath = "";

    private Handler mHandler = new MyHandler(this);


    private static class MyHandler extends Handler {
        private final WeakReference<VideoRecordActivity> mActivity;

        public MyHandler(VideoRecordActivity activity) {
            mActivity = new WeakReference<VideoRecordActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            L.i("=========handleMessage=============="+msg);
            if (mActivity.get() == null) {
                return;
            }
            switch (msg.what) {
                case CONTROL_CODE:
                    // 开启按钮
                    mActivity.get().mRecordControl.setEnabled(true);
                    break;
            }
        }
    }

    private MediaRecorder.OnErrorListener OnErrorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mediaRecorder, int what, int extra) {
            try {
                if (mediaRecorder != null) {
                    mediaRecorder.reset();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //锁定竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.ui_atm_movie_record_act);
        saveVideoPath = getIntent().getStringExtra(KEY_VIDEO_FILE_PATH);
        if(StringUtils.isEmpty(saveVideoPath)){
            saveVideoPath= getOrCreateVideoPath();
        }
        initView();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //申请成功，可以拍照
            } else {
                ViewUtils.toast("没有权限！");
                finish();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void initView() {
        surfaceView = (SurfaceView) findViewById(R.id.record_surfaceView);
        mRecordControl = (ImageView) findViewById(R.id.record_control);
        mRecordTime = (Chronometer) findViewById(R.id.record_time);
        mPauseRecord = (ImageView) findViewById(R.id.record_pause);
        mRecordControl.setOnClickListener(this);
        mPauseRecord.setOnClickListener(this);
        //mPauseRecord.setEnabled(false);

        // 配置SurfaceHodler
        mSurfaceHolder = surfaceView.getHolder();
        // 设置Surface不需要维护自己的缓冲区
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        // 设置分辨率
        //mSurfaceHolder.setFixedSize(320, 280);
        // 设置该组件不会让屏幕自动关闭
        mSurfaceHolder.setKeepScreenOn(true);
        mSurfaceHolder.addCallback(mCallBack);// 回调接口
    }

    private SurfaceHolder.Callback mCallBack = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            initCamera();
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int format,
                                   int width, int height) {
            if (mSurfaceHolder.getSurface() == null) {
                return;
            }
            /*L.i("======enclosing_method========1");
            if(mCamera!=null){
				L.i("======enclosing_method========2");
				mCamera.autoFocus(new AutoFocusCallback() {
					@Override
					public void onAutoFocus(boolean success, Camera camera) {
						L.i("======enclosing_method========3");
						if(success){
							ViewUtils.toast("对焦成功！");
							mCamera.cancelAutoFocus();
							L.i("======enclosing_method========4");
						}
					}
					});
			}*/
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            stopCamera();
        }
    };

    /**
     * 初始化摄像头
     *
     * @throws IOException
     * @author liuzhongjun
     * @date 2016-3-16
     */
    private void initCamera() {

        if (mCamera != null) {
            stopCamera();
        }
        // 默认启动后置摄像头
        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        if (mCamera == null) {
            Toast.makeText(this, "未能获取到相机！", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
            // 配置CameraParams
            setCameraParams();
            // 启动相机预览
            mCamera.startPreview();
            mCamera.cancelAutoFocus();

        } catch (IOException e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    /**
     * 设置摄像头为竖屏
     *
     * @author lip
     * @date 2015-3-16
     */

    private void setCameraParams() {
        if (mCamera != null) {
            Camera.Parameters params = mCamera.getParameters();
            // 设置相机的横竖屏(竖屏需要旋转90°)
            if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                params.set("orientation", "portrait");
                mCamera.setDisplayOrientation(90);
            } else {
                params.set("orientation", "landscape");
                mCamera.setDisplayOrientation(0);
            }
            // 设置聚焦模式
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            // 缩短Recording启动时间
            params.setRecordingHint(true);
            // 影像稳定能力
            if (params.isVideoStabilizationSupported()) {
                params.setVideoStabilization(true);
            }
            if (params.isAutoExposureLockSupported()) {
                params.setAutoExposureLock(false);
            }
            //最大曝光补偿
//			params.setExposureCompensation(params.getMaxExposureCompensation());
            mCamera.setParameters(params);
        }
    }

    /**
     * 释放摄像头资源
     *
     * @author liuzhongjun
     * @date 2016-2-5
     */
    private void stopCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 开始录制视频
     */
    public void startRecord() {
        initCamera();
        mCamera.unlock();
        setConfigRecord();
        try {
            // 开始录制
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        isRecording = true;
        if (mPauseTime != 0) {
            mRecordTime.setBase(SystemClock.elapsedRealtime()
                    - (mPauseTime - mRecordTime.getBase()));
        } else {
            mRecordTime.setBase(SystemClock.elapsedRealtime());
        }
        mRecordTime.start();
    }

    /**
     * 停止录制视频
     */
    public void stopRecord() {
        if (isRecording && mediaRecorder != null) {
            // 设置后不会崩
            mediaRecorder.setOnErrorListener(null);
            mediaRecorder.setPreviewDisplay(null);
            // 停止录制
            mediaRecorder.stop();
            mediaRecorder.reset();
            // 释放资源
            mediaRecorder.release();
            mediaRecorder = null;

            mRecordTime.stop();
			/*
			 * //设置开始按钮可点击，停止按钮不可点击 mRecordControl.setEnabled(true);
			 * mPauseRecord.setEnabled(false);
			 */
            isRecording = false;
        }
    }

    public void pauseRecord() {

    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.record_control) {
            if (isPause) {
                // 代表视频暂停录制，后点击中心（即继续录制视频）
                // Intent intent = new Intent(VideoRecordActivity.this,
                // PlayVideoActivity.class);
                // Bundle bundle = new Bundle();
                // bundle.putString("videoPath", saveVideoPath);
                // intent.putExtras(bundle);
                // startActivity(intent);
            } else {
                if (!isRecording) {
                    // 开始录制视频
                    startRecord();
                    mRecordControl
                            .setImageResource(R.mipmap.recordvideo_stop);
                    mRecordControl.setEnabled(false);// 1s后才能停止
                    mHandler.sendEmptyMessageDelayed(CONTROL_CODE, 1000);
                    mPauseRecord.setVisibility(View.VISIBLE);
                    //mPauseRecord.setEnabled(true);
                } else {
                    // 停止视频录制
                    mRecordControl
                            .setImageResource(R.mipmap.recordvideo_start);
                    mPauseRecord.setVisibility(View.GONE);
                    //mPauseRecord.setEnabled(false);
                    stopRecord();
                    mCamera.lock();
                    stopCamera();
                    mRecordTime.stop();
                    mPauseTime = 0;
                    setResultIntent();

                }
            }

        } else if (i == R.id.record_pause) {
            stopRecord();
            mRecordTime.stop();
            finish();
            // if (isRecording) { //正在录制的视频，点击后暂停
            // mPauseRecord.setImageResource(R.drawable.control_play);
            // //暂停视频录制
            // mCamera.autoFocus(new Camera.AutoFocusCallback() {
            // @Override
            // public void onAutoFocus(boolean success, Camera camera) {
            // if (success == true)
            // VideoRecordActivity.this.mCamera.cancelAutoFocus();
            // }
            // });
            // stopRecord();
            // mRecordTime.stop();
            // isPause = true;
            //
            // if (saveVideoPath.equals("")) {
            // saveVideoPath = currentVideoFilePath;
            // } else {
            // new Thread(new Runnable() {
            // @Override
            // public void run() {
            // try {
            // String[] str = new String[]{saveVideoPath, currentVideoFilePath};
            // // VideoUtils.appendVideo(VideoRecordActivity.this,
            // getOrCreateVideoPath(getApplicationContext()) + "append.mp4", str);
            // File reName = new File(saveVideoPath);
            // File f = new File(getOrCreateVideoPath(getApplicationContext()) +
            // "append.mp4");
            // f.renameTo(reName);
            // if (reName.exists()) {
            // f.delete();
            // new File(currentVideoFilePath).delete();
            // }
            // } catch (Exception e) {
            // e.printStackTrace();
            // }
            // }
            // }).start();
            // }
            //
            // } else {
            // mPauseRecord.setImageResource(R.drawable.control_pause);
            // if (mPauseTime != 0) {
            // mRecordTime.setBase(SystemClock.elapsedRealtime() - (mPauseTime -
            // mRecordTime.getBase()));
            // } else {
            // mRecordTime.setBase(SystemClock.elapsedRealtime());
            // }
            // mRecordTime.start();
            // //继续视频录制
            // startRecord();
            // isPause = false;
            // }

        }

    }

    private void setResultIntent() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Intent intent = new Intent();
                    intent.putExtra(KEY_VIDEO_FILE_PATH,
                            saveVideoPath);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    ///**
    // * 创建视频文件保存路径
    // */
    //private boolean createRecordDir() {
    //	if (!Environment.MEDIA_MOUNTED.equals(Environment
    //			.getExternalStorageState())) {
    //		Toast.makeText(this, "请查看您的SD卡是否存在！", Toast.LENGTH_SHORT).show();
    //		return false;
    //	}
    //
    //	File sampleDir = new File(
    //			Environment
    //					.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
    //			"Record");
    //	if (!sampleDir.exists()) {
    //		sampleDir.mkdirs();
    //	}
    //	String recordName = "VID_"
    //			+ new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())
    //			+ ".mp4";
    //	mVecordFile = new File(sampleDir, recordName);
    //	currentVideoFilePath = mVecordFile.getAbsolutePath();
    //	return true;
    //}

    public static String getOrCreateVideoPath() {
        String videoPath = FileUtils.getAppDirPath() + "/" + DateTimeUtils.getCurrentDay() + DateTimeUtils.getCurrentTime() + ".mp4";
        File eis = new File(videoPath);
        try {
            if (!eis.exists()) {
                eis.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return videoPath;
    }

    /**
     * 配置MediaRecorder()
     */
    private void setConfigRecord() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.reset();
        mediaRecorder.setCamera(mCamera);
        mediaRecorder.setOnErrorListener(OnErrorListener);

        // 使用SurfaceView预览
        mediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());

        // 1.设置采集声音
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        // 设置采集图像
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        // 2.设置视频，音频的输出格式 mp4
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        // 3.设置音频的编码格式
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        // 设置图像的编码格式
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        // 设置立体声
        // mediaRecorder.setAudioChannels(2);
        // 设置最大录像时间 单位：毫秒
        // mediaRecorder.setMaxDuration(60 * 1000);
        // 设置最大录制的大小 单位，字节
        // mediaRecorder.setMaxFileSize(1024 * 1024);
        // 音频一秒钟包含多少数据位
        CamcorderProfile mProfile = CamcorderProfile
                .get(CamcorderProfile.QUALITY_480P);
        mediaRecorder.setAudioEncodingBitRate(44100);
        if (mProfile.videoBitRate > 2 * 1024 * 1024)
            mediaRecorder.setVideoEncodingBitRate(2 * 1024 * 1024);
        else
            mediaRecorder.setVideoEncodingBitRate(1024 * 1024);
        mediaRecorder.setVideoFrameRate(mProfile.videoFrameRate);

        // 设置选择角度，顺时针方向，因为默认是逆向90度的，这样图像就是正常显示了,这里设置的是观看保存后的视频的角度
        mediaRecorder.setOrientationHint(90);
        // 设置录像的分辨率
        mediaRecorder.setVideoSize(320, 240);

        // 设置录像视频保存地址
        mediaRecorder.setOutputFile(saveVideoPath);
    }


}
