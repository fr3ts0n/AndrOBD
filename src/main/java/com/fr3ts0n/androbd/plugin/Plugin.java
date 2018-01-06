package com.fr3ts0n.androbd.plugin;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;


/**
 * Abstract AndrOBD plugin
 */
public abstract class Plugin
    extends Service
{
    /** ACTIONS */
    public static final String IDENTIFY    = "com.fr3ts0n.androbd.plugin.IDENTIFY";
    public static final String CONFIGURE   = "com.fr3ts0n.androbd.plugin.CONFIGURE";
    public static final String ACTION      = "com.fr3ts0n.androbd.plugin.ACTION";
    public static final String DATALIST    = "com.fr3ts0n.androbd.plugin.DATALIST";
    public static final String DATA        = "com.fr3ts0n.androbd.plugin.DATA";

    /** CATEGORIES */
    public static final String REQUEST     = "com.fr3ts0n.androbd.plugin.REQUEST";
    public static final String RESPONSE    = "com.fr3ts0n.androbd.plugin.RESPONSE";

    /** Parameters for DATALIST / DATA (content will be csv encoded) */
    public static final String EXTRA_DATA  = "com.fr3ts0n.androbd.plugin.extra.DATA";

    /**
     * Plugin supports configuration requests
     */
    public interface ConfigurationHandler
    {
        /**
         * Handle configuration request.
         * Perform plugin configuration
         */
        void performConfigure();
    }

    /**
     * Plugin supports Action requests
     */
    public interface ActionHandler
    {
        /**
         * Perform intended action of the plugin
         */
        void performAction();
    }

    /**
     * Plugin supports datalist / data requests
     */
    public interface DataReceiver
    {
        /**
         * Handle data list update.
         *
         * @param csvString
         * CSV data string in format key;description;value;units.
         * One line per data item
         */
        void onDataListUpdate(String csvString);

        /**
         * Handle data update.
         * @param key Key of data change
         * @param value New value of data change
         */
        void onDataUpdate(String key, String value);
    }

    /**
     * Plugin supports data provision interface
     */
    public interface DataProvider
    {
        /**
         * Send data item list to all enabled plugins which support DATALIST requests
         *
         * @param csvData CSV encoded data list
         *                mnemonic;description;value;units
         *                ...
         *                mnemonic;description;value;units
         */
        void sendDataList(String csvData);

        /**
         * Send data update to all enabled plugins which support DATA requests
         *
         * @param key Key of data change
         * @param value New value of data change
         */
        void sendDataUpdate(String key, String value);
    }

    /**
     * Host application info
     */
    PluginInfo hostInfo;

    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    /**
     * Start plugin service
     *
     * @param intent Intent to start sevice on
     * @param flags Additional flags for service
     * @param startId Start ID for service
     *
     * @return Service continuation flags
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        int result = super.onStartCommand(intent, flags, startId);
        if (intent != null)
        {
            final String action = intent.getAction();
            if(IDENTIFY.equals(action))
            {
                Log.d(toString(), "Identify: " +  intent);
                handleIdentify(getApplicationContext(),intent);
            }

            if (ConfigurationHandler.class.isInstance(this) && CONFIGURE.equals(action))
            {
                Log.d(toString(), "Configuration: " +  intent);
                ((ConfigurationHandler)this).performConfigure( );
            }

            if (ActionHandler.class.isInstance(this) && ACTION.equals(action))
            {
                Log.d(toString(), "Action: " + intent);
                ((ActionHandler)this).performAction( );
            }

            if(DataReceiver.class.isInstance(this) && DATALIST.equals(action))
            {
                Log.v(toString(), "Data list update: " + intent);
                String dataStr = intent.getStringExtra(EXTRA_DATA);
                ((DataReceiver)this).onDataListUpdate( dataStr );
            }

            if(DataReceiver.class.isInstance(this) &&  DATA.equals(action))
            {
                Log.v(toString(), "Data update: " + intent);
                String dataStr = intent.getStringExtra(EXTRA_DATA);
                String params[] = dataStr.split("=");
                ((DataReceiver)this).onDataUpdate( params[0], params[1] );
            }
        }

        return result;
    }

    /**
     * Handle IDENTIFY intent
     *
     * @param context Context of intent handler
     * @param intent Intent object of identify request
     */
    private void handleIdentify(Context context, Intent intent)
    {
        // remember broadcasting host application
        hostInfo = new PluginInfo( intent.getExtras());

        // create identify response to broadcast origin
        Intent identifyIntent = new Intent(IDENTIFY);
        identifyIntent.addCategory(RESPONSE);
        identifyIntent.putExtras(getPluginInfo().toBundle());
        Log.d(toString(), "Sending response: " + identifyIntent);
        sendBroadcast(identifyIntent);
    }
    
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
    
    /**
     * get own plugin info
     */
    abstract public PluginInfo getPluginInfo();
}
