package com.example.splashscreen;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;      
import android.view.Window;
import android.widget.TextView;

public class Fragment2Activity extends FragmentActivity {
	//SQLiteDatabase db;
	

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);//必须在加载content之前执行
		setContentView(R.layout.activity_fragment);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar);
		TextView tv=(TextView)findViewById(R.id.titleTv);
		tv.setText("用户菜单");
		
	}	
}
