package com.art.tech;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.art.tech.adapter.ImageAdapter;
import com.art.tech.application.Constants;
import com.art.tech.db.DBHelper;
import com.art.tech.db.ImageCacheColumn;
import com.art.tech.fragment.ImageGalleryFragment;
import com.art.tech.model.ProductInfo;
import com.art.tech.util.UIHelper;
import com.art.tech.view.GalleryView;

public class RecordActivity extends FragmentActivity {

	//private GalleryView gallery;
	
	ImageGalleryFragment imageGallery;
	private ImageAdapter adapter;

	private int mYear;
	private int mMonth;
	private int mDay;

	EditText copyName;
	EditText copyOwner;
	EditText copySizeChang;
	EditText copySizeKuan;
	EditText copySizeGao;
	EditText copyDescription;
	EditText copyMoney;
	CheckBox copyMoneyPublicity;
	
	ImageButton buttonCamera;
	Uri currentPicUri;

	private Button datePicker;
	private OnClickListener datePickerListener;
	private static final int DATE_DIALOG_ID = 1;
	protected static final int ACTION_CAPTURE_IMAGE = 0;
	private static final String TAG = "RecordActivity";

	private Spinner copyMaterial;
	
	private Button buttonOk;
	private Button buttonCancel;
	
	private ProductInfo currentProductInfo;
	
	private String saveLocation;

	private final DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			
			Calendar c = Calendar.getInstance();
			c.set(year, monthOfYear + 1, dayOfMonth);
			
			mYear = year;
			mMonth = monthOfYear + 1;
			mDay = dayOfMonth;

			updateDisplay(c);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_view_container);

		initProductList();//call before setWorkspace
		
		setWorkspace();
		
		initRes();
		
	}
	
	private void setWorkspace() {
		saveLocation = Constants.IMAGE_SAVE_PAHT
				+ currentProductInfo.real_code + "/";
	}
	
	private void initProductList() {
		currentProductInfo = new ProductInfo();
		currentProductInfo.real_code = "00000001";
	}

	private void initRes() {
		//gallery = (GalleryView) findViewById(R.id.mygallery);
		
		//imageGallery = this.gets(ImageGalleryFragment) findViewById(R.id.image_gallery_frag);
		
		
		//adapter = new ImageAdapter(this, saveLocation);
		
		//adapter.createReflectedImages();
		//gallery.setAdapter(adapter);

		{
			datePicker = (Button) findViewById(R.id.datePicker);
			setDialogOnClickListener(R.id.datePicker, DATE_DIALOG_ID);
			Calendar c = Calendar.getInstance();
			updateDisplay(c);
			
			datePickerListener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					showDialog(DATE_DIALOG_ID);
				}

			};
			datePicker.setOnClickListener(datePickerListener);
		}

		{
			copyMaterial = (Spinner) findViewById(R.id.copy_material);
			ArrayAdapter<CharSequence> adapter = ArrayAdapter
					.createFromResource(this, R.array.colors,
							android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			copyMaterial.setAdapter(adapter);
			copyMaterial
					.setOnItemSelectedListener(new OnItemSelectedListener() {
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {

						}

						public void onNothingSelected(AdapterView<?> parent) {

						}
					});
		}

		{
			/*
			gallery.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					// tvTitle.setText(adapter.titles[position]);
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});

			gallery.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Toast.makeText(RecordActivity.this,
							"img " + (position + 1) + " selected",
							Toast.LENGTH_SHORT).show();
				}
			});
			*/
            Fragment fr;
            String tag;
            tag = ImageGalleryFragment.class.getSimpleName();
            fr = getSupportFragmentManager().findFragmentByTag(tag);
            if (fr == null) {
                    fr = new ImageGalleryFragment();
            }

			//getSupportFragmentManager().beginTransaction().replace(R.id.image_gallery_frag, fr, tag).commit();
		}
		
		copyName = (EditText) this.findViewById(R.id.copy_name);
		copySizeChang = (EditText) this.findViewById(R.id.copy_size_chang);
		copySizeKuan = (EditText) this.findViewById(R.id.copy_size_kuan);
		copySizeGao = (EditText) this.findViewById(R.id.copy_size_gao);		
		
		buttonCamera = (ImageButton) this
				.findViewById(R.id.button_camera);
		buttonCamera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String saveLocation = Constants.IMAGE_SAVE_PAHT
						+ currentProductInfo.real_code + "/";
				Log.d(TAG, "kurt : " + saveLocation);
					currentPicUri = UIHelper.capureImage(RecordActivity.this,
						ACTION_CAPTURE_IMAGE, saveLocation);
					if (currentPicUri == null) {
						Log.d(TAG, "kurt 1 : " + saveLocation);
					} else {
						Log.d(TAG, "kurt 2 : " + currentPicUri  );
					}
				}
		});
		
		{
			buttonOk = (Button) this
					.findViewById(R.id.button_ok);
			buttonOk.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View v) {
					Intent i = new Intent(RecordActivity.this, ProductListActivity.class);
					startActivity(i);
				}
			});
			buttonCancel = (Button) this
					.findViewById(R.id.button_cancel);
			buttonCancel.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View v) {
					RecordActivity.this.finish();
				}
			});
			
		}
	}

	private void setDialogOnClickListener(int buttonId, final int dialogId) {
		Button b = (Button) findViewById(buttonId);
		b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(dialogId);
			}
		});
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);
		}
		return super.onCreateDialog(id);
	}

	private void updateDisplay() {
		datePicker.setText(new StringBuilder()
				// Month is 0 based so add 1
				.append(mYear).append("-").append(mMonth + 1).append("-").append(mDay));

	}
	
	private void updateDisplay(Calendar c) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String yymmdd = simpleDateFormat.format(c.getTime());
		datePicker.setText(yymmdd);

	}

	private void updateProductInfo(String name, String owner, int chang,
			int kuan, int gao, String description, int money, boolean publicity) {
		copyName.setText(name);
		copyOwner.setText(owner);
		copySizeChang.setText(chang + "");
		copySizeKuan.setText(kuan + "");
		copySizeGao.setText(gao + "");
		copyDescription.setText(description);
		copyMoney.setText(money  + "");
		copyMoneyPublicity.setChecked(publicity);
	}
	
	private void updateProductInfo(ProductInfo info) {
		
		copyName.setText(info.copy_name);
		copySizeChang.setText(info.copy_size_chang);
		copySizeKuan.setText(info.copy_size_kuan);
		copySizeGao.setText(info.copy_size_gao);
	}
	
	private void updateEditable(boolean editable) {
		copyName.setEnabled(editable);
		copyOwner.setEnabled(editable);
		copySizeChang.setEnabled(editable);
		copySizeKuan.setEnabled(editable);
		copySizeGao.setEnabled(editable);
		copyDescription.setEnabled(editable);
		copyMoney.setEnabled(editable);
		copyMoneyPublicity.setEnabled(editable);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (ACTION_CAPTURE_IMAGE == requestCode && resultCode == RESULT_OK) {
			
			ContentResolver cr = this.getContentResolver();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(cr
                        .openInputStream(currentPicUri));

                DBHelper dbHelper = DBHelper.getInstance(RecordActivity.this);
                Log.d(TAG, "currentImage url " + currentPicUri);
                
                ContentValues values = new ContentValues();                
                values.put(ImageCacheColumn.Url, currentPicUri.getPath());
                values.put(ImageCacheColumn.TIMESTAMP, System.currentTimeMillis());
                values.put(ImageCacheColumn.PAST_TIME, 0);
                values.put(ImageCacheColumn.REAL_CODE, currentProductInfo.real_code);
                
                dbHelper.insert(ImageCacheColumn.TABLE_NAME, values);
                ImageGalleryFragment igf = (ImageGalleryFragment) getSupportFragmentManager()
                		.findFragmentByTag(ImageGalleryFragment.class.getSimpleName());
                igf.refetchImageListFromGallery();
                
                /*
                Cursor cursor = this.getContentResolver().query(currentPicUri, null,
                		null, null, null);
                if (cursor.moveToFirst()) {
                	String path = cursor.getString(cursor
                			.getColumnIndex("_data"));
                	Log.d(TAG, "kurt uri : " + path);
                }
                */
            } catch (FileNotFoundException e) {
            	Log.e(TAG, e.getMessage());
            }
		}
	}
}
