package com.art.tech;

import com.art.tech.application.Constants;
import com.art.tech.board.ScanUIFragment;
import com.art.tech.db.DBHelper;
import com.art.tech.fragment.ImageGridFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {
	Button scanButton;
	Button browseButton;
	private Fragment mCurrentFragment;
	
	private Fragment scanFragment;
	private Fragment broweFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		scanButton = (Button) findViewById(R.id.button_scan);
		browseButton = (Button) findViewById(R.id.button_browse);

		scanButton.setOnClickListener(scanListener);
		browseButton.setOnClickListener(browseListener);
		
		//scanFragment = getSupportFragmentManager().findFragmentById(R.id.scan_frag);
		//broweFragment = getSupportFragmentManager().findFragmentById(R.id.image_grid_frag);
		scanFragment = new ScanUIFragment();
		broweFragment = new ImageGridFragment();
		
		mCurrentFragment = scanFragment;
	}

	OnClickListener scanListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
//			Intent mainIntent = new Intent(MainActivity.this,
//					RecordActivity.class);
//			startActivity(mainIntent);
			changeTabState(v.getId());
		}

	};

	OnClickListener browseListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
//			Intent mainIntent = new Intent(MainActivity.this,
//					ImageBrowseActivity.class);
//			startActivity(mainIntent);
			changeTabState(v.getId());
		}

	};
	
	private void changeFragment(Fragment fragment) {
		if (mCurrentFragment != null) {
			getSupportFragmentManager().beginTransaction()
					.detach(mCurrentFragment)
					.replace(R.id.main_container, fragment).attach(fragment)
					.addToBackStack(null).commit();
		} else {
			FragmentTransaction fragmentTransaction = getSupportFragmentManager()
					.beginTransaction();
			fragmentTransaction.replace(R.id.main_container, fragment);
			fragmentTransaction.commitAllowingStateLoss();
		}
		mCurrentFragment = fragment;
	}
	
	private void changeTabState(int buttonId) {
		switch (buttonId) {
		case R.id.button_scan:
			changeFragment(scanFragment);
			break;
		case R.id.button_browse:
			changeFragment(broweFragment);
			break;
		default:
			break;
		}
	}
	
	
	
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
	
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            DBHelper db = DBHelper.getInstance(this);
            db.closeDb();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
