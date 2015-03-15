package com.example.splashscreen;

import java.util.ArrayList;
import java.util.HashMap;

//import com.baidu.mapapi.BMapManager;
import com.example.splashscreen.adapter.DataHelper;
import com.example.splashscreen.adapter.MyConfig;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MenuActivity extends Activity {
	private String TAG = "MenuActivity";
	private long exitTime=0;
	public static MenuActivity instance=null;
	private int[ ] mImages={R.drawable.download,
			R.drawable.executing,R.drawable.location,
			R.drawable.phones,R.drawable.exploring,
			R.drawable.helps};
	private String[] mTextViews={"任务下载","任务执行","位置采集","视频通话",
			"历史查询","帮助设置"};
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);//必须在加载content之前执行
		setContentView(R.layout.activity_menu);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar);
		TextView tv=(TextView)getWindow().findViewById(R.id.titleTv);
		tv.setText("我的菜单");
		instance=this;
		Log.i(TAG, "onCreate"+System.currentTimeMillis());
	
		//建立数据库及空表
		DataHelper helper= new DataHelper(this, MyConfig.DB_NAME, null, MyConfig.DB_VERSION);  
		SQLiteDatabase db=helper.getWritableDatabase();//实例化数据库
		if(helper!=null){
			helper.close();
			db.close();
		}
		
		GridView mgridView=(GridView) findViewById(R.id.homeGrid);
		ArrayList<HashMap<String, Object>> ImgTextItem=new ArrayList<HashMap<String,Object>>();
		for(int i=0;i<6;i++){
			HashMap<String, Object> map=new HashMap<String, Object>();
			map.put("ItemImage", mImages[i]);
			map.put("ItemText", mTextViews[i]);
			ImgTextItem.add(map);
		}
	
		SimpleAdapter mAdapter=new SimpleAdapter(this, ImgTextItem,
				R.layout.griditem, new String[] {"ItemImage","ItemText"},
				new int[] {R.id.imageView,R.id.imageTitle});
		mgridView.setAdapter(mAdapter);
		mgridView.setOnItemClickListener(new GridView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			/*HashMap<String, Object> item = (HashMap<String, Object>)parent.getItemAtPosition(position);
            //当某个被点击时获取数据源的属性值
            String itemText=(String)item.get("ItemText");
           // Object object=item.get("ItemImage");
            //每个被点击时都会显示其标题
            Toast.makeText(MenuActivity.this, itemText, Toast.LENGTH_SHORT).show();
            */
			/**
	         * 点击项时触发事件
	         * 
	         * @param parent  发生点击动作的AdapterView
	         * @param view 在AdapterView中被点击的视图(它是由adapter提供的一个视图)。
	         * @param position 视图在adapter中的位置。
	         * @param rowid 被点击元素的行id。
	         */
			switch(position){
			case 0://任务下载
				Intent download=new Intent(MenuActivity.this,Download.class);
				//download 传递login_id, passwordmd5,org_no
				MenuActivity.this.startActivity(download);
				break;
			case 1://任务执行
				Intent taskintent=new Intent(MenuActivity.this,TaskListView.class);
				MenuActivity.this.startActivity(taskintent);	
				break;
			case 2://位置采集
				Toast.makeText(MenuActivity.this, 
						"位置采集正在开发中...",Toast.LENGTH_SHORT).show();
				break;
			case 3://视频通话
				Toast.makeText(MenuActivity.this, 
						"视频通话正在开发中...",Toast.LENGTH_SHORT).show();
				break;
			case 4://历史查询
				Intent historyintent=new Intent(MenuActivity.this, ExploreHistory.class);
				startActivity(historyintent);
				break;
			case 5://帮助设置
				Intent settingintent=new Intent(MenuActivity.this, SettingActivity.class);
				startActivity(settingintent);
				//startActivityForResult(settingintent,1);
				//1---requestCode 请求码,可以不同按钮打开同一个activity，通过不同请求码标志
				//这里1必须>=0,否则没法激活onActivityResult Result_OK=-1
				break;
			default:
				break;
			}	
		}
	});
}
	
/*@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//requestCode 请求码,可以不同按钮打开同一个activity，通过不同请求码标志请求来源
		//resultCode 结果码,可以一个startActivityForResult()打开不同的activity处理不同业务，标志不同的activity
		if(requestCode==1){
			switch(resultCode){
				case RESULT_CANCLED:
					break;
				case RESULT_OK:
					MenuActivity.this.finish();
					Log.i(TAG, "I'm killed"+System.currentTimeMillis());
					break;
			}
		}	
	}
*/
	protected void onPause() {
		   // 	 mBMapMan.stop();
		      Log.i(TAG, "onPause"+System.currentTimeMillis());
		    	// DemoApplication app = (DemoApplication)this.getApplication();
		    //	 app.mBMapManager.stop();
		        super.onPause();
		    }
		    
		    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK &&
				event.getAction()==KeyEvent.ACTION_DOWN){
			if((System.currentTimeMillis()-exitTime)>2000){
				Toast.makeText(MenuActivity.this, "再按一次退出", 
						Toast.LENGTH_SHORT).show();
				exitTime=System.currentTimeMillis();
			}else{
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

			@Override
		    protected void onResume() {
		  //  	 mBMapMan.start();
		    	  Log.i(TAG, "onResume"+System.currentTimeMillis());
		   // 	   DemoApplication app = (DemoApplication)this.getApplication();
		   //         app.mBMapManager.start();
		        super.onResume();
		    }
	    
	protected void onDestroy() {
		        // TODO Auto-generated method stub  
		    	  Log.i(TAG, "onDestroy"+System.currentTimeMillis());
		        super.onDestroy();
		     
//		        DemoApplication app = (DemoApplication)this.getApplication();
		  //      if (app.mBMapManager != null) {
//		            app.mBMapManager.destroy();
//		            app.mBMapManager = null;
		  //      }
		    }

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		  Log.i(TAG, "onStop"+System.currentTimeMillis());
		super.onStop();
	}  
	
	
}
