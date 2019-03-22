package com.oaui.view.signature;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.oaui.L;
import com.oaui.utils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Created by com.gzpykj.com
 * @Date 2016年4月18日
 * @Descrition 绘画板
 */
public class HuaBanView extends View {

	/**
	 * 画布
	 */
	private Canvas canvas;
	//缓存画布，用于生成cacheBitmap
	private Canvas cacheCanvas;

	private int bgColor=Color.WHITE;
	//缓存画布，用于手势抬起后保存之前的轨迹
	private Bitmap cacheBitmap;

	/**
	 * 画笔
	 */
	private Paint paint;
	private Paint bitmapPaint;
	/**
	 * 绘制的线条
	 */
	private Path path;
	
	/**
	 * 绘制的线条
	 */
	private List<Path> paths=new ArrayList<Path>();
	/**
	 * 画布高度
	 */
	private int height;
	/**
	 * 画布宽度
	 */
	private int width;
	/**
	 * 触摸屏幕时的x轴坐标
	 */
	private float pX;
	/**
	 * 触摸屏幕时的y轴坐标
	 */
	private float pY;

	/**
	 * 默认画笔颜色
	 */
	private int paintColor = Color.GREEN;
	/**
	 * 默认画笔样式
	 */
	private static Paint.Style paintStyle = Paint.Style.STROKE;
	/**
	 * 默认画笔大小
	 */
	private float paintWidth = 8;


	public HuaBanView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public HuaBanView(Context context) {
		super(context);

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		height = h;
		width = w;
		init();

	}

	@Override
	protected void onDraw(Canvas canvas) {
		this.canvas = canvas;
		canvas.drawBitmap(cacheBitmap,0,0,new Paint());
		canvas.drawPath(path, paint);
	}

	private void init() {
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		path = new Path();
		bitmapPaint = new Paint();
		cacheBitmap= Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		cacheCanvas=new Canvas(cacheBitmap);
		cacheCanvas.drawColor(bgColor);
		updatePaint();
	}

	/**
	 * 更新画笔
	 */
	private void updatePaint() {
		paint.setStyle(paintStyle);
		paint.setStrokeWidth(paintWidth);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		if (action == MotionEvent.ACTION_DOWN) {
			path.moveTo(event.getX(), event.getY());
			pX = event.getX();
			pY = event.getY();
		} else if (action == MotionEvent.ACTION_MOVE) {
			path.quadTo(pX, pY, event.getX(), event.getY());
			pX = event.getX();
			pY = event.getY();
		} else if (action == MotionEvent.ACTION_UP) {
			paths.add(path);
			cacheCanvas.drawPath(path,paint);
			path.reset();
		}
		invalidate();
		super.onTouchEvent(event);
		return true;
	}

	public void loadBitmap(Bitmap bitmapByPath) {
		Bitmap bitmap = BitmapUtils.zoomBitmap(bitmapByPath, width, height);
		cacheCanvas.drawBitmap(bitmap,0,0,new Paint());
	}
	

	/**
	 * 设置画笔颜色
	 * 
	 * @param color
	 */
	public void setColor(int color) {
		paint.setColor(color);
	}

	/**
	 * 设置画笔粗细
	 * 
	 * @param width
	 */
	public void setPaintWidth(float width) {
		paint.setStrokeWidth(width);
		paintWidth=width;
	}
	
	/**
	 *返回上一步
	 * 
	 */
	public void setMoveToLast() {
		path.reset();
		path=paths.get(paths.size()-1);
		L.i("path====="+paths.size());
		invalidate();
	}


	public static final int PEN = 1;
	public static final int PAIL = 2;
	/**
	 * 画笔样式
	 * 
	 * @param style
	 */
	public void setStyle(int style) {
		switch (style) {
		case PEN:// 钢笔
			paintStyle = Paint.Style.STROKE;
			break;
		case PAIL:// 水桶
			paintStyle = Paint.Style.FILL;
			break;
		}
		updatePaint();
	}

	/**
	 * 清空签名
	 */
	public void clearScreen() {
		path.reset();
		//Paint paint = new Paint();
		//paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		cacheCanvas.drawColor(bgColor);
		invalidate();
	}

	public float getPaintWidth() {
		return paintWidth;
	}

	public Bitmap getCacheBitmap() {
		return cacheBitmap;
	}

	public void setCacheBitmap(Bitmap cacheBitmap) {
		this.cacheBitmap = cacheBitmap;
	}


}
