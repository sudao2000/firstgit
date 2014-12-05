package com.art.tech.util;

import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;

public class IntentUtil {
	public static void start_activity(Activity activity,Class<?> cls,BasicNameValuePair...name)
	{
		Intent intent=new Intent();
		intent.setClass(activity,cls);
		activity.startActivity(intent);
	}
}
