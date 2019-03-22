package gzpykj.hzpqy.base;

import android.content.Context;
import android.util.AttributeSet;

import com.oaui.view.listview.DataListView;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2018-06-15  09:48
 * @Descrition
 */

public class HzDataListView extends DataListView {


    public HzDataListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //setUrl(Global.HOST);

    }

    public HzDataListView(Context context) {
        super(context);
    }
}
