package cn.oaui.data;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * @Created by com.gzpykj.com
 * @author zms
 * @Date 2015-12-18
 * @Descrition 解决RowObject在用intent传递时变成HashMap的问题
 */
public class SerializableRowObject implements Serializable {
	
	
	private static final long serialVersionUID = 1L;
	
	
	private RowObject rowObject;

	private LinkedList<RowObject> rows;
	
	public SerializableRowObject(RowObject rowObject){
		setRowObject(rowObject);
	}

	public SerializableRowObject(LinkedList<RowObject> rows ){
		setRows(rows);
	}


	public RowObject getRowObject() {
		return rowObject;
	}

	public void setRowObject(RowObject rowObject) {
		this.rowObject = rowObject;
	}

	public LinkedList<RowObject> getRows() {
		return rows;
	}

	public void setRows(LinkedList<RowObject> rows) {
		this.rows = rows;
	}
}