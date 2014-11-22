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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.art.tech.application.Constants;
import com.art.tech.util.UIHelper;

public class ImageBrowseActivity extends FragmentActivity {
	public static final int ACTION_CAPTURE_IMAGE = 0;

	private boolean isWaitingExit = false;
	Button product_report;
	private Uri uri;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.browse_grid_view);

		product_report = (Button) this
				.findViewById(R.id.product_report);
		product_report.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(ImageBrowseActivity.this,
						ProductReportActivity.class);
				startActivity(i);
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



}