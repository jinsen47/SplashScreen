package com.example.splashscreen.adapter;  
import android.content.Context;  
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;  
import android.database.sqlite.SQLiteDatabase.CursorFactory;  
import android.database.sqlite.SQLiteOpenHelper;  
public class DataHelper extends SQLiteOpenHelper {  
  /*
   * SQLiteOpenHelper 的构造函数，当数据库不存在时，就会创建数据库，然后打开数据库（过程已经被封装起来了），
   * 再调用onCreate (SQLiteDatabase db)方法来执行创建表之类的操作。
   * 当数据库存在时，SQLiteOpenHelper 就不会调用onCreate (SQLiteDatabase db)方法了，
   * 它会检测版本号，若传入的版本号高于当前的，就会执行onUpgrade()方法来更新数据库和版本号。
   * 
   */
    @Override  
    public synchronized void close() {  
        // TODO Auto-generated method stub  
        super.close();  
    }  
    public DataHelper(Context context, String name, CursorFactory factory,  
            int version) {  
        super(context, name, factory, version);  
        // TODO Auto-generated constructor stub  
    }  
    @Override  
    public void onCreate(SQLiteDatabase db) {  
        // 在数据库sql.db不存在时,才执行该函数,即存在后只执行一次 
    	//表数据需要与simplecursoradapter绑定时需要设定唯一主键是_id
    	//其他的表可以不设定
       try{
    	   String sql1= "CREATE TABLE taskitems(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
    	   		"task_no VARCHAR NOT NULL UNIQUE,powerStation VARCHAR,"+
    			"tg_name VARCHAR,faultCode VARCHAR,buildDate VARCHAR,"+
    	   		"longitude VARCHAR,latitude VARCHAR,uploadedflag VARCHAR default 0)"; 
    	   String sql2="CREATE TABLE consitems(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
    	   		"task_no VARCHAR NOT NULL,cons_no VARCHAR NOT NULL,"+
    			"cons_name VARCHAR,cons_address VARCHAR,rtua VARCHAR,"+
    	   		"meter_code VARCHAR,faultCode VARCHAR,longitude VARCHAR,"+
    			"latitude VARCHAR,maintenanceDate VARCHAR ,faultCode_up VARCHAR ,"+
    			"faultDescribe VARCHAR  ,maintenanceCode VARCHAR ,"+
    			"maintenanceDescribe VARCHAR ,maintenanceResult VARCHAR  ,"+
    	   		"finishflag VARCHAR default 0,uploadedflag VARCHAR default 0, "+
    			"new_longitude VARCHAR default 0, new_latitude VARCHAR default 0,"+
    			"UNIQUE(task_no,cons_no))";
    	   String sql3="CREATE TABLE orgno(code VARCHAR PRIMARY KEY,value VARCHAR)";
    	   String sql4="CREATE TABLE faultcode(code VARCHAR PRIMARY KEY,value VARCHAR)";
    	   String sql5="CREATE TABLE maintenancecode(code VARCHAR PRIMARY KEY,value VARCHAR)";
        db.execSQL(sql1);  //创建一个工单表
        db.execSQL(sql2);//创建一个用户表
        db.execSQL(sql3);  //创建一个单位码表
        db.execSQL(sql4);//创建一个错误代码表
        db.execSQL(sql5);  //创建一个维修代码表
     
        //使用SimpleCursorDapter所设计的数据表系统默认有_id这个字段
        
       }catch(SQLException e){
    	   e.printStackTrace();
       }
    }  
    @Override  
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  
        // 当数据库版本发生变化时使用 ,保留原来的数据来更新
    	try{
    		db.execSQL("ALTER TABLE taskitems ADD COLUMN other STRING");
    		db.execSQL("ALTER TABLE consitems ADD COLUMN other STRING");
    		db.execSQL("ALTER TABLE orgno ADD COLUMN other STRING");
    		db.execSQL("ALTER TABLE faultcode ADD COLUMN other STRING");
    		db.execSQL("ALTER TABLE maintenancecode ADD COLUMN other STRING");
    	}catch(SQLException e){
    		e.printStackTrace();
    	}
    }  
}  
