package com.art.tech.fragment;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.art.tech.R;
import com.art.tech.db.DBHelper;
import com.art.tech.db.ImageCacheColumn;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;


public class ImageGalleryFragment extends Fragment {

	public static final int INDEX = 3;

	//String[] imageUrls = new String[] {"file:///storage/emulated/0/arch_tech/download_images/00000001/1416746209000.jpg"};
	private List<String> imageUrlList = new LinkedList<String>();
	
	DisplayImageOptions options;
	ImageAdapter imageAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.build();
	}

	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.browse_image_gallery, container, false);
		Gallery gallery = (Gallery) rootView.findViewById(R.id.gallery);
		imageAdapter = new ImageAdapter();
		
		getImageListFromDB();

		gallery.setAdapter(imageAdapter);
		gallery.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startImagePagerActivity(position);
			}
		});
		return rootView;
	}
	
	private void getImageListFromDB() {
		String columns[] = {ImageCacheColumn.Url};
		DBHelper helper = DBHelper.getInstance(getActivity());
		Cursor c = helper.query(ImageCacheColumn.TABLE_NAME, columns, null, null);
		if (c != null && c.moveToFirst()) {
			do {
				imageUrlList.add("file://" + new File(c.getString(c.getColumnIndex(ImageCacheColumn.Url))).getAbsolutePath());
			} while (c.moveToNext());
			c.close();
		}
	}

	void addImageUrl(String url) {
		if (imageUrlList != null) {
			imageUrlList.add(url);
		}
		updateImageGallery();
	}	
	
	void updateImageGallery() {
		if (imageAdapter != null)
			imageAdapter.notifyDataSetChanged();
	}
	
	public void refetchImageListFromGallery() {
		if (!imageUrlList.isEmpty()) {
			imageUrlList.clear();
		}
		getImageListFromDB();
		updateImageGallery();
	}

	private void startImagePagerActivity(int position) {
		/*
		Intent intent = new Intent(getActivity(), SimpleImageActivity.class);
		intent.putExtra(Constants.Extra.FRAGMENT_INDEX, ImagePagerFragment.INDEX);
		intent.putExtra(Constants.Extra.IMAGE_POSITION, position);
		startActivity(intent);
		*/
	}

	private class ImageAdapter extends BaseAdapter {

		private LayoutInflater inflater;

		ImageAdapter() {
			inflater = LayoutInflater.from(getActivity());
		}

		@Override
		public int getCount() {
			return imageUrlList.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView = (ImageView) convertView;
			if (imageView == null) {
				imageView = (ImageView) inflater.inflate(R.layout.item_gallery_image, parent, false);
			}
			ImageLoader.getInstance().displayImage(imageUrlList.get(position), imageView, options);
			return imageView;
		}
	}
}