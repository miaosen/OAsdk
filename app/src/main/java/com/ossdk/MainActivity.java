package com.ossdk;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ossdk.base.BaseActivity;
import com.ossdk.http.HttpDemoActivity;
import com.ossdk.ui.BaseViewDemoAct;
import com.ossdk.ui.DialogDemoAct;
import com.ossdk.ui.RecordDemoAct;
import com.ossdk.ui.TipLayoutDemoAct;
import com.ossdk.ui.UIDatalistDemoAct;
import com.ossdk.ui.UIDemoAct;
import com.osui.L;
import com.osui.annotation.ViewInject;
import com.osui.data.RowObject;
import com.osui.utils.BitmapUtils;
import com.osui.utils.FileUtils;
import com.osui.utils.ViewUtils;
import com.osui.IntentFactory;
import com.osui.view.attachment.VideoRecordActivity;
import com.osui.view.dialog.FrameDialog;
import com.osui.view.signature.SignatureView;
import com.osui.view.voicerecord.VoiceRecordView;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    @ViewInject
    Button btnHttp, btnUI, btnRecord,
            btnDialog, btnTipLayout, btnViewPage,
            btnList,btnTakePic,btnVideoRcd,
            btnSign,btnVoice,btnBaseView;

    FrameDialog dlgVoice,dlgSign;

    @Override
    public void initConfig() {

    }

    @Override
    public int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    public void onViewCreate() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    111);
        } else {
            //writeFile();
        }
        VoiceRecordView voiceRecordView = new VoiceRecordView(MainActivity.this);
        dlgVoice=new FrameDialog(MainActivity.this,voiceRecordView);
        voiceRecordView.setOnRecordComplateListener(new VoiceRecordView.OnRecordComplateListener() {
            @Override
            public void onComplate(RowObject result) {
                ViewUtils.toast(result+"");
                dlgVoice.dismiss();
            }
        });
        SignatureView signatureView =new SignatureView(btnTipLayout.getContext());
        signatureView.setOnSureListener(new SignatureView.OnActionListener() {
            @Override
            public void onSure(Bitmap bitmap) {
                dlgSign.dismiss();
            }

            @Override
            public void onCancle() {
                dlgSign.dismiss();
            }
        });
        dlgSign=new FrameDialog(signatureView);

        //test();
    }

    private void test() {
        Bitmap bitmap= BitmapUtils.getBitmapByPath("/storage/emulated/0/OSSDk/2018-03-092018-03-09 170506.png");
        Bitmap bitmap1 = BitmapUtils.createImageThumbnail(bitmap,256,256);
        Bitmap roundedCornerBitmap = BitmapUtils.getRoundedCornerBitmap(bitmap1,50);
        BitmapUtils.saveBitmapToPathAsJpg(roundedCornerBitmap,"/storage/emulated/0/OSSDk/saaaaaaaa.jpg",Bitmap.CompressFormat.PNG);
    }

    public void writeFile() {
        String path = FileUtils.getSDCardPath() + "bbb.txt";
        File file = new File(path);
        //boolean mkdirs = file.mkdirs();
        try {
            file.createNewFile();
            FileUtils.writeFile("aaaaaaa", path);
            ViewUtils.toast("写入成功！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void initData() {
        List<View> allChildViews = ViewUtils.getAllChildViews(ViewUtils.getDecorView(MainActivity.this));
        for (int i = 0; i < allChildViews.size(); i++) {
            View view = allChildViews.get(i);
            if(view instanceof Button){
                view.setOnClickListener(this);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        RowObject rowObject = IntentFactory.onActivityResult(requestCode, resultCode, data);
        L.i("=========onActivityResult=============="+rowObject);
        ViewUtils.toast("onActivityResult===="+rowObject);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 111) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                writeFile();
            } else {
                Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode== VideoRecordActivity.REQUEST_CAMERA_PERMISSION_CODE){
            IntentFactory.movieRecord(MainActivity.this);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }


    @Override
    public void onClick(View v) {
        if(v==btnHttp){
            Intent intent = new Intent(MainActivity.this, HttpDemoActivity.class);
            startActivity(intent);
        }else  if(v==btnUI){
            Intent intent = new Intent(MainActivity.this, UIDemoAct.class);
            startActivity(intent);
        }else  if(v==btnRecord){
            Intent intent = new Intent(MainActivity.this, RecordDemoAct.class);
            startActivity(intent);
        }else  if(v==btnDialog){
            Intent intent = new Intent(context, DialogDemoAct.class);
            startActivity(intent);
        }else  if(v==btnTipLayout){
            Intent intent = new Intent(context, TipLayoutDemoAct.class);
            startActivity(intent);
        }else  if(v==btnList){
            Intent intent = new Intent(context, UIDatalistDemoAct.class);
            startActivity(intent);
        }else  if(v==btnVoice){
            dlgVoice.show();
        }else  if(v==btnTakePic){
            IntentFactory.takePicture(MainActivity.this);
        }else  if(v==btnVideoRcd){
            IntentFactory.movieRecord(MainActivity.this);
        }else  if(v==btnSign){
            dlgSign.fullscreen();
        }else  if(v==btnBaseView){
            Intent intent = new Intent(MainActivity.this, BaseViewDemoAct.class);
            startActivity(intent);
        }
    }
}
