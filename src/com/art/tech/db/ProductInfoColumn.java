package com.art.tech.db;

import java.util.HashMap;
import java.util.Map;

import com.art.tech.model.ProductInfo;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

public class ProductInfoColumn extends DatabaseColumn {

	public static final String TABLE_NAME = "productInfo";

	public static final String ID = "id";
	public static final String REAL_CODE = "real_code";
	public static final String COPY_NAME = "copy_name";
	public static final String COPY_TYPE = "copy_type";
	public static final String COPY_MATERIAL = "copy_material";
	public static final String COPY_DATE = "copy_date";

	public static final String COPY_SIZE_CHANG = "copy_size_chang";
	public static final String COPY_SIZE_KUAN = "copy_size_kuan";
	public static final String COPY_SIZE_GAO = "copy_size_gao";
	

	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + TABLE_NAME);
	
	private static final Map<String, String> mColumnMap = new HashMap<String, String>();
	static {
		mColumnMap.put(_ID, "integer primary key autoincrement");
		mColumnMap.put(REAL_CODE, "text");
		mColumnMap.put(COPY_NAME, "text");
		mColumnMap.put(COPY_TYPE, "text");
		mColumnMap.put(COPY_MATERIAL, "text");
		mColumnMap.put(COPY_SIZE_CHANG, "integer");
		mColumnMap.put(COPY_SIZE_KUAN, "integer");
		mColumnMap.put(COPY_SIZE_GAO, "integer");
		mColumnMap.put(COPY_DATE, "TimeStamp");
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return TABLE_NAME;
	}

	@Override
	public Uri getTableContent() {
		// TODO Auto-generated method stub
		return CONTENT_URI;
	}

	@Override
	protected Map<String, String> getTableMap() {
		// TODO Auto-generated method stub
		return mColumnMap;
	}
	
	public static void insert(Context c, ProductInfo info) {
		DBHelper dbHelper = DBHelper.getInstance(c);

	    ContentValues values = new ContentValues();    
	    
		values.put(_ID, "integer primary key autoincrement");
		values.put(REAL_CODE, info.real_code);
		values.put(COPY_NAME, info.copy_name);
		values.put(COPY_TYPE, info.copy_type);
		values.put(COPY_MATERIAL, info.copy_material);
		values.put(COPY_SIZE_CHANG, info.copy_size_chang);
		values.put(COPY_SIZE_KUAN, info.copy_size_kuan);
		values.put(COPY_SIZE_GAO, info.copy_size_gao);
		values.put(COPY_DATE, info.copy_date);
	    
	    dbHelper.insert(ImageCacheColumn.TABLE_NAME, values);
	}
	
	public static void insert(Context c, String real_code, String name, String type, String material, int chang, int kuan, int gao, long timestamp) {
	    DBHelper dbHelper = DBHelper.getInstance(c);

	    ContentValues values = new ContentValues();    
	    
		values.put(_ID, "integer primary key autoincrement");
		values.put(REAL_CODE, real_code);
		values.put(COPY_NAME, name);
		values.put(COPY_TYPE, type);
		values.put(COPY_MATERIAL, material);
		values.put(COPY_SIZE_CHANG, chang);
		values.put(COPY_SIZE_KUAN, kuan);
		values.put(COPY_SIZE_GAO, gao);
		values.put(COPY_DATE, timestamp);
	    
	    dbHelper.insert(ImageCacheColumn.TABLE_NAME, values);
	}

}
