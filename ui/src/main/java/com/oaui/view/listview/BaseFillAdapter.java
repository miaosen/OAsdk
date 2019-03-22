package com.oaui.view.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.oaui.UIGlobal;
import com.oaui.data.RowObject;
import com.oaui.form.Form;
import com.oaui.form.FormUtils;
import com.oaui.utils.JsonUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Created by com.gzpykj.com
 * @author zms
 * @Date 2015-11-13
 * @Descrition BaseFillListViewAdapter封装,根据布局的id和json中key的字段来填充数据,绑定数据填充模块
 */
public abstract class BaseFillAdapter extends BaseAdapter {

	private Context context;
	// 数据集 List<Map<?,?>>类型
	private List<RowObject> rows;
	// 布局文件
	private int layout;
	// item点击监听
	private OnItemClickListener onItemClickListener;
	// setItem之前修改item
	private OnItemModifylistenert onItemModifylistenert;

	public BaseFillAdapter(Context context, List<RowObject> rows,
						   int layout) {
		this.context = context;
		this.rows=rows;
		this.layout = layout;
	}

	//public BaseFillAdapter(Context context, String jsonStr, int layout) {
	//	this.context = context;
	//	this.rows = JSONSerializer.getRows(jsonStr);
	//	this.layout = layout;
	//}

	public BaseFillAdapter(List<RowObject> rows, int layout) {
		this.context = UIGlobal.getApplication();
		this.rows = rows;
		this.layout = layout;
	}

	public BaseFillAdapter(String jsonStr, int layout) {
		super();
		this.context =  UIGlobal.getApplication();
		this.rows = JsonUtils.jsonToRows(jsonStr);
		this.layout = layout;
	}

	@Override
	public int getCount() {
		if (rows == null) {
			return 0;
		}
		return rows.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(layout, null);
			Map<String, View> allChildViewWithId = FormUtils.getMapView(convertView);
			holder.views=allChildViewWithId;
			holder.fillUnit=new Form(allChildViewWithId);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		//数据填充
		RowObject row = rows.get(position);
		holder.fillUnit.fill(row);
		if(onItemModifylistenert!=null){
			onItemModifylistenert.onItemModify(convertView,row,position,holder);
		}
		setItem(convertView, row, position, holder);
		convertView.setOnClickListener(new mClick(convertView, row, position));
		return convertView;
	}


	/**
	 * 添加数据
	 * 
	 * @param jsonStr
	 */
	public void addJsonData(String jsonStr) {
		List<RowObject> list = JsonUtils.jsonToRows(jsonStr);
		if (list != null) {
			rows.addAll(list);
			notifyDataSetChanged();
		}
	}

	/**
	 * 添加数据
	 * @param list
	 */
	public void addRows(List<RowObject> list) {
		if (list != null) {
			rows.addAll(list);
			notifyDataSetChanged();
		}
	}

	/**
	 * 添加数据
	 * @param row
	 */
	public void addRow(RowObject row) {
		if (row!= null) {
			rows.add(row);
			notifyDataSetChanged();
		}
	}

	/**
	 * 清除数据
	 */
	public void clearData() {
		if (rows != null) {
			rows.clear();
			notifyDataSetChanged();
		}
	}

	/**
	 * ItemView初始化
	 */
	public class ViewHolder {
		/**
		 * 所有带id的View
		 */
		public Map<String, View> views = new HashMap<String, View>();

		public Object object;

		public Form fillUnit;

	}


	/**
	 * item监听
	 *
	 */
	class mClick implements OnClickListener {
		private View convertView;
		private RowObject row;
		private int position;
		/**
		 * 回调 item 监听接口
		 * @param position
		 */
		public mClick(View convertView, RowObject row, int position) {
			this.convertView = convertView;
			this.row = row;
			this.position = position;
		}
		@Override
		public void onClick(View v) {
			if (onItemClickListener != null) {
				onItemClickListener.onItemClick(convertView, row, position);
			}
		}
	}

	/**
	 * item 监听接口
	 */
	public interface OnItemClickListener {
		void onItemClick(View convertView, RowObject row, int position);
	}

	public interface OnItemModifylistenert {
		void onItemModify(View convertView, RowObject row, int position,ViewHolder holder);
	}

	/**
	 * item 手动填充，在字段填充完后执行
	 * @param convertView
	 * @param row
	 * @param position
     * @param holder
     */
	public abstract void setItem(View convertView, RowObject row, int position,
			 ViewHolder holder);



	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	public OnItemClickListener getOnItemClickListener() {
		return onItemClickListener;
	}

	public List<RowObject> getRows() {
		return rows;
	}

	public void setRows(List<RowObject> rows) {
		this.rows = rows;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

}
