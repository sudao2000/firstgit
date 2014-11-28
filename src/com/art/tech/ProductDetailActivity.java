package com.art.tech;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.art.tech.application.Constants;
import com.art.tech.fragment.ImageGalleryFragment;
import com.art.tech.fragment.ImagePagerFragment;
import com.art.tech.model.ProductInfo;
import com.art.tech.util.UIHelper;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class ProductDetailActivity extends FragmentActivity {

	private TextView productDetailInfo;
	
	private Button editButton;	
	
	private int mYear;
	private int mMonth;
	private int mDay;

	LinearLayout detailEditView;
	
	EditText copyName;
	private Button copyType;
	private Button copyMaterial;	
	private Button copySize;	
		private EditText copySizeChang;
		private EditText copySizeKuan;
		private EditText copySizeGao;		
	private Button datePicker;
	
	private Button buttonOk;
	private Button buttonCancel;
	
	private ProductInfo currentProductInfo;
	
	private OnClickListener datePickerListener;
	private static final int DATE_DIALOG_ID = 1;
	protected static final int ACTION_CAPTURE_IMAGE = 0;
	private static final String TAG = "ProductDetailActivity";

	//private String saveLocation;

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
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.product_detail_view);


		 
		Fragment fr;
		String tag;

		tag = ImagePagerFragment.class.getSimpleName();
		fr = getSupportFragmentManager().findFragmentByTag(tag);
		if (fr == null) {
			fr = new ImagePagerFragment();
			fr.setArguments(getIntent().getExtras());
		}
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.product_detail_content, fr, tag).commit();
		
		initProductList();//call before setWorkspace
		
		initView();
	}
	
	OnClickListener editListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			productDetailInfo.setVisibility(View.GONE);
			v.setVisibility(View.GONE);
			detailEditView.setVisibility(View.VISIBLE);
		}
	};
	OnClickListener okListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			productDetailInfo.setVisibility(View.VISIBLE);
			editButton.setVisibility(View.VISIBLE);
			detailEditView.setVisibility(View.GONE);
		}
	};
	OnClickListener cancelListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			productDetailInfo.setVisibility(View.VISIBLE);
			editButton.setVisibility(View.VISIBLE);
			detailEditView.setVisibility(View.GONE);
		}
	};
	
	private void initProductList() {
		currentProductInfo = new ProductInfo();
		currentProductInfo.real_code = "00000001";
	}

	private void initView() {
		{
			productDetailInfo = (TextView) findViewById(R.id.product_detail_info);
			productDetailInfo.setText("Hello");
			
			editButton = (Button) findViewById(R.id.product_detail_info_edit);
			editButton.setOnClickListener(editListener);			
			
			detailEditView = (LinearLayout) findViewById(R.id.detail_edit);
		}
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
			copyMaterial = (Button) findViewById(R.id.copy_material);
			copyName = (EditText) this.findViewById(R.id.copy_name);
			copyType = (Button) this.findViewById(R.id.copy_type);
			copySizeChang = (EditText) this.findViewById(R.id.copy_size_chang);
			copySizeKuan = (EditText) this.findViewById(R.id.copy_size_kuan);
			copySizeGao = (EditText) this.findViewById(R.id.copy_size_gao);
		}

//		{
//            Fragment fr;
//            String tag;
//            tag = ImageGalleryFragment.class.getSimpleName();
//            fr = getSupportFragmentManager().findFragmentByTag(tag);
//            if (fr == null) {
//                    fr = new ImageGalleryFragment();
//            }
//
//			//getSupportFragmentManager().beginTransaction().replace(R.id.image_gallery_frag, fr, tag).commit();
//		}

		{
			buttonOk = (Button) findViewById(R.id.button_ok);
			buttonOk.setOnClickListener(okListener);
			
			buttonCancel = (Button) findViewById(R.id.button_cancel);
			buttonCancel.setOnClickListener(cancelListener);			
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
		copySizeChang.setText(chang + "");
		copySizeKuan.setText(kuan + "");
		copySizeGao.setText(gao + "");
	}
	
	private void updateProductInfo(ProductInfo info) {
		
		copyName.setText(info.copy_name);
		copySizeChang.setText(info.copy_size_chang);
		copySizeKuan.setText(info.copy_size_kuan);
		copySizeGao.setText(info.copy_size_gao);
	}
	
	private void updateEditable(boolean editable) {
		copyName.setEnabled(editable);
		copySizeChang.setEnabled(editable);
		copySizeKuan.setEnabled(editable);
		copySizeGao.setEnabled(editable);
	}
}
