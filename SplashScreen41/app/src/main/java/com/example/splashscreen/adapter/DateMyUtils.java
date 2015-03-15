package com.example.splashscreen.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.util.Log;

public class DateMyUtils {
	public static String getCurrentDate(){
		Calendar tempDate=Calendar.getInstance();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd",Locale.US);//对应ASCII的日期
		String currentDate=sdf.format(tempDate.getTime());
		return currentDate;
	}
   public static String getLastWeekDate(String currentDateStr){
	   String lastWeekDayStr=null;
	   GregorianCalendar gc=new GregorianCalendar();
	   SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd",Locale.US);
	   try{
		   gc.setTime(sdf.parse(currentDateStr));
		   gc.add(Calendar.DAY_OF_MONTH, -6);//往前推7天，包括当天
		   lastWeekDayStr=sdf.format(gc.getTime());
	   }catch(ParseException e){//如果parse转化失误会报错
		   e.printStackTrace();
	   }
	   Log.i("lastWeekDayStr->", lastWeekDayStr);
	   return lastWeekDayStr;
   }
   public static String getLastMonthDate(String currentDateStr){
	   String lastMonthDayStr=null;
	   GregorianCalendar gc=new GregorianCalendar();
	   SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd",Locale.US);
	   try{
		   gc.setTime(sdf.parse(currentDateStr));
		   gc.add(Calendar.MONTH, -1);//往前推1个月
		   gc.add(Calendar.DAY_OF_MONTH, +1);//把一个月按30天算
		   lastMonthDayStr=sdf.format(gc.getTime());
	   }catch(ParseException e){//如果parse转化失误会报错
		   e.printStackTrace();
	   }
	   Log.i("lastMonthDayStr->", lastMonthDayStr);
	   return lastMonthDayStr;
   }

}
