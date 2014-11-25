package com.art.tech.board;

import com.art.tech.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

public class ProgressDialogEx extends Dialog {
	private static ProgressDialogEx dialog = null;

	public ProgressDialogEx(Context context) {
		super(context);
	}

	public ProgressDialogEx(Context context, int theme) {
		super(context, theme);
	}

	public static ProgressDialogEx createDialog(Context context) {
		dialog = new ProgressDialogEx(context, R.style.CustomProgressDialog);
		dialog.setContentView(R.layout.customprogressdialog);
		dialog.getWindow().getAttributes().gravity = Gravity.CENTER;

		return dialog;
	}

	public void onWindowFocusChanged(boolean hasFocus) {
		if (dialog == null) {
			return;
		}
		ImageView iv = (ImageView) dialog.findViewById(R.id.toastbox_anim);
		AnimationDrawable ad = (AnimationDrawable) iv.getBackground();
		ad.start();
	}
	
	public static ProgressDialogEx show(Context context, String message) {
		createDialog(context);
		if (dialog != null) {
			TextView tvMsg = (TextView) dialog.findViewById(R.id.toastbox_message);
			if (tvMsg != null) {
				tvMsg.setText(message);
			}
			dialog.show();
		}
		return dialog;
	}
	
	public void setMessage(String strMessage) {
		if (dialog == null) {
			return;
		}
		TextView tvMsg = (TextView) dialog.findViewById(R.id.toastbox_message);
		if (tvMsg != null) {
			tvMsg.setText(strMessage);
		}
	}
}