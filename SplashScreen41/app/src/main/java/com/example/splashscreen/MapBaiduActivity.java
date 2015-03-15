package com.example.splashscreen;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
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
import com.example.splashscreen.adapter.MyConfig;


import android.os.Bundle;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.splashscreen.MapDataCons;

public class MapBaiduActivity extends Activity {		
	    String TAG ="MapBaiduActivity";
		//地图相关
		private BMapManager mBMapMan = null;
		protected  static MapView mMapView = null;	
		private MapController mMapController = null;
	    public MKMapViewListener mMapListener = null;
		// 定位相关
		public  LocationClient mLocClient;
		public MyLocationListenner myListener = new MyLocationListenner();
	    public NotifyLister mNotifyer=null;
	    public static BDLocation location;
        public static MyLocationOverlay myLocationOverlay ;
		static LocationData locData = null;
	    public MapDataCons mapDataCons = null;
	    //titleBar相关
	//    private TextView textView = null;
	//    private ImageButton mapLayerBtn = null;
	    //GridView
	    private GridView mapGridView = null;
	    //overlay相关
	    public List<Drawable>  res = new ArrayList<Drawable>();
	    private List<OverlayItem> GeoList = new ArrayList<OverlayItem>();	
	    //Dialog 相关
	    public final int SELFROUTE = 0;
	    public final int TASKROUTE = 1;
	    public final int MAPLAYER = 2;
        public final int SELFROUTEFLAG = 1;
        public final int ROUTEFLAG = 2;
	    //Dialog selfroute
	    private EditText self_start_edittext;
	    private EditText self_end_edittext;
	    private TextView self_start_textv;
	    private TextView self_end_textv;
	    
		//数据库 修改1
		private  Cursor cursor;
		private static String task_num;    //接收RightFragment的任务号task_no
		private static String cons_num;
	 	private DataHelper dbHelper ;
	  	private SQLiteDatabase db;
		public void initView()
		{	    
	        mMapView = (MapView)findViewById(R.id.bmapsView2);	        
	        mMapController = mMapView.getController();
	        mMapView.setLongClickable(true);
	        mMapView.getController().setZoom(14);
	        mMapView.getController().enableClick(true);
	        mMapView.setBuiltInZoomControls(true);
	  	    mMapView.regMapViewListener(mBMapMan, mMapListener);
	        //mMapController.setMapClickEnable(true);
	        //mMapView.setSatellite(false);      
	        //设置定位参数包括：定位模式（单次定位，定时定位，），返回坐标类型及是否打开gps
	        LocationClientOption option = new LocationClientOption();
	        option.setOpenGps(true);//打开gps
	        option.setCoorType("bd09ll");     //设置坐标类型
	        option.setScanSpan(500);
	        //发起定位请求，请求过程为异步的，定位结果在上面的监听函数onReceiveLocation中获取
	        mLocClient = new LocationClient( this );	        
	        mLocClient.registerLocationListener( myListener );
	        mLocClient.setLocOption(option);
	        mLocClient.start();
	        //获取fragment传递的task_no和cons_no任务号和用户号
	        Intent intent = getIntent();
	        if(intent.getStringExtra("task_no")!=null){
	        task_num = intent.getStringExtra("task_no");
	        }
	        if(intent.getStringExtra("cons_no")!=null){
	        cons_num = intent.getStringExtra("cons_no");
	        }
	        //初始化titlebar相关控件      
	//        textView = (TextView)findViewById(R.id.titleTvmap1);
	//        textView.setText("地图导航");
	//        mapLayerBtn =(ImageButton)findViewById(R.id.titleLayerbtn);
	        //初始化Myoverlay我的图层相关元素
	        myLocationOverlay = new MyLocationOverlay(mMapView);
	  		locData = new LocationData();
	  		myLocationOverlay.setData(locData);
	  		mMapView.getOverlays().add(myLocationOverlay);
	  		myLocationOverlay.enableCompass();
	  		mMapView.refresh();
	  	
		}
		public void searchDatabase()
		{
			//数据库   修改1  从数据库中读取当前任务号的所有用户号和用户精度和纬度给对应数组，将当前的用户找出来
		   	dbHelper = new DataHelper(this,MyConfig.DB_NAME,null,MyConfig.DB_VERSION);
		   	db = dbHelper.getReadableDatabase();
		    cursor = db.rawQuery("SELECT cons_no,longitude,latitude FROM consitems WHERE task_no ="+task_num,null);
			int j = 0;
			while(cursor.moveToNext()){
			if(cons_num.equals(cursor.getString(0)))
			{
			mapDataCons = new MapDataCons(this, cursor.getString(0), cursor.getString(1),cursor.getString(2));
            mapDataCons.drawPicture = getApplication().getResources().getDrawable(R.drawable.icon_banner);
			}
			else
			{
			mapDataCons = new MapDataCons(this, cursor.getString(0), cursor.getString(1),cursor.getString(2));
			mapDataCons.drawPicture = getApplication().getResources().getDrawable(R.drawable.marker_location);
			//	 mapDataCons[j].drawPicture = getApplication().getResources().getDrawable(R.drawable.ic_launcher);						
			}
			res.add(mapDataCons.drawPicture);
			OverlayItem item= new OverlayItem(mapDataCons.p, "item"+j,"item"+j); 			  
			item.setMarker(res.get(j%(res.size())));			 
			GeoList.add(item);		
			++j;		
		  }
		  if(db != null){
		  db.close();
		  }
		}
		void showGridView(){
			int[] consImage={R.drawable.self1,R.drawable.task1,R.drawable.layer3};
			String[] consText={"自定义导航","任务导航","图层选择"};
			ArrayList<HashMap<String, Object>> mgriditem=new ArrayList<HashMap<String,Object>>();
			for(int i=0;i<consText.length;i++){
				HashMap<String, Object> map=new HashMap<String, Object>();
				map.put("consImage", consImage[i]);
				map.put("consText", consText[i]); 
				mgriditem.add(map);  
			}			
			SimpleAdapter mAdapter=new SimpleAdapter(this, mgriditem, R.layout.consgriditems, 
							new String[]{"consImage","consText"}, new int[]{R.id.consImage,R.id.consText});
			mapGridView.setAdapter(mAdapter);
		}
		
		public Dialog onCreateDialog(int id)
		{			
			AlertDialog.Builder builder=new Builder(MapBaiduActivity.this);
			switch(id)
			{
			    case SELFROUTE://自定义导航
			    	LayoutInflater inflater = LayoutInflater.from(MapBaiduActivity.this);
			    	View view = inflater.inflate(R.layout.dialogmapselfroute, null);
			    	//dialog selfroute			    
			  		self_start_edittext = (EditText)view.findViewById(R.id.selfroute_first_edittext);
		            self_end_edittext = (EditText)view.findViewById(R.id.selfroute_end_edittext);
                    Log.i("bupt","self_start_edittext:"+self_start_edittext);
		              Log.i("bupt","self_end_edittext :"+self_end_edittext);
                    builder.setTitle("自定义导航参数设置").setView(view);
                    builder.setIcon(R.drawable.star_light);
                    builder.setPositiveButton("进入导航", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							Log.i("bupt","1");
						  String startStr = self_start_edittext.getText().toString();
						  Log.i("bupt","2");
			              String endStr =  String.valueOf(self_end_edittext.getText());
			              Log.i("bupt","startStr:"+startStr);
			              Log.i("bupt","endStr :"+endStr);
			              
			             
			              if( endStr == null || endStr.length()<=0)
			              {
			            	  dialog.dismiss();
							  Toast.makeText(MapBaiduActivity.this, "设置参数为空，请检查设置!", 
										Toast.LENGTH_SHORT).show();
							}else{
								  Toast.makeText(MapBaiduActivity.this, "设置参数成功!", 
											Toast.LENGTH_SHORT).show();
							/*1表示自定义导航*/
						    MapSelfData data = new MapSelfData(1,startStr,endStr,"0","0");
							Intent intent_self = new Intent(MapBaiduActivity.this,RoutePlanDemo2.class);
							Log.i("bupt","3");
							Bundle mBundle = new Bundle();
							mBundle.putSerializable("data", (Serializable) data);
							intent_self.putExtras(mBundle);
							Log.i("bupt","4");
							startActivity(intent_self);
							
							dialog.dismiss();
					
							
			              }
						}
					});
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					});
				 
			    	
			    	break;
			    case TASKROUTE://任务导航
			    	builder.setTitle("任务导航帮助提示");
			    	builder.setIcon(R.drawable.star_light);
			    	builder.setMessage("地图上红色图标为当前用户，紫色图标为当前任务的其他用户。点击图标弹出绿色导航按钮，点击绿色导航按钮进入导航界面。");
			    	builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					});
			    	break;
			    case MAPLAYER: //图层导航
			    	String[] items={"街景地图","卫星地图","交通地图"};
			    	builder.setTitle("地图图层选择");
			    	builder.setIcon(R.drawable.star_light);
			    	builder.setItems(items, onselect);		    	
			    	break;
			    default:
			    	break;
			
			
			}
			return builder.create();
		}
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);       	
	        requestWindowFeature(Window.FEATURE_NO_TITLE);//必须在加载content之前执行
	        DemoApplication app = (DemoApplication)this.getApplication();
	        mBMapMan = app.mBMapManager;	        
			setContentView(R.layout.map);
	//		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebarmap);   	     
	        //初始化地图相关参数及界面相关元件
			initView();
			mapGridView = (GridView)findViewById(R.id.mapGridView);
			showGridView();
			mapGridView.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> parent, View v,
						int position, long id) {
					// TODO Auto-generated method stub
					switch(position)
					{
					case 0://自定义导航
						showDialog(SELFROUTE);
						break;
					case 1://任务导航
						showDialog(TASKROUTE);
						break;
					case 2:   //图层选择
						showDialog(MAPLAYER);
						break;
					default:
						break;
					}
				}
				
			});
			
	      
	        /**
	         * searchDatabase函数主要实现根据任务号查询所有的用户号码，再根据fragment传递的cons_num来找到当前
	         * 用户号码，给予相应不同的显示图标，其中对应的类定义为mapDataCons
	         */
	        searchDatabase();          
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
						Toast.makeText(MapBaiduActivity.this,title,Toast.LENGTH_SHORT).show();
					}
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
			 /**
		     * testItemClick弹出pop进入导航界面的按钮
		     */		 
    		 testItemClick();
    		 mMapView.refresh();//刷新地图
	    }
	   
	    
	    @Override
	    protected void onPause() {
	    	Log.i("MainActivity", "onPause");
	    	DemoApplication app = (DemoApplication)this.getApplication();
	    	app.mBMapManager.stop();
	        mMapView.onPause();	  
	        super.onPause();
	        
	    }    
	    @Override
	    protected void onResume() {
	    	Log.i("MainActivity", "onResume");
	    	DemoApplication app = (DemoApplication)this.getApplication();
		    app.mBMapManager.start();
		    mMapView.onResume();
	        super.onResume();
	    }    
	    @Override
	    protected void onDestroy() {
	    	Log.i("MainActivity", "onDestroy");
            if(db !=null)
            db.close();
	    	if(dbHelper != null){
			try{
			dbHelper.close();
			}finally{
			this.finish();
			}
			}
	        if (mLocClient != null)
	        {	        		
	        mLocClient.stop();
	        }
	        mMapView.destroy();        
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
	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        getMenuInflater().inflate(R.menu.splash_screen, menu);
	        return true;
	    }
	
		/**
	     * 监听函数，又新位置的时候，格式化成字符串，输出到屏幕中
	     */
	    public   class MyLocationListenner implements BDLocationListener {
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
	            //mMapController.animateTo(new GeoPoint((int)(locData.latitude* 1e6), (int)(locData.longitude *  1e6)), mHandler.obtainMessage(1));
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
	    /**
	     * testItemClick弹出pop进入导航界面的按钮
	     */
	    public void testItemClick() {
	    	Drawable marker =getApplication().getResources().getDrawable(R.drawable.layer3);	    
		    OverlayTest ov = new OverlayTest(marker, MapBaiduActivity.this, mMapView);		    
	//	    PopupOverlay pop = ov.pop;	
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
		
		public static  class OverlayTest extends ItemizedOverlay<OverlayItem> {
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
		 		
			 public OverlayTest(Drawable marker,Context context , MapView mMapView){
				 super(marker);
		         this.mContext = context;
		         this.mMapView = mMapView;
		         pop = new PopupOverlay( mMapView,new PopupClickListener() {
		         @Override
		         public void onClickedPopup(int index) {
		         String geoLat = String.valueOf(pt2.getLatitudeE6());
		         String geoLon = String.valueOf(pt2.getLongitudeE6());
		         Intent intent = new Intent();
		         /*2表示用户导航*/
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

