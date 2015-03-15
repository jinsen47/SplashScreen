package com.example.splashscreen.adapter;
 
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

public class MySimpleAdapter extends SimpleAdapter {

	public MySimpleAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view=null;
		if(convertView!=null){
			view=convertView;
		}else{
			view=super.getView(position, convertView, parent);
		}
		int colors[]={Color.rgb(219, 238, 244),Color.WHITE};
		view.setBackgroundColor(colors[position%colors.length]);
		return super.getView(position, view, parent);	
	}
}
