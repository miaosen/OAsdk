package com.gzpykj.im;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import cn.oahttp.HttpRequest;
import cn.oahttp.callback.StringCallBack;
import cn.oaui.annotation.InjectReader;
import cn.oaui.annotation.ViewInject;
import cn.oaui.data.Row;
import cn.oaui.utils.JsonUtils;
import cn.oaui.utils.StringUtils;
import cn.oaui.view.listview.BaseFillAdapter;
import okhttp3.Headers;


public class UserListActivity extends Activity {

	@ViewInject
	private ListView listview;

	/// 数据内容哦
	List<Map<String, Object>> m_listData =new ArrayList<Map<String, Object>>();

	/// 列表数据源
	BaseFillAdapter baseFillAdapter;


	/// 当前Activity的sId
	private String m_sId = null;

	/// 数据的互斥锁
	private Lock m_updateLock = new ReentrantLock();

	@ViewInject
    TextView tv_title;

	Context context;

	boolean alive=true;



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.im_userlist);
		context=this;
		InjectReader.injectAllFields(this);
		// 设置数据内容
		m_listData = getData();
		baseFillAdapter=new BaseFillAdapter(context,R.layout.im_userlist_item) {
			@Override
			public void setItem(View view, Row row, int i, ViewHolder viewHolder) {
				if("4".equals(row.getString("talktype"))){

				}
			}
		};
		listview.setAdapter(baseFillAdapter);
		talk();
	}

	private void talk() {
		HttpRequest request=ImGlobal.createRequest("/actions/imTalkAction/talk");
		request.addParam("talkType","1");
		request.addParam("msg","");
		request.addParam("talkTo","{}");
		request.setCallback(new StringCallBack() {
			@Override
			public void onSuccess(String s) {
				if(JsonUtils.isCanToRow(s)){
					Row row=JsonUtils.jsonToRow(s);
					Row rowData = row.getRow("data");
					LinkedList<Row> rowsUserlist = rowData.getRows("userlist");
					baseFillAdapter.clearData();
					baseFillAdapter.addRows(rowsUserlist);
					listview.setAdapter(baseFillAdapter);
					LinkedList<Row> rowsTalklist = rowData.getRows("talklist");
					if(rowsTalklist!=null&&rowsTalklist.size()>0){
						for (int i = 0; i < rowsTalklist.size(); i++) {
							Row row1 = rowsTalklist.get(i);
							String msg = row1.getString("msg");
							String talkType = row1.getString("talkType");
							if("4".equals(talkType)&& StringUtils.isNotEmpty(msg)){
								String url = ImGlobal.RTMP_HOST + UUID.randomUUID().toString().replaceAll("-", "");
								Intent intent = new Intent(UserListActivity.this, VideoTalkActivity.class);
								intent.putExtra("service_url", url);
								intent.putExtra("other_name", row1.getString("user_name"));
								intent.putExtra("other_id", row1.getString("user_id"));
								intent.putExtra("client_url", msg);
								intent.putExtra("callOrAnswer", "video_answer_");
								intent.putExtra("talkType", talkType);
								startActivity(intent);
							}
						}
					}
				}
				if(alive){
					new Handler().postDelayed(new Runnable() {
						public void run() {
							//要执行的任务
							talk();
						}
					}, 3000);
				}

			}

			@Override
			protected void onFail(Exception e) {
				super.onFail(e);
				e.printStackTrace();
				Headers headers = request.getRequest().headers();
			}
		});
		request.sendAsync();

	}

	/**
	 * Activity销毁的重载，注销当前Subsciber
	 * */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		alive=false;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	/// 辅助方法，列表加入数据
	private List<Map<String, Object>> getData() {
		Log.i("TalkingListActivity","getData()");
		List<Map<String, Object>> list= new ArrayList<Map<String, Object>>();
		return list;
	}

	/// 辅助方法，列表加入数据
	private Map<String, Object> addRowValue(int image, String name, String sTime, String login_state){
		Map<String, Object> map1 =new HashMap<String, Object>();
		map1.put("image", image);
		map1.put("name",name);
		map1.put("group",sTime);
		map1.put("login_state",login_state);
		return map1;
	}


	/**
	 * 默认的菜单重载方法
	 * */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/// 刷新列表的消息
	private static final int m_updataview = 1;

}