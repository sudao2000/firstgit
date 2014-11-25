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
import android.widget.Toast;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class activity_TagMemory extends TabActivity {

	private static final String[] m = {"EPC", "User", "RFU", "TID"};
	private Spinner spinner;  
	private Spinner spinner2;
	private Spinner spinner3;
	private ArrayAdapter<String> adapter;  
	
	private static final String[] sMask = {"EPC", "User", "Access Password", "Kill Password"};
	private static final String[] sAction = {"Unlock", "Lock", "PerLock"};
	
	private ArrayAdapter<String> adapterMask; 
	private ArrayAdapter<String> adapterAction; 
	
	public static String sEPC;
	//
	private TextView txtSelTag;
	private EditText edtAddress;
	private EditText edtData1;
	private Button btnKill;
	private EditText edtLength;
	private Button btnLock;
	private EditText edtPassword1;
	private EditText edtPassword3;
	private Button btnReadMem;
	private TextView txtResult;
	private Button btnWriteMem;
	//
	byte[] data = new byte[256];
	byte count = 0;
	//
	private ProgressDialogEx pd;
	private String cMsg;
	private boolean bSuccess;
	private MHandler handler = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tagmemory);
		//
		handler = new MHandler(this);
		//
		TabHost tabs = getTabHost();  
        //����Tab1  
        TabSpec tab1 = tabs.newTabSpec("tab1");  
        tab1.setIndicator("Read&Write");      // ����tab1�����  
        tab1.setContent(R.id.tab_read);    // �����ؼ�  
        tabs.addTab(tab1);                // ���tab1   
          
        //����Tab2  
        TabSpec tab2 = tabs.newTabSpec("tab2");  
        tab2.setIndicator("Lock");        
        tab2.setContent(R.id.tab_lock);      
        tabs.addTab(tab2);                  

        //����Tab3  
        TabSpec tab3 = tabs.newTabSpec("tab3");  
        tab3.setIndicator("Kill");        
        tab3.setContent(R.id.tab_kill);      
        tabs.addTab(tab3); 
          
        tabs.setCurrentTab(0);  
        //
        spinner = (Spinner) findViewById(R.id.target_memory); 
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, m);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
        spinner.setAdapter(adapter);
        spinner.setVisibility(View.VISIBLE);
        //
        spinner2 = (Spinner) findViewById(R.id.spinner1); 
        adapterMask = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sMask);
        adapterMask.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
        spinner2.setAdapter(adapterMask);
        spinner2.setVisibility(View.VISIBLE);
        //
        spinner3 = (Spinner) findViewById(R.id.spinner2); 
        adapterAction = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, sAction);
        adapterAction.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
        spinner3.setAdapter(adapterAction);
        spinner3.setVisibility(View.VISIBLE);
        //
        txtSelTag = (TextView) findViewById(R.id.selected_tag);
        txtSelTag.setText(sEPC);
        //
        txtResult = ((TextView)findViewById(R.id.result));
        txtResult.setText("");
        edtPassword1 = ((EditText)findViewById(R.id.password1));
        edtPassword3 = ((EditText)findViewById(R.id.password3));
        edtAddress = ((EditText)findViewById(R.id.address));
        edtLength = ((EditText)findViewById(R.id.length));
        edtData1 = ((EditText)findViewById(R.id.data1));
        edtPassword1.setText("00000000");
        edtPassword3.setText("00000000");
        edtAddress.setText("0");
        edtLength.setText("1");
        edtData1.setText("");
        
        btnReadMem = ((Button)findViewById(R.id.read_tag));
        btnReadMem.setOnClickListener(new btnReadMem_Click());
        btnWriteMem = ((Button)findViewById(R.id.write_tag));
        btnWriteMem.setOnClickListener(new btnWriteMem_Click());
        btnKill = ((Button)findViewById(R.id.kill_tag));
        btnKill.setOnClickListener(new btnKill_Click());
        btnLock = ((Button)findViewById(R.id.lock_tag));
        btnLock.setOnClickListener(new btnLock_Click());
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
        WeakReference<activity_TagMemory> outerClass;  
      
        MHandler(activity_TagMemory activity) {  
            outerClass = new WeakReference<activity_TagMemory>(activity);  
        }  
      
        @Override  
        public void handleMessage(android.os.Message msg) {  
        	activity_TagMemory theClass = outerClass.get();  
            switch (msg.what) {
	    		case 0:
	    			theClass.pd.dismiss(); // �ر�ProgressDialog
	    			if (!theClass.bSuccess && theClass.cMsg != null)
    				{
    					theClass.showToast(theClass.cMsg);
    					theClass.txtResult.setText(theClass.cMsg);
    				}
    				else if (theClass.bSuccess) {
    					theClass.edtData1.setText(theClass.convertByteArrayToString(theClass.data, 0, theClass.count * 2));
    					theClass.txtResult.setText("Success.");
    				}
	    			break;

	    		case 1:
	    			theClass.pd.dismiss(); // �ر�ProgressDialog
    				if (!theClass.bSuccess && theClass.cMsg != null)
    				{
    					theClass.showToast(theClass.cMsg);
    					theClass.txtResult.setText(theClass.cMsg);
    				}
    				else if (theClass.bSuccess) {
    					theClass.txtResult.setText("Success.");
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
    private class btnReadMem_Click implements android.view.View.OnClickListener
    {
		public void onClick(View v) 
		{ 
			final byte[] accesspsw = convertStringToByteArray(edtPassword1.getText().toString());
			final byte block = (byte)spinner.getSelectedItemPosition(); 
			final byte address = Byte.parseByte(edtAddress.getText().toString()); 
			count = Byte.parseByte(edtLength.getText().toString());
			pd = ProgressDialogEx.show(activity_TagMemory.this, "Reading tag...");
			new Thread(){  
                @Override  
                public void run() {  
                	int ret = 0;
                    try {
                    	cMsg = "Device communication error, make sure that is plugged."; 
                    	bSuccess = false;
                    	//ѡ���ǩ
                    	byte[] epcData = convertStringToByteArray(sEPC);
                    	ret = Demo.reader.selectTag(accesspsw, epcData);
                    	if (ret == 0)
                    	{
                    		ret = Demo.reader.readTag(block, address, count, data);                    		
                    	}
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
    
    //д�갴ť�¼�
    private class btnWriteMem_Click implements android.view.View.OnClickListener
    {
		public void onClick(View v) 
		{ 
			final byte[] accesspsw = convertStringToByteArray(edtPassword1.getText().toString());
			final byte block = (byte)spinner.getSelectedItemPosition(); 
			final byte address = Byte.parseByte(edtAddress.getText().toString()); 
			count = Byte.parseByte(edtLength.getText().toString());
			final byte[] data_w = convertStringToByteArray(edtData1.getText().toString());
			pd = ProgressDialogEx.show(activity_TagMemory.this, "Write tag...");
			new Thread(){  
                @Override  
                public void run() {  
                	int ret = 0;
                    try {
                    	cMsg = "Device communication error, make sure that is plugged."; 
                    	bSuccess = false;
                    	//ѡ���ǩ
                    	byte[] epcData = convertStringToByteArray(sEPC);
                    	ret = Demo.reader.selectTag(accesspsw, epcData);
                    	if (ret == 0)
                    	{
                    		ret = Demo.reader.writeTag(block, address, count, data_w);
                    	}
						if (ret == 0) {
							bSuccess = true;
						}
						else if (ret > 2) {
							cMsg = "Write tag failure.";
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
                    handler.sendEmptyMessage(1);
            }}.start();
		}
    }
    
    //��ٱ�ǩ��ť�¼�
    private class btnKill_Click implements android.view.View.OnClickListener
    {
		public void onClick(View v) 
		{ 
			final byte[] psw = convertStringToByteArray(edtPassword3.getText().toString());
			final byte[] accesspsw = convertStringToByteArray(edtPassword1.getText().toString());
			pd = ProgressDialogEx.show(activity_TagMemory.this, "Kill tag...");
			new Thread(){  
                @Override  
                public void run() {  
                	int ret = 0;
                    try {
                    	cMsg = "Device communication error, make sure that is plugged."; 
                    	bSuccess = false;
                    	//ѡ���ǩ
                    	byte[] epcData = convertStringToByteArray(sEPC);
                    	ret = Demo.reader.selectTag(accesspsw, epcData);
                    	if (ret == 0)
                    	{
                    		ret = Demo.reader.killTag(psw);
                    	}
						if (ret == 0) {
							bSuccess = true;
						}
						else if (ret > 2) {
							cMsg = "Kill tag failure.";
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
                    handler.sendEmptyMessage(1);
            }}.start();
		}
    }
    
    //���ǩ��ť�¼�
    private class btnLock_Click implements android.view.View.OnClickListener
    {
		public void onClick(View v) 
		{ 
			final byte[] accesspsw = convertStringToByteArray(edtPassword1.getText().toString());
			final byte lockObject = (byte)spinner2.getSelectedItemPosition();
			final byte lockAction = (byte)spinner3.getSelectedItemPosition();
			pd = ProgressDialogEx.show(activity_TagMemory.this, "Lock tag...");
			new Thread(){  
                @Override  
                public void run() {  
                	int ret = 0;
                    try {
                    	cMsg = "Device communication error, make sure that is plugged."; 
                    	bSuccess = false;
                    	//ѡ���ǩ
                    	byte[] epcData = convertStringToByteArray(sEPC);
                    	ret = Demo.reader.selectTag(accesspsw, epcData);
                    	if (ret == 0)
                    	{
                    		ret = Demo.reader.lockTag(accesspsw, lockObject, lockAction);
                    	}
						if (ret == 0) {
							bSuccess = true;
						}
						else if (ret > 2) {
							cMsg = "Lock tag failure.";
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
                    handler.sendEmptyMessage(1);
            }}.start();
		}
    }
}
