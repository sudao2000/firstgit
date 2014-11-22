package com.art.tech;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
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
import com.art.tech.model.ProductInfo;
import com.art.tech.util.UIHelper;
import com.art.tech.view.GalleryView;

public class RecordActivity extends BaseActivity {

	private GalleryView gallery;
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
	ImageButton button_camera;

	private Button datePicker;
	private OnClickListener datePickerListener;
	private static final int DATE_DIALOG_ID = 1;
	protected static final int ACTION_CAPTURE_IMAGE = 0;

	private Spinner copyMaterial;

	private final DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;

			updateDisplay();
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_view_container);

		initRes();
	}

	private void initRes() {
		gallery = (GalleryView) findViewById(R.id.mygallery);

		adapter = new ImageAdapter(this);
		adapter.createReflectedImages();
		gallery.setAdapter(adapter);

		datePicker = (Button) findViewById(R.id.datePicker);
		setDialogOnClickListener(R.id.datePicker, DATE_DIALOG_ID);

		{
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
		}
		
		copyName = (EditText) this.findViewById(R.id.copy_name);
		copyOwner = (EditText) this.findViewById(R.id.copy_owner);
		copySizeChang = (EditText) this.findViewById(R.id.copy_size_chang);
		copySizeKuan = (EditText) this.findViewById(R.id.copy_size_kuan);
		copySizeGao = (EditText) this.findViewById(R.id.copy_size_gao);		
		copyDescription = (EditText) this.findViewById(R.id.copy_description);
		copyMoney = (EditText) this.findViewById(R.id.copy_money);
		copyMoneyPublicity = (CheckBox) this.findViewById(R.id.copy_money_publicity);
		
		button_camera = (ImageButton) this
				.findViewById(R.id.button_camera);
		button_camera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String saveLocation = Constants.IMAGE_SAVE_PAHT;
				Uri uri = UIHelper.capureImage(RecordActivity.this,
						ACTION_CAPTURE_IMAGE, saveLocation);
				}
		});
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
		copyOwner.setText(info.copy_owner);
		copySizeChang.setText(info.copy_size_chang);
		copySizeKuan.setText(info.copy_size_kuan);
		copySizeGao.setText(info.copy_size_gao);
		copyDescription.setText(info.copy_description);
		copyMoney.setText(info.copy_money);
		copyMoneyPublicity.setChecked(info.publicity);
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
}
