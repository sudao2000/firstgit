package com.art.tech.board;

import java.lang.ref.WeakReference;

import com.art.tech.R;

import android.app.TabActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;
import android.widget.ToggleButton;

public class activity_Setting extends TabActivity {
	
	private Button btnRead;
	private EditText edtPassword;
	private ProgressDialogEx pd;
	private String cMsg;
	private boolean bSuccess;
	private MHandler handler = null;
	private TextView lblEPC;
	private TextView lblTID;
	private TextView lblBaterry;
	byte[] epcData = new byte[20];
	byte[] tidData = new byte[10];
	private Button btnGet;
	private Button btnSet;
	private ToggleButton tbBeep;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		//
		handler = new MHandler(this);
		//
		TabHost tabs = getTabHost();  
        //����Tab1  
        TabSpec tab1 = tabs.newTabSpec("tab1");  
        tab1.setIndicator("Setting");      // ����tab1�����  
        tab1.setContent(R.id.tab2);    // �����ؼ�  
        tabs.addTab(tab1);                // ���tab1   
          
        //����Tab2  
        TabSpec tab2 = tabs.newTabSpec("tab2");  
        tab2.setIndicator("EPC+TID");        
        tab2.setContent(R.id.tab1);      
        tabs.addTab(tab2); 
        //
        tbBeep = (ToggleButton) findViewById(R.id.toggleButton1);
        lblEPC = (TextView) findViewById(R.id.textView2);
        lblTID = (TextView) findViewById(R.id.textView4);
        lblBaterry = (TextView) findViewById(R.id.battery);
        edtPassword = ((EditText)findViewById(R.id.accesspwd));        
        edtPassword.setText("00000000");
        btnRead = ((Button)findViewById(R.id.button4));
        btnRead.setOnClickListener(new btnRead_Click());
        btnGet = ((Button)findViewById(R.id.button3));
        btnGet.setOnClickListener(new btnGet_Click());
        btnSet = ((Button)findViewById(R.id.button5));
        btnSet.setOnClickListener(new btnSet_Click());
	}
	
	private void showToast(String msg) {
		showToast(msg, R.drawable.icon_info, true);
	}
	
	private void showToast(String msg, int resID, boolean bError) {
	    View toastRoot = getLayoutInflater().inflate(R.layout.toast, null);   
	    Toast toast = new Toast(getApplicationContext());  
	    toast.setGravity(Gravity.CENTER, 0, 0);
	    //if (bError)
	    //	toast.setDuration(Toast.LENGTH_LONG);
	    //else
	    	toast.setDuration(Toast.LENGTH_SHORT);
	    toast.setView(toastRoot);   
	    TextView tv = (TextView)toastRoot.findViewById(R.id.toastbox_message);   
	    tv.setText(msg);  
	    if (resID > 0) {
	    	ImageView iv = (ImageView)toastRoot.findViewById(R.id.toastbox_icon);
	    	iv.setImageResource(resID);
	    }
	    toast.show();
	}
	
	static class MHandler extends Handler {  
        WeakReference<activity_Setting> outerClass;  
      
        MHandler(activity_Setting activity) {  
            outerClass = new WeakReference<activity_Setting>(activity);  
        }  
      
        @Override  
        public void handleMessage(android.os.Message msg) {  
        	activity_Setting theClass = outerClass.get();  
            switch (msg.what) {
	    		case 0:
	    			theClass.pd.dismiss(); // �ر�ProgressDialog
	    			if (!theClass.bSuccess && theClass.cMsg != null)
    				{
    					theClass.showToast(theClass.cMsg);
    				}
    				else if (theClass.bSuccess) {
    					theClass.lblEPC.setText(theClass.convertByteArrayToString(theClass.epcData, 1, theClass.epcData[0]));
    					theClass.lblTID.setText(theClass.convertByteArrayToString(theClass.tidData, 0, 8));
    				}
	    			break;
	    			
	    		case 1:
	    			theClass.pd.dismiss(); // �ر�ProgressDialog
	    			if (!theClass.bSuccess && theClass.cMsg != null)
    				{
    					theClass.showToast(theClass.cMsg);
    				}
    				else if (theClass.bSuccess) {
    					theClass.lblBaterry.setText(String.valueOf(theClass.epcData[0]));
    					if (theClass.epcData[1] == 0)
    						theClass.tbBeep.setChecked(false);
    					else
    						theClass.tbBeep.setChecked(true);
    				}
	    			break;
	    			
	    		case 2:
	    			theClass.pd.dismiss(); // �ر�ProgressDialog
	    			if (!theClass.bSuccess && theClass.cMsg != null)
    				{
    					theClass.showToast(theClass.cMsg);
    				}
    				else if (theClass.bSuccess) {

    				}
	    			break;

            }
        }  
    }
	
	private String convertByteArrayToString(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
	  {
	    String str1 = "";
	    for (int j = paramInt1; j < paramInt1 + paramInt2; j++)
	    {
	      str1 += String.format("%02X", (byte)paramArrayOfByte[j]);
	    }
	    return str1;
	  }
	
	private byte[] convertStringToByteArray(String paramString)
	{
	    int i = paramString.length();
	    byte[] arrayOfByte = new byte[i / 2];
	    for (int j = 0; j < i; j += 2)
	    {
	      int k = Integer.parseInt(paramString.substring(j, j + 1), 16);
	      int m = Integer.parseInt(paramString.substring(j + 1, j + 2), 16);
	      arrayOfByte[(j / 2)] = (byte)(0xFF & m + (k << 4));
	    }
	    return arrayOfByte;
	}
	
	//���갴ť�¼�
    private class btnRead_Click implements android.view.View.OnClickListener
    {
		public void onClick(View v) 
		{ 
			final byte[] accesspsw = convertStringToByteArray(edtPassword.getText().toString());
			pd = ProgressDialogEx.show(activity_Setting.this, "Read Tag...");
			new Thread(){  
                @Override  
                public void run() {  
                	int ret = 0;
                    try {
                    	cMsg = "Device communication error, make sure that is plugged."; 
                    	bSuccess = false;
                    	//ѡ���ǩ
                    	ret = Demo.reader.readEPCTID(accesspsw, tidData, epcData);
						if (ret == 0) {
							bSuccess = true;
						}
						else if (ret > 2) {
							cMsg = "Read tag data failure.";
						}
						else if (ret == -1) {
							cMsg = "Device is running low battery, please charge!";
						}
					} catch (Exception e) {
						cMsg = "Unknown error."; 
                    	bSuccess = false;
					}
                    finally {

                    }
                    handler.sendEmptyMessage(0);
            }}.start();
		}
    }
    

    private class btnGet_Click implements android.view.View.OnClickListener
    {
		public void onClick(View v) 
		{ 
			pd = ProgressDialogEx.show(activity_Setting.this, "Reading config...");
			new Thread(){  
                @Override  
                public void run() {  
                	int ret = 0;
                    try {
                    	cMsg = "Device communication error, make sure that is plugged."; 
                    	bSuccess = false;
                    	ret = Demo.reader.getBatteryBuzzer(epcData);
						if (ret == 0) {
							bSuccess = true;
						}
						else if (ret > 2) {
							cMsg = "Read config failure";
						}
					} catch (Exception e) {
						cMsg = "Unknown error."; 
                    	bSuccess = false;
					}
                    finally {

                    }
                    handler.sendEmptyMessage(1);
            }}.start();
		}
    }
    
    private class btnSet_Click implements android.view.View.OnClickListener
    {
		public void onClick(View v) 
		{ 
			pd = ProgressDialogEx.show(activity_Setting.this, "Write Config...");
			final boolean bBeep = tbBeep.isChecked();
			new Thread(){  
                @Override  
                public void run() {  
                	int ret = 0;
                    try {
                    	cMsg = "Device communication error, make sure that is plugged."; 
                    	bSuccess = false;
                    	ret = Demo.reader.setBuzzer(bBeep);
						if (ret == 0) {
							bSuccess = true;
						}
						else if (ret > 2) {
							cMsg = "Write config failure.";
						}
					} catch (Exception e) {
						cMsg = "Unknown error."; 
                    	bSuccess = false;
					}
                    finally {

                    }
                    handler.sendEmptyMessage(2);
            }}.start();
		}
    }

}
