package cn.oasdk.fileview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

import cn.oasdk.fileview.R;
import cn.oaui.L;
import cn.oaui.annotation.InjectReader;
import cn.oaui.annotation.ViewInject;
import cn.oaui.utils.FileUtils;
import cn.oaui.utils.ViewUtils;
import cn.oaui.view.CustomLayout;

public class FilePathView extends CustomLayout {

    @ViewInject
    LinearLayout ln_path;

    public FilePathView(Context context) {
        super(context);
    }

    public FilePathView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onCreateView() {
        InjectReader.injectAllFields(this);
    }

    @Override
    public int setXmlLayout() {
        return R.layout.file_path_view;
    }

    public void setPath(String curCollectDir) {
        ln_path.removeAllViews();
        L.i("======setPath===== "+curCollectDir);
        L.i("======setPath===== "+FileUtils.getSDCardPath());
        curCollectDir= curCollectDir.replace(FileUtils.getSDCardPath(),"存储");
        String[] split = curCollectDir.split(File.separator);
        for (int i = 0; i < split.length; i++) {
            String name = split[i];
            View view = ViewUtils.inflatView(getContext(), R.layout.file_path_item);
            TextView tv=view.findViewById(R.id.name);
            tv.setText(name);
            ln_path.addView(view);
        }
    }
}
