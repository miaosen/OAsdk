package cn.oasdk.ui;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.List;

import cn.oahttp.HttpRequest;
import cn.oasdk.base.BaseActivity;
import cn.oaui.L;
import cn.oaui.annotation.ViewInject;
import cn.oaui.data.RowObject;
import cn.oaui.utils.ViewUtils;
import cn.oaui.view.listview.BaseFillAdapter;
import cn.oaui.view.listview.DataListView;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2018-01-22  16:27
 * @Descrition
 */

public class DatalistDemoAct extends BaseActivity {

    @ViewInject
    ViewPager viewPager;

    ViewPagerAdapter viewPagerAdapter;

    List<View> views;

    @ViewInject
    DataListView dataListView;

    @Override
    public void initConfig() {

    }

    @Override
    public int getContentView() {
        return cn.oasdk.R.layout.ui_list_view_act;
    }

    @Override
    public void onViewCreate() {
        views=new LinkedList<>();
        views.add(ViewUtils.inflatView(context, cn.oasdk.R.layout.http_demo_act));
        views.add(ViewUtils.inflatView(context, cn.oasdk.R.layout.ui_dialog_demo_act));
        //dataListView.noScroll();
        dataListView.setOnDataListChange(new DataListView.OnDataListChange() {
            @Override
            public void onRefresh() {
                dataListView.addTempUrlSuffix("/1");
                //L.i("=========onRefresh==============");
                //dataListView.addParam("pageNum",1);
                //dataListView.addParam("pageSize",15);
            }
            @Override
            public void onLoadMore(int pageIndex) {
                L.i("=========onLoadMore==============");
                dataListView.addTempUrlSuffix("/"+pageIndex);
                //dataListView.addParam("pageNum",pageIndex);
            }

            @Override
            public void onLoadCompalte() {

            }
        });
        dataListView.getRequest().setMethod(HttpRequest.HttpMethod.GET);
        dataListView.setOnItemClickListener(new BaseFillAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View convertView, RowObject row, int position, BaseFillAdapter.ViewHolder holder) {
                ViewUtils.toast(row.toString());
            }
        });
    }

    @Override
    public void initData() {
        viewPagerAdapter=new ViewPagerAdapter(views);
        viewPager.setAdapter(viewPagerAdapter);
        //viewPager.setCurrentItem(0);

    }


    class ViewPagerAdapter extends PagerAdapter {

        private List<View> mViewList;
        public ViewPagerAdapter (List<View> views){
            this.mViewList = views;
        }

        @Override
        public int getCount() {
            if (mViewList != null) {
                return mViewList.size();
            }
            return 0;
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            ((ViewPager) view).addView(mViewList.get(position), 0);
            return mViewList.get(position);
        }

        @Override
        public boolean isViewFromObject(View view, Object arg1) {
            return (view == arg1);
        }

        @Override
        public void destroyItem(ViewGroup view, int position, Object arg2) {
            //L.i("=========destroyItem=============="+position);
            ((ViewPager) view).removeView(mViewList.get(position));
        }
    }
}
