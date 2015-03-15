package com.example.splashscreen;

import java.util.ArrayList;
import java.util.HashMap;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.splashscreen.adapter.*;
import com.example.splashscreen.webservice.PostCustomGpsResp;
import com.example.splashscreen.webservice.SoapHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class LocationCaptureActivity extends Activity {
	 // 地图定位我的位置的参数	
	 public LocationClient  mLocationClient = null;
	 public BDLocationListener myListener = new MyLocationListener();
	 public LocationData locData = new LocationData();
     BMapManager mBMapMan = null;
	 MapView mMapView = null;
   	 public static MyLocationOverlay myLocationOverlay = null; 
	 //数据库
	 private DataHelper dbHelper = null;
	 private SQLiteDatabase db = null;
	 private  Cursor cursor;
	 public static String cons_num = null ;
	 public static String task_num = null;
	 //数据库上传相关的输入参数
	 private String str1_lat;
	 private String str1_lon;
	 private String str1_con;
	 String login_id = null;
	 String passwordMD5 = null;	 
	 //titleBar相关
	 private TextView textView = null;
	 private PopupWindow popupWindow;
	 private LinearLayout layout;
	 private ListView listView;
	 private String title[] = { "我的位置", "地图点选" };
	 ProgressDialog progressDialog;
	 //overlay
	 MyItemizedOverlayCapLoc iconOverlay;
	 OverlayItem overlayitem;
	 //GridView
	 private GridView locCapGridView ;
	 
	 Handler handler = new Handler(){		
		 public void handleMessage(Message msg){
			 progressDialog.dismiss();
			 switch(msg.what){
			 case MyConfig.MSG_DATAINSERT:
				 Log.i("handler", "handler->insertDataSuc!");
				 Toast.makeText(getBaseContext(), "Handler->插入数据库成功", Toast.LENGTH_SHORT).show();
				 break;
			 case MyConfig.MSG_UPLOADGPS:
				 Toast.makeText(getBaseContext(), "上传GPS数据成功", Toast.LENGTH_SHORT).show();
				 break;
			 case MyConfig.MSG_NETWORKERROR:
				 Toast.makeText(getBaseContext(), "网络连接错误",Toast.LENGTH_SHORT).show();
				 break;
			 case MyConfig.MSG_GPSSTARTERROR:
				 Toast.makeText(getBaseContext(), "GPS设备未开启", Toast.LENGTH_SHORT).show();
				 break;
			 }
			 
		 }
	 };
	 void showGridView(){
			int[] consImage={R.drawable.layer3,R.drawable.loc_cap2,R.drawable.up_loc};
			String[] consText={"图层选择","位置采集","位置上传"};
			ArrayList<HashMap<String, Object>> mgriditem=new ArrayList<HashMap<String,Object>>();
			for(int i=0;i<consText.length;i++){
				HashMap<String, Object> map=new HashMap<String, Object>();
				map.put("consImage", consImage[i]);
				map.put("consText", consText[i]); 
				mgriditem.add(map);  
			}			
			SimpleAdapter mAdapter=new SimpleAdapter(this, mgriditem, R.layout.consgriditems, 
							new String[]{"consImage","consText"}, new int[]{R.id.consImage,R.id.consText});
			locCapGridView.setAdapter(mAdapter);
		}
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	  
		requestWindowFeature(Window.FEATURE_NO_TITLE);//必须在加载content之前执行
		Log.i("bupt","1");
		  DemoApplication app = (DemoApplication)this.getApplication();
		  Log.i("bupt","2");
	        mBMapMan = app.mBMapManager;
	        Log.i("bupt","3");
		setContentView(R.layout.locationcapture);
		Log.i("bupt","4");
	    //初始化参数
		initView();	
		Log.i("bupt","5");
		locCapGridView = (GridView)findViewById(R.id.locCaptureGridView);
		Log.i("bupt","6");
		showGridView();
		Log.i("bupt","7");
		locCapGridView.setOnItemClickListener( new OnItemClickListener() {
    		@Override
    		public void onItemClick(AdapterView<?> parent, View v, int position,
    				long id) {
    			switch(position){
    			case 0: //图层选择  
    				String[] items={"街景地图","卫星地图","交通地图"};
    				Dialog dialog=new AlertDialog.Builder(LocationCaptureActivity.this).setTitle("地图显示选择").
    						setIcon(R.drawable.star_light).setItems(items, onselect).create();
    				dialog.show();
    				break;
    			case 1://位置采集
    				
    				 v.getTop();
    				 int y = v.getBottom() * 3 / 2;
    				 int x = getWindowManager().getDefaultDisplay().getWidth()/2 ;
    	
    			//	showPopupWindow(x, y);
    				 showPopupWindowMy(v);
    				break; 
    			case 2: //位置上传		   
    				progressDialog.show();
    				((MyLocationListener) myListener).uploadMyLocation();
    				break;
    			 			
    			default:
    				break;
    			}			
    		}
    		}
    		
		);

		Log.i("bupt","8");
    }
    
    public void initView()
    {
    	//获取sharepreference的内容
    	SharedPreferences sp=getSharedPreferences(MyConfig.SharePreferenceFile, 0);
    	login_id=sp.getString("name", "");
    	passwordMD5=sp.getString("passwordmd5", "");
    	//获取fragment上传过来的用户号和任务号码
    	Intent intent = getIntent();
		cons_num = intent.getStringExtra("cons_no");
		task_num = intent.getStringExtra("task_no");
	
		//初始化地图相关参数
    	mMapView = (MapView)findViewById(R.id.bmapsView1);
    	mMapView.setBuiltInZoomControls(true);
    	//初始化定位相关参数
   	    mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
    	option.setOpenGps(true);
        option.setAddrType("all");
        option.setCoorType("bd0911");
        option.setScanSpan(5000);
        mLocationClient.setLocOption(option);
        mLocationClient.start();  
        mMapView.getController().setZoom(14);
        mMapView.getController().enableClick(true);
        mMapView.setBuiltInZoomControls(true);
    	myLocationOverlay = new MyLocationOverlay(mMapView);
    	myLocationOverlay.setData(locData);
    	mMapView.getOverlays().add(myLocationOverlay);
    	myLocationOverlay.enableCompass();
    	mMapView.refresh();
    
       
    	//显示上传位置的进度条
        progressDialog=new ProgressDialog(LocationCaptureActivity.this);
    	progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    	progressDialog.setTitle("提示");
    	progressDialog.setIcon(R.drawable.info_48_blue);
    	progressDialog.setMessage("正在上传,请稍后...");
    	progressDialog.setCancelable(true);
    	//database	   
    	dbHelper = new DataHelper(this,MyConfig.DB_NAME,null,MyConfig.DB_VERSION);
    }
      
	 DialogInterface.OnClickListener onselect=new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch(which){
					case 0://街景地图
						mMapView.setTraffic(false);
						mMapView.setSatellite(false);
						break;
					case 1://卫星地图
						mMapView.setSatellite(true);
						mMapView.setTraffic(false);
						break;
					case 2://表号
						mMapView.setTraffic(true);
						break;
					default:
						
				}
				dialog.dismiss();
			}
		};
		
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0, 1, 0, "查询");
    	getMenuInflater().inflate(R.menu.splash_screen, menu);
		return super.onCreateOptionsMenu(menu);
	}
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == 1){
		db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query("consitems",new String[]{"new_latitude","new_longitude","cons_no"}, "cons_no = ?", new String[]{cons_num}, null, null, null);
		//Cursor cursor = db.rawQuery("select cons_no,new_latitude,new_longitude FROM consitems WHERE cons_no ="+cons_num,null);
		while(cursor.moveToNext()){
			str1_con = cursor.getString(0);
			str1_lat = cursor.getString(1);
			str1_lon = cursor.getString(2);
		}
		Toast t = Toast.makeText(this, "cons33Menu :"+str1_con+"new_latitude:"+str1_lat+",new_latitude:"+str1_lon, Toast.LENGTH_SHORT);
		t.show();
		if(db!=null)
 		   db.close();
		}
		return true;
	}
	
	 public void showPopupWindowMy(View parent) {
			layout = (LinearLayout) LayoutInflater.from(LocationCaptureActivity.this).inflate(
					R.layout.dialog, null);
			listView = (ListView) layout.findViewById(R.id.lv_dialog);
			listView.setAdapter(new ArrayAdapter<String>(LocationCaptureActivity.this,
					R.layout.text, R.id.tv_text, title));

			popupWindow = new PopupWindow(LocationCaptureActivity.this);
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
			popupWindow
					.setWidth(getWindowManager().getDefaultDisplay().getWidth() / 2);
			popupWindow.setHeight(150);
			popupWindow.setOutsideTouchable(true);
			popupWindow.setFocusable(true);
			popupWindow.setContentView(layout);
			// showAsDropDown会把里面的view作为参照物，所以要那满屏幕parent
			// popupWindow.showAsDropDown(findViewById(R.id.tv_title), x, 10);
			
			WindowManager windowManager = (WindowManager)getSystemService(
					Context.WINDOW_SERVICE);
			int screenWidth = windowManager.getDefaultDisplay().getWidth();
	//		int xPos = screenWidth/2- popupWindow.getWidth() / 2;
			int xPos = popupWindow.getWidth()*1/5; //默认为距离parent就是在哪个view下面，具体view左边的偏移距离
			popupWindow.showAsDropDown(parent,-xPos,0);//默认view parent的左下角，负值 表示向左移动，正值表示向右移动
		//	popupWindow.showAtLocation(findViewById(R.id.loccapture), Gravity.LEFT
		//			| Gravity.TOP, x, y);//需要指定Gravity，默认情况是center.

			 listView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View view, int position,
							long id) {
						switch (position)
						{
						case 0:  //我的位置				
							((MyLocationListener) myListener).insertMyLocGPSData();
							break;
						case 1:  //地图点选
							 addTapOverLay();
							break;
					    default:
					    	break;
						}
						popupWindow.dismiss();
						popupWindow = null;
					}
					
				});
		}
	   /*
	    * showPopupWindow主要实现pupwindow浮动窗口的实现
	    */
	   public void showPopupWindow(int x, int y) {
			layout = (LinearLayout) LayoutInflater.from(LocationCaptureActivity.this).inflate(
						R.layout.dialog, null);
			listView = (ListView) layout.findViewById(R.id.lv_dialog);
		   //设置listview中具体字的显示text并且设置listview的adapter
		    listView.setAdapter(new ArrayAdapter<String>(LocationCaptureActivity.this,
						R.layout.text, R.id.tv_text, title));
            popupWindow = new PopupWindow(LocationCaptureActivity.this);
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
			popupWindow.setWidth(getWindowManager().getDefaultDisplay().getWidth() / 2);
			popupWindow.setHeight(150);
			popupWindow.setOutsideTouchable(true);
			popupWindow.setFocusable(true);
			popupWindow.setContentView(layout);
			// showAsDropDown会把里面的view作为参照物，所以要那满屏幕parent
		//	 popupWindow.showAsDropDown(findViewById(R.id.), x, 10);
			popupWindow.showAtLocation(findViewById(R.id.loccapture), Gravity.CENTER_HORIZONTAL
						, x, y);//需要指定Gravity，默认情况是center.
            listView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View view, int position,
							long id) {
						switch (position)
						{
						case 0:  //我的位置		
							
							((MyLocationListener) myListener).insertMyLocGPSData();
							break;
						case 1:  //地图点选
							 addTapOverLay();
							break;
					    default:
					    	break;
						}
						popupWindow.dismiss();
						popupWindow = null;
					}
					
				});
			}
	   
	public class MyLocationListener implements BDLocationListener{
		public String lat_str = null;
		public String lon_str = null;
		private GeoPoint p_current;
		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			if(location == null){
				return ;
			}
			locData.latitude = location.getLatitude();
			locData.longitude = location.getLongitude();
			locData.accuracy = location.getRadius();
			locData.direction = location.getDerect();			
            myLocationOverlay.setData(locData);
            p_current = new GeoPoint(Integer.valueOf((int) (locData.latitude*1e6)), Integer.valueOf((int) (locData.longitude*1e6)));
            mMapView.refresh();			
		}
		
		@Override
		public void onReceivePoi(BDLocation poiLocation) {
			// TODO Auto-generated method stub
			 if (poiLocation == null){
	                return ;
	            }
		}
		//上传两种采集方式的位置信息
		public void uploadMyLocation()
	    {
		   
	   	   Thread thread = new Thread(){
	   	     public void run(){
	   		 Looper.prepare();
	   		 Message msg = Message.obtain();
	   		 db = dbHelper.getReadableDatabase();
	   		 Cursor cursor = db.query("consitems",new String[]{"new_latitude","new_longitude","cons_no"}, "cons_no = ? and task_no=?", new String[]{cons_num,task_num}, null, null, null);  				      
	   	   	 while(cursor.moveToNext()){
	   	   		 str1_lat = cursor.getString(cursor.getColumnIndex("new_latitude"));
	   	   		 str1_lon = cursor.getString(cursor.getColumnIndex("new_longitude"));
	   	   		 str1_con = cursor.getString(cursor.getColumnIndex("cons_no"));
	   	   
	   		 }
	   	    if(db!=null)
   		    db.close();
    	     PostCustomGpsResp postGpsStatus = null;
	   		 postGpsStatus = SoapHelper.postCustomGps(login_id, passwordMD5, str1_con, str1_lon,str1_lat);
	   		
	   		 if((postGpsStatus != null)&&(postGpsStatus.status.equals(MyConfig.Success))){
	   		 msg.what=MyConfig.MSG_UPLOADGPS;
	   		 }
	   		 else{
	   		 msg.what=MyConfig.MSG_NETWORKERROR;
	   		 }
	   	   	 handler.sendMessage(msg);
	   		}
	   	    
	   	    };
	   		thread.start();
	        }
		//采集当前我的位置信息并且插入数据库
		int insertMyLocGPSData(){//write data to database set finishflag=1
			
			mMapView.getController().animateTo(p_current);
    		int row_num=0;
    		Toast.makeText(LocationCaptureActivity.this, "当前位置纬度:"+String.valueOf(locData.latitude)+"经度:"+String.valueOf(locData.longitude), Toast.LENGTH_SHORT).show();
    		db = dbHelper.getWritableDatabase();
    		lat_str = String.valueOf(locData.latitude);
            lon_str = String.valueOf(locData.longitude);
    		ContentValues values = new ContentValues();
	        values.put("new_latitude", lat_str);
	        values.put("new_longitude", lon_str);
	        row_num = db.update("consitems", values, "cons_no = ? and task_no=?", new String[]{cons_num,task_num});
	        if(row_num !=0)
	        {
	        	Toast.makeText(getBaseContext(), "插入数据库成功", Toast.LENGTH_SHORT).show();
	        }
	        Cursor cursor = db.query("consitems",new String[]{"new_latitude","new_longitude","cons_no"}, "cons_no = ?and task_no=?", new String[]{cons_num,task_num}, null, null, null);			   	   
	        while(cursor.moveToNext()){		
		     str1_lat = cursor.getString(0);
	    	 str1_lon = cursor.getString(1);
			 str1_con = cursor.getString(2);
		   
			}		
			Toast t = Toast.makeText(LocationCaptureActivity.this, "查询数据库 :纬度:"+str1_lat+"经度:"+str1_lon, Toast.LENGTH_SHORT);
			t.show();
    		if(db!=null)
    		   db.close();
    		return row_num;
    	}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		progressDialog.dismiss();
		if(db != null){
			db.close();
		}
	    if(dbHelper != null){
			try{
				dbHelper.close();
			}finally{
				this.finish();
			}
		}
		if(mLocationClient != null){
			mLocationClient.stop();
		}		
		mMapView.destroy();
	    DemoApplication app = (DemoApplication)this.getApplication();
        if (app.mBMapManager != null) {
            app.mBMapManager.stop();
        }
		super.onDestroy();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub		
		DemoApplication app = (DemoApplication)this.getApplication();
    	app.mBMapManager.stop();
    	mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub	
		DemoApplication app = (DemoApplication)this.getApplication();
		app.mBMapManager.start();
		mMapView.onResume();
		super.onResume();
	}
	
	//地图点选方式
	public void addTapOverLay(){  
        //以一副透明图片标示可点选图层，这里在地图上不显示出来，只提供可点选目的地功能   
        GeoPoint gpoint = new GeoPoint((int) (39.914714 * 1E6), (int) (116.404269 * 1E6));  
         overlayitem = new OverlayItem(gpoint, "title", "content");  
        Drawable drawale = getResources().getDrawable(R.drawable.icon_marka);  
         iconOverlay = new  MyItemizedOverlayCapLoc(LocationCaptureActivity.this,drawale,dbHelper,db);  
        // 添加图层   
        iconOverlay.addOverlay(overlayitem);  
        mMapView.getOverlays().add(iconOverlay);  
    }  
	
	
}
