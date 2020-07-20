package cn.oaui;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;
import com.bumptech.glide.request.RequestOptions;

import java.security.MessageDigest;


/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2018-03-12  15:45
 * @Descrition
 */

public class ImageFactory {


    /**
     * 加载图片
     * @param imageView
     * @param path
     */
    public static void loadImage(ImageView imageView, String path) {
        Glide.with(imageView.getContext()).load(path).into(imageView);

    }

    /**
     * 加载图片
     * @param imageView
     * @param path
     */
    public static void loadFileImage(ImageView imageView, String path) {
        Glide.with(imageView.getContext()).load(path).into(imageView);

    }


    /**
     * 缩放图片并且圆角
     * @param imageView
     * @param path
     */
    public static void loadImageCorner(ImageView imageView, String path) {
        //通过RequestOptions扩展功能,override:采样率,因为ImageView就这么大,可以压缩图片,降低内存消耗
        RequestOptions options=RequestOptions.bitmapTransform(new GlideRoundTransform(6)).override(100,100).fallback(R.mipmap.icon_file).error(R.mipmap.icon_file);

        Glide.with(imageView.getContext()).load(path).apply(options).into(imageView);
    }
    /**
     * 缩放图片并且圆角
     * @param imageView
     * @param drawable
     */
    public static void loadImageCorner(ImageView imageView, Drawable drawable) {
        //通过RequestOptions扩展功能,override:采样率,因为ImageView就这么大,可以压缩图片,降低内存消耗
        RequestOptions options=RequestOptions.bitmapTransform(new GlideRoundTransform(6)).override(100,100).fallback(R.mipmap.icon_file).error(R.mipmap.icon_file);

        Glide.with(imageView.getContext()).load(drawable).apply(options).into(imageView);
    }
    /**
     * 拉伸然后圆角
     */
    static class GlideRoundTransform extends BitmapTransformation {
        public static float radius = 0f;
        public GlideRoundTransform(int dp) {
            super();
            radius = Resources.getSystem().getDisplayMetrics().density * dp;
        }
        @Override
        protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
            Bitmap bitmap = TransformationUtils.centerCrop(pool, toTransform, outWidth, outHeight);
            return roundCrop(pool, bitmap);
        }

        private static Bitmap roundCrop(BitmapPool pool, Bitmap source) {
            if (source == null) return null;
            Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
            canvas.drawRoundRect(rectF, radius, radius, paint);
            return result;
        }
        @Override
        public void updateDiskCacheKey(MessageDigest messageDigest) {

        }

    }

    //public static void loadImage(ImageView imageView, Bitmap bitmap) {
    //    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    //    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
    //    byte[] bytes = baos.toByteArray();
    //    Glide.with(imageView.getContext()).load(path).into(imageView);
    //
    //}
}
