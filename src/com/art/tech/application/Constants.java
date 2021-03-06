package com.art.tech.application;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileFilter;
import java.io.IOException;

import com.art.tech.R;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.Environment;

public class Constants {
	
	public static final String FOLDER = "arch_tech";
	public static final String INTENT_ACTION_LOGOUT = "com.art.tech.LOGOUT";

	public final static String BASE_DIR = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/" + FOLDER + "/";
	
	public final static String IMAGE_SAVE_PAHT = BASE_DIR +"download_images" + "/";
	
	public final static String NO_PICTURE_PRODUCT_IAMAGE = "file:///android_asset/image.png";

	public final static int EXIT_TIMEOUT = 2000;
	
	public final static String WHITE_SPACE = " ";
	public static class Config {
		public static final boolean DEVELOPER_MODE = false;
	}
	
	public static FileFilter jpgFilefilter = new FileFilter() {
        public boolean accept(File file) {
            if (file.getName().endsWith(".jpg")) {
                return true;
            }
            return false;
        }
    };
}
