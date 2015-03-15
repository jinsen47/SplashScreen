package com.example.splashscreen;


import com.example.splashscreen.adapter.MyConfig;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.WindowManager;
 
public class SplashScreen4 extends Activity {
	//DemoApplication app;
	//SharedPreferences mysp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//获取该activity的窗口getWindow()
		/**
		 * RGBA_8888为android的一种32位颜色格式，R,G,B,A分别用八位表示，
		 * Android默认格式是PixelFormat.OPAQUE，其是不带Alpha值的。
		 * 设置之后可以看到图片的显示效果就和在PC上看到一样，不会出现带状的轮廓线了。
		 */ 
		getWindow().setFormat(PixelFormat.RGBA_8888);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);//设置标志位，屏幕抖动
		//用来取消界面光圈和抖动问题
		setContentView(R.layout.activity_splash_screen);
		//app=(DemoApplication)getApplicationContext();
		new Handler().postDelayed(new Runnable() {//经过2.9s后开启下一个界面			
			@Override
			public void run() {
				/*if(app.getSkipLogin()){
					Intent mIntent=new Intent(SplashScreen4.this,MenuActivity.class);
					SplashScreen4.this.startActivity(mIntent);
				}else{
					Intent mIntent=new Intent(SplashScreen4.this,LoginActivity.class);
					SplashScreen4.this.startActivity(mIntent);
				}
				Log.i("SplashScreen4", String.valueOf(app.getSkipLogin()));
				*/
				SharedPreferences mysp=getSharedPreferences(MyConfig.SharePreferenceFile,
						MODE_PRIVATE);
				boolean firstLogin=mysp.getBoolean("firstLogin", true);
				Log.i("firstLogin", String.valueOf(firstLogin));
				if(firstLogin){//是否第一次登录
					Intent mIntent=new Intent(SplashScreen4.this,LoginActivity.class);
					SplashScreen4.this.startActivity(mIntent);
				}else{
					Intent mIntent=new Intent(SplashScreen4.this,MenuActivity.class);
					SplashScreen4.this.startActivity(mIntent);
				}
				SplashScreen4.this.finish();
			} 
		}, 1000);//1000ms for delay
	}

}
