package com.example.splashscreen.network;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

public class NetWork {
	private static ConnectivityManager connMgr;
	public NetWork() {
		// TODO Auto-generated constructor stub
	}
	public static boolean isMobileNetworkAvailable(Context con){
		if(null==connMgr){
			connMgr=(ConnectivityManager)con.getSystemService(
					Context.CONNECTIVITY_SERVICE);
		}
		NetworkInfo wifiInfo=connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mobileInfo=connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if(wifiInfo.isAvailable())
			return true;
		else if(mobileInfo.isAvailable())
			return true;
		else
			return false;
			
	}
	// if the network isnot available,new an intent and tell user to alter network setting
	public static void showConnectionDialog(final Context conn){
		AlertDialog.Builder builder=new AlertDialog.Builder(conn);
		builder.setTitle("网络不可用").
			setIcon(android.R.drawable.ic_dialog_alert).
			setMessage("现在更改网络设置吗？").
			setPositiveButton("是", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent=new Intent(Settings.ACTION_WIRELESS_SETTINGS);
					conn.startActivity(intent);
					
				}
			})
			.setNegativeButton("取消", new OnClickListener(){  
	            @Override  
	            public void onClick(DialogInterface dialog, int which) {  
	                dialog.dismiss();  
	            }  
	        })  
	        .setCancelable(true);       
	        builder.create().show();  
	}
}
