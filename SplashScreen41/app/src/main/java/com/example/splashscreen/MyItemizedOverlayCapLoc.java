package com.example.splashscreen;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.splashscreen.adapter.DataHelper;


public class MyItemizedOverlayCapLoc extends ItemizedOverlay<OverlayItem> {
	public MKMapViewListener mMapListener = null;
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context context;
	private GeoPoint p1 ;
	//数据库
	private DataHelper dbHelper = null;
	private SQLiteDatabase db = null;
	private String str1_con = null;
	private String str1_lat = null;
	private String str1_lon = null;
	public MyItemizedOverlayCapLoc(Context context, Drawable marker,DataHelper dbHelper,SQLiteDatabase db) {
		// TODO Auto-gene'trrated constructor stub
		 super(marker);
		 this.context = context;
		 this.dbHelper = dbHelper;
		 this.db = db;
	}
	public boolean onTap(GeoPoint p,MapView mapView){
		 final SharedPreferences sharedPreferences	= context.getSharedPreferences("navigation_pre", Context.MODE_WORLD_WRITEABLE);
		 //获取点击当前位置的经度和纬度
		 final float lat=p.getLatitudeE6();   
	     final float lon=p.getLongitudeE6();  	     
	     final MapView map = mapView;	   
	     AlertDialog.Builder builder = new AlertDialog.Builder(this.context);  
         builder.setTitle("设置目的地");  
         builder.setMessage("设置选中的点为目的地吗？");       
         builder.setPositiveButton("确定",new OnClickListener() {               
             public void onClick(DialogInterface dialog, int which) {
            	 List<Overlay> overlays = map.getOverlays();  
	             map.getOverlays().clear();
	           map.getOverlays().add(LocationCaptureActivity.myLocationOverlay);
	             GeoPoint gpoint = new GeoPoint((int)lat,(int)lon);  
	             OverlayItem overlayitem = new OverlayItem(gpoint, "title", "content");  
	             Drawable drawale = context.getResources().getDrawable(R.drawable.icon_marka);  
	             MyItemizedOverlayCapLoc iconOverlay = new MyItemizedOverlayCapLoc(context,drawale,dbHelper,db);  
	             // 添加图层   
	             iconOverlay.addOverlay(overlayitem);  
	             overlays.add(iconOverlay);  
	             map.getController().animateTo(gpoint);  
                 Editor editor = sharedPreferences.edit();  
                 editor.putFloat("lat", lat);  
                 editor.putFloat("lon", lon);  
                 editor.commit();  
                 Toast.makeText(context, "当前位置纬度:"+lat / 1E6+"经度:"+lon / 1E6, Toast.LENGTH_SHORT).show();
                 //数据库
                 db = dbHelper.getWritableDatabase();
                 String lat_str = String.valueOf(lat/1E6);
                 String lon_str = String.valueOf(lon/1E6);
	             ContentValues values = new ContentValues();
		         values.put("new_latitude", lat_str);
		         values.put("new_longitude", lon_str);
		         int aa = db.update("consitems", values, "cons_no = ? and task_no=?", new String[]{LocationCaptureActivity.cons_num,LocationCaptureActivity.task_num});
		         if(aa !=0)
			        {
			        	Toast.makeText(context, "插入数据库成功", Toast.LENGTH_SHORT).show();
			        }
		         Cursor cursor = db.query("consitems",new String[]{"new_latitude","new_longitude","cons_no"}, "cons_no = ?and task_no=?", new String[]{LocationCaptureActivity.cons_num,LocationCaptureActivity.task_num}, null, null, null);
				 while(cursor.moveToNext()){
				 str1_lat = cursor.getString(0);
				 str1_lon = cursor.getString(1);
				 str1_con = cursor.getString(2);
				}							
				Toast.makeText(context, "插入数据库:纬度:"+str1_lat+",经度:"+str1_lon, Toast.LENGTH_SHORT).show();
							
	              if(db != null){
					db.close();
					}
             }  
         });  
         builder.setNegativeButton("取消", null);  
         builder.create().show();  

		return super.onTap(p,mapView);
	}
	 @Override
		protected OverlayItem createItem(int i) {
			// TODO Auto-generated method stub
			return mOverlays.get(i);
		}


		public int size() {
			// TODO Auto-generated method stub
			return mOverlays.size();
		}
		
		public void addOverlay(OverlayItem overlay){
			mOverlays.add(overlay);
			populate();
		}
		
		public void removeOverlay(OverlayItem overlay){
			mOverlays.remove(overlay);
			populate();
		}
		
		protected boolean onTap(int index){
			return true;
		}
		
		
		
	

}
