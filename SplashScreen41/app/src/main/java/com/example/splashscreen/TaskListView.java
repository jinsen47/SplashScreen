package com.example.splashscreen;  

import com.example.splashscreen.adapter.MyConfig;
import com.example.splashscreen.adapter.MySimpleCursorAdapter;

import android.app.Activity;  
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase; 
import android.os.Bundle;  
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;  
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
  
public class TaskListView extends Activity {  
	private String TAG = "TaskListView";
    SQLiteDatabase sqldb;  
  
    String[] ColumnNames1 = {"uploadedflag","task_no", "powerStation", "tg_name", "faultCode","buildDate" };  
	int[] TaskID={R.id.taskflag, R.id.task_no,  R.id.powerStation, R.id.tg_name, R.id.faultCode,R.id.buildDate };
    ListView lv;  
   
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        // TODO Auto-generated method stub  
        super.onCreate(savedInstanceState);      
   
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);//必须在加载content之前执行
		setContentView(R.layout.tasklistview);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar);
		TextView tv=(TextView)findViewById(R.id.titleTv);
		tv.setText("任务清单");
		
        sqldb=openOrCreateDatabase(MyConfig.DB_NAME, MODE_PRIVATE, null);
        lv = (ListView) findViewById(R.id.tasklistview);  
        Cursor cr = sqldb.query(MyConfig.TASK_TABLE, null, null, null, null, null, null);  
        SimpleCursorAdapter adapter = new MySimpleCursorAdapter(this,  
                R.layout.tasklistitems, cr, ColumnNames1, TaskID); 
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
			@Override
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
				if(view.getId()==R.id.taskflag){
					String uploadedflag=cursor.getString(cursor.getColumnIndex("uploadedflag"));
					if(uploadedflag.equals("1")){
						((ImageView)view).setImageDrawable(getResources().
								getDrawable(R.drawable.star_light));
					}else{
						((ImageView)view).setImageDrawable(getResources().
								getDrawable(R.drawable.star_white));
					}
					return true;
				}
				return false;
			}
		});
        lv.setAdapter(adapter);  
        startManagingCursor(cr);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) { 
				switch(parent.getId()){
				//获取prarent对应的listview 再根据position获取每个item的view,
				//然后定位到需要保存数据的task_id
				case R.id.tasklistview://这里对应的是listview的ID
					ListView listView=(ListView) parent;
					//View mView=listView.getChildAt(position);
					//TextView text1=(TextView) mView.findViewById(R.id.task_no);
					Cursor cr=(Cursor)listView.getItemAtPosition(position);
					String task_no=cr.getString(cr.getColumnIndex("task_no"));
					startManagingCursor(cr);
					Intent intent=new Intent(TaskListView.this,ConsListview.class);
					intent.putExtra("task_no", task_no);//给另外一个activity传值task_id
					//startActivityForResult(intent, TASK_ID);
					startActivity(intent);	
					break;
				default:
					break;
				}
			}   	
		});
    }  
   
   
    @Override  
    protected void onDestroy() {// 关闭数据库  
        // TODO Auto-generated method stub  
    	Log.i(TAG,"onDestroy");
        super.onDestroy();  
        if (sqldb != null) {  
            sqldb.close();  
        }  
    }  
  
}  
