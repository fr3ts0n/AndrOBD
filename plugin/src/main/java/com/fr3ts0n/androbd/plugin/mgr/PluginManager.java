package com.fr3ts0n.androbd.plugin.mgr;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

import com.fr3ts0n.androbd.plugin.R;

/**
 * Plugin manager
 *
 * This class visually handles a list of detected plugins:
 * - Show list if identified plugins
 * - Allow Enabling/Disabling plugin usage
 * - Allow trigger configuration of individual plugin
 * - Allow manual triggering plugin action
 */
public class PluginManager extends ListActivity
{
    public static PluginHandler pluginHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(pluginHandler == null) pluginHandler = new PluginHandler(this);
    }

    @Override
    protected void onDestroy()
    {
        pluginHandler.closeAllPlugins();
        pluginHandler.cleanup();
        pluginHandler = null;
        super.onDestroy();
    }

    /**
     * Set Plugin manager view
     */
    protected void setManagerView()
    {
        setContentView(R.layout.content_main);
        setListAdapter(pluginHandler);
    }

    public void sendIdentify(View view)
    {
        pluginHandler.clear();
        pluginHandler.identifyPlugins();
    }

    public void sendConfigure(View view)
    {
        int pos = getListView().getPositionForView(view);
        pluginHandler.triggerConfiguration(pos);
    }

    public void sendPerformAction(View view)
    {
        int pos = getListView().getPositionForView(view);
        pluginHandler.triggerAction(pos);
    }

    public void setPluginEnabled(View view)
    {
        int pos = getListView().getPositionForView(view);
        pluginHandler.setPluginEnabled(pos, ((Switch) view).isChecked());
    }
}
