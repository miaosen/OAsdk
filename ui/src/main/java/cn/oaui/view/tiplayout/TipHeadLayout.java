package cn.oaui.view.tiplayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.oaui.R;
import cn.oaui.annotation.InjectReader;
import cn.oaui.annotation.ViewInject;
import cn.oaui.view.CustomLayout;


/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2018-01-10  15:55
 * @Descrition
 */

public class TipHeadLayout extends CustomLayout {

    @ViewInject
    TextView tv_head,tv_error;
    @ViewInject
    public LinearLayout error_in_head,header;

    private String refreshText="获取中...";

    public TipHeadLayout(Context context) {
        super(context);
    }

    public TipHeadLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    public void initData() {
        start();
    }

    @Override
    protected void onCreateView() {
        InjectReader.injectAllFields(this);
    }


    @Override
    public int setXmlLayout() {
        return R.layout.tip_layout_head;
    }

    public boolean attached(){
        return !isFirstLoad;
    }

    public void refreshing(){
        error_in_head.setVisibility(View.GONE);
        header.setVisibility(View.VISIBLE);
        tv_head.setText(refreshText);
    }

    public void start(){
        error_in_head.setVisibility(View.GONE);
        header.setVisibility(View.VISIBLE);
        tv_head.setText("下拉释放刷新");
    }

    public void error(){
        error_in_head.setVisibility(View.VISIBLE);
        header.setVisibility(View.GONE);
    }

    public void error(String text){
        error_in_head.setVisibility(View.VISIBLE);
        header.setVisibility(View.GONE);
        tv_error.setText(text);
    }

    public String getRefreshText() {
        return refreshText;
    }

    public void setRefreshText(String refreshText) {
        this.refreshText = refreshText;
        if(tv_head!=null){
            tv_head.setText(refreshText);
        }
    }
}
