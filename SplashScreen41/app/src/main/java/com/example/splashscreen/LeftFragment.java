package com.example.splashscreen;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.splashscreen.adapter.MyConfig;
import com.example.splashscreen.adapter.MySimpleAdapter;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

public class LeftFragment extends ListFragment {
	public static String task_no="";
	public static String cons_no="";
	private String[] consdata=new String[8];//实例化数组
	private String[] queryColumns={"_id","cons_no","cons_name","cons_address","rtua",
			"meter_code","faultCode","longitude","latitude"};
	
	//String[] consdata;只是声明数组
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.leftfragment, container, true);
		//不用listFragment默认的fragment格式,自定义fragment布局
		//只要listfragment对应的listview android:id="@id/android:list"即可
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		Bundle extras=getActivity().getIntent().getExtras();
		if(extras!=null){
			task_no=extras.getString("task_no");
			cons_no=extras.getString("cons_no");
		}	
		consdata=queryDatabase();
	  ArrayList<HashMap<String, Object>> mlist=new ArrayList<HashMap<String,Object>>();
		for(int i=0;i<constitles.length;i++){
			HashMap<String, Object>map=new HashMap<String, Object>();
			map.put("constitles", constitles[i]);
			map.put("consdata", consdata[i]);
			mlist.add(map);     
		} 
		   
		ListAdapter madapter=new MySimpleAdapter(getActivity(), mlist, 
				R.layout.fragconsitems, new String[]{"constitles","consdata"}, 
				new int[]{R.id.constitles,R.id.consdata});
		setListAdapter(madapter);
	}
	String[] queryDatabase(){
		String[] consdata=new String[8];
		String[] parms={cons_no};
		SQLiteDatabase db=getActivity().openOrCreateDatabase(MyConfig.DB_NAME, 0,null);
		try{
			Cursor cursor=db.query(MyConfig.CONS_TABLE,queryColumns, "cons_no=?", parms, null, null, null);
			if(cursor.moveToFirst()){//这里moveToFirst指该cursor的第一行
				do{
					for(int i=0;i<cursor.getColumnCount()-1;i++){
					consdata[i]=cursor.getString(i+1);
					//consdata装载一行的数据
					}	
				}while(cursor.moveToNext());
			}
			cursor.close();
			db.close();
		}catch(SQLiteException e){
			e.printStackTrace();
		}
		return consdata;
	}
		public static final String[] constitles={ 
			"户号","户名","地址","Rtua","表号","故障","经度","纬度"
		};
}
