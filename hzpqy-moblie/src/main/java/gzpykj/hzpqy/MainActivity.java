package gzpykj.hzpqy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.oaui.annotation.ViewInject;
import com.oaui.view.HeaderView;

import gzpykj.hzpqy.base.BaseActivity;
import gzpykj.hzpqy.feed.FeedListActivity;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2018-06-14  16:46
 * @Desrition
 */

public class MainActivity extends BaseActivity {

    @ViewInject
    HeaderView headerView;
    @ViewInject
    LinearLayout ln_drug;


    @Override
    public void initConfig() {

    }

    @Override
    public int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    public void onViewCreate(Bundle savedInstanceState) {
        headerView.setTitleText("广州市化妆品质量监督管理系统");
        headerView.setClickLeftFinish(false);
        ln_drug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(context,FeedListActivity.class);
                startActivity(in);
            }
        });
    }

    @Override
    public void initData() {

    }
}
