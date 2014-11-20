package com.art.tech;

import java.io.FileNotFoundException;
import java.util.Timer;
import java.util.TimerTask;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.art.tech.util.UIHelper;

public class ImageBrowseActivity extends FragmentActivity {
	public static final int ACTION_CAPTURE_IMAGE = 0;

	private boolean isWaitingExit = false;
	ImageButton button_camera;
	private Uri uri;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.browse_grid_view);

		ImageButton button_camera = (ImageButton) this
				.findViewById(R.id.button_camera);
		button_camera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String saveLocation = "/sdcard/";
				uri = UIHelper.capureImage(ImageBrowseActivity.this,
						ACTION_CAPTURE_IMAGE, saveLocation);
				
				
			}

		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (ACTION_CAPTURE_IMAGE == requestCode && resultCode == RESULT_OK
				&& data.getData() != null) {
			
			ContentResolver cr = this.getContentResolver();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(cr
                        .openInputStream(uri));
            } catch (FileNotFoundException e) {
            	
            }
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {

			if (isWaitingExit) {
				isWaitingExit = false;

			} else {
				Toast.makeText(this,
						getResources().getString(R.string.press_again_exit),
						Toast.LENGTH_SHORT).show();
				isWaitingExit = true;
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						isWaitingExit = false;
					}
				}, 3000);
				return true;
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_MENU) {

		}
		return super.onKeyDown(keyCode, event);
	}

}