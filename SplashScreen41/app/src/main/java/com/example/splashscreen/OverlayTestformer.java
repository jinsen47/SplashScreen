package com.example.splashscreen;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;


import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.map.MapView.LayoutParams;
import com.baidu.platform.comapi.basestruct.GeoPoint;


public class OverlayTestformer extends ItemizedOverlay<OverlayItem> {
	 public List<OverlayItem> mGeoList = new ArrayList<OverlayItem>();
     private Context mContext = null;
     public static PopupOverlay pop = null;
 //    MyLocationListenner myListener = new MyLocationListenner();
     private MapView mMapView;
     public GeoPoint pt2;
     private LocationClient mLocClient;
     private  Bitmap bmp;
     private Button mBtn = null;
//    private BDLocation location;
     private Toast mToast = null;
 		
	 public OverlayTestformer(Drawable marker,Context context , MapView mMapView){
		 super(marker);
         this.mContext = context;
         this.mMapView = mMapView;
         pop = new PopupOverlay( mMapView,new PopupClickListener() {
         @Override
         public void onClickedPopup(int index) {
         String geoLat = String.valueOf(pt2.getLatitudeE6());
         String geoLon = String.valueOf(pt2.getLongitudeE6());
         Intent intent = new Intent();
         intent.putExtra("key1_lat", geoLat);
         intent.putExtra("key2_lon", geoLon);
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


