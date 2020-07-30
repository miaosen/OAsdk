package cn.oaui.view.attachment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import cn.oaui.IntentFactory;
import cn.oaui.R;
import cn.oaui.annotation.InjectReader;
import cn.oaui.annotation.ViewInject;
import cn.oaui.data.Row;
import cn.oaui.utils.BitmapUtils;
import cn.oaui.utils.DateTimeUtils;
import cn.oaui.utils.FileUtils;
import cn.oaui.view.CustomLayout;
import cn.oaui.view.FlowLayout;
import cn.oaui.view.TempFragment;
import cn.oaui.view.dialog.FrameDialog;
import cn.oaui.view.signature.SignatureView;
import cn.oaui.view.voicerecord.VoiceRecordView;

import java.util.LinkedList;
import java.util.List;


/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2018-03-07  14:51
 * @Descrition
 */

public class AttachmentView extends CustomLayout implements View.OnClickListener {

    @ViewInject
    FlowLayout flowLayout;
    AttachmentAdp attachmentAdp;
    @ViewInject
    ImageView btn_video, btn_photo, btn_voice, add_local_btn,btn_sgin;
    TempFragment tempFragmentView;

    FrameDialog dlgVoice, dlgSign;


    List<Row> attachmentList = new LinkedList<Row>();

    public AttachmentView(Context context) {
        super(context);
    }

    public AttachmentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void initData() {
        attachmentAdp = new AttachmentAdp(getContext(), attachmentList, R.layout.ui_view_attachmentview_item);
        flowLayout.setAdapter(attachmentAdp);
    }

    @Override
    protected void onCreateView() {
        InjectReader.injectAllFields(this);
        btn_video.setOnClickListener(this);
        btn_photo.setOnClickListener(this);
        add_local_btn.setOnClickListener(this);
        btn_voice.setOnClickListener(this);
        tempFragmentView=TempFragment.addHideFragment((Activity) context,"attachment");
        tempFragmentView.setOnActivityResultListener(new TempFragment.OnActivityResultListener() {
            @Override
            public void onActivityResult(int requestCode, int resultCode, Intent data) {
               Row row = IntentFactory.onActivityResult(requestCode, resultCode, data);
                if(row !=null){
                    attachmentList.add(row);
                    attachmentAdp.notifyDataSetChanged();
                }
            }
        });
        //录音
        VoiceRecordView voiceRecordView = new VoiceRecordView(context);
        dlgVoice = new FrameDialog(context, voiceRecordView);
        voiceRecordView.setOnRecordComplateListener(new VoiceRecordView.OnRecordComplateListener() {
            @Override
            public void onComplate(Row result) {
                dlgVoice.dismiss();
                if(result!=null){
                    attachmentList.add(result);
                    attachmentAdp.notifyDataSetChanged();
                }
            }
        });
        btn_sgin.setOnClickListener(this);
        final SignatureView signatureView = new SignatureView(getContext());
        dlgSign = new FrameDialog(signatureView);
        signatureView.setOnSureListener(new SignatureView.OnActionListener() {
            @Override
            public void onSure(Bitmap bitmap) {
                String path= FileUtils.getAppDirPath()+"/"+ DateTimeUtils.getCurrentTimeMillis()+".png";
                BitmapUtils.saveBitmapToPathAsPng(bitmap,path);
                Row result=new Row();
                result.put("path",path);
                result.put("type","sign");
                attachmentList.add(result);
                attachmentAdp.notifyDataSetChanged();
                signatureView.clear();
                dlgSign.dismiss();
            }
            @Override
            public void onCancle() {
                dlgSign.dismiss();
            }
        });
    }




    @Override
    public int setXmlLayout() {
        return R.layout.ui_view_attachmentview;
    }

    @Override
    public void onClick(View v) {
        if (v == btn_video) {
            IntentFactory.movieRecord(tempFragmentView);
        } else if (v == btn_photo) {
            IntentFactory.takePicture(tempFragmentView);
        } else if (v == btn_voice) {
            dlgVoice.show();
        } else if (v == btn_sgin) {
            dlgSign.fullscreen();
        }else if (v == add_local_btn) {
          IntentFactory.openFileExplore(tempFragmentView);
        }
    }

    public List<Row> getAttachmentList() {
        return attachmentList;
    }

    public void setAttachmentList(List<Row> attachmentList) {
        this.attachmentList = attachmentList;
    }

    public AttachmentAdp getAttachmentAdp() {
        return attachmentAdp;
    }

    public void setAttachmentAdp(AttachmentAdp attachmentAdp) {
        this.attachmentAdp = attachmentAdp;
    }

    public List<Row> getData() {
        return attachmentAdp.getRows();
    }

    public void  setData(List<Row> rows) {
        attachmentAdp.addRows(rows);
    }
}
