package com.example.splashscreen.adapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class MyReader {
	
	public static String getString(InputStream inputStream) {
		InputStreamReader inputStreamReader = null;
		try {
			//inputStreamReader = new InputStreamReader(inputStream, "gbk");//utf-8,gb2312
			inputStreamReader=new InputStreamReader(inputStream,"utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		//8192(8k) is the size of the buffer in characters.
		//避免 Default buffer size used in BufferedReader constructor. 
		//It would be better to be explicit if an 8k-char buffer is required.
		BufferedReader reader = new BufferedReader(inputStreamReader,8192);
		StringBuffer sb = new StringBuffer("");//无论sb如何添加字符，指向的引用不变
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	public static String getString(String filepath) {
		File file = new File(filepath);
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return getString(fileInputStream);
	}
}

