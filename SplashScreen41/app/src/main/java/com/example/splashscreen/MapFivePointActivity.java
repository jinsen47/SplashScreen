package com.example.splashscreen;



import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.splashscreen.adapter.DataHelper;
import com.example.splashscreen.adapter.MapSelfData;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.splashscreen.webservice.MapData;



	public class MapFivePointActivity extends Activity {
		private BMapManager mBMapMan;
		public  MapView mMapView = null;		
	    public MKMapViewListener mMapListener = null;

		// 定位相关
		LocationClient mLocClient;
		public MyLocationListenner myListener = new MyLocationListenner();
	    public NotifyLister mNotifyer=null;
		private Spinner spinner;
	    private static final String[] layer = {"街景地图","卫星地图","交通地图"};
	    private ArrayAdapter<String> adapter;
	    public List<Drawable>  res = new ArrayList<Drawable>();
	    private List<OverlayItem> GeoList = new ArrayList<OverlayItem>();
        MyLocationOverlay myLocationOverlay = null;	
		LocationData locData = null;
	 	
	    Button testUpdateButton = null;
			
		 //数据库
		 private DataHelper dbHelper = null;
		 private static String DB_NAME = "sql.db";
		 private static int DB_VERSION = 1;
		 private SQLiteDatabase db = null;
		 private  Cursor cursor;
		 public static String task_num = null;
	     public MapData[] mapData = null;
	     
	     //titleBar
	     private TextView textView = null;
	     private ImageButton mapLayerBtn = null;
		
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);        
	        DemoApplication app = (DemoApplication)this.getApplication();
	        mBMapMan = app.mBMapManager;
	        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);//必须在加载content之前执行
	        setContentView(R.layout.mapfivepoint);
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebarmap);  
	    	
	      
	      //实例化mapView，mapController，locationClient
	        mMapView = (MapView)findViewById(R.id.bmapsViewFivePoint);
	        initMapView();
	        mLocClient = new LocationClient( this );
	        mLocClient.registerLocationListener( myListener );
	        
	        // 处理Intent
	        Intent intent = getIntent();
	        task_num = intent.getStringExtra("task_no");
	        
	        textView = (TextView)findViewById(R.id.titleTvmap1);
	        textView.setText("地图导航");
	        mapLayerBtn =(ImageButton)findViewById(R.id.titleLayerbtn);
	        initMapView();
	        mapLayerBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String[] items={"街景地图","卫星地图","交通地图"};
					Dialog dialog=new AlertDialog.Builder(MapFivePointActivity.this).setTitle("地图显示选择").
						setIcon(R.drawable.star_light).setItems(items, onselect).create();
					dialog.show();
					
				}
			});
	        //数据库   修改1
		   	 dbHelper = new DataHelper(this,DB_NAME,null,DB_VERSION);
		   	 db = dbHelper.getReadableDatabase();	   	 
		     cursor = db.rawQuery("SELECT cons_no,longitude,latitude FROM consitems WHERE task_no ="+task_num,null);
		     mapData = new MapData[cursor.getCount()];
		    //j用于控制mapData数组中的移位
	 		int j = 0;
			while(cursor.moveToNext()){
				
				mapData[j] =new MapData(this,cursor.getString(0),cursor.getString(1),cursor.getString(2));
				res.add(mapData[j].drawPicture);
			  	OverlayItem item= new OverlayItem(mapData[j].p, "item"+j,"item"+j);
			   	item.setMarker(res.get(j%(res.size())));
			   	GeoList.add(item);
				j++;
			   }		
	        //Spinner
			/**
	        spinner = (Spinner)findViewById(R.id.Spinner01);
	        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,layer);
	        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        spinner.setAdapter(adapter);
	        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					String msg = adapter.getItem(arg2);
					if(msg == "卫星地图")
					{
						mMapView.setSatellite(true);
						mMapView.setTraffic(false);
					}
						
					else if(msg =="交通地图"){
						mMapView.setTraffic(true);
						
					}
						
					else if(msg =="街景地图")
					{
						mMapView.setTraffic(false);
						mMapView.setSatellite(false);
					}
					
					Log.i("bupt_msg", msg);
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
	        	
	        });
	        spinner.setVisibility(View.VISIBLE);
	        
	        **/
	        //设置定位参数包括：定位模式（单次定位，定时定位，），返回坐标类型及是否打开gps
	        LocationClientOption option = new LocationClientOption();
	        Log.i("bupt", "10");
	        option.setOpenGps(true);//打开gps
	        option.setCoorType("bd09ll");     //设置坐标类型
	        option.setScanSpan(500);
	     
	        //发起定位请求，请求过程为异步的，定位结果在上面的监听函数onReceiveLocation中获取
	        mLocClient.setLocOption(option);
	        mLocClient.start();        
	        mMapView.getController().setZoom(14);
	        mMapView.getController().enableClick(true);
	        mMapView.setBuiltInZoomControls(true);
	        mMapListener = new MKMapViewListener() {
	          
				@Override
				public void onMapMoveFinish() {
					// TODO Auto-generated method stub
				}
				
				@Override
				public void onClickMapPoi(MapPoi mapPoiInfo) {
					// TODO Auto-generated method stub
					String title = "";
					if (mapPoiInfo != null){
						/**
						 * 此处为添加获取点击位置的地理信息位置
						 */
						title = mapPoiInfo.strText;
						Toast.makeText(MapFivePointActivity.this,title,Toast.LENGTH_SHORT).show();
					}
					  Log.i("bupt", "13");
				}

				@Override
				public void onGetCurrentMap(Bitmap b) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onMapAnimationFinish() {
					// TODO Auto-generated method stub
					
				}
			};
			//myLocation有关图层
		    mMapView.regMapViewListener(mBMapMan, mMapListener);
			myLocationOverlay = new MyLocationOverlay(mMapView);
			locData = new LocationData();
		    myLocationOverlay.setData(locData);
			mMapView.getOverlays().add(myLocationOverlay);
			myLocationOverlay.enableCompass();
			mMapView.refresh();
			 Log.i("bupt", "15");
	
		    //ItemizedOverlay有关图层
    		 testItemClick();
			 mMapView.refresh();//刷新地图
			 if(db != null){
					db.close();
				}
				if(dbHelper != null){
					
						dbHelper.close();
				
				}
	
		   
	    }
	    
	    @Override
	    protected void onPause() {
	        mMapView.onPause();
	    	 DemoApplication app = (DemoApplication)this.getApplication();
	    	 app.mBMapManager.stop();
	        super.onPause();
	    }
	    
	    @Override
	    protected void onResume() {
	    	  mMapView.onResume();    	 
	    	   DemoApplication app = (DemoApplication)this.getApplication();	       
	            app.mBMapManager.start();
	        super.onResume();
	    }
	    
	    
	    @Override
	    protected void onDestroy() {
	    	 mMapView.destroy();
	        if (mLocClient != null)
	            {	        	
	        	//注意：必须有此句话，不能直接调用mLocClient。stop，否则在退出程序时会出现异常
	            mLocClient.unRegisterLocationListener(myListener);
	            mLocClient.stop();
	            }
	        
	        DemoApplication app = (DemoApplication)this.getApplication();
	        if (app.mBMapManager != null) {
	            app.mBMapManager.stop();
	        }
	        
	        super.onDestroy();
	    }
	    
	    @Override
	    protected void onSaveInstanceState(Bundle outState) {
	    	super.onSaveInstanceState(outState);
	    	mMapView.onSaveInstanceState(outState);
	    	
	    }
	    
	    @Override
	    protected void onRestoreInstanceState(Bundle savedInstanceState) {
	    	super.onRestoreInstanceState(savedInstanceState);
	    	mMapView.onRestoreInstanceState(savedInstanceState);
	    }
	    
	    public void testUpdateClick(){
	        mLocClient.requestLocation();
	    }
	    private void initMapView() {
	        mMapView.setLongClickable(true);
	     
	        //mMapView.setSatellite(false);
	    }
	   

	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        getMenuInflater().inflate(R.menu.splash_screen, menu);
	        return true;
	    }

		
		/**
	     * 监听函数，又新位置的时候，格式化成字符串，输出到屏幕中
	     */
	   
	    public class MyLocationListenner implements BDLocationListener {
	        @Override
	        public void onReceiveLocation(BDLocation location) {
	            if (location == null)
	                return ;
	            locData.latitude = location.getLatitude();
	            locData.longitude = location.getLongitude();
	            locData.accuracy = location.getRadius();
	            locData.direction = location.getDerect();
	            myLocationOverlay.setData(locData);
	            mMapView.refresh();
	            Log.i("locData.latitude",String.valueOf(locData.latitude));
	            Log.i("locData.longitude",String.valueOf(locData.longitude));
	        }
	        
			public void onReceivePoi(BDLocation poiLocation) {
	            if (poiLocation == null){
	                return ;
	            }
	        }
	    }
	    
	
	    public class NotifyLister extends BDNotifyListener{
	        public void onNotify(BDLocation mlocation, float distance) {
	        }
	    }
	 
	    public void testItemClick() {
	    	Drawable marker = MapFivePointActivity.this.getResources().getDrawable(R.drawable.marker_location);
		    OverTestFivePoint ov = new OverTestFivePoint(marker, this, mMapView);
		    for(OverlayItem item : GeoList){
		    	ov.addItem(item);
		    }
		    mMapView.getOverlays().add(ov);
		    mMapView.refresh();
	
	    	
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
		public static  class OverTestFivePoint extends ItemizedOverlay<OverlayItem> {
			 public List<OverlayItem> mGeoList = new ArrayList<OverlayItem>();
		     private Context mContext = null;
		     public static PopupOverlay pop;
	//	     MyLocationListenner myListener = new MyLocationListenner();
		     private MapView mMapView;
		     public GeoPoint pt2;
		     private LocationClient mLocClient;
		     private  Bitmap bmp;
		     private Button mBtn = null;
//		    private BDLocation location;
		     private Toast mToast = null;
		 		
			 public OverTestFivePoint(Drawable marker,Context context , MapView mMapView){
				 super(marker);
		         this.mContext = context;
		         this.mMapView = mMapView;
		         pop = new PopupOverlay( mMapView,new PopupClickListener() {
		         @Override
		         public void onClickedPopup(int index) {
		         String geoLat = String.valueOf(pt2.getLatitudeE6());
		         String geoLon = String.valueOf(pt2.getLongitudeE6());
		         Intent intent = new Intent();      
		         MapSelfData data2 = new MapSelfData(2,"0","0",geoLat,geoLon);		 							
				 Bundle mBundle = new Bundle();
				 mBundle.putSerializable("data", (Serializable) data2);
				 intent.putExtras(mBundle);
		         intent.setClass(mContext, RoutePlanDemo2.class);
		         mContext.startActivity(intent); 
		         }
		         });
		         populate();      
		     }
			 
			 protected boolean onTap(int index) {
				 Log.i("bupt", "onTap1");
		         try {
		         bmp = BitmapFactory.decodeStream(mContext.getAssets().open("route2.png"));
		         } catch (IOException e) {
		         e.printStackTrace();
		         }
		          pop.showPopup(bmp, mGeoList.get(index).getPoint(), 32);
		          /**用于显示当前点是第几个点
		         if (null == mToast)
		         mToast = Toast.makeText(mContext, mGeoList.get(index).getTitle(), Toast.LENGTH_SHORT);
		         else mToast.setText(mGeoList.get(index).getTitle());
		         mToast.show();
		           **/
		         return true;
		     }
		     public boolean onTap(final GeoPoint pt, MapView mapView){
		    	 Log.i("bupt", "onTap2");
		    	 this.mMapView = mapView;
		    	 this.pt2 = pt;
		        if (pop != null){
		            pop.hidePop();
		          /**
		         if (mBtn != null) {
		         mapView.removeView(mBtn);
		         mBtn = null;
		         }
		         **/
		         }
		         super.onTap(pt,mapView);
		         return false;
		     }  
		     @Override
		     protected OverlayItem createItem(int i) {
		         return mGeoList.get(i);
		     } 
		     @Override
		     public int size() {
		         return mGeoList.size();
		     }
		     public void addItem(OverlayItem item){
		         mGeoList.add(item);
		         populate();
		     }
		     public void removeItem(int index){
		         mGeoList.remove(index);
		         populate();
		     }
		 }
	}

