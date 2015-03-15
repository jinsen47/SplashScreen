package com.example.splashscreen;
//日期选择器datepicker学习链接http://blog.csdn.net/lganggang131/article/details/7342790
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import com.example.splashscreen.adapter.DateMyUtils;
import com.example.splashscreen.adapter.MyConfig;
import com.example.splashscreen.adapter.MySimpleAdapter;
import com.example.splashscreen.webservice.MaintenanceHistory;
import com.example.splashscreen.webservice.MeterReadingHistory;
import com.example.splashscreen.webservice.SoapHelper;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ExploreActivity extends Activity{
	private TextView begin_tv,end_tv;
	private Spinner choice_sp,dateMode_sp;
	private EditText num_et;
	private ListView explore_lv;
	private Button btn1,btn2;
	private ProgressDialog progressDialog;
	private static final int BEGIN_DATE_DIALOG_ID=0;
	private static final int END_DATE_DIALOG_ID=1;
    private String beginDateString=null, endDateString=null;
    private String mode;
    private String meter_code="";//从editview读到的表号
    private String cons_no="";
    private String default_cons_no="";//rightfragment传过来的值
    private String default_meter_code="";//由根据default_cons_no由数据库读到
	String login_id;
	String passwordmd5;
    private final String[] explore_mode={"选择户号","选择表号"}; 
    private final String[] explore_date={"最近一星期","最近一个月"};
    private Handler mhandler=new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			ArrayList<HashMap<String,Object>> exlist=new 
					ArrayList<HashMap<String, Object>>(); 
			SimpleAdapter madapter=new MySimpleAdapter(ExploreActivity.this, exlist, 
				    	R.layout.explore_listitem1, new String[]{"readingDate","indication"}, 
				    	new int[]{R.id.readingDate,R.id.indication});
			explore_lv.setAdapter(madapter);
			progressDialog.dismiss();
			switch(msg.what){
			case MyConfig.MSG_METER:
				Toast.makeText(ExploreActivity.this, "查询表历史读数记录成功！", 
						Toast.LENGTH_SHORT).show();
				MeterReadingHistory[] meterReadingHistory=(MeterReadingHistory[])msg.obj;
				int count=meterReadingHistory.length;//ArrayList是List接口的可变数组的实现
				for(int i=0;i<count+1;i++){
				    HashMap<String, Object> map=new HashMap<String, Object>();
				    if(i==0){//插入表头
				    	map.put("readingDate", "采集日期");
					    map.put("indication", "表读数");
					    exlist.add(map);
				    }else{//插入数据
				    	 map.put("readingDate", meterReadingHistory[i-1].readingDate);
						 map.put("indication",meterReadingHistory[i-1].indication);
						 exlist.add(map);
				    }
				 }
				madapter.notifyDataSetChanged();
				break;
			case MyConfig.MSG_MAINTENANCE:
				Toast.makeText(ExploreActivity.this, "查询表历史维修记录成功！", 
						Toast.LENGTH_SHORT).show();
				MaintenanceHistory[] maintenanceHistory=(MaintenanceHistory[])msg.obj;
				int cnt=maintenanceHistory.length;//ArrayList是List接口的可变数组的实现
				for(int i=0;i<cnt+1;i++){
				    HashMap<String, Object> map=new HashMap<String, Object>();
				    if(i==0){//插入表头
				    	map.put("maintenanceDate", "维修日期");
				    	map.put("falseDescribe","故障描述");
				    	map.put("maintenanceDescribe", "维修描述");
				    	map.put("maintenancerName", "维修人");
					    exlist.add(map);
				    }else{//插入数据
				    	map.put("maintenanceDate", maintenanceHistory[i-1].maintenanceDate);
				    	map.put("falseDescribe",maintenanceHistory[i-1].faultDescribe);
				    	map.put("maintenanceDescribe", maintenanceHistory[i-1].maintenanceDescribe);
				    	map.put("maintenancerName", maintenanceHistory[i-1].maintenancerName);
						exlist.add(map);
				    }
				 }
				madapter=new MySimpleAdapter(ExploreActivity.this, exlist, 
			    		R.layout.explore_listitem2, new String[]{"maintenanceDate","falseDescribe",
			    		"maintenanceDescribe","maintenancerName"}, 
			    		new int[]{R.id.maintenanceDate,R.id.faultDescribe,
			    		R.id.maintenanceDescribe,R.id.maintenancerName});
			    explore_lv.setAdapter(madapter);
				break;
			case MyConfig.MSG_FAILED:
				Toast.makeText(ExploreActivity.this, "暂无历史记录", 
						Toast.LENGTH_SHORT).show();
				if(exlist!=null){
					exlist.clear();
					madapter.notifyDataSetChanged();
				}
				break;
			case MyConfig.MSG_ERROR:
				Toast.makeText(ExploreActivity.this, "网络错误", 
						Toast.LENGTH_SHORT).show();
				if(exlist!=null){
					exlist.clear();
					madapter.notifyDataSetChanged();
				}
				break;
			}
			return true;
		}
	});
    
	@Override
	protected Dialog onCreateDialog(int id) {
		Calendar calBeginDate=Calendar.getInstance();
		Calendar calEndDate=Calendar.getInstance();
		Dialog dialog=null;  
		switch(id){
		case BEGIN_DATE_DIALOG_ID:
			DatePickerDialog.OnDateSetListener beginSetDateListener=new
						DatePickerDialog.OnDateSetListener() {	
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear,
						int dayOfMonth) {
						String tmpMonth=String.valueOf(monthOfYear+1);
						String tmpDay=String.valueOf(dayOfMonth);
						if((monthOfYear>-1)&&(monthOfYear<9))
							tmpMonth="0"+tmpMonth;
						if((dayOfMonth>0)&&(dayOfMonth<10))
							tmpDay="0"+tmpDay;
						String beginDatetemp=new StringBuilder().append(year).append("-").
							append(tmpMonth).append("-").append(tmpDay).toString();
						begin_tv.setText(beginDatetemp);
				}
			};
			dialog=new DatePickerDialog(this, beginSetDateListener, 
					calBeginDate.get(Calendar.YEAR), calBeginDate.get(Calendar.MONTH), 
					calBeginDate.get(Calendar.DAY_OF_MONTH));
			break;
		case END_DATE_DIALOG_ID:
			DatePickerDialog.OnDateSetListener endSetDateListener=new
						DatePickerDialog.OnDateSetListener() {	
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear,
						int dayOfMonth) {
					String tmpMonth=String.valueOf(monthOfYear+1);
					String tmpDay=String.valueOf(dayOfMonth);
					if((monthOfYear>-1)&&(monthOfYear<9))
						tmpMonth="0"+tmpMonth;
					if((dayOfMonth>0)&&(dayOfMonth<10))
						tmpDay="0"+tmpDay;
						String endDatetemp=new StringBuilder().append(year).append("-").
								append(tmpMonth).append("-").append(tmpDay).toString();
						end_tv.setText(endDatetemp);
				}
			};
			dialog=new DatePickerDialog(this, endSetDateListener, 
					calEndDate.get(Calendar.YEAR), calEndDate.get(Calendar.MONTH), 
					calEndDate.get(Calendar.DAY_OF_MONTH));		
			break;	
		default:    
			break;  
		}
		return dialog;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);//必须在加载content之前执行
		setContentView(R.layout.explore);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar);
		TextView tv=(TextView)findViewById(R.id.titleTv);
		tv.setText("查询历史记录");
		
		Bundle extras=getIntent().getExtras();
		if(extras!=null){
			default_cons_no=extras.getString("cons_no");//如果没有获取到默认为""
			Log.i("default_cons_no",default_cons_no);
		}  
		default_meter_code=getMeterCode(
				new String[]{default_cons_no},new String[]{"meter_code"});//得到默认的表号
		Log.i("default_meter_code",default_meter_code);
		initView();
				
	}
	//根据cons_no=parms[0]查数据库字段queryColumns的值
	String getMeterCode(String[] parms,String[] queryColumns){
		String meter_code="";
		//String[] parms={default_cons_no};
		//String[] queryColumns={"meter_code"};
		SQLiteDatabase db=openOrCreateDatabase(MyConfig.DB_NAME, MODE_PRIVATE,null);
		try{
			Cursor cursor=db.query(MyConfig.CONS_TABLE,queryColumns, "cons_no=?", parms, null, null, null);
			if(cursor.moveToFirst()){//这里moveToFirst指该cursor的第一行
				do{
					meter_code=cursor.getString(cursor.getColumnIndex("meter_code"));	
				}while(cursor.moveToNext());
			}
			if(cursor!=null)
			    cursor.close();
		}catch(SQLiteException e){
			e.printStackTrace();
		}
		if(db!=null)
		   db.close();
		return meter_code;
	}
	//根据meter_code=parms[0],查找queryColumns的值
	String getCons_no(String[] parms,String[] queryColumns){
		String cons_no="";
		//String[] parms={default_meter_code};
		//String[] queryColumns={"cons_no"};
		SQLiteDatabase db=openOrCreateDatabase(MyConfig.DB_NAME, MODE_PRIVATE,null);
		try{
			Cursor cursor=db.query(MyConfig.CONS_TABLE,queryColumns, "meter_code=?", parms, null, null, null);
			if(cursor.moveToFirst()){//这里moveToFirst指该cursor的第一行
				do{
					cons_no=cursor.getString(cursor.getColumnIndex("cons_no"));
					Log.i("Activity_getCons_no", cons_no);
				}while(cursor.moveToNext());
			}
			if(cursor!=null)
			    cursor.close();
		}catch(SQLiteException e){
			e.printStackTrace();
		}
		if(db!=null)
		   db.close();
		return cons_no;
	}
	void initView(){
		begin_tv=(TextView)findViewById(R.id.begin_tv);
		end_tv=(TextView)findViewById(R.id.end_tv);
		//默认是最近一周
		String endDatetemp=DateMyUtils.getCurrentDate();
		String beginDatetemp=DateMyUtils.getLastWeekDate(endDatetemp);
		if(beginDatetemp!=null){
			begin_tv.setText(beginDatetemp);
			end_tv.setText(endDatetemp);
		}else{
			begin_tv.setText("开始日期");
			end_tv.setText("结束日期");
		}
		begin_tv.setOnClickListener(listener);
		end_tv.setOnClickListener(listener);
		 
		choice_sp=(Spinner)findViewById(R.id.explore_spinner);
		ArrayAdapter<String> mspinner=new ArrayAdapter<String>(this, 
				android.R.layout.simple_spinner_item, explore_mode); 
		mspinner.setDropDownViewResource(R.layout.spinner_items); 
		choice_sp.setAdapter(mspinner);
		choice_sp.setOnItemSelectedListener(itemSelectedListener);
		
		dateMode_sp=(Spinner)findViewById(R.id.explore_spinner2);
		ArrayAdapter<String> mspinner2=new ArrayAdapter<String>(this, 
				android.R.layout.simple_spinner_item, explore_date); 
		mspinner2.setDropDownViewResource(R.layout.spinner_items); 
		dateMode_sp.setAdapter(mspinner2);
		dateMode_sp.setOnItemSelectedListener(itemSelectedListener2);
		
		num_et=(EditText)findViewById(R.id.num_et);	
	    num_et.setText(default_cons_no);//初始化时是当前的户号
	    mode=explore_mode[0];//默认是户号
	    explore_lv=(ListView) findViewById(R.id.explore_list);
	   
	    SharedPreferences sp=getSharedPreferences(
	    		MyConfig.SharePreferenceFile, MODE_PRIVATE);
	    login_id=sp.getString("name", "");
	    passwordmd5=sp.getString("passwordmd5", "");
	    
	    btn1=(Button)findViewById(R.id.read_meter);
	    btn1.setOnClickListener(listener);
	    btn2=(Button)findViewById(R.id.history_task);
	    btn2.setOnClickListener(listener);
	    
		progressDialog=new ProgressDialog(ExploreActivity.this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setTitle("提示");
		progressDialog.setIcon(R.drawable.info_48_blue);
		progressDialog.setMessage("正在查询,请稍后...");
		progressDialog.setCancelable(true);	
	} 
	
	OnClickListener listener=new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.begin_tv:
				showDialog(BEGIN_DATE_DIALOG_ID);
				break;
			case R.id.end_tv:
				showDialog(END_DATE_DIALOG_ID);		
				break;
			case R.id.read_meter:
				//与服务器连接获取数据,检查本机网络
				//if(!NetWork.isMobileNetworkAvailable(ExploreActivity.this)){
				//	NetWork.showConnectionDialog(ExploreActivity.this);
				//}else{
					readinputdata(1);	
				//}	
				break;
			case R.id.history_task:
				//if(!NetWork.isMobileNetworkAvailable(ExploreActivity.this)){
				//	NetWork.showConnectionDialog(ExploreActivity.this);
				//}else{
					readinputdata(2);	
				//}	
				break;
			default:
				break;
			}
	  }
  };
  	OnItemSelectedListener itemSelectedListener=new Spinner.OnItemSelectedListener() {
  		@Override
  		public void onItemSelected(AdapterView<?> parent, View v, int position,
  				long id) {
					mode=explore_mode[position];
					if(position==1)
						num_et.setText(default_meter_code);
					else//position=0
						num_et.setText(default_cons_no);
  		}
  		@Override
  		public void onNothingSelected(AdapterView<?> arg0) {	
  		}
   };
   OnItemSelectedListener itemSelectedListener2=new Spinner.OnItemSelectedListener() {
 		@Override
 		public void onItemSelected(AdapterView<?> parent, View v, int position,
 				long id) {
					String endDatetemp=DateMyUtils.getCurrentDate();
					String beginDatetemp="";
					switch(position){
						case 0:
							beginDatetemp=DateMyUtils.getLastWeekDate(endDatetemp);
						break;
						case 1:
							beginDatetemp=DateMyUtils.getLastMonthDate(endDatetemp);
						break;
					}
					if(beginDatetemp!=null){
						begin_tv.setText(beginDatetemp);
						end_tv.setText(endDatetemp);
					}else{
						begin_tv.setText("开始日期");
						end_tv.setText("结束日期");
					}
					Log.i("beginDatechanged->",beginDatetemp);
					Log.i("endDatechanged->",endDatetemp);
 		}
 		@Override
 		public void onNothingSelected(AdapterView<?> arg0) {	
 		}
  };
   	
   	void readinputdata(int listflag){
	   if(numvalidate()&&datevalidate()){
		  if(listflag==1){
			  showlistview1();
		  }else if(listflag==2){
			  showlistview2();
		  }	  
	   }else{
		   Toast.makeText(this, "请按提示输入号码和起始和终止日期", 
				   Toast.LENGTH_SHORT).show();  
	   }   
   }
   public boolean numvalidate(){//还可以判断户号的位数和表号的位数
	   String num_temp=num_et.getText().toString();
	   if((num_temp!=null)&&(!num_temp.equals(""))){
		   if(mode.equals("选择户号")){
			   cons_no=num_temp;//当户号正确，表号可以为""
			   meter_code="";
			   System.out.println("1cons_no:"+cons_no);
			   System.out.println("1meter_code:"+meter_code);
		   }else{//选择表号
			   meter_code=num_temp;//当表号正确,户号必须也正确
			   cons_no=getCons_no(new String[]{meter_code}, new String[]{"cons_no"});
			   System.out.println("2meter_code:"+meter_code);
			   System.out.println("2cons_no:"+cons_no);
		   }
		   return true;
	   }else{
		   //Toast.makeText(this, "请按提示输入号码", Toast.LENGTH_SHORT).show(); 
		   return false;
	   }
   }
   public boolean datevalidate(){
	   String beginDatetemp=begin_tv.getText().toString();
	   String endDatetemp=end_tv.getText().toString();
	   if(!beginDatetemp.equals("")&&!endDatetemp.equals("")){
		   //System.out.println("begintime:"+beginDatetemp);
		   //System.out.println("endtime:"+endDatetemp);   
		   beginDateString=beginDatetemp;
		   endDateString=endDatetemp;
		   return true;
	   }else{
		  // Toast.makeText(this, "请输入起始结束时间", Toast.LENGTH_SHORT).show();
		   return false;
	   }
   }
   
   void showlistview1(){
	   progressDialog.show();
	   new Thread(){
		@Override
		public void run() {
			Message msg=Message.obtain();
			MeterReadingHistory[] meterReadingHistory=SoapHelper.getMeterReadingHistoryList(
					login_id, passwordmd5, cons_no, meter_code, beginDateString, endDateString);
			if((meterReadingHistory!=null)&&(meterReadingHistory.length!=0)){
				msg.what=MyConfig.MSG_METER;
				msg.obj=meterReadingHistory;		
			}else if((meterReadingHistory!=null)&&(meterReadingHistory.length==0)){
				msg.what=MyConfig.MSG_FAILED;
			}else{
				msg.what=MyConfig.MSG_ERROR;
			}
			mhandler.sendMessage(msg);
		}   
	   }.start();
   }
   void showlistview2(){
	   progressDialog.show();
	   new Thread(){
		@Override
		public void run() {
			Message msg=Message.obtain();
			MaintenanceHistory[] maintenanceHistory=SoapHelper.getMaintenanceHistoryList(
					login_id, passwordmd5, cons_no, meter_code, beginDateString, endDateString);
			if((maintenanceHistory!=null)&&(maintenanceHistory.length!=0)){//没查到数据时maintenanceHistory.length==0
				msg.what=MyConfig.MSG_MAINTENANCE;
				msg.obj=maintenanceHistory;			
			}else if((maintenanceHistory!=null)&&maintenanceHistory.length==0){
				msg.what=MyConfig.MSG_FAILED;//没有查到结果，返回长度为0的数组，即没有上传值
			}else{
				msg.what=MyConfig.MSG_ERROR;
			}
			mhandler.sendMessage(msg);
		}   
	   }.start();
   }
}
