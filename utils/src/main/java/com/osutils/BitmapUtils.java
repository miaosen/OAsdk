package com.osutils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 图片压缩工具类
 * 
 * @author touch_ping 2015-1-5 下午1:29:59
 */
public class BitmapUtils {

	/**
	 * http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
	 * 官网：获取压缩后的图片
	 * 
	 * @param res
	 * @param resId
	 * @param reqWidth
	 *            所需图片压缩尺寸最小宽度
	 * @param reqHeight
	 *            所需图片压缩尺寸最小高度
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromResource(Resources res,
														 int resId, int reqWidth, int reqHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	/**
	 * @param reqWidth
	 *            所需图片压缩尺寸最小宽度
	 * @param reqHeight
	 *            所需图片压缩尺寸最小高度
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromFile(String filepath,
													 int reqWidth, int reqHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		BitmapFactory.decodeFile(filepath, options);
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filepath, options);
	}

	public static Bitmap decodeSampledBitmapFromBitmap(Bitmap bitmap,
													   int reqWidth, int reqHeight) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 90, baos);
		byte[] data = baos.toByteArray();

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, 0, data.length, options);
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeByteArray(data, 0, data.length, options);
	}

	/**
	 * 计算压缩比例值(改进版 by touch_ping)
	 * 
	 * 原版2>4>8...倍压缩 当前2>3>4...倍压缩
	 * 
	 * @param options
	 *            解析图片的配置信息
	 * @param reqWidth
	 *            所需图片压缩尺寸最小宽度O
	 * @param reqHeight
	 *            所需图片压缩尺寸最小高度
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {

		final int picheight = options.outHeight;
		final int picwidth = options.outWidth;
		Log.i("test", "原尺寸: " + picwidth + "*" + picheight);

		int targetheight = picheight;
		int targetwidth = picwidth;
		int inSampleSize = 1;

		if (targetheight > reqHeight || targetwidth > reqWidth) {
			while (targetheight >= reqHeight && targetwidth >= reqWidth) {
				Log.i("test", "压缩: " + inSampleSize + "倍");
				inSampleSize += 1;
				targetheight = picheight / inSampleSize;
				targetwidth = picwidth / inSampleSize;
			}
		}

		Log.i("test", "最终压缩比例:" + inSampleSize + " 倍");
		Log.i("test", "新尺寸:" + "  targetwidth " + "*" + targetheight);
		return inSampleSize;
	}

	/**
	 * 根据Bitmap创建缩略图
	 * 
	 * @param cur_bitmap
	 *            需要压缩的Bitmap
	 * @param square_size
	 *            输出图片宽度
	 * @param quality
	 *            输出图片质量
	 * @return
	 * @throws IOException
	 */
	public static Bitmap createImageThumbnail(Bitmap cur_bitmap,
											  int square_size, int quality) throws IOException {
		if (cur_bitmap == null) {
			return null;
		}
		// 原始图片的高宽
		int[] cur_img_size = new int[] { cur_bitmap.getWidth(),
				cur_bitmap.getHeight() };
		// 计算原始图片缩放后的宽高
		int[] new_img_size = scaleImageSize(cur_img_size, square_size);
		// 生成缩放后的bitmap
		Bitmap thb_bitmap = zoomBitmap(cur_bitmap, new_img_size[0],
				new_img_size[1]);
		return thb_bitmap;
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
	 * 保存Bitmap到本地
	 * 
	 * @param file
	 * @param bitmap
	 * @param quality
	 * @throws IOException
	 */
	public static void saveBitmapToSD(File file, Bitmap bitmap, int quality)
			throws IOException {
		if (bitmap != null && file != null) {
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(file.getAbsolutePath()));
			bitmap.compress(CompressFormat.PNG, quality, bos);
			bos.flush();
			bos.close();
		} else {
		}
	}

	/**
	 * 计算缩放图片的宽高
	 * 
	 * @param img_size
	 * @param square_size
	 * @return
	 */
	public static int[] scaleImageSize(int[] img_size, int square_size) {
		if (img_size[0] <= square_size && img_size[1] <= square_size)
			return img_size;
		double ratio = square_size
				/ (double) Math.max(img_size[0], img_size[1]);
		return new int[] { (int) (img_size[0] * ratio),
				(int) (img_size[1] * ratio) };
	}

	/**
	 * 添加文字水印
	 * 
	 * @param photo
	 * @param str
	 */
	public static Bitmap addWaterMmark(Bitmap photo, String str) {
		int width = photo.getWidth();
		int hight = photo.getHeight();
		System.out.println("宽" + width + "高" + hight);
		Bitmap icon = Bitmap
				.createBitmap(width, hight, Bitmap.Config.ARGB_8888); // 建立一个空的BItMap
		Canvas canvas = new Canvas(icon);// 初始化画布绘制的图像到icon上

		Paint photoPaint = new Paint(); // 建立画笔
		photoPaint.setDither(true); // 获取跟清晰的图像采样
		photoPaint.setFilterBitmap(true);// 过滤一些

		Rect src = new Rect(0, 0, photo.getWidth(), photo.getHeight());// 创建一个指定的新矩形的坐标
		Rect dst = new Rect(0, 0, width, hight);// 创建一个指定的新矩形的坐标
		canvas.drawBitmap(photo, src, dst, photoPaint);// 将photo 缩放或则扩大到
														// dst使用的填充区photoPaint

		Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DEV_KERN_TEXT_FLAG);// 设置画笔
		textPaint.setTextSize(AppUtils.dip2px(20));// 字体大小
		textPaint.setTypeface(Typeface.DEFAULT_BOLD);// 采用默认的宽度
		textPaint.setColor(Color.RED);// 采用的颜色
		// textPaint.setShadowLayer(3f, 1,
		// 1,this.getResources().getColor(android.R.color.background_dark));//影音的设置
		canvas.drawText(str, AppUtils.dip2px(20), AppUtils.dip2px(40), textPaint);// 绘制上去字，开始未知x,y采用那只笔绘制
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		return icon;

	}

	/**
	 * 加载本地图片
	 * 
	 * @param filePath
	 * @return
	 */
	public static Bitmap getLoacalBitmap(String filePath) {
		FileInputStream fis = null;
		Bitmap bitmap = null;
		try {
			File file = new File(filePath);
			fis = new FileInputStream(file);
			bitmap = BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bitmap;
	}

	/**
	 * 加载本地图片
	 *
	 * @param filePath
	 * @return
	 */
	public static Bitmap getLoacalBitmap(String filePath,
										 BitmapFactory.Options opts) {
		FileInputStream fis = null;
		Bitmap bitmap = null;
		try {
			File file = new File(filePath);
			fis = new FileInputStream(file);
			bitmap = BitmapFactory.decodeStream(fis, null, opts);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bitmap;
	}

	/**
	 * 获得圆角图片的方法
	 * 
	 * @param bitmap
	 *            需处理的图片
	 * @param roundPx
	 *            圆角的弧率
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), bitmap.getConfig());
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(Color.RED);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

}
