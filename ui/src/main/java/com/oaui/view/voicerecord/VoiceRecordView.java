package com.oaui.view.voicerecord;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.oaui.R;
import com.oaui.annotation.InjectReader;
import com.oaui.annotation.ViewInject;
import com.oaui.data.RowObject;
import com.oaui.utils.ViewUtils;


/**
 * @Created by com.gzpykj.com
 * @author zms
 * @Date 2016-3-18
 * @Descrition 录音模块，录完像后返回录像文件地址和缩略图地址
 */
public class VoiceRecordView extends LinearLayout {

	private Context context;

	private View voiceRecordView;

	@ViewInject
	private Button start;


	@ViewInject
	private SecondsView secondsView;
	@ViewInject
	private ProgressBar pg_voice;

	private VoiceRecord voiceRecord;


	public FileModel fileModel;

	@ViewInject
	ImageView img_mai;

	/**
	 * 录音完成回调
	 */
	public OnRecordComplateListener onRecordComplateListener;

	public VoiceRecordView(Context context, AttributeSet attr) {
		super(context, attr);
		this.context = context;
		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public VoiceRecordView(Context context) {
		super(context);
		this.context = context;
		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		initVoiceRcdCfg();
		if (voiceRecord == null) {
			voiceRecord = new VoiceRecord(fileModel);
			voiceRecord.setAudioStatusUpdateListener(new VoiceRecord.OnAudioStatusUpdateListener() {
				@Override
				public void onUpdate(double db) {
					img_mai.getDrawable().setLevel((int) (3000 + 6000 * db / 100));
				}
			});
		}
		voiceRecordView = ViewUtils.inflatView(context,
				R.layout.ui_atm_voice_record_view);
		voiceRecordView.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		this.addView(voiceRecordView);
		this.setOrientation(LinearLayout.VERTICAL);
		this.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));

		InjectReader.injectAllFields(this, voiceRecordView);
		start.setOnClickListener(new mClick());

	}


	/**
	 * 录音文件输出配置
	 */
	private void initVoiceRcdCfg() {
		fileModel = new FileModel();
		fileModel.setNextDir("/voice");
		fileModel.setPrefix("voice_");
		fileModel.setSuffix(".amr");

	}



	class mClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (v == start) {
				String status=start.getText()+"";
				if(status.equals("录音")){
					start.setText("停止");
					secondsView.reset();
					secondsView.start();
					voiceRecord.start();
					Animation loadAnimation = AnimationUtils.loadAnimation(context,
							R.anim.anm_rotate);
					LinearInterpolator lir = new LinearInterpolator();
					loadAnimation.setInterpolator(lir);
					pg_voice.startAnimation(loadAnimation);
					pg_voice.setVisibility(View.VISIBLE);
				}else{
					start.setText("录音");
					pg_voice.setVisibility(View.GONE);
					voiceRecord.stop();
					secondsView.pluse();
					pg_voice.clearAnimation();
					onRecordComplate();

				}
			
			} 
		}

	}
	

	private void onRecordComplate() {
		if(onRecordComplateListener!=null){
			RowObject result = new RowObject();
			result.put("path", voiceRecord.getVociePath());
			result.put("type", "voice");
			onRecordComplateListener.onComplate(result);
		}
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public View getVoiceRecordView() {
		return voiceRecordView;
	}

	public void setVoiceRecordView(View voiceRecordView) {
		this.voiceRecordView = voiceRecordView;
	}

	public Button getStart() {
		return start;
	}

	public void setStart(Button start) {
		this.start = start;
	}

	public SecondsView getSecondsView() {
		return secondsView;
	}

	public void setSecondsView(SecondsView secondsView) {
		this.secondsView = secondsView;
	}

	public ProgressBar getPg_voice() {
		return pg_voice;
	}

	public void setPg_voice(ProgressBar pg_voice) {
		this.pg_voice = pg_voice;
	}

	public VoiceRecord getVoiceRecord() {
		return voiceRecord;
	}

	public void setVoiceRecord(VoiceRecord voiceRecord) {
		this.voiceRecord = voiceRecord;
	}


	public FileModel getFileModel() {
		return fileModel;
	}

	public void setFileModel(FileModel fileModel) {
		this.fileModel = fileModel;
	}

	public interface OnRecordComplateListener {
		void onComplate(RowObject result);
	}

	public OnRecordComplateListener getOnRecordComplateListener() {
		return onRecordComplateListener;
	}

	public void setOnRecordComplateListener(
			OnRecordComplateListener onRecordComplateListener) {
		this.onRecordComplateListener = onRecordComplateListener;
	}
	

}
