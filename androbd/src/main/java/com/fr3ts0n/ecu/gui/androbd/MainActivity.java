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

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.fr3ts0n.androbd.plugin.Plugin;
import com.fr3ts0n.androbd.plugin.mgr.PluginManager;
import com.fr3ts0n.ecu.EcuCodeItem;
import com.fr3ts0n.ecu.EcuDataItem;
import com.fr3ts0n.ecu.EcuDataItems;
import com.fr3ts0n.ecu.EcuDataPv;
import com.fr3ts0n.ecu.prot.obd.ElmProt;
import com.fr3ts0n.ecu.prot.obd.ObdProt;
import com.fr3ts0n.pvs.ProcessVar;
import com.fr3ts0n.pvs.PvChangeEvent;
import com.fr3ts0n.pvs.PvList;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Main Activity for AndrOBD app
 */
public class MainActivity extends PluginManager
        implements AdapterView.OnItemLongClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener,
        AbsListView.MultiChoiceModeListener
{
    /**
     * Key names for preferences
     */
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    public static final String PREF_AUTOHIDE = "autohide_toolbar";
    public static final String PREF_FULLSCREEN = "full_screen";
    public static final String PREF_AUTOHIDE_DELAY = "autohide_delay";
    /**
     * Message types sent from the BluetoothChatService Handler
     */
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_FILE_READ = 2;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_UPDATE_VIEW = 7;
    public static final int MESSAGE_TOOLBAR_VISIBLE = 12;
    /**
     * Mapping list from Plugin.CsvField to EcuDataPv.key
     */
    static final Object[] csvFidMap =
    {
        EcuDataPv.FID_MNEMONIC,
        EcuDataPv.FIELDS[EcuDataPv.FID_DESCRIPT],
        EcuDataPv.FID_MIN,
        EcuDataPv.FID_MAX,
        EcuDataPv.FIELDS[EcuDataPv.FID_UNITS]
    };
    private static final String DEVICE_ADDRESS = "device_address";
    private static final String DEVICE_PORT = "device_port";
    private static final String MEASURE_SYSTEM = "measure_system";
    private static final String NIGHT_MODE = "night_mode";
    private static final String ELM_ADAPTIVE_TIMING = "adaptive_timing_mode";
    private static final String ELM_RESET_ON_NRC = "elm_reset_on_nrc";
    private static final String PREF_USE_LAST = "USE_LAST_SETTINGS";
    private static final String PREF_OVERLAY = "toolbar_overlay";
    private static final String PREF_DATA_DISABLE_MAX = "data_disable_max";
    public static final int MESSAGE_FILE_WRITTEN = 3;
    public static final int MESSAGE_DATA_ITEMS_CHANGED = 6;
    public static final int MESSAGE_OBD_STATE_CHANGED = 8;
    public static final int MESSAGE_OBD_NUMCODES = 9;
    public static final int MESSAGE_OBD_ECUS = 10;
    public static final int MESSAGE_OBD_NRC = 11;
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
    private static final int REQUEST_GRAPH_DISPLAY_DONE = 7;
    /**
     * app exit parameters
     */
    private static final int EXIT_TIMEOUT = 2500;
    /**
     * time between display updates to represent data changes
     */
    private static final int DISPLAY_UPDATE_TIME = 250;
    private static final String LOG_MASTER = "log_master";
    private static final String KEEP_SCREEN_ON = "keep_screen_on";
    private static final String ELM_CUSTOM_INIT_CMDS = "elm_custom_init_cmds";
    /**
     * Logging
     */
    private static final Logger rootLogger = Logger.getLogger("");
    private static final Logger log = Logger.getLogger(TAG);
    /**
     * Timer for display updates
     */
    private static Timer updateTimer;
    /**
     * empty string set as default parameter
     */
    private static final Set<String> emptyStringSet = new HashSet<>();
    /**
     * Container for Plugin-provided data
     */
    public static PvList mPluginPvs = new PvList();
    /**
     * current status of night mode
     */
    public static boolean nightMode = false;
    /**
     * app preferences ...
     */
    static SharedPreferences prefs;
    /**
     * dialog builder
     */
    private static AlertDialog.Builder dlgBuilder;
    /**
     * Local Bluetooth adapter
     */
    private static BluetoothAdapter mBluetoothAdapter = null;
    /**
     * Name of the connected BT device
     */
    private static String mConnectedDeviceName = null;
    /**
     * menu object
     */
    private static Menu menu;
    /**
     * Data list adapters
     */
    public static ObdItemAdapter mPidAdapter;
    public static VidItemAdapter mVidAdapter;
    public static TidItemAdapter mTidAdapter;
    public static DfcItemAdapter mDfcAdapter;
    public static PluginDataAdapter mPluginDataAdapter;
    public static ObdItemAdapter currDataAdapter;
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

    /**
     * Flag to temporarily ignore NRCs
     * This flag ist used to temporarily allow negative OBD responses without issuing an error message.
     * i.e. un-supported mode 0x0A for DFC reading
     */
    private static boolean ignoreNrcs = false;

    /**
     * handler for freeze frame selection
     */
    private final AdapterView.OnItemSelectedListener ff_selected = new AdapterView.OnItemSelectedListener()
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
     * Member object for the BT comm services
     */
    //private CommService mCommService = null;
    /**
     * file helper
     */
    private FileHelper fileHelper;
    /**
     * the local list view
     */
    private View mListView;
    /**
     * current data view mode
     */
    private DATA_VIEW_MODE dataViewMode = DATA_VIEW_MODE.LIST;
    /**
     * AutoHider for the toolbar
     */
    private AutoHider toolbarAutoHider;
    /**
     * log file handler
     */
    private FileHandler logFileHandler;
    /**
     * current OBD service
     */
    private int obdService = ElmProt.OBD_SVC_NONE;
    /**
     * current operating mode
     */
    private MODE mode = MODE.OFFLINE;
    /**
     * Handle message requests
     */
    @SuppressLint("HandlerLeak")
    private transient final Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            try
            {
                PropertyChangeEvent evt;

                // log trace message for received handler notification event
                log.log(Level.FINEST, String.format("Handler notification: %s", msg.toString()));

                switch (msg.what)
                {
                    case MESSAGE_STATE_CHANGE:
                        // log trace message for received handler notification event
                        log.log(Level.FINEST, String.format("State change: %s", msg.toString()));
                        switch ((CommService.STATE) msg.obj)
                        {
                            case CONNECTED:
                                onConnect(!WorkerService.isRunning);
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
                        // set adapters data source to loaded list instances
                        mPidAdapter.setPvList(ObdProt.PidPvs);
                        mVidAdapter.setPvList(ObdProt.VidPvs);
                        mTidAdapter.setPvList(ObdProt.VidPvs);
                        mDfcAdapter.setPvList(ObdProt.tCodes);

                        mWorkerServiceConnectionPendingTask = () -> {
                            mWorkerServiceBinder.connectToFile();
                        };

                        if (!mIsWorkerServiceBound) {
                            startOrBindWorkerService();
                        }
                        else {
                            mWorkerServiceConnectionPendingTask.func();
                            mWorkerServiceConnectionPendingTask = null;
                        }

                        // set OBD data mode to the one selected by input file
                        setObdService(CommService.elm.getService(), getString(R.string.saved_data));
                        // Check if last data selection shall be restored
                        if (obdService == ObdProt.OBD_SVC_DATA)
                        {
                            checkToRestoreLastDataSelection();
                            checkToRestoreLastViewMode();
                        }
                        break;

                    case MESSAGE_DEVICE_NAME:
                        // save the connected device's name
                        mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.connected_to) + mConnectedDeviceName,
                                Toast.LENGTH_SHORT).show();
                        break;

                    case MESSAGE_TOAST:
                        stopWorkerService();

                        Toast.makeText(getApplicationContext(),
                                msg.getData().getString(TOAST),
                                Toast.LENGTH_SHORT).show();
                        break;

                    case MESSAGE_DATA_ITEMS_CHANGED:
                        PvChangeEvent event = (PvChangeEvent) msg.obj;
                        switch (event.getType())
                        {
                            case PvChangeEvent.PV_ADDED:
                                try
                                {
                                    if (event.getSource() == ObdProt.PidPvs)
                                    {
                                        // append plugin measurements to data list
                                        currDataAdapter.addAll(mPluginPvs.values());
                                        // Check if last data selection shall be restored
                                        checkToRestoreLastDataSelection();
                                        checkToRestoreLastViewMode();
                                    }
                                } catch (Exception e)
                                {
                                    log.log(Level.FINER, "Error adding PV", e);
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

                    // handle state change in OBD protocol
                    case MESSAGE_OBD_STATE_CHANGED:
                        evt = (PropertyChangeEvent) msg.obj;
                        ElmProt.STAT state = (ElmProt.STAT) evt.getNewValue();
                        /* Show ELM status only in ONLINE mode */
                        if (getMode() != MODE.DEMO)
                        {
                            setStatus(getResources().getStringArray(R.array.elmcomm_states)[state
                                    .ordinal()]);
                        }
                        // if last selection shall be restored ...
                        if (istRestoreWanted(PRESELECT.LAST_SERVICE))
                        {
                            if (state == ElmProt.STAT.ECU_DETECTED)
                            {
                                setObdService(prefs.getInt(PRESELECT.LAST_SERVICE.toString(), 0),
                                        null);
                            }
                        }
                        break;

                    // handle change in number of fault codes
                    case MESSAGE_OBD_NUMCODES:
                        evt = (PropertyChangeEvent) msg.obj;
                        setNumCodes((Integer) evt.getNewValue());
                        break;

                    // handle ECU detection event
                    case MESSAGE_OBD_ECUS:
                        evt = (PropertyChangeEvent) msg.obj;
                        selectEcu((Set<Integer>) evt.getNewValue());
                        break;

                    // handle negative result code from OBD protocol
                    case MESSAGE_OBD_NRC:
                        // show error dialog ...
                        if(! ignoreNrcs)
                        {
                            evt = (PropertyChangeEvent) msg.obj;
                            ObdProt.NRC nrc = (ObdProt.NRC) evt.getOldValue();
                            String nrcMsg = (String) evt.getNewValue();
                            switch (nrc.disp)
                            {
                                case ERROR:
                                    dlgBuilder
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setTitle(R.string.obd_error)
                                        .setMessage(nrcMsg)
                                        .setPositiveButton(null, null)
                                        .show();
                                    break;
                                // Display warning (with confirmation)
                                case WARN:
                                    dlgBuilder
                                        .setIcon(android.R.drawable.ic_dialog_info)
                                        .setTitle(R.string.obd_error)
                                        .setMessage(nrcMsg)
                                        .setPositiveButton(null, null)
                                        .show();
                                    break;
                                // Display notification (no confirmation)
                                case NOTIFY:
                                    Toast.makeText(getApplicationContext(),
                                        nrcMsg,
                                        Toast.LENGTH_SHORT).show();
                                    break;

                                case HIDE:
                                default:
                                    // intentionally ignore
                            }
                        }
                        break;

                    // set toolbar visibility
                    case MESSAGE_TOOLBAR_VISIBLE:
                        Boolean visible = (Boolean) msg.obj;
                        // log action
                        log.fine(String.format("ActionBar: %s", visible ? "show" : "hide"));
                        // set action bar visibility
                        ActionBar ab = getActionBar();
                        if (ab != null)
                        {
                            if (visible)
                            {
                                ab.show();
                            } else
                            {
                                ab.hide();
                            }
                        }
                        break;
                }
            } catch (Exception ex)
            {
                log.log(Level.SEVERE, "Error in mHandler", ex);
            }
        }
    };

    private WorkerService.WorkerServiceBinder mWorkerServiceBinder = null;
    private boolean mIsWorkerServiceBound = false;
    private boolean mIsWorkerServiceStarted = WorkerService.isRunning;

    interface WorkerServiceConnectionTask {
        void func();
    }

    private WorkerServiceConnectionTask mWorkerServiceConnectionPendingTask = null;

    private final ServiceConnection mWorkerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mWorkerServiceBinder = (WorkerService.WorkerServiceBinder) service;
            mWorkerServiceBinder.setHandler(mHandler);
            mWorkerServiceBinder.sendSavedDataToHandler();

            if (mWorkerServiceConnectionPendingTask != null)
            {
                mWorkerServiceConnectionPendingTask.func();
                mWorkerServiceConnectionPendingTask = null;
            }

            mIsWorkerServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            unbindWorkerService();
        }
    };

    private void bindWorkerService() {
        Intent intent = new Intent(getApplicationContext(), WorkerService.class);
        bindService(intent, mWorkerServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindWorkerService() {
        mWorkerServiceBinder.removeHandler();
        mWorkerServiceBinder = null;
        mIsWorkerServiceBound = false;
        unbindService(mWorkerServiceConnection);
    }

    private void startOrBindWorkerService() {
        if (!mIsWorkerServiceStarted) {
            Intent intent = new Intent(getApplicationContext(), WorkerService.class);
            startService(intent);
            mIsWorkerServiceStarted = true;
        }

        if (!mIsWorkerServiceBound) {
            bindWorkerService();
        }
    }

    private void stopWorkerService() {
        if (mIsWorkerServiceStarted) {
            if (mIsWorkerServiceBound) {
                unbindWorkerService();
            }

            Intent intent = new Intent(this, WorkerService.class);
            stopService(intent);
            mIsWorkerServiceStarted = false;
        }
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
        for (Integer pidNum : pidNumbers)
        {
            pids[i++] = pidNum;
        }
        Arrays.sort(pids);
        // set protocol fixed PIDs
        ObdProt.setFixedPid(pids);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // instantiate superclass
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_PROGRESS);

        // get additional permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            // Storage Permissions
            final int REQUEST_EXTERNAL_STORAGE = 1;
            final String[] PERMISSIONS_STORAGE = {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
            requestPermissions(PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            // Workaround for FileUriExposedException in Android >= M
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        dlgBuilder = new AlertDialog.Builder(this);

        // get preferences
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // register for later changes
        prefs.registerOnSharedPreferenceChangeListener(this);

        // Overlay feature has to be set before window content is set
        if (prefs.getBoolean(PREF_AUTOHIDE, false)
                && prefs.getBoolean(PREF_OVERLAY, false))
        {
            getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        }

        // Set up all data adapters
        mPidAdapter = new ObdItemAdapter(this, R.layout.obd_item, ObdProt.PidPvs);
        mVidAdapter = new VidItemAdapter(this, R.layout.obd_item, ObdProt.VidPvs);
        mTidAdapter = new TidItemAdapter(this, R.layout.obd_item, ObdProt.VidPvs);
        mDfcAdapter = new DfcItemAdapter(this, R.layout.obd_item, ObdProt.tCodes);
        mPluginDataAdapter = new PluginDataAdapter(this, R.layout.obd_item, mPluginPvs);
        currDataAdapter = mPidAdapter;

        // get list view
        mListView = getWindow().getLayoutInflater().inflate(R.layout.obd_list, null);

        // update all settings from preferences
        onSharedPreferenceChanged(prefs, null);

        // set up logging system
        setupLoggers();

        // Log program startup
        log.info(String.format("%s %s starting",
                getString(R.string.app_name),
                getString(R.string.app_version)));

        // create file helper instance
        fileHelper = new FileHelper(this);
        // set up action bar
        ActionBar actionBar = getActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayShowTitleEnabled(true);
        }
        // start automatic toolbar hider
        setAutoHider(prefs.getBoolean(PREF_AUTOHIDE, false));

        // set content view
        setContentView(R.layout.startup_layout);

        // override comm medium with USB connect intent
        if ("android.hardware.usb.action.USB_DEVICE_ATTACHED".equals(getIntent().getAction()))
        {
            CommService.medium = CommService.MEDIUM.USB;
        }

        if (mIsWorkerServiceStarted){
            bindWorkerService();
        }

        switch (CommService.medium)
        {
            case BLUETOOTH:
                // Get local Bluetooth adapter
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                log.fine("Adapter: " + mBluetoothAdapter);
                // If BT is not on, request that it be enabled.
                if (getMode() != MODE.DEMO && mBluetoothAdapter != null)
                {
                    // remember initial bluetooth state
                    initialBtStateEnabled = mBluetoothAdapter.isEnabled();
                    if (!initialBtStateEnabled)
                    {
                        // request to enable bluetooth
                        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                    }
                    else
                    {
                        // last device to be auto-connected?
                        if(istRestoreWanted(PRESELECT.LAST_DEV_ADDRESS))
                        {
                            // auto-connect ...
                            setMode(MODE.ONLINE);
                        }
                        else
                        {
                            // leave "connect" action to the user
                        }
                    }
                }
                break;

            case USB:
            case NETWORK:
                setMode(MODE.ONLINE);
                break;
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

    @Override protected void onPause()
    {
        super.onPause();

        // stop data display update timer
        updateTimer.cancel();
    }

    @Override protected void onResume()
    {
        // set up data display update timer
        updateTimer = new Timer();
        final TimerTask updateTask = new TimerTask()
        {
            @Override
            public void run()
            {
                /* forward message to update the view */
                Message msg = mHandler.obtainMessage(MainActivity.MESSAGE_UPDATE_VIEW);
                mHandler.sendMessage(msg);
            }
        };
        updateTimer.schedule(updateTask, 0, DISPLAY_UPDATE_TIME);

        super.onResume();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy()
    {
        // Stop toolbar hider thread
        setAutoHider(false);

        // stop demo service if it was started
        setMode(MODE.OFFLINE);

        if (mIsWorkerServiceBound) {
            unbindWorkerService();
        }

        // if bluetooth adapter was switched OFF before ...
        if (mBluetoothAdapter != null && !initialBtStateEnabled)
        {
            // ... turn it OFF again
            mBluetoothAdapter.disable();
        }

        log.info(String.format("%s %s finished",
                getString(R.string.app_name),
                getString(R.string.app_version)));

        /* remove log file handler, if available (file access was granted) */
        if (logFileHandler != null) logFileHandler.close();
        Logger.getLogger("").removeHandler(logFileHandler);

        super.onDestroy();
    }

    @Override
    public void setContentView(int layoutResID)
    {
        setContentView(getLayoutInflater().inflate(layoutResID, null));
    }

    @Override
    public void setContentView(View view)
    {
        super.setContentView(view);
        getListView().setOnTouchListener(toolbarAutoHider);
    }

    /**
     * handle pressing of the BACK-KEY
     */
    @Override
    public void onBackPressed()
    {
        if (getListAdapter() == pluginHandler)
        {
            setObdService(obdService, null);
        } else
        {
            if (CommService.elm.getService() != ObdProt.OBD_SVC_NONE)
            {
                if (dataViewMode != DATA_VIEW_MODE.LIST)
                {
                    setDataViewMode(DATA_VIEW_MODE.LIST);
                    checkToRestoreLastDataSelection();
                } else
                {
                    setObdService(ObdProt.OBD_SVC_NONE, null);
                }
            } else
            {
                if (lastBackPressTime < System.currentTimeMillis() - EXIT_TIMEOUT)
                {
                    exitToast =
                            Toast.makeText(this, R.string.back_again_to_exit, Toast.LENGTH_SHORT);
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
    }

    /**
     * Handler for options menu creation event
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        getMenuInflater().inflate(R.menu.obd_services, menu.findItem(R.id.obd_services).getSubMenu());
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
        switch (item.getItemId())
        {
            case R.id.day_night_mode:
                // toggle night mode setting
                prefs.edit().putBoolean(NIGHT_MODE, !nightMode).apply();
                return true;

            case R.id.secure_connect_scan:
                setMode(MODE.ONLINE);
                return true;

            case R.id.reset_preselections:
                clearPreselections();
                recreate();
                return true;

            case R.id.disconnect:
                // stop communication service
                stopWorkerService();

                setMode(MODE.OFFLINE);
                return true;

            case R.id.settings:
                // Launch the BtDeviceListActivity to see devices and do scan
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivityForResult(settingsIntent, REQUEST_SETTINGS);
                return true;

            case R.id.plugin_manager:
                setManagerView();
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

            case R.id.service_testcontrol:
                setObdService(ObdProt.OBD_SVC_CTRL_MODE, item.getTitle());
                return true;

            case R.id.service_codes:
                setObdService(ObdProt.OBD_SVC_READ_CODES, item.getTitle());
                return true;

            case R.id.service_clearcodes:
                clearObdFaultCodes();
                setObdService(ObdProt.OBD_SVC_READ_CODES, item.getTitle());
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked)
    {
        // Intentionally do nothing
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu)
    {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.context_graph, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu)
    {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.chart_selected:
                setDataViewMode(DATA_VIEW_MODE.CHART);
                return true;

            case R.id.hud_selected:
                setDataViewMode(DATA_VIEW_MODE.HEADUP);
                return true;

            case R.id.dashboard_selected:
                setDataViewMode(DATA_VIEW_MODE.DASHBOARD);
                return true;

            case R.id.filter_selected:
                setDataViewMode(DATA_VIEW_MODE.FILTERED);
                return true;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode)
    {

    }

    /**
     * Handler for result messages from other activities
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        boolean secureConnection = false;

        switch (requestCode)
        {
            // device is connected
            case REQUEST_CONNECT_DEVICE_SECURE:
                secureConnection = true;
                // no break here ...
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When BtDeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK)
                {
                    // Get the device MAC address
                    String address = Objects.requireNonNull(data.getExtras()).getString(
                            BtDeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // save reported address as last setting
                    prefs.edit().putString(PRESELECT.LAST_DEV_ADDRESS.toString(), address).apply();
                    connectBtDevice(address, secureConnection);
                } else
                {
                    setMode(MODE.OFFLINE);
                }
                break;

            // USB device selected
            case REQUEST_CONNECT_DEVICE_USB:
                // DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK)
                {
                    mWorkerServiceConnectionPendingTask = () -> {
                        mWorkerServiceBinder.connectToUsb();
                    };

                    startOrBindWorkerService();
                } else
                {
                    setMode(MODE.OFFLINE);
                }
                break;

            // bluetooth enabled
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

            // file selected
            case REQUEST_SELECT_FILE:
                if (resultCode == RESULT_OK)
                {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    log.info("Load content: " + uri);
                    // load data ...
                    fileHelper.loadDataThreaded(uri, mHandler);
                    // don't allow saving it again
                    setMenuItemEnable(R.id.save, false);
                    setMenuItemEnable(R.id.obd_services, true);
                }
                break;

            // settings finished
            case REQUEST_SETTINGS:
            {
                // change handling done by callbacks
            }
            break;

            // graphical data view finished
            case REQUEST_GRAPH_DISPLAY_DONE:
                // let context know that we are in list mode again ...
                dataViewMode = DATA_VIEW_MODE.LIST;
                break;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key)
    {
        // keep main display on?
        if (key == null || KEEP_SCREEN_ON.equals(key))
        {
            getWindow().addFlags(prefs.getBoolean(KEEP_SCREEN_ON, false)
                    ? WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    : 0);
        }

        // FULL SCREEN operation based on preference settings
        if (key == null || PREF_FULLSCREEN.equals(key))
        {
            getWindow().setFlags(prefs.getBoolean(PREF_FULLSCREEN, true)
                            ? WindowManager.LayoutParams.FLAG_FULLSCREEN : 0,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        // night mode
        if (key == null || NIGHT_MODE.equals(key))
        {
            setNightMode(prefs.getBoolean(NIGHT_MODE, false));
        }

        // set default comm medium
        if (key == null || SettingsActivity.KEY_COMM_MEDIUM.equals(key))
        {
            CommService.medium =
                    CommService.MEDIUM.values()[
                            getPrefsInt(SettingsActivity.KEY_COMM_MEDIUM, 0)];
        }

        // enable/disable ELM adaptive timing
        if (key == null || ELM_ADAPTIVE_TIMING.equals(key))
        {
            CommService.elm.mAdaptiveTiming.setMode(
                    ElmProt.AdaptTimingMode.valueOf(
                            prefs.getString(ELM_ADAPTIVE_TIMING,
                                    ElmProt.AdaptTimingMode.OFF.toString())));
        }

        // set protocol flag to initiate immediate reset on NRC reception
        if (key == null || ELM_RESET_ON_NRC.equals(key))
        {
            CommService.elm.setResetOnNrc(prefs.getBoolean(ELM_RESET_ON_NRC, false));
        }

        // set custom ELM init commands
        if (key == null || ELM_CUSTOM_INIT_CMDS.equals(key))
        {
            String value = prefs.getString(ELM_CUSTOM_INIT_CMDS, null);
            if (value != null && value.length() > 0)
            {
                CommService.elm.setCustomInitCommands(value.split("\n"));
            }
        }

        // ELM timeout
        if (key == null || SettingsActivity.ELM_MIN_TIMEOUT.equals(key))
        {
            CommService.elm.mAdaptiveTiming.setElmTimeoutMin(
                    getPrefsInt(SettingsActivity.ELM_MIN_TIMEOUT,
                            CommService.elm.mAdaptiveTiming.getElmTimeoutMin()));
        }

        // ... measurement system
        if (key == null || MEASURE_SYSTEM.equals(key))
        {
            setConversionSystem(getPrefsInt(MEASURE_SYSTEM, EcuDataItem.SYSTEM_METRIC));
        }

        // ... preferred protocol
        if (key == null || SettingsActivity.KEY_PROT_SELECT.equals(key))
        {
            ElmProt.setPreferredProtocol(getPrefsInt(SettingsActivity.KEY_PROT_SELECT, 0));
        }

        // log levels
        if (key == null || LOG_MASTER.equals(key))
        {
            setLogLevels();
        }

        // update from protocol extensions
        if (key == null || key.startsWith("ext_file-"))
        {
            loadPreferredExtensions();
        }

        // set disabled ELM commands
        if (key == null || SettingsActivity.ELM_CMD_DISABLE.equals(key))
        {
            ElmProt.disableCommands(prefs.getStringSet(SettingsActivity.ELM_CMD_DISABLE, null));
        }

        // AutoHide ToolBar
        if (key == null || PREF_AUTOHIDE.equals(key) || PREF_AUTOHIDE_DELAY.equals(key))
        {
            setAutoHider(prefs.getBoolean(PREF_AUTOHIDE, false));
        }

        // Max. data disabling debounce counter
        if (key == null || PREF_DATA_DISABLE_MAX.equals(key))
        {
            EcuDataItem.MAX_ERROR_COUNT = getPrefsInt(PREF_DATA_DISABLE_MAX, 3);
        }

        // Customized PID display color preference
        if (key != null)
        {
            // specific key -> update single
            updatePidColor(key);
            updatePidDisplayRange(key);
            updatePidUpdatePeriod(key);
        }
        else
        {
            // loop through all keys
            for (String currKey : prefs.getAll().keySet())
            {
                // update by key
                updatePidColor(currKey);
                updatePidDisplayRange(currKey);
                updatePidUpdatePeriod(currKey);
            }
        }
    }

    /**
     * Update PID PV display color from preference
     * @param key Preference key
     */
    private void updatePidColor(String key)
    {
        int pos = key.indexOf("/".concat(EcuDataPv.FID_COLOR));
        if(pos >= 0)
        {
            String mnemonic = key.substring(0, pos);
            EcuDataItem itm = EcuDataItems.byMnemonic.get(mnemonic);
            // Default BLACK is to detect key removal
            Integer color = prefs.getInt(key, Color.BLACK);
            if(Color.BLACK != color)
            {
                itm.pv.put(EcuDataPv.FID_COLOR, color);
                log.info(String.format("PID pref %s=#%08x", key, color));
            }
        }
    }

    /**
     * Update PID PV display color from preference
     * @param key Preference key
     */
    private void updatePidDisplayRange(String key)
    {
        final String[] rangeFields = new String[]
        {
            EcuDataPv.FID_MIN,
            EcuDataPv.FID_MAX
        };
        // Loop through <MIN/MAX>> fields
        for (String field : rangeFields)
        {
            // If preference key matches PID/<MIN/MAX>
            int pos = key.indexOf("/".concat(field));
            if (pos >= 0)
            {
                // Default MAX_VALUE is to detect key removal
                Number value = prefs.getFloat(key, Float.MAX_VALUE);
                if (Float.MAX_VALUE != value.floatValue())
                {
                    // Find corresponding data item
                    String mnemonic = key.substring(0, pos);
                    EcuDataItem itm = EcuDataItems.byMnemonic.get(mnemonic);
                    // update display range limit in data item
                    itm.pv.put(field, value);

                    log.info(String.format("PID pref %s=%f", key, value));
                }
            }
        }
    }

    /**
     * Update customized PID display update period from preference
     * @param key Preference key
     */
    private void updatePidUpdatePeriod(String key)
    {
            // If preference key matches PID/<MIN/MAX>
            int pos = key.indexOf("/".concat(EcuDataPv.FID_UPDT_PERIOD));
            if (pos >= 0)
            {
                // Default MAX_VALUE is to detect key removal
                long value = prefs.getLong(key, 0);
                if (0 != value)
                {
                    // Find corresponding data item
                    String mnemonic = key.substring(0, pos);
                    EcuDataItem itm = EcuDataItems.byMnemonic.get(mnemonic);
                    // update display range limit in data item
                    itm.updatePeriod_ms = value;

                    log.info(String.format("PID pref %s=%f", key, value));
                }
            }
    }

    /**
     * Handle long licks on OBD data list items
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
    {
        Intent intent;
        EcuDataPv pv;

        switch (CommService.elm.getService())
        {
            /* if we are in OBD data mode:
             * ->Long click on an item starts the single item dashboard activity
             */
            case ObdProt.OBD_SVC_DATA:
                pv = (EcuDataPv) getListAdapter().getItem(position);
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
                try
                {
                    intent = new Intent(Intent.ACTION_WEB_SEARCH);
                    EcuCodeItem dfc = (EcuCodeItem) getListAdapter().getItem(position);
                    intent.putExtra(SearchManager.QUERY,
                            "OBD " + String.valueOf(dfc.get(EcuCodeItem.FID_CODE)));
                    startActivity(intent);
                } catch (Exception e)
                {
                    log.log(Level.SEVERE, "WebSearch DFC", e);
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;

            case ObdProt.OBD_SVC_VEH_INFO:
                // copy VID content to clipboard ...
                pv = (EcuDataPv) getListAdapter().getItem(position);
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(String.valueOf(pv.get(EcuDataPv.FID_DESCRIPT)),
                        String.valueOf(pv.get(EcuDataPv.FID_VALUE)));
                clipboard.setPrimaryClip(clip);
                // Show Toast message
                Toast.makeText(this, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show();
                break;

            case ObdProt.OBD_SVC_CTRL_MODE:
                pv = (EcuDataPv) getListAdapter().getItem(position);
                // Confirm & perform OBD test control ...
                confirmObdTestControl(pv.get(EcuDataPv.FID_DESCRIPT).toString(),
                        ObdProt.OBD_SVC_CTRL_MODE,
                        pv.getAsInt(EcuDataPv.FID_PID));
                break;
        }
        return true;
    }

    /**
     * Check if restore of specified preselection is wanted from settings
     *
     * @param preselect specified preselect
     * @return flag if preselection shall be restored
     */
    private boolean istRestoreWanted(PRESELECT preselect)
    {
        return prefs.getStringSet(PREF_USE_LAST, emptyStringSet).contains(preselect.toString());
        //return prefs.contains(preselect.toString());
    }

    /**
     * Check if last data selection shall be restored
     * <p>
     * If previously selected items shall be re-selected, then re-select them
     */
    private void checkToRestoreLastDataSelection()
    {
        // if last data items shall be restored
        if (istRestoreWanted(PRESELECT.LAST_ITEMS))
        {
            // get preference for last seleted items
            int[] lastSelectedItems =
                    toIntArray(prefs.getString(PRESELECT.LAST_ITEMS.toString(), ""));
            // select last selected items
            if (lastSelectedItems.length > 0)
            {
                if (!selectDataItems(lastSelectedItems))
                {
                    // if items could not be applied
                    // remove invalid preselection
                    prefs.edit().remove(PRESELECT.LAST_ITEMS.toString()).apply();
                    log.warning(String.format("Invalid preselection: %s",
                            Arrays.toString(lastSelectedItems)));
                }
            }
        }
    }

    /**
     * Check if last view mode shall be restored
     * <p>
     * If last view mode shall be restored by user settings,
     * then restore the last selected view mode
     */
    private void checkToRestoreLastViewMode()
    {
        // if last view mode shall be restored
        if (istRestoreWanted(PRESELECT.LAST_VIEW_MODE))
        {
            // set last data view mode
            DATA_VIEW_MODE lastMode =
                    DATA_VIEW_MODE.valueOf(prefs.getString(PRESELECT.LAST_VIEW_MODE.toString(),
                            DATA_VIEW_MODE.LIST.toString()));
            setDataViewMode(lastMode);
        }
    }

    /**
     * convert result of Arrays.toString(int[]) back into int[]
     *
     * @param input String of array
     * @return int[] of String value
     */
    private int[] toIntArray(String input)
    {
        int[] result = {};
        int numValidEntries = 0;
        try
        {
            String beforeSplit = input.replaceAll("\\[|]|\\s", "");
            String[] split = beforeSplit.split(",");
            int[] ints = new int[split.length];
            for (String s : split)
            {
                if (s.length() > 0)
                {
                    ints[numValidEntries++] = Integer.parseInt(s);
                }
            }
            result = Arrays.copyOf(ints, numValidEntries);
        } catch (Exception ex)
        {
            log.severe(ex.toString());
        }

        return result;
    }

    /**
     * Prompt for selection of a single ECU from list of available ECUs
     *
     * @param ecuAdresses List of available ECUs
     */
    private void selectEcu(final Set<Integer> ecuAdresses)
    {
        // if more than one ECUs available ...
        if (ecuAdresses.size() > 1)
        {
            int preferredAddress = prefs.getInt(PRESELECT.LAST_ECU_ADDRESS.toString(), 0);
            // check if last preferred address matches any of the reported addresses
            if (istRestoreWanted(PRESELECT.LAST_ECU_ADDRESS)
                    && ecuAdresses.contains(preferredAddress))
            {
                // set address
                CommService.elm.setEcuAddress(preferredAddress);
            } else
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
                                int address =
                                        Integer.parseInt(entries[which].toString().substring(2), 16);
                                // set address
                                CommService.elm.setEcuAddress(address);
                                // set this as preference (preference change will trigger ELM command)
                                prefs.edit().putInt(PRESELECT.LAST_ECU_ADDRESS.toString(), address)
                                        .apply();
                            }
                        })
                        .show();
            }
        }
    }

    /**
     * OnClick handler - Browse URL from content description
     *
     * @param view view source of click event
     */
    public void browseClickedUrl(View view)
    {
        String url = view.getContentDescription().toString();
        startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)));
    }

    /**
     * Unhide action bar
     */
    private void unHideActionBar()
    {
        if (toolbarAutoHider != null)
        {
            toolbarAutoHider.showComponent();
        }
    }

    protected void setNightMode(boolean nightMode)
    {
        // store last mode selection
        MainActivity.nightMode = nightMode;

        // Set display theme based on specified mode
        setTheme(nightMode ? R.style.AppTheme_Dark : R.style.AppTheme);
        getWindow().getDecorView().setBackgroundColor(nightMode ? Color.BLACK : Color.WHITE);

        // Trigger screen update to get immediate reaction
        setObdService(obdService, null);
    }

    private void setNumCodes(int newNumCodes)
    {
        // set list background based on MIL status
        View list = findViewById(R.id.obd_list);
        if (list != null)
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

    /**
     * start/stop the autmatic toolbar hider
     */
    private void setAutoHider(boolean active)
    {
        // disable existing hider
        if (toolbarAutoHider != null)
        {
            // cancel auto hider
            toolbarAutoHider.cancel();
            // forget about it
            toolbarAutoHider = null;
        }

        // if new hider shall be activated
        if (active)
        {
            int timeout = getPrefsInt(MainActivity.PREF_AUTOHIDE_DELAY, 15);
            toolbarAutoHider = new AutoHider(this,
                    mHandler,
                    timeout * 1000);
            // start with update resolution of 1 second
            toolbarAutoHider.start(1000);
        }
    }

    /**
     * Get preference int value
     *
     * @param key          preference key name
     * @param defaultValue numeric default value
     * @return preference int value
     */
    @SuppressLint("DefaultLocale")
    private int getPrefsInt(String key, int defaultValue)
    {
        int result = defaultValue;

        try
        {
            result = Integer.valueOf(prefs.getString(key, String.valueOf(defaultValue)));
        } catch (Exception ex)
        {
            // log error message
            log.severe(String.format("Preference '%s'(%d): %s", key, result, ex.toString()));
        }

        return result;
    }

    /**
     * get current operating mode
     */
    private MODE getMode()
    {
        return mode;
    }

    private void startBtDeviceListActivity() {
        Intent serverIntent = new Intent(this, BtDeviceListActivity.class);
        startActivityForResult(serverIntent,
                prefs.getBoolean("bt_secure_connection", false)
                        ? REQUEST_CONNECT_DEVICE_SECURE
                        : REQUEST_CONNECT_DEVICE_INSECURE);
    }

    /**
     * set new operating mode
     *
     * @param mode new mode
     */
    private void setMode(MODE mode)
    {
        // if this is a mode change, or file reload ...
        if (mode != this.mode || mode == MODE.FILE)
        {
            if (mode != MODE.DEMO)
            {
                stopDemoService();
            }

            // Disable data updates in FILE mode
            ObdItemAdapter.allowDataUpdates = (mode != MODE.FILE);

            switch (mode)
            {
                case OFFLINE:
                    // update menu item states
                    setMenuItemVisible(R.id.disconnect, false);
                    setMenuItemVisible(R.id.secure_connect_scan, true);
                    setMenuItemEnable(R.id.obd_services, false);
                    break;

                case ONLINE:
                    switch (CommService.medium)
                    {
                        case BLUETOOTH:
                            if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled())
                            {
                                Toast.makeText(this, getString(R.string.none_found), Toast.LENGTH_SHORT).show();
                                mode = MODE.OFFLINE;
                            } else
                            {
                                // if pre-settings shall be used ...
                                String address = prefs.getString(PRESELECT.LAST_DEV_ADDRESS.toString(), null);
                                if (istRestoreWanted(PRESELECT.LAST_DEV_ADDRESS)
                                        && address != null)
                                {
                                    // ... connect with previously connected device
                                    connectBtDevice(address, prefs.getBoolean("bt_secure_connection", false));
                                } else
                                {
                                    // ... otherwise launch the BtDeviceListActivity to see devices and do scan
                                    startBtDeviceListActivity();
                                }
                            }
                            break;

                        case USB:
                            Intent enableIntent = new Intent(this, UsbDeviceListActivity.class);
                            startActivityForResult(enableIntent, REQUEST_CONNECT_DEVICE_USB);
                            break;

                        case NETWORK:
                            connectNetworkDevice(prefs.getString(DEVICE_ADDRESS, null),
                                    getPrefsInt(DEVICE_PORT, 23));
                            break;
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
            // remember previous mode
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
    private void setConversionSystem(int cnvId)
    {
        log.info("Conversion: " + getResources().getStringArray(R.array.measure_options)[cnvId]);
        if (EcuDataItem.cnvSystem != cnvId)
        {
            // set coversion system
            EcuDataItem.cnvSystem = cnvId;
        }
    }

    /**
     * Set up loggers
     */
    private void setupLoggers()
    {
        // set file handler for log file output
        String logFileName = FileHelper.getPath(this).concat(File.separator).concat("log");
        try
        {
            // ensure log directory is available
            //noinspection ResultOfMethodCallIgnored
            new File(logFileName).mkdirs();
            // Create new log file handler (max. 250 MB, 5 files rotated, non appending)
            logFileHandler = new FileHandler(logFileName.concat("/AndrOBD.log.%g.txt"),
                    250 * 1024 * 1024,
                    5,
                    false);
            // Set log message formatter
            logFileHandler.setFormatter(new SimpleFormatter()
            {
                final String format = "%1$tF\t%1$tT.%1$tL\t%4$s\t%3$s\t%5$s%n";

                @SuppressLint("DefaultLocale")
                @Override
                public synchronized String format(LogRecord lr)
                {
                    return String.format(format,
                            new Date(lr.getMillis()),
                            lr.getSourceClassName(),
                            lr.getLoggerName(),
                            lr.getLevel().getName(),
                            lr.getMessage()
                    );
                }
            });
            // add file logging ...
            rootLogger.addHandler(logFileHandler);
            // set
            setLogLevels();
        } catch (IOException e)
        {
            // try to log error (at least with system logging)
            log.log(Level.SEVERE, logFileName, e);
        }
    }

    /**
     * Set logging levels from shared preferences
     */
    private void setLogLevels()
    {
        // get level from preferences
        Level level;
        try
        {
            level = Level.parse(prefs.getString(LOG_MASTER, "INFO"));
        } catch (Exception e)
        {
            level = Level.INFO;
        }

        // set logger main level
        MainActivity.rootLogger.setLevel(level);
    }

    /**
     * Load optional extension files which may have
     * been defined in preferences
     */
    private void loadPreferredExtensions()
    {
        String errors = "";

        // custom conversions
        try
        {
            String filePath = prefs.getString(SettingsActivity.extKeys[0], null);
            if (filePath != null)
            {
                log.info("Load ext. conversions: " + filePath);
                Uri uri = Uri.parse(filePath);
                InputStream inStr = getContentResolver().openInputStream(uri);
                EcuDataItems.cnv.loadFromStream(inStr);
            }
        } catch (Exception e)
        {
            log.log(Level.SEVERE, "Load ext. conversions: ", e);
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
                Uri uri = Uri.parse(filePath);
                InputStream inStr = getContentResolver().openInputStream(uri);
                ObdProt.dataItems.loadFromStream(inStr);
            }
        } catch (Exception e)
        {
            log.log(Level.SEVERE, "Load ext. PIDs: ", e);
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

            boolean allowConnect = mBluetoothAdapter != null && mBluetoothAdapter.isEnabled();
            setMenuItemVisible(R.id.secure_connect_scan, allowConnect);
            setMenuItemVisible(R.id.disconnect, !allowConnect);

            setMenuItemEnable(R.id.obd_services, true);
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
        setStatus(getString(resId));
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
            // show action bar to make state change visible
            unHideActionBar();
        }
    }

    /**
     * Select file to be loaded
     */
    private void selectFileToLoad()
    {
        File file = new File(FileHelper.getPath(this));
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uri = FileProvider.getUriForFile(MainActivity.this, getPackageName()+".provider", file);
        String type = "*/*";
        intent.setDataAndType(uri, type);
        startActivityForResult(intent, REQUEST_SELECT_FILE);
    }

    /**
     * clear all preselections
     */
    private void clearPreselections()
    {
        for (PRESELECT selection : PRESELECT.values())
        {
            prefs.edit().remove(selection.toString()).apply();
        }
    }

    /**
     * Initiate a connect to the selected bluetooth device
     *
     * @param address bluetooth device address
     * @param secure  flag to indicate if the connection shall be secure, or not
     */
    private void connectBtDevice(String address, boolean secure)
    {
        mWorkerServiceConnectionPendingTask = () -> {
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
            mWorkerServiceBinder.connectToBluetooth(device, secure);
        };

        startOrBindWorkerService();
    }

    /**
     * Initiate a connect to the selected network device
     *
     * @param address IP device address
     * @param port    IP port to connect to
     */
    private void connectNetworkDevice(String address, int port)
    {
        mWorkerServiceConnectionPendingTask = () -> {
            mWorkerServiceBinder.connectToNetwork(address, port);
        };

        startOrBindWorkerService();
    }

    /**
     * Activate desired OBD service
     *
     * @param newObdService OBD service ID to be activated
     */
    private void setObdService(int newObdService, CharSequence menuTitle)
    {
        // remember this as current OBD service
        obdService = newObdService;
        ignoreNrcs = false;

        // set list view
        setContentView(mListView);
        getListView().setOnItemLongClickListener(this);
        getListView().setMultiChoiceModeListener(this);
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

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
        // set protocol service
        CommService.elm.setService(newObdService, (getMode() != MODE.FILE && getMode() != MODE.OFFLINE));
        // show / hide freeze frame selector */
        Spinner ff_selector = findViewById(R.id.ff_selector);
        ff_selector.setOnItemSelectedListener(ff_selected);
        ff_selector.setAdapter(mDfcAdapter);
        ff_selector.setVisibility(
                newObdService == ObdProt.OBD_SVC_FREEZEFRAME ? View.VISIBLE : View.GONE);
        // set corresponding list adapter
        switch (newObdService)
        {
            case ObdProt.OBD_SVC_DATA:
                getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
                // no break here
            case ObdProt.OBD_SVC_FREEZEFRAME:
                currDataAdapter = mPidAdapter;
                break;

            case ObdProt.OBD_SVC_PENDINGCODES:
            case ObdProt.OBD_SVC_PERMACODES:
            case ObdProt.OBD_SVC_READ_CODES:
                // NOT all DFC modes are supported by all vehicles, disable NRC handling for this request
                ignoreNrcs = true;
                currDataAdapter = mDfcAdapter;
                Toast.makeText(this, getString(R.string.long_press_dfc_hint), Toast.LENGTH_LONG).show();
                break;

            case ObdProt.OBD_SVC_CTRL_MODE:
                currDataAdapter = mTidAdapter;
                break;

            case ObdProt.OBD_SVC_NONE:
                setContentView(R.layout.startup_layout);
                // intentionally no break to initialize adapter
            case ObdProt.OBD_SVC_VEH_INFO:
                currDataAdapter = mVidAdapter;
                break;
        }

        // un-filter display
        setFiltered(false);

        setListAdapter(currDataAdapter);

        // remember this as last selected service
        if (newObdService > ObdProt.OBD_SVC_NONE)
        {
            prefs.edit().putInt(PRESELECT.LAST_SERVICE.toString(), newObdService).apply();
        }
    }

    /**
     * Filter display items to just the selected ones
     */
    private void setFiltered(boolean filtered)
    {
        if (filtered)
        {
            TreeSet<Integer> selPids = new TreeSet<>();
            int[] selectedPositions = getSelectedPositions();
            for (int pos : selectedPositions)
            {
                EcuDataPv pv = (EcuDataPv) currDataAdapter.getItem(pos);
                selPids.add(pv != null ? pv.getAsInt(EcuDataPv.FID_PID) : 0);
            }
            currDataAdapter.filterPositions(selectedPositions);

            if (currDataAdapter == mPidAdapter)
                setFixedPids(selPids);
        } else
        {
            if (currDataAdapter == mPidAdapter)
                ObdProt.resetFixedPid();

            /* Return to original PV list */
            if (currDataAdapter == mPidAdapter)
            {
                currDataAdapter.setPvList(ObdProt.PidPvs);
                // append plugin measurements to data list
                currDataAdapter.addAll(mPluginPvs.values());
            } else if (currDataAdapter == mVidAdapter)
                currDataAdapter.setPvList(ObdProt.VidPvs);
            else if (currDataAdapter == mDfcAdapter)
                currDataAdapter.setPvList(ObdProt.tCodes);
            else if (currDataAdapter == mPluginDataAdapter)
                currDataAdapter.setPvList(mPluginPvs);

        }
    }

    /**
     * get the Position in model of the selected items
     *
     * @return Array of selected item positions
     */
    private int[] getSelectedPositions()
    {
        int[] selectedPositions;
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
            // trim to really detected value (workaround for invalid length reported)
            selectedPositions = Arrays.copyOf(selectedPositions, j);
        }
        String strPreselect = Arrays.toString(selectedPositions);
        log.fine("Preselection: '" + strPreselect + "'");
        // save this as last seleted positions
        prefs.edit().putString(PRESELECT.LAST_ITEMS.toString(), strPreselect)
                .apply();
        return selectedPositions;
    }

    /**
     * Set selection status on specified list item positions
     *
     * @param positions list of positions to be set
     * @return flag if selections could be applied
     */
    private boolean selectDataItems(int[] positions)
    {
        int count;
        int max;
        boolean positionsValid;

        Arrays.sort(positions);
        max = positions.length > 0 ? positions[positions.length - 1] : 0;
        count = getListAdapter().getCount();
        positionsValid = (max < count);
        // if all positions are valid for current list ...
        if (positionsValid)
        {
            // set list items as selected
            for (int i : positions)
            {
                getListView().setItemChecked(i, true);
            }
        }

        // return validity of positions
        return positionsValid;
    }

    /**
     * Handle bluetooth connection established ...
     */
    @SuppressLint("StringFormatInvalid")
    private void onConnect(boolean elmRestartRequired)
    {
        stopDemoService();

        mode = MODE.ONLINE;
        // handle further initialisations
        setMenuItemVisible(R.id.secure_connect_scan, false);
        setMenuItemVisible(R.id.disconnect, true);

        setMenuItemEnable(R.id.obd_services, true);
        // display connection status
        setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
        // send RESET to Elm adapter
        if (elmRestartRequired) {
            CommService.elm.reset();
        }
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
     * clear OBD fault codes after a warning
     * confirmation dialog is shown and the operation is confirmed
     */
    private void clearObdFaultCodes()
    {
        dlgBuilder
                .setIcon(android.R.drawable.ic_dialog_info)
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
     * confirm OBD test control
     * confirmation dialog is shown and the operation is confirmed
     */
    private void confirmObdTestControl(String testControlName, int service, int tid)
    {
        dlgBuilder
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(testControlName)
                .setMessage(R.string.obd_test_confirm)
                .setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                runObdTestControl(testControlName, service, tid);
                            }
                        })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    /**
     * perform OBD test control
     * confirmation dialog is shown and the operation is confirmed
     */
    private void runObdTestControl(String testControlName, int service, int tid)
    {
        // start desired test TID
        char emptyBuffer[] = {};
        CommService.elm.writeTelegram(emptyBuffer, service, tid);

        // Show test progress message
        dlgBuilder
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(testControlName)
                .setMessage(R.string.obd_test_progress)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                            }
                        })
                .setNegativeButton(null, null)
                .show();
    }

    /**
     * Set new data view mode
     *
     * @param dataViewMode new data view mode
     */
    private void setDataViewMode(DATA_VIEW_MODE dataViewMode)
    {
        // if this is a real change ...
        if (dataViewMode != this.dataViewMode)
        {
            log.info(String.format("Set view mode: %s -> %s", this.dataViewMode, dataViewMode));

            switch (dataViewMode)
            {
                case LIST:
                    setFiltered(false);
                    getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
                    this.dataViewMode = dataViewMode;
                    break;

                case FILTERED:
                    if (getListView().getCheckedItemCount() > 0)
                    {
                        setFiltered(true);
                        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                        this.dataViewMode = dataViewMode;
                    }
                    break;

                case HEADUP:
                case DASHBOARD:
                    if (getListView().getCheckedItemCount() > 0)
                    {
                        DashBoardActivity.setAdapter(getListAdapter());
                        Intent intent = new Intent(this, DashBoardActivity.class);
                        intent.putExtra(DashBoardActivity.POSITIONS, getSelectedPositions());
                        intent.putExtra(DashBoardActivity.RES_ID,
                                dataViewMode == DATA_VIEW_MODE.DASHBOARD
                                        ? R.layout.dashboard
                                        : R.layout.head_up);
                        startActivityForResult(intent, REQUEST_GRAPH_DISPLAY_DONE);
                        this.dataViewMode = dataViewMode;
                    }
                    break;

                case CHART:
                    if (getListView().getCheckedItemCount() > 0)
                    {
                        ChartActivity.setAdapter(getListAdapter());
                        Intent intent = new Intent(this, ChartActivity.class);
                        intent.putExtra(ChartActivity.POSITIONS, getSelectedPositions());
                        startActivityForResult(intent, REQUEST_GRAPH_DISPLAY_DONE);
                        this.dataViewMode = dataViewMode;
                    }
                    break;
            }

            // remember this as the last data view mode (if not regular list)
            if (dataViewMode != DATA_VIEW_MODE.LIST)
            {
                prefs.edit().putString(PRESELECT.LAST_VIEW_MODE.toString(), dataViewMode.toString())
                        .apply();
            }
        }
    }

    @Override
    public void onDataListUpdate(String csvString)
    {
        log.log(Level.FINE, "PluginDataList: " + csvString);
        // append unknown items to list of known items
        synchronized (mPluginPvs)
        {
            for (String csvLine : csvString.split("\n"))
            {
                String[] fields = csvLine.split(";");
                if (fields.length >= Plugin.CsvField.values().length)
                {
                    // check if PV already is known ...
                    PluginDataPv pv = (PluginDataPv) mPluginPvs.get(fields[Plugin.CsvField.MNEMONIC.ordinal()]);
                    // if not, create a new one
                    if (pv == null)
                    {
                        pv = new PluginDataPv();
                    }
                    // fill field content
                    for (Plugin.CsvField fld : Plugin.CsvField.values())
                    {
                        try
                        {
                            // if content is numeric, set numeric value
                            Double value = Double.valueOf(fields[fld.ordinal()]);
                            pv.put(csvFidMap[fld.ordinal()], value);
                        } catch (Exception ex)
                        {
                            pv.put(csvFidMap[fld.ordinal()], fields[fld.ordinal()]);
                        }
                    }
                    // add/update into pv list
                    mPluginPvs.put(pv.getKeyValue(), pv);
                }
            }
        }
    }

    @Override
    public void onDataUpdate(String key, String value)
    {
        log.log(Level.FINE, "PluginData: " + key + "=" + value);
        // Update value of plugin data item
        synchronized (mPluginPvs)
        {
            ProcessVar pv = (ProcessVar) mPluginPvs.get(key);
            if (pv != null)
            {
                try
                {
                    // if content is numeric, set numeric value
                    Double numVal = Double.valueOf(value);
                    pv.put(EcuDataPv.FIELDS[EcuDataPv.FID_VALUE], numVal);
                } catch (Exception ex)
                {
                    pv.put(EcuDataPv.FIELDS[EcuDataPv.FID_VALUE], value);
                }
            }
        }
    }

    /*
     * Implementations of PluginManager data interface callbacks
     */

    /**
     * operating modes
     */
    public enum MODE
    {
        OFFLINE,//< OFFLINE mode
        ONLINE,    //< ONLINE mode
        DEMO,    //< DEMO mode
        FILE,   //< FILE mode
    }

    /**
     * data view modes
     */
    public enum DATA_VIEW_MODE
    {
        LIST,       //< data list (un-filtered)
        FILTERED,   //< data list (filtered)
        DASHBOARD,  //< dashboard
        HEADUP,     //< Head up display
        CHART,        //< Chart display
    }

    /**
     * Preselection types
     */
    public enum PRESELECT
    {
        LAST_DEV_ADDRESS,
        LAST_ECU_ADDRESS,
        LAST_SERVICE,
        LAST_ITEMS,
        LAST_VIEW_MODE,
    }
}
