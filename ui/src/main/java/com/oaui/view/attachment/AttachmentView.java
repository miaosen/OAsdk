package com.oaui.view.attachment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.oaui.IntentFactory;
import com.oaui.R;
import com.oaui.annotation.InjectReader;
import com.oaui.annotation.ViewInject;
import com.oaui.data.RowObject;
import com.oaui.utils.BitmapUtils;
import com.oaui.utils.DateTimeUtils;
import com.oaui.utils.FileUtils;
import com.oaui.view.CustomLayout;
import com.oaui.view.FlowLayout;
import com.oaui.view.TempFragment;
import com.oaui.view.dialog.FrameDialog;
import com.oaui.view.signature.SignatureView;
import com.oaui.view.voicerecord.VoiceRecordView;

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


    List<RowObject> attachmentList = new LinkedList<RowObject>();

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
               RowObject rowObject = IntentFactory.onActivityResult(requestCode, resultCode, data);
                if(rowObject!=null){
                    attachmentList.add(rowObject);
                    attachmentAdp.notifyDataSetChanged();
                }
            }
        });
        //录音
        VoiceRecordView voiceRecordView = new VoiceRecordView(context);
        dlgVoice = new FrameDialog(context, voiceRecordView);
        voiceRecordView.setOnRecordComplateListener(new VoiceRecordView.OnRecordComplateListener() {
            @Override
            public void onComplate(RowObject result) {
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
                String path=FileUtils.getAppDirPath()+"/"+ DateTimeUtils.getCurrentTimeMillis()+".png";
                BitmapUtils.saveBitmapToPathAsPng(bitmap,path);
                RowObject result=new RowObject();
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

    public List<RowObject> getAttachmentList() {
        return attachmentList;
    }

    public void setAttachmentList(List<RowObject> attachmentList) {
        this.attachmentList = attachmentList;
    }

    public AttachmentAdp getAttachmentAdp() {
        return attachmentAdp;
    }

    public void setAttachmentAdp(AttachmentAdp attachmentAdp) {
        this.attachmentAdp = attachmentAdp;
    }

    public List<RowObject> getData() {
        return attachmentAdp.getRows();
    }

    public void  setData(List<RowObject> rows) {
        attachmentAdp.addRows(rows);
    }
}
