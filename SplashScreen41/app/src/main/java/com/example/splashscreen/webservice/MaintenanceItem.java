package com.example.splashscreen.webservice;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class MaintenanceItem implements KvmSerializable {
	public String cons_no=null;
	public String faultCode=null;
	public String faultDescribe=null;
	public String maintenanceCode=null;
	public String maintenanceDate=null;
	public String maintenanceDescribe=null;
	public String maintenanceResult=null;
	public String meter_code=null;
	public String task_no=null;
	public MaintenanceItem() {
		// TODO Auto-generated constructor stub
	}
	public MaintenanceItem(String cons_no,String faultCode,String faultDescribe,
			String maintenanceCode,String maintenanceDate,String maintenanceDescribe,
			String maintenanceResult,String meter_code,String task_no){
		this.cons_no=cons_no;
		this.faultCode=faultCode;
		this.faultDescribe=faultDescribe;
		this.maintenanceCode=maintenanceCode;
		this.maintenanceDate=maintenanceDate;
		this.maintenanceDescribe=maintenanceDescribe;
		this.maintenanceResult=maintenanceResult;
		this.meter_code=meter_code;
		this.task_no=task_no;
	}
	@Override
	public Object getProperty(int arg0) {
		// TODO Auto-generated method stub
		Object res=null;
		switch(arg0){
		case 0:
			res=this.cons_no;
			break;
		case 1:
			res=this.faultCode;
			break;
		case 2:
			res=this.faultDescribe;
			break;
		case 3:
			res=this.maintenanceCode;
			break;
		case 4:
			res=this.maintenanceDate;
			break;
		case 5:
			res=this.maintenanceDescribe;
			break;
		case 6:
			res=this.maintenanceResult;
			break;
		case 7:
			res=this.meter_code;
			break;
		case 8:
			res=this.task_no;
			break;
		default:
			break;
		}
		return res;
	}

	@Override
	public int getPropertyCount() {
		// TODO Auto-generated method stub
		return 9;
	}

	@Override
	public void getPropertyInfo(int arg0, Hashtable arg1, PropertyInfo arg2) {
		// TODO Auto-generated method stub
		switch(arg0){
		case 0:
			arg2.type=PropertyInfo.STRING_CLASS;
			arg2.name="cons_no";
			break;
		case 1:
			arg2.type=PropertyInfo.STRING_CLASS;
			arg2.name="faultCode";
			break;
		case 2:
			arg2.type=PropertyInfo.STRING_CLASS;
			arg2.name="faultDescribe";
			break;
		case 3:
			arg2.type=PropertyInfo.STRING_CLASS;
			arg2.name="maintenanceCode";
			break;
		case 4:
			arg2.type=PropertyInfo.STRING_CLASS;
			arg2.name="maintenanceDate";
			break;
		case 5:
			arg2.type=PropertyInfo.STRING_CLASS;
			arg2.name="maintenanceDescribe";
			break;
		case 6:
			arg2.type=PropertyInfo.STRING_CLASS;
			arg2.name="maintenanceResult";
			break;
		case 7:
			arg2.type=PropertyInfo.STRING_CLASS;
			arg2.name="meter_code";
			break;
		case 8:
			arg2.type=PropertyInfo.STRING_CLASS;
			arg2.name="task_no";
			break;
		default:
			break;
		}

	}

	@Override
	public void setProperty(int arg0, Object arg1) {
		// TODO Auto-generated method stub
		if(arg1==null) return;
		switch(arg0){
		case 0:
			this.cons_no=arg1.toString();
			break;
		case 1:
			this.faultCode=arg1.toString();
			break;
		case 2:
			this.faultDescribe=arg1.toString();
			break;
		case 3:
			this.maintenanceCode=arg1.toString();
			break;
		case 4:
			this.maintenanceDate=arg1.toString();
			break;
		case 5:
			this.maintenanceDescribe=arg1.toString();
			break;
		case 6:
			this.maintenanceResult=arg1.toString();
			break;
		case 7:
			this.meter_code=arg1.toString();
			break;
		case 8:
			this.task_no=arg1.toString();
			break;
		default:
			break;
		}

	}

}
