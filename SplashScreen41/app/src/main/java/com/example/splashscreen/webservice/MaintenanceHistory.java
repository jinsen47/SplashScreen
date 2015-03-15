package com.example.splashscreen.webservice;

public class MaintenanceHistory {

	public String cons_no=null;
	public String faultCode=null;
	public String faultDescribe=null;
	public String maintenanceCode=null;
	public String maintenanceDate=null;
	public String maintenanceDescribe=null;
	public String maintenancerName=null;
	public String meter_code=null;
	public String task_no=null;
	
	public MaintenanceHistory() {
		// TODO Auto-generated constructor stub
	}
	public MaintenanceHistory(String cons_no,String faultCode,String faultDescribe,
			String maintenanceCode,String maintenanceDate,String maintenanceDescribe,
			String maintenancerName,String meter_code,String task_no){
		this.cons_no=cons_no;
		this.faultCode=faultCode;
		this.faultDescribe=faultDescribe;
		this.maintenanceCode=maintenanceCode;
		this.maintenanceDate=maintenanceDate;
		this.maintenanceDescribe=maintenanceDescribe;
		this.maintenancerName=maintenancerName;
		this.meter_code=meter_code;
		this.task_no=task_no;
		
	}
	/*public String getCons_no(){
		return cons_no;
	}
	public void setCons_no(String cons_no){
		this.cons_no=cons_no;
	}
	
	public String getFaultCode(){
		return faultCode;
	}
	public void setFaultCode(String faultCode){
		this.faultCode=faultCode;
	}
	
	public String getFaultDescribe(){
		return faultDescribe;
	}
	public void setFaultDescribe(String faultDescribe){
		this.faultDescribe=faultDescribe;
	}
	
	public String getMaintenanceCode(){
		return maintenanceCode;
	}
	public void setMaintenanceCode(String maintenanceCode){
		this.maintenanceCode=maintenanceCode;
	}
	
	public String getMaintenanceDate(){
		return maintenanceDate;
	}
	public void setMaintenanceDate(String maintenanceDate){
		this.maintenanceDate=maintenanceDate;
	}
	
	public String getMaintenanceDescribe(){
		return maintenanceDescribe;
	}
	public void setMaintenanceDescribe(String maintenanceDescribe){
		this.maintenanceDescribe=maintenanceDescribe;
	}
	
	public String getMaintenancerName(){
		return maintenancerName;
	}
	public void setMaintenancerName(String maintenancerName){
		this.maintenancerName=maintenancerName;
	}
	
	public String getMeter_code(){
		return meter_code;
	}
	public void setMeter_code(String meter_code){
		this.meter_code=meter_code;
	}
	
	public String getTask_no(){
		return task_no;
	}
	public void setTask_no(String task_no){
		this.task_no=task_no;
	}
	*/
}
