package com.daveaw.dataviewer.frame;

import java.util.ArrayList;

public class DataFrameStorage {

	protected ArrayList<DataFrame> data = new ArrayList<DataFrame>();
	protected int max_length = 0;
	protected int count = 0;
	
	public int getCount() {
		return count;
	}
	
	public int getMaxLength() {
		return max_length;
	}
	
	public ArrayList<DataFrame> storage() {
		return data;
	}
	
	public int getMaxLengthFrameFromRange(int start, int end) {
		int max = 0;
		for (int i = start; i < end; i++) {
			if (data.get(i).count() > max)
				max = data.get(i).count();
		}
		return max;
	}
	
	public int getMaxLengthFrameFromNumber(int start) {
		return getMaxLengthFrameFromRange(start, data.size());
	}
	
}
