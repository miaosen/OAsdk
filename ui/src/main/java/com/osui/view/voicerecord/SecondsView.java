package com.osui.view.voicerecord;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.osui.R;


/**
 * @Created by gzpykj.com
 * @author zms
 * @Date 2015-7-20
 * @Descrition 秒表
 */

public class SecondsView extends LinearLayout {

	private Context context;
	private LinearLayout lnSeconds;
	private TextView tvMinite;
	private TextView tvSeconds;
	private LinearLayout lnControl;
	private  int timeCount;
	private Button btnStart;
	private Button btnPaluse;
	private Button btnReset;
	private static boolean isStart = false;
	private static boolean isSimple = true;// 是否显示button界面

	private AttributeSet attrs;

	private int textSize = 20;// 字体大小

	private int textColor = getResources().getColor(R.color.text_color);// 字体大小

	public SecondsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.attrs = attrs;
		this.context = context;
		init();
		this.setOrientation(LinearLayout.VERTICAL);
	
	}
	
	@SuppressLint("NewApi")
	public SecondsView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.attrs = attrs;
		this.context = context;
		init();
	}

	public SecondsView(Context context) {
		super(context);
		this.context = context;
		init();
		this.setOrientation(LinearLayout.VERTICAL);
		
	}

	/**
	 * 初始化
	 */
	private void init() {
		//parseAttributes();
		setSecondsView();
		if (!isSimple) {
			setControlView();
		}
	}

	/**
	 * 解析从XML传递给视图的属性
	 */
	//private void parseAttributes() {
	//	TypedArray typeArray = context.obtainStyledAttributes(attrs,
	//			R.styleable.SecondsView);
	//	textColor = typeArray.getColor(R.styleable.SecondsView_ttextColor,
	//			textColor);
	//	typeArray.recycle();
	//}

	/**
	 * 秒表界面
	 */
	private void setSecondsView() {
		lnSeconds = new LinearLayout(context);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lnSeconds.setGravity(Gravity.CENTER);
		lnSeconds.setLayoutParams(lp);
		lnSeconds.setPadding(5, 3, 5, 3);
		LinearLayout ln1 = new LinearLayout(context);
		ln1.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		// ln1.setBackgroundColor(Color.parseColor("#000000"));
		tvMinite = new TextView(context);
		tvMinite.setText("00");
		tvMinite.setTextSize(textSize);
		tvMinite.setTextColor(textColor);
		tvMinite.setGravity(Gravity.CENTER);
		ln1.addView(tvMinite);

		TextView tv = new TextView(context);
		tv.setText(" : ");
		tv.setTextSize(textSize);
		tv.setGravity(Gravity.CENTER);
		tv.setTextColor(textColor);
		ln1.addView(tv);

		tvSeconds = new TextView(context);
		tvSeconds.setText("00");
		tvSeconds.setTextSize(textSize);
		tvSeconds.setTextColor(textColor);
		tvSeconds.setGravity(Gravity.CENTER);
		ln1.addView(tvSeconds);
		lnSeconds.addView(ln1);
		this.addView(lnSeconds);
	}

	/*
	 * 操控界面
	 */
	public void setControlView() {
		lnControl = new LinearLayout(context);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lnControl.setGravity(Gravity.CENTER);
		lnControl.setLayoutParams(lp);
		lnControl.setPadding(5, 3, 5, 3);
		btnStart = new Button(context);
		btnStart.setText("开始");
		btnStart.setOnClickListener(new mClick());
		btnPaluse = new Button(context);
		btnPaluse.setText("暂停");
		btnPaluse.setOnClickListener(new mClick());
		btnReset = new Button(context);
		btnReset.setOnClickListener(new mClick());
		btnReset.setText("重置");
		lnControl.addView(btnPaluse);
		lnControl.addView(btnStart);
		lnControl.addView(btnReset);
		this.addView(lnControl);
	}

	 Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				handler.sendEmptyMessageDelayed(1, 1000);
				updateView();
				break;
			default:
				break;
			}
		}
	};

	private void updateView() {
		timeCount += 1;
		tvMinite.setText(null);// 分钟
		tvSeconds.setText(null);// 秒钟
		int minute = (int) (timeCount / 60) % 60;
		int second = (int) (timeCount % 60);
		
		if (minute < 10) {
			tvMinite.setText("0" + minute);
		} else {
			tvMinite.setText("" + minute);
		}
		if (second < 10) {
			try{
				tvSeconds.setText("0" + second);
			}catch(Exception e){
				e.printStackTrace();
			}
			
		} else {
			tvSeconds.setText("" + second);
		}
		
	};

	public void start() {
		if (!isStart) {
			handler.sendEmptyMessage(1);
			isStart = true;
		}
	}

	public void pluse() {
		handler.removeMessages(1);
		isStart = false;
	}

	public void reset() {
		handler.removeMessages(1);
		isStart = false;
		timeCount = 0;
		tvSeconds.setText("00");
		tvMinite.setText("00");
	}

	class mClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (v == btnStart) {
				start();
			}
			if (v == btnPaluse) {
				pluse();
			}
			if (v == btnReset) {
				reset();
			}
		}
	}

	public static void setSimple(boolean isSimple) {
		SecondsView.isSimple = isSimple;
	}

	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}

}
