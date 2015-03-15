package com.example.splashscreen;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.example.splashscreen.adapter.MyConfig;
/***
 * 本程序主要作用：1。调用MapManager初始化地图的key
 *           2。创建类MyGeneralListener进行网络及输入方面错误的处理
 * @author thinkpad
 *
 */

public class DemoApplication extends Application {
	
    private static DemoApplication mInstance = null;
    //private static Context context;
    public boolean m_bKeyRight = true;
    BMapManager mBMapManager = null;

    //public static final String strKey = "请输入你的key";
    public static final String strKey = "523682CC4FE88F767AE4F27B4A3F3762F73E1191" ;
    
    private String login_id="";
	private String passwordmd5="";
	private String org_no="";
	//private String URL="http://10.110.6.143:8080/axis2/services/MaintainServic?wsdl";
	//private String URL="http://222.199.184.22:8080/axis2/services/MaintainServic?wsdl";
	@Override
    public void onCreate() {
	    super.onCreate();
		mInstance = this;
		//context=getBaseContext();
		initEngineManager(this);
	}
	
	@Override
	//建议在您app的退出之前调用mapadpi的destroy()函数，避免重复初始化带来的时间消耗
	public void onTerminate() {
		// TODO Auto-generated method stub
	    if (mBMapManager != null) {
            mBMapManager.destroy();
            mBMapManager = null;
        }
		super.onTerminate();
	}
	
	public void initEngineManager(Context context) {
        if (mBMapManager == null) {
            mBMapManager = new BMapManager(context);
        }

        if (!mBMapManager.init(strKey,new MyGeneralListener())) {
            Toast.makeText(DemoApplication.getInstance().getApplicationContext(), 
                    "BMapManager  初始化错误!", Toast.LENGTH_LONG).show();
        }
	}
	
	public static DemoApplication getInstance() {
		return mInstance;
	}
	//整个程序共享变量---------
	public void setLogin_id(String str){
		this.login_id=str;
	}
	public String getLogin_id(){
		return login_id;
	}
	public void setPasswordmd5(String str){
		this.passwordmd5=str;
	}
	public String getPasswordmd5(){
		return passwordmd5;
	}
	public void setOrg_no(String str){
		this.org_no=str;
	}
	public String getOrg_no(){
		return org_no;
	}
	public static String getUrl(){
		String URL="http://222.199.184.22:8080/axis2/services/MaintainServic?wsdl";
		SharedPreferences sp=mInstance.getSharedPreferences(
				MyConfig.SharePreferenceFile, MODE_PRIVATE);
		String url=sp.getString("url", URL);
		return url;
	}
	
	public static boolean isIPAddress(String address){
		boolean flag=false;
		if(!address.equals("")&&!address.equals("null")){
			String regex ="^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\." 
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."  
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."  
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$"; 
			//^a:以a开头 b$ :以b结尾 |--or \\d{2}===\d\d两位
			Pattern p=Pattern.compile(regex);
			Matcher m=p.matcher(address);
			flag=m.matches();
		}
		Log.i("IP","IP is:"+flag);
		return flag;
	}
	public static boolean isPort(String port){
		boolean flag=false;
		if((!port.equals(""))&&(!port.equals(null))){
			Integer integer=Integer.valueOf(port);
			if (integer>0 &&integer<65536)
				flag=true;
		}
		Log.i("Port","Port is:"+flag);
		return flag;
	}
	//-----------
	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
    static class MyGeneralListener implements MKGeneralListener {
        
        @Override
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                Toast.makeText(DemoApplication.getInstance().getApplicationContext(), "您的网络出错啦！",
                    Toast.LENGTH_LONG).show();
            }
            else if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(DemoApplication.getInstance().getApplicationContext(), "输入正确的检索条件！",
                        Toast.LENGTH_LONG).show();
            }
            // ...
        }

        @Override
        public void onGetPermissionState(int iError) {
            if (iError ==  MKEvent.ERROR_PERMISSION_DENIED) {
                //授权Key错误：
                Toast.makeText(DemoApplication.getInstance().getApplicationContext(), 
                        "请在 DemoApplication.java文件输入正确的授权Key！", Toast.LENGTH_LONG).show();
                DemoApplication.getInstance().m_bKeyRight = false;
            }
        }
    }
}