package com.example.splashscreen;

import com.baidu.mapapi.BMapManager;
import com.example.splashscreen.adapter.MyConfig;
import com.example.splashscreen.DemoApplication;
import com.example.splashscreen.webservice.MD5Builder;
import com.example.splashscreen.webservice.SoapHelper;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

public class LoginActivity extends Activity {
	
	AutoCompleteTextView cardNumAuto;
	EditText passwordET,ip,port,setCity;
	Button login,login_setting;
	CheckBox savePasswordCB;
	SharedPreferences sp;
	ProgressDialog progressDialog;
	DemoApplication app;
	private BMapManager mBMapMan;
	
	String cardNumStr;
	String passwordStr;
	String passwordmd5;
	private Handler mhandler=new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			progressDialog.dismiss();
			switch(msg.what){
			case MyConfig.MSG_LOGIN:
				Toast.makeText(LoginActivity.this, "登陆成功，正在获取用户数据……",  
						Toast.LENGTH_SHORT).show(); 
				String org_no=String.valueOf(msg.obj);
				if(savePasswordCB.isChecked()){
					SharedPreferences.Editor editor=sp.edit();
					editor.putString("name", cardNumStr);
					editor.putString("password", passwordStr);
					editor.putString("passwordmd5", passwordmd5);
					editor.putString("org_no", org_no);
					editor.putString("city","北京");
					editor.putBoolean("firstLogin", false);
					editor.commit();
					//-----------------------------------
					//app.setLogin_id(cardNumStr);
					//app.setPasswordmd5(passwordmd5);
					//app.setOrg_no(org_no);
					//app.setSkipLogin(true);
					//--------------------------------------
				}
				Intent intent=new Intent(LoginActivity.this,MenuActivity.class);
				startActivity(intent);
				LoginActivity.this.finish();
				break;
			case MyConfig.MSG_FILLALL:
				Toast.makeText(LoginActivity.this, "请输入帐号和密码", 
						Toast.LENGTH_SHORT).show();
				break;
			case MyConfig.MSG_ERROR:
				Toast.makeText(LoginActivity.this, "网络错误",  
						Toast.LENGTH_SHORT).show();
				break;
			case MyConfig.MSG_FAILED:
				Toast.makeText(LoginActivity.this, "登陆失败",  
						Toast.LENGTH_SHORT).show(); 
				break;
			}
			return true;
		}
	});
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		 app = (DemoApplication)this.getApplication();
		  if (app.mBMapManager == null) {
	            app.mBMapManager = new BMapManager(this);
	        	mBMapMan = app.mBMapManager;
	            app.mBMapManager.init(DemoApplication.strKey,new DemoApplication.MyGeneralListener());
	        }
	       
		setContentView(R.layout.login);
	
		//自动完成输入用户名,2.2默认字体颜色是白色,要设置该字体颜色
		initView();
		
	}
	void initView(){
		progressDialog=new ProgressDialog(LoginActivity.this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setTitle("提示");
		progressDialog.setIcon(R.drawable.info_48_blue);
		progressDialog.setMessage("正在登录,请稍后...");
		progressDialog.setCancelable(true);
	
		cardNumAuto=(AutoCompleteTextView) findViewById(R.id.cardNumAuto);
		passwordET=(EditText) findViewById(R.id.passwordET);
		login=(Button) findViewById(R.id.login);
		login_setting=(Button)findViewById(R.id.login_setting);
		savePasswordCB=(CheckBox) findViewById(R.id.savePasswordCB);
		//SharedPreferences是一个接口,通过方法实例化数据
		sp=this.getSharedPreferences(MyConfig.SharePreferenceFile, MODE_PRIVATE);//以默认的模式打开文件
		
		savePasswordCB.setChecked(true);// 默认为记住密码 
		cardNumAuto.setThreshold(1);// 输入1个字母就开始自动提示
		cardNumAuto.setText(sp.getString("name", "abc"));
		passwordET.setText(sp.getString("password", "welcome2abc"));
		passwordET.setInputType(InputType.TYPE_CLASS_TEXT |
				InputType.TYPE_TEXT_VARIATION_PASSWORD);//密码设置为文本方式不可见
		//隐藏密码为InputType.TYPE_TEXT_VARIATION_PASSWORD，也就是0x81  
        // 显示密码为InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD，也就是0x91
		cardNumAuto.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			//String[ ] allUserName= new String[sp.getAll().size()];
			//allUserName=sp.getAll().keySet().toArray(new String[2]);
			String name=sp.getString("name", "");
			String[] allUserName=new String[]{name};
			// sp.getAll()返回一张hash map  
            // keySet()得到的是a set of the keys.  
            // hash map是由key-value组成的 
			ArrayAdapter<String> adapter=new ArrayAdapter<String>(
					LoginActivity.this,
					android.R.layout.simple_list_item_1,//使用该布局避免下拉框颜色为白，字看不到颜色
					allUserName);
			cardNumAuto.setAdapter(adapter);
			}	
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				//passwordET.setText(sp.getString(cardNumAuto.getText().toString(), ""));
				passwordET.setText(sp.getString("password", ""));
				// 从passwordFile获取帐号名对应的密码自动补上，如果没有默认为"" 
			}
		});	
		login.setOnClickListener(new OnClickListener() {		  
			@Override
			public void onClick(View v) {
				progressDialog.show();
				new Thread(){
					@Override
					public void run() {
						cardNumStr=cardNumAuto.getText().toString();
						passwordStr=passwordET.getText().toString();	
						Message msg=Message.obtain();
						if((cardNumStr.equals(""))&&(passwordStr.equals(""))){
							msg.what=MyConfig.MSG_FILLALL;
						}else{
								passwordmd5=MD5Builder.makeMD5String(passwordStr);//需要插入线程
								String[] loginResp=SoapHelper.login(cardNumStr, passwordmd5);
								String status=loginResp[1];
								if(status.equals(MyConfig.NetError)){//包括服务器故障，本机网络故障，网络传输故障
									msg.what=MyConfig.MSG_ERROR;
								}else if(status.equals(MyConfig.Success)){
								msg.what=MyConfig.MSG_LOGIN;
								msg.obj=loginResp[0];
								}else{
									msg.what=MyConfig.MSG_FAILED;
								}
						}
						mhandler.sendMessage(msg);
					}
				}.start();
			}	
		});		
		login_setting.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder=new Builder(LoginActivity.this);
				View view=LayoutInflater.from(LoginActivity.this).inflate(
						R.layout.dialogserversetting, null);
				ip=(EditText)view.findViewById(R.id.server_ip);
				port=(EditText)view.findViewById(R.id.server_port);
				setCity=(EditText)view.findViewById(R.id.setCity);
				ip.setText(sp.getString("ip", ""));
				port.setText(sp.getString("port", ""));
				setCity.setText(sp.getString("city", "北京"));
				builder.setIcon(R.drawable.info_48_blue);
				builder.setTitle("服务器参数设置").setView(view);
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String ipstr=ip.getText().toString();
							String portstr=port.getText().toString();
							String citystr=setCity.getText().toString();
							String url="http://"+ipstr+":"+portstr+"/axis2/services/MaintainServic?wsdl";
							Log.i("LoginActivity", url);
							if(DemoApplication.isIPAddress(ipstr)&&DemoApplication.isPort(portstr)){
								//app.setUrl(url);
								SharedPreferences.Editor editor=sp.edit();
								editor.putString("ip", ipstr);
								editor.putString("port", portstr);
								editor.putString("city", citystr);
								editor.putString("url", url);
								editor.commit();
								dialog.dismiss();
								Toast.makeText(LoginActivity.this, "更改参数成功!", 
										Toast.LENGTH_SHORT).show();
							}else{
								dialog.dismiss();
								Toast.makeText(LoginActivity.this,
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
				builder.create().show();
			}
		});
	}
}
