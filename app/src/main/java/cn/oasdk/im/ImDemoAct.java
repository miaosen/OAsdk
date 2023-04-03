package cn.oasdk.im;

import android.os.Bundle;

import com.gzpykj.im.ImGlobal;
import com.gzpykj.im.UserListActivity;

import cn.oasdk.base.GlobalConst;
import cn.oaui.L;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2021-11-15  01:11
 * @Descrition
 */
public class ImDemoAct extends UserListActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        L.i("============onCreate===========");
        ImGlobal.HOST= GlobalConst.SERVICE_ROOT;
        super.onCreate(savedInstanceState);


    }
}
