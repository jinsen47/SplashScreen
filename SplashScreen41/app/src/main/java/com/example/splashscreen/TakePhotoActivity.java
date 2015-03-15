package com.example.splashscreen;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.kobjects.base64.Base64;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.example.splashscreen.adapter.MapSelfData;
import com.example.splashscreen.adapter.MyConfig;
import com.example.splashscreen.webservice.PostCustomGpsResp;
import com.example.splashscreen.webservice.SoapHelper;
import com.example.splashscreen.webservice.PostPictureResp;

public class TakePhotoActivity extends Activity {
    private Button takePhotoButton;
    private Button selectButton;
    private Button uploadButton;   
    //updateImage方法的入参
    private String login_id ;
    private String passwd ;
    private String cons_no;
    private String task_no ;
    private String fileName=null;
    private final static String NAMESPACE="http://ws.apache.org/axis2";
    //Dialog
    ProgressDialog progressDialog;
    //选择照片及拍照相关的参数
    public final int TAKEPHOTO = 100;
    public final int SELECTPIC = 101;
    private ImageView imageView; 
	private static File mPhotoFile;
	private static String mPhotoPath;
	private static Bitmap bitmap;
    //GridView
    private GridView photoGridView;
    Handler handler = new Handler(){		
		 public void handleMessage(Message msg){
			 progressDialog.dismiss();
			 switch(msg.what){
			 case MyConfig.MSG_DATAINSERT:
				 Log.i("handler", "handler->insertDataSuc!");
				 Toast.makeText(getBaseContext(), "Handler->插入数据库成功", Toast.LENGTH_SHORT).show();
				 break;
			 case MyConfig.MSG_UPLOADGPS:
				 Toast.makeText(getBaseContext(), "上传图片数据成功", Toast.LENGTH_SHORT).show();
				 break;
			 case MyConfig.MSG_NETWORKERROR:
				 Toast.makeText(getBaseContext(), "网络连接错误",Toast.LENGTH_SHORT).show();
				 break;
			 case MyConfig.MSG_GPSSTARTERROR:
				 Toast.makeText(getBaseContext(), "GPS设备未开启", Toast.LENGTH_SHORT).show();
				 break;
			 }
			 
		 }
	 };
    void showGridView(){
		int[] consImage={R.drawable.photo,R.drawable.picture_select,R.drawable.up_loc,R.drawable.help};
		String[] consText={"拍照","照片选择","上传照片","帮助"};
		ArrayList<HashMap<String, Object>> mgriditem=new ArrayList<HashMap<String,Object>>();
		for(int i=0;i<consText.length;i++){
			HashMap<String, Object> map=new HashMap<String, Object>();
			map.put("consImage", consImage[i]);
			map.put("consText", consText[i]); 
			mgriditem.add(map);  
		}			
		SimpleAdapter mAdapter=new SimpleAdapter(this, mgriditem, R.layout.consgriditems, 
						new String[]{"consImage","consText"}, new int[]{R.id.consImage,R.id.consText});
		photoGridView.setAdapter(mAdapter);
	}
    public void initView()
    {	
		photoGridView = (GridView)findViewById(R.id.photoGridView);
		SharedPreferences sp=getSharedPreferences(MyConfig.SharePreferenceFile, 0);
    	login_id=sp.getString("name", "");
    	passwd=sp.getString("passwordmd5", "");
		imageView =(ImageView)findViewById(R.id.image);
		Intent intent = getIntent();
	    cons_no = intent.getStringExtra("cons_no");
	    task_no = intent.getStringExtra("task_no");
	    Log.i("bupt","cons_no is:"+cons_no);
	    Log.i("bupt","task_no is:"+task_no); 
	    progressDialog=new ProgressDialog(TakePhotoActivity.this);
	    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	    progressDialog.setTitle("提示");
	    progressDialog.setIcon(R.drawable.info_48_blue);
	    progressDialog.setMessage("正在上传,请稍后...");
	    progressDialog.setCancelable(true);
    }
	@Override
	public  void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//必须在加载content之前执行
		setContentView(R.layout.photo_take);
		/**
		 * 实现调用android系统内的拍照功能
		 */
		 initView();
		 showGridView();
		
		 photoGridView.setOnItemClickListener( new OnItemClickListener() {
	    		@Override
	    		public void onItemClick(AdapterView<?> parent, View v, int position,
	    				long id) {
	    			switch(position){
	    			case 0: //goto intent navigator  拍照	
	    				/**
	    				Calendar ca = Calendar.getInstance();
	    				int year = ca.get(Calendar.YEAR);
	    				int month = ca.get(Calendar.MONTH);
	    				int day = ca.get(Calendar.DATE);
	    				int hour = ca.get(Calendar.HOUR);
	    				int minute = ca.get(Calendar.MINUTE);			
	    				int second = ca.get(Calendar.SECOND);
	    				String fileName = String.valueOf(year)+String.valueOf(month)+String.valueOf(day)
	    						+String.valueOf(hour)+String.valueOf(minute)+String.valueOf(second);
	    				PhotoName = "mnt/sdcard/DCIM/Camera/"+fileName+".jpg";	
	    				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File
	    						(Environment.getExternalStorageDirectory(),fileName+".jpg")));
	    				startActivityForResult(intent,1);**/
	    				try{
	    					if(bitmap != null)
	    					{
	    					  bitmap.recycle();
	    					}
	    					Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
	    					mPhotoPath = "/mnt/sdcard/DCIM/Camera/"+getPhotoFileName();
	    					mPhotoFile = new File(mPhotoPath);
	    					if(!mPhotoFile.exists()){
	    						mPhotoFile.createNewFile();
	    					}
	    					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
	    					startActivityForResult(intent,TAKEPHOTO);
	    				}catch(Exception e){
	    					e.printStackTrace();
	    				}
	    				
	    				break;
	    			case 1://选择照片 				
	    				if (bitmap != null){
	    					bitmap.recycle();
	    				}
	    				Intent intent = new Intent();
	    				intent.setType("image/*");
	    				intent.setAction(Intent.ACTION_GET_CONTENT);
	    				startActivityForResult(intent,SELECTPIC);
	    				break; 
	    			case 2: //上传照片	   
	    				progressDialog.show();
	    				uploadTest();
	    				break;
	    			case 3:// 帮助
	    				showDialog(TAKEPHOTO);
	    				break;
	    			
	    			default:
	    				break;
	    			}			
	    		}
	    		}
	    		
			);
      
		
	}
	public Dialog onCreateDialog(int id)
	{			
		AlertDialog.Builder builder=new Builder(TakePhotoActivity.this);
		switch(id)
		{
		   
		    case TAKEPHOTO://任务导航
		    	builder.setTitle("照片上传帮助提示");
		    	builder.setIcon(R.drawable.star_light);
		    	builder.setMessage("界面有拍照，照片选择和上传照片三个按键，总共有两种方式：第一种为点击拍照，再照片选择并且上传照片；另一种为照片选择再上传照片。目前，这个模块仍然在继续开发中。");
		    	builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
		    	break;	   
		    default:
		    	break;
		
		
		}
		return builder.create();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode,Intent data)
		{
			if(resultCode != RESULT_OK){
				return;
			}
			if(requestCode == TAKEPHOTO){	
				/**
				 * ImageView显示bitmap会占用较多的资源，特别是图片比较大的时候，可能导致OOM问题
				 * 使用BitmapFactory.Options设置inSampleSize，可以减少对系统内存分配的要求
				 * 属性inSampleSize表示缩略图大小为原始图片大小的几分之一，如果为2，那么取出的缩略图的
				 * 宽和高为原来大小的1/2，图片大小为原始大小的1/4 
				 */
				BitmapFactory.Options opts = new BitmapFactory.Options();
				opts.inSampleSize = 2;				
				// mPhotoPath为拍照产生图片所在的路径
			    bitmap = BitmapFactory.decodeFile(mPhotoPath, opts);
			    /**
			     * 当采用pad进行拍照时，imageView中不能显示bitmap报错
			     * bitmap too large to be uploaded into a texture
			     * 解决方法: bitmap = decodeSampledBitmapFromFile(mPhotoPath,100,100);
			     * http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
			     * 和http://blog.csdn.net/xu_fu/article/details/8262153
			     * http://sinfrancis.iteye.com/blog/1146664
			     */
	            // bitmap = decodeSampledBitmapFromFile(mPhotoPath,100,100);
			     imageView.setImageBitmap(bitmap);
               return;
			} 
			else if(requestCode == SELECTPIC){			
				  if (data != null) {
					   // 取得返回的Uri,基本上选择照片的时候返回的是以Uri形式，但是在拍照中有得机子呢Uri是空的，所 以要特别注意
					   Uri mImageCaptureUri = data.getData();			   
					  // 返回的Uri不为空时，那么图片信息数据都会在Uri中获得。如果为空，那么我们就进行下面的方式获取
					  if (mImageCaptureUri != null) {
					  Log.i("bupt","data ->URI!= null");
					  String[] filePathColumn = {MediaStore.Images.Media.DATA};
					  Cursor cursor = getContentResolver().query(mImageCaptureUri,   
			                           filePathColumn, null, null, null);   
					      if(cursor != null){
			                 cursor.moveToFirst();   
			                 int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
			                 mPhotoPath = cursor.getString(columnIndex);
			                 cursor.close();
						  }else{
							 mPhotoPath = data.getDataString();
							 //台电的平板拍出来的照片路径格式为file:///mnt/sdcard/DCIM/Camera
							 if(mPhotoPath!=null&&mPhotoPath.startsWith("file://")){                                                    
							 mPhotoPath = mPhotoPath.replace("file://", "");                                                }	 
							 }
					         //通过分隔/符号，将/mnt/sdcard/DCIM/Camera/***.jpg得到最后一个/后图片的名称并且赋值给fileName
			                 String[] str =  mPhotoPath.split("/");
			                 for(String str2:str)
			                  {
			                	  fileName = str2;
			                  }
			                   Log.i("bupt","picturePath->"+fileName);
			                   Log.i("bupt","mpicturePath->"+  mPhotoPath);
					       try {
					           // 这个方法是根据Uri获取Bitmap图片的静态方法
					           bitmap = MediaStore.Images.Media.getBitmap(
					           this.getContentResolver(), mImageCaptureUri);
					            if ( bitmap != null) {
					            imageView.setImageBitmap(  bitmap);
					             }
					        } catch (Exception e) {
					          e.printStackTrace();
					        }
					} else {
					    Log.i("bupt","data ->URI== null");
					     Bundle extras = data.getExtras();
					     if (extras != null) {
					      // 这里是有些拍照后的图片是直接存放到Bundle中的所以我们可以从这里面获取Bitmap图片
					    	  bitmap = extras.getParcelable("data");
					       if(bitmap != null)
					       imageView.setImageBitmap(  bitmap);
					      
					     }
					    }
					   }
					 
			 return ;

			}
			
		}
	 private String getPhotoFileName() {
	       Date date = new Date(System.currentTimeMillis());
//        SimpleDateFormat dateFormat = new SimpleDateFormat(  "'IMG'_yyyyMMdd_HHmmss");
	       SimpleDateFormat dateFormat = new SimpleDateFormat(  "yyyyMMdd_HHmmss");
		  return dateFormat.format(date) + ".jpg";
	    }
	/**
	 * 上传图片功能
	 */
	public void uploadTest(){
		try{
			String srcUrl = "/mnt/sdcard/DCIM/Camera/";   //路径
			  //文件名
			Log.i("bupt", "3-->mPhotoPath->"+mPhotoPath);
			FileInputStream fis = new FileInputStream(mPhotoPath);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[8192];
			int count = 0;
			while((count = fis.read(buffer))>=0){
				baos.write(buffer,0,count);
			}
			final String uploadBuffer = new String(Base64.encode(baos.toByteArray()));
			 Thread thread = new Thread(){
		   	     public void run(){
		   		 Looper.prepare();
		   		 Message msg = Message.obtain();
		   	   PostPictureResp postPictureStatus = null;
		   		 postPictureStatus = SoapHelper.postPicture(login_id,passwd,cons_no,task_no,fileName,uploadBuffer);	   			   		
		   		 if((postPictureStatus != null)&&(postPictureStatus.status.equals(MyConfig.Success))){
		   		 msg.what=MyConfig.MSG_UPLOADGPS;
		   		 } else{
			   		 msg.what=MyConfig.MSG_NETWORKERROR;
		   		 }			
		   	   	 handler.sendMessage(msg);
		   		}	   	    
		   	    };
		   	thread.start();		        
			Log.i("bupt_uploadTest","start");
			fis.close();				
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	
	}
	/**
	 * cacluateInSampleSize()和decodeSampledBitmapFromFile() 方法都为解决bitmap too large显示问题
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {  
		 // Raw height and width of image  
		final int height = options.outHeight;  
		final int width = options.outWidth;   
		int inSampleSize = 1;   
		if (height > reqHeight || width > reqWidth) {   
			// Calculate ratios of height and width to requested height and width    
			final int heightRatio = Math.round((float) height / (float) reqHeight);  
			final int widthRatio = Math.round((float) width / (float) reqWidth);     
			// Choose the smallest ratio as inSampleSize value, this will guarantee   
			// a final image with both dimensions larger than or equal to the    
			// requested height and width.      
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;   
			}   
		return inSampleSize;
		}
	
	public static Bitmap decodeSampledBitmapFromFile(String filePath, int reqWidth, int reqHeight) {   
		// First decode with inJustDecodeBounds=true to check dimensions  
		final BitmapFactory.Options options = new BitmapFactory.Options();   
		options.inJustDecodeBounds = true;  
		BitmapFactory.decodeFile(filePath,  options);   
		// Calculate inSampleSize    
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);  
		// Decode bitmap with inSampleSize set    
		options.inJustDecodeBounds = false;   
		return BitmapFactory.decodeFile(filePath,  options);
		}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash_screen, menu);
		return true;
	}

}
