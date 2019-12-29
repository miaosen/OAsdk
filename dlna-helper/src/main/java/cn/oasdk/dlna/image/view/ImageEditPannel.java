package cn.oasdk.dlna.image.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import cn.oasdk.dlna.R;
import cn.oaui.L;
import cn.oaui.annotation.InjectReader;
import cn.oaui.annotation.ViewInject;
import cn.oaui.view.CustomLayout;
import cn.oaui.view.dialog.FrameDialog;
import cn.oaui.view.signature.SignatureView;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2019-12-04  10:22
 * @Descrition
 */
public class ImageEditPannel extends CustomLayout {

    @ViewInject
    ImageView img_edit_pannel;

    FrameDialog dlgSign;
    SignatureView signatureView;

    public ImageEditPannel(Context context) {
        super(context);
    }

    public ImageEditPannel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



    @Override
    protected void onCreateView() {
        InjectReader.injectAllFields(this);
        img_edit_pannel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                dlgSign.fullscreen();
            }
        });
        signatureView = new SignatureView(getContext());
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
        signatureView.setBackgroundColor(Color.parseColor("#00000000"));
        signatureView.getHuaBan().setBgColor(Color.parseColor("#00000000"));
        L.i("============onCreateView==========="+Color.parseColor("#00000000"));
        signatureView.getHeaderView().setVisibility(GONE);
        dlgSign = new FrameDialog(signatureView);
        dlgSign.setBackgroundColor(Color.parseColor("#00000000"));

    }

    @Override
    public void initData() {

    }

    @Override
    public int setXmlLayout() {
        return R.layout.view_image_edit_pannel;
    }
}
