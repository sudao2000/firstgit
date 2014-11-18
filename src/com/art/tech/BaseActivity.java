package com.art.tech;

import com.art.tech.model.AVUser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

public class BaseActivity extends Activity {

  public BaseActivity activity;
  private String userId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    activity = this;
    userId = null;
    AVUser currentUser = AVUser.getCurrentUser();
    if (currentUser != null) {
      userId = currentUser.getObjectId();
    }
  }

  public String getUserId() {
    //return userId;
	  return "true";
  }

  protected void showError(String errorMessage) {
    showError(errorMessage, activity);
  }

  public void showError(String errorMessage, Activity activity) {
    new AlertDialog.Builder(activity)
        .setTitle(
            activity.getResources().getString(
                R.string.dialog_message_title))
        .setMessage(errorMessage)
        .setNegativeButton(android.R.string.ok,
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog,
                                  int which) {
                dialog.dismiss();
              }
            }).show();
  }

  protected void onPause() {
    super.onPause();
  }

  protected void onResume() {
    super.onResume();
  }
}
