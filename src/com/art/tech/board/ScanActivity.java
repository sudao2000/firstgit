package com.art.tech.board;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.art.tech.R;
import com.art.tech.R.id;
import com.art.tech.R.layout;

public class ScanActivity extends FragmentActivity {
	private Fragment scanFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scan_activity);
		
		
		scanFragment = new ScanUIFragment();
		
		FragmentTransaction fragmentTransaction = getSupportFragmentManager()
				.beginTransaction();
		fragmentTransaction.replace(R.id.scan_container, scanFragment);
		fragmentTransaction.commitAllowingStateLoss();
	}
}
