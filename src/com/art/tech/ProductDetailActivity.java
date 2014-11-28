package com.art.tech;

import com.art.tech.fragment.ImagePagerFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ProductDetailActivity extends FragmentActivity {

	private TextView productDetailInfo;
	private Button editButton;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.product_detail_view);

		productDetailInfo = (TextView) findViewById(R.id.product_detail_info);
		productDetailInfo.setText("Hello");
		editButton = (Button) findViewById(R.id.product_detail_info_edit);
		editButton.setOnClickListener(editListener);
		
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
	}
	
	OnClickListener editListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			
		}
	};
}
