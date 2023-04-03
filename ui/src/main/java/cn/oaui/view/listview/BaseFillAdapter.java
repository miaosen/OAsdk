package cn.oaui.view.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.oaui.UIGlobal;
import cn.oaui.data.Row;
import cn.oaui.form.Form;
import cn.oaui.form.FormUtils;
import cn.oaui.utils.JsonUtils;

/**
 * @Created by com.gzpykj.com
 * @author zms
 * @Date 2015-11-13
 * @Descrition BaseFillListViewAdapter封装,根据布局的id和json中key的字段来填充数据,绑定数据填充模块
 */
public abstract class BaseFillAdapter extends BaseAdapter {

	private Context context;
	// 数据集 List<Map<?,?>>类型
	private List<Row> rows;
	// 布局文件
	private int layout;
	// item点击监听
	private OnItemClickListener onItemClickListener;
	// setItem之前修改item
	private OnItemModifylistenert onItemModifylistenert;

	public BaseFillAdapter(Context context, List<Row> rows,
						   int layout) {
		this.context = context;
		this.rows=rows;
		this.layout = layout;
	}


	public BaseFillAdapter(Context context, int layout) {
		this.context = context;
		this.rows=new LinkedList<Row>();
		this.layout = layout;
	}

	//public BaseFillAdapter(Context context, String jsonStr, int layout) {
	//	this.context = context;
	//	this.rows = JSONSerializer.getRows(jsonStr);
	//	this.layout = layout;
	//}

	public BaseFillAdapter(List<Row> rows, int layout) {
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
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
		final Row row = rows.get(position);
		holder.fillUnit.fill(row);
		if(onItemModifylistenert!=null){
			onItemModifylistenert.onItemModify(convertView,row,position,holder);
		}
		setItem(convertView, row, position, holder);
		if (onItemClickListener != null) {
			final View finalConvertView = convertView;
			convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onItemClickListener.onItemClick(finalConvertView, row, position,holder);
			}
		});

		}
		return convertView;
	}


	/**
	 * 添加数据
	 * 
	 * @param jsonStr
	 */
	public void addJsonData(String jsonStr) {
		List<Row> list = JsonUtils.jsonToRows(jsonStr);
		if (list != null) {
			rows.addAll(list);
			notifyDataSetChanged();
		}
	}

	/**
	 * 添加数据
	 * @param list
	 */
	public void addRows(List<Row> list) {
		if (list != null) {
			rows.addAll(list);
			notifyDataSetChanged();
		}
	}

	/**
	 * 添加数据
	 * @param row
	 */
	public void addRow(Row row) {
		if (row!= null) {
			rows.add(row);
			notifyDataSetChanged();
		}
	}


	/**
	 * 移除数据
	 * @param row
	 */
	public void removeRow(Row row) {
		if (row!= null) {
			rows.remove(row);
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
	 * item 监听接口
	 */
	public interface OnItemClickListener {
		void onItemClick(View convertView, Row row, int position, BaseFillAdapter.ViewHolder holder);
	}

	public interface OnItemModifylistenert {
		void onItemModify(View convertView, Row row, int position, ViewHolder holder);
	}

	/**
	 * item 手动填充，在字段填充完后执行
	 * @param convertView
	 * @param row
	 * @param position
     * @param holder
     */
	public abstract void setItem(View convertView, Row row, int position,
                                 ViewHolder holder);



	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}



	public OnItemClickListener getOnItemClickListener() {
		return onItemClickListener;
	}

	public List<Row> getRows() {
		return rows;
	}

	public void setRows(List<Row> rows) {
		this.rows = rows;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

}
