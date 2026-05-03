package com.fr3ts0n.ecu.gui.androbd;

import android.os.Bundle;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.fr3ts0n.androbd.plugin.Plugin;
import com.fr3ts0n.androbd.plugin.PluginInfo;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Android instrumented tests for {@link PluginInfo}.
 *
 * Covers: feature constants, constructor field population, auto-detection of
 * supported features from interface implementation, equality/hash semantics,
 * and Bundle round-trip serialisation used when passing plugin info via Intents.
 */
@RunWith(AndroidJUnit4.class)
public class PluginInfoInstrumentedTest
{
    // -------------------------------------------------------------------------
    // Helper stub classes (no Android deps – just implement the marker interfaces)
    // -------------------------------------------------------------------------

    /** Stub that supports ALL four plugin features */
    private static class AllFeaturesPlugin
            implements Plugin.ConfigurationHandler,
                       Plugin.ActionHandler,
                       Plugin.DataReceiver,
                       Plugin.DataProvider
    {
        @Override public void performConfigure() {}
        @Override public void performAction() {}
        @Override public void onDataListUpdate(String csv) {}
        @Override public void onDataUpdate(String key, String value) {}
        @Override public void sendDataList(String csv) {}
        @Override public void sendDataUpdate(String key, String value) {}
    }

    /** Stub that supports only CONFIGURE */
    private static class ConfigOnlyPlugin implements Plugin.ConfigurationHandler
    {
        @Override public void performConfigure() {}
    }

    /** Stub that supports only ACTION */
    private static class ActionOnlyPlugin implements Plugin.ActionHandler
    {
        @Override public void performAction() {}
    }

    /** Stub that supports DATA + DATAPROVISION */
    private static class DataPlugin
            implements Plugin.DataReceiver, Plugin.DataProvider
    {
        @Override public void onDataListUpdate(String csv) {}
        @Override public void onDataUpdate(String key, String value) {}
        @Override public void sendDataList(String csv) {}
        @Override public void sendDataUpdate(String key, String value) {}
    }

    /** Stub that supports no features */
    private static class NoFeaturePlugin {}

    // -------------------------------------------------------------------------
    // Feature constant definitions
    // -------------------------------------------------------------------------

    @Test
    public void featureConstant_configure_isBit0()
    {
        assertEquals(0x01, PluginInfo.FEATURE_CONFIGURE);
    }

    @Test
    public void featureConstant_action_isBit1()
    {
        assertEquals(0x02, PluginInfo.FEATURE_ACTION);
    }

    @Test
    public void featureConstant_data_isBit2()
    {
        assertEquals(0x04, PluginInfo.FEATURE_DATA);
    }

    @Test
    public void featureConstant_dataProvision_isBit3()
    {
        assertEquals(0x08, PluginInfo.FEATURE_DATAPROVISION);
    }

    @Test
    public void featureConstant_none_isZero()
    {
        assertEquals(0x00, PluginInfo.FEATURE_NONE);
    }

    @Test
    public void featureConstant_all_isCombination()
    {
        int expected = PluginInfo.FEATURE_CONFIGURE
                     | PluginInfo.FEATURE_ACTION
                     | PluginInfo.FEATURE_DATA
                     | PluginInfo.FEATURE_DATAPROVISION;
        assertEquals(expected, PluginInfo.FEATURES_ALL);
    }

    // -------------------------------------------------------------------------
    // Feature auto-detection from class interfaces
    // -------------------------------------------------------------------------

    @Test
    public void featureDetection_noFeatures()
    {
        PluginInfo info = makeInfo("NoFeature", NoFeaturePlugin.class);
        assertEquals(PluginInfo.FEATURE_NONE, info.features);
    }

    @Test
    public void featureDetection_configureOnly()
    {
        PluginInfo info = makeInfo("Config", ConfigOnlyPlugin.class);
        assertTrue("CONFIGURE bit should be set",
                (info.features & PluginInfo.FEATURE_CONFIGURE) != 0);
        assertEquals("Only CONFIGURE bit should be set",
                PluginInfo.FEATURE_CONFIGURE, info.features);
    }

    @Test
    public void featureDetection_actionOnly()
    {
        PluginInfo info = makeInfo("Action", ActionOnlyPlugin.class);
        assertTrue("ACTION bit should be set",
                (info.features & PluginInfo.FEATURE_ACTION) != 0);
        assertEquals("Only ACTION bit should be set",
                PluginInfo.FEATURE_ACTION, info.features);
    }

    @Test
    public void featureDetection_dataAndProvision()
    {
        PluginInfo info = makeInfo("Data", DataPlugin.class);
        int expected = PluginInfo.FEATURE_DATA | PluginInfo.FEATURE_DATAPROVISION;
        assertEquals(expected, info.features);
    }

    @Test
    public void featureDetection_allFeatures()
    {
        PluginInfo info = makeInfo("All", AllFeaturesPlugin.class);
        assertEquals(PluginInfo.FEATURES_ALL, info.features);
    }

    // -------------------------------------------------------------------------
    // Constructor field population
    // -------------------------------------------------------------------------

    @Test
    public void constructor_setsName()
    {
        PluginInfo info = makeInfo("TestPlugin", NoFeaturePlugin.class);
        assertEquals("TestPlugin", info.name);
    }

    @Test
    public void constructor_setsPackageName()
    {
        PluginInfo info = makeInfo("P", NoFeaturePlugin.class);
        assertEquals(NoFeaturePlugin.class.getPackage().getName(), info.packageName);
    }

    @Test
    public void constructor_setsClassName()
    {
        PluginInfo info = makeInfo("P", NoFeaturePlugin.class);
        assertEquals(NoFeaturePlugin.class.getName(), info.className);
    }

    @Test
    public void constructor_setsDescription()
    {
        PluginInfo info = new PluginInfo("n", NoFeaturePlugin.class, "desc", "copy", "lic", "url");
        assertEquals("desc", info.description);
    }

    @Test
    public void constructor_setsCopyright()
    {
        PluginInfo info = new PluginInfo("n", NoFeaturePlugin.class, "d", "© 2024", "lic", "url");
        assertEquals("© 2024", info.copyright);
    }

    @Test
    public void constructor_setsLicense()
    {
        PluginInfo info = new PluginInfo("n", NoFeaturePlugin.class, "d", "c", "GPL v3", "url");
        assertEquals("GPL v3", info.license);
    }

    @Test
    public void constructor_setsUrl()
    {
        PluginInfo info = new PluginInfo("n", NoFeaturePlugin.class, "d", "c", "l",
                                         "https://example.com");
        assertEquals("https://example.com", info.url);
    }

    @Test
    public void constructor_enabledDefaultTrue()
    {
        PluginInfo info = makeInfo("P", NoFeaturePlugin.class);
        assertTrue(info.enabled);
    }

    // -------------------------------------------------------------------------
    // toString / equals / hashCode
    // -------------------------------------------------------------------------

    @Test
    public void toString_returnsClassName()
    {
        PluginInfo info = makeInfo("P", NoFeaturePlugin.class);
        assertEquals(NoFeaturePlugin.class.getName(), info.toString());
    }

    @Test
    public void equals_sameClassName_isEqual()
    {
        PluginInfo a = makeInfo("A", NoFeaturePlugin.class);
        PluginInfo b = makeInfo("B", NoFeaturePlugin.class); // same class, different name
        assertTrue(a.equals(b));
    }

    @Test
    public void equals_differentClassName_notEqual()
    {
        PluginInfo a = makeInfo("A", NoFeaturePlugin.class);
        PluginInfo b = makeInfo("B", ConfigOnlyPlugin.class);
        assertFalse(a.equals(b));
    }

    @Test
    public void hashCode_sameClassName_sameHash()
    {
        PluginInfo a = makeInfo("A", NoFeaturePlugin.class);
        PluginInfo b = makeInfo("B", NoFeaturePlugin.class);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void hashCode_differentClassName_differentHash()
    {
        PluginInfo a = makeInfo("A", NoFeaturePlugin.class);
        PluginInfo b = makeInfo("B", ConfigOnlyPlugin.class);
        assertNotEquals(a.hashCode(), b.hashCode());
    }

    // -------------------------------------------------------------------------
    // Bundle round-trip (Android API)
    // -------------------------------------------------------------------------

    @Test
    public void bundleRoundTrip_name()
    {
        PluginInfo original = new PluginInfo("MyPlugin", AllFeaturesPlugin.class,
                                             "desc", "copy", "lic", "http://url");
        PluginInfo restored = new PluginInfo(original.toBundle());
        assertEquals(original.name, restored.name);
    }

    @Test
    public void bundleRoundTrip_className()
    {
        PluginInfo original = makeInfo("P", AllFeaturesPlugin.class);
        PluginInfo restored = new PluginInfo(original.toBundle());
        assertEquals(original.className, restored.className);
    }

    @Test
    public void bundleRoundTrip_packageName()
    {
        PluginInfo original = makeInfo("P", AllFeaturesPlugin.class);
        PluginInfo restored = new PluginInfo(original.toBundle());
        assertEquals(original.packageName, restored.packageName);
    }

    @Test
    public void bundleRoundTrip_features()
    {
        PluginInfo original = makeInfo("P", AllFeaturesPlugin.class);
        PluginInfo restored = new PluginInfo(original.toBundle());
        assertEquals(original.features, restored.features);
    }

    @Test
    public void bundleRoundTrip_description()
    {
        PluginInfo original = new PluginInfo("n", NoFeaturePlugin.class, "My desc", "c", "l", "u");
        PluginInfo restored = new PluginInfo(original.toBundle());
        assertEquals("My desc", restored.description);
    }

    @Test
    public void bundleRoundTrip_copyright()
    {
        PluginInfo original = new PluginInfo("n", NoFeaturePlugin.class, "d", "© Test", "l", "u");
        PluginInfo restored = new PluginInfo(original.toBundle());
        assertEquals("© Test", restored.copyright);
    }

    @Test
    public void bundleRoundTrip_license()
    {
        PluginInfo original = new PluginInfo("n", NoFeaturePlugin.class, "d", "c", "MIT", "u");
        PluginInfo restored = new PluginInfo(original.toBundle());
        assertEquals("MIT", restored.license);
    }

    @Test
    public void bundleRoundTrip_url()
    {
        PluginInfo original = new PluginInfo("n", NoFeaturePlugin.class, "d", "c", "l",
                                             "https://github.com");
        PluginInfo restored = new PluginInfo(original.toBundle());
        assertEquals("https://github.com", restored.url);
    }

    @Test
    public void bundleRoundTrip_toBundle_containsAllFields()
    {
        PluginInfo info = new PluginInfo("BundlePlug", AllFeaturesPlugin.class,
                                         "A plugin", "Copyright 2024", "GPLv3",
                                         "https://example.org");
        Bundle bundle = info.toBundle();

        for (PluginInfo.Field field : PluginInfo.Field.values())
        {
            assertTrue("Bundle should contain field " + field,
                       bundle.containsKey(field.toString()));
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static PluginInfo makeInfo(String name, Class<?> clazz)
    {
        return new PluginInfo(name, clazz, "desc", "copyright", "license", "http://url");
    }
}
