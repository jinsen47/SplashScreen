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


public class OverTestFivePointformer extends ItemizedOverlay<OverlayItem> {

	public List<OverlayItem> mGeoList = new ArrayList<OverlayItem>();
     private Context mContext = null;
     static PopupOverlay pop = null;
     private Button mBtn = null;
     BDLocation location;
     Toast mToast = null;
 	 MyLocationListenner myListener = new MyLocationListenner();
     private MapView mMapView;
     private LocationClient mLocClient;
     public GeoPoint pt2;
	
	 public OverTestFivePointformer(Drawable marker,Context context ,final MapView mMapView){
         super(marker);
         this.mContext = context;
         this.mMapView = mMapView;
         pop = new PopupOverlay( mMapView,new PopupClickListener() {
            	
             @Override
             public void onClickedPopup(int index) {
            	 Log.i("bupt_Popup", "1");
            	 Log.i("bupt_onTapPop3_pt2Lat",String.valueOf(pt2.getLatitudeE6()) );  //39912338
             	Log.i("bupt_onTapPop4_pt2Lon", String.valueOf(pt2.getLongitudeE6()));  //116397559
            	 /***
                 if (null == mToast)
                     mToast = Toast.makeText(mContext, "popup item :" + index + " is clicked.", Toast.LENGTH_SHORT);
                 else mToast.setText("popup item :" + index + " is clicked.");
                 mToast.show();
              ***/
             	Log.i("bupt_onTapPop5_pt2Lat",String.valueOf(pt2.getLatitudeE6()) );
            	Log.i("bupt_onTapPop6_pt2Lon", String.valueOf(pt2.getLongitudeE6()));
        		 Log.i("bupt_Popup", "4");
        	
        	      
//         		 mLocClient.unRegisterLocationListener(myListener);
        	
            		if(index == 0){
            		
                
            	
            		Intent intent = new Intent();
                   	String geoLat = String.valueOf(pt2.getLatitudeE6());
                   	String geoLon = String.valueOf(pt2.getLongitudeE6());
                   	intent.putExtra("key1_lat", geoLat);
                   	intent.putExtra("key2_lon", geoLon);
                     	Log.i("bupt_Popup", "4");
                   	intent.setClass(mContext, RoutePlanDemo2.class);
                   	Log.i("bupt_Popup", "3");
//                   	 mMapView.getOverlays().clear();
                   	 mContext.startActivity(intent);
       
            	
            	
            	 
            	 }
            	
             }
         });
         populate();
         
     }
	 protected boolean onTap(int index) {
         Log.i("bupt_onTap", "1");
         Bitmap[] bmps = new Bitmap[3];
   //      if (index % 5 == 0) {
        	   Log.i("bupt_onTap", "2");
             try {
                 bmps[0] = BitmapFactory.decodeStream(mContext.getAssets().open("router2.png"));
     //            bmps[1] = BitmapFactory.decodeStream(mContext.getAssets().open("marker2.png"));
     //            bmps[2] = BitmapFactory.decodeStream(mContext.getAssets().open("marker3.png"));
             } catch (IOException e) {
                 e.printStackTrace();
             }
             pop.showPopup(bmps, mGeoList.get(index).getPoint(), 32);
             Log.i("bupt_onTap", "3");
     //    }
         /***
         else if (index % 5 == 1){
        	   Log.i("bupt_onTap", "4");
             try {
                 bmps[2] = BitmapFactory.decodeStream(mContext.getAssets().open("marker1.png"));
                 bmps[1] = BitmapFactory.decodeStream(mContext.getAssets().open("marker2.png"));
                 bmps[0] = BitmapFactory.decodeStream(mContext.getAssets().open("marker3.png"));
             } catch (IOException e) {
                 e.printStackTrace();
             }
             pop.showPopup(bmps, mGeoList.get(index).getPoint(), 32);
         }
         
         else {
        	   Log.i("bupt_onTap", "5");
             if (mBtn == null)
                 mBtn = new Button(mContext);
             mBtn.setText("TestTest");
             mMapView.addView(mBtn,new MapView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, mGeoList.get(index).getPoint(), MapView.LayoutParams.BOTTOM_CENTER));
         }
         ***/
         Log.i("bupt_onTap", "6");
         if (null == mToast)
             mToast = Toast.makeText(mContext, mGeoList.get(index).getTitle(), Toast.LENGTH_SHORT);
         else mToast.setText(mGeoList.get(index).getTitle());
         mToast.show();
         Log.i("bupt_onTap", "7");
         return true;
     }
     public boolean onTap(final GeoPoint pt, MapView mapView){
    	 Log.i("bupt_onTap_GeoPoint", "1");
    	 mapView = mMapView;
    	 Log.i("bupt_onTapPop1_ptLat",String.valueOf(pt.getLatitudeE6()) );
       	Log.i("bupt_onTapPop2_ptLon", String.valueOf(pt.getLongitudeE6()));
        
    	 pt2 = pt;
         if (pop != null){
            pop.hidePop();
             if (mBtn != null) {
            	 Log.i("bupt_onTap_GeoPoint", "2");
            	mapView.removeView(mBtn);
                 mBtn = null;
             }
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


