/*
 * (C) Copyright 2015 by fr3ts0n <erwin.scheuch-heilig@gmx.at>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA 02111-1307 USA
 */

package com.fr3ts0n.ecu.gui.androbd;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.SearchManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fr3ts0n.ecu.EcuCodeItem;
import com.fr3ts0n.ecu.EcuConversions;
import com.fr3ts0n.ecu.EcuDataItem;
import com.fr3ts0n.ecu.EcuDataItems;
import com.fr3ts0n.ecu.EcuDataPv;
import com.fr3ts0n.ecu.prot.ElmProt;
import com.fr3ts0n.ecu.prot.ObdProt;
import com.fr3ts0n.pvs.PvChangeEvent;
import com.fr3ts0n.pvs.PvChangeListener;
import com.fr3ts0n.pvs.PvList;

import org.apache.log4j.Level;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import de.mindpipe.android.logging.log4j.LogConfigurator;

/**
 * Main Activity for AndrOBD app
 */
public class MainActivity extends ListActivity
	implements PvChangeListener, AdapterView.OnItemLongClickListener
{
	/** Key names received from the BluetoothChatService Handler */
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";
	public static final String MEASURE_SYSTEM = "measure_system";

	/** Message types sent from the BluetoothChatService Handler */
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;
	public static final int MESSAGE_DATA_ITEMS_CHANGED = 6;
	public static final int MESSAGE_UPDATE_VIEW = 7;
	private static final String TAG = "AndrOBD";
	/** internal Intent request codes */
	private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
	private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
	private static final int REQUEST_ENABLE_BT = 3;
	private static final int REQUEST_SELECT_FILE = 4;
	private static final int REQUEST_SETTINGS = 5;
	
	/**
	 * app exit parameters
	 */
	private static final int EXIT_TIMEOUT = 2500;
	/** time between display updates to represent data changes */
	private static final int DISPLAY_UPDATE_TIME = 300;
	/** Member object for the BT comm services */
	private static ObdCommService mCommService = null;
	/** Local Bluetooth adapter */
	private static BluetoothAdapter mBluetoothAdapter = null;

	/** Name of the connected BT device */
	private static String mConnectedDeviceName = null;
	/** log4j configurator */
	private static LogConfigurator logCfg;
	private static Menu menu;

	/** Data list adapters */
	private static ObdItemAdapter mPidAdapter;
	private static VidItemAdapter mVidAdapter;
	private static DfcItemAdapter mDfcAdapter;
	private static ObdItemAdapter currDataAdapter;
	/** Timer for display updates */
	private static Timer updateTimer = new Timer();
	/* is demo mode enabled? */
	private static boolean demoMode = false;
	/* initial state of bluetooth adapter */
	private static boolean initialBtStateEnabled = false;
	/** last time of back key pressed */
	private static long lastBackPressTime = 0;
	/** toast for showing exit message */
	private static Toast exitToast = null;
	private static FileHelper fileHelper;
	// app preferences ...
	SharedPreferences prefs;

	/**
	 * Timer Task to cyclically update data screen
	 */
	private transient final TimerTask updateTask = new TimerTask()
	{
		@Override
		public void run()
		{
			/* forward message to update the view */
			Message msg = mHandler.obtainMessage(MainActivity.MESSAGE_UPDATE_VIEW);
			mHandler.sendMessage(msg);
		}
	};

	/**
	 * Handle message requests
	 */
	private transient final Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{

			switch (msg.what)
			{
				case MESSAGE_STATE_CHANGE:
					switch (msg.arg1)
					{
						case ObdCommService.STATE_CONNECTED:
							onConnect();
							break;

						case ObdCommService.STATE_CONNECTING:
							setStatus(R.string.title_connecting);
							break;

						case ObdCommService.STATE_LISTEN:
						case ObdCommService.STATE_NONE:
							onDisconnect();
							break;
					}
					break;
				case MESSAGE_WRITE:
					break;

				case MESSAGE_READ:
					break;

				case MESSAGE_DEVICE_NAME:
					// save the connected device's name
					mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
					Toast.makeText(getApplicationContext(),
						getString(R.string.connected_to) + mConnectedDeviceName,
						Toast.LENGTH_SHORT).show();
					break;

				case MESSAGE_TOAST:
					Toast.makeText(getApplicationContext(),
						msg.getData().getString(TOAST),
						Toast.LENGTH_SHORT).show();
					break;

				case MESSAGE_DATA_ITEMS_CHANGED:
					PvChangeEvent event = (PvChangeEvent) msg.obj;
					switch (event.getType())
					{
						case PvChangeEvent.PV_ADDED:
							currDataAdapter.setPvList(currDataAdapter.pvs);
							try
							{
								updateTimer.schedule(updateTask, 0, DISPLAY_UPDATE_TIME);
							} catch (Exception ignored)
							{
							}
							break;

						case PvChangeEvent.PV_CLEARED:
							currDataAdapter.clear();
							break;
					}
					break;

				case MESSAGE_UPDATE_VIEW:
					getListView().invalidateViews();
					break;

			}
		}
	};

	/**
	 * Set fixed PIDs for protocol to specified list of PIDs
	 * @param pidNumbers List of PIDs
	 */
	public static void setFixedPids(HashSet<Integer> pidNumbers)
	{
		int[] pids = new int[pidNumbers.size()];
		int i=0;
		for(Integer pidNum : pidNumbers) pids[i++] = pidNum;
		Arrays.sort(pids);
		// set protocol fixed PIDs
		ObdProt.setFixedPid(pids);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
			WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// get preferences
		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		// keep main display on?
		if(prefs.getBoolean("keep_screen_on", false))
		{
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}

		// ... measurement system
		setConversionSystem(Integer.valueOf(
				prefs.getString(MEASURE_SYSTEM,
					String.valueOf(EcuDataItem.SYSTEM_METRIC))
			)
		);

		// set up action bar
		ActionBar actionBar = getActionBar();
		if (actionBar != null)
		{
			actionBar.setDisplayShowTitleEnabled(true);
			// actionBar.setDisplayOptions(ActionBar.NAVIGATION_MODE_TABS);
		}

		setContentView(R.layout.startup_layout);

		// set up log4j logging ...
		logCfg = new LogConfigurator();
		logCfg.setUseLogCatAppender(true);
		logCfg.setUseFileAppender(true);
		logCfg.setFileName(FileHelper.getPath(this).concat(File.separator).concat("log/AndrOBD.log"));
		setLogLevels();

		// Set up all data adapters
		mCommService = new ObdCommService(this, mHandler);
		mPidAdapter = new ObdItemAdapter(this, R.layout.obd_item, ObdProt.PidPvs);
		mVidAdapter = new VidItemAdapter(this, R.layout.obd_item, ObdProt.VidPvs);
		mDfcAdapter = new DfcItemAdapter(this, R.layout.obd_item, ObdProt.tCodes);
		currDataAdapter = mPidAdapter;
		// load csv files for protocol extensions
		loadPreferredExtensions();

		// create file helper instance
		fileHelper = new FileHelper(this, mCommService.elm);
		// set listeners for data structure changes
		setDataListeners();

		// Get local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		Log.d(TAG, "Adapter: " + mBluetoothAdapter);
		// If BT is not on, request that it be enabled.
		if (!demoMode && mBluetoothAdapter != null)
		{
			// remember initial bluetooth state
			initialBtStateEnabled = mBluetoothAdapter.isEnabled();
			if (!initialBtStateEnabled)
			{
				Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			}
		}
	}

	/**
	 * Setr logging levels from shared preferences
	 */
	private void setLogLevels()
	{
		logCfg.setRootLevel(Level.toLevel(prefs.getString("log_master", "INFO")));
		logCfg.configure();
	}

	/**
	 * set listeners for data structure changes
	 */
	private void setDataListeners()
	{
		// add pv change listeners to trigger model updates
		ObdProt.PidPvs.addPvChangeListener(this,
			PvChangeEvent.PV_ADDED
				| PvChangeEvent.PV_CLEARED
		);
		ObdProt.VidPvs.addPvChangeListener(this,
			PvChangeEvent.PV_ADDED
				| PvChangeEvent.PV_CLEARED
		);
		ObdProt.tCodes.addPvChangeListener(this,
			PvChangeEvent.PV_ADDED
				| PvChangeEvent.PV_CLEARED
		);
	}

	/**
	 * Handler for options menu creation event
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		MainActivity.menu = menu;
		// update menu item status for current conversion
		setConversionSystem(EcuDataItem.cnvSystem);
		return true;
	}

	/**
	 * get the Position in model of the selected items
	 *
	 * @return Array of selected item positions
	 */
	private int[] getSelectedPositions()
	{
		int selectedPositions[];
		// SparseBoolArray - what a garbage data type to return ...
		final SparseBooleanArray checkedItems = getListView().getCheckedItemPositions();
		// get number of items
		int checkedItemsCount = getListView().getCheckedItemCount();
		// dimension array
		selectedPositions = new int[checkedItemsCount];
		int j=0;
		// loop through findings
		for (int i = 0; i < checkedItems.size(); i++)
		{
			// Item position in adapter
			if(checkedItems.valueAt(i))
			{
				selectedPositions[j++] = checkedItems.keyAt(i);
			}
		}
		return selectedPositions;
	}

	/**
	 * Filter display items to just the selected ones
	 */
	private void setFiltered(boolean filtered)
	{
		if(filtered)
		{
			PvList filteredList = new PvList();
			HashSet<Integer> selPids = new HashSet<Integer>();
			for(int pos : getSelectedPositions())
			{
				EcuDataPv pv = (EcuDataPv)mPidAdapter.getItem(pos);
				selPids.add(pv.getAsInt(EcuDataPv.FID_PID));
				filteredList.put(pv.toString(), pv);
			}
			mPidAdapter.setPvList(filteredList);
			setFixedPids(selPids);
		}
		else
		{
			ObdProt.resetFixedPid();
			mPidAdapter.setPvList(ObdProt.PidPvs);
		}

		setMenuItemEnable(R.id.filter_selected, !filtered);
		setMenuItemEnable(R.id.unfilter_selected, filtered);
		setMenuItemEnable(R.id.chart_selected, !filtered);
		setMenuItemEnable(R.id.dashboard_selected, !filtered);
		setMenuItemEnable(R.id.hud_selected, !filtered);
	}

	/**
	 * Handler for Options menu selection
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle presses on the action bar items
		Intent serverIntent;
		updateTimer.purge();

		switch (item.getItemId())
		{
			case R.id.secure_connect_scan:
				// Launch the DeviceListActivity to see devices and do scan
				serverIntent = new Intent(this, DeviceListActivity.class);
				startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
				return true;

			case R.id.settings:
				// Launch the DeviceListActivity to see devices and do scan
				Intent settingsIntent = new Intent(this, SettingsActivity.class);
				startActivityForResult(settingsIntent, REQUEST_SETTINGS);
				return true;
				
			case R.id.chart_selected:
				/* if we are in OBD data mode:
				 * -> Short click on an item starts the readout activity
			     */
				if (mCommService.elm.getService() == ObdProt.OBD_SVC_DATA)
				{
					if (getListView().getCheckedItemCount() > 0)
					{
						ChartActivity.setAdapter(getListAdapter());
						Intent intent = new Intent(this, ChartActivity.class);
						intent.putExtra(ChartActivity.POSITIONS, getSelectedPositions());
						startActivity(intent);
					} else
					{
						setMenuItemEnable(R.id.chart_selected, false);
					}
				}
				return true;

			case R.id.hud_selected:
			case R.id.dashboard_selected:
				/* if we are in OBD data mode:
				 * -> Short click on an item starts the readout activity
			     */
				if (mCommService.elm.getService() == ObdProt.OBD_SVC_DATA)
				{
					if (getListView().getCheckedItemCount() > 0)
					{
						DashBoardActivity.setAdapter(getListAdapter());
						Intent intent = new Intent(this, DashBoardActivity.class);
						intent.putExtra(DashBoardActivity.POSITIONS, getSelectedPositions());
						intent.putExtra(DashBoardActivity.RES_ID, item.getItemId() == R.id.dashboard_selected ? R.layout.dashboard : R.layout.head_up);
						startActivity(intent);
					} else
					{
						setMenuItemEnable(R.id.graph_actions, false);
					}
				}
				return true;

			case R.id.filter_selected:
				setFiltered(true);
				break;

			case R.id.unfilter_selected:
				setFiltered(false);
				break;

			case R.id.save:
				// save recorded data (threaded)
				fileHelper.saveDataThreaded();
				return true;

			case R.id.load:
				selectFileToLoad();
				return true;

			case R.id.service_none:
				setObdService(ObdProt.OBD_SVC_NONE, item.getTitle());
				return true;

			case R.id.service_data:
				setObdService(ObdProt.OBD_SVC_DATA, item.getTitle());
				return true;

			case R.id.service_vid_data:
				setObdService(ObdProt.OBD_SVC_VEH_INFO, item.getTitle());
				return true;

			case R.id.service_freezeframes:
				setObdService(ObdProt.OBD_SVC_FREEZEFRAME, item.getTitle());
				return true;

			case R.id.service_codes:
				setObdService(ObdProt.OBD_SVC_READ_CODES, item.getTitle());
				return true;

			case R.id.service_permacodes:
				setObdService(ObdProt.OBD_SVC_PERMACODES, item.getTitle());
				return true;

			case R.id.service_pendingcodes:
				setObdService(ObdProt.OBD_SVC_PENDINGCODES, item.getTitle());
				return true;

			case R.id.service_clearcodes:
				clearObdFaultCodes();
				setObdService(ObdProt.OBD_SVC_READ_CODES, item.getTitle());
				return true;

			default:
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * set mesaurement conversion system to metric/imperial
	 * @param cnvId ID for metric/imperial conversion
	 */
	void setConversionSystem( int cnvId )
	{
		Log.i(TAG, "Conversion: " + getResources().getStringArray(R.array.measure_options)[cnvId]);
		if(EcuDataItem.cnvSystem != cnvId)
		{
			// set coversion system
			EcuDataItem.cnvSystem =  cnvId;
		}
	}

	/**
	 * Select file to be loaded
	 */
	public void selectFileToLoad()
	{
		stopDemoService();
		// set OBD data mode
		setObdService(ObdProt.OBD_SVC_DATA, getString(R.string.saved_data));

		File file = new File(FileHelper.getPath(this));
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		Uri data = Uri.fromFile(file);
		String type = "*/*";
		intent.setDataAndType(data, type);
		startActivityForResult(intent, REQUEST_SELECT_FILE);
	}

	/**
	 * Handler for result messages from other activities
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (requestCode)
		{
			case REQUEST_CONNECT_DEVICE_SECURE:
				// When DeviceListActivity returns with a device to connect
				if (resultCode == Activity.RESULT_OK)
					connectDevice(data, true);
				break;

			case REQUEST_CONNECT_DEVICE_INSECURE:
				// When DeviceListActivity returns with a device to connect
				if (resultCode == Activity.RESULT_OK)
					connectDevice(data, false);
				break;

			case REQUEST_ENABLE_BT:
				// When the request to enable Bluetooth returns
				if (resultCode == Activity.RESULT_OK)
				{
					// Launch the DeviceListActivity to see devices and do scan
					Intent serverIntent = new Intent(this, DeviceListActivity.class);
					startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
				} else
				{
					// Start demo service Thread
					startDemoService();
				}
				break;

			case REQUEST_SELECT_FILE:
				if (resultCode == RESULT_OK)
				{
					// Get the Uri of the selected file
					Uri uri = data.getData();
					Log.i(TAG, "Load content: " + uri);
					// load data ...
					try
					{
						InputStream inStr = getContentResolver().openInputStream(uri);
						fileHelper.loadData(inStr);
					} catch (FileNotFoundException e)
					{
						e.printStackTrace();
					}
					// don't allow saving it again
					setMenuItemEnable(R.id.save, false);
					// set listeners for data structure changes
					setDataListeners();
					// set adapters data source to loaded list instances
					mPidAdapter.setPvList(ObdProt.PidPvs);
					mVidAdapter.setPvList(ObdProt.VidPvs);
					mDfcAdapter.setPvList(ObdProt.tCodes);
					// set OBD data mode to the one selected by input file
					setObdService(mCommService.elm.getService(), getString(R.string.saved_data));
				}
				break;
				
			case REQUEST_SETTINGS:
			{
				// log levels
				setLogLevels();

				// ... measurement system
				setConversionSystem(
					Integer.valueOf(prefs.getString(MEASURE_SYSTEM,
							String.valueOf(EcuDataItem.SYSTEM_METRIC))
					)
				);

				// keep main display on?
				if(prefs.getBoolean("keep_screen_on", false))
				{
					getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				}

				// update from protocol extensions
				loadPreferredExtensions();
			}
			break;
		}
	}

	/**
	 * Load optional extension files which may have
	 * been defined in preferences
	 */
	public void loadPreferredExtensions()
	{
		String errors = "";

		// custom code list
		try
		{
			String filePath = prefs.getString(SettingsActivity.extKeys[2], null);
			if(filePath != null)
			{
				Log.i(TAG, "Load ext. codelist: "+filePath);
				InputStream inStr = getContentResolver().openInputStream(Uri.parse(filePath));
				EcuConversions.codeList.loadFromStream(inStr);
			}
		} catch (Exception e)
		{
			Log.e(TAG, "Load ext. codelist: ", e);
			e.printStackTrace();
			errors += e.getLocalizedMessage()+"\n";
		}

		// custom conversions
		try
		{
			String filePath = prefs.getString(SettingsActivity.extKeys[0], null);
			if(filePath != null)
			{
				Log.i(TAG, "Load ext. conversions: "+filePath);
				InputStream inStr = getContentResolver().openInputStream(Uri.parse(filePath));
				EcuDataItems.cnv.loadFromStream(inStr);
			}
		} catch (Exception e)
		{
			Log.e(TAG, "Load ext. conversions: ", e);
			e.printStackTrace();
			errors += e.getLocalizedMessage()+"\n";
		}

		// custom PIDs
		try
		{
			String filePath = prefs.getString(SettingsActivity.extKeys[1], null);
			if(filePath != null)
			{
				Log.i(TAG, "Load ext. conversions: "+filePath);
				InputStream inStr = getContentResolver().openInputStream(Uri.parse(filePath));
				ObdProt.dataItems.loadFromStream(inStr);
			}
		} catch (Exception e)
		{
			Log.e(TAG, "Load ext. PIDs: ", e);
			e.printStackTrace();
			errors += e.getLocalizedMessage()+"\n";
		}

		if(errors.length() != 0)
		{
			new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(R.string.extension_loading)
				.setMessage(getString(R.string.check_cust_settings) + errors)
				.show();
		}
	}

	/**
	 * handle pressing of the BACK-KEY
	 */
	@Override
	public void onBackPressed()
	{
		if (mCommService.elm.getService() != ObdProt.OBD_SVC_NONE)
		{
			setObdService(ObdProt.OBD_SVC_NONE, null);
		} else
		{
			if (lastBackPressTime < System.currentTimeMillis() - EXIT_TIMEOUT)
			{
				exitToast = Toast.makeText(this, R.string.back_again_to_exit, Toast.LENGTH_SHORT);
				exitToast.show();
				lastBackPressTime = System.currentTimeMillis();
			} else
			{
				if (exitToast != null)
				{
					exitToast.cancel();
				}
				super.onBackPressed();
			}
		}
	}

	/**
	 * Handler for application start event
	 */
	@Override
	public void onStart()
	{
		super.onStart();
		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null)
		{
			// start ELM protocol demo loop
			startDemoService();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy()
	{
		// stop demo service if it was started
		stopDemoService();

		// if bluetooth adapter was switched OFF before ...
		if (mBluetoothAdapter != null && !initialBtStateEnabled)
		{
			// ... turn it OFF again
			mBluetoothAdapter.disable();
		}

		super.onDestroy();
	}

	/**
	 * Handle short clicks in OBD data list items
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);
		// enable graphic actions only on DATA service if min 1 item selected
		setMenuItemEnable(R.id.graph_actions,
			((mCommService.elm.getService() == ObdProt.OBD_SVC_DATA)
				&& (getListView().getCheckedItemCount() > 0)
			)
		);
	}

	/**
	 * Handle long licks on OBD data list items
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
	{
		Intent intent;

		switch (mCommService.elm.getService())
		{
		    /* if we are in OBD data mode:
		     * ->Long click on an item starts the single item dashboard activity
		     */
			case ObdProt.OBD_SVC_DATA:
				EcuDataPv pv = (EcuDataPv)getListAdapter().getItem(position);
				/* only numeric values may be shown as graph/dashboard */
				if(pv.get(EcuDataPv.FID_VALUE) instanceof Number)
				{
					DashBoardActivity.setAdapter(getListAdapter());
					intent = new Intent(this, DashBoardActivity.class);
					intent.putExtra(DashBoardActivity.POSITIONS, new int[]{ position });
					startActivity(intent);
				}
				break;

			/* If we are in DFC mode of any kind
			 * -> Long click leads to a web search for selected DFC
			 */
			case ObdProt.OBD_SVC_READ_CODES:
			case ObdProt.OBD_SVC_PERMACODES:
			case ObdProt.OBD_SVC_PENDINGCODES:
				intent = new Intent(Intent.ACTION_WEB_SEARCH);
				EcuCodeItem dfc = (EcuCodeItem) getListAdapter().getItem(position);
				intent.putExtra(SearchManager.QUERY,
					"OBD " + String.valueOf(dfc.get(EcuCodeItem.FID_CODE)));
				startActivity(intent);
				break;
		}
		return true;
	}

	/**
	 * Activate desired OBD service
	 *
	 * @param obdService OBD service ID to be activated
	 */
	public void setObdService(int obdService, CharSequence menuTitle)
	{
		setContentView(R.layout.obd_list);
		// un-filter display
		setFiltered(false);
		// set title
		TextView title = (TextView) findViewById(R.id.title);
		title.setText(menuTitle);
		// update controls
		setMenuItemEnable(R.id.graph_actions, false);
		getListView().setOnItemLongClickListener(this);
		// set protocol service
		mCommService.elm.setService(obdService);
		// set corresponding list adapter
		switch (obdService)
		{
			case ObdProt.OBD_SVC_DATA:
			case ObdProt.OBD_SVC_FREEZEFRAME:
				currDataAdapter = mPidAdapter;
				break;

			case ObdProt.OBD_SVC_PENDINGCODES:
			case ObdProt.OBD_SVC_PERMACODES:
			case ObdProt.OBD_SVC_READ_CODES:
				currDataAdapter = mDfcAdapter;
				break;

			case ObdProt.OBD_SVC_NONE:
				setContentView(R.layout.startup_layout);
				// intentionally no break to initialize adapter
			case ObdProt.OBD_SVC_VEH_INFO:
				currDataAdapter = mVidAdapter;
				break;
		}
		setListAdapter(currDataAdapter);
	}

	/**
	 * Set enabled state for a specified menu item
	 * * this includes shading disabled items to visualize state
	 *
	 * @param id      ID of menu item
	 * @param enabled flag if to be enabled/disabled
	 */
	private void setMenuItemEnable(int id, boolean enabled)
	{
		if (menu != null)
		{
			MenuItem item = menu.findItem(id);
			item.setEnabled(enabled);
			item.getIcon().setAlpha(enabled ? 255 : 127);
		}
	}

	/**
	 * Start demo mode Thread
	 */
	private void startDemoService()
	{
		if (!demoMode)
		{
			demoMode = true;
			setStatus(getString(R.string.demo));
			Toast.makeText(this, getString(R.string.demo_started), Toast.LENGTH_SHORT).show();
			setMenuItemEnable(R.id.secure_connect_scan, false);
			setMenuItemEnable(R.id.obd_services, true);
			setMenuItemEnable(R.id.graph_actions, false);
			/* The Thread object for processing the demo mode loop */
			Thread demoThread = new Thread(mCommService.elm);
			demoThread.start();
		}
	}

	/**
	 * Stop demo mode Thread
	 */
	private void stopDemoService()
	{
		if (demoMode)
		{
			demoMode = false;
			ElmProt.runDemo = false;
			Toast.makeText(this, getString(R.string.demo_stopped), Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * set status message in status bar
	 *
	 * @param resId Resource ID of the text to be displayed
	 */
	private void setStatus(int resId)
	{
		final ActionBar actionBar = getActionBar();
		if (actionBar != null)
		{
			actionBar.setSubtitle(resId);
		}
	}

	/**
	 * set status message in status bar
	 *
	 * @param subTitle status text to be set
	 */
	private void setStatus(CharSequence subTitle)
	{
		final ActionBar actionBar = getActionBar();
		if (actionBar != null)
		{
			actionBar.setSubtitle(subTitle);
		}
	}

	/**
	 * Initiate a connect to the selected bluetooth device
	 *
	 * @param data   Intent data which contains the bluetooth device address
	 * @param secure flag to indicate if the connection shall be secure, or not
	 */
	private void connectDevice(Intent data, boolean secure)
	{
		// Get the device MAC address
		String address = data.getExtras().getString(
			DeviceListActivity.EXTRA_DEVICE_ADDRESS);
		// Get the BluetoothDevice object
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		// Attempt to connect to the device
		mCommService.connect(device, secure);
	}

	/**
	 * Handle bluetooth connection established ...
	 */
	private void onConnect()
	{
		// handle further initialisations
		setMenuItemEnable(R.id.secure_connect_scan, false);
		setMenuItemEnable(R.id.obd_services, true);
		setMenuItemEnable(R.id.graph_actions, false);
		// display connection status
		setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
	}

	/**
	 * Handle bluetooth connection lost ...
	 */
	private void onDisconnect()
	{
		// handle further initialisations
		setMenuItemEnable(R.id.secure_connect_scan, true);
		setMenuItemEnable(R.id.obd_services, false);
		setMenuItemEnable(R.id.graph_actions, false);
		// display connection status
		setStatus(R.string.title_not_connected);
	}

	/**
	 * Handler for PV change events This handler just forwards the PV change
	 * events to the android handler, since all adapter / GUI actions have to be
	 * performed from the main handler
	 *
	 * @param event PvChangeEvent which is reported
	 */
	@Override
	public synchronized void pvChanged(PvChangeEvent event)
	{
		// forward PV change to the UI Activity
		Message msg = mHandler.obtainMessage(MainActivity.MESSAGE_DATA_ITEMS_CHANGED);
		msg.obj = event;
		mHandler.sendMessage(msg);
	}

	/**
	 * clear OBD fault codes after a warning
	 * confirmation dialog is shown and the operation is confirmed
	 */
	protected void clearObdFaultCodes()
	{
		new AlertDialog.Builder(this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle(R.string.obd_clearcodes)
			.setMessage(R.string.obd_clear_info)
			.setPositiveButton(android.R.string.yes,
				new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						// set service CLEAR_CODES to clear the codes
						mCommService.elm.setService(ObdProt.OBD_SVC_CLEAR_CODES);
						// set service READ_CODES to re-read the codes
						mCommService.elm.setService(ObdProt.OBD_SVC_READ_CODES);
					}
				})
			.setNegativeButton(android.R.string.no, null)
			.show();
	}

}
