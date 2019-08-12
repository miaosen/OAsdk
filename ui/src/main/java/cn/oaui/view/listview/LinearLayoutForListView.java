package cn.oaui.view.listview;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

/**
 * @Created by com.gzpykj.com
 * @author zms
 * @Date 2016年4月8日
 * @Descrition 解决ScrollView嵌套ListView ListView重复刷新的问题,数据量较少时推荐使用
 * 
 */
public class LinearLayoutForListView extends LinearLayout {

	private Context context;
	private OnClickListener onClickListener = null;

	BaseAdapter adapter;

	public LinearLayoutForListView(Context context) {
		super(context);
	}

	// 在xml定义时必须引用AttributeSet参数
	public LinearLayoutForListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		setOrientation(VERTICAL);
	}


	public void setAdapter(BaseAdapter adapter) {
		if (this.adapter == null) {
			adapter.registerDataSetObserver(mObserver);
		}
		this.adapter = adapter;
		addAllView();
	}


	//使notifyDataSetChanged生效
	private DataSetObserver mObserver = new DataSetObserver() {
		public void onChanged() {
			addAllView();
		}
	};

	private void addAllView() {
		int count = adapter.getCount();

		this.removeAllViews();
		for (int i = 0; i < count; i++) {
			View v = adapter.getView(i, null, null);
			if(onClickListener!=null){
				v.setOnClickListener(this.onClickListener);
			}
			v.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			addView(v, i);
			onItemLayoutAttached(v,i);
		}
	}


	public void onItemLayoutAttached(View v, int i){

	}

	public BaseAdapter getAdapter() {
		return adapter;
	}
}
