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
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
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
import org.apache.log4j.Logger;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import de.mindpipe.android.logging.log4j.LogConfigurator;

/**
 * Main Activity for AndrOBD app
 */
public class MainActivity extends ListActivity
	implements PvChangeListener,
	AdapterView.OnItemLongClickListener,
	PropertyChangeListener,
	SharedPreferences.OnSharedPreferenceChangeListener
{
	/**
	 * Key names for preferences
	 */
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";
	public static final String MEASURE_SYSTEM = "measure_system";
	public static final String NIGHT_MODE = "night_mode";
	public static final String ELM_ADAPTIVE_TIMING = "elm_adaptive_timing";
	public static final String PREF_ECU_ADDRESS = "ecu_address";
	public static final String PREF_DEV_ADDRESS = "device_address";
	public static final String PREF_USE_LAST = "use_last_settings";
	public static final String PREF_LAST_ITEMS = "last_items";

	/**
	 * Message types sent from the BluetoothChatService Handler
	 */
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_FILE_READ = 2;
	public static final int MESSAGE_FILE_WRITTEN = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;
	public static final int MESSAGE_DATA_ITEMS_CHANGED = 6;
	public static final int MESSAGE_UPDATE_VIEW = 7;
	public static final int MESSAGE_OBD_STATE_CHANGED = 8;
	public static final int MESSAGE_OBD_NUMCODES = 9;
	public static final int MESSAGE_OBD_ECUS = 10;
	private static final String TAG = "AndrOBD";
	/**
	 * internal Intent request codes
	 */
	private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
	private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
	private static final int REQUEST_ENABLE_BT = 3;
	private static final int REQUEST_SELECT_FILE = 4;
	private static final int REQUEST_SETTINGS = 5;
	private static final int REQUEST_CONNECT_DEVICE_USB = 6;

	/**
	 * app exit parameters
	 */
	private static final int EXIT_TIMEOUT = 2500;
	/**
	 * time between display updates to represent data changes
	 */
	private static final int DISPLAY_UPDATE_TIME = 200;
	public static final String LOG_MASTER = "log_master";
	public static final String KEEP_SCREEN_ON = "keep_screen_on";

	public static final Logger log = Logger.getLogger(TAG);

	/** dialog builder */
	private static AlertDialog.Builder dlgBuilder;

	/**
	 * app preferences ...
	 */
	protected static SharedPreferences prefs;
	/**
	 * Member object for the BT comm services
	 */
	private static CommService mCommService = null;
	/**
	 * Local Bluetooth adapter
	 */
	private static BluetoothAdapter mBluetoothAdapter = null;
	/**
	 * Name of the connected BT device
	 */
	private static String mConnectedDeviceName = null;
	/**
	 * log4j configurator
	 */
	private static LogConfigurator logCfg;
	private static Menu menu;
	/**
	 * Data list adapters
	 */
	private static ObdItemAdapter mPidAdapter;
	private static VidItemAdapter mVidAdapter;
	private static DfcItemAdapter mDfcAdapter;
	private static ObdItemAdapter currDataAdapter;
	/**
	 * Timer for display updates
	 */
	private static Timer updateTimer = new Timer();
	/**
	 * initial state of bluetooth adapter
	 */
	private static boolean initialBtStateEnabled = false;
	/**
	 * last time of back key pressed
	 */
	private static long lastBackPressTime = 0;
	/**
	 * toast for showing exit message
	 */
	private static Toast exitToast = null;
	/** file helper */
	private static FileHelper fileHelper;
	/** the local list view */
	protected View mListView;

	/** handler for freeze frame selection */
	AdapterView.OnItemSelectedListener ff_selected = new AdapterView.OnItemSelectedListener()
	{
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
		{
			CommService.elm.setFreezeFrame_Id(position);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent)
		{

		}
	};

	/**
	 * activation of night mode
	 */
	private boolean nightMode = false;
	/** current OBD service */
	private int obdService = ElmProt.OBD_SVC_NONE;
	/**
	 * current operating mode
	 */
	private MODE mode = MODE.OFFLINE;

	/**
	 * Handle message requests
	 */
	private transient final Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			PropertyChangeEvent evt;

			switch (msg.what)
			{
				case MESSAGE_STATE_CHANGE:
					switch ((CommService.STATE) msg.obj)
					{
						case CONNECTED:
							onConnect();
							break;

						case CONNECTING:
							setStatus(R.string.title_connecting);
							break;

						default:
							onDisconnect();
							break;
					}
					break;

				case MESSAGE_FILE_WRITTEN:
					break;

				// data has been read - finish up
				case MESSAGE_FILE_READ:
					// set listeners for data structure changes
					setDataListeners();
					// set adapters data source to loaded list instances
					mPidAdapter.setPvList(ObdProt.PidPvs);
					mVidAdapter.setPvList(ObdProt.VidPvs);
					mDfcAdapter.setPvList(ObdProt.tCodes);
					// set OBD data mode to the one selected by input file
					setObdService(CommService.elm.getService(), getString(R.string.saved_data));
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
								// if last settings shall be used ...
								if(prefs.getBoolean(PREF_USE_LAST, false)
									&& event.getSource() == ObdProt.PidPvs)
								{
									// get preference for last seleted items
									int[] lastSelectedItems =
										toIntArray(prefs.getString(PREF_LAST_ITEMS, ""));
									// select last selected items
									selectDataItems(lastSelectedItems, true);
								}
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

				case MESSAGE_OBD_STATE_CHANGED:
					/* Show ELM status only in ONLINE mode */
					if (getMode() != MODE.DEMO)
					{
						evt = (PropertyChangeEvent) msg.obj;
						setStatus(String.valueOf(evt.getNewValue()));
					}
					break;

				case MESSAGE_OBD_NUMCODES:
					evt = (PropertyChangeEvent) msg.obj;
					setNumCodes((Integer) evt.getNewValue());
					break;

				case MESSAGE_OBD_ECUS:
					evt = (PropertyChangeEvent) msg.obj;
					selectEcu((Set<Integer>)evt.getNewValue());
					break;
			}
		}
	};


	/**
	 * convert result of Arrays.toString(int[]) back into int[]
	 * @param input String of array
	 * @return int[] of String value
	 */
	private int[] toIntArray(String input) {
		String beforeSplit = input.replaceAll("\\[|\\]|\\s", "");
		String[] split = beforeSplit.split("\\,");
		int[] result = new int[split.length];
		for (int i = 0; i < split.length; i++) {
			result[i] = Integer.parseInt(split[i]);
		}
		return result;
	}

	/**
	 * Prompt for selection of a single ECU from list of available ECUs
	 * @param ecuAdresses List of available ECUs
	 */
	protected void selectEcu(final Set<Integer> ecuAdresses)
	{
		// if more than one ECUs available ...
		if(ecuAdresses.size() > 1)
		{
			int preferredAddress = prefs.getInt(PREF_ECU_ADDRESS,0);
			// check if last preferred address matches any of the reported addresses
			if(prefs.getBoolean(PREF_USE_LAST,false)
			   && ecuAdresses.contains(preferredAddress))
			{
				// set this as preference (preference change will trigger ELM command)
				prefs.edit().putInt(PREF_ECU_ADDRESS, preferredAddress).apply();
			}
			else
			{
				// NO match with preference -> allow selection

				// .. allow selection of single ECU address ...
				final CharSequence[] entries = new CharSequence[ecuAdresses.size()];
				// create list of entries
				int i = 0;
				for (Integer addr : ecuAdresses)
				{
					entries[i++] = String.format("0x%X", addr);
				}
				// show dialog ...
				dlgBuilder
					.setTitle(R.string.select_ecu_addr)
					.setItems(entries, new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							int address = Integer.parseInt(entries[which].toString().substring(2), 16);
							// set this as preference (preference change will trigger ELM command)
							prefs.edit().putInt(PREF_ECU_ADDRESS, address).apply();
						}
					})
					.show();
			}
		}
	}

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

	public boolean isNightMode()
	{
		return nightMode;
	}

	public void setNightMode(boolean nightMode)
	{
		this.nightMode = nightMode;
		setTheme(nightMode ? R.style.AppTheme_Dark : R.style.AppTheme);
		getWindow().getDecorView().setBackgroundColor(nightMode ? Color.BLACK : Color.WHITE);
		setObdService(obdService, null);
	}

	private void setNumCodes(int newNumCodes)
	{
		// set list background based on MIL status
		View list = findViewById(R.id.obd_list);
		if(list != null)
		{
			list.setBackgroundResource((newNumCodes & 0x80) != 0
				                           ? R.drawable.mil_on
				                           : R.drawable.mil_off);
		}
		// enable / disable freeze frames based on number of codes
		setMenuItemEnable(R.id.service_freezeframes, (newNumCodes != 0));
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
			if (item != null)
			{
				item.setEnabled(enabled);

				// if menu item has icon ...
				Drawable icon = item.getIcon();
				if (icon != null)
				{
					// set it's shading
					icon.setAlpha(enabled ? 255 : 127);
				}
			}
		}
	}

	/**
	 * Set enabled state for a specified menu item
	 * * this includes shading disabled items to visualize state
	 *
	 * @param id      ID of menu item
	 * @param enabled flag if to be visible/invisible
	 */
	private void setMenuItemVisible(int id, boolean enabled)
	{
		if (menu != null)
		{
			MenuItem item = menu.findItem(id);
			if (item != null)
			{
				item.setVisible(enabled);
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		                     WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// set up log4j logging ...
		logCfg = new LogConfigurator();
		logCfg.setUseLogCatAppender(true);
		logCfg.setUseFileAppender(true);
		logCfg.setFileName(
			FileHelper.getPath(this).concat(File.separator).concat("log/AndrOBD.log"));

		log.info(String.format("%s %s starting",
		                       getString(R.string.app_name),
		                       getString(R.string.app_version)));

		// Set up all data adapters
		mPidAdapter = new ObdItemAdapter(this, R.layout.obd_item, ObdProt.PidPvs);
		mVidAdapter = new VidItemAdapter(this, R.layout.obd_item, ObdProt.VidPvs);
		mDfcAdapter = new DfcItemAdapter(this, R.layout.obd_item, ObdProt.tCodes);
		currDataAdapter = mPidAdapter;

		mListView = getWindow().getLayoutInflater().inflate(R.layout.obd_list, null);

		// get preferences
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		onSharedPreferenceChanged(prefs, null);
		// register for later changes
		prefs.registerOnSharedPreferenceChangeListener(this);

		// set up action bar
		ActionBar actionBar = getActionBar();
		if (actionBar != null)
		{
			actionBar.setDisplayShowTitleEnabled(true);
		}

		setContentView(R.layout.startup_layout);

		dlgBuilder = new AlertDialog.Builder(this);

		// create file helper instance
		fileHelper = new FileHelper(this, CommService.elm);
		// set listeners for data structure changes
		setDataListeners();
		// automate elm status display
		CommService.elm.addPropertyChangeListener(this);

		// override comm medium with USB connect intent
		if ("android.hardware.usb.action.USB_DEVICE_ATTACHED".equals(getIntent().getAction()))
		{
			CommService.medium = CommService.MEDIUM.USB;
		}

		switch (CommService.medium)
		{
			case BLUETOOTH:
				// Get local Bluetooth adapter
				mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				log.debug("Adapter: " + mBluetoothAdapter);
				// If BT is not on, request that it be enabled.
				if (getMode() != MODE.DEMO && mBluetoothAdapter != null)
				{
					// remember initial bluetooth state
					initialBtStateEnabled = mBluetoothAdapter.isEnabled();
					if (!initialBtStateEnabled)
					{
						Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
						startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
					}
				}
				break;

			case USB:
				setMode(MODE.ONLINE);
				break;
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences prefs, String key)
	{
		// keep main display on?
		if (key==null || KEEP_SCREEN_ON.equals(key))
		{
			getWindow().addFlags(prefs.getBoolean(KEEP_SCREEN_ON, false)
			                     ? WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
		                         : 0);
		}

		// night mode
		if(key==null || NIGHT_MODE.equals(key))
			setNightMode(prefs.getBoolean(NIGHT_MODE, false));

		// set default comm medium
		if(key==null || SettingsActivity.KEY_COMM_MEDIUM.equals(key))
			CommService.medium =
				CommService.MEDIUM.values()[
					Integer.valueOf(prefs.getString(SettingsActivity.KEY_COMM_MEDIUM, "0"))];

		// enable/disable ELM adaptive timing
		if(key==null || ELM_ADAPTIVE_TIMING.equals(key))
			CommService.elm.mAdaptiveTiming.setEnabled(prefs.getBoolean(ELM_ADAPTIVE_TIMING, true));

		// ELM timeout
		if(key==null || SettingsActivity.ELM_MIN_TIMEOUT.equals(key))
			CommService.elm.mAdaptiveTiming.setElmTimeoutMin(
				Integer.valueOf(prefs.getString(SettingsActivity.ELM_MIN_TIMEOUT,
				                                String.valueOf(CommService.elm.mAdaptiveTiming.getElmTimeoutMin()))));

		// ECU address
		if(PREF_ECU_ADDRESS.equals(key))
			CommService.elm.setEcuAddress(prefs.getInt(PREF_ECU_ADDRESS, 0));

		// ... measurement system
		if(key==null || MEASURE_SYSTEM.equals(key))
			setConversionSystem(Integer.valueOf(
				                    prefs.getString(MEASURE_SYSTEM,
				                                    String.valueOf(EcuDataItem.SYSTEM_METRIC)))
			);

		// ... preferred protocol
		if(key==null || SettingsActivity.KEY_PROT_SELECT.equals(key))
			ElmProt.setPreferredProtocol(
				Integer.valueOf(prefs.getString(SettingsActivity.KEY_PROT_SELECT, "0")));

		// log levels
		if(key==null || LOG_MASTER.equals(key))
			setLogLevels();

		// update from protocol extensions
		if(key==null || key.startsWith("ext_file-"))
			loadPreferredExtensions();

		// set disabled ELM commands
		if(key==null || SettingsActivity.ELM_CMD_DISABLE.equals(key))
		{
			ElmProt.disableCommands(prefs.getStringSet(SettingsActivity.ELM_CMD_DISABLE, null));
		}
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
	 * set listeners for data structure changes
	 */
	private void removeDataListeners()
	{
		// remove pv change listeners
		ObdProt.PidPvs.removePvChangeListener(this);
		ObdProt.VidPvs.removePvChangeListener(this);
		ObdProt.tCodes.removePvChangeListener(this);
	}

	/**
	 * get current operating mode
	 */
	public MODE getMode()
	{
		return mode;
	}

	/**
	 * set new operating mode
	 *
	 * @param mode new mode
	 */
	public void setMode(MODE mode)
	{
		// if this is a mode change, or file reload ...
		if (mode != this.mode || mode == MODE.FILE)
		{
			if (mode != MODE.DEMO) stopDemoService();

			switch (mode)
			{
				case OFFLINE:
					setMenuItemVisible(R.id.disconnect, false);
					setMenuItemVisible(R.id.secure_connect_scan, true);

					setMenuItemEnable(R.id.obd_services, false);
					setMenuItemEnable(R.id.graph_actions, false);
					break;

				case ONLINE:
					switch (CommService.medium)
					{
						case BLUETOOTH:
							// if pre-settings shall be used ...
							String address = prefs.getString(PREF_DEV_ADDRESS, null);
							if(prefs.getBoolean(PREF_USE_LAST,false)
								 && address != null)
							{
								// ... connect with previously connected device
								connectBtDevice(address, true);
							}
							else
							{
								// ... otherwise launch the BtDeviceListActivity to see devices and do scan
								Intent serverIntent = new Intent(this, BtDeviceListActivity.class);
								startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
							}
							break;

						case USB:
							Intent enableIntent = new Intent(this, UsbDeviceListActivity.class);
							startActivityForResult(enableIntent, REQUEST_CONNECT_DEVICE_USB);
					}
					break;

				case DEMO:
					startDemoService();
					break;

				case FILE:
					setStatus(R.string.saved_data);
					selectFileToLoad();
					break;
			}
			// set new mode
			this.mode = mode;
			setStatus(mode.toString());
		}
	}

	/**
	 * set mesaurement conversion system to metric/imperial
	 *
	 * @param cnvId ID for metric/imperial conversion
	 */
	void setConversionSystem(int cnvId)
	{
		log.info("Conversion: " + getResources().getStringArray(R.array.measure_options)[cnvId]);
		if (EcuDataItem.cnvSystem != cnvId)
		{
			// set coversion system
			EcuDataItem.cnvSystem = cnvId;
		}
	}

	/**
	 * Setr logging levels from shared preferences
	 */
	private void setLogLevels()
	{
		logCfg.setRootLevel(Level.toLevel(prefs.getString(LOG_MASTER, "INFO")));
		logCfg.configure();
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
			if (filePath != null)
			{
				log.info("Load ext. codelist: " + filePath);
				InputStream inStr = getContentResolver().openInputStream(Uri.parse(filePath));
				EcuConversions.codeList.loadFromStream(inStr);
			}
		} catch (Exception e)
		{
			log.error("Load ext. codelist: ", e);
			e.printStackTrace();
			errors += e.getLocalizedMessage() + "\n";
		}

		// custom conversions
		try
		{
			String filePath = prefs.getString(SettingsActivity.extKeys[0], null);
			if (filePath != null)
			{
				log.info("Load ext. conversions: " + filePath);
				InputStream inStr = getContentResolver().openInputStream(Uri.parse(filePath));
				EcuDataItems.cnv.loadFromStream(inStr);
			}
		} catch (Exception e)
		{
			log.error("Load ext. conversions: ", e);
			e.printStackTrace();
			errors += e.getLocalizedMessage() + "\n";
		}

		// custom PIDs
		try
		{
			String filePath = prefs.getString(SettingsActivity.extKeys[1], null);
			if (filePath != null)
			{
				log.info("Load ext. conversions: " + filePath);
				InputStream inStr = getContentResolver().openInputStream(Uri.parse(filePath));
				ObdProt.dataItems.loadFromStream(inStr);
			}
		} catch (Exception e)
		{
			log.error("Load ext. PIDs: ", e);
			e.printStackTrace();
			errors += e.getLocalizedMessage() + "\n";
		}

		if (errors.length() != 0)
		{
			dlgBuilder
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(R.string.extension_loading)
				.setMessage(getString(R.string.check_cust_settings) + errors)
				.show();
		}
	}

	/**
	 * Stop demo mode Thread
	 */
	private void stopDemoService()
	{
		if (getMode() == MODE.DEMO)
		{
			ElmProt.runDemo = false;
			Toast.makeText(this, getString(R.string.demo_stopped), Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Start demo mode Thread
	 */
	private void startDemoService()
	{
		if (getMode() != MODE.DEMO)
		{
			setStatus(getString(R.string.demo));
			Toast.makeText(this, getString(R.string.demo_started), Toast.LENGTH_SHORT).show();

			boolean allowConnect = mBluetoothAdapter != null
															&& mBluetoothAdapter.isEnabled();
			setMenuItemVisible(R.id.secure_connect_scan, allowConnect);
			setMenuItemVisible(R.id.disconnect, !allowConnect);

			setMenuItemEnable(R.id.obd_services, true);
			setMenuItemEnable(R.id.graph_actions, false);
			/* The Thread object for processing the demo mode loop */
			Thread demoThread = new Thread(CommService.elm);
			demoThread.start();
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
	 * Select file to be loaded
	 */
	public void selectFileToLoad()
	{
		File file = new File(FileHelper.getPath(this));
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		Uri data = Uri.fromFile(file);
		String type = "*/*";
		intent.setDataAndType(data, type);
		startActivityForResult(intent, REQUEST_SELECT_FILE);
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
	 * Handler for application start event
	 */
	@Override
	public void onStart()
	{
		super.onStart();
		// If the adapter is null, then Bluetooth is not supported
		if (CommService.medium == CommService.MEDIUM.BLUETOOTH && mBluetoothAdapter == null)
		{
			// start ELM protocol demo loop
			setMode(MODE.DEMO);
		}
	}

	/**
	 * handle pressing of the BACK-KEY
	 */
	@Override
	public void onBackPressed()
	{
		if (CommService.elm.getService() != ObdProt.OBD_SVC_NONE)
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
	 * Handler for Options menu selection
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle presses on the action bar items
		updateTimer.purge();

		switch (item.getItemId())
		{
			case R.id.day_night_mode:
				// toggle night mode setting
				prefs.edit().putBoolean(NIGHT_MODE, !isNightMode()).apply();
				return true;

			case R.id.secure_connect_scan:
				setMode(MODE.ONLINE);
				return true;

			case R.id.reset_preselections:
				prefs.edit().remove(PREF_DEV_ADDRESS).apply();
				prefs.edit().remove(PREF_ECU_ADDRESS).apply();
				recreate();
				return true;

			case R.id.disconnect:
				setMode(MODE.OFFLINE);
				return true;

			case R.id.settings:
				// Launch the BtDeviceListActivity to see devices and do scan
				Intent settingsIntent = new Intent(this, SettingsActivity.class);
				startActivityForResult(settingsIntent, REQUEST_SETTINGS);
				return true;

			case R.id.chart_selected:
				/* if we are in OBD data mode:
				 * -> Short click on an item starts the readout activity
			     */
				if (CommService.elm.getService() == ObdProt.OBD_SVC_DATA)
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
				if (CommService.elm.getService() == ObdProt.OBD_SVC_DATA)
				{
					if (getListView().getCheckedItemCount() > 0)
					{
						DashBoardActivity.setAdapter(getListAdapter());
						Intent intent = new Intent(this, DashBoardActivity.class);
						intent.putExtra(DashBoardActivity.POSITIONS, getSelectedPositions());
						intent.putExtra(DashBoardActivity.RES_ID,
						                item.getItemId() == R.id.dashboard_selected ? R.layout.dashboard : R.layout.head_up);
						startActivity(intent);
					} else
					{
						setMenuItemEnable(R.id.graph_actions, false);
					}
				}
				return true;

			case R.id.filter_selected:
				setFiltered(true);
				return true;

			case R.id.unfilter_selected:
				setFiltered(false);
				return true;

			case R.id.save:
				// save recorded data (threaded)
				fileHelper.saveDataThreaded();
				return true;

			case R.id.load:
				setMode(MODE.FILE);
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
		}

		return super.onOptionsItemSelected(item);
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
			case REQUEST_CONNECT_DEVICE_INSECURE:
				// When BtDeviceListActivity returns with a device to connect
				if (resultCode == Activity.RESULT_OK)
				{
					// Get the device MAC address
					String address = data.getExtras().getString(
						BtDeviceListActivity.EXTRA_DEVICE_ADDRESS);
					// save reported address as last setting
					prefs.edit().putString(PREF_DEV_ADDRESS, address).apply();
					connectBtDevice(address, false);
				} else
				{
					setMode(MODE.OFFLINE);
				}
				break;

			case REQUEST_CONNECT_DEVICE_USB:
				// DeviceListActivity returns with a device to connect
				if (resultCode == Activity.RESULT_OK)
				{
					mCommService = new UsbCommService(this, mHandler);
					mCommService.connect(UsbDeviceListActivity.selectedPort, true);
				} else
				{
					setMode(MODE.OFFLINE);
				}
				break;

			case REQUEST_ENABLE_BT:
				// When the request to enable Bluetooth returns
				if (resultCode == Activity.RESULT_OK)
				{
					// Start online mode
					setMode(MODE.ONLINE);
				} else
				{
					// Start demo service Thread
					setMode(MODE.DEMO);
				}
				break;

			case REQUEST_SELECT_FILE:
				if (resultCode == RESULT_OK)
				{
					// Get the Uri of the selected file
					Uri uri = data.getData();
					log.info("Load content: " + uri);
					// load data ...
					fileHelper.loadDataThreaded(uri, mHandler, MESSAGE_FILE_READ);
					// don't allow saving it again
					setMenuItemEnable(R.id.save, false);
					setMenuItemEnable(R.id.obd_services, true);
				}
				break;

			case REQUEST_SETTINGS:
			{
				// change handling done by callbacks
			}
			break;
		}
	}

	/**
	 * Initiate a connect to the selected bluetooth device
	 *
	 * @param address bluetooth device address
	 * @param secure flag to indicate if the connection shall be secure, or not
	 */
	private void connectBtDevice(String address, boolean secure)
	{
		// Get the BluetoothDevice object
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		// Attempt to connect to the device
		mCommService = new BtCommService(this, mHandler);
		mCommService.connect(device, secure);
	}

	/**
	 * Activate desired OBD service
	 *
	 * @param newObdService OBD service ID to be activated
	 */
	public void setObdService(int newObdService, CharSequence menuTitle)
	{
		// remember this as current OBD service
		obdService = newObdService;
		// set list view
		setContentView(mListView);
		// un-filter display
		setFiltered(false);
		// set title
		ActionBar ab = getActionBar();
		if (ab != null)
		{
			// title specified ... show it
			if (menuTitle != null)
			{
				ab.setTitle(menuTitle);
			} else
			{
				// no title specified, set to app name if no service set
				if (newObdService == ElmProt.OBD_SVC_NONE)
				{
					ab.setTitle(getString(R.string.app_name));
				}
			}
		}
		// update controls
		setMenuItemEnable(R.id.graph_actions, false);
		getListView().setOnItemLongClickListener(this);
		// set protocol service
		CommService.elm.setService(newObdService, (getMode() != MODE.FILE));
		// show / hide freeze frame selector */
		Spinner ff_selector = (Spinner) findViewById(R.id.ff_selector);
		ff_selector.setOnItemSelectedListener(ff_selected);
		ff_selector.setAdapter(mDfcAdapter);
		ff_selector.setVisibility(
			newObdService == ObdProt.OBD_SVC_FREEZEFRAME ? View.VISIBLE : View.GONE);
		// set corresponding list adapter
		switch (newObdService)
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
	 * Filter display items to just the selected ones
	 */
	private void setFiltered(boolean filtered)
	{
		if (filtered)
		{
			PvList filteredList = new PvList();
			TreeSet<Integer> selPids = new TreeSet<Integer>();
			for (int pos : getSelectedPositions())
			{
				EcuDataPv pv = (EcuDataPv) mPidAdapter.getItem(pos);
				selPids.add(pv.getAsInt(EcuDataPv.FID_PID));
				filteredList.put(pv.toString(), pv);
			}
			mPidAdapter.setPvList(filteredList);
			setFixedPids(selPids);
		} else
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
		if (checkedItemsCount > 0)
		{
			int j = 0;
			// loop through findings
			for (int i = 0; i < checkedItems.size(); i++)
			{
				// Item position in adapter
				if (checkedItems.valueAt(i))
				{
					selectedPositions[j++] = checkedItems.keyAt(i);
				}
			}
		}
		// save this as last seleted positions
		prefs.edit().putString(PREF_LAST_ITEMS, Arrays.toString(selectedPositions)).apply();
		return selectedPositions;
	}

	/**
	 * Set selection status on specified list item positions
	 * @param positions list of positions to be set
	 * @param selectionStatus status to be set
	 */
	private void selectDataItems(int[] positions, boolean selectionStatus)
	{
		for(int i : positions)
		{
			getListView().setItemChecked(i, selectionStatus);
		}
		// enable graphic actions only on DATA service if min 1 item selected
		setMenuItemEnable(R.id.graph_actions, positions.length > 0 && selectionStatus);
	}

	/**
	 * Set fixed PIDs for protocol to specified list of PIDs
	 *
	 * @param pidNumbers List of PIDs
	 */
	public static void setFixedPids(Set<Integer> pidNumbers)
	{
		int[] pids = new int[pidNumbers.size()];
		int i = 0;
		for (Integer pidNum : pidNumbers) pids[i++] = pidNum;
		Arrays.sort(pids);
		// set protocol fixed PIDs
		ObdProt.setFixedPid(pids);
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
		                  ((CommService.elm.getService() == ObdProt.OBD_SVC_DATA)
			                  && (getListView().getCheckedItemCount() > 0)
		                  )
		);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy()
	{
		try
		{
			// Reduce ELM power consumption by setting it to sleep
			CommService.elm.goToSleep();
			// wait until message is out ...
			Thread.sleep(100, 0);
		} catch (InterruptedException e)
		{
			// do nothing
		}

		/* don't listen to ELM data changes any more */
		removeDataListeners();
		// don't listen to ELM property changes any more
		CommService.elm.removePropertyChangeListener(this);

		// stop demo service if it was started
		setMode(MODE.OFFLINE);

		if (mCommService != null) mCommService.stop();

		// if bluetooth adapter was switched OFF before ...
		if (mBluetoothAdapter != null && !initialBtStateEnabled)
		{
			// ... turn it OFF again
			mBluetoothAdapter.disable();
		}

		log.info(String.format("%s %s finished",
		                       getString(R.string.app_name),
		                       getString(R.string.app_version)));

		super.onDestroy();
	}

	/**
	 * Handle long licks on OBD data list items
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
	{
		Intent intent;

		switch (CommService.elm.getService())
		{
		    /* if we are in OBD data mode:
		     * ->Long click on an item starts the single item dashboard activity
		     */
			case ObdProt.OBD_SVC_DATA:
				EcuDataPv pv = (EcuDataPv) getListAdapter().getItem(position);
				/* only numeric values may be shown as graph/dashboard */
				if (pv.get(EcuDataPv.FID_VALUE) instanceof Number)
				{
					DashBoardActivity.setAdapter(getListAdapter());
					intent = new Intent(this, DashBoardActivity.class);
					intent.putExtra(DashBoardActivity.POSITIONS, new int[]{position});
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
	 * Handle bluetooth connection established ...
	 */
	private void onConnect()
	{
		stopDemoService();

		mode = MODE.ONLINE;
		// handle further initialisations
		setMenuItemVisible(R.id.secure_connect_scan, false);
		setMenuItemVisible(R.id.disconnect, true);

		setMenuItemEnable(R.id.obd_services, true);
		setMenuItemEnable(R.id.graph_actions, false);
		// display connection status
		setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
		// send RESET to Elm adapter
		CommService.elm.reset();
	}

	/**
	 * Handle bluetooth connection lost ...
	 */
	private void onDisconnect()
	{
		// handle further initialisations
		setMode(MODE.OFFLINE);
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
		if(!event.isChildEvent())
		{
			msg.obj = event;
			mHandler.sendMessage(msg);
		}
	}

	/**
	 * Property change listener to ELM-Protocol
	 *
	 * @param evt the property change event to be handled
	 */
	public void propertyChange(PropertyChangeEvent evt)
	{
    /* handle protocol status changes */
		if (ElmProt.PROP_STATUS.equals(evt.getPropertyName()))
		{
			// forward property change to the UI Activity
			Message msg = mHandler.obtainMessage(MESSAGE_OBD_STATE_CHANGED);
			msg.obj = evt;
			mHandler.sendMessage(msg);
		} else if (ElmProt.PROP_NUM_CODES.equals(evt.getPropertyName()))
		{
			// forward property change to the UI Activity
			Message msg = mHandler.obtainMessage(MESSAGE_OBD_NUMCODES);
			msg.obj = evt;
			mHandler.sendMessage(msg);
		} else if (ElmProt.PROP_ECU_ADDRESS.equals(evt.getPropertyName()))
		{
			// forward property change to the UI Activity
			Message msg = mHandler.obtainMessage(MESSAGE_OBD_ECUS);
			msg.obj = evt;
			mHandler.sendMessage(msg);
		}
	}

	/**
	 * clear OBD fault codes after a warning
	 * confirmation dialog is shown and the operation is confirmed
	 */
	protected void clearObdFaultCodes()
	{
		dlgBuilder
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
					                   CommService.elm.setService(ObdProt.OBD_SVC_CLEAR_CODES);
					                   // set service READ_CODES to re-read the codes
					                   CommService.elm.setService(ObdProt.OBD_SVC_READ_CODES);
				                   }
			                   })
			.setNegativeButton(android.R.string.no, null)
			.show();
	}

	/**
	 * operating modes
	 */
	public enum MODE
	{
		OFFLINE,
		ONLINE,
		DEMO,
		FILE
	}

}
