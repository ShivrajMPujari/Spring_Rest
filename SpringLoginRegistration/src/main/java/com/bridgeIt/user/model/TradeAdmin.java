package com.bridgeIt.user.model;

import java.io.Serializable;
import java.util.Arrays;

public class TradeAdmin implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id;
	private String name;
	private byte [] data;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "TradeAdmin [id=" + id + ", name=" + name + ", data=" + Arrays.toString(data) + "]";
	}
	
}
