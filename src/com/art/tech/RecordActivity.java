package com.art.tech;

import com.art.tech.adapter.ImageAdapter;
import com.art.tech.view.GalleryView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class RecordActivity extends Activity {
	private TextView tvTitle; 	
	
	private GalleryView gallery; 	
	private ImageAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.grallery_layout);

		initRes();
	}
	
	private void initRes(){
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		gallery = (GalleryView) findViewById(R.id.mygallery);

		adapter = new ImageAdapter(this); 	
		adapter.createReflectedImages();
		gallery.setAdapter(adapter);
		
		gallery.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				tvTitle.setText(adapter.titles[position]);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		gallery.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Toast.makeText(RecordActivity.this, "img " + (position+1) + " selected", Toast.LENGTH_SHORT).show();
			}
		});
	}
}
