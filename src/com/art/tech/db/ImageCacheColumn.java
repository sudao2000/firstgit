package com.art.tech.db;

import java.util.HashMap;
import java.util.Map;

import com.art.tech.fragment.ImageGridFragment.AsyncListener;

import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class ImageCacheColumn extends DatabaseColumn {

	public final static String TABLE_NAME = "imageCache";
	public final static String TIMESTAMP = "timestamp";
	public final static String Url = "url";
	/**
	 * 单位：天
	 */
	public final static String PAST_TIME = "past_time";
	public final static String REAL_CODE = "real_code";
	
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + TABLE_NAME);
	private static final Map<String, String> mColumnMap = new HashMap<String, String>();
	static {

		mColumnMap.put(_ID, "integer primary key autoincrement");
		mColumnMap.put(TIMESTAMP, "TimeStamp");
		mColumnMap.put(Url, "text");
		mColumnMap.put(PAST_TIME, "TimeStamp");
		mColumnMap.put(REAL_CODE, "text");
	}
	
	public static class ImageInfo {
		public long timestamp;
		public String real_code;
		public String uri;
	};
	
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
	
	public static void insert(Context c, String url, String real_code) {
	    DBHelper dbHelper = DBHelper.getInstance(c);

	    ContentValues values = new ContentValues();                
	    values.put(ImageCacheColumn.Url, url);
	    values.put(ImageCacheColumn.TIMESTAMP, System.currentTimeMillis());
	    values.put(ImageCacheColumn.PAST_TIME, 0);
	    values.put(ImageCacheColumn.REAL_CODE, real_code);
	    
	    dbHelper.insert(ImageCacheColumn.TABLE_NAME, values);
	}
	
	public static int delete(Context c,  long id) {
		DBHelper dbHelper = DBHelper.getInstance(c);
		return dbHelper.delete(ImageCacheColumn.TABLE_NAME, id);
	}

	public static void asyncQuery(Context c, int token, Object cookie, Uri uri, 
				String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		
		AsyncQueryHandler queryHandler = new AsyncQueryHandler(c.getContentResolver()) {
			@Override
			protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
					AsyncListener r = (AsyncListener) cookie;
			        r.updateImageUrls(cursor);
			    }
			};

			// 调用时只需要调用startQuery(int token, Object cookie, ContentURI uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)函数即可：
		queryHandler.startQuery(token, cookie, uri, projection, selection, selectionArgs, sortOrder);	
	}


}
