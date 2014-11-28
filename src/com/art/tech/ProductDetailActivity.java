package com.art.tech;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.art.tech.application.Constants;
import com.art.tech.fragment.ImageGalleryFragment;
import com.art.tech.fragment.ImagePagerFragment;
import com.art.tech.model.ProductInfo;
import com.art.tech.util.UIHelper;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
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
	
	private OnClickListener editListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			productDetailInfo.setVisibility(View.GONE);
			v.setVisibility(View.GONE);
			detailEditView.setVisibility(View.VISIBLE);
		}
	};
	private OnClickListener okListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			productDetailInfo.setVisibility(View.VISIBLE);
			editButton.setVisibility(View.VISIBLE);
			detailEditView.setVisibility(View.GONE);
		}
	};
	private OnClickListener cancelListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			productDetailInfo.setVisibility(View.VISIBLE);
			editButton.setVisibility(View.VISIBLE);
			detailEditView.setVisibility(View.GONE);
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
			copySize = (Button) this.findViewById(R.id.copy_size);
			
			copySizeChang = (EditText) this.findViewById(R.id.copy_size_chang);
			copySizeKuan = (EditText) this.findViewById(R.id.copy_size_kuan);
			copySizeGao = (EditText) this.findViewById(R.id.copy_size_gao);
			
			copyType.setOnClickListener(typeListener);
			copySize.setOnClickListener(sizeListener);
			copySize.setOnClickListener(materialListener);
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
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
		case TYPE_DIALOG_ID:
			return createTypeDialog();
		case SIZE_DIALOG_ID:
			return createSizeDialog();
		case MATERIAL_DIALOG_ID:
			return createMaterialDialog();
		}
		return super.onCreateDialog(id);
	}

	private AlertDialog createMaterialDialog() {
		return null;
	}
	
	String []texts = new String[]{ 
			"宫式布局1", "宫式布局2",
            "宫式布局3", "宫式布局4", 
            "宫式布局5", "宫式布局6",
            "宫式布局7", "宫式布局8"};

	private AlertDialog createTypeDialog() {

		LayoutInflater factory = LayoutInflater.from(this);
		final View v = factory.inflate(R.layout.grid_view, null);
		AlertDialog d = new AlertDialog.Builder(this).setView(v).create();
		d.requestWindowFeature(Window.FEATURE_NO_TITLE);

		GridView gridview = (GridView) v;
		ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < 8; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemText", texts[i]);
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
