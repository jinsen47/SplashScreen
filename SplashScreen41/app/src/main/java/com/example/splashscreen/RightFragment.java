package com.example.splashscreen;

import java.util.ArrayList;
import java.util.HashMap;
import com.example.splashscreen.adapter.DateMyUtils;
import com.example.splashscreen.adapter.MyConfig;
import com.example.splashscreen.adapter.PopulAdapter;
import com.example.splashscreen.adapter.PopulAdapter.ViewHolder;
import com.example.splashscreen.webservice.MaintenanceItem;
import com.example.splashscreen.webservice.SoapHelper;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class RightFragment extends Fragment {
	//chulicode ---maintaincecode,guzhangcode---faultcode varchar2(10 byte) 
	//AL32UTF8的编码集1个汉字对应2个字节 不超过5个汉字

	private GridView consGridView;
	private TextView popul_tv1,popul_tv2,popul_tv3,popul_tv4,popul_tv5;
	private String falsecode="";
	private String maintaincode="";
	private String falsedetails="";
	private String maintaindetails="";
	private String fieldfailure="";
	private int screenWidth=0;
	private int screenHeight=0;
	private int falsecode_pos=-1,maintaincode_pos=-1;
	public static String task_no;
	public static String cons_no;
	String login_id;
	String passwordmd5;
	ProgressDialog progressDialog;
	private Handler mhandler=new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			progressDialog.dismiss();
			switch(msg.what){
				case MyConfig.MSG_UPLOAD:
					Toast.makeText(getActivity(), "上传成功", 
							Toast.LENGTH_SHORT).show();
					set_uploadedflag(MyConfig.CONS_TABLE,"cons_no=? and task_no=?",
							new String[]{cons_no,task_no},true);//set cons_table uploadflag=1
					set_finishflag(false);//set finishflag=0
					//check all the uploadedflag to see whether set taskitems' uploadedflag =1
					if(checkuploadedflag())
						set_uploadedflag(MyConfig.TASK_TABLE,"task_no=?",
								new String[]{task_no},true);//set task_table uploadflag=1		
					break;	
				case MyConfig.MSG_FILLALL:
					Toast.makeText(getActivity(), "请保存后再上传", 
							Toast.LENGTH_SHORT).show();
					break;
				case MyConfig.MSG_ERROR:
					Toast.makeText(getActivity(), "网络故障", 
							Toast.LENGTH_SHORT).show();	
					set_uploadedflag(MyConfig.CONS_TABLE,"cons_no=? and task_no=?",
							new String[]{cons_no,task_no},false);
				break;
			}
			return true;
		}
	});
	@Override    
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
   
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		return inflater.inflate(R.layout.rightfragment, container, true);
		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initView();
		WindowManager windowManager = (WindowManager) getActivity().getSystemService(
				Context.WINDOW_SERVICE);
		screenWidth=windowManager.getDefaultDisplay().getWidth();
		screenHeight=windowManager.getDefaultDisplay().getHeight();
		Bundle extras=getActivity().getIntent().getExtras();
		if(extras!=null){
			task_no=extras.getString("task_no");
			cons_no=extras.getString("cons_no");	
		}
	}
	void initView(){
		showGridView();
		consGridView.setOnItemClickListener(gridviewItemClickListener);
		popul_tv1 = (TextView) getActivity().findViewById(R.id.popul_tv1);
		popul_tv1.setText("故障类型");
		popul_tv1.setOnClickListener(new TextViewClickListener());
			
		popul_tv2 = (TextView) getActivity().findViewById(R.id.popul_tv2);
		popul_tv2.setText("故障描述");
		popul_tv2.setOnClickListener(new TextViewClickListener());
		popul_tv3 = (TextView) getActivity().findViewById(R.id.popul_tv3);
		popul_tv3.setText("维修代码");
		popul_tv3.setOnClickListener(new TextViewClickListener());
		popul_tv4 = (TextView) getActivity().findViewById(R.id.popul_tv4);
		popul_tv4.setText("维修描述");
		popul_tv4.setOnClickListener(new TextViewClickListener());
		popul_tv5 = (TextView) getActivity().findViewById(R.id.popul_tv5);
		popul_tv5.setText("现场操作正常");
		popul_tv5.setOnClickListener(new TextViewClickListener()); 
		
		progressDialog=new ProgressDialog(getActivity());
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setTitle("提示");
		progressDialog.setIcon(R.drawable.info_48_blue);
		progressDialog.setMessage("正在上传,请稍后...");
		progressDialog.setCancelable(true);
		SharedPreferences sp=getActivity().getSharedPreferences(
				MyConfig.SharePreferenceFile, 0);
		login_id=sp.getString("name", "");
		passwordmd5=sp.getString("passwordmd5", "");
	}
	private class TextViewClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.popul_tv1:
				init_falsecodepopup(v);
				break;
			case R.id.popul_tv2:
				init_falsedetailspopup(v);
				break;
			case R.id.popul_tv3:
				init_maintaincodepopup(v);
				break;
			case R.id.popul_tv4:
				init_maintaindetailspopup(v);
				break;
			case R.id.popul_tv5:
				init_choicepopup(v);
				break;
			default:
				break;
			}
		}	
	}
	void showGridView(){
		int[] consImage={R.drawable.navigation48,R.drawable.location48,
				R.drawable.search48,R.drawable.photos48,
				R.drawable.save48,R.drawable.upload48};
		String[] consText={"导航","定位","查询","拍照","保存","上传"};
		consGridView=(GridView)getActivity().findViewById(R.id.consHomeGrid);
		ArrayList<HashMap<String, Object>> mgriditem=new ArrayList<HashMap<String,Object>>();
		for(int i=0;i<consText.length;i++){
			HashMap<String, Object> map=new HashMap<String, Object>();
			map.put("consImage", consImage[i]);
			map.put("consText", consText[i]); 
			mgriditem.add(map);  
		}
				
		SimpleAdapter mAdapter=new SimpleAdapter(getActivity(), mgriditem, R.layout.consgriditems, 
						new String[]{"consImage","consText"}, new int[]{R.id.consImage,R.id.consText});
		consGridView.setAdapter(mAdapter);
	}
	
	void init_falsecodepopup(View parent){
		ListView popul_lv;
		View popupview=null;//获得popupwindow的视图
		ArrayList<String> popul_list=null;
		final String faultMap="faultcode";
	
		LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		popupview = layoutInflater.inflate(R.layout.popul_list, null);
		
			// 创建一个PopuWidow对象,rightfragment宽288=480*0.6
		final PopupWindow popupWindow = new PopupWindow(popupview, screenWidth*3/5, screenHeight*3/8);
		popupWindow.setFocusable(true);// 使其聚集
		popupWindow.setOutsideTouchable(true);// 设置允许在外点击消失
		//popupWindow.setBackgroundDrawable(new BitmapDrawable());
		//设置背景,其实xml文件已经设置了
		popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popullistview_bg));
		//WindowManager windowManager = (WindowManager) getActivity().getSystemService(
		//		Context.WINDOW_SERVICE);
		// 显示的位置为:屏幕的宽度的一半-PopupWindow的宽度的一半 xPos=(480-280)/2=100,从左下角向右偏移100
		int xPos = screenWidth/2- popupWindow.getWidth() / 2;
		popupWindow.showAsDropDown(parent, xPos, 10);//默认view parent的左下角
		
		popul_lv = (ListView) popupview.findViewById(R.id.popul_lv);//获取listview
		popul_list = code2value(faultMap);
		
		PopulAdapter populAdapter = new PopulAdapter(getActivity(), popul_list);
		popul_lv.setAdapter(populAdapter);
			
		popul_lv.setItemsCanFocus(false);//去掉items被点击的优先级
		popul_lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		if(falsecode_pos!=-1){
			PopulAdapter.isLastSelected.put(falsecode_pos, true);
		}
		popul_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ViewHolder viewHolder=(ViewHolder)view.getTag();		
				viewHolder.cb.toggle();//在每次获取点击的item时改变checkbox的状态   
				PopulAdapter.isLastSelected.put(position, viewHolder.cb.isChecked());
				String temp=viewHolder.tv.getText().toString();
				falsecode=value2code(faultMap, temp);//如果没有查到将会有一些错误
				Log.i("faultcode", falsecode);
				falsecode_pos=position;
				popupWindow.dismiss();
			}
		});	
	}
	void init_maintaincodepopup(View parent){
		ListView popul_lv;
		View popupview=null;//获得popupwindow的视图
		ArrayList<String> popul_list=null;
		final String maintenanceMap="maintenancecode";
	
		LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		popupview = layoutInflater.inflate(R.layout.popul_list, null);	
			// 创建一个PopuWidow对象,rightfragment宽288=480*0.6
		final PopupWindow popupWindow = new PopupWindow(popupview, screenWidth*3/5, screenHeight*3/8);
		popupWindow.setFocusable(true);// 使其聚集
		popupWindow.setOutsideTouchable(true);// 设置允许在外点击消失
		//popupWindow.setBackgroundDrawable(new BitmapDrawable());
		//设置背景,其实xml文件已经设置了
		popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popullistview_bg));
		//WindowManager windowManager = (WindowManager) getActivity().getSystemService(
		//		Context.WINDOW_SERVICE);
		// 显示的位置为:屏幕的宽度的一半-PopupWindow的宽度的一半 xPos=(480-280)/2=100,从左下角向右偏移100
		int xPos = screenWidth/2- popupWindow.getWidth() / 2;
		popupWindow.showAsDropDown(parent, xPos, 10);//默认view parent的左下角
		
		popul_lv = (ListView) popupview.findViewById(R.id.popul_lv);//获取listview
		popul_list = code2value(maintenanceMap);
		
		PopulAdapter populAdapter = new PopulAdapter(getActivity(), popul_list);
		popul_lv.setAdapter(populAdapter);
			
		popul_lv.setItemsCanFocus(false);//去掉items被点击的优先级
		popul_lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		if(maintaincode_pos!=-1){
			PopulAdapter.isLastSelected.put(maintaincode_pos, true);
		}
		popul_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ViewHolder viewHolder=(ViewHolder)view.getTag();		
				viewHolder.cb.toggle();// 把CheckBox的选中状态改为当前状态的反,gridview确保是单一选中
				PopulAdapter.isLastSelected.put(position, viewHolder.cb.isChecked());
				String temp=viewHolder.tv.getText().toString();
				maintaincode=value2code(maintenanceMap, temp);
				Log.i("maintaincode",maintaincode);	
				maintaincode_pos=position;
				popupWindow.dismiss();
			}
		});	
	}
	void init_falsedetailspopup(View parent){
		
		View popupview=null;
		LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
		popupview = layoutInflater.inflate(R.layout.popul_edit, null);
		final PopupWindow popupWindow = new PopupWindow(popupview, screenWidth*3/5, screenHeight*3/8);   

		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popullistview_bg));
		//WindowManager windowManager = (WindowManager) getActivity().getSystemService(
		//		Context.WINDOW_SERVICE);
		int xPos = screenWidth/2- popupWindow.getWidth() / 2;
		popupWindow.showAsDropDown(parent, xPos, 10);//默认view parent的左下角
		
		Button button_ok=(Button)popupview.findViewById(R.id.button_ok);
		Button button_cancel=(Button)popupview.findViewById(R.id.button_cancel);
		final EditText edit_text=(EditText)popupview.findViewById(R.id.edit_text);
		if(!falsedetails.equals("")){
			edit_text.setText(falsedetails);
		}
		button_ok.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
			String detailstemp=edit_text.getText().toString();
				if(detailstemp!=""){
						falsedetails=detailstemp;
						System.out.println("falsedetails:"+detailstemp);
				}	
				popupWindow.dismiss();
			}
		});
		button_cancel.setOnClickListener(new  View.OnClickListener() {
			@Override
			public void onClick(View v) {
				edit_text.setText("");
			}
		});	
	}
	void init_maintaindetailspopup(View parent){
		
		View popupview=null;
		LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
		popupview = layoutInflater.inflate(R.layout.popul_edit, null);
		final PopupWindow popupWindow = new PopupWindow(popupview, screenWidth*3/5, screenHeight*3/8);   

		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popullistview_bg));
		//WindowManager windowManager = (WindowManager) getActivity().getSystemService(
		//		Context.WINDOW_SERVICE);
		int xPos = screenWidth/2- popupWindow.getWidth() / 2;
		popupWindow.showAsDropDown(parent, xPos, 10);//默认view parent的左下角
		
		Button button_ok=(Button)popupview.findViewById(R.id.button_ok);
		Button button_cancel=(Button)popupview.findViewById(R.id.button_cancel);
		final EditText edit_text=(EditText)popupview.findViewById(R.id.edit_text);
		if(!maintaindetails.equals("")){
			edit_text.setText(maintaindetails);
		}
		button_ok.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
			String detailstemp=edit_text.getText().toString();
				if(detailstemp!=""){
						maintaindetails=detailstemp;
						System.out.println("maitaindetails:"+detailstemp);
				}	
				popupWindow.dismiss();
			}
		});
		button_cancel.setOnClickListener(new  View.OnClickListener() {
			@Override
			public void onClick(View v) {
				edit_text.setText("");
			}
		});	
	}
	void init_choicepopup(View parent){
		View popupview=null;
		LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
		popupview = layoutInflater.inflate(R.layout.popul_choice, null);
		final PopupWindow popupWindow = new PopupWindow(popupview, screenWidth*3/5, screenHeight/14);   

		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popullistview_bg));
		//WindowManager windowManager = (WindowManager) getActivity().getSystemService(
		//		Context.WINDOW_SERVICE);
		int xPos = screenWidth/2- popupWindow.getWidth() / 2;
		popupWindow.showAsDropDown(parent, xPos, 10);//默认view parent的左下角
		
		RadioGroup radio_choice=(RadioGroup)popupview.findViewById(R.id.radioChoice);
		RadioButton radio_yes=(RadioButton)popupview.findViewById(R.id.choice_yes);
		RadioButton radio_no=(RadioButton)popupview.findViewById(R.id.choice_no);
		if(fieldfailure.equals("1")){
			radio_yes.setChecked(true);
			radio_no.setChecked(false);
		}else if(fieldfailure.equals("0")){
			radio_no.setChecked(true);
			radio_yes.setChecked(false);
		}
		radio_choice.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(checkedId==R.id.choice_yes){
					fieldfailure="1";
					System.out.println("flagtemp:"+fieldfailure);
				}else if(checkedId==R.id.choice_no){
					fieldfailure="0";
					System.out.println("flagtemp:"+fieldfailure);
				}
				popupWindow.dismiss();
			}
		});
		/*radio_yes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {		
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					fieldfailure="1";
					System.out.println("flagtemp:"+fieldfailure);
				}
				popupWindow.dismiss();
			}
		});
        radio_no.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {		
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					fieldfailure="0";
					System.out.println("flagtemp:"+fieldfailure);
				}
				popupWindow.dismiss();
			}
		});
		*/
	} 
	
	OnItemClickListener gridviewItemClickListener=new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			switch(position){
			case 0: //goto intent navigator
				Intent intent = new Intent(getActivity(),MapBaiduActivity.class);
				intent.putExtra("task_no",task_no);
				intent.putExtra("cons_no", cons_no);
				startActivity(intent);
				break;
			case 1://go to intent location	
				Intent captureLoc_intent = new Intent(getActivity(),LocationCaptureActivity.class);
				captureLoc_intent.putExtra("task_no",task_no);
				captureLoc_intent.putExtra("cons_no", cons_no);
				startActivity(captureLoc_intent);
				break; 
			case 2:
				Intent explore_intent =new Intent(getActivity(),ExploreActivity.class);
				explore_intent.putExtra("cons_no", cons_no);
				startActivity(explore_intent);
				break;
			case 3:// go to intent take photos
				Intent takePhoto_intent = new Intent(getActivity(),TakePhotoActivity.class);
			    takePhoto_intent.putExtra("task_no",task_no);
			    takePhoto_intent.putExtra("cons_no", cons_no);
			    startActivity(takePhoto_intent);
			    
				break;
			case 4://save data, if success, write data to database and set finishflag=1
				//falsecode.equals("")&&falsedetails.equals("")&&
				//maintaincode.equals("")&&maintaindetails.equals("")&&fieldfailure.equals(""))
				if(fieldfailure.equals(""))
					Toast.makeText(getActivity(), "请至少填写 '现场是否正常' 再保存!", 
							Toast.LENGTH_SHORT).show();
				else{
					int row_num=update_uploaddata();
					if(row_num!=0){
						Toast.makeText(getActivity(), "保存信息到本地数据库!", 
								Toast.LENGTH_SHORT).show();
						set_finishflag(true);	
					}else
						Toast.makeText(getActivity(), "保存失败", 
								Toast.LENGTH_SHORT).show();
				}
				break;
			case 5:
				progressDialog.show();
				new Thread(){
					@Override
					public void run() {
						String finishflag= getFinishflagStatus();
						System.out.println("finishflag:"+finishflag);
						Message msg=Message.obtain();
						if(finishflag.equals("0")){//字符串比对用equals 而用==""检测不出来
							msg.what=MyConfig.MSG_FILLALL;
						}else if(finishflag.equals("1")){
							//if(!NetWork.isMobileNetworkAvailable(getActivity())){//检查本地网络状态，上传到服务器
							//	NetWork.showConnectionDialog(getActivity());
							MaintenanceItem items=getMaintenanceItem();
							String status=SoapHelper.postMaintenanceItems(
									login_id, passwordmd5, items);
							if(status.equals(MyConfig.Success)){
								msg.what=MyConfig.MSG_UPLOAD;
							}else{
								msg.what=MyConfig.MSG_ERROR;
							}
						}
						mhandler.sendMessage(msg);
					}	
				}.start();
				break;
			default:
				break;
			}			
		}
	};	
	
	int update_uploaddata(){//write data to database set finishflag=1
		int row_num=0;
		String maintenanceDate=DateMyUtils.getCurrentDate();
		SQLiteDatabase db=getActivity().openOrCreateDatabase(MyConfig.DB_NAME, 0, null);//MODE_PRIVATE=0x0默认
		String[] args={cons_no,task_no};
		ContentValues cv=new ContentValues();
		cv.put("maintenanceDate", maintenanceDate);
		cv.put("faultCode_up", falsecode);
		cv.put("faultDescribe", falsedetails);
		cv.put("maintenanceCode",maintaincode);
		cv.put("maintenanceDescribe", maintaindetails);
		cv.put("maintenanceResult", fieldfailure);
		row_num=db.update(MyConfig.CONS_TABLE, cv, "cons_no=? and task_no=?", args);
		if(db!=null)
		   db.close();
		return row_num;
	}
	int set_finishflag(boolean flag){
		int row_num=0;
		 SQLiteDatabase db=getActivity().openOrCreateDatabase(MyConfig.DB_NAME, 0, null);
		 String[] args={cons_no,task_no};
		 Integer a=0;
		 if(flag)
			 a=1;
		 else
			 a=0;
		 ContentValues cv=new ContentValues();
		 cv.put("finishflag", a);
		 row_num=db.update(MyConfig.CONS_TABLE, cv, "cons_no=? and task_no=?", args);
		 if(db!=null)
		    db.close();
		 return row_num;
	}
	/*
	 * 输入flag :true---置1 false----置0
	 */
	int set_uploadedflag(String table,String whereClause,
			String[] whereArgs,boolean flag){
	 int row_num=0;
	 SQLiteDatabase db=getActivity().openOrCreateDatabase(MyConfig.DB_NAME, 0, null);
	 Integer a=0;
	 if(flag)
		 a=1;
	 else
		 a=0;
	 ContentValues cv=new ContentValues();
	 cv.put("uploadedflag", a);
	 row_num=db.update(table, cv, whereClause, whereArgs);
	 if(db!=null)
	    db.close();
	 return row_num;
	}
	boolean checkuploadedflag(){
		boolean flag=false;
		SQLiteDatabase db=null;
		try{
			 db=getActivity().openOrCreateDatabase(
					 MyConfig.DB_NAME, 0, null);
			 String[] args={task_no};
			 String[] queryColumns={"uploadedflag"};
			 Cursor cr=db.query(MyConfig.CONS_TABLE, queryColumns,
					 "task_no=?", args, null, null, null);
			 int count=0;
			 for(cr.moveToFirst(); !cr.isAfterLast(); cr.moveToNext()){
				 String uploadedflag=cr.getString(cr.getColumnIndex("uploadedflag"));
				 if(uploadedflag.equals("1"))
					 count++;
			 }
			 Log.i("ConsUploadedFlag_Counts", String.valueOf(count));
			 if(count>=cr.getCount())
				 flag=true;
			 if(cr!=null)
				 cr.close();
		}catch(SQLiteException e){
			e.printStackTrace();
		}
		if(db!=null){
			 db.close();
		}   
		return flag;
	}
	String getFinishflagStatus(){
		String finishflag_status="0";
		SQLiteDatabase db=getActivity().openOrCreateDatabase(MyConfig.DB_NAME, 0, null);
		try{
			String[] args={cons_no,task_no};
			System.out.println("finishflagstatus_cons_no:"+args[0]);
			Cursor c=db.query(MyConfig.CONS_TABLE, new String[]{"finishflag"},
					"cons_no=? and task_no=?", args,null, null, null);
			if(c.moveToFirst()){
				do{
					finishflag_status=c.getString(c.getColumnIndex("finishflag"));
					//int a =c.getType(c.getColumnIndex("finishflag")); a=3--string
					System.out.println("finishflag_status:"+finishflag_status);
				}while(c.moveToNext());
			}
			if(c!=null)
				 c.close();
		}catch(SQLiteException e){
			e.printStackTrace();
		}
		if(db!=null)
		   db.close();
		return finishflag_status;
		
	}
	MaintenanceItem getMaintenanceItem(){
		MaintenanceItem maintenanceItem=null;
		SQLiteDatabase db=getActivity().openOrCreateDatabase(MyConfig.DB_NAME, 0, null);
		try{
			String[] args={cons_no,task_no};
			Cursor c=db.query(MyConfig.CONS_TABLE, new String[]{"task_no","meter_code","maintenanceDate",
					"faultCode_up","faultDescribe","maintenanceCode","maintenanceDescribe","maintenanceResult"},
					"cons_no=? and task_no=?", args,null, null, null);
			if(c.moveToFirst()){
				do{
					maintenanceItem=new MaintenanceItem();
					maintenanceItem.cons_no=cons_no;
					maintenanceItem.task_no=c.getString(c.getColumnIndex("task_no"));
					maintenanceItem.meter_code=c.getString(c.getColumnIndex("meter_code"));
					maintenanceItem.maintenanceDate=c.getString(c.getColumnIndex("maintenanceDate"));
					maintenanceItem.maintenanceCode=c.getString(c.getColumnIndex("maintenanceCode"));
					maintenanceItem.maintenanceDescribe=c.getString(c.getColumnIndex("maintenanceDescribe"));
					maintenanceItem.maintenanceResult=c.getString(c.getColumnIndex("maintenanceResult"));
					maintenanceItem.faultCode=c.getString(c.getColumnIndex("faultCode_up"));
					maintenanceItem.faultDescribe=c.getString(c.getColumnIndex("faultDescribe"));
				}while(c.moveToNext());
			}
			if(c!=null)
				 c.close();
		}catch(SQLiteException e){
			e.printStackTrace();
			maintenanceItem=null;
		}
		if(db!=null)
		   db.close();
		return maintenanceItem;
	}
	ArrayList<String> code2value(String Table){
			ArrayList<String> list=new ArrayList<String>();
			SQLiteDatabase db=getActivity().openOrCreateDatabase(MyConfig.DB_NAME, 0, null);
			try{
				Cursor c=db.query(Table, new String[]{"value"},
						null, null,null, null, null);
				if(c.moveToFirst()){
					do{
						list.add(c.getString(c.getColumnIndex("value")));
						//Log.i("value", c.getString(c.getColumnIndex("value")));
					}while(c.moveToNext());
				}
				if(c!=null)
					 c.close();
			}catch(SQLiteException e){
				e.printStackTrace();
				list=null;
			}
			if(db!=null)
			   db.close();
			return list;
		}
	String value2code(String Table,String value){
			String code=null;
			SQLiteDatabase db=getActivity().openOrCreateDatabase(MyConfig.DB_NAME, 0, null);
			try{
				String[] args={value};
				Cursor c=db.query(Table, new String[]{"code"},
						"value=?", args,null, null, null);
				if(c.moveToFirst()){
					do{
						code=c.getString(c.getColumnIndex("code"));
						//Log.i("code",c.getString(c.getColumnIndex("code")));				
					}while(c.moveToNext());
				}
				if(c!=null)
					 c.close();
			}catch(SQLiteException e){
				e.printStackTrace();
				code=null;
			}
			if(db!=null)
			   db.close();
			return code;
		}
}

