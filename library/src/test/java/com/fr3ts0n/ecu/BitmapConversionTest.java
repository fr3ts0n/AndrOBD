package com.fr3ts0n.ecu;

import com.fr3ts0n.ecu.prot.obd.Messages;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for BitmapConversion
 */
class BitmapConversionTest
{
	@BeforeAll
	static void initMessages()
	{
		Messages.init("com.fr3ts0n.ecu.prot.obd.res.messages");
	}

	/** Helper: create a bitmap conversion from a 2-bit map */
	private static BitmapConversion makeTwoBitConversion()
	{
		TreeMap<Long, String> data = new TreeMap<>();
		// Bit 0 → key 1 (1<<0)
		data.put(1L, "Bit0");
		// Bit 1 → key 2 (1<<1)
		data.put(2L, "Bit1");
		return new BitmapConversion(data);
	}

	/** physToPhysFmtString shows active bits with (*) marker */
	@Test
	void physToPhysFmtString_activeBit()
	{
		BitmapConversion cnv = makeTwoBitConversion();
		// value 1 → bit 0 set
		String result = cnv.physToPhysFmtString(1L, "%d");
		assertTrue(result.contains("(*)" ), "Expected (*) marker for active bit, got: " + result);
		assertTrue(result.contains("Bit0"), "Expected Bit0 label, got: " + result);
	}

	/** physToPhysFmtString shows inactive bits with (  ) marker */
	@Test
	void physToPhysFmtString_inactiveBit()
	{
		BitmapConversion cnv = makeTwoBitConversion();
		// value 2 → bit 1 set, bit 0 clear
		String result = cnv.physToPhysFmtString(2L, "%d");
		assertTrue(result.contains("(  )"), "Expected (  ) marker for inactive bit");
		assertTrue(result.contains("Bit0"), "Expected Bit0 label");
	}

	/** physToPhysFmtString with all bits set */
	@Test
	void physToPhysFmtString_allBitsSet()
	{
		BitmapConversion cnv = makeTwoBitConversion();
		String result = cnv.physToPhysFmtString(3L, "%d");
		// Both bits should show as active
		int activeCount = 0;
		for (String line : result.split(System.lineSeparator()))
		{
			if (line.contains("(*)")) activeCount++;
		}
		assertTrue(activeCount >= 2, "Expected at least 2 active bits, got: " + activeCount);
	}

	/** physToPhysFmtString with no bit map entries returns numeric fallback */
	@Test
	void physToPhysFmtString_emptyMap()
	{
		BitmapConversion cnv = new BitmapConversion(new TreeMap<Long, String>());
		String result = cnv.physToPhysFmtString(42L, "%d");
		assertNotNull(result);
		assertFalse(result.isEmpty(), "Expected numeric fallback for empty map");
	}

	/** memToPhys is identity */
	@Test
	void memToPhys_identity()
	{
		BitmapConversion cnv = makeTwoBitConversion();
		long val = 7L;
		assertTrue(cnv.memToPhys(val).longValue() == val);
	}

	/** physToMem is identity */
	@Test
	void physToMem_identity()
	{
		BitmapConversion cnv = makeTwoBitConversion();
		long val = 3L;
		assertTrue(cnv.physToMem(val).longValue() == val);
	}

	/** Initialize from String array (bit positions) */
	@Test
	void initFromStrings()
	{
		// "0=Ready" → key = 1<<0 = 1, "1=Running" → key = 1<<1 = 2
		BitmapConversion cnv = new BitmapConversion(new String[]{"0=Ready;1=Running"});
		String result = cnv.physToPhysFmtString(1L, "%d");
		assertTrue(result.contains("Ready"), "Expected Ready label, got: " + result);
	}
}
