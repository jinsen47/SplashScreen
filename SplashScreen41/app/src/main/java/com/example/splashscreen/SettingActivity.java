package com.example.splashscreen;

import java.io.InputStream;

import com.example.splashscreen.adapter.MyConfig;
import com.example.splashscreen.adapter.MyReader;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends Activity{
	Button setting,check_new,logout,about;
	private final int ABOUT=0;
	private final int LOGOUT=1;
	private final int SETTING=2;
	//DemoApplication app;
	String str;
	EditText ip,port,setCity;
	SharedPreferences sp;
	SharedPreferences.Editor editor;
	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder=new Builder(SettingActivity.this);
		switch(id){
		case ABOUT:	
			builder.setTitle("关于");
			builder.setMessage(str);
			builder.setIcon(R.drawable.info_48_blue);
			builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {		
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();		
				}
			});
			break;
		case LOGOUT:
			builder.setTitle("提示");
			builder.setIcon(R.drawable.info_48_blue);
			builder.setMessage("确定退出此帐号?");
			builder.setPositiveButton("确认",new DialogInterface.OnClickListener() {	
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//app.setSkipLogin(false);
					editor.putBoolean("firstLogin", true);
					editor.putString("passwordmd5", "");
					editor.putString("org_no", "");//没有删除用户名和密码便于联想
					editor.commit();
					Intent mIntent=new Intent(SettingActivity.this,LoginActivity.class);
					startActivity(mIntent);
					dialog.dismiss();
					//SettingActivity.this.setResult(RESULT_OK);
					SettingActivity.this.finish();
					MenuActivity.instance.finish();
				}
			});
			builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();		
				}
			});
			break;
		case SETTING:
			LayoutInflater inflater=LayoutInflater.from(SettingActivity.this);
			View view=inflater.inflate(R.layout.dialogserversetting, null);
			ip=(EditText)view.findViewById(R.id.server_ip);
			port=(EditText)view.findViewById(R.id.server_port);
			setCity=(EditText)view.findViewById(R.id.setCity);
			ip.setText(sp.getString("ip", ""));
			port.setText(sp.getString("port", ""));
			setCity.setText(sp.getString("city", "北京 "));
			builder.setTitle("服务器参数设置").setView(view);
			builder.setIcon(R.drawable.info_48_blue);
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String ipstr=ip.getText().toString();
						String portstr=port.getText().toString();
						String citystr=setCity.getText().toString();
						String url="http://"+ipstr+":"+portstr+"/axis2/services/MaintainServic?wsdl";
						Log.i("SettingActivity", url);
						if(DemoApplication.isIPAddress(ipstr)&&DemoApplication.isPort(portstr)){
							//app.setUrl(url);
							editor.putString("ip", ipstr);
							editor.putString("port", portstr);
							editor.putString("city", citystr);
							editor.putString("url", url);
							editor.commit();
							dialog.dismiss();
							Toast.makeText(SettingActivity.this, "更改参数成功!", 
									Toast.LENGTH_SHORT).show();
						}else{
							dialog.dismiss();
							Toast.makeText(SettingActivity.this,
									"输入参数格式有误", Toast.LENGTH_SHORT).show();
						}
					}
				});
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
				});
			break;
		}
		return builder.create();
	}
	
	@Override
	protected void onDestroy() {
		Log.i("SettingActivity", "onDestroy"+System.currentTimeMillis());
		super.onDestroy();
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
	} 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);//必须在加载content之前执行
		setContentView(R.layout.settings);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar);
		TextView tv=(TextView)findViewById(R.id.titleTv);
		tv.setText("帮助设置");
		
		initView();
	}
	void initView(){
		setting=(Button)findViewById(R.id.universe_setting);
		check_new=(Button)findViewById(R.id.check_new);
		logout=(Button)findViewById(R.id.logout);
		about=(Button)findViewById(R.id.aboutMe);
		setting.setOnClickListener(listener);
		check_new.setOnClickListener(listener);
		logout.setOnClickListener(listener);
		about.setOnClickListener(listener);
		InputStream inputStream = getResources().openRawResource(R.raw.help);
		str = MyReader.getString(inputStream);
		//app=(DemoApplication)getApplicationContext();
		sp=this.getSharedPreferences(
				MyConfig.SharePreferenceFile, MODE_PRIVATE);
		editor=sp.edit();
		
	}
	OnClickListener listener=new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.universe_setting://go to setting
				showDialog(SETTING);
				break;
			case R.id.aboutMe://go to help
				showDialog(ABOUT);
				break;
			case R.id.check_new://go to check if it has update_version
				break;
			case R.id.logout://go to logout
				showDialog(LOGOUT);
				break;
			default:
				break;
			}	
		}
	};
	
}
