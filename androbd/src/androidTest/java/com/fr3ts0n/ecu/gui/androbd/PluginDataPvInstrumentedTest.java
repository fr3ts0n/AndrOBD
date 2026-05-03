package com.fr3ts0n.ecu.gui.androbd;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.fr3ts0n.ecu.EcuDataPv;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Android instrumented tests for {@link PluginDataPv}.
 *
 * {@code PluginDataPv} is a specialisation of {@link EcuDataPv} that:
 *  - uses the MNEMONIC field as its key attribute (instead of the default PID)
 *  - overrides {@code toString()} to return the key value string
 */
@RunWith(AndroidJUnit4.class)
public class PluginDataPvInstrumentedTest
{
    private PluginDataPv pv;

    @Before
    public void setUp()
    {
        pv = new PluginDataPv();
    }

    /** Default key attribute must be FID_MNEMONIC, not FID_PID */
    @Test
    public void keyAttribute_isMnemonic()
    {
        assertEquals(EcuDataPv.FID_MNEMONIC, pv.getKeyAttribute());
    }

    /** Key attribute must NOT be the default PID field */
    @Test
    public void keyAttribute_isNotPid()
    {
        String pid = EcuDataPv.FIELDS[EcuDataPv.FID_PID];
        assertNotNull("PID field name must not be null", pid);
        // MNEMONIC key attribute means the PV is keyed differently from default
        org.junit.Assert.assertNotEquals(pid, pv.getKeyAttribute());
    }

    /** Setting the mnemonic value and reading it back round-trips correctly */
    @Test
    public void setMnemonic_getKeyValue_roundtrip()
    {
        pv.put(EcuDataPv.FID_MNEMONIC, "RPM");
        assertEquals("RPM", pv.getKeyValue());
    }

    /** toString() returns the mnemonic string that was set */
    @Test
    public void toString_returnsMnemonic()
    {
        pv.put(EcuDataPv.FID_MNEMONIC, "ECT");
        assertEquals("ECT", pv.toString());
    }

    /** Different mnemonic values produce different toString() results */
    @Test
    public void toString_differentMnemonics_differ()
    {
        PluginDataPv pv1 = new PluginDataPv();
        PluginDataPv pv2 = new PluginDataPv();
        pv1.put(EcuDataPv.FID_MNEMONIC, "SPEED");
        pv2.put(EcuDataPv.FID_MNEMONIC, "RPM");
        org.junit.Assert.assertNotEquals(pv1.toString(), pv2.toString());
    }

    /** All standard EcuDataPv fields are present after construction */
    @Test
    public void construction_allFieldsPresent()
    {
        for (String field : EcuDataPv.FIELDS)
        {
            // containsKey returns true even for null-value entries initialised in IndexedProcessVar
            assertTrue("Field '" + field + "' should be present",
                        pv.containsKey(field));
        }
    }

    /** Stores a description value and retrieves it */
    @Test
    public void storeAndRetrieve_description()
    {
        pv.put(EcuDataPv.FIELDS[EcuDataPv.FID_DESCRIPT], "Engine Coolant Temp");
        assertEquals("Engine Coolant Temp", pv.get(EcuDataPv.FIELDS[EcuDataPv.FID_DESCRIPT]));
    }

    /** Stores a units value and retrieves it */
    @Test
    public void storeAndRetrieve_units()
    {
        pv.put(EcuDataPv.FIELDS[EcuDataPv.FID_UNITS], "°C");
        assertEquals("°C", pv.get(EcuDataPv.FIELDS[EcuDataPv.FID_UNITS]));
    }

    // Helper to avoid an import clash with JUnit 4 Assert
    private static void assertTrue(String msg, boolean condition)
    {
        org.junit.Assert.assertTrue(msg, condition);
    }
}
