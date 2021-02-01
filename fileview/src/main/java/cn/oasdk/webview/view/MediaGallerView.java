package cn.oasdk.webview.view;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.danikula.videocache.HttpProxyCacheServer;

import java.util.LinkedList;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import cn.oasdk.base.AppContext;
import cn.oasdk.fileview.R;
import cn.oaui.ImageFactory;
import cn.oaui.L;
import cn.oaui.annotation.InjectReader;
import cn.oaui.annotation.ViewInject;
import cn.oaui.data.Row;
import cn.oaui.view.CustomLayout;
import cn.oaui.view.ViewPagerForScrollView;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2020-08-11  14:31
 * @Descrition
 */
public class MediaGallerView extends CustomLayout {


    @ViewInject
    public ViewPagerForScrollView viewPager;
    public PagerAdapter adapter;
    public LinkedList<Row> rowsMedia = new LinkedList<>();
    public LinkedList<Row> rowsRes = new LinkedList<>();

    @ViewInject
    ImageView img_colse;

    public MediaGallerView(Context context) {
        super(context);
    }

    public MediaGallerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void initData() {
        adapter = new MyPagerAdapter();
        viewPager.setCanScroll(true);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(final int position) {
                Row row = rowsMedia.get(position);
                String url = row.getString("url");
                View rootView = viewPager.getChildAt(position);
                if(rootView instanceof VideoPalyView){
                    VideoPalyView videoPalyView= (VideoPalyView) rootView;
                    videoPalyView.player.setPlayWhenReady(true);
                }

                //L.i("============onPageSelected==========="+url);
                //L.i("============onPageSelected==========="+rootView.getClass().getSimpleName());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }


        });
        //缓存个数
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(adapter);
        //viewPager.setCurrentItem(0);

    }

    @Override
    protected void onCreateView() {
        InjectReader.injectAllFields(this);
        img_colse.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup parent = (ViewGroup) MediaGallerView.this.getParent();
                parent.removeView(MediaGallerView.this);
            }
        });
    }

    @Override
    public int setXmlLayout() {
        return R.layout.media_gallery_view;
    }


    class MyPagerAdapter extends PagerAdapter {
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return rowsMedia.size();
        }

        //对超出范围的资源进行销毁
        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            //super.destroyItem(container,position,object);
            //L.i("============destroyItem==========="+position);
            //if(container.getChildAt(position)!=null){
            //    container.removeViewAt(position);
            //}
            ((ViewPager) container).removeView((View) object);
        }

        //对显示的资源进行初始化
        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            L.i("============instantiateItem==========="+position);
            View childAt = container.getChildAt(position);
            if (childAt != null) {
                String tagUrl = (String) childAt.getTag(R.id.activity_main);
                String url = rowsMedia.get(position).getString("url");
                String type = rowsMedia.get(position).getString("type");
                //更新原画面
                if(!url.equals(tagUrl)){
                    L.i("============instantiateItem 1==========="+position);
                    if ("audio".equals(type) || "video".equals(type)) {//原画面为视频，音频
                        if(childAt instanceof VideoPalyView){
                            VideoPalyView videoPalyView = (VideoPalyView) childAt;
                            videoPalyView.stop();
                            videoPalyView.prepare(getProxyUrl(url));
                        } else{
                            container.removeView(childAt);
                            childAt=getPagerView(position);
                        }
                    }else{//原画面为图片
                        if(childAt instanceof VideoPalyView){
                            container.removeView(childAt);
                            childAt=getPagerView(position);
                        }else{
                            ImageView imgView = (ImageView) childAt;
                            imgView.setBackgroundColor(R.color.transparent_half);
                            ImageFactory.loadImage(imgView, url);
                        }
                    }
                }
                return childAt;
            }else{
                View view = getPagerView(position);
                container.addView(view);
                return view;
            }


        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }
    }




    public View getPagerView(int position) {
        Row row = rowsMedia.get(position);
        String type = row.getString("type");
        String url = row.getString("url");
        View view = null;
        if ("audio".equals(type) || "video".equals(type)) {
            VideoPalyView videoPalyView = new VideoPalyView(getContext());
            view = videoPalyView;
            videoPalyView.stop();
            videoPalyView.prepare(getProxyUrl(url));
        } else {
            ImageView imgView = new ImageView(getContext());
            ImageFactory.loadImage(imgView, url);
            view = imgView;
        }
        view.setTag(R.id.activity_main,url);
        return view;
    }

    private Uri getProxyUrl(String url) {
        HttpProxyCacheServer proxy = AppContext.getProxy();
        String proxyUrl = proxy.getProxyUrl(url);
        return Uri.parse(proxyUrl);

    }

    public LinkedList<Row> getRowsMedia() {
        return rowsMedia;
    }

    public void setRowsMedia(LinkedList<Row> rowsMedia) {
        this.rowsMedia.clear();
        this.rowsMedia.addAll(rowsMedia);
        this.rowsRes = rowsMedia;
    }
}
