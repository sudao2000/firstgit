package com.art.tech.util;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

public class UIHelper {
	
	public static Uri capureImage(Activity activity, int actionCode, String saveLocation) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		String cameraPicName = System.currentTimeMillis() + ".jpg";
		File photofile = new File(saveLocation + cameraPicName);
		Uri uri = Uri.fromFile(photofile);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		activity.startActivityForResult(intent, actionCode);
		return uri;
	}
	
	

}
