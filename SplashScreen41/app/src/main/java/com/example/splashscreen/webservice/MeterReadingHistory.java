package com.example.splashscreen.webservice;

public class MeterReadingHistory {

	public String cons_no=null;
	public String indication=null;
	public String meter_code=null;
	public String readingDate=null;
	public MeterReadingHistory(){
		
	}
	public MeterReadingHistory(String cons_no,String indication,
			String meter_code,String readingDate) {
		this.cons_no=cons_no;
		this.indication=indication;
		this.meter_code=meter_code;
		this.readingDate=readingDate;
	}
	
}
