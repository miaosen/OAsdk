package gzpykj.hzpqy.feed;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.util.List;
import java.util.Map;

import androidx.annotation.RequiresApi;
import cn.oahttp.HttpRequest;
import cn.oahttp.callback.StringCallBack;
import cn.oaui.L;
import cn.oaui.annotation.ViewInject;
import cn.oaui.data.Row;
import cn.oaui.form.Form;
import cn.oaui.utils.DateTimeUtils;
import cn.oaui.utils.IntentUtils;
import cn.oaui.utils.RowUtils;
import cn.oaui.utils.StringUtils;
import cn.oaui.utils.ViewUtils;
import cn.oaui.view.HeaderView;
import cn.oaui.view.dialog.FrameDialog;
import cn.oaui.view.dialog.extra.TipDialog;
import cn.oaui.view.dialog.extra.WindowTipDialog;
import cn.oaui.view.listview.BaseFillAdapter;
import cn.oaui.view.listview.DataListView;
import gzpykj.hzpqy.R;
import gzpykj.hzpqy.base.BaseActivity;
import gzpykj.hzpqy.base.Global;
import gzpykj.hzpqy.base.HzDataListView;
import gzpykj.hzpqy.base.JsonHandler;
import okhttp3.Callback;
import zxing.CaptureActivity;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2018-06-14  16:57
 * @Descrition
 */

public class FeedDeatailActivity extends BaseActivity {


    @ViewInject
    HeaderView headerView;

    @ViewInject
    DataListView dataListView;

    @ViewInject
    Button btn_blue;

    @ViewInject
    EditText handler;
    
    @ViewInject
    View btn_save;

    Row rowFeed;

    List<Row> rowsFeedNeed;

    Form form;

    TipDialog tipDialog;



    @Override
    public void initConfig() {
        rowFeed = IntentUtils.getRow(getIntent(), FeedListActivity.FEED_DETAIL_ROW);
        L.i("=========代办信息==============" + rowFeed);


    }

    private void initFeedNeed(String text) {
        JsonHandler jsonHandler = new JsonHandler(text);
        if (jsonHandler.isSuccess()) {
            rowsFeedNeed = (List<Row>) jsonHandler.getAsRow().get("data");
            if (rowsFeedNeed == null || rowsFeedNeed.size() == 0) {
                ViewUtils.toast("领料单为空！");
            }

        }
    }

    private void getFeed(Callback callback) {
        HttpRequest request = Global.createRequest("/jsp/produce/feed/pickers/req_detail_list.jsp");
        Global.addJsonParam(request);
        request.addParam("workOrderId", rowFeed.getString("mainId"));
        request.setCallback(callback);
        request.sendAsync();
    }

    @Override
    public int getContentView() {
        return R.layout.feed_detail_act;
    }

    @Override
    public void onViewCreate(Bundle savedInstanceState) {
        form = new Form(activity);
        form.fill(rowFeed);
        dataListView.noScroll();
        dataListView.addParam("workOrderId", rowFeed.getString("mainId"));
        dataListView.setOnItemClickListener(new BaseFillAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View convertView, Row row, int position, BaseFillAdapter.ViewHolder viewHolder) {

            }
        });
        dataListView.setOnItemModifylistenert(new DataListView.OnItemModifylistenert() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void setItemView(View convertView, final Row row, int position, BaseFillAdapter.ViewHolder holder) {
                EditText displayQty = (EditText) holder.views.get("displayQty");
                displayQty.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                    @Override
                    public void afterTextChanged(Editable s) {
                        row.put("displayQty", s + "");
                    }
                });
                TextView tv_validate= (TextView) holder.views.get("tv_validate");
                String action = row.getString("action");
                if(StringUtils.isEmpty(action)){
                    tv_validate.setBackground(getResources().getDrawable(R.drawable.shape_sector_grey));
                    tv_validate.setText("未验证");
                }else{
                    tv_validate.setBackground(getResources().getDrawable(R.drawable.shape_sector_blue));
                    tv_validate.setText("已验证");
                }
            }
        });
        dataListView.setOnDataListChange(new DataListView.OnDataListChange() {
            @Override
            public void onRefresh() {
            }

            @Override
            public void onLoadMore(int pageIndex) {
            }

            @Override
            public void onLoadCompalte() {
                //addFeedsDetail();
            }
        });
        btn_blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startScan();
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if(validateDetail()){
                    lineSubmit();
                //}
            }
        });
    }

    /**
     * 开始扫描
     */
    private void startScan() {
        //IntentIntegrator integrator = new IntentIntegrator(activity);
        //integrator.setPrompt("请扫描领料单条码");
        //integrator.setCameraId(0);  // Use a specific camera of the device
        //integrator.setCaptureActivity(ScanActivity.class);
        //integrator.setBarcodeImageEnabled(true);
        //integrator.initiateScan();
        //Intent intent = new Intent(context, CaptureActivity.class);
        //startActivityForResult(intent, CaptureActivity.REQUSET_CODE);
        //启动二维码扫码
        CaptureActivity.startScan(this);

    }


    private boolean validateDetail() {
        boolean success=true;
        List<Row> rows = dataListView.getRows();
        for (int i = 0; i < rows.size(); i++) {
            Row row = rows.get(i);
            String action = row.getString("action");
            if(StringUtils.isEmpty(action)){
                String materialName = row.getString("materialName");
                ViewUtils.toast("物料 "+materialName+" 未进行扫码");
                success=false;
                i=rows.size();
            }

        }
        return success;
    }

    private void lineSubmit() {
        Row contentValue = getContentValue();
        Row detail = new Row();
        detail.put("foreignKeyField", "feedId");
        detail.put("rows", dataListView.getRows());
        String cacheData = JSON.toJSONString(detail);
        contentValue.put("cacheData", cacheData);
        contentValue.remove("dataListView");
        contentValue.remove("workOrderNo");
        String workOrderIdText = contentValue.getString("workOrderNo");
        contentValue.put("workOrderIdText", workOrderIdText);
        contentValue.put("primaryKeyField", "mainId");
        contentValue.put("_feedNo","");
        //contentValue.put("handler","李静");
        contentValue.put("handleTime", DateTimeUtils.getCurrentTime("yyyy-MM-dd"));
        Map<String, Object> paramMap = RowUtils.rowToMap(contentValue);
        L.i("=========onClick=============="+cacheData);
        HttpRequest request = Global.createRequest("/jsp/produce/feed/feed_action.jsp?save");
        request.addParam(paramMap);
        request.addParam("workOrderId", rowFeed.getString("mainId"));
        request.setCallback(new StringCallBack() {
            @Override
            public void onSuccess(String text) {
                JsonHandler jsonHandler = new JsonHandler(text);
                if (jsonHandler.isSuccess()) {
                    ViewUtils.toast("保存成功！");
                    Intent intent = new Intent();
                    intent.setAction(FeedListActivity.BC_TEXT);
                    sendBroadcast(intent);
                    finish();
                } else {
                    ViewUtils.toast("保存失败！" + jsonHandler.getMessage());
                }
            }
        });
        request.sendAsync();
    }

    public void setErrorVoice() {
        SoundPool soundPool = new SoundPool(100, AudioManager.STREAM_MUSIC, 0);//构建对象
        final int soundId = soundPool.load(activity, R.raw.error, 1);//加载资源，得到soundId
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPool.play(soundId, 0.6f, 0.6f, 1, 0, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String result = CaptureActivity.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if (result.getContents() == null) {
                //Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            //} else {
                //String contents = result.getContents();
                validateFeed(result);
            //}
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void validateFeed(final String contents) {
        if (rowsFeedNeed != null) {
            addScanKey(contents);
        } else {
            getFeed(new StringCallBack() {
                @Override
                public void onSuccess(String text) {
                    initFeedNeed(text);
                    addScanKey(contents);
                }
            });
        }
    }


    /**
     * 扫码后添加关键字
     * @param contents
     */
    private void addScanKey(String contents) {
        L.i("=========addScanKey contents=============="+contents);
        for (int i = 0; i < rowsFeedNeed.size(); i++) {
            Row rowFeedNeed = rowsFeedNeed.get(i);
            String barcode = rowFeedNeed.getString("barcode");
            L.i("=========addScanKey barcode=============="+barcode);
            if (contents.equals(barcode)) {
                List<Row> rows = dataListView.getRows();
                for (int j = 0; j < rows.size(); j++) {
                    Row row = rows.get(j);
                    if (row.getString("materialId").equals(rowFeedNeed.getString("materialId"))) {
                        //rowObject.putAll(rowFeedNeed);
                        //投料数量默认为需求数据
                        //if (StringUtils.isEmpty(rowObject.getString("displayQty"))) {
                        //    rowObject.put("displayQty", rowObject.getString("displayWorkOrderQty"));
                        //}
                        row.put("action", "create");
                        j = rows.size();
                        i = rowsFeedNeed.size();
                        dataListView.notifyDataSetChanged();
                    }
                }
            }
            //没有匹配到
            L.i("=========addScanKey=============="+i+"    "+rowsFeedNeed.size());
            if(i==rowsFeedNeed.size()-1){
                setErrorVoice();
                tipDialog.show();
            }
        }
    }

    /**
     * 添加条码，生产批号等细节
     */
    private void addFeedsDetail() {
        List<Row> rows = dataListView.getRows();
        L.i("=========addAll rows==============" + rows);
        L.i("=========addAll rowsFeedNeed==============" + rowsFeedNeed);
        for (int i = 0; i < rowsFeedNeed.size(); i++) {
            Row rowFeedNeed = rowsFeedNeed.get(i);
            for (int j = 0; j < rows.size(); j++) {
                Row row = rows.get(j);
                if (row.getString("materialId").equals(rowFeedNeed.getString("materialId"))) {
                    row.putAll(rowFeedNeed);
                    //投料数量默认为需求数据
                    if (StringUtils.isEmpty(row.getString("displayQty"))) {
                        row.put("displayQty", row.getString("displayWorkOrderQty"));
                    }
                    //rowObject.put("action", "create");
                    row.put("unitConvertRatio", "1");
                }
            }
        }
        dataListView.notifyDataSetChanged();
    }

    private void combo() {
        final FrameDialog combo=new FrameDialog(context,R.layout.employee_list);
        final HzDataListView dataListView= (HzDataListView) combo.findViewById(R.id.dataListView);
        handler.setFocusable(false);
        handler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtils.showKeyboard(handler);
                combo.showAsDown(handler);
            }
        });
        combo.setFocuse(false);
        combo.setShadow(false);
        combo.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.setFocusable(false);
                ViewUtils.hideKeyboard(handler);
            }
        });
        combo.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
            }
        });
        handler.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                handler.setFocusable(true);
                handler.setFocusableInTouchMode(true);
                combo.showAsDown(handler);
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(StringUtils.isNotEmpty(s)){
                    dataListView.addTempParam("keyword",s);
                }
                dataListView.refresh();
            }
        });
        dataListView.setOnItemClickListener(new BaseFillAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View convertView, Row row, int position, BaseFillAdapter.ViewHolder holder) {
                String value = row.getString("value");
                handler.setText(value);
                combo.dismiss();
            }
        });
    }

    /**
     * 创建全局提示弹窗
     */
    private TipDialog createTipDialog() {
        TipDialog tipDialog=new TipDialog(context);
        //windowDialog.hideCancleButton();
        tipDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
        tipDialog.setOnSureListener(new WindowTipDialog.OnSureListener() {
            @Override
            public void onSure(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
        //windowDialog.setContentView(ViewUtils.inflatView(context,R.layout.ui_dialog_view));
        tipDialog.getTv_content().setTextColor(Color.RED);
        tipDialog.setText("您选择的投料不正确，请仔细检查一下！");
        return tipDialog;
    }

    @Override
    public void initData() {
        getFeed(new StringCallBack() {
            @Override
            public void onSuccess(String text) {
                initFeedNeed(text);

                dataListView.refresh();

            }
        });
        initHandler();
        tipDialog= createTipDialog();
    }

    /**
     * 初始化投料人
     */
    private void initHandler() {
        combo();

    }
}
