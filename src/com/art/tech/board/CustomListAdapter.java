package com.art.tech.board;

import java.util.ArrayList;

import com.art.tech.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomListAdapter extends ArrayAdapter<seqTag> {
	private LayoutInflater mInflater;
	private ArrayList<seqTag> mList;
	private int mResource;

	public CustomListAdapter(Context paramContext, int paramInt,
			ArrayList<seqTag> paramArrayList) {
		super(paramContext, paramInt, paramArrayList);
		this.mResource = paramInt;
		this.mList = paramArrayList;
		this.mInflater = ((LayoutInflater) paramContext.getSystemService("layout_inflater"));
	}

	public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
		seqTag localseqTag = (seqTag) this.mList.get(paramInt);
		if (paramView == null)
			paramView = this.mInflater.inflate(this.mResource, null);
		if (localseqTag != null) {
			TextView localTextView2 = (TextView) paramView.findViewById(R.id.list_number);
			TextView localTextView3 = (TextView) paramView.findViewById(R.id.list_tag);
			TextView localTextView1 = (TextView) paramView.findViewById(R.id.list_count);
			localTextView2.setText(localseqTag.getNum());
			localTextView3.setText(localseqTag.getTag());
			localTextView1.setText(localseqTag.getCount());
			if (paramInt == selectItem) {  
				paramView.setBackgroundColor(Color.BLUE); 
				localTextView1.setBackgroundColor(Color.BLUE);
				localTextView2.setBackgroundColor(Color.BLUE);
				localTextView3.setBackgroundColor(Color.BLUE);
	        }   
	        else {  
	        	paramView.setBackgroundColor(Color.TRANSPARENT); 
	        	localTextView1.setBackgroundColor(Color.TRANSPARENT);
				localTextView2.setBackgroundColor(Color.TRANSPARENT);
				localTextView3.setBackgroundColor(Color.TRANSPARENT);
	        }   
		}
		return paramView;
	}
	
	public  void setSelectItem(int selectItem) {  
         this.selectItem = selectItem;  
    }  
    private int selectItem=-1;  
}
