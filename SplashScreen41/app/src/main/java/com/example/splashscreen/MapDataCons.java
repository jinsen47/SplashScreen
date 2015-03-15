package com.example.splashscreen;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.baidu.platform.comapi.basestruct.GeoPoint;

public class MapDataCons {
	public String cons_no = null;
	public String longitude = null;
	public String latitude = null;
	public GeoPoint p = null;
	public Drawable drawPicture = null;
	public Context context = null;
	public MapDataCons(Context context,String cons_no,String longitude,String latitude){
		this.cons_no = cons_no;
		this.longitude = longitude;
		this.latitude = latitude;
		this.p = new GeoPoint((int) (Double.valueOf(latitude) * 1E6), (int) (Double.valueOf(longitude) * 1E6));
		this.context = context;	
		this.drawPicture = this.context.getApplicationContext().getResources().getDrawable(R.drawable.icon_marka);	
	}
	
	
}
