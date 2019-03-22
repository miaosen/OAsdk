//package gzpykj.hzpqy;
//
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.view.KeyEvent;
//
//import com.journeyapps.barcodescanner.CaptureManager;
//import com.journeyapps.barcodescanner.DecoratedBarcodeView;
//
//import com.gzpykj.hzpqy.base.BaseActivity;
//
///**
// * @author zengmiaosen
// * @email 1510809124@qq.com
// * @CreateDate 2018-06-14  11:33
// * @Descrition
// */
//
//public class ScanActivity extends BaseActivity {
//    private CaptureManager capture;
//    private DecoratedBarcodeView barcodeScannerView;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//    }
//
//    @Override
//    public void initConfig() {
//
//    }
//
//    @Override
//    public int getContentView() {
//        return  R.layout.scan_act;
//    }
//
//    @Override
//    public void onViewCreate(Bundle savedInstanceState) {
//        barcodeScannerView = initializeContent();
//
//        capture = new CaptureManager(this, barcodeScannerView);
//        capture.initializeFromIntent(getIntent(), savedInstanceState);
//        capture.decode();
//    }
//
//    @Override
//    public void initData() {
//
//    }
//
//    /**
//     * Override to use a different layout.
//     *
//     * @return the DecoratedBarcodeView
//     */
//    protected DecoratedBarcodeView initializeContent() {
//        return (DecoratedBarcodeView)findViewById(R.id.zxing_barcode_scanner);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        capture.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        capture.onPause();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        capture.onDestroy();
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        capture.onSaveInstanceState(outState);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
//        capture.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
//    }
//}
