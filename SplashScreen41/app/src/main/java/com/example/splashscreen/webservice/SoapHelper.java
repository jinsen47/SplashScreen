package com.example.splashscreen.webservice;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.example.splashscreen.DemoApplication;
import com.example.splashscreen.adapter.MyConfig;
import com.example.splashscreen.webservice.PostCustomGpsResp;
import android.util.Log;

public class SoapHelper {
	//private static String URL="http://10.110.6.143:8080/axis2/services/MaintainServic?wsdl";
	private final static String NAMESPACE="http://ws.apache.org/axis2";
	private final static String NAMESPACE1="http://data.maintian.amr.ywepc.com/xsd";//对应的是自定义类的命名空间
	
	
	public SoapHelper() {
		// TODO Auto-generated constructor stub
	}
	/*
	 * 返回string[0]------org_no  string[1]-------status
	 */
	public static String[] login(String login_id,String passwordmd5){
		String URL=DemoApplication.getUrl();
		Log.i("SoapHelper",URL);
		String[] loginResp=new String[2];
		String methodName="login";
		SoapObject request=new SoapObject(NAMESPACE, methodName);
		request.addProperty("login_id", login_id);
		request.addProperty("passwordmd5", passwordmd5);
		Log.i(methodName, "request->"+request.toString());
		
		SoapSerializationEnvelope envelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.bodyOut=request;
		envelope.dotNet=false;//这里如果设置为TRUE,那么在服务器端将获取不到参数值(如:将这些数据插入到数据库中的话)
		envelope.setOutputSoapObject(request);
		Log.i(methodName, "envelop->"+envelope.toString());

		HttpTransportSE httpTransportSE=new HttpTransportSE(URL);
		httpTransportSE.debug=true;
		String SOAP_ACTION=NAMESPACE+"/"+methodName; 
	
		try{ 
			httpTransportSE.call(SOAP_ACTION, envelope);
			Object response=envelope.bodyIn;
			
			//由于httpTransportSE.debug=true;打印出HttpTransport的调试信息。
			//尤其当前面call方法和getResult方法发生异常时，这个调试信息是非常有用的
			//System.out.println("Response dump>>" + httpTransportSE.responseDump);
			SoapObject detail=(SoapObject)((SoapObject)response).getProperty("return");
			Log.i(methodName,"detail->"+detail);
			//loginResp=new String[2];
			loginResp[0]=String.valueOf(detail.getProperty("org_no"));
			loginResp[1]=String.valueOf(detail.getProperty("status"));
			
		}catch (Exception e){
			e.printStackTrace();
			loginResp[1]=MyConfig.NetError;
			Log.i(methodName, MyConfig.NetError);
		}
		return loginResp;
	}
	/*
	 * 输入参数 codeCatalog ：orgNo,FaultCode,MaitenanceCode
	 * 返回：Map<String,String> map ----key->code,value->valueName
	 */

	public static ValueCode[] getCodeList(String login_id,String passwordmd5,String codeCatalog){
		String URL=DemoApplication.getUrl();
		Log.i("SoapHelper",URL);
	    ValueCode[] valueCode;
		String methodName="getCodeList";
		SoapObject request=new SoapObject(NAMESPACE, methodName);
		request.addProperty("login_id", login_id);
		request.addProperty("passwordmd5", passwordmd5);
		request.addProperty("codeCatalog", codeCatalog);
		Log.i(methodName, "request->"+request.toString());
		
		SoapSerializationEnvelope envelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.bodyOut=request;
		envelope.dotNet=false;//这里如果设置为TRUE,那么在服务器端将获取不到参数值(如:将这些数据插入到数据库中的话)
		envelope.setOutputSoapObject(request);
		Log.i(methodName, "envelop->"+envelope.toString());

		HttpTransportSE httpTransportSE=new HttpTransportSE(URL);
		httpTransportSE.debug=true;
		String SOAP_ACTION=NAMESPACE+"/"+methodName; 
	
		try{ 
			httpTransportSE.call(SOAP_ACTION, envelope);
			Object response=(SoapObject)envelope.bodyIn;
			Log.i(methodName,"response->"+response);
			//由于httpTransportSE.debug=true;打印出HttpTransport的调试信息。
			//尤其当前面call方法和getResult方法发生异常时，这个调试信息是非常有用的
			//Object response=envelope.getResponse();
			//System.out.println("Response dump>>" + httpTransportSE.responseDump);
			int propertyCount=((SoapObject)response).getPropertyCount();
	          valueCode =new ValueCode[propertyCount];
			Log.i(methodName, "propertyCount"+propertyCount);
			for (int i=0;i<propertyCount;i++){
				SoapObject detail=(SoapObject)((SoapObject)response).getProperty(i);
				Log.i(methodName,"detail->"+detail);
				valueCode[i]=new ValueCode();
				//Object.toString必须object!=null,否则重写派生类
				//String.valueOf(Object) 若Object==null,返回“null”,做比对时注意equals()
                valueCode[i].code = String.valueOf(detail.getProperty("code"));
                valueCode[i].valueName = String.valueOf(detail.getProperty("valueName"));
				valueCode[i].codeCatalog = codeCatalog;
				
			}		
			
		}catch (Exception e){
			e.printStackTrace();
			valueCode = null;
			Log.i(methodName, MyConfig.NetError);
		}
		return valueCode;
	}
	
	/**
	public static Map <String,String> getCodeList(String login_id,String passwordmd5,String codeCatalog){
		Map<String, String> map=null;
		String methodName="getCodeList";
		SoapObject request=new SoapObject(NAMESPACE, methodName);
		request.addProperty("login_id", login_id);
		request.addProperty("passwordmd5", passwordmd5);
		request.addProperty("codeCatalog", codeCatalog);
		Log.i(methodName, "request->"+request.toString());
		
		SoapSerializationEnvelope envelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.bodyOut=request;
		envelope.dotNet=false;//这里如果设置为TRUE,那么在服务器端将获取不到参数值(如:将这些数据插入到数据库中的话)
		envelope.setOutputSoapObject(request);
		Log.i(methodName, "envelop->"+envelope.toString());

		HttpTransportSE httpTransportSE=new HttpTransportSE(URL);
		httpTransportSE.debug=true;
		String SOAP_ACTION=NAMESPACE+"/"+methodName; 
	
		try{ 
			httpTransportSE.call(SOAP_ACTION, envelope);
			Object response=(SoapObject)envelope.bodyIn;
			Log.i(methodName,"response->"+response);
			//由于httpTransportSE.debug=true;打印出HttpTransport的调试信息。
			//尤其当前面call方法和getResult方法发生异常时，这个调试信息是非常有用的
			//Object response=envelope.getResponse();
			//System.out.println("Response dump>>" + httpTransportSE.responseDump);
			int propertyCount=((SoapObject)response).getPropertyCount();
			map =new HashMap<String, String>();
			for (int i=0;i<propertyCount;i++){
				SoapObject detail=(SoapObject)((SoapObject)response).getProperty(i);
				Log.i(methodName,"detail->"+detail);
				
				String key=String.valueOf(detail.getProperty("code"));
				//如果 valueName=null,valueName替换为字符串“null”
				String value=String.valueOf(detail.getProperty("valueName"));	
				map.put(key, value);
			}		
			
		}catch (Exception e){
			e.printStackTrace();
			map=null;
		}
		return map;
	}
	**/
	/* public SoapObject getTaskList1(String login_id,String passwordmd5,String org_no){
		SoapObject response=null;
		String methodName="getTaskList";
		SoapObject request=new SoapObject(NAMESPACE, methodName);
		request.addProperty("login_id", login_id);
		request.addProperty("passwordmd5", passwordmd5);
		request.addProperty("org_no", org_no);

		Log.i(methodName, "request->"+request.toString());
		
		SoapSerializationEnvelope envelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.bodyOut=request;
		envelope.dotNet=false;//这里如果设置为TRUE,那么在服务器端将获取不到参数值(如:将这些数据插入到数据库中的话)
		envelope.setOutputSoapObject(request);
		Log.i(methodName, "envelop->"+envelope.toString());

		HttpTransportSE httpTransportSE=new HttpTransportSE(URL);
		httpTransportSE.debug=true;
		String SOAP_ACTION=NAMESPACE+"/"+methodName; 
	
		try{ 
			httpTransportSE.call(SOAP_ACTION, envelope);
			response=(SoapObject)envelope.bodyIn;
			Log.i(methodName, "response->"+response);
			
			 }catch (Exception e){
			e.printStackTrace();
			response=null;
		}
    	return response;
	}*/
	/*
	 * 返回参数: 类数组Task[] 
	 */
	public static Task[] getTaskList(String login_id,String passwordmd5,String org_no){
		String URL=DemoApplication.getUrl();
		Log.i("SoapHelper",URL);
		Task[] task;
		String methodName="getTaskList";
		SoapObject request=new SoapObject(NAMESPACE, methodName);
		request.addProperty("login_id", login_id);
		request.addProperty("passwordmd5", passwordmd5);
		request.addProperty("org_no", org_no);

		Log.i(methodName, "request->"+request.toString());
		
		SoapSerializationEnvelope envelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.bodyOut=request;
		envelope.dotNet=false;//这里如果设置为TRUE,那么在服务器端将获取不到参数值(如:将这些数据插入到数据库中的话)
		envelope.setOutputSoapObject(request);
		Log.i(methodName, "envelop->"+envelope.toString());

		HttpTransportSE httpTransportSE=new HttpTransportSE(URL);
		httpTransportSE.debug=true;
		String SOAP_ACTION=NAMESPACE+"/"+methodName; 
	
		try{ 
			httpTransportSE.call(SOAP_ACTION, envelope);
			Object response=(SoapObject)envelope.bodyIn;
			
			//由于httpTransportSE.debug=true;打印出HttpTransport的调试信息。
			//尤其当前面call方法和getResult方法发生异常时，这个调试信息是非常有用的
			//Object response=envelope.getResponse();
			//System.out.println("Response dump>>" + httpTransportSE.responseDump);
			int propertyCount=((SoapObject)response).getPropertyCount();
			//声明类数组
			task=new Task[propertyCount];
			
			for(int i=0;i<propertyCount;i++){
					SoapObject detail=(SoapObject)((SoapObject)response).getProperty(i);
					Log.i(methodName, "detail->"+detail.toString());
					task[i]=new Task();
					//Object.toString必须object!=null,否则重写派生类
					//String.valueOf(Object) 若Object==null,返回“null”,做比对时注意equals()
					task[i].task_no=String.valueOf(detail.getProperty("task_no"));
					task[i].org_no=String.valueOf(detail.getProperty("org_no"));
					task[i].tg_name=String.valueOf(detail.getProperty("tg_name"));
					task[i].faultCode=String.valueOf(detail.getProperty("faultCode"));
					task[i].buildDate=String.valueOf(detail.getProperty("buildDate"));
					task[i].latitude=Double.parseDouble(detail.getProperty("latitude").toString());
					task[i].longitude=Double.parseDouble(detail.getProperty("longitude").toString());
					task[i].address=String.valueOf(detail.getProperty("address"));
					task[i].describe=String.valueOf(detail.getProperty("describe"));
			 }
			
		}catch (Exception e){
			e.printStackTrace();
			task=null;
			Log.i(methodName, MyConfig.NetError);
		}
    	return task;
	}
	public static TaskItem[] getTaskItemList(String login_id,String passwordmd5,String task_no){
		String URL=DemoApplication.getUrl();
		Log.i("SoapHelper",URL);
		TaskItem[] taskItem;
		String methodName="getTaskItemList";
		SoapObject request=new SoapObject(NAMESPACE, methodName);
		request.addProperty("login_id", login_id);
		request.addProperty("passwordmd5", passwordmd5);
		request.addProperty("task_no", task_no);

		Log.i(methodName, "request->"+request.toString());
		
		SoapSerializationEnvelope envelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.bodyOut=request;
		envelope.dotNet=false;//这里如果设置为TRUE,那么在服务器端将获取不到参数值(如:将这些数据插入到数据库中的话)
		envelope.setOutputSoapObject(request);
		Log.i(methodName, "envelop->"+envelope.toString());

		HttpTransportSE httpTransportSE=new HttpTransportSE(URL);
		httpTransportSE.debug=true;
		String SOAP_ACTION=NAMESPACE+"/"+methodName; 
	
		try{ 
			httpTransportSE.call(SOAP_ACTION, envelope);
			Object response=(SoapObject)envelope.bodyIn;
			
			//由于httpTransportSE.debug=true;打印出HttpTransport的调试信息。
			//尤其当前面call方法和getResult方法发生异常时，这个调试信息是非常有用的
			//Object response=envelope.getResponse();
			//System.out.println("Response dump>>" + httpTransportSE.responseDump);
			int propertyCount=((SoapObject)response).getPropertyCount();
			//声明类数组
			taskItem=new TaskItem[propertyCount];
			
			for(int i=0;i<propertyCount;i++){
					SoapObject detail=(SoapObject)((SoapObject)response).getProperty(i);
					Log.i(methodName, "detail->"+detail.toString());
					taskItem[i]=new TaskItem();
					//Object.toString必须object!=null,否则重写派生类
					//String.valueOf(Object) 若Object==null,返回“null”,做比对时注意equals()
					taskItem[i].task_no=String.valueOf(detail.getProperty("task_no"));
					taskItem[i].cons_no=String.valueOf(detail.getProperty("cons_no"));
					taskItem[i].cons_name=String.valueOf(detail.getProperty("cons_name"));
					taskItem[i].cons_address=String.valueOf(detail.getProperty("cons_address"));
					taskItem[i].meter_code=String.valueOf(detail.getProperty("meter_code"));
					taskItem[i].rtua=String.valueOf(detail.getProperty("rtua"));
					taskItem[i].faultCode=String.valueOf(detail.getProperty("faultCode"));
					taskItem[i].latitude=Double.parseDouble(detail.getProperty("latitude").toString());
					taskItem[i].longitude=Double.parseDouble(detail.getProperty("longitude").toString());
			 }
			
		}catch (Exception e){
			e.printStackTrace();
			taskItem=null;
			Log.i(methodName, MyConfig.NetError);
		}
    	return taskItem;
	}
	
	/*
	 * 输入5个参数，返回一个MeterReadingHistory类数组
	 */
     public static MeterReadingHistory[] getMeterReadingHistoryList(String login_id,String passwordmd5,String cons_no,
			String meter_code,String beginDate, String endDate){
    	String URL=DemoApplication.getUrl();
 		Log.i("SoapHelper",URL);
    	MeterReadingHistory[] meterReadingHistory;
    	
    	String methodName="getMeterReadingHistroyList";
    	SoapObject request=new SoapObject(NAMESPACE, methodName);
		request.addProperty("login_id", login_id);
		request.addProperty("passwordmd5", passwordmd5);
		request.addProperty("cons_no", cons_no);
		request.addProperty("meter_code",meter_code);
		request.addProperty("beginDate", beginDate);
		request.addProperty("endDate", endDate);

		Log.i(methodName, "request->"+request.toString());
		
		SoapSerializationEnvelope envelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.bodyOut=request;
		envelope.dotNet=false;//这里如果设置为TRUE,那么在服务器端将获取不到参数值(如:将这些数据插入到数据库中的话)
		envelope.setOutputSoapObject(request);
		Log.i(methodName, "envelop->"+envelope.toString());

		HttpTransportSE httpTransportSE=new HttpTransportSE(URL);
		httpTransportSE.debug=true;
		String SOAP_ACTION=NAMESPACE+"/"+methodName; 
	
		try{ 
			httpTransportSE.call(SOAP_ACTION, envelope);
			Object response=envelope.bodyIn;
			
			//由于httpTransportSE.debug=true;打印出HttpTransport的调试信息。
			//尤其当前面call方法和getResult方法发生异常时，这个调试信息是非常有用的
			//System.out.println("Response dump>>" + httpTransportSE.responseDump);
			int propertyCount=((SoapObject)response).getPropertyCount();
			//声明类数组
			meterReadingHistory=new MeterReadingHistory[propertyCount];
			
			for(int i=0;i<propertyCount;i++){
					SoapObject detail=(SoapObject)((SoapObject)response).getProperty(i);
					Log.i(methodName, "detail->"+detail.toString());
					meterReadingHistory[i]=new MeterReadingHistory();
					meterReadingHistory[i].cons_no=String.valueOf(detail.getProperty("cons_no"));
					meterReadingHistory[i].indication=String.valueOf(detail.getProperty("indication"));
					meterReadingHistory[i].meter_code=String.valueOf(detail.getProperty("meter_code"));
					meterReadingHistory[i].readingDate=String.valueOf(detail.getProperty("readingDate"));
			 }
			
		}catch (Exception e){
			e.printStackTrace();
			meterReadingHistory=null;
			Log.i(methodName, MyConfig.NetError);
		}
    	return meterReadingHistory;
    }
    /*
     * 输入：pi---MaintenanceItems类的信息 输出：是否上传成功的标志
     */
    public static String postMaintenanceItems(String login_id,String passwordmd5,MaintenanceItem items){
    	String URL=DemoApplication.getUrl();
		Log.i("SoapHelper",URL);
    	String status;
    	String methodName="postMaintenanceItems";
    	SoapObject request=new SoapObject(NAMESPACE, methodName);//definition 下对应的targetnamespace
		request.addProperty("login_id", login_id);
		request.addProperty("passwordmd5", passwordmd5);
		
		PropertyInfo pi=new PropertyInfo();
		pi.setType(MaintenanceItem.class);
		pi.setValue(items);
		pi.setName("items");
		request.addProperty(pi);
		Log.i(methodName, "request->"+request.toString());
		
		SoapSerializationEnvelope envelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.bodyOut=request;
		//envelope.dotNet = false;这个属性是对dotnetwebservice协议的支持,如果dotnet的webservice 不指定rpc方式则用true否则要用false
		envelope.dotNet=false;//这里如果设置为TRUE,那么在服务器端将获取不到参数值(如:将这些数据插入到数据库中的话)
		envelope.setOutputSoapObject(request);
		//做类型映射
		//传输自定义类时，定义类MaintencanceItem所在的targetnamespace
		envelope.addMapping(NAMESPACE1, "MaintenanceItem", new MaintenanceItem().getClass());
		envelope.encodingStyle="UTF-8";
		Log.i(methodName, "envelop->"+envelope.toString());
		
		
		HttpTransportSE httpTransportSE=new HttpTransportSE(URL);
		httpTransportSE.debug=true;
		String SOAP_ACTION=NAMESPACE+"/"+methodName; //postMaintenanceItems 该函数schemas定义的targetnamespace
	
		try{ 
			httpTransportSE.call(SOAP_ACTION, envelope);
		    Object response=(SoapObject)envelope.bodyIn;
			Log.i(methodName, "response->"+response.toString());
			//由于httpTransportSE.debug=true;打印出HttpTransport的调试信息。
			//尤其当前面call方法和getResult方法发生异常时，这个调试信息是非常有用的
			//Object response=envelope.getResponse();
			//System.out.println("Response dump>>" + httpTransportSE.responseDump);
			Object detail=((SoapObject)response).getProperty("return");
			status=String.valueOf(((SoapObject)detail).getProperty("status"));
			Log.i(methodName,"status->"+status);
			
		}catch (Exception e){
			e.printStackTrace();
			status=MyConfig.NetError;
			Log.i(methodName, status);
		}
    	return status;
    }
    /*
     * 输入信息，输出MaintenanceHistory类数组，声明类数组，该数组分配内存空间，每个元素类也需要分配空间
     */
    public static MaintenanceHistory[] getMaintenanceHistoryList(String login_id,String passwordmd5,String cons_no,
			String meter_code,String beginDate, String endDate){
    	String URL=DemoApplication.getUrl();
		Log.i("SoapHelper",URL);
    	MaintenanceHistory[] maintenanceHistory;
    	String methodName="getMaintenanceHistroyList";
    	SoapObject request=new SoapObject(NAMESPACE, methodName);
		request.addProperty("login_id", login_id);
		request.addProperty("passwordmd5", passwordmd5);
		request.addProperty("cons_no", cons_no);
		request.addProperty("meter_code",meter_code);
		request.addProperty("beginDate", beginDate);
		request.addProperty("endDate", endDate);

		Log.i(methodName, "request->"+request.toString());
		
		SoapSerializationEnvelope envelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.bodyOut=request;
		envelope.dotNet=false;//这里如果设置为TRUE,那么在服务器端将获取不到参数值(如:将这些数据插入到数据库中的话)
		envelope.setOutputSoapObject(request);
		Log.i(methodName, "envelop->"+envelope.toString());

		HttpTransportSE httpTransportSE=new HttpTransportSE(URL);
		httpTransportSE.debug=true;
		String SOAP_ACTION=NAMESPACE+"/"+methodName; 
	
		try{ 
			httpTransportSE.call(SOAP_ACTION, envelope);
			Object response=envelope.bodyIn;
			
			//由于httpTransportSE.debug=true;打印出HttpTransport的调试信息。
			//尤其当前面call方法和getResult方法发生异常时，这个调试信息是非常有用的
			//Object response=envelope.getResponse();
			//System.out.println("Response dump>>" + httpTransportSE.responseDump);
			int propertyCount=((SoapObject)response).getPropertyCount();
			maintenanceHistory=new MaintenanceHistory[propertyCount];//如果没有分配内存会有nullPointerException错误
			
			for(int i=0;i<propertyCount;i++){
				SoapObject detail=(SoapObject)((SoapObject)response).getProperty(i);
				Log.i(methodName, "detail->"+detail.toString());
				maintenanceHistory[i]=new MaintenanceHistory();
				maintenanceHistory[i].cons_no=String.valueOf(detail.getProperty("cons_no"));
				maintenanceHistory[i].faultCode=String.valueOf(detail.getProperty("faultCode"));
				maintenanceHistory[i].faultDescribe=String.valueOf(detail.getProperty("faultDescribe"));
				maintenanceHistory[i].maintenanceCode=String.valueOf(detail.getProperty("maintenanceCode"));
				maintenanceHistory[i].maintenanceDate=String.valueOf(detail.getProperty("maintenanceDate"));
				maintenanceHistory[i].maintenanceDescribe=String.valueOf(detail.getProperty("maintenanceDescribe"));
				maintenanceHistory[i].maintenancerName=String.valueOf(detail.getProperty("maintenancerName"));
				maintenanceHistory[i].meter_code=String.valueOf(detail.getProperty("meter_code"));
				maintenanceHistory[i].task_no=String.valueOf(detail.getProperty("task_no"));
			}
			
		}catch (Exception e){
			e.printStackTrace();
			maintenanceHistory=null;
			Log.i(methodName, MyConfig.NetError);
		}
    	return maintenanceHistory;
    }
    public static PostCustomGpsResp postCustomGps(String login_id, String passwordmd5,
			String cons_no, String longitude, String latitude)
    {
    	String URL=DemoApplication.getUrl();
		Log.i("SoapHelper",URL);
    	PostCustomGpsResp postGpsStatus;
	    String methodName="postCustomGps";
	    SoapObject request=new SoapObject(NAMESPACE, methodName);
	    request.addProperty("login_id", login_id);
	    request.addProperty("passwordmd5", passwordmd5);
	    request.addProperty("cons_no", cons_no);
	    request.addProperty("longitude",longitude);
	    request.addProperty("latitude", latitude);

	   Log.i(methodName, "request->"+request.toString());
	
	   SoapSerializationEnvelope envelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
	   envelope.bodyOut=request;
	   envelope.dotNet=false;//这里如果设置为TRUE,那么在服务器端将获取不到参数值(如:将这些数据插入到数据库中的话)
	   envelope.setOutputSoapObject(request);
	   Log.i(methodName, "envelop->"+envelope.toString());

	   HttpTransportSE httpTransportSE=new HttpTransportSE(URL);
	   httpTransportSE.debug=true;
	   String SOAP_ACTION=NAMESPACE+"/"+methodName; 

	try{ 
		 httpTransportSE.call(SOAP_ACTION, envelope);
		 Object response=(SoapObject)envelope.bodyIn;
		
		//由于httpTransportSE.debug=true;打印出HttpTransport的调试信息。
		//尤其当前面call方法和getResult方法发生异常时，这个调试信息是非常有用的
		//Object response=envelope.getResponse();
		//System.out.println("Response dump>>" + httpTransportSE.responseDump);
		int propertyCount=((SoapObject)response).getPropertyCount();
		postGpsStatus = new PostCustomGpsResp();//如果没有分配内存会有nullPointerException错误
		
		for(int i=0;i<propertyCount;i++){
			SoapObject detail=(SoapObject)((SoapObject)response).getProperty(i);
			Log.i(methodName, "detail->"+detail.toString());
	        postGpsStatus.status = String.valueOf(detail.getProperty("status"));
		}
		
	}catch (Exception e){
		e.printStackTrace();
		postGpsStatus = null;
		Log.i(methodName, MyConfig.NetError);
	}
	return postGpsStatus;
    }
    
    public static PostPictureResp postPicture(String login_id,String passwordmd5,String cons_no,String task_no,
    		String pictureName,String imgBuffer)
    	    {
    	    	String URL=DemoApplication.getUrl();
    			Log.i("SoapHelper",URL);
    	    	PostPictureResp postPictureStatus;
    		    String methodName="postPicture";
    		    SoapObject request=new SoapObject(NAMESPACE, methodName);
    		    request.addProperty("login_id",login_id);
    			request.addProperty("passwordmd5",passwordmd5);
    			request.addProperty("cons_no",cons_no);
    			request.addProperty("task_no",task_no);
    			request.addProperty("pictureName",pictureName);
    			request.addProperty("pictureFile",imgBuffer);

    		   Log.i(methodName, "request->"+request.toString());
    		
    		   SoapSerializationEnvelope envelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
    		   envelope.bodyOut=request;
    		   envelope.dotNet=false;//这里如果设置为TRUE,那么在服务器端将获取不到参数值(如:将这些数据插入到数据库中的话)
    		   envelope.setOutputSoapObject(request);
    		   Log.i(methodName, "envelop->"+envelope.toString());

    		   HttpTransportSE httpTransportSE=new HttpTransportSE(URL);
    		   httpTransportSE.debug=true;
    		   String SOAP_ACTION=NAMESPACE+"/"+methodName; 

    		try{ 
    			 httpTransportSE.call(SOAP_ACTION, envelope);
    			 Object response=(SoapObject)envelope.bodyIn;
    			
    			//由于httpTransportSE.debug=true;打印出HttpTransport的调试信息。
    			//尤其当前面call方法和getResult方法发生异常时，这个调试信息是非常有用的
    			//Object response=envelope.getResponse();
    			//System.out.println("Response dump>>" + httpTransportSE.responseDump);
    			int propertyCount=((SoapObject)response).getPropertyCount();
    			postPictureStatus = new PostPictureResp();//如果没有分配内存会有nullPointerException错误
    			
    			for(int i=0;i<propertyCount;i++){
    				SoapObject detail=(SoapObject)((SoapObject)response).getProperty(i);
    				Log.i(methodName, "detail->"+detail.toString());
    		        postPictureStatus.status = String.valueOf(detail.getProperty("status"));
    			}
    			
    		}catch (Exception e){
    			e.printStackTrace();
    			postPictureStatus = null;
    			Log.i(methodName, MyConfig.NetError);
    		}
    		return postPictureStatus;	
    	    }
}
