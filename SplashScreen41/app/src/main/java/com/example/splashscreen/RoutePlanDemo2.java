package com.example.splashscreen;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.map.TransitOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKGeocoderAddressComponent;
import com.baidu.mapapi.search.MKPlanNode;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKRoute;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.splashscreen.adapter.MapSelfData;
import com.example.splashscreen.adapter.MyConfig;
import com.example.splashscreen.adapter.MyReader;
import com.example.splashscreen.webservice.MaintenanceItem;
import com.example.splashscreen.webservice.SoapHelper;



public class RoutePlanDemo2 extends Activity {
	private List<String> list = new ArrayList<String>();
    private String TAG = "RoutePlanDemo";	 
	private GridView consGridView;
	private BMapManager mBMapManager =null;
	private static MapView  mMapView_route = null;
	private MapController mMapController = null;
    public MKMapViewListener mMapListener = null;
    public OnItemClickListener gridviewItemClickListener;
	// 定位相关
	public LocationClient  mLocClient_route;
    MyLocationListenner myListener = new MyLocationListenner();
	NotifyLister mNotifyer=null;
	MyLocationOverlay myLocationOverlay = null;	
	LocationData locData = null;		
	private static View driveView;
	private static View TransitView;
	private static View walkView;

	public static String startCityName;   //定位的城市
	public static String endCityName;
	public static String busCityName;
	//Dialog
	private final int HELP=0;
	private final int LOGOUT=1;
	private final int SETTING=2;
	private String str;
	private EditText city_name = null;
	private EditText loc_first = null;
	private EditText loc_end = null;

	private EditText city2_firstname = null;
	private EditText city2_endname = null;
	SharedPreferences sp;
	SharedPreferences.Editor editor;
	
	//Button mBtnCusRoute = null; //自定义路线
	MKSearch mSearch = null;	// 搜索模块，也可去掉地图模块独立使用	
    private String mLat1 ;     // point1纬度
	private String mLon1 ;     // point1经度
    //导航终点
    private GeoPoint p1_end;
    private GeoPoint p1_start;
    private GeoPoint p1_current;
    //Flag = 1表示自定义导航，Flag = 2 表示默认用户导航
    private int Flag = 0;
    private MapSelfData data ;
    private String startAddr;
    private String endAddr;
	void initView()
	{
	 mMapView_route = (MapView)findViewById(R.id.bmapViewRoute);
	 mLocClient_route = new LocationClient(getApplicationContext());
	 mLocClient_route.registerLocationListener(myListener);
	 initMapView();
	 Intent intent = getIntent();
	 data = ( MapSelfData)intent.getSerializableExtra("data");
	 Flag = data.Flag;
	 Log.i("bupt","flag is:"+Flag);
	 mLat1 = data.geoLat;
	 mLon1 = data.geoLon;
	 startAddr = data.startAddr;
	 endAddr = data.endAddr;
	 Log.i("bupt","geoLat is:"+mLat1+"mLon1 is:"+mLon1);
	 Log.i("bupt","startAddr is:"+startAddr+"endAddr is:"+endAddr);	
	 p1_end = new GeoPoint(Integer.valueOf(mLat1), Integer.valueOf(mLon1));
	 LocationClientOption option = new LocationClientOption();
	 option.setOpenGps(true);//打开gps
	 option.setCoorType("bd09ll");     //设置坐标类型
	 option.setScanSpan(500);	     
     //发起定位请求，请求过程为异步的，定位结果在上面的监听函数onReceiveLocation中获取
	 mLocClient_route.setLocOption(option);
	 mLocClient_route.start();
	 mMapView_route.getController().setZoom(14);
	 mMapView_route.getController().enableClick(true);
	 sp=this.getSharedPreferences(MyConfig.SharePreferenceFile, MODE_PRIVATE);//以默认的模式打开文件
	 editor =sp.edit();
	 Log.i(TAG, "12");
	 mMapView_route.setBuiltInZoomControls(true);
	 mMapView_route.regMapViewListener( mBMapManager, mMapListener);
	 myLocationOverlay = new MyLocationOverlay( mMapView_route);
	 locData = new LocationData();
	 myLocationOverlay.setData(locData);
	 mMapView_route.getOverlays().add(myLocationOverlay);
	 myLocationOverlay.enableCompass();
	 mMapView_route.refresh();
	 //帮助文件初始化
	
	}
	void showGridView(){
		int[] consImage={R.drawable.car2,R.drawable.bus2,R.drawable.walk2,R.drawable.layer3};
		String[] consText={"驾车导航","公交导航","步行导航","图层选择"};
		ArrayList<HashMap<String, Object>> mgriditem=new ArrayList<HashMap<String,Object>>();
		for(int i=0;i<consText.length;i++){
			HashMap<String, Object> map=new HashMap<String, Object>();
			map.put("consImage", consImage[i]);
			map.put("consText", consText[i]); 
			mgriditem.add(map);  
		}			
		SimpleAdapter mAdapter=new SimpleAdapter(this, mgriditem, R.layout.consgriditems, 
						new String[]{"consImage","consText"}, new int[]{R.id.consImage,R.id.consText});
		consGridView.setAdapter(mAdapter);
	}
	
	 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//必须在加载content之前执行
		DemoApplication app = (DemoApplication)this.getApplication();
		mBMapManager = app.mBMapManager;
		setContentView(R.layout.routeplan);
		initView();
		consGridView=(GridView)findViewById(R.id.routeGridView);
		showGridView();
//		consGridView.setOnItemClickListener(gridviewItemClickListener);
		Toast.makeText(RoutePlanDemo2.this, "刚进入界面请稍等2秒钟,再选择导航",Toast.LENGTH_SHORT).show();
		consGridView.setOnItemClickListener( new OnItemClickListener() {
    		@Override
    		public void onItemClick(AdapterView<?> parent, View v, int position,
    				long id) {
    			switch(position){
    			case 0: //goto intent navigator  驾车导航   	
    				driveView = v;
    				myListener.SearchButtonProcess(driveView);
    				break;
    			case 1://公交导航
    				TransitView = v;		
    				myListener.SearchButtonProcess(TransitView);
    				break; 
    			case 2: //步行导航		   
    				walkView = v;
    				myListener.SearchButtonProcess(walkView);
    				break;
    			case 3:// 图层选择
    				String[] items={"街景地图","卫星地图","交通地图"};
    				Dialog dialog=new AlertDialog.Builder(RoutePlanDemo2.this).setTitle("地图显示选择").
    						setIcon(R.drawable.star_light).setItems(items, onselect).create();
    				dialog.show();
    				break;
    			
    			default:
    				break;
    			}			
    		}
    		}
    		
		);
		
        // 初始化搜索模块，注册事件监听
        mSearch = new MKSearch();      
        mSearch.init(  mBMapManager, new MKSearchListener(){

            @Override
        public void onGetPoiDetailSearchResult(int type, int error) 
            {
            }
        public void onGetDrivingRouteResult(MKDrivingRouteResult res,int error) {
		// 错误号可参考MKEvent中的定义
		if (error != 0 || res == null) {
		Toast.makeText(RoutePlanDemo2.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
		return;
			}			
		RouteOverlay routeOverlay = new RouteOverlay(RoutePlanDemo2.this,mMapView_route);
		// 此处仅展示一个方案作为示例
		routeOverlay.setData(res.getPlan(0).getRoute(0));
		mMapView_route.getOverlays().clear();
		mMapView_route.getOverlays().add(routeOverlay);
		mMapView_route.getOverlays().add(myLocationOverlay);
		
		mMapView_route.refresh();
		// 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
		mMapView_route.getController().zoomToSpan(routeOverlay.getLatSpanE6(), routeOverlay.getLonSpanE6());
		mMapView_route.getController().animateTo(res.getStart().pt);
		}
        public void onGetTransitRouteResult(MKTransitRouteResult res,int error) {
		if (error != 0 || res == null) {
		Toast.makeText(RoutePlanDemo2.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
		return;
		}
		TransitOverlay  routeOverlay = new TransitOverlay (RoutePlanDemo2.this,mMapView_route);
		// 此处仅展示一个方案作为示例
		routeOverlay.setData(res.getPlan(0));
		mMapView_route.getOverlays().clear();
		mMapView_route.getOverlays().add(routeOverlay);
		mMapView_route.getOverlays().add(myLocationOverlay);
		
		mMapView_route.refresh();
		// 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
		mMapView_route.getController().zoomToSpan(routeOverlay.getLatSpanE6(), routeOverlay.getLonSpanE6());
		mMapView_route.getController().animateTo(res.getStart().pt);
		}
        public void onGetWalkingRouteResult(MKWalkingRouteResult res,int error) {
		if (error != 0 || res == null) {
		Toast.makeText(RoutePlanDemo2.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
		return;
		}
		RouteOverlay routeOverlay = new RouteOverlay(RoutePlanDemo2.this,mMapView_route);
		// 此处仅展示一个方案作为示例
		routeOverlay.setData(res.getPlan(0).getRoute(0));
		mMapView_route.getOverlays().clear();
		mMapView_route.getOverlays().add(routeOverlay);
		mMapView_route.getOverlays().add(myLocationOverlay);
		
		mMapView_route.refresh();
		// 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
		mMapView_route.getController().zoomToSpan(routeOverlay.getLatSpanE6(), routeOverlay.getLonSpanE6());
		mMapView_route.getController().animateTo(res.getStart().pt);
		}
		public void onGetAddrResult(MKAddrInfo result, int error)
        {  
		if ( result == null) {
		Toast.makeText(RoutePlanDemo2.this, "city抱歉，未找到结果", Toast.LENGTH_SHORT).show();
		return;
		}
		GeoPoint point = result.geoPt;
		MKGeocoderAddressComponent kk=result.addressComponents; 
        String city=kk.city;        
        }
		public void onGetPoiResult(MKPoiResult res, int arg1, int arg2) {
		}
		public void onGetBusDetailResult(MKBusLineResult result, int iError) {
		}
		@Override
		public void onGetSuggestionResult(MKSuggestionResult res, int arg1) {
		}
        });
        
        // 设定搜索按钮的响应
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
		/**此处为添加获取点击位置的地理信息位置*/
		title = mapPoiInfo.strText;
		Toast.makeText(RoutePlanDemo2.this,title,Toast.LENGTH_SHORT).show();
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
	}
	
	private void initMapView() {
		 mMapView_route.getController().enableClick(true);
		 mMapView_route.getController().setZoom(12);
		 mMapView_route.setBuiltInZoomControls(true);
		 mMapView_route.setDoubleClickZooming(true);  
	}
	
	@Override
    protected void onDestroy() {
	//	mMapView.getOverlays().clear();
		Log.i("Route", "onDestry");
	 mMapView_route.getOverlays().clear();
	 mMapView_route.destroy();
	 if (mBMapManager != null) {
	 mBMapManager.stop();
	 }
	 mLocClient_route.unRegisterLocationListener(myListener);
	 if (mLocClient_route != null)
	 {
	   mLocClient_route.stop();
	 }    
	 super.onDestroy();
       
    }
	  DialogInterface.OnClickListener onselect=new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch(which){
					case 0://街景地图
						mMapView_route.setTraffic(false);
						mMapView_route.setSatellite(false);
						break;
					case 1://卫星地图
						mMapView_route.setSatellite(true);
						mMapView_route.setTraffic(false);
						break;
					case 2://表号
						mMapView_route.setTraffic(true);
						break;
					default:
						
				}
				dialog.dismiss();
			}
		};
		protected Dialog onCreateDialog(int id) {
			AlertDialog.Builder builder=new Builder(RoutePlanDemo2.this);
			switch(id){
			case HELP:	
				builder.setTitle("帮助");
				builder.setMessage(str);
				builder.setIcon(R.drawable.star_light);
				builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {		
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();		
					}
				});
				break;
			case LOGOUT:
				LayoutInflater inflater=LayoutInflater.from(RoutePlanDemo2.this);
				View view=inflater.inflate(R.layout.dialogcityset, null);
				city2_firstname =(EditText)view.findViewById(R.id.city_first_name2);
				city2_endname = (EditText)view.findViewById(R.id.city_end_name2);
					builder.setTitle("导航参数设置").setView(view);
					builder.setIcon(R.drawable.star_light);
					Log.i("bupt", "2");
					builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					Log.i("bupt", "3");
					String city_start_str = city2_firstname.getText().toString();
					String  city_end_str = city2_endname.getText().toString();
					if((city_start_str.equals(null))&&(city_end_str.equals(null)))
					{
						startCityName = null ;   //定位的城市
						endCityName= null;
						dialog.dismiss();
					}else
					{Log.i("bupt", "4");
						editor.putString("startCityName",city_start_str );
						editor.putString("endCityName",city_end_str );
						editor.commit();
						dialog.dismiss();
					}
					Log.i("bupt", "5");
					
			
					Toast.makeText(RoutePlanDemo2.this, "更改参数成功!", 
							Toast.LENGTH_SHORT).show();
					}
				});
						
					break;
			case SETTING:
			LayoutInflater inflater2=LayoutInflater.from(RoutePlanDemo2.this);
			View view2 = inflater2.inflate(R.layout.dialogmapset, null);
			Log.i("bupt", "1");
				 city_name =(EditText)view2.findViewById(R.id.city_name);
				Log.i("bupt", "1.1");
				 loc_first=(EditText)view2.findViewById(R.id.loc_first_name);
				Log.i("bupt", "1.2");
				 loc_end =(EditText)view2.findViewById(R.id.loc_end_name);
				Log.i("bupt", "2");
				builder.setTitle("导航参数设置").setView(view2);
				builder.setIcon(R.drawable.star_light);
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Log.i("bupt", "3");
							String cityStr = city_name.getText().toString();
							String loc_start_str=loc_first.getText().toString();
							String loc_end_str = loc_end.getText().toString();
	/**
							if(DemoApplication.isIPAddress(ipstr)&&DemoApplication.isPort(portstr)){
								//app.setUrl(url);
								editor.putString("ip", ipstr);
								editor.putString("port", portstr);
								editor.putString("url", url);
								editor.commit();
								dialog.dismiss();
								Toast.makeText(RoutePlanDemo.this, "更改参数成功!", 
										Toast.LENGTH_SHORT).show();
							}else{
								dialog.dismiss();
								Toast.makeText(RoutePlanDemo.this,
										"输入参数格式有误", Toast.LENGTH_SHORT).show();
							}
							**/
						}
					});
					builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Log.i("bupt", "4");
								dialog.dismiss();
							}
					});
				break;
			
			}
			return builder.create();
		}
    @Override
    protected void onPause() {
    	Log.i("Route", "onPause");
    	 mMapView_route.onPause();
    	 if(mBMapManager != null){
    		 mBMapManager.stop();
    	 }
        super.onPause();
    }
   
    @Override
    protected void onResume() {
    	Log.i("Route", "onResume");
    	 mMapView_route.onResume();   	
    	 if(mBMapManager != null){
    		 mBMapManager.start();
    	 }
        super.onResume();
    }
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, 1, 1, "设置城市");
		return super.onCreateOptionsMenu(menu);
		
		
		
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getItemId()==1){
			showDialog(LOGOUT);
		}
		return super.onOptionsItemSelected(item);
	}
	
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	 mMapView_route.onSaveInstanceState(outState);
    	
    }
	@Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	 mMapView_route.onRestoreInstanceState(savedInstanceState);
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
            mMapView_route.refresh();
        }     
        void SearchButtonProcess(View v) {
    		// 处理搜索按钮响应
 //   		EditText editSt = (EditText)findViewById(R.id.start);
 //   		EditText editEn = (EditText)findViewById(R.id.end);   	
    		
    		 p1_current = new GeoPoint((int) (locData.latitude*1e6),(int) (locData.longitude*1e6));
    		
    		 SharedPreferences sp=getSharedPreferences(MyConfig.SharePreferenceFile, 0);
    	    	startCityName=sp.getString("city", "");
   // 	    	endCityName=sp.getString("endCityName", "");
    	    	Log.i("bupt", "startCityName:"+String.valueOf(startCityName));
   // 	    	Log.i("bupt", "endCityName:"+ endCityName);
    	    	if(startCityName.equals("北京 "))
    	    	{
    	    		Log.i("bupt", "step1");
    	    		busCityName = "北京";
    	    	}
    	    	else
    	    	{
    	    		Log.i("bupt", "step2");
    	    		busCityName = startCityName;
    	    	}
    	    	Log.i("bupt", "busCityName :"+busCityName);
    		// 对起点终点的name进行赋值，也可以直接对坐标赋值，赋值坐标则将根据坐标进行搜索
    		MKPlanNode stNode = new MKPlanNode();
    	//	stNode.name = editSt.getText().toString();
    		MKPlanNode enNode = new MKPlanNode();
  //  	 	Boolean b1_st = (editSt.getText()).equals(null);
    //		Boolean b1_en = (editEn.getText()).equals("");
    		Log.i("bupt","Flag is:"+Flag);
    		Log.i("bupt","v is:"+v);
    		 if(Flag == 1)
      		{
    			 Log.i("bupt","flag ==1");
    			 Log.i("bupt",startAddr);
    			 Log.i("bupt",String.valueOf(startAddr.equals(startAddr)));
    			 Log.i("bupt",String.valueOf(startAddr.equals("我的位置")));
      			if(startAddr.equals("我的位置"))
      			{
      				Log.i("BUPT","我的地址");
      			  stNode.pt = p1_current;
      			  enNode.name = endAddr; 
      			  Log.i("bupt","aaa->endAddr is:"+endAddr);
      			}
      			else
      			{
      				stNode.name = startAddr;
      				enNode.name = endAddr;
      			}
      		}
    		 else if(Flag == 2)
    		 {
    			 stNode.pt = p1_current;
    			 enNode.pt = p1_end;
    			 
    		 }
    			Log.i("bupt","search-->v is:"+v);
    			Log.i("bupt","search-->driveView is:"+driveView);
    			Log.i("bupt","search-->walkView is:"+walkView);
    			
    			/*采用gridview识别每一种item对应的view必须将view转换成为字符串的形式，使用String，valueOf（），否则
    			 * 会出现java.lang.nullpointerexception错误*/
    		if (String.valueOf(driveView).equals(String.valueOf(v))) {
    			  Log.i("bupt", "bupt-->driveView is" + driveView);
    			mSearch.drivingSearch(null,stNode,null,enNode);
    		} else if (String.valueOf(TransitView).equals(String.valueOf(v))) {
    			Log.i("bupt", "bupt-->TransitView is" + TransitView);
    			mSearch.transitSearch(busCityName, stNode, enNode);
    			
    		} else if (String.valueOf(walkView).equals(String.valueOf(v))) {
    			Log.i("bupt", "bupt-->walkView is" + walkView);
    			mSearch.walkingSearch(null, stNode, null, enNode);
    		}
    	
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
  
    	
    	
    	


}

