package com.art.tech.board;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import rfid.ivrjacku1.IvrJackAdapter;
import rfid.ivrjacku1.IvrJackService;
import rfid.ivrjacku1.IvrJackStatus;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.art.tech.R;
import com.art.tech.R.id;
import com.art.tech.R.layout;
import com.art.tech.db.ProductInfoColumn;
import com.art.tech.model.ProductInfo;

public class ScanActivity extends Activity implements IvrJackAdapter {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scan_main);
		
		imgPlugout = (ImageView) this.findViewById(R.id.imgPlugout);
		
		no_scanner_container = (LinearLayout) this.findViewById(R.id.id_no_scanner_container);
		scanner_result_container = (LinearLayout) this.findViewById(R.id.id_scanner_result_container);
		
		txtRealCode  = (TextView)this.findViewById(R.id.id_scan_real_code);
		txtStatus = (TextView)this.findViewById(R.id.id_no_scanner_detected);
		txtConnect = (TextView)this.findViewById(R.id.id_connect_scanner);
		editProductName = (EditText)this.findViewById(R.id.id_scan_edit_product_name);
		btnSubmit =  (Button) this.findViewById(R.id.id_scan_submit_button);
		btnSubmit.setOnClickListener(new btnSubmitClickListener());
		
		btnScan =  (Button) this.findViewById(R.id.id_scan_button);
		btnScan.setOnClickListener(new btnScanClickListener());
		
		seqAdapter = new CustomListAdapter(this, R.layout.customlistview, this.seqArray);
		handler = new MHandler(this);
    	
		reader = new IvrJackService();
		reader.open(this, this);
		

		String realCode = "123456789abcd";
		String name = "TAB";
		handleRealCode(realCode, name);
	}
	
	public interface OnSubmitProduct {
		void onSubmit();
	}
	
	private OnSubmitProduct onSubmitProductListener;
	public void setOnSubmitProductListener(OnSubmitProduct l) {
		onSubmitProductListener = l;
	}
	
	private static final int UPDATE_REQUIRED = 100;
	private static final int SET_TOTAL = 104;

    private static final int QUERYING = 1;
	private static final String TAG = ScanActivity.class.getSimpleName();
    
    
	private boolean bFirstLoad = true;
	
	
	private LinearLayout no_scanner_container;
	private LinearLayout scanner_result_container;
	private ImageView imgPlugout = null;
	
	private TextView txtStatus = null;
	private EditText editProductName = null;
	private TextView txtConnect = null;
	private TextView txtRealCode = null;
	private Button btnScan;
	
	private Button btnSubmit;

    private ProgressDialogEx pd; 
    private boolean bSuccess;
    private String cMsg;
    
    private boolean bCancel = false;
    private boolean bOpened = false; 
    private MHandler handler = null;
    //
    private CustomListAdapter seqAdapter;
    private ArrayList<seqTag> seqArray = new ArrayList<seqTag>();
    private ArrayList<String> tagArray = new ArrayList<String>();
    private boolean bUpdateRequired = false;
    
    public static IvrJackService reader = null;
    
    LayoutInflater mInflater;
	private Object status;

	private void setVisible(int no_scanner, int scanbutton, int scan_result) {		
		no_scanner_container.setVisibility(no_scanner);
		btnScan.setVisibility(scanbutton);
		scanner_result_container.setVisibility(scan_result);
	}
	
	
	private void ListClear() {
		seqAdapter.setSelectItem(-1);
		this.seqArray.clear();
		//this.txtTotal.setText(" 0");
		this.tagArray.clear();
		//this.epclist.setAdapter(this.seqAdapter);
		this.bUpdateRequired = false;
	}

	private void ListRefresh(String paramString) {
		String[] sEPC = paramString.split(";");
		for(String str: sEPC)
		{
			Log.d(TAG, "ListRefresh " + str);
			
			Message msg= Message.obtain();
			msg.what = SET_TOTAL;
			msg.obj = (Object) str;
			handler.sendMessageDelayed(msg, 1000L);
			
			
			if (this.tagArray.contains(str)) {
				int i = Integer.parseInt(((seqTag) this.seqArray.get(this.tagArray
						.indexOf(str))).getCount());
				((seqTag) this.seqArray.get(this.tagArray.indexOf(str)))
						.setCount(Integer.toString(i + 1));
				if (!this.bUpdateRequired) {
					handler.sendEmptyMessageDelayed(UPDATE_REQUIRED, 80L);
					Log.d(TAG, "sendEmptyMessageDelayed 1 ");
					this.bUpdateRequired = true;
				}
			} else {
				seqTag localseqTag = new seqTag();
				this.tagArray.add(str);
				localseqTag.setTag(str);
				localseqTag.setNum(Integer.toString(this.tagArray.size()));
				localseqTag.setCount("1");
				this.seqArray.add(localseqTag);
				if (!this.bUpdateRequired) {
					handler.sendEmptyMessageDelayed(UPDATE_REQUIRED, 80L);
					Log.d(TAG, "sendEmptyMessageDelayed 2 ");
					this.bUpdateRequired = true;
				}
			}
		}
		
		//handler.sendEmptyMessageDelayed(SET_TOTAL, 1000L);

	}
	
	private void showToast(String msg) {
		showToast(msg, R.drawable.icon_info, true);
	}
	
	private void showToast(String msg, int resID, boolean bError) {
	    View toastRoot = this.getLayoutInflater().inflate(R.layout.toast, null);   
	    Toast toast = new Toast(this);  
	    toast.setGravity(Gravity.CENTER, 0, 0);
	    //if (bError)
	    //	toast.setDuration(Toast.LENGTH_LONG);
	    //else
	    	toast.setDuration(Toast.LENGTH_SHORT);
	    toast.setView(toastRoot);   
	    TextView tv = (TextView) toastRoot.findViewById(R.id.toastbox_message);   
	    tv.setText(msg);  
	    if (resID > 0) {
	    	ImageView iv = (ImageView)toastRoot.findViewById(R.id.toastbox_icon);
	    	iv.setImageResource(resID);
	    }
	    toast.show();
	}
	
	private void showToast(String msg, int resID) {
	    showToast(msg, resID, false);
	}
	
	/** 
     * 用Handler来更新UI 
    */  
    class MHandler extends Handler {  
        WeakReference<ScanActivity> outerClass;  
      
        MHandler(ScanActivity activity) {  
            outerClass = new WeakReference<ScanActivity>(activity);  
        }  
      
        @Override  
        public void handleMessage(android.os.Message msg) {  
        	ScanActivity theClass = outerClass.get();  
            switch (msg.what) {
	            case QUERYING:
	            	/*
	    			theClass.pd.dismiss(); // 关闭ProgressDialog
	    			theClass.btnQuery.setEnabled(true);
	    			if (theClass.bCancel) break;
	    			if (theClass.bSuccess) {
	    				theClass.bOpened = !theClass.bOpened;
	    				if (!theClass.bOpened) 
	    					theClass.btnQuery.setText(">>>>Start<<<<");
	    				else
	    					theClass.btnQuery.setText(">>>>Stop<<<<");
	    			} else {
	    				if (theClass.cMsg != null)
	    					theClass.showToast(theClass.cMsg);
	    			}
	    			break;
	    			*/
    				if (cMsg != null)
    					showToast(cMsg);
    				break;
				case UPDATE_REQUIRED:
					//theClass.seqAdapter.notifyDataSetChanged();
					//theClass.epclist.setSelection(theClass.epclist.getAdapter().getCount() - 1);
					bUpdateRequired = false;
					break;
				     
				case SET_TOTAL:
					//theClass.txtTotal.setText("Total:" + theClass.tagArray.size());
					setVisible(View.GONE, View.GONE, View.VISIBLE);
					bUpdateRequired = false;
					break;
    		}
        }  
    }
    
    private class btnSubmitClickListener implements OnClickListener {
		public void onClick(View v) {
			String realcode = txtRealCode.getText().toString();
			String name = editProductName.getText().toString().trim();
			
			if (name == null || name.isEmpty()) {
				new AlertDialog.Builder(ScanActivity.this)
		        .setTitle(R.string.no_name_message)
		        .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int whichButton) {

		            }
		        }).create().show();
			}
			
			handleRealCode(realcode, name);
			
			reader.readEPC(false);
			
			if (onSubmitProductListener != null) {
				onSubmitProductListener.onSubmit();
			}
			
			// reset to original status
			if (status == IvrJackStatus.ijsRecognized)
				setVisible(View.GONE, View.VISIBLE, View.GONE);
			else
				setVisible(View.VISIBLE, View.GONE, View.GONE);
			
		}
    }
    
	@Override
	public void onDestroy() {
		super.onDestroy();
		reader.readEPC(false);
    	reader.stopReadEPC();
    	reader.close();
    	reader = null;
    }
    

	private class btnScanClickListener implements
			android.view.View.OnClickListener {
		
		public void onClick(View v) {
			v.setVisibility(View.INVISIBLE);
			
			new Thread() {
				@Override
				public void run() {
					int ret = 0;
					try {
						cMsg = "Device communication error, make sure that is plugged.";
						bSuccess = false;
						ret = reader.readEPC(true);
						//ret = reader.readEPC(!bOpened);
						if (ret == 0 && !bCancel) {
							bSuccess = true;
						} else if (ret == -1) {
							cMsg = "Device is running low battery, please charge!";
						}
					} catch (Exception e) {
						cMsg = "Unknown error.";
						bSuccess = false;
					} finally {

					}
					handler.sendEmptyMessage(QUERYING);
				}
			}.start();
		}
	}
      
	//查询按钮事件
    private class btnQuery_Click implements android.view.View.OnClickListener
    {
		public void onClick(View v) 
		{
			//btnQuery.setEnabled(false);
			if (!bOpened)
				pd = ProgressDialogEx.show(ScanActivity.this, "Start read epc");
			else
				pd = ProgressDialogEx.show(ScanActivity.this, "Stop read epc");
            new Thread(){  
                @Override  
                public void run() {  
                	int ret = 0;
                    try {
                    	cMsg = "Device communication error, make sure that is plugged."; 
                    	bSuccess = false;
						ret = reader.readEPC(!bOpened);
						if (ret == 0 && !bCancel) {
							bSuccess = true;
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
                    handler.sendEmptyMessage(QUERYING);
                }}.start();

		}	  
    }

    private class epclistItemClick implements OnItemClickListener 
    {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			if (bOpened) {
				showToast("Please stop the inventory tag action.");
				return;
			}
			seqAdapter.setSelectItem(arg2);  
			seqAdapter.notifyDataSetInvalidated(); 
			//activity_TagMemory.sEPC = ((seqTag) seqArray.get(arg2)).getTag();
			Intent intent1 = new Intent();
	    	//intent1.setClass(ScanActivity.this.getActivity(), activity_TagMemory.class);
	    	startActivity(intent1);
		}
    }

//    TODO
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//    	if (keyCode == KeyEvent.KEYCODE_BACK) {
//    		if (bOpened) {
//    			reader.stopReadEPC();
//    		}
//	   		finish();
//	   		//System.exit(0);
// 		   	return true;
// 	   	}else{       
// 	   		return super.onKeyDown(keyCode, event);
// 	   	} 
//    }

	@Override
	public void onConnect(String arg0) {
		Log.d(TAG, "onConnect");
		setVisible(View.GONE, View.VISIBLE, View.GONE);
		showToast("Recognized.", R.drawable.toastbox_auth_success);
	}

	@Override
	public void onDisconnect() {
		Log.d(TAG, "onDisconnect");
		setVisible(View.VISIBLE, View.GONE, View.GONE);
		
		bOpened = false;
		if (!bFirstLoad) {
			showToast("Plugout!", R.drawable.toastbox_remove);
		}
		bFirstLoad = false;
		bCancel = false;
		
		//reader.readEPC(false);
	}

	@Override
	public void onInventory(String arg0) {
		
		ListRefresh(arg0);
		txtRealCode.setText(arg0);
	}
	
	
	private void handleRealCode(String realcode, String name) {
		Resources res = getResources();
		String other = res.getString(R.string.other);
		
		ProductInfo info = new ProductInfo();
		info.real_code = realcode;
		info.copy_name = name;
		info.copy_size_chang = 0;
		info.copy_size_gao = 0;
		info.copy_size_kuan = 0;
		info.copy_type =  other;
		info.copy_material = other;
		info.copy_date = System.currentTimeMillis();
		
		String where = ProductInfoColumn.REAL_CODE + "=" + "'" + realcode + "'";
		ProductInfoColumn.delete(this, ProductInfoColumn.TABLE_NAME, where);
		
		if (ProductInfoColumn.insert(this, info) > 0) {
			Log.v(TAG, "insert a new product success");
		} else {
			Log.e(TAG, "insert a new product failed");
		}
	}

	@Override
	public void onStatusChange(IvrJackStatus arg0) {
		switch (arg0) {
			case ijsPlugout:
				status = IvrJackStatus.ijsPlugout;
				break;
			case ijsDetecting: 
				status = IvrJackStatus.ijsDetecting;
				//pd = ProgressDialogEx.show(ScanActivity.this.getActivity(), "Detecting...");
				//setVisible(View.VISIBLE, View.GONE, View.GONE);
				break;
				
			case ijsRecognized:
				//pd.dismiss();		
				status = IvrJackStatus.ijsRecognized;
				
				//setVisible(View.GONE, View.VISIBLE, View.GONE);
				
				showToast("Recognized.", R.drawable.toastbox_auth_success);
				
				break;
				
			case ijsUnRecognized:
				status = IvrJackStatus.ijsUnRecognized;
				//pd.dismiss();
				//Toast.makeText(this.getActivity(), "Unrecognized!", Toast.LENGTH_SHORT).show();
				
				break;
		}
	}
	

}
