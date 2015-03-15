package com.example.splashscreen;

import com.example.splashscreen.adapter.MyConfig;
//import com.example.splashscreen.DemoApplication;
import com.example.splashscreen.webservice.SoapHelper;
import com.example.splashscreen.webservice.Task;
import com.example.splashscreen.webservice.TaskItem;
import com.example.splashscreen.webservice.ValueCode;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Download extends Activity {

	Button codeBtn,taskBtn,conBtn,finishedBtn,deleteAllBtn;
	String login_id;
	String passwordmd5;
	String org_no;
	String[] tempTask=null;
	String TAG="Download";
	String codeCata =null;
	static boolean isTaskNoOk=false;
	//static boolean isConNoOk=false;
	//static boolean iscodeNoOk=false;
	SQLiteDatabase db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);//必须在加载content之前执行
		setContentView(R.layout.download);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar);
		TextView tv=(TextView)findViewById(R.id.titleTv);
		tv.setText("任务下载");
		initView();
		db=openOrCreateDatabase(MyConfig.DB_NAME, MODE_PRIVATE, null);
	}
	void initView(){
		codeBtn=(Button)findViewById(R.id.download_codes);
		taskBtn=(Button)findViewById(R.id.download_tasks);
		conBtn=(Button)findViewById(R.id.download_cons);
		codeBtn.setOnClickListener(myListener);
		taskBtn.setOnClickListener(myListener);
		conBtn.setOnClickListener(myListener);
		
		finishedBtn=(Button)findViewById(R.id.Delete_task);
		deleteAllBtn=(Button)findViewById(R.id.Delete_all);
		finishedBtn.setOnClickListener(myListener);
		deleteAllBtn.setOnClickListener(myListener);
		
		/*DemoApplication app=(DemoApplication)getApplicationContext();
		login_id=app.getLogin_id();
		passwordmd5=app.getPasswordmd5();
		org_no=app.getOrg_no();	
		*/
		SharedPreferences sp=this.getSharedPreferences(
				MyConfig.SharePreferenceFile, MODE_PRIVATE);
		login_id=sp.getString("name", "");
		passwordmd5=sp.getString("passwordmd5", "");
		org_no=sp.getString("org_no", "");
		
	}
	
	View.OnClickListener myListener=new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.download_codes:
				String[] code={"FaultCode","MaitenanceCode","orgNo"};
				for(int i=0;i<code.length;i++){
					new GetCodeWork().execute(login_id,passwordmd5,code[i]);
				}
				break;
			case R.id.download_tasks://执行异步下载列表
				new GetTaskWork().execute(login_id,passwordmd5,org_no);
				break;
			case R.id.download_cons:
				if(isTaskNoOk){
					for(int i=0;i<tempTask.length;i++){
						new GetConWork().execute(login_id,passwordmd5,tempTask[i]);
					}	
				}
				break;
			case R.id.Delete_task:{
				AlertDialog.Builder builder1=new AlertDialog.Builder(Download.this);
				builder1.setMessage("确定要删掉已完成的任务吗?(不可恢复)");
				builder1.setTitle("提示");
				builder1.setIcon(R.drawable.info_48_blue);
				builder1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						db.delete(MyConfig.CONS_TABLE, "uploadedflag=?",new String[]{"1"});
						db.delete(MyConfig.TASK_TABLE, "uploadedflag=?", new String[]{"1"});
						Toast.makeText(Download.this, "已完成任务删除成功", Toast.LENGTH_SHORT).show();
						dialog.dismiss();
					}
				});
				builder1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder1.create().show();
				}
				break;
			case R.id.Delete_all:{
				AlertDialog.Builder builder2=new AlertDialog.Builder(Download.this);
				builder2.setMessage("确定要删掉所有的数据吗?(不可恢复)");
				builder2.setTitle("提示");
				builder2.setIcon(R.drawable.info_48_blue);
				builder2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						db.delete(MyConfig.OrgNo_TALBE, null, null);
						db.delete(MyConfig.FaultCode_TABLE, null, null);
						db.delete(MyConfig.MaintenanceCode_TABLE, null, null);
						db.delete(MyConfig.TASK_TABLE,null, null);
						db.delete(MyConfig.CONS_TABLE, null, null);
						Toast.makeText(Download.this, "数据删除成功", Toast.LENGTH_SHORT).show();
						dialog.dismiss();
					}
				});
				builder2.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder2.create().show();
				}
				break;
			default:
				break;
			}
			
		}
	};
	class GetTaskWork extends AsyncTask<String, Integer, Task[]>{
		ProgressDialog taskProgressDialog;
		@Override
		protected void onPreExecute() {
			taskProgressDialog=new ProgressDialog(Download.this);
			taskProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			taskProgressDialog.setIcon(R.drawable.info_48_blue);
			taskProgressDialog.setTitle("提示");
			taskProgressDialog.setMessage("正在下载任务表...");
			taskProgressDialog.setCancelable(true);
			taskProgressDialog.setProgress(0);
			taskProgressDialog.show();
		}
		@Override
		protected Task[] doInBackground(String... params) {//可以传入多个参数
			Task[] myTask=null;
			publishProgress(0);//将会调用onProgressUpdate(Integer... progress)方法  
			myTask=SoapHelper.getTaskList(params[0],params[1],params[2]);
			publishProgress(100);
			return myTask;
		}
		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			taskProgressDialog.setProgress(values[0]);
		}
		@Override
		protected void onPostExecute(Task[] result) {
			// TODO Auto-generated method stub
			taskProgressDialog.dismiss();
			if((result!=null)&&(result.length!=0)){//result==null 服务器网络故障
				//result.length=0 输入参数错误等原因导致没有获得结果
				Toast.makeText(Download.this, "成功获取任务列表", 
						Toast.LENGTH_SHORT).show();
				tempTask=new String[result.length];
				for(int i=0;i<result.length;i++){//获取任务号表
					Log.i(TAG, "task_no->"+String.valueOf(result[i].task_no));
					tempTask[i]=result[i].task_no;
				}
				isTaskNoOk=true;
				//插入数据入taskitems
				db.beginTransaction();
				try{
					for(int i=0;i<result.length;i++){
						String fault_temp=null;
						String orgno_temp=null;
						ContentValues cv=new ContentValues();
						cv.put("task_no", result[i].task_no);
						//带来了问题--------------------------------------------
						if(!result[i].org_no.equals("null")){
							orgno_temp=code2value(db, MyConfig.OrgNo_TALBE, result[i].org_no);
						}else{
							orgno_temp="无";
						}
						cv.put("powerStation", orgno_temp);
						cv.put("tg_name", result[i].tg_name);
						if(!result[i].faultCode.equals("null")){
							fault_temp=code2value(db, MyConfig.FaultCode_TABLE, result[i].faultCode);
						}else{
							fault_temp="无";
						}
						cv.put("faultCode", fault_temp);						
						cv.put("buildDate", result[i].buildDate);
						cv.put("latitude",result[i].latitude);
						cv.put("longitude",result[i].longitude);
						db.insert(MyConfig.TASK_TABLE, null, cv);
					}
					db.setTransactionSuccessful();
				}catch(Exception e){
					e.printStackTrace();
					Toast.makeText(Download.this, "插入工单表失败", Toast.LENGTH_SHORT).show();
				}finally{
					db.endTransaction();
				}
				Toast.makeText(Download.this, "插入工单表成功", Toast.LENGTH_SHORT).show();
			}else if((result!=null)&&(result.length==0)){//目前参数由系统内部处理，不会出现，一般是服务器网络故障
				Toast.makeText(Download.this, "任务列表获取失败",
						Toast.LENGTH_SHORT).show();
				isTaskNoOk=false;
			}else{
				Toast.makeText(Download.this, "网络错误",
						Toast.LENGTH_SHORT).show();
				isTaskNoOk=false;
			}
		}
		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			taskProgressDialog.setProgress(0);
		}
	}
	class GetConWork extends AsyncTask<String, Integer, TaskItem[]>{
		ProgressDialog conProgressDialog;
		@Override
		protected void onPreExecute() {
			conProgressDialog=new ProgressDialog(Download.this);
			conProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			conProgressDialog.setTitle("提示");
			conProgressDialog.setIcon(R.drawable.info_48_blue);
			conProgressDialog.setMessage("正在下载用户表...");
			conProgressDialog.setCancelable(true);
			conProgressDialog.setProgress(0);
			conProgressDialog.show();
		}
		@Override
		protected TaskItem[] doInBackground(String... params) {
			// TODO Auto-generated method stub
			TaskItem[] taskItem=null;
			publishProgress(0);//将会调用onProgressUpdate(Integer... progress)方法  
			taskItem=SoapHelper.getTaskItemList(params[0],params[1],params[2]);
			publishProgress(100);
			return taskItem;
		}
		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			conProgressDialog.setProgress(0);
		}
		@Override
		protected void onPostExecute(TaskItem[] result) {
			conProgressDialog.dismiss();
			if((result!=null)&&(result.length!=0)){//result==null 服务器网络故障
				//result.length=0 输入参数错误
				Toast.makeText(Download.this, "成功获取用户列表", 
						Toast.LENGTH_SHORT).show();
				for(int i=0;i<result.length;i++){//获取任务号表
					Log.i(TAG, "cons_no->"+String.valueOf(result[i].cons_no));
				}
				isTaskNoOk=false;//成功获取数据后标志关闭
				//isConNoOk=true;
				//插入数据入consitems
				db.beginTransaction();
				try{
					for(int i=0;i<result.length;i++){
						Log.i(TAG, result[i].cons_name);
						ContentValues cv=new ContentValues();
						cv.put("task_no", result[i].task_no);
						cv.put("cons_no", result[i].cons_no);
						cv.put("cons_name", result[i].cons_name);
						cv.put("cons_address",result[i].cons_address);
						cv.put("rtua", result[i].rtua);
						cv.put("meter_code", result[i].meter_code);
						String faulttemp=null;//带来问题----------------------------
						if(!result[i].faultCode.equals("null")){
							faulttemp=code2value(db, MyConfig.FaultCode_TABLE, result[i].faultCode);
						}else{
							faulttemp="无";
						}
						cv.put("faultCode", faulttemp);
						cv.put("latitude",result[i].latitude);
						cv.put("longitude",result[i].longitude);
						db.insert(MyConfig.CONS_TABLE, null, cv);
					}
					db.setTransactionSuccessful();
				}catch(Exception e){
					e.printStackTrace();
					Toast.makeText(Download.this, "插入用户表失败", Toast.LENGTH_SHORT).show();
				}finally{
					db.endTransaction();
				}
				Toast.makeText(Download.this, "插入用户表成功", Toast.LENGTH_SHORT).show();
			}else if((result!=null)&&(result.length==0)){//目前参数由系统内部处理，不会出现，一般是服务器网络故障
				Toast.makeText(Download.this, "用户列表获取失败",
						Toast.LENGTH_SHORT).show();
				//isConNoOk=false;
			}else{
				Toast.makeText(Download.this, "网络错误",
						Toast.LENGTH_SHORT).show();
				//isConNoOk=false;
			}
		}
		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			conProgressDialog.setProgress(values[0]);
		}	
	}
	class GetCodeWork extends AsyncTask<String,Integer,ValueCode[]>{
		ProgressDialog codeProgressDialog;
		@Override
		protected void onPreExecute() {
			codeProgressDialog=new ProgressDialog(Download.this);
			codeProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			codeProgressDialog.setTitle("提示");
			codeProgressDialog.setIcon(R.drawable.info_48_blue);
			codeProgressDialog.setMessage("正在下载代码表...");
			codeProgressDialog.setCancelable(true);
			codeProgressDialog.setProgress(0);
			codeProgressDialog.show();
		}
		@Override
		protected  ValueCode[] doInBackground(String... params) {
            ValueCode[] valueCode = null;
			publishProgress(0);
			valueCode = SoapHelper.getCodeList(params[0], params[1], params[2]);
			publishProgress(100);
			return valueCode;
		}
		@Override
		protected void onCancelled() {
			codeProgressDialog.setProgress(0);
		}

		@Override
		protected void onPostExecute(ValueCode[] result) {
			codeProgressDialog.dismiss();
			if((result!=null)&&(result.length!=0)){//result==null 服务器网络故障
				//result.size=0 输入参数错误
					for(int i=0;i<result.length;i++){//获取任务号表
						Log.i(TAG, "codeCatalog->"+String.valueOf(result[i].codeCatalog));			
						codeCata = String.valueOf(result[i].codeCatalog);
					}
					Toast.makeText(Download.this, "成功获取"+codeCata+"代码列表", 
							Toast.LENGTH_SHORT).show();
					//插入数据
					String TABLE_NAME = null;
					 db.beginTransaction();
						try{
							for(int i=0;i<result.length;i++){
								codeCata = String.valueOf(result[i].codeCatalog);
								ContentValues cv=new ContentValues();
								cv.put("code", result[i].code);
								cv.put("value", result[i].valueName);//这里需要转化为文字
								if(codeCata.equals("FaultCode"))
									 TABLE_NAME = MyConfig.FaultCode_TABLE;
								else if(codeCata.equals("MaitenanceCode"))
									TABLE_NAME = MyConfig.MaintenanceCode_TABLE;
								else if(codeCata.equals("orgNo"))
									TABLE_NAME = MyConfig.OrgNo_TALBE;
								db.insert( TABLE_NAME, null, cv);
								
							}
							db.setTransactionSuccessful();
						}catch(Exception e){
							e.printStackTrace();
							Toast.makeText(Download.this, "插入代码表失败", Toast.LENGTH_SHORT).show();
						}finally{
							db.endTransaction();
						}
						Toast.makeText(Download.this, "插入代码表成功", Toast.LENGTH_SHORT).show();
				}
			else if((result!=null)&&(result.length==0)){//目前参数由系统内部处理，不会出现，一般是服务器网络故障
				Toast.makeText(Download.this, "代码列表获取失败",
						Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(Download.this, "网络错误",
						Toast.LENGTH_SHORT).show();
			}
		}
		@Override
		protected void onProgressUpdate(Integer... values) {
			codeProgressDialog.setProgress(values[0]);
		}
	}
	
	String code2value(SQLiteDatabase db,String Table,String code){
		String value=null;
		try{
			String[] args={code};
			Cursor c=db.query(Table, new String[]{"value"},
					"code=?", args,null, null, null);
			if(c.moveToFirst()){
				do{
					value=c.getString(c.getColumnIndex("value"));
					Log.i("value", c.getString(c.getColumnIndex("value")));
				}while(c.moveToNext());
			}
			if(c!=null)
				c.close();
		}catch(SQLiteException e){
			e.printStackTrace();
			value=null;
		}//这里让自己关db
		return value;
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(db != null)
			{
			   db.close();
			  db = null;
			}
	}	
}
