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
	
	
	private Row row;

	private LinkedList<Row> rows;
	
	public SerializableRowObject(Row row){
		setRow(row);
	}

	public SerializableRowObject(LinkedList<Row> rows ){
		setRows(rows);
	}


	public Row getRow() {
		return row;
	}

	public void setRow(Row row) {
		this.row = row;
	}

	public LinkedList<Row> getRows() {
		return rows;
	}

	public void setRows(LinkedList<Row> rows) {
		this.rows = rows;
	}
}