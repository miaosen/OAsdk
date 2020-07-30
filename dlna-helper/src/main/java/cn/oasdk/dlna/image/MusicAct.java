package cn.oasdk.dlna.image;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import cn.oasdk.dlna.BaseFileAct;
import cn.oasdk.dlna.R;
import cn.oasdk.dlna.dms.FileServer;
import cn.oaui.L;
import cn.oaui.data.Row;
import cn.oaui.utils.IntentUtils;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2019-10-25  11:29
 * @Descrition
 */

public class MusicAct extends BaseFileAct {




    @Override
    public void initConfig() {

    }

    @Override
    public int getContentView() {
        return R.layout.image_act;
    }

    @Override
    public void onViewCreate() {
        super.onViewCreate();
    }

    @Override
    protected void onListItemClick(View convertView, Row row, int position) {
        Intent intent = new Intent(context, ShowImageAct.class);
        IntentUtils.addRows(intent,dataListView.getRows(),ShowImageAct.ROW_KEY);
        intent.putExtra(ShowImageAct.IMG_INDEX,position);
        startActivity(intent);
    }

    @Override
    public void initData() {
        rows=FileServer.rowsMusic;
        super.initData();
         L.i("============initData==========="+listFilePath);

    }

    @Override
    public void setFileIcon(String filePath, ImageView img_file_type) {
        if(filePath.endsWith(".mp3")||filePath.endsWith(".docx")){
            img_file_type.setImageDrawable(getResources().getDrawable(R.mipmap.icon_word));
        }else if(filePath.endsWith(".xls")||filePath.endsWith(".xlsx")){
            img_file_type.setImageDrawable(getResources().getDrawable(R.mipmap.icon_excel));
        }else if(filePath.endsWith(".pdf")){
            img_file_type.setImageDrawable(getResources().getDrawable(R.mipmap.icon_pdf));
        }else{
            img_file_type.setImageDrawable(getResources().getDrawable(R.mipmap.icon_file));
        }
    }


}
