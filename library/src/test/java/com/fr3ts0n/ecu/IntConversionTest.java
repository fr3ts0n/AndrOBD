package com.fr3ts0n.ecu;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for IntConversion
 */
class IntConversionTest
{
	private final IntConversion cnv = new IntConversion();

	/** memToPhys is identity */
	@Test
	void memToPhys_identity()
	{
		assertEquals(0L, cnv.memToPhys(0).longValue());
		assertEquals(42L, cnv.memToPhys(42).longValue());
		assertEquals(255L, cnv.memToPhys(255).longValue());
		assertEquals(-1L, cnv.memToPhys(-1).longValue());
	}

	/** physToMem is identity */
	@Test
	void physToMem_identity()
	{
		assertEquals(0L, cnv.physToMem(0).longValue());
		assertEquals(100L, cnv.physToMem(100L).longValue());
	}

	/** physToPhysFmtString formats decimal */
	@Test
	void physToPhysFmtString_decimal()
	{
		assertEquals("42", cnv.physToPhysFmtString(42L, "%d"));
	}

	/** physToPhysFmtString formats hex */
	@Test
	void physToPhysFmtString_hex()
	{
		assertEquals("ff", cnv.physToPhysFmtString(255L, "%x"));
		assertEquals("FF", cnv.physToPhysFmtString(255L, "%X"));
	}

	/** getUnits returns empty string by default */
	@Test
	void getUnits_default()
	{
		assertEquals("", cnv.getUnits());
	}
}
