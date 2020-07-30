package cn.oaui.view.signature;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import cn.oaui.IntentFactory;
import cn.oaui.R;
import cn.oaui.annotation.InjectReader;
import cn.oaui.annotation.ViewInject;
import cn.oaui.data.Row;
import cn.oaui.utils.BitmapUtils;
import cn.oaui.view.CustomLayout;
import cn.oaui.view.HeaderView;
import cn.oaui.view.TempFragment;
import cn.oaui.view.colorpick.ColorPickerLayout;
import cn.oaui.view.dialog.FrameDialog;

/**
 * @Created by com.gzpykj.com
 * @author zms
 * @Date 2016年4月18日
 * @Descrition 签名页面
 */
public class SignatureView extends CustomLayout {
	
	private Context context;
	@ViewInject
	private LinearLayout ln_color,ln_bold,ln_pic,ln_clear,ln_sure;
	@ViewInject
	private HuaBanView huaBan;
	@ViewInject
	HeaderView headerView;

	OnActionListener onSureListener;

	FrameDialog dlgColorPicker,dlgBoldPick;

	TempFragment tempFragment;

	public SignatureView(Context context) {
		super(context);
		this.context=context;
	}
	
	public SignatureView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;

	}

	@Override
	public void initData() {

	}

	@Override
	protected void onCreateView() {
		initView();
	}

	@Override
	public int setXmlLayout() {
		return R.layout.ui_view_signature;
	}


	private void initView() {
		//注解查找View
		setBackgroundColor(Color.parseColor("#ffffff"));
		InjectReader.injectAllFields(this);
		ln_color.setOnClickListener(new mClick());
		ln_bold.setOnClickListener(new mClick());
		ln_pic.setOnClickListener(new mClick());
		ln_clear.setOnClickListener(new mClick());
		ln_sure.setOnClickListener(new mClick());
		ColorPickerLayout colorPickerLayout = new ColorPickerLayout(getContext());
		dlgColorPicker =new FrameDialog(getContext(),colorPickerLayout);
		colorPickerLayout.setOnColorCheckedListener(new ColorPickerLayout.OnColorCheckedListener() {
			@Override
			public void onChecked(int color) {
				dlgColorPicker.dismiss();
				huaBan.setColor(color);
			}
		});
		SigntureBoldPickerView signtureBoldPickerView = new SigntureBoldPickerView(getContext());
		signtureBoldPickerView.setBold(huaBan.getPaintWidth()+"");
		signtureBoldPickerView.setOnBoldPickListener(new SigntureBoldPickerView.OnBoldPickListener() {
			@Override
			public void onPick(float progress) {
				dlgBoldPick.dismiss();
				huaBan.setPaintWidth(progress);
			}
		});
		dlgBoldPick=new FrameDialog(signtureBoldPickerView);
		headerView.getRightBtn().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(onSureListener!=null){
					onSureListener.onSure(huaBan.getCacheBitmap());
				}
			}
		});
		headerView.getLeftBtn().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(onSureListener!=null){
					onSureListener.onCancle();
				}
			}
		});
		tempFragment=TempFragment.addHideFragment((Activity) getContext());
		tempFragment.setOnActivityResultListener(new TempFragment.OnActivityResultListener() {
			@Override
			public void onActivityResult(int requestCode, int resultCode, Intent data) {
				Row row = IntentFactory.onActivityResult(requestCode, resultCode, data);
				if(row !=null){
					String path = row.getString("path");
					Bitmap bitmapByPath = BitmapUtils.getBitmapByPath(path);
					if(bitmapByPath!=null){
						huaBan.loadBitmap(bitmapByPath);
					}
				}
			}
		});
	}
	
	class mClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			if(v==ln_pic){
				IntentFactory.openFileExplore(tempFragment,"image/*");
			}else if(v==ln_clear){
				clear();
			}else if(v==ln_color){
				dlgColorPicker.show();
			}else if(v==ln_bold){
				dlgBoldPick.show();
			}else if(v==ln_sure){
				if(onSureListener!=null){
					onSureListener.onSure(huaBan.getCacheBitmap());
				}
			}
		}
	}

	public void clear(){
		huaBan.clearScreen();
	}


	public interface OnActionListener {
		void onSure(Bitmap bitmap);
		void onCancle();
	}

	public OnActionListener getOnSureListener() {
		return onSureListener;
	}

	public void setOnSureListener(OnActionListener onSureListener) {
		this.onSureListener = onSureListener;
	}


	public HuaBanView getHuaBan() {
		return huaBan;
	}

	public void setHuaBan(HuaBanView huaBan) {
		this.huaBan = huaBan;
	}

	public HeaderView getHeaderView() {
		return headerView;
	}

	public void setHeaderView(HeaderView headerView) {
		this.headerView = headerView;
	}
}
