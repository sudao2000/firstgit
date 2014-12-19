package com.art.tech.fragment;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.art.tech.R;
import com.art.tech.db.DBHelper;
import com.art.tech.db.ImageCacheColumn;
import com.art.tech.fragment.ImageGridFragment.ImageAdapter;

import com.art.tech.model.PictureInfo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class ImagePagerFragment extends Fragment {
	private static final int MSG_QUERY_IMAGE = 1;
	private List<PictureInfo> imageUrls = new LinkedList<PictureInfo>();

	DisplayImageOptions options;
	private Handler uiHandler;
	private ImageAdapter imageAdapter;
	private ViewPager pager;
	
	public PictureInfo getCurrentPageProductInfo() {
		return imageUrls.get(pager.getCurrentItem());
	}
	
	public int getPageCount() {
		return imageUrls.size();
	}
	
	public void deleteCurrentImage() {
		PictureInfo pi = getCurrentPageProductInfo();
		new File(pi.url).delete();
		ImageCacheColumn.delete(getActivity(), pi.id);
		
		imageUrls.remove(pager.getCurrentItem());
		
		imageAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error)
				.resetViewBeforeLoading(true).cacheOnDisk(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300)).build();

		uiHandler = new UiHandler();

		initImageUrls();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fr_image_pager, container,
				false);
		pager = (ViewPager) rootView.findViewById(R.id.pager);		
		imageAdapter = new ImageAdapter();		

		return rootView;
	}	

	public void initImageUrls() {
		new ImageQueryThread(uiHandler).start();

	}

	private class UiHandler extends Handler {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_QUERY_IMAGE:
				if (imageAdapter != null) {
					if (pager.getAdapter() == null) {
						pager.setAdapter(imageAdapter);
						pager.setCurrentItem(0);
					}
					imageAdapter.notifyDataSetChanged();
				}
				break;
			}
		}
	}

	private class ImageQueryThread extends Thread {
		private WeakReference<Handler> weakHandler;

		ImageQueryThread(Handler h) {
			weakHandler = new WeakReference<Handler>(h);
		}

		@Override
		public void run() {
			String columns[] = {
					ImageCacheColumn._ID,
					ImageCacheColumn.Url,
					ImageCacheColumn.REAL_CODE };
			DBHelper helper = DBHelper.getInstance(ImagePagerFragment.this
					.getActivity());
			Cursor c = helper.query(ImageCacheColumn.TABLE_NAME, columns, null,
					null);
			if (c != null && c.moveToFirst()) {
				imageUrls.clear();
				do {
					imageUrls.add(new PictureInfo(c.getLong(c
							.getColumnIndex(ImageCacheColumn._ID)), 
							"file://" + new File(c.getString(c
									.getColumnIndex(ImageCacheColumn.Url)))
									.getAbsolutePath(), 
									c.getString(c.getColumnIndex(ImageCacheColumn.REAL_CODE))));
				} while (c.moveToNext());
				c.close();

				if (weakHandler.get() != null) {
					weakHandler.get().sendEmptyMessage(MSG_QUERY_IMAGE);
				}
			}
		}
	}

	private class ImageAdapter extends PagerAdapter {

		private LayoutInflater inflater;

		ImageAdapter() {
			inflater = LayoutInflater.from(getActivity());
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			return imageUrls.size();
		}
		@Override
		public int getItemPosition(Object object) {  
            return POSITION_NONE;  
        }

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			View imageLayout = inflater.inflate(R.layout.item_pager_image,
					view, false);
			assert imageLayout != null;
			ImageView imageView = (ImageView) imageLayout
					.findViewById(R.id.image);
			final ProgressBar spinner = (ProgressBar) imageLayout
					.findViewById(R.id.loading);

			ImageLoader.getInstance().displayImage(imageUrls.get(position).url,
					imageView, options, new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							spinner.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							String message = null;
							switch (failReason.getType()) {
							case IO_ERROR:
								message = "Input/Output error";
								break;
							case DECODING_ERROR:
								message = "Image can't be decoded";
								break;
							case NETWORK_DENIED:
								message = "Downloads are denied";
								break;
							case OUT_OF_MEMORY:
								message = "Out Of Memory error";
								break;
							case UNKNOWN:
								message = "Unknown error";
								break;
							}
							Toast.makeText(getActivity(), message,
									Toast.LENGTH_SHORT).show();

							spinner.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							spinner.setVisibility(View.GONE);
						}
					});

			view.addView(imageLayout, 0);
			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}
	}
}