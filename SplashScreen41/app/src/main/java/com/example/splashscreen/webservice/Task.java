package com.example.splashscreen.webservice;

public class Task {

	public String task_no=null;
	public String org_no=null;
	public String tg_name=null;
	public String faultCode=null;
	public String buildDate=null;
	public double latitude=0.0;
	public double longitude=0.0;
	//保留字段
	public String address="";
	public String describe="";
	public Task() {
		// TODO Auto-generated constructor stub
	}
	public Task(String task_no,String org_no,String tg_name,String faultCode,
			String buildDate,double latitude,double longitude,String address,String describe){
		this.task_no=task_no;
		this.org_no=org_no;
		this.tg_name=tg_name;
		this.faultCode=faultCode;
		this.buildDate=buildDate;
		this.latitude=latitude;
		this.longitude=longitude;
		this.address=address;
		this.describe=describe;
	}

}
