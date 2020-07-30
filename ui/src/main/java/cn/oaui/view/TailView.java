package cn.oaui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import cn.oaui.data.Row;
import cn.oaui.view.listview.BaseFillAdapter;
import cn.oaui.view.listview.LinearLayoutForListView;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TailView extends LinearLayoutForListView {
	
	
	BaseFillAdapter fillListViewAdapter;
	List<Row> rows=new LinkedList<Row>();

	String strValueText="";
	
	Map<String ,OnTailItemListener> onTailItemListeners=new LinkedHashMap<String ,OnTailItemListener>();

	int itemLayoutId= cn.oaui.R.layout.ui_tailview_item;
	

	public TailView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(HORIZONTAL);
		if(!isInEditMode()){
			readAttr(attrs);
			initAdp();
		}
	}

	private void readAttr(AttributeSet attrs) {
		TypedArray ta = getContext().obtainStyledAttributes(attrs, cn.oaui.R.styleable.TailView);
		strValueText = ta.getString(cn.oaui.R.styleable.TailView_valueText)+"";
		ta.recycle();
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if(!isInEditMode()){
			refresh();
		}
	}

	private void refresh() {
		rows.clear();
		String[] split = strValueText.split(",");
		for (int i = 0; i < split.length; i++) {
			Row row=new Row();
			row.put("name", split[i]);
			rows.add(row);
		}
		//setAdapter(fillListViewAdapter);
		fillListViewAdapter.notifyDataSetChanged();
	}

	public void setValueText(String strValueText){
		this.strValueText=strValueText;
		refresh();
	}
	
	
	private void initAdp() {
		fillListViewAdapter=new BaseFillAdapter(getContext(),rows,itemLayoutId) {
			@Override
			public void setItem(View convertView, Row row, int position, ViewHolder holder) {

			}
		};
		fillListViewAdapter.setOnItemClickListener(new BaseFillAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(View convertView, Row row, int position, BaseFillAdapter.ViewHolder holder) {
				String name=row.getString("name");
				if(onTailItemListeners.containsKey(name)){
					onTailItemListeners.get(name).onClickItem();
				}
			}
		});
		setAdapter(fillListViewAdapter);
	}
	
	public void addTailItemListener(String name, OnTailItemListener onTailItemListener) {
		onTailItemListeners.put(name, onTailItemListener);
	}
	

	public interface OnTailItemListener{
		void onClickItem();
	}


	@Override
	public void onItemLayoutAttached(View v, int i) {
		super.onItemLayoutAttached(v, i);
		v.setLayoutParams(new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT, 1));
		//v.setBackgroundColor(Color.parseColor("#ffffff"));
	}
}
