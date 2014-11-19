package com.art.tech;

import java.io.File;

import com.art.tech.callback.LogInCallback;
import com.art.tech.exception.AVException;
import com.art.tech.model.AVUser;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageBrowseActivity extends BaseActivity {
	ImageView imageButton;
	private static final int IMAGE_REQUEST = 657843;
	private static final String TAG = "ImageBrowseActivity";
	GridView feedbackListView;
	ImageAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.browse_grid_view);

		imageButton = (ImageView) findViewById(R.id.button_camera);

		adapter = new ImageAdapter(this);

		feedbackListView = (GridView) findViewById(R.id.grid_view);

		feedbackListView.setAdapter(adapter);

		imageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(
						Intent.createChooser(intent, "Select Picture"),
						IMAGE_REQUEST);
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (IMAGE_REQUEST == requestCode && resultCode == RESULT_OK
				&& data.getData() != null) {
			Uri _uri = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(_uri, filePathColumn,
					null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String filePath = cursor.getString(columnIndex);
			cursor.close();
			try {
				Log.d(TAG, "img picked:" + filePath);
				File attachmentFile = new File(filePath);

				adapter.notifyDataSetChanged();
				feedbackListView.setSelection(feedbackListView.getAdapter()
						.getCount());
				feedbackListView.smoothScrollToPosition(feedbackListView
						.getAdapter().getCount());

			} catch (Exception e) {
				e.printStackTrace();
				Log.e(TAG, e.getMessage());
			}
		}
	}

	public class ViewHolder {
		ImageView image;
	}

	public class ImageAdapter extends BaseAdapter {
		public ImageAdapter(Context c) {
			mContext = c;
		}

		public int getCount() {
			return mThumbIds.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView;
			if (convertView == null) {
				imageView = new ImageView(mContext);
				imageView.setLayoutParams(new GridView.LayoutParams(45, 45));
				imageView.setAdjustViewBounds(false);
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imageView.setPadding(8, 8, 8, 8);
			} else {
				imageView = (ImageView) convertView;
			}

			imageView.setImageResource(mThumbIds[position]);

			return imageView;
		}

		private Context mContext;

		private Integer[] mThumbIds = { R.drawable.image01};
	}
}
