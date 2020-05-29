package cn.oasdk.fileview.data;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

import cn.oasdk.fileview.R;
import cn.oasdk.fileview.view.FileView;
import cn.oasdk.fileview.view.QuickView;
import cn.oaui.L;
import cn.oaui.annotation.InjectReader;
import cn.oaui.annotation.ViewInject;
import cn.oaui.utils.IntentUtils;
import cn.oaui.view.ViewPagerForScrollView;

public class MainActivity extends AppCompatActivity {

    @ViewInject
    FileView fileView;
    boolean isRoot=true;


    QuickView quickView;

    @ViewInject
    ViewPagerForScrollView viewPager;
    Map<Integer, View> mapFgmView = new HashMap<Integer, View>();
    PagerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPermission();
        setContentView(R.layout.activity_main);
        InjectReader.injectAllFields(this);
        initPagerView();
    }


    private void initPermission() {
        L.i("======initPermission===== "+Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= 23) {
            String[] PERMISSIONS_CAMERA_AND_STORAGE = {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_CAMERA_AND_STORAGE,
                        11);
            }
        }
    }

    private void initPagerView() {
        adapter = new MyPagerAdapter();
        viewPager.setCanScroll(true);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(final int position) {
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        //缓存个数
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == 11) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                IntentUtils.jump(MainActivity.this,MainActivity.class);
                FileData.scanDataComplate=false;
//                AppContext.getApplication().initData();
                finish();
            } else {
                finish();
            }
            return;
        }
    }


    class MyPagerAdapter extends PagerAdapter {
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
        @Override
        public int getCount() {
            return 2;
        }
        //对超出范围的资源进行销毁
        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
//            super.destroyItem(container,position,object);
        }
        //对显示的资源进行初始化
        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = getPagerView(position);
            if (view != null && view.getParent() == null) {
                //container.removeView(view);
                container.addView(view);
            }
            return view;
        }
    }

    public View getPagerView(int position) {
        View view = mapFgmView.get(position);
        if (view == null) {
            if (position == 0) {
                fileView =new FileView(MainActivity.this);
                view=fileView;
            } else if (position == 1) {
                quickView=new QuickView(MainActivity.this);
                view=quickView;
            }
            mapFgmView.put(position, view);
        }
        return view;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if(viewPager.getCurrentItem()==0){
                if(fileView.isEditMode){
                    fileView.switchEditMode(false);
                } else if (isRoot&&fileView.isRootDir()) {
                    isRoot= fileView.isRootDir();
                    return super.onKeyDown(keyCode, event);
                }else{
                    isRoot= fileView.isRootDir();
                    fileView.back();
                }
            }
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

}
