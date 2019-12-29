package cn.oasdk.dlna.image;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;

import cn.oasdk.dlna.R;
import cn.oasdk.dlna.base.BaseActivity;
import cn.oaui.ImageFactory;
import cn.oaui.L;
import cn.oaui.annotation.ViewInject;
import cn.oaui.data.RowObject;
import cn.oaui.utils.IntentUtils;
import cn.oaui.utils.ViewUtils;
import cn.oaui.view.ViewPagerForScrollView;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2019-11-04  15:59
 * @Descrition
 */

public class ShowImageAct extends BaseActivity{

    public static final String IMG_INDEX ="path";

    public static final String ROW_KEY="row_key";

    int index;

    LinkedList<RowObject>  rowsCur;



    @ViewInject
    ViewPagerForScrollView viewPager;

    @Override
    public void initConfig() {
        rowsCur = IntentUtils.getRows(getIntent(), ROW_KEY);
        index = getIntent().getIntExtra(IMG_INDEX,0);
    }

    @Override
    public int getContentView() {
        return R.layout.image_show_act;
    }

    @Override
    public void onViewCreate() {
        MyPagerAdapter adapter = new MyPagerAdapter();
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(index);
    }

    //Handler handler=new Handler(){
    //    @Override
    //    public void handleMessage(Message msg) {
    //        super.handleMessage(msg);
    //        Bitmap bitmap= (Bitmap) msg.obj;
    //        img.setImageBitmap(bitmap);
    //    }
    //};

    @Override
    public void initData() {

        //thread.start();
    }



    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    class MyPagerAdapter extends PagerAdapter {
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return rowsCur.size();
        }

        //对超出范围的资源进行销毁
        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
        }
        //对显示的资源进行初始化
        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = ViewUtils.inflatView(container.getContext(),R.layout.image_vp_item);
            L.i("============instantiateItem==========="+position+"  "+index);
            ZoomImageView img=view.findViewById(R.id.img);
            RowObject rowObject = rowsCur.get(position);
            L.i("============initConfig==========="+rowsCur);
            ImageFactory.loadFileImage(img, rowObject.getString("filePath"));
            //Thread thread = new Thread(new Runnable() {
            //    @Override
            //    public void run() {
            //        FutureTarget<Bitmap> bitmap = Glide.with(ShowImageAct.this)
            //                .asBitmap()
            //                .load(path)
            //                .submit();
            //        Message message = new Message();
            //        try {
            //            message.obj = bitmap.get();
            //        } catch (InterruptedException e) {
            //            e.printStackTrace();
            //        } catch (ExecutionException e) {
            //            e.printStackTrace();
            //        }
            //        handler.sendMessage(message);
            //    }
            //});
            container.addView(view);
            return view;
        }
    }
}
