package com.oaui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.oaui.L;
import com.oaui.R;
import com.oaui.data.RowObject;
import com.oaui.view.listview.BaseFillAdapter;
import com.oaui.view.listview.LinearLayoutForListView;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TailView extends LinearLayoutForListView{
	
	
	BaseFillAdapter fillListViewAdapter;
	List<RowObject> rows=new LinkedList<RowObject>();

	String strValueText="";
	
	Map<String ,OnTailItemListener> onTailItemListeners=new LinkedHashMap<String ,OnTailItemListener>();

	int itemLayoutId=R.layout.ui_tailview_item;
	

	public TailView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if(!isInEditMode()){
			readAttr(attrs);
			initAdp();
		}
	}

	private void readAttr(AttributeSet attrs) {
		TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.TailView);
		strValueText = ta.getString(R.styleable.TailView_valueText)+"";
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
			RowObject row=new RowObject();
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
			public void setItem(View convertView, RowObject row, int position, ViewHolder holder) {

			}
		};
		fillListViewAdapter.setOnItemClickListener(new BaseFillAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(View convertView, RowObject row, int position) {
				String name=row.getString("name");
				L.i("=========onItemClick=============="+name);
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
