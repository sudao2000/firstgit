package com.art.tech.board;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import rfid.ivrjacku1.IvrJackAdapter;
import rfid.ivrjacku1.IvrJackService;
import rfid.ivrjacku1.IvrJackStatus;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.art.tech.R;
import com.art.tech.db.ProductInfoColumn;
import com.art.tech.model.ProductInfo;

public class ScanUIFragment extends Fragment  implements IvrJackAdapter {
	
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
	private static final String TAG = ScanUIFragment.class.getSimpleName();
    
    
	private boolean bFirstLoad = true;
	private ImageView imgPlugout = null;
	
	private TextView txtStatus = null;
	private EditText editProductName = null;
	private TextView txtConnect = null;
	private TextView txtRealCode = null;
	private Button btnScan;
	
	private Button btnSubmit;
//	
//	private TextView txtTotal = null;
//	//private TextView txtDate = null;
//	//
//	private TextView lblEPC = null;
//	private TextView lblTimes = null;
//	private Button btnQuery = null;
//	//
//	private Button btnSetting = null;
//	private Button clearScreen;
//	private ListView epclist;
//
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
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mInflater = inflater;
		
		View rootView = inflater.inflate(R.layout.scan_main, container,
				false);
		
		
		imgPlugout = (ImageView) rootView.findViewById(R.id.imgPlugout);
		
//		
//		btnQuery = (Button)rootView.findViewById(R.id.btnQuery);
//		btnSetting = (Button)rootView.findViewById(R.id.btnSetting);
//		btnSetting.setVisibility(View.GONE);
//		btnSetting.setOnClickListener(new View.OnClickListener()
//	    {
//			public void onClick(View paramView)
//			{
//				if (bOpened) {
//					showToast("Please stop the inventory tag action.");
//					return;
//				}
//				Intent intent1 = new Intent();
//		    	intent1.setClass(ScanUIFragment.this.getActivity(), activity_Setting.class);
//		    	startActivity(intent1);
//			}
//	    });
//		lblEPC = (TextView)rootView.findViewById(R.id.textView1);
//		lblTimes = (TextView)rootView.findViewById(R.id.textView11);
//		txtTotal = (TextView)rootView.findViewById(R.id.txtTotal);
		txtRealCode  = (TextView)rootView.findViewById(R.id.id_scan_real_code);
		txtStatus = (TextView)rootView.findViewById(R.id.id_no_scanner_detected);
		txtConnect = (TextView)rootView.findViewById(R.id.id_connect_scanner);
		editProductName = (EditText)rootView.findViewById(R.id.id_scan_edit_product_name);
		btnSubmit =  (Button) rootView.findViewById(R.id.id_scan_submit_button);
		btnSubmit.setOnClickListener(new btnSubmitClickListener());
		
		btnScan =  (Button) rootView.findViewById(R.id.id_scan_button);
		btnScan.setOnClickListener(new btnScanClickListener());
		
//		//txtDate = (TextView)rootView.findViewById(R.id.txtDate);
//		clearScreen = (Button) rootView.findViewById(R.id.btnClear);
//		clearScreen.setOnClickListener(new View.OnClickListener()
//	    {
//	      public void onClick(View paramView)
//	      {
//	    	  ListClear();
//	      }
//	    });
//		
//		//
//		epclist = ((ListView)rootView.findViewById(R.id.tag_list));
//		epclist.setCacheColorHint(Color.TRANSPARENT); 
//		epclist.setOnItemClickListener(new epclistItemClick());
//	    seqAdapter = new CustomListAdapter(this.getActivity(), R.layout.customlistview, this.seqArray);
//	    epclist.setAdapter(this.seqAdapter);
//		//
//		btnQuery.setOnClickListener(new btnQuery_Click());
//		btnQuery.setVisibility(View.GONE);
		seqAdapter = new CustomListAdapter(this.getActivity(), R.layout.customlistview, this.seqArray);
		handler = new MHandler(this);
    	//
		reader = new IvrJackService();
		reader.open(getActivity(), this);
		
		return rootView;
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
	
//	@Override 
//	public void onStart() {
//		super.onStart();  
//		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd  EEEE", Locale.ENGLISH);     
//    	Date curDate = new Date(System.currentTimeMillis());//获取当前时间   
//		txtDate.setText(formatter.format(curDate));
//	}
	
	@Override  
    public void onDestroy() { 
		if (reader != null) {
			reader.close();
			Log.i("HEX", "reader close");
		}
        super.onDestroy();  
    }  
	
	private void showToast(String msg) {
		showToast(msg, R.drawable.icon_info, true);
	}
	
	private void showToast(String msg, int resID, boolean bError) {
	    View toastRoot = mInflater.inflate(R.layout.toast, null);   
	    Toast toast = new Toast(this.getActivity());  
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
        WeakReference<ScanUIFragment> outerClass;  
      
        MHandler(ScanUIFragment activity) {  
            outerClass = new WeakReference<ScanUIFragment>(activity);  
        }  
      
        @Override  
        public void handleMessage(android.os.Message msg) {  
        	ScanUIFragment theClass = outerClass.get();  
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
				case UPDATE_REQUIRED:
					//theClass.seqAdapter.notifyDataSetChanged();
					//theClass.epclist.setSelection(theClass.epclist.getAdapter().getCount() - 1);
					theClass.bUpdateRequired = false;
					break;
				     
				case SET_TOTAL:
					//theClass.txtTotal.setText("Total:" + theClass.tagArray.size());
					String realCode = (String) msg.obj;
					
					txtRealCode.setText(realCode);
			    	txtRealCode.setVisibility(View.VISIBLE);
			    	editProductName.setVisibility(View.VISIBLE);
			    	btnSubmit.setVisibility(View.VISIBLE);
			    	
					btnScan.setVisibility(View.INVISIBLE);		
					txtStatus.setVisibility(View.INVISIBLE);
					txtConnect.setVisibility(View.INVISIBLE);				
					imgPlugout.setVisibility(View.INVISIBLE);	
					
					theClass.bUpdateRequired = false;
					break;
    		}
        }  
    }
    
    private class btnSubmitClickListener implements OnClickListener {
		public void onClick(View v) {
			String realcode = txtRealCode.getText().toString();
			String name = editProductName.getText().toString().trim();
			
			if (name == null || name.isEmpty()) {
				new AlertDialog.Builder(getActivity())
		        .setTitle(R.string.no_name_message)
		        .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int whichButton) {

		            }
		        }).create().show();
			}
			
			handleRealCode(realcode, name);
			
			if (onSubmitProductListener != null) {
				onSubmitProductListener.onSubmit();
			}
			
			// reset to original status
			if (status == IvrJackStatus.ijsRecognized)
				onConnect(null);
			else
				onDisconnect();
			reader.stopReadEPC();
		}
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
						ret = reader.readEPC(!bOpened);
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
				pd = ProgressDialogEx.show(ScanUIFragment.this.getActivity(), "Start read epc");
			else
				pd = ProgressDialogEx.show(ScanUIFragment.this.getActivity(), "Stop read epc");
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
	    	//intent1.setClass(ScanUIFragment.this.getActivity(), activity_TagMemory.class);
	    	startActivity(intent1);
		}
    }
    
    public void onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
    		if (bOpened) {
    			reader.stopReadEPC();
    		}
	   		//finish();
	   		//System.exit(0);
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
		Resources res = getActivity().getResources();
		
		
//		btnQuery.setVisibility(View.VISIBLE);					
//		btnSetting.setVisibility(View.VISIBLE);
//		txtTotal.setVisibility(View.VISIBLE);
//		lblEPC.setVisibility(View.VISIBLE);
//		lblTimes.setVisibility(View.VISIBLE);
//		clearScreen.setVisibility(View.VISIBLE);
//		epclist.setVisibility(View.VISIBLE);
		
		//txtRealCode.setVisibility(View.VISIBLE);
		//txtRealCode.setText("Reading...........");
		
		btnScan.setVisibility(View.VISIBLE);
		
		txtStatus.setVisibility(View.INVISIBLE);
		txtConnect.setVisibility(View.INVISIBLE);
		
		editProductName.setVisibility(View.INVISIBLE);
		btnSubmit.setVisibility(View.INVISIBLE);
		imgPlugout.setVisibility(View.INVISIBLE);
		
		showToast("Recognized.", R.drawable.toastbox_auth_success);
	}

	@Override
	public void onDisconnect() {
		Resources res = getActivity().getResources();
		imgPlugout.setVisibility(View.VISIBLE);
		txtRealCode.setVisibility(View.INVISIBLE);
		btnScan.setVisibility(View.INVISIBLE);
//		btnQuery.setVisibility(View.INVISIBLE);
//		btnSetting.setVisibility(View.INVISIBLE);
//		lblEPC.setVisibility(View.INVISIBLE);
//		lblTimes.setVisibility(View.INVISIBLE);
//		epclist.setVisibility(View.INVISIBLE);
//		clearScreen.setVisibility(View.INVISIBLE);
//		txtTotal.setVisibility(View.INVISIBLE);		
		
		
		txtStatus.setText(res.getString(R.string.no_scanner_detected));
		txtConnect.setText(res.getString(R.string.connect_scanner));
		editProductName.setVisibility(View.INVISIBLE);
		btnSubmit.setVisibility(View.INVISIBLE);
		//btnQuery.setText(">>>>Start<<<<");
		bOpened = false;
		if (!bFirstLoad) {
			showToast("Plugout!", R.drawable.toastbox_remove);
		}
		bFirstLoad = false;
		bCancel = false;
	}

	@Override
	public void onInventory(String arg0) {
		ListRefresh(arg0);
		txtRealCode.setText(arg0);
	}
	
	
	private void handleRealCode(String realcode, String name) {
		Resources res = getActivity().getResources();
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
		ProductInfoColumn.delete(getActivity(), ProductInfoColumn.TABLE_NAME, where);
		
		if (ProductInfoColumn.insert(getActivity(), info) > 0) {
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
			onDisconnect();
			break;
			case ijsDetecting: 
				status = IvrJackStatus.ijsDetecting;
				//pd = ProgressDialogEx.show(ScanUIFragment.this.getActivity(), "Detecting...");
				break;
				
			case ijsRecognized:
				//pd.dismiss();		
				status = IvrJackStatus.ijsRecognized;
				
				Resources res = getActivity().getResources();
				
				imgPlugout.setVisibility(View.INVISIBLE);
				
				txtRealCode.setVisibility(View.VISIBLE);
				txtRealCode.setText(res.getString(R.string.please_press_scan));
				btnScan.setVisibility(View.VISIBLE);
				editProductName.setVisibility(View.INVISIBLE);
				btnSubmit.setVisibility(View.INVISIBLE);
				txtStatus.setVisibility(View.INVISIBLE);
				txtConnect.setVisibility(View.INVISIBLE);
				
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

