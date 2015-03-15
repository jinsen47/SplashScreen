package com.example.splashscreen.adapter;

import java.io.File;

import android.content.Context;
import android.os.Environment;

public class DataCleanManager {
	/**
	 * 清除内部缓存(/data/data/com.xxx.xxx/cache)
	 * @param context
	 */
	public static void cleanInternalCache(Context context){
		deleteFilesByDirectory(context.getCacheDir());
	}
	/**
	 * 清除内部数据库(/data/data/com.xxx.xxx/databases)
	 * @param context
	 */
	public static void cleanDatabases(Context context){
		deleteFilesByDirectory(new File("/data/data/"+
				context.getPackageName()+"/databases"));
	}
	/**
	 * 清除sharedpreference(/data/data/com.xxx.xxx/shared_prefs)
	 * @param context
	 */
	public static void cleanSharedPreference(Context context){
		deleteFilesByDirectory(new File("/data/data"+
				context.getPackageName()+"/shared_prefs"));
	}
	/**
	 * 按名字删除本应用下数据库/data/data/com.xxx.xxx/databases
	 * @param context
	 * @param name
	 */
	public static void cleanDatabasesByName(Context context, String dbName){
		context.deleteDatabase(dbName);
	}
	/**
	 *  删除本应用下文件/data/data/com.xxx.xxx/files
	 * @param context
	 */
	public static void cleanFiles(Context context){
		deleteFilesByDirectory(context.getFilesDir());
	}
	/**
	 * 清除外部cache的内容(/mnt/sdcard/data/com.xxx.xxx/cache)
	 * @param context
	 */
	public static void cleanExternalCache(Context context){
		if(Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)){
			deleteFilesByDirectory(context.getExternalCacheDir());
		}
	}
	/**
	 * 清除自定义路径下的文件,不要误删，只支持目录下文件的删除
	 */
	public static void cleanCustomCache(String filepath){
		deleteFilesByDirectory(new File(filepath));
	}
	/**
	 * 清除本应用下所有数据
	 * @param context
	 * @param filepath
	 */
	//public static void cleanApplicationData(Context context,String...filepath){
	public static void cleanApplicationData(Context context){
		cleanInternalCache(context);
		cleanDatabases(context);
		/*cleanExternalCache(context);
		cleanSharedPreference(context);
		cleanFiles(context);
		for(String filePath: filepath){
			cleanCustomCache(filePath);
		}
		*/
	}
	/**
	 * 只删除某个目录下文件，如果传入的是文件将不处理
	 * @param directory
	 */
	private static void deleteFilesByDirectory(File directory){
		if(directory!=null && directory.exists() && directory.isDirectory()){
			for(File item: directory.listFiles()){
				item.delete();
			}
		}
	}
	
	
	
}
