package com.art.tech.application;

import android.os.Environment;

public class Constants {
	
	public static final String FOLDER = "arch_tech";
	public static final String INTENT_ACTION_LOGOUT = "com.art.tech.LOGOUT";

	public final static String BASE_DIR = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/" + FOLDER + "/";
	
	public final static String IMAGE_SAVE_PAHT = BASE_DIR +"download_images" + "/";

	public static class Config {
		public static final boolean DEVELOPER_MODE = false;
	}
}
