package com.art.tech;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.art.tech.application.Constants;
import com.art.tech.db.DBHelper;
import com.art.tech.db.ImageCacheColumn;
import com.art.tech.db.ProductInfoColumn;
import com.art.tech.fragment.ImageGalleryFragment;
import com.art.tech.fragment.ImagePagerFragment;
import com.art.tech.model.ProductInfo;
import com.art.tech.util.UIHelper;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

public class ProductDetailActivity extends FragmentActivity {

	private TextView productDetailInfo;
	
	private Button editButton;	
	
	private int mYear;
	private int mMonth;
	private int mDay;

	LinearLayout detailEditView;
	LinearLayout detailSendView;
	
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
	
	private Button buttonOkSend;
	private Button buttonCancelSend;
	
	private ProductInfo currentProductInfo;
	
	private OnClickListener datePickerListener;
	
	private static final int DATE_DIALOG_ID = 1;
	private static final int TYPE_DIALOG_ID = 2;
	private static final int MATERIAL_DIALOG_ID = 3;
	private static final int SIZE_DIALOG_ID = 4;
	
	
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

			updateDate(c);
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
	
	private OnClickListener editListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			productDetailInfo.setVisibility(View.GONE);
			v.setVisibility(View.GONE);
			detailEditView.setVisibility(View.VISIBLE);
			detailSendView.setVisibility(View.GONE);
		}
	};
	private OnClickListener okListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			productDetailInfo.setVisibility(View.GONE);
			editButton.setVisibility(View.GONE);
			detailEditView.setVisibility(View.GONE);
			detailSendView.setVisibility(View.VISIBLE);
			
			ProductInfoColumn.insert(ProductDetailActivity.this, currentProductInfo);
		}
	};
	private OnClickListener cancelListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			productDetailInfo.setVisibility(View.VISIBLE);
			editButton.setVisibility(View.VISIBLE);
			detailEditView.setVisibility(View.GONE);
			detailSendView.setVisibility(View.GONE);
		}
	};
	
	private OnClickListener okListenerSend = new OnClickListener() {
		@Override
		public void onClick(View v) {
			productDetailInfo.setVisibility(View.VISIBLE);
			editButton.setVisibility(View.VISIBLE);
			detailEditView.setVisibility(View.GONE);
			detailSendView.setVisibility(View.GONE);
		}
	};

	private OnClickListener cancelListenerSend = new OnClickListener() {
		@Override
		public void onClick(View v) {
			productDetailInfo.setVisibility(View.GONE);
			editButton.setVisibility(View.GONE);
			detailEditView.setVisibility(View.VISIBLE);
			detailSendView.setVisibility(View.GONE);
		}
	};
	
	
	
	private OnClickListener typeListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			ProductDetailActivity.this.showDialog(TYPE_DIALOG_ID);
		}
	};

	private OnClickListener sizeListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			ProductDetailActivity.this.showDialog(SIZE_DIALOG_ID);
		}
	};

	private OnClickListener materialListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			ProductDetailActivity.this.showDialog(MATERIAL_DIALOG_ID);
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
			detailSendView = (LinearLayout) findViewById(R.id.detail_send);
		}
		{
			datePicker = (Button) findViewById(R.id.datePicker);
			setDialogOnClickListener(R.id.datePicker, DATE_DIALOG_ID);
			Calendar c = Calendar.getInstance();
			updateDate(c);
			
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
			copySize = (Button) this.findViewById(R.id.copy_size);
			
			copySizeChang = (EditText) this.findViewById(R.id.copy_size_chang);
			copySizeKuan = (EditText) this.findViewById(R.id.copy_size_kuan);
			copySizeGao = (EditText) this.findViewById(R.id.copy_size_gao);
			
			copyType.setOnClickListener(typeListener);
			copySize.setOnClickListener(sizeListener);
			copySize.setOnClickListener(materialListener);
		}

		{
			buttonOk = (Button) findViewById(R.id.button_ok);
			buttonOk.setOnClickListener(okListener);
			
			buttonCancel = (Button) findViewById(R.id.button_cancel);
			buttonCancel.setOnClickListener(cancelListener);
			
			buttonOkSend = (Button) findViewById(R.id.button_ok_send);
			buttonOkSend.setOnClickListener(okListenerSend);
			
			buttonCancelSend = (Button) findViewById(R.id.button_cancel_send);
			buttonCancelSend.setOnClickListener(cancelListenerSend);
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
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
		case TYPE_DIALOG_ID:
			return createTypeDialog(types);
		case SIZE_DIALOG_ID:
			return createSizeDialog();
		case MATERIAL_DIALOG_ID:
			return createMaterialDialog(materials);
		}
		return super.onCreateDialog(id);
	}

	private AlertDialog createMaterialDialog(String[] data) {
		return createTypeDialog(data);
	}
	
	private static final String []types = new String[]{ 
			"type1", "type2",
            "type3", "type4", 
            "type5", "type6",
            "type7", "type8"};

	private static final String []materials = new String[]{ 
		"cai1", "cai2",
        "cai3", "cai4", 
        "cai5", "cai6",
        "cai7", "cai8"};
	
	private AlertDialog createTypeDialog(String[] data) {

		LayoutInflater factory = LayoutInflater.from(this);
		final View v = factory.inflate(R.layout.grid_view, null);
		AlertDialog d = new AlertDialog.Builder(this).setView(v).create();
		d.requestWindowFeature(Window.FEATURE_NO_TITLE);

		GridView gridview = (GridView) v;
		ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < 8; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemText", data[i]);
			lstImageItem.add(map);
		}
		SimpleAdapter saImageItems = new SimpleAdapter(this, lstImageItem,// 数据源
				R.layout.grid_view_item,// 显示布局
				new String[] { "itemText" }, new int[] { R.id.itemText });
		gridview.setAdapter(saImageItems);
		gridview.setOnItemClickListener(new ItemClickListener());
		
		return d;
	}

	private AlertDialog createSizeDialog() {

		LayoutInflater factory = LayoutInflater.from(this);
		final View v = factory.inflate(R.layout.detail_edit_size, null);
		AlertDialog d = new AlertDialog.Builder(this).setView(v).create();
		d.requestWindowFeature(Window.FEATURE_NO_TITLE);

		d.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface d) {
				copySize.setText(copySizeChang + "x" + copySizeKuan + "x"
						+ copySizeGao);
			}
			
		});
		
		return d;
	}
	
	private class ItemClickListener implements OnItemClickListener {

		public void onItemClick(AdapterView<?> parent, View view, int position,
				long rowid) {
			HashMap<String, Object> item = (HashMap<String, Object>) parent
					.getItemAtPosition(position);
			// 获取数据源的属性值
			String itemText = (String) item.get("itemText");
			currentProductInfo.copy_type = itemText;
			copyType.setText(itemText);
			ProductDetailActivity.this.dismissDialog(TYPE_DIALOG_ID);
		}
	}
	
	private void updateDate(Calendar c) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String yymmdd = simpleDateFormat.format(c.getTime());
		datePicker.setText(yymmdd);
		currentProductInfo.copy_date = c.getTimeInMillis();
	}

	
	private void updateView(ProductInfo info) {
		
		copyName.setText(info.copy_name);
		copyName.setText(info.copy_name);
		copyType.setText(info.copy_type);
		copyType.setText(info.copy_material);
		
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(info.copy_date);
		updateDate(c);
		
		copySizeChang.setText(info.copy_size_chang);
		copySizeKuan.setText(info.copy_size_kuan);
		copySizeGao.setText(info.copy_size_gao);
	}

	private void updateProductInfo(String name, String type, String material, int chang,
			int kuan, int gao) {
		copyName.setText(name);
		copyType.setText(type);
		copyType.setText(material);
		
		copySizeChang.setText(chang + "");
		copySizeKuan.setText(kuan + "");
		copySizeGao.setText(gao + "");
	}
	
	private void updateEditable(boolean editable) {
		copyName.setEnabled(editable);
		copySizeChang.setEnabled(editable);
		copySizeKuan.setEnabled(editable);
		copySizeGao.setEnabled(editable);
	}
}
