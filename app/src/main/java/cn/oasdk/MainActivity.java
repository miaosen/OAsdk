package cn.oasdk;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import cn.oasdk.dlna.base.BaseActivity;
import cn.oasdk.data.HttpDemoActivity;
import cn.oasdk.data.bluetooth.BluetoothDemoAct;
import cn.oasdk.ui.BaseViewDemoAct;
import cn.oasdk.ui.DatalistDemoAct;
import cn.oasdk.ui.DialogDemoAct;
import cn.oasdk.ui.RecordDemoAct;
import cn.oasdk.ui.TipLayoutDemoAct;
import cn.oasdk.ui.UIDemoAct;
import cn.oasdk.wifi.WifiAct;
import cn.oaui.IntentFactory;
import cn.oaui.L;
import cn.oaui.annotation.ViewInject;
import cn.oaui.data.RowObject;
import cn.oaui.utils.BitmapUtils;
import cn.oaui.utils.FileUtils;
import cn.oaui.utils.ViewUtils;
import cn.oaui.view.attachment.VideoRecordActivity;
import cn.oaui.view.dialog.FrameDialog;
import cn.oaui.view.signature.SignatureView;
import cn.oaui.view.voicerecord.VoiceRecordView;
import zxing.CaptureActivity;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @ViewInject
    Button btnHttp, btnUI, btnRecord,
            btnDialog, btnTipLayout, btnViewPage,
            btnList, btnTakePic, btnVideoRcd,
            btnSign, btnVoice, btnBaseView,
            btn_zxing, btn_wifi, btn_bluetooth,btn_dlna;

    FrameDialog dlgVoice, dlgSign;
    XmlResourceParser parser;


    @Override
    public void initConfig() {

    }


    @Override
    public int getContentView() {
        return R.layout.activity_main;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onViewCreate() {
        VoiceRecordView voiceRecordView = new VoiceRecordView(MainActivity.this);
        dlgVoice = new FrameDialog(MainActivity.this, voiceRecordView);
        voiceRecordView.setOnRecordComplateListener(new VoiceRecordView.OnRecordComplateListener() {
            @Override
            public void onComplate(RowObject result) {
                ViewUtils.toast(result + "");
                dlgVoice.dismiss();
            }
        });
        SignatureView signatureView = new SignatureView(btnTipLayout.getContext());
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
        dlgSign = new FrameDialog(signatureView);
        //if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
        //    File  dataDir = getApplication().getObbDir();
        //    String absolutePath = dataDir.getAbsolutePath();
        //    File file=new File(absolutePath+"/AndroidManifest.xml");
        //}

        //String packageCodePath = (String) fileInputStream;
        //File file = new File(packageCodePath);
        //File[] files = file.listFiles();
        //L.i("=========onViewCreate=============="+files.toString());
        //applicationContext = getApplication().getApplicationContext();
        //List<PackageInfo> packagesHoldingPermissions = getPackageManager().
        //        getPackagesHoldingPermissions(new String[]{}, PackageManager.GET_PERMISSIONS);
        //for (int i = 0; i < packagesHoldingPermissions.size(); i++) {
        //    PackageInfo packageInfo = packagesHoldingPermissions.get(i);
        //    L.i("=============="+packageInfo);
        //}
        //L.i("=========onActivityResult============="+packagesHoldingPermissions.size()+"  "+packagesHoldingPermissions);
        //test();
        test01();
        if (Build.VERSION.SDK_INT >= 23) {
            String[] PERMISSIONS_CAMERA_AND_STORAGE = {
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
            int recordAudioPermission = ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.RECORD_AUDIO);
            int cameraPermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA);
            if (recordAudioPermission != PackageManager.PERMISSION_GRANTED
                    || cameraPermission != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_CAMERA_AND_STORAGE,
                        11);
            }


        }
        //EventBus.setObject(this,"refresh");

    }

    //public void refresh(String text){
    //    btnUI.setText(text);
    //}

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void test01() {
        //apk(getApplication().getPackageCodePath());
    }

    public static void apk(String apkUrl) {
        int length;
        ZipFile zipFile;
        try {
            zipFile = new ZipFile(new File(apkUrl));
            Enumeration enumeration = zipFile.entries();
            ZipEntry zipEntry = zipFile.getEntry(("AndroidManifest.xml"));
            InputStream inputStream = zipFile.getInputStream(zipEntry);
            byte[] buf = new byte[1024];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            for (int i; (i = inputStream.read(buf)) != -1; ) {
                baos.write(buf, 0, i);
            }
            String clob = new String(baos.toString().getBytes("iso8859-1"), "UTF-8");
            L.i("=========apk==============" + clob);
            //XmlResourceParser parser = new XmlResourceParser();
            //parser.open(zipFile.getInputStream(zipEntry));
            //boolean flag = true;
            //while (flag) {
            //
            //    int type = parser.next();
            //
            //    if (type == XmlPullParser.START_TAG) {
            //        int count = parser.getAttributeCount();
            //        for (int i = 0; i < count; i++) {
            //            String name = parser.getAttributeName(i);
            //            String value = parser.getAttributeValue(i);
            //            if (value.contains("MAIN")) {
            //                System.out.println(name + "-----------" + value);
            //                flag = false;
            //                break;
            //            }else if("package".equals(name)){
            //                System.out.println(name+"***"+value);
            //            }else{
            //                System.out.println(name+"   "+value);
            //            }
            //        }//end for
            //    }
            //}// end while       
        } catch (Exception e) {
        }
    }
    //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
    //        .permitAll().build();
    //StrictMode.setThreadPolicy(policy);
    //try {
    //    Properties p = new Properties();
    //    p.showLoad(getApplication().openFileInput("/AndroidManifest.xml"));
    //    L.i("=========test01=============="+p);
    //} catch (IOException e) {
    //    e.printStackTrace();
    //}


    private void test() {
        Bitmap bitmap = BitmapUtils.getBitmapByPath("/storage/emulated/0/OSSDk/2018-03-092018-03-09 170506.png");
        Bitmap bitmap1 = BitmapUtils.createImageThumbnail(bitmap, 256, 256);
        Bitmap roundedCornerBitmap = BitmapUtils.getRoundedCornerBitmap(bitmap1, 50);
        BitmapUtils.saveBitmapToPathAsJpg(roundedCornerBitmap, "/storage/emulated/0/OSSDk/saaaaaaaa.jpg", Bitmap.CompressFormat.PNG);
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
            if (view instanceof Button) {
                view.setOnClickListener(this);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        RowObject rowObject = IntentFactory.onActivityResult(requestCode, resultCode, data);
        if (rowObject != null) {
            L.i("=========onActivityResult==============" + rowObject);
            ViewUtils.toast("onActivityResult====" + rowObject);
        }

        String result = CaptureActivity.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            ViewUtils.toast(result);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 111) {
            //if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //    writeFile();
            //} else {
            //    Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            //}
        } else if (requestCode == VideoRecordActivity.REQUEST_CAMERA_PERMISSION_CODE) {
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
        if (v == btnHttp) {
            Intent intent = new Intent(MainActivity.this, HttpDemoActivity.class);
            startActivity(intent);
        } else if (v == btnUI) {
            Intent intent = new Intent(MainActivity.this, UIDemoAct.class);
            startActivity(intent);
        } else if (v == btnRecord) {
            Intent intent = new Intent(MainActivity.this, RecordDemoAct.class);
            startActivity(intent);
        } else if (v == btnDialog) {
            Intent intent = new Intent(context, DialogDemoAct.class);
            startActivity(intent);
        } else if (v == btnTipLayout) {
            Intent intent = new Intent(context, TipLayoutDemoAct.class);
            startActivity(intent);
        } else if (v == btnList) {
            Intent intent = new Intent(context, DatalistDemoAct.class);
            startActivity(intent);
        } else if (v == btnVoice) {
            dlgVoice.show();
        } else if (v == btnTakePic) {
            IntentFactory.takePicture(MainActivity.this);
        } else if (v == btnVideoRcd) {
            IntentFactory.movieRecord(MainActivity.this);
        } else if (v == btnSign) {
            dlgSign.fullscreen();
        } else if (v == btnBaseView) {
            Intent intent = new Intent(MainActivity.this, BaseViewDemoAct.class);
            startActivity(intent);
        } else if (v == btn_zxing) {
            Intent intent = new Intent(context, CaptureActivity.class);
            //intent.putExtra("viewId", CompanyCapture.getId());
            startActivityForResult(intent, CaptureActivity.REQUSET_CODE);
        } else if (v == btn_wifi) {
            Intent intent = new Intent(MainActivity.this, WifiAct.class);
            startActivity(intent);
        } else if (v == btn_bluetooth) {
            Intent intent = new Intent(MainActivity.this, BluetoothDemoAct.class);
            startActivity(intent);
        } else if (v == btn_dlna) {
            Intent intent = new Intent(MainActivity.this, BluetoothDemoAct.class);
            startActivity(intent);
        }
    }

}
