package com.art.tech.util;

import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class IntentUtil {
	public static void start_activity(Activity activity,Class<?> cls,BasicNameValuePair...name)
	{
		Intent intent=new Intent();
		intent.setClass(activity,cls);
		activity.startActivity(intent);
	}
	
	public static void start_activity(Activity activity,Class<?> cls, String key, Bundle value)
	{
		Intent intent=new Intent();
		intent.setClass(activity,cls);
		intent.putExtra(key, value);
		activity.startActivity(intent);
	}
}
