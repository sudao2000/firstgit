package com.art.tech;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

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
			MainActivity.this.finish();

		}

	};

	OnClickListener browseListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub

		}

	};
}
