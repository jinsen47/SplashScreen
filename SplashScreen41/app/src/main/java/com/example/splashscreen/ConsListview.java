package com.example.splashscreen;

import java.util.ArrayList;
import com.example.splashscreen.adapter.MyConfig;
import com.example.splashscreen.adapter.MySimpleCursorAdapter;
import com.example.splashscreen.adapter.Popul2Adapter;
import com.example.splashscreen.webservice.MaintenanceItem;
import com.example.splashscreen.webservice.SoapHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.AsyncTask;

import android.os.Build;
//import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ConsListview extends Activity {
	ListView conslv;
	ImageButton menu;
	ProgressDialog progressDialog;
	TextView tv1,tv2,tv3;
	String mtask_no="";
	String[] ColumnNames2 = {"uploadedflag","cons_no", "cons_name", "cons_address", "rtua","meter_code","faultCode" };  
	int[] ConID={ R.id.consflag,R.id.cons_no,  R.id.cons_name, R.id.cons_address, R.id.rtua,R.id.meter_code,R.id.faultCode};
	String[] queryColumns={"_id","uploadedflag","cons_no","cons_name","cons_address","rtua",
			"meter_code","faultCode"};
	String[] orderBy={"null","cons_no","cons_name","meter_code"};
	String login_id,passwordmd5;
	SQLiteDatabase db=null;
	private int sequenceFlag=0;//排序标记默认0 户号-1 户名-2 表号-3
	//private int itemService=0;//显示默认所有条目0 未处理条目1
	private int searchFlag=4;//搜索标记默认4 户号0 户名1 表号2 全局3
	//private int dialogID=0;//批量上传0
	private String mySearchWhat="";
	private int queryMode=0;//默认0--初始，批量，所有条目；1--待处理条目；2--排序
	private int rows=0; 
	SimpleCursorAdapter adapter2;
	//Cursor myNewCursor;
	private Handler mhandler=new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch(msg.what){
				case MyConfig.MSG_UPLOAD:
					progressDialog.incrementProgressBy(1);
					Log.i("progressDialog.getProgress()->", String.valueOf(progressDialog.getProgress()));
					
					String cons_no=(String)msg.obj;
					set_uploadedflag(MyConfig.CONS_TABLE,"cons_no=? and task_no=?",
							new String[]{cons_no,mtask_no},true);//set cons_table uploadflag=1
					set_finishflag(cons_no,false);//set finishflag=0
					/*Cursor cr = db.query(MyConfig.CONS_TABLE, queryColumns, "task_no=?", 
							new String[]{mtask_no}, null, null, orderBy[sequenceFlag]); 
					adapter2.changeCursor(cr);
					startManagingCursor(cr);
					*/
					new RefreshListView().execute("task_no=?",orderBy[sequenceFlag]);
					if(progressDialog.getProgress()>=rows){
						progressDialog.dismiss();
						//check all the uploadedflag to see whether set taskitems' uploadedflag =1
						if(checkuploadedflag()){
							set_uploadedflag(MyConfig.TASK_TABLE,"task_no=?",
									new String[]{mtask_no},true);//set task_table uploadflag=1		
						}		
						Toast.makeText(ConsListview.this, "批量上传完毕", 
										Toast.LENGTH_SHORT).show();
					}
					break;	
				case MyConfig.MSG_FILLALL:
					progressDialog.dismiss();
					Toast.makeText(ConsListview.this, "没有需要上传的选项", 
							Toast.LENGTH_SHORT).show();
					break;
				case MyConfig.MSG_ERROR:
					progressDialog.dismiss();
					String cons_no2=(String)msg.obj;
					set_uploadedflag(MyConfig.CONS_TABLE,"cons_no=? and task_no=?",
							new String[]{cons_no2,mtask_no},false);
					Toast.makeText(ConsListview.this, "网络故障", 
							Toast.LENGTH_SHORT).show();	
				break;
			}
			return true;
		}
	});
	@Override
	protected Dialog onCreateDialog(int id) {//该函数只要activity不destroy只执行一次
		switch(id){
		case 0:
			progressDialog=new ProgressDialog(ConsListview.this);
			progressDialog.setTitle("批量上传");
			progressDialog.setIndeterminate(false);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setCancelable(true);
			break;
		}
		return progressDialog;
	}
	@Override //对于progressDialog进行动态刷新
	protected void onPrepareDialog(int id, Dialog dialog) {
		progressDialog.setMax(rows);//对于progressDialog.Max是变化的应该放到这里重新定义
		switch(id){//进行动态刷新
		case 0://对话框进度清零
			progressDialog.incrementProgressBy(-progressDialog.getProgress());
			new Thread(){
				@Override
				public void run() {
					MaintenanceItem[] items=getMaintenanceItem();
					if(items!=null){
						Log.i("items.length->", String.valueOf(items.length));
						for(int i=0;i<items.length;i++){
							Message msg=new Message();//等同Message.obtain(); mhandler.obtainMessage();
							String status=SoapHelper.postMaintenanceItems(
									login_id, passwordmd5, items[i]);
							Log.i("status->", status);
							msg.obj=items[i].cons_no;
							if(status.equals(MyConfig.Success)){
								msg.what=MyConfig.MSG_UPLOAD;//上传成功一个
							}else{
								msg.what=MyConfig.MSG_ERROR;//上传失败
							}
							mhandler.sendMessage(msg);
						}
					}else{
						Message msg=mhandler.obtainMessage();
						msg.what=MyConfig.MSG_FILLALL;//没有需要上传的
						mhandler.sendMessage(msg);
					}
				}
			}.start();
			break;
		}
		super.onPrepareDialog(id, dialog);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);//必须在加载content之前执行
		setContentView(R.layout.conslistview);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar2);
		
		Bundle extras=getIntent().getExtras();//Bundle使用“名字-值”来存储数据 ,获取bundle存的值
		if(extras!=null){
			mtask_no=extras.getString("task_no");//task_no传的工单号
		}
		initView();
	}    
	@Override
	protected void onRestart() {//刷新点亮的小星星，得重新查数据库，刷新数据uploadedflag
		//Cursor oldCursor=adapter2.getCursor();
		//oldCursor.close();
		Log.i("sequenceFlag", String.valueOf(sequenceFlag));
		switch(queryMode){
		case 0:
			new RefreshListView().execute("task_no=?",orderBy[sequenceFlag]);
			break;
		case 1:
			new RefreshListView().execute(
					"task_no=? and uploadedflag='0' ",orderBy[sequenceFlag]);
			break;
		case 2:
			if(!mySearchWhat.equals("")){
				searchMyManager(mySearchWhat, searchFlag);
			}else{
				new RefreshListView().execute("task_no=?",orderBy[sequenceFlag]);
			}
			break;
		}
		super.onRestart();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.i("ConsListview", "onDestroy");
		super.onDestroy();
		//if(myNewCursor!=null)
		//	myNewCursor.close();
		if(db!=null)
			db.close();
	}
	
	void initView(){
		TextView tv=(TextView)findViewById(R.id.titleTv2);
		tv1=(TextView)findViewById(R.id.cons_no_title);
		tv2=(TextView)findViewById(R.id.cons_name_title);
		tv3=(TextView)findViewById(R.id.metet_code_title);
		tv.setText("任务明细");
		
		SharedPreferences sp=getSharedPreferences(
				MyConfig.SharePreferenceFile, MODE_PRIVATE);
		login_id=sp.getString("name", "");
		passwordmd5=sp.getString("passwordmd5", "");
		menu=(ImageButton)findViewById(R.id.titlebtn);
		menu.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				init_popup(v);
			}
		});
		conslv=(ListView) findViewById(R.id.conslistview);
	 	try{//虽然不显示,但_id一定要写出来	
			db=openOrCreateDatabase(MyConfig.DB_NAME, MODE_PRIVATE, null);
			Cursor cursor= db.query(MyConfig.CONS_TABLE, queryColumns, "task_no=?", 
					new String[]{mtask_no}, null, null, orderBy[sequenceFlag]); 
			//寻找行号"工单号" mtask_no, uploadedflag='0'的数据。列号queryColumns的数据
			adapter2 = new MySimpleCursorAdapter(this,  
                    R.layout.conslistitems, cursor, ColumnNames2, ConID); 
			adapter2.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
				@Override
				public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
					if(view.getId()==R.id.consflag){
						String uploadedflag=cursor.getString(cursor.getColumnIndex("uploadedflag"));
						if(uploadedflag.equals("1")){
							((ImageView)view).setImageDrawable(
									getResources().getDrawable(R.drawable.star_light));
						}else{
							((ImageView)view).setImageDrawable(
									getResources().getDrawable(R.drawable.star_white));
						}
						return true;
					}
					return false;
				}
			});
            conslv.setAdapter(adapter2);
           // myNewCursor=cursor;
			startManagingCursor(cursor);
			conslv.setOnItemClickListener(ItemClickListener);
		}catch (SQLiteException e){
			e.printStackTrace();
		}		
	}
	
	void init_popup(View parent){
		ListView popul_lv;
		View popupview;//获得popupwindow的视图
		ArrayList<String> popul_list=new ArrayList<String>();
		
		LayoutInflater layoutInflater = (LayoutInflater)getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		popupview = layoutInflater.inflate(R.layout.popupmenu, null);	
			// 创建一个PopuWidow对象,rightfragment宽288=480*0.6
		
		WindowManager windowManager = (WindowManager)getSystemService(
				Context.WINDOW_SERVICE);
		int screenWidth=windowManager.getDefaultDisplay().getWidth();
		
		final PopupWindow popupWindow = new PopupWindow(popupview,
				screenWidth/2,LayoutParams.WRAP_CONTENT);
		popupWindow.setFocusable(true);// 使其聚集
		popupWindow.setOutsideTouchable(true);// 设置允许在外点击消失
		 // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景  
		//popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popullistview_bg));
		// 显示的位置为:屏幕的宽度的一半-PopupWindow的宽度的一半 xPos=(480-280)/2=100,从左下角向右偏移100
		int xPos = screenWidth/2- popupWindow.getWidth() / 2;
		popupWindow.showAsDropDown(parent,xPos,0);//默认view parent的左下角
		popul_lv = (ListView) popupview.findViewById(R.id.popul_lv2);//获取listview
		//initialize popuplist
		popul_list.add("排序");
		popul_list.add("用户分布");
		popul_list.add("所有条目");
		popul_list.add("未处理条目");
		popul_list.add("搜索条目");
		popul_list.add("批量上传");
		
		Popul2Adapter populAdapter = new Popul2Adapter(ConsListview.this, popul_list);
		popul_lv.setAdapter(populAdapter);	
		popul_lv.setItemsCanFocus(false);//去掉items被点击的优先级
		popul_lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		popul_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//ViewHolder viewHolder=(ViewHolder)view.getTag();		
				switch(position){
					case 0: //排序/
						String[] items={"户号","户名","表号"};
						queryMode=0;
						Dialog dialog=new AlertDialog.Builder(ConsListview.this).setTitle("排序").
							setIcon(R.drawable.star_light).setItems(items, onSequenceSelect).create();
						dialog.show();				
						break;
					case 1: //全局导航
					Intent intent =new Intent(ConsListview.this,MapFivePointActivity.class);
					intent.putExtra("task_no",mtask_no);
					startActivity(intent);
						break;
					case 2:{ //所有条目
						queryMode=0;
						tv1.setTextColor(Color.BLACK);
						tv2.setTextColor(Color.BLACK);
						tv3.setTextColor(Color.BLACK);
						/*Cursor cr1 = db.query(MyConfig.CONS_TABLE, queryColumns, 
								"task_no=?", new String[]{mtask_no}, null, null, orderBy[sequenceFlag]); 
						adapter2.changeCursor(cr1);//--------------------
						startManagingCursor(cr1);
						*/
						new RefreshListView().execute("task_no=?",orderBy[sequenceFlag]);
						break;
					}	
					case 3:{ //待处理条目
						queryMode=1;
						tv1.setTextColor(Color.BLACK);
						tv2.setTextColor(Color.BLACK);
						tv3.setTextColor(Color.BLACK);
						/*Cursor cr2 = db.query(MyConfig.CONS_TABLE, queryColumns, 
								"task_no=? and uploadedflag='0' ", new String[]{mtask_no}, null, null, orderBy[sequenceFlag]); 
						adapter2.changeCursor(cr2);
						startManagingCursor(cr2);
						*/
						new RefreshListView().execute("task_no=? and uploadedflag='0' ",orderBy[sequenceFlag]);
						break;
					}	
					case 4://搜索条目
						queryMode=2;
						View searchView=LayoutInflater.from(ConsListview.this).
												inflate(R.layout.dialogsearch, null);
						final EditText search_et=(EditText)searchView.findViewById(R.id.search_et);
						RadioGroup radioGroup=(RadioGroup)searchView.findViewById(R.id.search_group);
						radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
							@Override
							public void onCheckedChanged(RadioGroup group, int checkedId) {
								switch(checkedId){
									case R.id.searchByCons_no:
										searchFlag=0;
										tv1.setTextColor(Color.RED);
										tv2.setTextColor(Color.BLACK);
										tv3.setTextColor(Color.BLACK);
										break;
									case R.id.searchByCons_name:
										searchFlag=1;
										tv1.setTextColor(Color.BLACK);
										tv2.setTextColor(Color.RED);
										tv3.setTextColor(Color.BLACK);
										break;
									case R.id.searchByMeter_code:
										searchFlag=2;
										tv1.setTextColor(Color.BLACK);
										tv2.setTextColor(Color.BLACK);
										tv3.setTextColor(Color.RED);
										break;
									case R.id.searchByConsTable:
										searchFlag=3;
										tv1.setTextColor(Color.BLACK);
										tv2.setTextColor(Color.BLACK);
										tv3.setTextColor(Color.BLACK);
										break;
									default:
										searchFlag=4;
										break;
								}
								Log.i("searchFlagChanged", String.valueOf(searchFlag));
							}
						});
						AlertDialog.Builder builder=new AlertDialog.Builder(ConsListview.this);
						builder.setTitle("搜索条目");
						builder.setView(searchView);
						builder.setPositiveButton("开始搜索", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								String et_temp=search_et.getText().toString();
								Log.i("et_temp",et_temp);
								Log.i("searchFlagNow->", String.valueOf(searchFlag));
								if(!et_temp.equals("") && et_temp!=null && searchFlag!=4){
									mySearchWhat=et_temp;
									boolean  success=searchMyManager(et_temp, searchFlag);
									dialog.dismiss();
									if(!success){
										Toast.makeText(ConsListview.this, "抱歉,没有搜到", 
												Toast.LENGTH_SHORT).show();
									}
								}else{
									Toast.makeText(ConsListview.this, "请输入搜索的内容以及选择搜索方式",
											Toast.LENGTH_SHORT).show();
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
						break;
					case 5://批量上传
						queryMode=0;
						tv1.setTextColor(Color.BLACK);
						tv2.setTextColor(Color.BLACK);
						tv3.setTextColor(Color.BLACK);
						rows=getFinishFlagCounts();
						Log.i("finishflag_rows->", String.valueOf(rows));
						if(rows==0){
							Toast.makeText(ConsListview.this, 
									"没有需要上传的条目", Toast.LENGTH_SHORT).show();
						}else{
							showDialog(0);//批量上传表示为0
							//执行顺序  dialog is null then oncreatedialog; else onpredialog 
							//因此dialog存在后刷新只能通过onpredialog来进行。
						}
						break;
					default:
						break;
				}
				popupWindow.dismiss();
			}
		});			
	}
	OnItemClickListener ItemClickListener=new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
			ListView lv=(ListView)parent;
			Cursor cr=(Cursor)lv.getItemAtPosition(position);//获取每一格连接的数据
			String cons_no=cr.getString(cr.getColumnIndex("cons_no"));			
			startManagingCursor(cr);
			
			Intent mintent=new Intent(ConsListview.this, Fragment2Activity.class);
			mintent.putExtra("task_no", mtask_no);
			mintent.putExtra("cons_no", cons_no);
			startActivity(mintent); 
			/*switch(parent.getId()){
			case R.id.conslistview:
				ListView lv=(ListView)parent;
				View v=lv.getChildAt(position);
				break;
			}*/
		}	
	};
	DialogInterface.OnClickListener onSequenceSelect=new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch(which){
				case 0://户号
					sequenceFlag=1;
					tv1.setTextColor(Color.RED);
					tv2.setTextColor(Color.BLACK);
					tv3.setTextColor(Color.BLACK);
					break;
				case 1://户名首字母
					sequenceFlag=2;
					tv1.setTextColor(Color.BLACK);
					tv2.setTextColor(Color.RED);
					tv3.setTextColor(Color.BLACK);
					break;
				case 2://表号
					sequenceFlag=3;
					tv1.setTextColor(Color.BLACK);
					tv2.setTextColor(Color.BLACK);
					tv3.setTextColor(Color.RED);
					break;
				default:
					sequenceFlag=0;
					tv1.setTextColor(Color.BLACK);
					tv2.setTextColor(Color.BLACK);
					tv3.setTextColor(Color.BLACK);
					break;
			}
			Log.i("sequenceFlag",String.valueOf(sequenceFlag));
			/*Cursor cr = db.query(MyConfig.CONS_TABLE, queryColumns, "task_no=?", 
					new String[]{mtask_no}, null, null, orderBy[sequenceFlag]); 
			adapter2.changeCursor(cr);
            startManagingCursor(cr);
            */
			new RefreshListView().execute("task_no=?",orderBy[sequenceFlag]);
			dialog.dismiss();
		}
	};
	boolean searchMyManager(String searchWhat, int searchFlag){
		boolean isSuccess;
		//数字查询以输入值为开头 searchWhat* 以输入值结尾*searchWhat，汉字查询包括输入值
		String[] where={"task_no=? and cons_no like ?"  ," task_no=? and cons_name like ?",
				" task_no=? and meter_code like ?" ,
				"(task_no=? and cons_no like ?) or(task_no=? and cons_name like ?) or"+
				"(task_no=? and cons_address like ?) or(task_no=? and rtua like ?) or"+
				"(task_no=? and meter_code like ?) or(task_no=? and faultCode like ?)"};
		String[] selectionArgs={mtask_no, "%"+searchWhat+"%"};
		String[] selectionArgs1={mtask_no, "%"+searchWhat+"%", mtask_no, "%"+searchWhat+"%",
				mtask_no, "%"+searchWhat+"%", mtask_no, "%"+searchWhat+"%",
				mtask_no, "%"+searchWhat+"%", mtask_no, "%"+searchWhat+"%"};
		try{
			Cursor cr;
			if(searchFlag!=3){
				cr=db.query(MyConfig.CONS_TABLE, queryColumns, where[searchFlag], 
						selectionArgs, null, null, null);
			}else{
				cr=db.query(MyConfig.CONS_TABLE, queryColumns, where[searchFlag], 
						selectionArgs1, null, null, null);
			}
			if(!cr.moveToFirst()){
				isSuccess=false;
			}else{
				isSuccess=true;
			}
			//myNewCursor=cr;
			adapter2.changeCursor(cr);
			startManagingCursor(cr);
		}catch(SQLiteException e){
			e.printStackTrace();
			isSuccess=false;
		}
		return isSuccess;
	}
	int getFinishFlagCounts(){
		int rows;
		try{
			Cursor cr=db.query(MyConfig.CONS_TABLE,new String[]{"finishflag"}, 
					"task_no=? and finishflag='1' ", new String[]{mtask_no}, null, null, null);
			if(!cr.moveToFirst()){
				rows=0;
			}else{
				rows=cr.getCount();
			}
			if(cr!=null)
				cr.close();
		}catch(SQLiteException e){
			e.printStackTrace();
			rows=0;
		}
		return rows;
	}
	/**
	 * 检查尚未上传6项 finishflag='1'
	 * @return MaintenanceItem[] 上传的选项=null 没有finishflag='1' 的选项以及查询失误
	 */
	MaintenanceItem[] getMaintenanceItem(){//调用它时已经确保cr!=null
		MaintenanceItem[] items=null;
		try{
			Cursor cr=db.query(MyConfig.CONS_TABLE,new String[]{"task_no","cons_no",
					"meter_code","maintenanceDate","faultCode_up","faultDescribe",
					"maintenanceCode","maintenanceDescribe","maintenanceResult","finishflag"}, 
					"task_no=? and finishflag='1' ", new String[]{mtask_no}, null, null, null);
			if(cr.moveToFirst()){
				int count=cr.getCount();
				items=new MaintenanceItem[count];
				int i=0;
				for(cr.moveToFirst(); i<count; i++,cr.moveToNext()){
					items[i]=new MaintenanceItem();
					items[i].cons_no=cr.getString(cr.getColumnIndex("cons_no"));
					items[i].task_no=cr.getString(cr.getColumnIndex("task_no"));
					items[i].meter_code=cr.getString(cr.getColumnIndex("meter_code"));
					items[i].maintenanceDate=cr.getString(cr.getColumnIndex("maintenanceDate"));
					items[i].maintenanceCode=cr.getString(cr.getColumnIndex("maintenanceCode"));
					items[i].maintenanceDescribe=cr.getString(cr.getColumnIndex("maintenanceDescribe"));
					items[i].maintenanceResult=cr.getString(cr.getColumnIndex("maintenanceResult"));
					items[i].faultCode=cr.getString(cr.getColumnIndex("faultCode_up"));
					items[i].faultDescribe=cr.getString(cr.getColumnIndex("faultDescribe"));
				}
			}else{
				Log.i("cursor->",String.valueOf(cr));
				items=null;
			}
			if(cr!=null)
				cr.close();
		}catch(SQLiteException e){
			e.printStackTrace();
			items=null;
		}
		return items;
	}
	
	int set_finishflag(String cons_no,boolean flag){
		int row_num=0;
		 Integer a=0;
		 if(flag)
			 a=1;
		 else
			 a=0;
		 ContentValues cv=new ContentValues();
		 cv.put("finishflag", a);
		 row_num=db.update(MyConfig.CONS_TABLE, cv,
				 "cons_no=? and task_no=?", new String[]{cons_no,mtask_no});
		 return row_num;
	}
	/*
	 * 输入flag :true---置1 false----置0
	 */
	int set_uploadedflag(String table,String whereClause,
			String[] whereArgs,boolean flag){
	 int row_num=0;
	 Integer a=0;
	 if(flag)
		 a=1;
	 else
		 a=0;
	 ContentValues cv=new ContentValues();
	 cv.put("uploadedflag", a);
	 row_num=db.update(table, cv, whereClause, whereArgs);
	 return row_num;
	}
	boolean checkuploadedflag(){
		boolean flag=false;
		try{
			 Cursor cr=db.query(MyConfig.CONS_TABLE, new String[]{"uploadedflag"},
					 "task_no=?", new String[]{mtask_no}, null, null, null);
			 int count=0;
			 for(cr.moveToFirst(); !cr.isAfterLast(); cr.moveToNext()){
				 String uploadedflag=cr.getString(cr.getColumnIndex("uploadedflag"));
				 if(uploadedflag.equals("1"))
					 count++;
			 }
			 Log.i("ConsUploadedFlag_Counts", String.valueOf(count));
			 if(count>=cr.getCount())
				 flag=true;
			 cr.close();
		}catch(SQLiteException e){
			e.printStackTrace();
		}
		return flag;
	}
	//异步后台更新cursor,更新listview
	private class RefreshListView extends AsyncTask<String, Void, Cursor>{
		@Override
		protected Cursor doInBackground(String... params) {
			
			Cursor newCursor=db.query(MyConfig.CONS_TABLE, queryColumns, 
					params[0], new String[]{mtask_no}, null, null, params[1]);
			return newCursor;
		}
		@Override
		protected void onPostExecute(Cursor newCursor) {
			//Cursor oldCursor = adapter2.getCursor();
			//myNewCursor=newCursor;
			adapter2.changeCursor(newCursor);
			//stopManagingCursor(oldCursor);
			startManagingCursor(newCursor);		
		}
	}
	@Override
	public void startManagingCursor(Cursor c) {
		final int HONEYCOMB = 11;
		Log.i("SDK_INT->", String.valueOf(Build.VERSION.SDK_INT));
		// To solve the following error for honeycomb:
	    // java.lang.RuntimeException: Unable to resume activity 
	    // java.lang.IllegalStateException: trying to requery an already closed cursor
	    if (Build.VERSION.SDK_INT <HONEYCOMB) {
	        super.startManagingCursor(c);
	    }
	}
}
