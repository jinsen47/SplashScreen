package com.example.splashscreen.webservice;

import android.graphics.drawable.Drawable;

import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.splashscreen.R;

import android.content.Context;
public class MapData {
	public String cons_no = null;
	public String longitude = null;
	public String latitude = null;
	public GeoPoint p = null;
	public Drawable drawPicture = null;
	public Context context = null;
	public MapData(Context context,String cons_no,String longitude,String latitude){
		this.cons_no = cons_no;
		this.longitude = longitude;
		this.latitude = latitude;
		this.p = new GeoPoint((int) (Double.valueOf(latitude) * 1E6), (int) (Double.valueOf(longitude) * 1E6));
		this.context = context;
		this.drawPicture = this.context.getApplicationContext().getResources().getDrawable(R.drawable.marker_location);
	
	}
	  
	
}
