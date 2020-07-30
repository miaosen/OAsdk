package com.gzpykj.rmew;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import com.gzpykj.base.BaseActivity;
import com.gzpykj.rmew.ctas.ListAct;
import com.gzpykj.rmew.riskmap.RiskCompanyMapAct;

import java.util.LinkedList;
import java.util.List;

import cn.oaui.annotation.ViewInject;
import cn.oaui.data.Row;
import cn.oaui.utils.AppUtils;
import cn.oaui.utils.IntentUtils;
import cn.oaui.view.FlowLayout;
import cn.oaui.view.listview.BaseFillAdapter;

public class MainActivity extends BaseActivity {

    @ViewInject
    FlowLayout flowLayout;

    MAdapter mAdapter;

    private List<Row> rows = new LinkedList<Row>();

    String[] argsName = new String[]{
            "企业风险", "风险点", "药品风险", "问题药品流向", "企业预警", "预警地图", "药品预警"
    };
    String[] argsColor = new String[]{
            "#FE0303", "#4795E9", "#FE0303", "#4795E9", "#F8B51A", "#3CCF52", "#F8B51A"
    };
    Integer[] argsIcon = new Integer[]{
            R.mipmap.icon_company_hazards, R.mipmap.icon_point, R.mipmap.icon_drug
            , R.mipmap.icon_drug_flow, R.mipmap.icon_company_warn, R.mipmap.icon_map
            , R.mipmap.icon_drug
    };

    @Override
    public void initConfig() {

        for (int i = 0; i < 7; i++) {
            Row row = new Row();
            row.put("NAME", argsName[i]);
            row.put("COLOR", argsColor[i]);
            row.put("ICON", argsIcon[i]);
            rows.add(row);
        }

    }

    @Override
    public int getContentView() {
        return R.layout.activity_main;
    }


    @Override
    public void onViewCreate(Bundle savedInstanceState) {
        flowLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        mAdapter = new MAdapter(rows, R.layout.main_grid_item);
        flowLayout.setAdapter(mAdapter);
        flowLayout.setOnItemClickListener(new FlowLayout.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if ("风险点".equals(argsName[position])) {
                    IntentUtils.jump(context, ListAct.class);
                } else if ("问题药品流向".equals(argsName[position])) {
                    IntentUtils.jump(context, com.gzpykj.rmew.tdrug.ListAct.class);
                } else if ("企业预警".equals(argsName[position])) {
                    IntentUtils.jump(context, com.gzpykj.rmew.riskcomp.ListAct.class);
                } else if ("药品预警".equals(argsName[position])) {
                    IntentUtils.jump(context, com.gzpykj.rmew.uqdrug.ListAct.class);
                }else if ("药品风险".equals(argsName[position])) {
                    IntentUtils.jump(context, com.gzpykj.rmew.riskdrug.ListAct.class);
                }else if ("企业风险".equals(argsName[position])) {
                    IntentUtils.jump(context, com.gzpykj.rmew.comrisklv.ListAct.class);
                }else if ("预警地图".equals(argsName[position])) {
                    IntentUtils.jump(context, RiskCompanyMapAct.class);
                }
            }
        });
    }

    @Override
    public void initData() {

    }


    @SuppressLint("NewApi")
    public static Bitmap setRoundedColor(View view, float roundPx, String color) {
        Bitmap output = Bitmap.createBitmap(AppUtils.dp2px(90), AppUtils.dp2px(90), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, AppUtils.dp2px(90), AppUtils.dp2px(90));
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor(color));
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(output, rect, rect, paint);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(output);
        view.setBackground(bitmapDrawable);
        return output;
    }

    class MAdapter extends BaseFillAdapter {

        public MAdapter(List<Row> rows, int layout) {
            super(MainActivity.this, rows, layout);
        }

        @SuppressLint("NewApi")
        @Override
        public void setItem(View convertView, Row row, int position, ViewHolder holder) {
            View ln_bg = holder.views.get("ln_bg");
            View img = holder.views.get("img");
            setRoundedColor(ln_bg, 30, row.getString("COLOR"));
            img.setBackground(getResources().getDrawable((Integer) row.get("ICON")));
        }
    }


}
