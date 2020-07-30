package cn.oasdk.dlna.image;

import android.content.Intent;
import android.view.View;

import cn.oasdk.dlna.BaseFileAct;
import cn.oasdk.dlna.R;
import cn.oasdk.dlna.dms.FileServer;
import cn.oasdk.dlna.tbs.TBSVideoPlayerAct;
import cn.oaui.L;
import cn.oaui.data.Row;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2019-10-25  11:29
 * @Descrition
 */

public class VideoAct extends BaseFileAct {


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

        //dataListView.setOnLongClickListener(new View.OnLongClickListener() {
        //    @Override
        //    public boolean onLongClick(View v) {
        //        L.i("============onLongClick===========");
        //        return true;
        //    }
        //});
    }

    @Override
    protected void onListItemClick(View convertView, Row row, int position) {
        //if(TbsVideo.canUseTbsPlayer(context)){
        //    Bundle data = new Bundle();
        //    //全屏
        //    data.putBoolean("standardFullScreen", true);
        //    TbsVideo.openVideo(context,row.getString("filePath"),data);
        //}else{
        Intent intent = new Intent(context, TBSVideoPlayerAct.class);
        L.i("============onItemClick===========" + row.getString("filePath"));
        intent.putExtra("filePath", row.getString("filePath"));
        startActivity(intent);
        //}
        //QbSdk.openFileReader(context, row.getString("filePath"), null, new ValueCallback<String>() {
        //    @Override
        //    public void onReceiveValue(String s) {
        //        L.i("============onReceiveValue==========="+s);
        //    }
        //});
    }

    @Override
    public void initData() {
        rows = FileServer.rowsVideo;
        super.initData();
        //L.i("============initData==========="+listFilePath);

    }


}
