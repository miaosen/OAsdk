package gzpykj.hzpqy;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.EditText;

import com.oahttp.HttpRequest;
import com.oahttp.callback.StringCallBack;
import com.oahttp.cookies.CookieManager;
import com.oaui.IntentFactory;
import com.oaui.L;
import com.oaui.annotation.ViewInject;
import com.oaui.utils.FileUtils;
import com.oaui.utils.ViewUtils;
import com.oaui.view.attachment.VideoRecordActivity;
import com.oaui.view.tiplayout.TipLayout;

import java.io.File;
import java.io.IOException;

import gzpykj.hzpqy.base.BaseActivity;
import gzpykj.hzpqy.base.Global;
import gzpykj.hzpqy.base.JsonHandler;
import gzpykj.hzpqy.utils.StringUtils;

public class LoginActivity extends BaseActivity {

    @ViewInject
    View login_btn;
    @ViewInject
    EditText userName,password;

    @ViewInject
    TipLayout tipLayout;

    static int REQUEST_CODE=11002;

    @Override
    public void initConfig() {

    }

    @Override
    public int getContentView() {
        return R.layout.login;
    }

    @Override
    public void onViewCreate(Bundle savedInstanceState) {
        tipLayout.end();
        tipLayout.setEanableRefresh(false);
        tipLayout.setRefreshText("登陆中...");
        tipLayout.setOnErrorViewCilckListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        }); login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //new IntentIntegrator(LoginActivity.this).initiateScan();
                //IntentIntegrator integrator = new IntentIntegrator(LoginActivity.this);
                //integrator.setPrompt("Scan a barcode");
                //integrator.setCameraId(0);  // Use a specific camera of the device
                //integrator.setBeepEnabled(false);
                //integrator.setCaptureActivity(ScanActivity.class);
                //integrator.setBarcodeImageEnabled(true);
                //integrator.initiateScan();
                login();
            }
        });

        ActivityCompat.requestPermissions(LoginActivity.this,
                            new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE },
                            111);
        ActivityCompat.requestPermissions(LoginActivity.this,
                new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE },
                111);
        IntentFactory.checkCameraPermission(LoginActivity.this,12);
        //writeFile();
    }
    private void login() {
        L.i("=========login=============="+StringUtils.sha1("123456"));
        //192.168.0.100
        tipLayout.refresh();

        CookieManager.clearAll(getApplication());
        String url="/actions/hzpLoginAction/login/"+userName.getText()+"/"+StringUtils.sha1(password.getText()+"");
        HttpRequest request = Global.createRequest(url);
        request.setCallback(new StringCallBack() {
            @Override
            public void onSuccess(String text) {
                JsonHandler jsonHandler = new JsonHandler(text);
                if(jsonHandler.isSuccess()){
                    Global.saveUserInfo(jsonHandler.getAsRow());
                    Intent in=new Intent(context,MainActivity.class);
                    tipLayout.end();
                    startActivity(in);
                    finish();
                }else{
                    tipLayout.error(text);
                }
            }
            @Override
            protected void onFail(Exception e) {
                super.onFail(e);
                e.printStackTrace();
                tipLayout.error();
            }
        });

        request.send();
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
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 111) {
            //if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //    //writeFile();
            //} else {
            //    Toast.makeText(LoginActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            //}
        }else if(requestCode== VideoRecordActivity.REQUEST_CAMERA_PERMISSION_CODE){
            IntentFactory.movieRecord(LoginActivity.this);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



    @Override
    public void initData() {

    }

}
