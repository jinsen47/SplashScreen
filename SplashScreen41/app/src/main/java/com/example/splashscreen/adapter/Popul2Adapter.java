package com.example.splashscreen.adapter;

import java.util.ArrayList;

import com.example.splashscreen.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class Popul2Adapter extends BaseAdapter {
	private Context context;
	private ArrayList<String>list;
	public Popul2Adapter(Context context1,ArrayList<String> mlist) {
		this.context=context1;
		this.list=mlist;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder=null;
		if(convertView==null){
			convertView=LayoutInflater.from(context).inflate(R.layout.popupmenu_item, null);
			viewHolder=new ViewHolder();
			viewHolder.tv=(TextView)convertView.findViewById(R.id.item_tv2);
			convertView.setTag(viewHolder);
		}else{
			viewHolder=(ViewHolder)convertView.getTag();
		}
		viewHolder.tv.setText(list.get(position));
		return convertView;
	}
	public class ViewHolder{
		public TextView tv;
	}
}
