package com.art.tech;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.art.tech.application.Constants;
import com.art.tech.board.ScanActivity;
import com.art.tech.board.ScanUIFragment;
import com.art.tech.board.ScanUIFragment.OnSubmitProduct;
import com.art.tech.db.DBHelper;
import com.art.tech.fragment.ImageGridFragment;
import com.art.tech.util.IntentUtil;

public class MainActivity extends FragmentActivity implements OnSubmitProduct {
	Button scanButton;
	Button browseButton;
	private Fragment mCurrentFragment;
	
	private Fragment scanFragment;
	private Fragment broweFragment;
	
	private LinearLayout footer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		scanButton = (Button) findViewById(R.id.button_scan);
		browseButton = (Button) findViewById(R.id.button_browse);

		scanButton.setOnClickListener(scanListener);
		browseButton.setOnClickListener(browseListener);
		
		footer = (LinearLayout) findViewById(R.id.main_footer);
		
		scanFragment = new ScanUIFragment();
		((ScanUIFragment) scanFragment).setOnSubmitProductListener(this);
		broweFragment = new ImageGridFragment();
		
		changeFragment(broweFragment);
	}

	OnClickListener scanListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			//changeTabState(v.getId());
			IntentUtil.start_activity(MainActivity.this, ScanActivity.class, null);
		}
	};

	OnClickListener browseListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
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
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actions, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
        case R.id.menu_edit_profile:
        	IntentUtil.start_activity(this, EditProfileActivity.class, null);
        	break;
        case R.id.menu_about:
        	break;	
        }
        return false;
    }

	@Override
	public void onSubmit() {
		footer.setVisibility(View.VISIBLE);
	}
}
