package com.daveaw.dataviewer.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.daveaw.dataviewer.frame.RawDataStore;

public class RawFileImporter {

	public static RawDataStore importData(File file) {
		RawDataStore data;
		try {
			FileInputStream fis = new FileInputStream(file);
			data = new RawDataStore(fis.readAllBytes());		
			fis.close();
			return data;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
