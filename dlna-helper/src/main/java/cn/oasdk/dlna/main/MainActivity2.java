package cn.oasdk.dlna.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import java.util.LinkedList;
import java.util.List;

import cn.oasdk.dlna.R;
import cn.oasdk.dlna.base.BaseActivity;
import cn.oasdk.dlna.dms.FileServer;
import cn.oasdk.dlna.image.DocumentAct;
import cn.oasdk.dlna.image.ImageAct;
import cn.oasdk.dlna.image.MusicAct;
import cn.oasdk.dlna.image.VideoAct;
import cn.oaui.L;
import cn.oaui.annotation.ViewInject;
import cn.oaui.data.RowObject;
import cn.oaui.form.FormUtils;
import cn.oaui.utils.AppUtils;
import cn.oaui.utils.IntentUtils;
import cn.oaui.utils.ViewUtils;
import cn.oaui.view.FlowLayout;
import cn.oaui.view.dialog.extra.LoadingDialog;
import cn.oaui.view.listview.BaseFillAdapter;
import cn.oaui.view.tiplayout.TipLayout;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2019-10-24  14:38
 * @Descrition
 */

public class MainActivity2 extends BaseActivity {

    @ViewInject
    FlowLayout flowLayout;

    MAdapter mAdapter;

    private List<RowObject> rows = new LinkedList<RowObject>();

    String[] argsName = new String[]{
            "视频", "音乐", "图片", "文档", "收藏", "网络共享"
    };
    String[] argsColor = new String[]{
            "#C9638A", "#FE0303", "#01BF93", "#4795E9", "#F8B51A", "#938E8A"
    };
    Integer[] argsIcon = new Integer[]{
            R.drawable.icon_video, R.drawable.icon_radio, R.drawable.icon_pic
            , R.drawable.icon_file, R.drawable.icon_mark, R.drawable.icon_share

    };

    List<LinearLayout> listView = new LinkedList<>();


    LoadingDialog loadingDialog;

    @ViewInject
    TipLayout tipLayout;


    @Override
    public void initConfig() {

    }

    @Override
    public int getContentView() {
        return R.layout.main_act2;
    }


    @Override
    public void onViewCreate() {
        flowLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        mAdapter = new MAdapter(rows, R.layout.main_act2_grid_item);
        flowLayout.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseFillAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View convertView, RowObject row, int position) {
                L.i("============onItemClick===========" + row);
                if ("文档".equals(row.getString("NAME"))) {
                    IntentUtils.jump(context, DocumentAct.class);
                } else if ("图片".equals(row.getString("NAME"))) {
                    IntentUtils.jump(context, ImageAct.class);
                }
            }
        });
        //loadingDialog=new LoadingDialog(context);
        //loadingDialog.show();
        tipLayout.getHeadView().setRefreshText("正在进行文件扫描...");
        tipLayout.setOnTipListener(new TipLayout.OnTipListener() {
            @Override
            public void onRefresh() {
                L.i("============onRefresh===========");
                FileServer.scanFile(handler);
            }
            @Override
            public void onLoadMore() {

            }
        });
        tipLayout.refresh();
        List<View> allChildViews = ViewUtils.getAllChildViews(ViewUtils.getContentView(context));
        for (int i = 0; i < allChildViews.size(); i++) {
            View view = allChildViews.get(i);
            if (view instanceof LinearLayout && "item_view".equals(view.getTag())) {
                listView.add((LinearLayout) view);
            }
        }
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                ViewUtils.toast("扫描完成");
                //loadingDialog.dismiss();
                tipLayout.endRefresh();
                setListView();
            }
            if (msg.obj != null) {
                tipLayout.setRefreshText(msg.obj + "");
            }

        }
    };

    @SuppressLint("NewApi")
    private void setListView() {
        rows.clear();
        for (int i = 0; i < 6; i++) {
            RowObject row = new RowObject();
            row.put("NAME", argsName[i]);
            row.put("COLOR", argsColor[i]);
            row.put("ICON", argsIcon[i]);
            L.i("============setListView==========="+FileServer.rowsImage.size());
            L.i("============setListView==========="+FileServer.rowsDocumnet.size());
            L.i("============setListView==========="+FileServer.rowsVideo.size());
            L.i("============setListView==========="+FileServer.rowsMusic.size());
            if (i == 2) {
                row.put("NUM", "(" + FileServer.rowsImage.size() + ")");
            } else if (i == 3) {
                row.put("NUM", "(" + FileServer.rowsDocumnet.size() + ")");
            } else if (i == 0) {
                row.put("NUM", "(" + FileServer.rowsVideo.size() + ")");
            } else if (i == 1) {
                row.put("NUM", "(" + FileServer.rowsMusic.size() + ")");
            }
            rows.add(row);
        }
        for (int i = 0; i < listView.size(); i++) {
            LinearLayout linearLayout = listView.get(i);
            final RowObject row = rows.get(i);
            FormUtils.setContentValues(linearLayout, row);
            View ln_bg = linearLayout.findViewById(R.id.ln_bg);
            View img = linearLayout.findViewById(R.id.img);
            setRoundedColor(ln_bg, 30, row.getString("COLOR"));
            img.setBackground(getResources().getDrawable((Integer) row.get("ICON")));
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if ("文档".equals(row.getString("NAME"))) {
                        IntentUtils.jump(context, DocumentAct.class);
                    } else if ("图片".equals(row.getString("NAME"))) {
                        IntentUtils.jump(context, ImageAct.class);
                    } else if ("视频".equals(row.getString("NAME"))) {
                        IntentUtils.jump(context, VideoAct.class);
                    } else if ("音乐".equals(row.getString("NAME"))) {
                        IntentUtils.jump(context, MusicAct.class);
                    }
                }
            });
        }
    }

    @Override
    public void initData() {
        L.i("============initData===========");
        FileServer.initFileFromCache(handler);
        if (Build.VERSION.SDK_INT >= 23) {
            String[] PERMISSIONS_CAMERA_AND_STORAGE = {
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE
            };
            int recordAudioPermission = ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.RECORD_AUDIO);
            int cameraPermission = ContextCompat.checkSelfPermission(MainActivity2.this, Manifest.permission.CAMERA);
            if (recordAudioPermission != PackageManager.PERMISSION_GRANTED
                    || cameraPermission != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(MainActivity2.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(MainActivity2.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(MainActivity2.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(MainActivity2.this, PERMISSIONS_CAMERA_AND_STORAGE,
                        11);
            }

        }
        //checkPermission();
    }

    private void checkPermission() {
        if (!Settings.System.canWrite(this)) {
            ViewUtils.toast("请在该设置页面勾选，才可以使用路况提醒功能");
            Uri selfPackageUri = Uri.parse("package:"
                    + this.getPackageName());
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                    selfPackageUri);
            startActivity(intent);
        }
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

        public MAdapter(List<RowObject> rows, int layout) {
            super(MainActivity2.this, rows, layout);
        }

        @SuppressLint("NewApi")
        @Override
        public void setItem(View convertView, RowObject row, int position, ViewHolder holder) {
            View ln_bg = holder.views.get("ln_bg");
            View img = holder.views.get("img");
            setRoundedColor(ln_bg, 30, row.getString("COLOR"));
            img.setBackground(getResources().getDrawable((Integer) row.get("ICON")));
        }
    }


}
