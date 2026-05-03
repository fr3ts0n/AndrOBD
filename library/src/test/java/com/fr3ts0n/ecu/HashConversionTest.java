package com.fr3ts0n.ecu;

import com.fr3ts0n.ecu.prot.obd.Messages;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for HashConversion
 */
class HashConversionTest
{
	@BeforeAll
	static void initMessages()
	{
		Messages.init("com.fr3ts0n.ecu.prot.obd.res.messages");
	}

	/** physToPhysFmtString returns mapped string for known key */
	@Test
	void physToPhysFmtString_knownKey()
	{
		Map<Long, String> data = new HashMap<>();
		data.put(0L, "Off");
		data.put(1L, "On");
		HashConversion cnv = new HashConversion(data);
		assertEquals("Off", cnv.physToPhysFmtString(0L, "%d"));
		assertEquals("On", cnv.physToPhysFmtString(1L, "%d"));
	}

	/** physToPhysFmtString returns "Unknown state" for unmapped key */
	@Test
	void physToPhysFmtString_unknownKey()
	{
		Map<Long, String> data = new HashMap<>();
		data.put(0L, "Off");
		HashConversion cnv = new HashConversion(data);
		String result = cnv.physToPhysFmtString(99L, "%d");
		assertTrue(result.startsWith("Unknown state:"), "Expected unknown state message, got: " + result);
	}

	/** memToPhys is identity */
	@Test
	void memToPhys_identity()
	{
		Map<Long, String> data = new HashMap<>();
		HashConversion cnv = new HashConversion(data);
		assertEquals(42L, cnv.memToPhys(42L).longValue());
	}

	/** physToMem is identity */
	@Test
	void physToMem_identity()
	{
		Map<Long, String> data = new HashMap<>();
		HashConversion cnv = new HashConversion(data);
		assertEquals(42L, cnv.physToMem(42L).longValue());
	}

	/** Initialize from String array */
	@Test
	void initFromStrings()
	{
		HashConversion cnv = new HashConversion(new String[]{"0=Off;1=On"});
		assertEquals("Off", cnv.physToPhysFmtString(0L, "%d"));
		assertEquals("On", cnv.physToPhysFmtString(1L, "%d"));
	}

	/** Initialize from multiple String array entries */
	@Test
	void initFromStrings_multipleEntries()
	{
		HashConversion cnv = new HashConversion(new String[]{"0=Idle", "1=Running", "2=Error"});
		assertEquals("Idle", cnv.physToPhysFmtString(0L, "%d"));
		assertEquals("Running", cnv.physToPhysFmtString(1L, "%d"));
		assertEquals("Error", cnv.physToPhysFmtString(2L, "%d"));
	}
}
