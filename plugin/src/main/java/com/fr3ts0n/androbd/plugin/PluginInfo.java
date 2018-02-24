package com.fr3ts0n.androbd.plugin;

import android.os.Bundle;

/**
 * Plugin information
 */

public class PluginInfo
{
    /*
     * Feature bit definitions
     */

    /** Configuration is supported */
    public static final int FEATURE_CONFIGURE       = (1 << 0);
    /** Manual action trigger is supported */
    public static final int FEATURE_ACTION          = (1 << 1);
    /** Datalist updates are supported */
    public static final int FEATURE_DATA            = (1 << 2);
    /** Data updates are supported */
    public static final int FEATURE_DATAPROVISION   = (1 << 3);
    /** NO feature supported */
    public static final int FEATURE_NONE            = 0x00;
    /** ALL features supported */
    public static final int FEATURES_ALL            = FEATURE_CONFIGURE
                                                      | FEATURE_ACTION
                                                      | FEATURE_DATA
                                                      | FEATURE_DATAPROVISION;

    public enum Field
    {
        NAME,
        CLASS,
        PACKAGE,
        FEATURES,
        DESCRIPTION,
        COPYRIGHT,
        LICENSE,
        URL,
    }

    /** Plugin name */
    public String  name;
    /** Plugin package name */
    public String  packageName;
    /** Plugin class name */
    public String  className;
    /** Bit-masked feature indication */
    public int     features = FEATURE_NONE;
    /** Description of plugin */
    public String  description;
    /** copyright notice */
    public String  copyright;
    /** license information */
    public String  license;
    /** URL */
    public String url;
    /** enabled */
    public boolean enabled = true;

    public PluginInfo( String _name,
                       Class  _class,
                       String _description,
                       String _copyright,
                       String _license,
                       String _url)
    {
        name        = _name;
        packageName = _class.getPackage().getName();
        className   = _class.getName();
        description = _description;
        copyright   = _copyright;
        license     = _license;
        url         = _url;

        // calculate features based on implemented interfaces
        if(Plugin.ConfigurationHandler.class.isAssignableFrom(_class))
            features |= FEATURE_CONFIGURE;
        if(Plugin.ActionHandler.class.isAssignableFrom(_class))
            features |= FEATURE_ACTION;
        if(Plugin.DataReceiver.class.isAssignableFrom(_class))
            features |= FEATURE_DATA;
    }

    /**
     * Construct PluginInfo from Bundle data
     * @param bundle bundle containing Plugin info data
     */
    public PluginInfo(Bundle bundle)
    {
        name = bundle.getString(Field.NAME.toString());
        packageName = bundle.getString(Field.PACKAGE.toString());
        className = bundle.getString(Field.CLASS.toString());
        features = bundle.getInt(Field.FEATURES.toString(),0);
        description = bundle.getString(Field.DESCRIPTION.toString());
        copyright = bundle.getString(Field.COPYRIGHT.toString());
        license = bundle.getString(Field.LICENSE.toString());
        url = bundle.getString(Field.URL.toString());
    }

    /**
     * get PluginInfo as Bundle
     * @return @ref Bundle of plugin info data
     */
    public Bundle toBundle()
    {
        Bundle bundle = new Bundle();
        bundle.putString(Field.NAME.toString(), name);
        bundle.putString(Field.PACKAGE.toString(), packageName);
        bundle.putString(Field.CLASS.toString(), className);
        bundle.putInt(Field.FEATURES.toString(), features);
        bundle.putString(Field.DESCRIPTION.toString(), description);
        bundle.putString(Field.COPYRIGHT.toString(), copyright);
        bundle.putString(Field.LICENSE.toString(), license);
        bundle.putString(Field.URL.toString(), url);

        return bundle;
    }

    public String toString()
    {
        return className;
    }
}
