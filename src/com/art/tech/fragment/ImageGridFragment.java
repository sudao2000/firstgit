package com.art.tech.fragment;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.art.tech.ProductDetailActivity;
import com.art.tech.R;
import com.art.tech.application.Constants;
import com.art.tech.db.DBHelper;
import com.art.tech.db.ImageCacheColumn;
import com.art.tech.db.ProductInfoColumn;
import com.art.tech.model.PictureInfo;
import com.art.tech.model.ProductInfo;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class ImageGridFragment extends Fragment {

	private static final String TAG = "ImageGridFragment";
	private static final int MSG_QUERY_IMAGE = 1;
	private static final int MSG_QUERY_IMAGE_FINISH = 2;
	
	private List<PictureInfo> imageUrls = new LinkedList<PictureInfo>();
	private Set<String> realCodeSet = new HashSet<String>();
	private ConcurrentHashMap<String, ProductInfo> map = new ConcurrentHashMap<String, ProductInfo>();

	DisplayImageOptions options;
	private ImageAdapter imageAdapter;
	private GridView gridView;
	private Handler uiHandler;

	public void addImageUrl(long id, String url, String realCode) {
		if (!realCodeSet.contains(realCode)) {
			realCodeSet.add(realCode);
			imageUrls.add(new PictureInfo(id, url, realCode));				
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(com.art.tech.R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fr_image_grid, container,
				false);
		gridView = (GridView) rootView.findViewById(R.id.grid);

		imageAdapter = new ImageAdapter();
		((GridView) gridView).setAdapter(imageAdapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.v(TAG, "position " + position + " id : " + id);
				startImagePagerActivity(position);
			}
		});
		
		
		uiHandler = new UiHandler();

		initImageUrls();

		return rootView;
	}
	
	protected void startImagePagerActivity(int position) {
		Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
		intent.putExtra(ProductInfoColumn.COPY_NAME, map.get(imageUrls.get(position).realCode).copy_name);
		intent.putExtra(ProductInfoColumn.REAL_CODE, imageUrls.get(position).realCode);
		startActivity(intent);
	}
	
	public interface AsyncListener {
		void updateImageUrls(Cursor c);
	}
	
	private class UiHandler extends Handler {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_QUERY_IMAGE:
				if (imageAdapter != null)
					imageAdapter.notifyDataSetChanged();
				break;
			case MSG_QUERY_IMAGE_FINISH:
				queryProductInfo();
				break;
			}
		}
	}
	
	

	private class QueryProductInfoTask extends AsyncTask<String, Integer, Void> {
        @Override  
        protected void onPreExecute() {
        }
        @Override  
        protected Void doInBackground(String... params) {
        	String where = params[0];
        	
			DBHelper helper = DBHelper.getInstance(ImageGridFragment.this.getActivity());
			
			Cursor c = helper.query(ProductInfoColumn.TABLE_NAME,
					ProductInfoColumn.columns, null, null);
			
			while (c != null && c.moveToNext()) {
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

				map.put(info.real_code, info);
				
				Log.d(TAG, "real code" + info.real_code);
			}
			
			c.close();
			return null;
        }

        protected void onPostExecute(Void a) {
        	if (imageUrls.isEmpty()) {
        		for (String realcode : map.keySet()) {        			
        			addImageUrl(-1, Constants.NO_PICTURE_PRODUCT_IAMAGE, realcode);
        		}
        	}
        		
        	for (PictureInfo info : imageUrls) {
        		if (!map.containsKey(info.realCode)) {
        			addImageUrl(-1, Constants.NO_PICTURE_PRODUCT_IAMAGE, info.realCode);
        		}
        	}
        	imageAdapter.notifyDataSetChanged();
        }
	}
	
	private void queryProductInfo() {
		new QueryProductInfoTask().execute(new String[]{"invalid where"});
	}
	
	private class ImageQueryThread extends Thread {
		private WeakReference<Handler> weakHandler;
		ImageQueryThread(Handler h) {
			weakHandler = new WeakReference<Handler>(h);
		}
		
		@Override
		public void run() {
			String columns[] = {ImageCacheColumn._ID, ImageCacheColumn.Url , ImageCacheColumn.REAL_CODE};
			DBHelper helper = DBHelper.getInstance(ImageGridFragment.this.getActivity());
			Cursor c = helper.query(ImageCacheColumn.TABLE_NAME, columns, null,
					null);
			if (c != null && c.moveToFirst()) {
				do {
					String realCode = c.getString(c.getColumnIndex(ImageCacheColumn.REAL_CODE));
					if (!realCodeSet.contains(realCode)) {
						realCodeSet.add(realCode);						
						ImageGridFragment.this.imageUrls.add(
								new PictureInfo(c.getLong(c.getColumnIndex(ImageCacheColumn._ID)),
										"file://"+ new File(c.getString(c
										.getColumnIndex(ImageCacheColumn.Url)))
										.getAbsolutePath(),
										realCode));
						
						Log.d(TAG, "ImageCacheColumn realcode added" + c.getString(c.getColumnIndex(ImageCacheColumn.REAL_CODE)));
					}
					
					Log.d(TAG, "ImageCacheColumn real code" + c.getString(c.getColumnIndex(ImageCacheColumn.REAL_CODE)));
					
				} while (c.moveToNext());
				c.close();
				
				if (weakHandler.get() != null) {
					weakHandler.get().sendEmptyMessage(MSG_QUERY_IMAGE);
					
				}				
			}
			
			weakHandler.get().sendEmptyMessage(MSG_QUERY_IMAGE_FINISH);
		}
	}

	private void initImageUrls() {
		imageUrls.clear();
		realCodeSet.clear();
		new ImageQueryThread(uiHandler).start();

	}

	public class ImageAdapter extends BaseAdapter {

		private LayoutInflater inflater;

		ImageAdapter() {
			inflater = LayoutInflater.from(getActivity());
		}

		@Override
		public int getCount() {
			return imageUrls.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			View view = convertView;
			if (view == null) {
				view = inflater
						.inflate(R.layout.item_grid_image, parent, false);
				holder = new ViewHolder();
				assert view != null;
				holder.imageView = (ImageView) view.findViewById(R.id.image);
				holder.progressBar = (ProgressBar) view
						.findViewById(R.id.progress);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			ImageLoader.getInstance().displayImage(imageUrls.get(position).url,
					holder.imageView, options,
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							holder.progressBar.setProgress(0);
							holder.progressBar.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							holder.progressBar.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							holder.progressBar.setVisibility(View.GONE);
						}
					}, new ImageLoadingProgressListener() {
						@Override
						public void onProgressUpdate(String imageUri,
								View view, int current, int total) {
							holder.progressBar.setProgress(Math.round(100.0f
									* current / total));
						}
					});

			return view;
		}
	}

	static class ViewHolder {
		ImageView imageView;
		ProgressBar progressBar;
	}
}