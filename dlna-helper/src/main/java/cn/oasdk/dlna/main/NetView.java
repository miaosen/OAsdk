package cn.oasdk.dlna.main;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.oasdk.dlna.R;
import cn.oasdk.dlna.dms.MediaServer;
import cn.oaui.annotation.ViewInject;
import cn.oaui.data.RowObject;
import cn.oaui.form.FormUtils;
import cn.oaui.utils.SPUtils;
import cn.oaui.utils.StringUtils;
import cn.oaui.utils.ViewUtils;
import cn.oaui.view.dialog.FrameDialog;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2019-08-08  15:15
 * @Descrition
 */

public class NetView  extends FileView {


    @ViewInject
    TextView tv_book, tv_net;

    @ViewInject
    LinearLayout ln_edit;


    FrameDialog fdl_net;
    View btn_sure, btn_cancle;

    public NetView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NetView(Context context) {
        super(context);
    }

    @Override
    protected void onCreateView() {
        super.onCreateView();
        ln_edit.setVisibility(VISIBLE);
        fdl_net = new FrameDialog(context, R.layout.dlg_net_edit);
        btn_sure = fdl_net.findViewById(R.id.btn_sure);
        btn_cancle = fdl_net.findViewById(R.id.btn_cancle);
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RowObject contentRow = FormUtils.getContentRow(fdl_net);
                contentRow.put("type", "net");
                contentRow.put("title", contentRow.getString("name"));
                if(StringUtils.isEmpty(contentRow.getString("name"))||
                        StringUtils.isEmpty(contentRow.getString("name")) ){
                    ViewUtils.toast("名称或者链接不能为空");
                }else{
                    MediaServer.rowsNet.add(contentRow);
                    SPUtils.saveRows(MediaServer.FILE_TYPE.NET, "url_list", MediaServer.rowsNet);
                    fdl_net.dismiss();
                }
            }
        });
        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fdl_net.dismiss();
            }
        });

        tv_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fdl_net.fullscreen();
            }
        });
    }
}
