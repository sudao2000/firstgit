package com.art.tech;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.art.tech.application.Constants;
import com.art.tech.db.DBHelper;
import com.art.tech.db.ImageCacheColumn;
import com.art.tech.db.ProductInfoColumn;
import com.art.tech.fragment.ImagePagerFragment;
import com.art.tech.model.ProductInfo;
import com.art.tech.util.UIHelper;

public class ProductDetailActivity extends FragmentActivity {

	private TextView productDetailInfo;
	private ImagePagerFragment fragment;
	
	private Button editButton;	
	
	private int mYear;
	private int mMonth;
	private int mDay;

	private LinearLayout detailEditView;
	private LinearLayout detailSendView;
	
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
	
	private ImageButton buttonAddImage;
	private ImageButton buttonRemoveImage;
	private Uri currentPicUri;
	
	private ProductInfo currentProductInfo;
	
	private OnClickListener datePickerListener;
	
	private static final int DATE_DIALOG_ID = 1;
	private static final int TYPE_DIALOG_ID = 2;
	private static final int MATERIAL_DIALOG_ID = 3;
	private static final int SIZE_DIALOG_ID = 4;
	
	private static final int NO_NAME_MESSAGE_DIALOG = 5;
	
	
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
		setContentView(R.layout.product_detail_view);

		String tag = ImagePagerFragment.class.getSimpleName();
		fragment = (ImagePagerFragment) getSupportFragmentManager().findFragmentByTag(tag);
		if (fragment == null) {
			fragment = new ImagePagerFragment();
			fragment.setArguments(getIntent().getExtras());
		}
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.product_detail_content, fragment, tag).commit();
		
		initProductList();//call before setWorkspace
		
		initView(getIntent());
	}
	
	private OnClickListener editListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			productDetailInfo.setVisibility(View.GONE);
			v.setVisibility(View.GONE);
			detailEditView.setVisibility(View.VISIBLE);
			
			detailEditView.setAnimation(AnimationUtils.loadAnimation(ProductDetailActivity.this, R.anim.dialog_enter));
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
			detailSendView.setAnimation(AnimationUtils.loadAnimation(ProductDetailActivity.this, R.anim.dialog_enter));
			
			
			currentProductInfo.copy_name = copyName.getText().toString().trim();
			
			if (currentProductInfo.copy_name == null || currentProductInfo.copy_name.isEmpty()) {
				showDialog(NO_NAME_MESSAGE_DIALOG);
				return;
			}
			
			ProductInfoColumn.delete(ProductDetailActivity.this, currentProductInfo.id);
			
			if (ProductInfoColumn.insert(ProductDetailActivity.this, currentProductInfo) > 0) {
				setProductInfoView();
			} else {
				throw new IllegalStateException("fail to insert product info into database");
			}
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
	
    private AlertDialog createNoNameDialog() {
        return new AlertDialog.Builder(ProductDetailActivity.this)
        .setTitle(R.string.no_name_message)
        .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        }).create();

    }
	
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
	
	private OnClickListener deletePictureListener = new OnClickListener() {
		@Override
		public void onClick(View v) {			
			//ImageCacheColumn.delete(c, id);
			//ProductInfoColumn.delete(c, id);
			
			//delete file 
		}
	};

	private void initProductList() {
		currentProductInfo = new ProductInfo();
		currentProductInfo.real_code = getIntent().getStringExtra(ProductInfoColumn.REAL_CODE);
	}
	
	private void setProductInfoView() {
		StringBuilder sb = new StringBuilder();
		String size = currentProductInfo.copy_size_chang + "x" + currentProductInfo.copy_size_kuan + "x"
				+ currentProductInfo.copy_size_gao;
		
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(currentProductInfo.copy_date);
		String yymmdd = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());

		sb.append(currentProductInfo.copy_type).append(Constants.WHITE_SPACE)
			.append(currentProductInfo.copy_material).append(Constants.WHITE_SPACE)
			.append(size).append(Constants.WHITE_SPACE)
			.append(yymmdd);
		
		productDetailInfo.setText(sb.toString());
	}

	private void initView(Intent intent) {
		{
			productDetailInfo = (TextView) findViewById(R.id.product_detail_info);
			
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
			
			copyType.setOnClickListener(typeListener);
			copySize.setOnClickListener(sizeListener);
			copyMaterial.setOnClickListener(materialListener);
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
		{
			buttonAddImage = (ImageButton) findViewById(R.id.button_image_add);
			buttonAddImage.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					String saveLocation = Constants.IMAGE_SAVE_PAHT
							+ currentProductInfo.real_code + "/";
					Log.d(TAG, "kurt : " + saveLocation);
						currentPicUri = UIHelper.capureImage(ProductDetailActivity.this,
							ACTION_CAPTURE_IMAGE, saveLocation);
						if (currentPicUri == null) {
							Log.e(TAG, "error, fail to capture image at : " + saveLocation);
						} else {
							Log.v(TAG, "succeed to capture image at" + currentPicUri  );
						}
					}
				
			});
			
			buttonRemoveImage = (ImageButton) findViewById(R.id.button_image_remove);
			buttonRemoveImage.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					//fragment.deleteCurrentImage();
				}
				
			});
		}
		
		String realCode = intent.getStringExtra(ProductInfoColumn.REAL_CODE);

    	setTitle(intent.getStringExtra(ProductInfoColumn.COPY_NAME) + "  " + realCode);
		String where = ProductInfoColumn.REAL_CODE + "=" + "'" + realCode + "'";
		new QueryProductInfoTask().execute(where);
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
			return createTypeDialog(types, new TypeItemClickListener());
		case SIZE_DIALOG_ID:
			return createSizeDialog();
		case MATERIAL_DIALOG_ID:
			return createMaterialDialog(materials, new MaterialItemClickListener());
		case NO_NAME_MESSAGE_DIALOG:
		    return createNoNameDialog();
		}
		return super.onCreateDialog(id);
	}
	
	private static final String []types = new String[]{ 
			"雕塑", "油画",
            "书法", "陶瓷", 
            "国画", "装置",
            "摄影", "版画",
            "雕刻", "其他"};

	private static final String []materials = new String[]{ 
		"金属", "木材",
        "玉石", "玻璃钢", 
        "粘土", "石膏",
        "玻璃", "树脂",
        "综合材料", "其他"};
	
	private AlertDialog createMaterialDialog(String[] data, MaterialItemClickListener l) {

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
		gridview.setOnItemClickListener(l);
		
		return d;
	}
	
	private AlertDialog createTypeDialog(String[] data, TypeItemClickListener l) {

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
		gridview.setOnItemClickListener(l);
		
		return d;
	}

	private AlertDialog createSizeDialog() {

		LayoutInflater factory = LayoutInflater.from(this);
		final View v = factory.inflate(R.layout.detail_edit_size, null);
		
		copySizeChang = (EditText) v.findViewById(R.id.copy_size_chang);
		copySizeKuan = (EditText) v.findViewById(R.id.copy_size_kuan);
		copySizeGao = (EditText) v.findViewById(R.id.copy_size_gao);		
		
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {

				currentProductInfo.copy_size_chang = Integer.parseInt(copySizeChang.getText().toString());
				currentProductInfo.copy_size_kuan = Integer.parseInt(copySizeKuan.getText().toString());
				currentProductInfo.copy_size_gao =  Integer.parseInt(copySizeGao.getText().toString());
	
				copySize.setText(copySizeChang + "x" + copySizeKuan + "x" + copySizeGao);
			}
		};
		
		AlertDialog d = new AlertDialog.Builder(this).setView(v)
				//.setPositiveButton(R.string.dialog_OK, listener)
				.create();
		d.requestWindowFeature(Window.FEATURE_NO_TITLE);

		d.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface d) {
				currentProductInfo.copy_size_chang = Integer.parseInt(copySizeChang.getText().toString());
				currentProductInfo.copy_size_kuan = Integer.parseInt(copySizeKuan.getText().toString());
				currentProductInfo.copy_size_gao =  Integer.parseInt(copySizeGao.getText().toString());
				
				copySize.setText(copySizeChang.getText().toString()  + "x" + copySizeKuan.getText().toString()  + "x"
						+ copySizeGao.getText().toString());
			}
			
		});
		
		return d;
	}
	
	private class QueryProductInfoTask extends AsyncTask<String, Integer, ProductInfo> {
        @Override  
        protected ProductInfo doInBackground(String... params) {
        	String where = params[0];
        	
			DBHelper helper = DBHelper.getInstance(ProductDetailActivity.this);
			
			Cursor c = helper.query(ProductInfoColumn.TABLE_NAME,
					ProductInfoColumn.columns, where, null);

			if (c != null && c.moveToFirst()) {
				long _id = c.getLong(c.getColumnIndex(ProductInfoColumn._ID));

				String realCode = new String(c.getString(c
						.getColumnIndex(ProductInfoColumn.REAL_CODE)));
				String name = new String(c.getString(c
						.getColumnIndex(ProductInfoColumn.COPY_NAME)));
				
				String type = new String(c.getString(c
						.getColumnIndex(ProductInfoColumn.COPY_TYPE)));
				String material = new String(c.getString(c
						.getColumnIndex(ProductInfoColumn.COPY_MATERIAL)));

				int chang = c.getInt(c
						.getColumnIndex(ProductInfoColumn.COPY_SIZE_CHANG));
				int kuan = c.getInt(c
						.getColumnIndex(ProductInfoColumn.COPY_SIZE_KUAN));
				int gao = c.getInt(c
						.getColumnIndex(ProductInfoColumn.COPY_SIZE_GAO));
				long date = c.getLong(c
						.getColumnIndex(ProductInfoColumn.COPY_DATE));

				ProductInfo info = new ProductInfo();
				info.id = _id;
				info.real_code = realCode;
				info.copy_name = name;
				info.copy_type = type;
				info.copy_material = material;

				info.copy_size_chang = chang;
				info.copy_size_kuan = kuan;
				info.copy_size_gao = gao;
				info.copy_date = date;

				c.close();
				return info;
			}
        	return null;
        }
        
        @Override  
        protected void onProgressUpdate(Integer... progresses) {
        }
        
        @Override  
        protected void onPostExecute(ProductInfo result) {
        	if (null == result) {
        		return;
        	}
        	currentProductInfo = result;
        	updateView(currentProductInfo);
        }  
	}
	
	private class TypeItemClickListener implements OnItemClickListener {

		public void onItemClick(AdapterView<?> parent, View view, int position,
				long rowid) {
			HashMap<String, Object> item = (HashMap<String, Object>) parent
					.getItemAtPosition(position);
			String itemText = (String) item.get("itemText");
			currentProductInfo.copy_type = itemText;
			copyType.setText(itemText);
			ProductDetailActivity.this.dismissDialog(TYPE_DIALOG_ID);
		}
	}
	
	private class MaterialItemClickListener implements OnItemClickListener {

		public void onItemClick(AdapterView<?> parent, View view, int position,
				long rowid) {
			HashMap<String, Object> item = (HashMap<String, Object>) parent
					.getItemAtPosition(position);
			String itemText = (String) item.get("itemText");
			currentProductInfo.copy_material = itemText;
			copyMaterial.setText(itemText);
			ProductDetailActivity.this.dismissDialog(MATERIAL_DIALOG_ID);
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
		copyType.setText(info.copy_type);
		copyMaterial.setText(info.copy_material);
		
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(info.copy_date);
		updateDate(c);
		
		copySize.setText(info.copy_size_chang + "x" + info.copy_size_kuan+ "x" + info.copy_size_gao);
		setProductInfoView();
	}

	private void updateProductInfo(String name, String type, String material, int chang,
			int kuan, int gao) {
		copyName.setText(name);
		copyType.setText(type);
		copyType.setText(material);

	}
	
	private void updateEditable(boolean editable) {
		copyName.setEnabled(editable);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (ACTION_CAPTURE_IMAGE == requestCode && resultCode == RESULT_OK) {
			if (currentPicUri == null) {
				Log.e(TAG, "fail to take a picture, currentImage url " + currentPicUri);
				return;
			}
            DBHelper dbHelper = DBHelper.getInstance(this);
            
            
            ContentValues values = new ContentValues();                
            values.put(ImageCacheColumn.Url, currentPicUri.getPath());
            values.put(ImageCacheColumn.TIMESTAMP, System.currentTimeMillis());
            values.put(ImageCacheColumn.PAST_TIME, 0);
            values.put(ImageCacheColumn.REAL_CODE, currentProductInfo.real_code);
            
            dbHelper.insert(ImageCacheColumn.TABLE_NAME, values);

            ((ImagePagerFragment) getSupportFragmentManager().findFragmentByTag(ImagePagerFragment.class.getSimpleName())).initImageUrls();            
		}
	}

}
