package com.daveaw.dataviewer.frame;

public class DataFrame {

	private byte[] data; // Integer array storing data in frame
	private int length; // Length of data frame in bits
	private int number;
	
	public DataFrame(byte[] data, int length) {
		this.data = data;
		this.length = length;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public int getBitCount() {
		return length;
	}
	
	public int getByteCount() {
		return data.length;
	}
	
	public int getNumber() {
		return number;
	}
	
}
