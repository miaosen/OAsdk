package cn.oaui.data;

import java.io.Serializable;

/**
 * @Created by com.gzpykj.com
 * @author zms
 * @Date 2015-12-18
 * @Descrition 解决RowObject在用intent传递时变成HashMap的问题
 */
public class SerializableRowObject implements Serializable {
	
	
	private static final long serialVersionUID = 1L;
	
	
	private RowObject rowObject;
	
	public SerializableRowObject(RowObject rowObject){
		setRowObject(rowObject);
	}

	public RowObject getRowObject() {
		return rowObject;
	}

	public void setRowObject(RowObject rowObject) {
		this.rowObject = rowObject;
	}

}