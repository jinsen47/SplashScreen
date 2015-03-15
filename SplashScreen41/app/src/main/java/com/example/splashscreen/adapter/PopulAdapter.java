package com.example.splashscreen.adapter;
import java.util.ArrayList;

import com.example.splashscreen.R;

import android.app.Activity;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class PopulAdapter extends BaseAdapter {
	//private Activity activity;//上下文
	private ArrayList<String> list;
	//控制CheckBox选中情况
	public static SparseBooleanArray isLastSelected;
	//这样rightfragment只需要发送isLastSelected的状态给PopulAdapter,
	//PopulAdapter根据状态变化进行更新
	//本来该方法应对checkbox多选，由于选后popupwindow立马消失，
	//再次打开只看到上次选的状态，类似单选
	private LayoutInflater inflater=null;//导入布局
	//private int temp=-1;
	
	public PopulAdapter(Activity context, ArrayList<String> list) {
		//this.activity = context;
		this.list = list;
		inflater=LayoutInflater.from(context);
		isLastSelected=new SparseBooleanArray();//性能比hashmap更好
		initData();
	}
	private void initData(){//初始化isSelected的数据
		for(int i=0;i<list.size();i++){
			isLastSelected.put(i, false);
		}	
	}
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
    //listview每显示一行数据,该函数就执行一次
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder=null;  
		if (convertView==null) {//当第一次加载ListView控件时  convertView为空
			convertView=inflater.inflate(R.layout.popul_list_item, null);//所以当ListView控件没有滑动时都会执行这条语句
			holder=new ViewHolder();
			holder.tv=(TextView)convertView.findViewById(R.id.item_tv);
			holder.cb=(CheckBox)convertView.findViewById(R.id.item_cb);
			convertView.setTag(holder);//为view设置标签
					 
		}else{//取出holder
			holder=(ViewHolder) convertView.getTag();//the Object stored in this view as a tag
		}
		//设置list的textview显示
		//holder.tv.setTextColor(Color.WHITE);
		holder.tv.setText(list.get(position));//初始化显示的数据
		holder.cb.setChecked(isLastSelected.get(position));// 根据isSelected来设置checkbox的选中状况
		/*holder.cb.setId(position);//对checkbox的id进行重新设置为当前的position
		holder.cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			//把上次被选中的checkbox设为false
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){//实现checkbox的单选功能,同样适用于radiobutton
					if(temp!=-1){
						//找到上次点击的checkbox,并把它设置为false,对重新选择时可以将以前的关掉
						CheckBox tempCheckBox=(CheckBox)activity.findViewById(temp);
						if(tempCheckBox!=null){
							tempCheckBox.setChecked(false);
						}
					}
					temp=buttonView.getId();//保存当前选中的checkbox的id值		
				}
			}
		});
		if(position==temp){//比对position和当前的temp是否一致
			holder.cb.setChecked(true);
			
		}else{
			holder.cb.setChecked(false);	
		}
		*/
		return convertView;
	}
	public static class ViewHolder {
		public TextView tv;
		public CheckBox cb;
	}
}
