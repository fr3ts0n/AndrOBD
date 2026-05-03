package com.fr3ts0n.ecu.gui.androbd;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.fr3ts0n.androbd.plugin.Plugin;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Android instrumented tests for the {@link Plugin} framework constants.
 *
 * Verifies that the ACTIONS and CATEGORIES string constants and the
 * {@link Plugin.CsvField} enum used across the whole plugin communication
 * protocol have the expected, stable values that plugins and the host
 * application both depend on.
 */
@RunWith(AndroidJUnit4.class)
public class PluginConstantsInstrumentedTest
{
    // -------------------------------------------------------------------------
    // Action constants (used as Intent actions)
    // -------------------------------------------------------------------------

    @Test
    public void action_identify_hasExpectedValue()
    {
        assertEquals("com.fr3ts0n.androbd.plugin.IDENTIFY", Plugin.IDENTIFY);
    }

    @Test
    public void action_configure_hasExpectedValue()
    {
        assertEquals("com.fr3ts0n.androbd.plugin.CONFIGURE", Plugin.CONFIGURE);
    }

    @Test
    public void action_action_hasExpectedValue()
    {
        assertEquals("com.fr3ts0n.androbd.plugin.ACTION", Plugin.ACTION);
    }

    @Test
    public void action_datalist_hasExpectedValue()
    {
        assertEquals("com.fr3ts0n.androbd.plugin.DATALIST", Plugin.DATALIST);
    }

    @Test
    public void action_data_hasExpectedValue()
    {
        assertEquals("com.fr3ts0n.androbd.plugin.DATA", Plugin.DATA);
    }

    // -------------------------------------------------------------------------
    // Category constants
    // -------------------------------------------------------------------------

    @Test
    public void category_request_hasExpectedValue()
    {
        assertEquals("com.fr3ts0n.androbd.plugin.REQUEST", Plugin.REQUEST);
    }

    @Test
    public void category_response_hasExpectedValue()
    {
        assertEquals("com.fr3ts0n.androbd.plugin.RESPONSE", Plugin.RESPONSE);
    }

    // -------------------------------------------------------------------------
    // Extra key constant
    // -------------------------------------------------------------------------

    @Test
    public void extra_data_hasExpectedValue()
    {
        assertEquals("com.fr3ts0n.androbd.plugin.extra.DATA", Plugin.EXTRA_DATA);
    }

    // -------------------------------------------------------------------------
    // All action/category constants are non-null and non-empty
    // -------------------------------------------------------------------------

    @Test
    public void allActionConstants_nonNullNonEmpty()
    {
        String[] actions = {
            Plugin.IDENTIFY, Plugin.CONFIGURE, Plugin.ACTION,
            Plugin.DATALIST, Plugin.DATA
        };
        for (String action : actions)
        {
            assertNotNull("Action constant must not be null", action);
            assertFalse("Action constant must not be empty", action.isEmpty());
        }
    }

    @Test
    public void allCategoryConstants_nonNullNonEmpty()
    {
        String[] categories = {Plugin.REQUEST, Plugin.RESPONSE};
        for (String cat : categories)
        {
            assertNotNull("Category constant must not be null", cat);
            assertFalse("Category constant must not be empty", cat.isEmpty());
        }
    }

    // -------------------------------------------------------------------------
    // CsvField enum
    // -------------------------------------------------------------------------

    @Test
    public void csvField_hasExpectedOrdinals()
    {
        assertEquals(0, Plugin.CsvField.MNEMONIC.ordinal());
        assertEquals(1, Plugin.CsvField.DESCRIPTION.ordinal());
        assertEquals(2, Plugin.CsvField.MIN.ordinal());
        assertEquals(3, Plugin.CsvField.MAX.ordinal());
        assertEquals(4, Plugin.CsvField.UNITS.ordinal());
    }

    @Test
    public void csvField_enumValues_countFive()
    {
        assertEquals(5, Plugin.CsvField.values().length);
    }

    @Test
    public void csvField_allValuesDistinct()
    {
        Plugin.CsvField[] vals = Plugin.CsvField.values();
        for (int i = 0; i < vals.length; i++)
        {
            for (int j = i + 1; j < vals.length; j++)
            {
                assertFalse("CsvField values should be distinct",
                            vals[i] == vals[j]);
            }
        }
    }

    @Test
    public void csvField_valueOf_byName()
    {
        assertEquals(Plugin.CsvField.MNEMONIC,    Plugin.CsvField.valueOf("MNEMONIC"));
        assertEquals(Plugin.CsvField.DESCRIPTION, Plugin.CsvField.valueOf("DESCRIPTION"));
        assertEquals(Plugin.CsvField.MIN,          Plugin.CsvField.valueOf("MIN"));
        assertEquals(Plugin.CsvField.MAX,          Plugin.CsvField.valueOf("MAX"));
        assertEquals(Plugin.CsvField.UNITS,        Plugin.CsvField.valueOf("UNITS"));
    }

    // -------------------------------------------------------------------------
    // Action constants all belong to the same namespace prefix
    // -------------------------------------------------------------------------

    @Test
    public void actionConstants_shareCommonPrefix()
    {
        String prefix = "com.fr3ts0n.androbd.plugin.";
        assertTrue(Plugin.IDENTIFY.startsWith(prefix));
        assertTrue(Plugin.CONFIGURE.startsWith(prefix));
        assertTrue(Plugin.ACTION.startsWith(prefix));
        assertTrue(Plugin.DATALIST.startsWith(prefix));
        assertTrue(Plugin.DATA.startsWith(prefix));
        assertTrue(Plugin.REQUEST.startsWith(prefix));
        assertTrue(Plugin.RESPONSE.startsWith(prefix));
        assertTrue(Plugin.EXTRA_DATA.startsWith(prefix));
    }
}
