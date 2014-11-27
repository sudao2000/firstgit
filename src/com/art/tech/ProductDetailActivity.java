package com.art.tech;

import com.art.tech.fragment.ImagePagerFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;


public class ProductDetailActivity extends FragmentActivity {

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			Fragment fr;
			String tag;

			tag = ImagePagerFragment.class.getSimpleName();
			fr = getSupportFragmentManager().findFragmentByTag(tag);
			if (fr == null) {
				fr = new ImagePagerFragment();
				fr.setArguments(getIntent().getExtras());
			}
			getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fr, tag).commit();
		}
}
