package com.example.splashscreen;

import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;

public class MyLocationListenner extends MapBaiduActivity implements BDLocationListener {

	@Override
	public void onReceiveLocation(BDLocation location) {
		// TODO Auto-generated method stub
		 if (location == null)
             return ;
         Log.i("bupt_myLocation", "1");
         locData.latitude = location.getLatitude();
         locData.longitude = location.getLongitude();
         locData.accuracy = location.getRadius();
         locData.direction = location.getDerect();
         Log.i("bupt_myLocation", "2");
         myLocationOverlay.setData(locData);
         Log.i("bupt_myLocation", "3");
         mMapView.refresh();
	}

	@Override
	public void onReceivePoi(BDLocation poiLocation) {
		// TODO Auto-generated method stub
		 if (poiLocation == null){
             return ;
         }
	}

}
