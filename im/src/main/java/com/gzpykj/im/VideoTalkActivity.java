package com.gzpykj.im;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import cn.nodemedia.NodeCameraView;
import cn.nodemedia.NodePlayer;
import cn.nodemedia.NodePlayerDelegate;
import cn.nodemedia.NodePlayerView;
import cn.nodemedia.NodePublisher;
import cn.nodemedia.NodePublisherDelegate;
import cn.oahttp.HttpRequest;
import cn.oahttp.callback.StringCallBack;
import cn.oaui.L;
import cn.oaui.annotation.InjectReader;
import cn.oaui.annotation.ViewInject;
import cn.oaui.data.Row;
import cn.oaui.utils.ScreenShotUtils;
import cn.oaui.utils.StringUtils;
import cn.oaui.utils.ViewUtils;
import cn.oaui.view.dialog.extra.LoadingDialog;

/**
 * Created by Sikang on 2017/5/2.
 */

public class VideoTalkActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "CameraActivity";

    private Button mPublishBtn;
    private Button mCameraSwitchBtn;
    private Button mEncoderBtn;
    private EditText mRempUrlEt;
    //private SrsPublisher mPublisher;
    private String rtmpUrl;
    private String rtmpOtherUrl, otherName, rtmpOtherID;

    private String talkType, callOrAnswer;

    boolean isVideo = false;

    @ViewInject
    NodePlayerView nodePlayerView;
    private NodePlayer nodePlayer;

    private SharedPreferences sp;
    private NodePublisher nodePublisher;

    boolean isPublisherReady = false, isAnswerCall = false;

    // 当前Activity的sId
    private String m_sId = null;

    boolean hasRecord = false;


    @ViewInject
    LinearLayout ln_phone_down, ln_close_camera, ln_switch_camera, ln_answer_call, ln_cancel_call,
            ln_answer_or_cancel, ln_video_control, ln_start_screen_shot, ln_start_record;
    @ViewInject
    NodeCameraView nodeCameraView;

    @ViewInject
    TextView tv_change_vioce, tv_tip, tv_start_record,tv_otherName;


    boolean firstConnected = true;

    MediaPlayer mediaPlayer;

    ScreenShotUtils screenShotUtils;


    String MAINID = StringUtils.getUUID(), FILEID = StringUtils.getUUID();


    LoadingDialog loadingDialog;

    AlertDialog exitAlertDialog;


    List<String> imagesList = new LinkedList<>();


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);// 隐藏状态栏
        setContentView(R.layout.video_talk_act);

        InjectReader.injectAllFields(this);
        screenShotUtils = new ScreenShotUtils(VideoTalkActivity.this);
        screenShotUtils.setScreenEventListener(new ScreenShotUtils.ScreenEventListener() {
            @Override
            public void onStart() {
                ln_video_control.setVisibility(View.GONE);
            }

            @Override
            public void onFinish(String path) {
                ln_video_control.setVisibility(View.VISIBLE);
                ViewUtils.toast("截图成功！");
                imagesList.add(path);
            }
        });
        rtmpUrl = getIntent().getStringExtra("service_url").replace("video_call_", "");
        otherName = getIntent().getStringExtra("other_name");
        tv_otherName.setText(otherName);
        rtmpOtherID =getIntent().getStringExtra("other_id");
        talkType = getIntent().getStringExtra("talkType");
        callOrAnswer = getIntent().getStringExtra("callOrAnswer");
        rtmpOtherUrl = getIntent().getStringExtra("client_url");
        mPublishBtn = (Button) findViewById(R.id.publish);
        mCameraSwitchBtn = (Button) findViewById(R.id.swCam);
        mEncoderBtn = (Button) findViewById(R.id.swEnc);
        mRempUrlEt = (EditText) findViewById(R.id.url);
        if ("video_call_".equals(callOrAnswer)) {
            ln_answer_or_cancel.setVisibility(View.GONE);
            ln_video_control.setVisibility(View.VISIBLE);
            isAnswerCall = true;
            openAssetMusics("call.wav");
        } else {
            openAssetMusics("phone_come.mp3");
        }
        // 订阅服务器信息
        //m_sendInterface = (ISend) AppContext.mainThread;
        //if (m_sendInterface != null) {
        //    m_sId = m_sendInterface.GetSubId();
        //    m_sendInterface.RegistSubsciber(m_sId, new ISubSciber() {
        //        @Override
        //        public void ArriveTalk(ArrayList<XMLProtocol.Talk> obj) {
        //            for (int i = 0; i < obj.size(); i++) {
        //                final XMLProtocol.Talk talk = obj.get(i);
        //                L.i("============ArriveTalk===========" + JSONSerializer.toJSONString(talk));
        //                if (talk.talkType.equals(XMLProtocol.Talk.TalkType.VIDEO) && JsonUtils.isCanToRow(talk.talkMsg)
        //                        && "video_answer_".equals(JsonUtils.jsonToRow(talk.talkMsg).getString("callOrAnswer"))) {
        //                    //if (talk.talkMsg.startsWith("video_answer_rtmp") && talk.talkOther.equals(otherName)) {
        //                    //new Handler().postDelayed(new Runnable(){
        //                    //    public void run() {
        //                    L.i("============ArriveTalk===========" + talk.talkMsg);
        //                    Row row = JsonUtils.jsonToRow(talk.talkMsg);
        //                    rtmpOtherUrl = row.getString("rtmpurl");
        //                    otherName = row.getString("NAME");
        //                    rtmpOtherID = row.getString("MAINID");
        //                    //rtmpOtherUrl = talk.talkMsg.replace("video_answer_", "");
        //                    runOnUiThread(new Runnable() {
        //                        @Override
        //                        public void run() {
        //                            L.i("============run===========" + rtmpOtherUrl);
        //                            nodePlayer.setInputUrl(rtmpOtherUrl);
        //                            nodePlayer.start();
        //                            ln_start_record.setVisibility(View.VISIBLE);
        //                            //tv_tip.setText("正在连接服务器..." + talk.talkMsg);
        //                            //ViewUtils.toast("正在连接服务器...");
        //                            if (mediaPlayer != null) {
        //                                mediaPlayer.stop();
        //                            }
        //                        }
        //                    });
        //                    //}
        //                    //}, 2000);
        //
        //                } else if (talk.talkMsg.equals("call_end") && (talk.talkOther.equals(otherName) || talk.talkOther.equals(GlobalConst.NAME))) {
        //                    runOnUiThread(new Runnable() {
        //                        @Override
        //                        public void run() {
        //                            finishAndJumpRecord();
        //                        }
        //                    });
        //                }
        //            }
        //        }
        //
        //        @Override
        //        public void ArriveUser(ArrayList<XMLProtocol.User> obj) {
        //        }
        //    });
        //}
        initPlay();
        initPublish();
        ln_phone_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAndJumpRecord();
            }
        });
        ln_close_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("转到语音".equals(tv_change_vioce.getText())) {
                    switchToVoice();
                } else {
                    switchToVideo();
                }

            }
        });
        ln_switch_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nodePublisher.switchCamera();
                //mPublisher.switchCameraFace((mPublisher.getCamraId() + 1) % Camera.getNumberOfCameras());
            }
        });
        ln_answer_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                L.i("============onClick==========="+rtmpOtherUrl);
                if (StringUtils.isNotEmpty(rtmpOtherUrl)) {
                    nodePlayer.setInputUrl(rtmpOtherUrl);
                    nodePlayer.start();
                    ln_answer_or_cancel.setVisibility(View.GONE);
                    ln_video_control.setVisibility(View.VISIBLE);
                    isAnswerCall = true;
                    L.i("============onClick==========="+isPublisherReady);
                    if (isPublisherReady) {
                        sendMyRtmp();
                    }
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                    }

                }
            }
        });
        nodePlayerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ln_video_control.setVisibility(ln_video_control.getVisibility()==View.VISIBLE?View.GONE:View.VISIBLE);
            }
        });
        ln_cancel_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callend();
                finishAndJumpRecord();
            }
        });
        ln_start_screen_shot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //screenShotUtils.startScreenShot();
            }
        });
        ln_start_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();
                final String controlUrl1 = ImGlobal.HOST + "/control/record/start?app=live&name=" + rtmpUrl.substring(rtmpUrl.indexOf("home")) + "&rec=all";
                final String controlUrl2 = ImGlobal.HOST + "/control/record/start?app=live&name=" + rtmpOtherUrl.substring(rtmpOtherUrl.indexOf("home")) + "&rec=all";
                String url = ImGlobal.HOST + "/actions/imCheckRecordAction/startRecord";
                final HttpRequest request = new HttpRequest(url);
                //request.setMethod(HttpRequest.HttpMethod.GET);
                request.addParam("rtmp_check_man", rtmpUrl.substring(rtmpUrl.indexOf("home")));
                request.addParam("rtmp_check_comapny", rtmpOtherUrl.substring(rtmpOtherUrl.indexOf("home")));
                request.addParam("MAINID", MAINID);
                request.addParam("FILEID", FILEID);
                request.addParam("COMAPNY_ID", rtmpOtherID);
                request.addParam("COMAPNY_NAME", otherName);
                request.addParam("CHECK_MAN_ID", ImGlobal.USER_ID);
                request.addParam("CHECK_MAN", ImGlobal.NAME);
                request.addParam("CHECK_MAN_LOGIN_NAME", ImGlobal.USER_NAME);
                request.setCallback(new StringCallBack() {
                    @Override
                    public void onSuccess(String text) {
                        loadingDialog.dismiss();
                        ln_start_record.setEnabled(false);
                        hasRecord = true;
                        tv_start_record.setText("监管中...");
                        ViewUtils.toast("初始化成功，已开始监管！");
                        L.i("============onSuccess===========" + text);
                        //if (StringUtils.isNotEmpty(text)) {
                        //    HttpRequest request2 = new HttpRequest(controlUrl2);
                        //    request2.setCallback(new StringCallBack() {
                        //        @Override
                        //        public void onSuccess(String text1) {
                        //            if (StringUtils.isNotEmpty(text1)) {
                        //                L.i("============onSuccess===========" + text1);
                        //            }
                        //        }
                        //    });
                        //    request2.sendAsync();
                        //}
                    }

                    @Override
                    protected void onFail(final Exception e) {
                        super.onFail(e);
                        L.i("============onFail==========="+e.getMessage());
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadingDialog.dismiss();
                                ViewUtils.toast("监管失败" + e.getMessage());
                            }
                        });
                    }
                });
                request.sendAsync();
            }
        });
        if (loadingDialog == null) {
            //LoadingDialog.Builder loadBuilder = new LoadingDialog.Builder(context)
            //        .setMessage("监管信息准备中...")
            //        .setCancelable(true)//返回键是否可点击
            //        .setCancelOutside(false);//窗体外是否可点击
            //loadingDialog = loadBuilder.create();

             loadingDialog=new LoadingDialog(VideoTalkActivity.this);
        }

        exitAlertDialog = ViewUtils.showAlertDialog(VideoTalkActivity.this, "提示", "是否退出视频通话？", "确定", "取消", new DialogInterface.OnClickListener() {//添加"Yes"按钮
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finishAndJumpRecord();
            }
        }, new DialogInterface.OnClickListener() {//添加取消
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

    }
    boolean isFirstLoad=true;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(isFirstLoad&&hasFocus){
            isFirstLoad=false;

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ScreenShotUtils.REQUEST_CODE) {
            screenShotUtils.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void initPlay() {

        //nodePlayerView.setVideoSize(480,360);
        //设置渲染器类型
        nodePlayerView.setRenderType(NodePlayerView.RenderType.SURFACEVIEW);
        //设置视频画面缩放模式
        nodePlayerView.setUIViewContentMode(NodePlayerView.UIViewContentMode.ScaleToFill);
        nodePlayer = new NodePlayer(VideoTalkActivity.this, "M2FmZTEzMGUwMC00ZTRkNTMyMS1jbi5ub2RlbWVkaWEucWxpdmU=-OTv6MJuhXZKNyWWMkdKJWsVKmLHwWPcPfnRbbWGIIf+8t39TqL/mW2f5O5WdT/W8JJE7ePvkvKaS371xVckAZ/U00dSwPp8ShB8Yic2W1GhwCyq04DYETsrGnkOWrhARH7nzNhd3Eq6sVC1Fr74GCEUHbDSCZnCfhcEnzGU9InRiQJ2PImtHORahN3blAGlHb6LZmdnobw5odvKEeUhbkhxYf8S1Fv4VRnSpDCSS3LZ2U3Mp6MfGDA1ZXPadmgdwaJitIrnWA2zP/yqmlUHjMtTv8PzGcc73Tm5k5q+OMbKCJsPq8KSEpFthncvaGZJ2kS2GHx6V5TqYZglBrTx61g==");
        //nodePlayer = new NodePlayer(VideoTalkActivityNew.this);
        int bufferTime = 300;
        int maxBufferTime = 1000;
        /**
         * 设置启动缓冲区时长,单位毫秒.此参数关系视频流连接成功开始获取数据后缓冲区存在多少毫秒后开始播放
         */
        nodePlayer.setBufferTime(bufferTime);
        nodePlayer.setAutoReconnectWaitTimeout(1000);
        /**
         * 设置最大缓冲区时长,单位毫秒.此参数关系视频最大缓冲时长.
         * RTMP基于TCP协议不丢包,网络抖动且缓冲区播完,之后仍然会接受到抖动期的过期数据包.
         * 设置改参数,sdk内部会自动清理超出部分的数据包以保证不会存在累计延迟,始终与直播时间线保持最大maxBufferTime的延迟
         */
        nodePlayer.setMaxBufferTime(maxBufferTime);
        //设置播放视图
        nodePlayer.setPlayerView(nodePlayerView);
        //设置RTSP流使用的传输协议,支持的模式有:
        nodePlayer.setRtspTransport(NodePlayer.RTSP_TRANSPORT_TCP);
        //设置视频是否开启
        nodePlayer.setVideoEnable(true);
        //L.i("============initPlay===========" + rtmpOtherUrl);
        //nodePlayer.setConnectWaitTimeout(30);
        nodePlayer.setNodePlayerDelegate(new NodePlayerDelegate() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onEventCallback(NodePlayer player, final int event, String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (event) {
                            case 1000:
                                // 正在连接视频
                                //ViewUtils.toast("正在连接视频" + rtmpOtherUrl);
                                //ToastUtils.show(mContext, mContext.getString(R.string.toast_1000));
                                break;
                            case 1001:
                                //ViewUtils.toast("视频连接成功");
                                // 视频连接成功
                                //ToastUtils.show(mContext, mContext.getString(R.string.toast_1001));
                                //isStarting = true;
                                //break;
                            case 1002:
                                nodePlayer.start();
                                //ViewUtils.toast("视频连接失败 流地址不存在，或者本地网络无法和服务端通信，回调这里。5秒后重连， 可停止");
                                // 视频连接失败 流地址不存在，或者本地网络无法和服务端通信，回调这里。5秒后重连， 可停止
                                //ToastUtils.show(mContext, mContext.getString(R.string.toast_1002));
                                break;
                            case 1003:
                                //ViewUtils.toast("视频开始重连,自动重连总开关");
                                nodePlayer.start();
                                // 视频开始重连,自动重连总开关
                                //ToastUtils.show(mContext, mContext.getString(R.string.toast_1003));
                                break;
                            case 1004:
                                //ViewUtils.toast("视频播放结束");
                                // 视频播放结束
                                // ToastUtils.show(mContext, mContext.getString(R.string.toast_1004));
                                //   isStarting = false;
                                break;
                            case 1005:
                                nodePlayer.start();
                                //ViewUtils.toast("网络异常,播放中断,播放中途网络异常，回调这里。1秒后重连，如不需要，可停止");
                                // 网络异常,播放中断,播放中途网络异常，回调这里。1秒后重连，如不需要，可停止
                                //ToastUtils.show(mContext, mContext.getString(R.string.toast_1005));
                                break;
                            default:
                                nodePlayer.start();
                        }
                    }
                });

            }
        });

    }

    private void initPublish() {

        //mPublishBtn.setOnClickListener(this);
        //mCameraSwitchBtn.setOnClickListener(this);
        //mEncoderBtn.setOnClickListener(this);
        ////在上层显示
        //glsurfaceview_camera.setZOrderOnTop(true);
        //glsurfaceview_camera.getHolder().setFormat(PixelFormat.TRANSPARENT);
        //mPublisher = new SrsPublisher(glsurfaceview_camera);
        ////编码状态回调
        //mPublisher.setEncodeHandler(new SrsEncodeHandler(this));
        //mPublisher.setRecordHandler(new SrsRecordHandler(this));
        ////rtmp推流状态回调
        //mPublisher.setRtmpHandler(new RtmpHandler(this));
        ////预览分辨率
        //mPublisher.setPreviewResolution(1280, 720);
        ////推流分辨率
        //mPublisher.setOutputResolution(720, 1280);
        ////传输率
        //mPublisher.setVideoHDMode();
        ////开启美颜（其他滤镜效果在MagicFilterType中查看）
        //mPublisher.switchCameraFilter(MagicFilterType.BEAUTY);
        ////打开摄像头，开始预览（未推流）
        //mPublisher.startCamera();
        //if (StringUtils.isNotEmpty(rtmpUrl)) {
        //    mPublisher.startPublish(rtmpUrl);
        //
        //}


        // 得到我们的存储Preferences值的对象，然后对其进行相应操作
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int cameraPostion = getPreferenceValue("camera_postion", "1");
        boolean camreaFrontMirror = getPreferenceValue("camera_front_mirror", true);
        boolean videoFrontMirror = getPreferenceValue("video_front_mirror", false);
        int videoResolution = getPreferenceValue("video_resolution", "1");
        int videoProfile = getPreferenceValue("video_profile", "0");
        int videoKeyframeInterval = getPreferenceValue("video_keyframe_interval", "1");
        int videoBitrate = getPreferenceValue("video_bitrate", "500000");
        int videoFps = getPreferenceValue("video_fps", "15");
        int audioProfile = getPreferenceValue("audio_profile", "1");
        int audioBitrate = getPreferenceValue("audio_bitrate", "32000");
        int audioSamplerate = getPreferenceValue("audio_samplerate", "44100");
        boolean audioDenoise = getPreferenceValue("audio_denoise", true);
        boolean autoHardwareAcceleration = getPreferenceValue("auto_hardware_acceleration", true);
        int smoothSkinLevel = getPreferenceValue("smooth_skin_level", "0");
        String pushCryptoKey = sp.getString("push_cryptokey", "");
        nodePublisher = new NodePublisher(VideoTalkActivity.this, "M2FmZTEzMGUwMC00ZTRkNTMyMS1jbi5ub2RlbWVkaWEucWxpdmU=-OTv6MJuhXZKNyWWMkdKJWsVKmLHwWPcPfnRbbWGIIf+8t39TqL/mW2f5O5WdT/W8JJE7ePvkvKaS371xVckAZ/U00dSwPp8ShB8Yic2W1GhwCyq04DYETsrGnkOWrhARH7nzNhd3Eq6sVC1Fr74GCEUHbDSCZnCfhcEnzGU9InRiQJ2PImtHORahN3blAGlHb6LZmdnobw5odvKEeUhbkhxYf8S1Fv4VRnSpDCSS3LZ2U3Mp6MfGDA1ZXPadmgdwaJitIrnWA2zP/yqmlUHjMtTv8PzGcc73Tm5k5q+OMbKCJsPq8KSEpFthncvaGZJ2kS2GHx6V5TqYZglBrTx61g==");
        //nodePlayer = new NodePlayer(VideoTalkActivityNew.this);
        nodePublisher.setNodePublisherDelegate(new NodePublisherDelegate() {
            @Override
            public void onEventCallback(NodePublisher streamer, final int event, String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (event) {
                            case 2000:
                                //ViewUtils.toast("正在连接服务器");
                                break;
                            case 2001:
                                //ViewUtils.toast("服务器连接成功，开始推流");
                                isPublisherReady = true;
                                if (isAnswerCall) {
                                    sendMyRtmp();

                                }
                                //nodeCameraView.getCamera().setDisplayOrientation(90);
                                break;
                            case 2002:
                                nodePublisher.setOutputUrl(rtmpUrl);
                                nodePublisher.start();
                                ViewUtils.toast("服务器连接失败，请重试");
                                break;
                            case 2004:
                                nodePublisher.setOutputUrl(rtmpUrl);
                                //nodePublisher.start();
                                //ViewUtils.toast("推流结束");
                                break;
                            case 2005:
                                nodePublisher.setOutputUrl(rtmpUrl);
                                nodePublisher.start();
                                //ViewUtils.toast("推流中途网络异常");
                                break;
                            default:
                                //nodePublisher.setOutputUrl(rtmpUrl);
                                //nodePublisher.start();
                        }
                    }
                });
            }
        });
        nodePublisher.setOutputUrl(rtmpUrl);

        nodePublisher.setCameraPreview(nodeCameraView, cameraPostion, camreaFrontMirror);
        nodePublisher.setVideoParam(1, videoFps, videoBitrate, videoProfile, videoFrontMirror);
        nodePublisher.setKeyFrameInterval(videoKeyframeInterval);
        nodePublisher.setAudioParam(audioBitrate, audioProfile, audioSamplerate);
        nodePublisher.setDenoiseEnable(audioDenoise);
        nodePublisher.setHwEnable(autoHardwareAcceleration);
        nodePublisher.setBeautyLevel(smoothSkinLevel);
        nodePublisher.setCryptoKey(pushCryptoKey);
        nodePublisher.startPreview();
        if (StringUtils.isNotEmpty(rtmpUrl)) {
            nodePublisher.start();
            nodeCameraView.getGLSurfaceView().setZOrderOnTop(true);
        }
    }

    private void finishAndJumpRecord() {
        //if (hasRecord) {
        //    Intent intent = new Intent(VideoTalkActivityNew.this, RecordAct.class);
        //    LinkedList<Row> rows = new LinkedList<>();
        //    for (int i = 0; i < imagesList.size(); i++) {
        //        String s = imagesList.get(i);
        //        Row row = new Row();
        //        row.put("URL", s);
        //        row.put("path", s);
        //        rows.add(row);
        //    }
        //    IntentUtils.addRows(intent, rows, RecordAct.IMG_LIST);
        //    intent.putExtra("MAINID", MAINID);
        //    intent.putExtra("FILEID", FILEID);
        //    startActivity(intent);
        //
        //}
        finish();
    }


    private int getPreferenceValue(String key, String defValue) {
        String value = sp.getString(key, defValue);
        return Integer.parseInt(value);
    }

    private boolean getPreferenceValue(String key, boolean defValue) {
        return sp.getBoolean(key, defValue);
    }

    /**
     * 打开assets下的音乐mp3文件
     */
    private void openAssetMusics(String path) {
        if (mediaPlayer == null) {
            //打开Asset目录
            mediaPlayer = new MediaPlayer();
            try {
                //打开音乐文件shot.mp3
                AssetFileDescriptor assetFileDescriptor = getAssets().openFd(path);
                //设置媒体播放器的数据资源
                mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.start();
                    }
                });
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            exitAlertDialog.show();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void switchToVoice() {
        nodePublisher.setVideoEnable(false);

        //mPublisher.stopCamera();
        nodeCameraView.setVisibility(View.GONE);
        ln_switch_camera.setVisibility(View.INVISIBLE);
        tv_change_vioce.setText("转到视频");
    }


    private void switchToVideo() {
        //mPublisher.startCamera();
        nodePublisher.setVideoEnable(true);
        nodeCameraView.getGLSurfaceView().setZOrderOnTop(true);
        nodeCameraView.setVisibility(View.VISIBLE);
        ln_switch_camera.setVisibility(View.VISIBLE);
        tv_change_vioce.setText("转到语音");
    }

    private void callend() {
        //XMLProtocol.Talk one = new XMLProtocol.Talk();
        //one.we = GlobalConst.NAME;
        //one.talkType = talkType;
        //one.talkOther = otherName;
        //one.strTimeStamp = DataTimeUtil.getTimeStamp();
        //one.talkMsg = "call_end";
        //m_sendInterface.sendTalk(one);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();//开始/停止推流
        if (id == R.id.publish) {
            if (mPublishBtn.getText().toString().contentEquals("开始")) {
                //rtmpUrl = mRempUrlEt.getText().toString();
                if (TextUtils.isEmpty(rtmpUrl)) {
                    Toast.makeText(getApplicationContext(), "地址不能为空！", Toast.LENGTH_SHORT).show();
                }
                //mPublisher.startPublish(rtmpUrl);
                //mPublisher.startCamera();
                nodePublisher.start();
                if (mEncoderBtn.getText().toString().contentEquals("软编码")) {
                    Toast.makeText(getApplicationContext(), "当前使用硬编码", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "当前使用软编码", Toast.LENGTH_SHORT).show();
                }
                mPublishBtn.setText("停止");
                nodePlayer.start();
                mEncoderBtn.setEnabled(false);
            } else if (mPublishBtn.getText().toString().contentEquals("停止")) {
                //mPublisher.stopPublish();
                //mPublisher.stopRecord();
                nodePublisher.stop();
                nodePlayer.stop();
                mPublishBtn.setText("开始");
                mEncoderBtn.setEnabled(true);
            }
            //切换摄像头
        } else if (id == R.id.swCam) {
            nodePublisher.switchCamera();
            //mPublisher.switchCameraFace((mPublisher.getCamraId() + 1) % Camera.getNumberOfCameras());
            //切换编码方式
        } else if (id == R.id.swEnc) {
            if (mEncoderBtn.getText().toString().contentEquals("软编码")) {
                //mPublisher.switchToSoftEncoder();
                mEncoderBtn.setText("硬编码");
            } else if (mEncoderBtn.getText().toString().contentEquals("硬编码")) {
                //mPublisher.switchToHardEncoder();
                mEncoderBtn.setText("软编码");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mPublisher.resumeRecord();
        //nodePublisher.re
    }

    @Override
    protected void onPause() {
        super.onPause();
        //mPublisher.pauseRecord();
    }

    @Override
    protected void onDestroy() {
        callend();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        nodePublisher.stopPreview();
        nodePublisher.stop();
        nodePublisher.release();
        //mPublisher.stopPublish();
        //mPublisher.stopRecord();
        nodePlayer.release();
        nodePlayer.stop();
        super.onDestroy();

    }


    private void sendMyRtmp() {
        L.i("============sendMyRtmp===========");
   /*     new Thread(new Runnable() {
            @Override
            public void run() {*/
                //XMLProtocol.Talk one = new XMLProtocol.Talk();
                //one.we = GlobalConst.NAME;
                //one.talkType = XMLProtocol.Talk.TalkType.VIDEO;
                //one.talkOther = otherName;
                //one.talkMsg = buildJsonMsg();
                ////one.talkMsg = callOrAnswer + rtmpUrl;
                //one.strTimeStamp = DataTimeUtil.getTimeStamp();
                //m_sendInterface.sendTalk(one);

                HttpRequest httpRequest=ImGlobal.createRequest("/actions/imTalkAction/talk");
                httpRequest.addParam("msg",rtmpUrl);
                httpRequest.addParam("talkType","4");
                //httpRequest.addParam("talkto_id",otherName);
                Row row=new Row();
                row.put("name",otherName);
                row.put("mainId",rtmpOtherID);
                httpRequest.addParam("talkTo",row.toJsonString());
                httpRequest.setCallback(new StringCallBack() {
                    @Override
                    public void onSuccess(String s) {
                        L.i("============onSuccess==========="+s);
                    }
                });
                httpRequest.sendAsync();

       /*     }
        }).start();*/

    }

    private String buildJsonMsg() {
        Row row = new Row();
        row.put("callOrAnswer", callOrAnswer);
        row.put("rtmpurl", rtmpUrl);
        row.put("MAINID", ImGlobal.USER_ID);
        row.put("NAME", ImGlobal.NAME);
        return row.toJsonString().replaceAll("\n","");
    }

    //@Override
    //public void onRtmpVideoStreaming() {
    //
    //}
    //
    //@Override
    //public void onRtmpAudioStreaming() {
    //
    //}

    //@Override
    //public void onRtmpStopped() {
    //    Toast.makeText(getApplicationContext(), "已停止", Toast.LENGTH_SHORT).show();
    //}
    //
    //@Override
    //public void onRtmpDisconnected() {
    //    Toast.makeText(getApplicationContext(), "已断开连接", Toast.LENGTH_SHORT).show();
    //}
    //
    //@Override
    //public void onRtmpVideoFpsChanged(double fps) {
    //
    //}
    //
    //@Override
    //public void onRtmpVideoBitrateChanged(double bitrate) {
    //
    //}
    //
    //@Override
    //public void onRtmpAudioBitrateChanged(double bitrate) {
    //
    //}
    //
    //@Override
    //public void onRtmpSocketException(SocketException e) {
    //    handleException(e);
    //}
    //
    //@Override
    //public void onRtmpIOException(IOException e) {
    //    handleException(e);
    //}
    //
    //@Override
    //public void onRtmpIllegalArgumentException(IllegalArgumentException e) {
    //    handleException(e);
    //}
    //
    //@Override
    //public void onRtmpIllegalStateException(IllegalStateException e) {
    //    handleException(e);
    //}

    //@Override
    //public void onRecordPause() {
    //    Toast.makeText(getApplicationContext(), "Record paused", Toast.LENGTH_SHORT).show();
    //}
    //
    //@Override
    //public void onRecordResume() {
    //    Toast.makeText(getApplicationContext(), "Record resumed", Toast.LENGTH_SHORT).show();
    //}
    //
    //@Override
    //public void onRecordStarted(String msg) {
    //    Toast.makeText(getApplicationContext(), "Recording file: " + msg, Toast.LENGTH_SHORT).show();
    //}
    //
    //@Override
    //public void onRecordFinished(String msg) {
    //    Toast.makeText(getApplicationContext(), "MP4 file saved: " + msg, Toast.LENGTH_SHORT).show();
    //}
    //
    //@Override
    //public void onRecordIOException(IOException e) {
    //    handleException(e);
    //}
    //
    //@Override
    //public void onRecordIllegalArgumentException(IllegalArgumentException e) {
    //    handleException(e);
    //}

}
