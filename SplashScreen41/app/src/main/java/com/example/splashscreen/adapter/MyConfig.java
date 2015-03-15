package com.example.splashscreen.adapter;
//共享常量，有关数据库定义
public class MyConfig {
	//sqlite3 共享常量
	public final static String DB_NAME = "sql.db";  
    public final static String TASK_TABLE = "taskitems";  
    public final static String CONS_TABLE="consitems";
    public final static String OrgNo_TALBE="orgno";
    public final static String FaultCode_TABLE="faultcode";
    public final static String MaintenanceCode_TABLE="maintenancecode";
    public final static int DB_VERSION = 1;  
    
    //sharepreference共享常量
    public final static String SharePreferenceFile="passwordFile";
    
    //网络错误共享常量
    public final static String NetError="NetError";
    public final static String Success="SUCCES";
    //消息标志号
	public final static int MSG_FILLALL=0;//补全信息
	public final static int MSG_ERROR=1;//网络故障
	public final static int MSG_FAILED=2;//login参数输入出错或者没有查询到记录
	public final static int MSG_LOGIN=3;//
	public final static int MSG_UPLOAD=4;//
	public final static int MSG_METER=5;//
	public final static int MSG_MAINTENANCE=6;//
	
	//上传地理位置
	public final static int MSG_DATAINSERT = 0; //插入数据库
	public final static int MSG_UPLOADGPS = 1;//上传GPS
	public final static int MSG_NETWORKERROR = 2;//网络连接错误
	public final static int MSG_GPSSTARTERROR = 3;  //GPS未开启

	
}
