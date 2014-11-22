package com.art.tech;

import com.art.tech.application.Constants;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends BaseActivity {
	Button scanButton;
	Button browseButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		scanButton = (Button) findViewById(R.id.button_scan);
		browseButton = (Button) findViewById(R.id.button_browse);

		scanButton.setOnClickListener(scanListener);
		browseButton.setOnClickListener(browseListener);
	}

	OnClickListener scanListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			Intent mainIntent = new Intent(MainActivity.this,
					RecordActivity.class);
			startActivity(mainIntent);
		}

	};

	OnClickListener browseListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			Intent mainIntent = new Intent(MainActivity.this,
					ImageBrowseActivity.class);
			startActivity(mainIntent);
			
		}

	};
	
	private long exitTime = 0;	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){   
	        if((System.currentTimeMillis()-exitTime) > Constants.EXIT_TIMEOUT){  
	            Toast.makeText(getApplicationContext(), getResources().getString(R.string.press_again_exit)
	            		, Toast.LENGTH_SHORT).show();                                
	            exitTime = System.currentTimeMillis();   
	        } else {
	            finish();
	        }
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
}
