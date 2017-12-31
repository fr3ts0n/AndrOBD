package com.fr3ts0n.androbd.plugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Plugin broadcast receiver
 *
 * This starts a service based on an incoming broadcast message
 */
abstract public class PluginReceiver extends BroadcastReceiver
{
    public PluginReceiver()
    {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d(toString(), "Broadcast received: " + intent);
        intent.setClass(context, getPluginClass());
        Log.d(toString(), "Starting service: " + intent);
        context.startService(intent);
    }

    /**
     * Get class of plugin implementation
     * @return Plugin implementation class
     */
    abstract public Class getPluginClass();
}
