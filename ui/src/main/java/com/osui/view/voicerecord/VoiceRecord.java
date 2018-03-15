package com.osui.view.voicerecord;

import android.media.MediaRecorder;
import android.os.Handler;

import com.osui.utils.ViewUtils;

import java.io.IOException;

import static android.os.Build.VERSION_CODES.BASE;

/**
 * @Created by gzpykj.com
 * @author zms
 * @Date 2016-6-6
 * @Descrition 使用MediaRecorder的录音器,必须传入录音文件的输出路径
 * 
 */
public class VoiceRecord {

	private MediaRecorder mMediaRecorder;

	// 是否正在录音
	private boolean isRecording = false;

	// 是否正在录音
	private boolean isInitializ = false;


	private FileModel fileModel;

	private OnAudioStatusUpdateListener audioStatusUpdateListener;
	private int SPACE = 100;// 间隔取样时间
	private final Handler mHandler = new Handler();



	public VoiceRecord(FileModel fileModel) {
		this.fileModel = fileModel;
	}

	/**
	 * 初始化录音组件
	 */
	public void initRecord() {
		try {
			/* ①Initial：实例化MediaRecorder对象 */
			mMediaRecorder = new MediaRecorder();
			mMediaRecorder.setOnErrorListener(null);// 防止start()与stop()间隔小于1秒(有时候大于1秒也崩)时崩溃
			/* ②setAudioSource/setVedioSource */
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);// 设置麦克风
			/*
			 * ②设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default
			 * THREE_GPP(3gp格式，H263视频
			 * /ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
			 */
			mMediaRecorder
					.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
			/* ②设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default */
			mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			/* ②设置输出文件的路径 */
			mMediaRecorder.setOutputFile(fileModel.getPath());
			/* ③准备 */
			mMediaRecorder.prepare();
			isInitializ=true;
		} catch (IOException e) {
			e.printStackTrace();
			isInitializ=false;
			ViewUtils.toast("初始化出错 !");
			//Logger.e("初始化出错 !");
		}
	}

	/**
	 * 开始录音
	 */
	public void start() {
		fileModel.updateName();
		fileModel.createFile();
		if (isRecording) {
			ViewUtils.toast("正在录音");
		} else {
			initRecord();
			if(isInitializ){
				mMediaRecorder.start();
				updateMicStatus();
				isRecording = true;
			}
			
		}

	}

	/**
	 * 停止录音
	 */
	public void stop() {
		try{
			if (mMediaRecorder != null&&isRecording) {
			/* ⑤停止录音 */
				mMediaRecorder.stop();
				mMediaRecorder.reset();
			/* ⑥释放MediaRecorder */
				mMediaRecorder.release();
				//不置空个别机型会崩溃，比如小米6
				mMediaRecorder=null;
				isRecording = false;
			} else {
				ViewUtils.toast("请先开始录音");
			}
		}catch( Exception e){
			e.printStackTrace();
			ViewUtils.toast("停止录音出错！");
		}

	}

	private void updateMicStatus() {
		if (mMediaRecorder != null) {
			double ratio = (double)mMediaRecorder.getMaxAmplitude() / BASE;
			double db = 0;// 分贝
			if (ratio > 1) {
				db = 20 * Math.log10(ratio);
				if(null != audioStatusUpdateListener) {
					audioStatusUpdateListener.onUpdate(db);
				}
			}
			mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);
		}
	}

	private Runnable mUpdateMicStatusTimer = new Runnable() {
		public void run() {
			updateMicStatus();
		}
	};



	public interface OnAudioStatusUpdateListener {
		public void onUpdate(double db);
	}
	public MediaRecorder getmMediaRecorder() {
		return mMediaRecorder;
	}

	public void setmMediaRecorder(MediaRecorder mMediaRecorder) {
		this.mMediaRecorder = mMediaRecorder;
	}

	public String getVociePath() {
		return fileModel.getPath();
	}

	public boolean isRecording() {
		return isRecording;
	}

	public void setRecording(boolean isRecording) {
		this.isRecording = isRecording;
	}

	public OnAudioStatusUpdateListener getAudioStatusUpdateListener() {
		return audioStatusUpdateListener;
	}

	public void setAudioStatusUpdateListener(OnAudioStatusUpdateListener audioStatusUpdateListener) {
		this.audioStatusUpdateListener = audioStatusUpdateListener;
	}
}
