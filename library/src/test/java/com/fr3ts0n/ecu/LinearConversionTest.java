package com.fr3ts0n.ecu;

import com.fr3ts0n.pvs.PvLimits;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for LinearConversion
 */
class LinearConversionTest
{
	/** Simple identity conversion: factor=1, divider=1, offset=0, offsetPhys=0 */
	@Test
	void memToPhys_identity()
	{
		LinearConversion cnv = new LinearConversion(1, 1, 0, 0, "");
		assertEquals(0.0f, cnv.memToPhys(0).floatValue(), 0.001f);
		assertEquals(100.0f, cnv.memToPhys(100).floatValue(), 0.001f);
		assertEquals(255.0f, cnv.memToPhys(255).floatValue(), 0.001f);
	}

	/** factor=1, divider=1, offset=0, offsetPhys=-40 (e.g. coolant temperature OBD PID 0x05) */
	@Test
	void memToPhys_withPhysOffset()
	{
		LinearConversion cnv = new LinearConversion(1, 1, 0, -40, "°C");
		// raw 0x10 = 16 → 16 - 40 = -24
		assertEquals(-24.0f, cnv.memToPhys(0x10).floatValue(), 0.001f);
		// raw 0x7B = 123 → 123 - 40 = 83
		assertEquals(83.0f, cnv.memToPhys(0x7B).floatValue(), 0.001f);
	}

	/** factor=100, divider=255, offset=0, offsetPhys=0 (percentage style, 0–100%) */
	@Test
	void memToPhys_percentageLike()
	{
		LinearConversion cnv = new LinearConversion(100, 255, 0, 0, "%");
		// raw 255 → 100%
		assertEquals(100.0f, cnv.memToPhys(255).floatValue(), 0.01f);
		// raw 0 → 0%
		assertEquals(0.0f, cnv.memToPhys(0).floatValue(), 0.001f);
	}

	/** physToMem roundtrip */
	@Test
	void physToMem_roundtrip()
	{
		LinearConversion cnv = new LinearConversion(1, 1, 0, -40, "°C");
		// physical -24 → raw 16
		assertEquals(16L, cnv.physToMem(-24f).longValue());
		// physical 83 → raw 123
		assertEquals(123L, cnv.physToMem(83f).longValue());
	}

	/** physToMem with factor/divider */
	@Test
	void physToMem_withFactor()
	{
		// factor=3, divider=2, offset=0, offsetPhys=0
		LinearConversion cnv = new LinearConversion(3, 2, 0, 0, "");
		// raw 10 → 10 * 3 / 2 = 15.0
		assertEquals(15.0f, cnv.memToPhys(10).floatValue(), 0.001f);
		// physical 15.0 → raw = round(15 * 2 / 3) = 10
		assertEquals(10L, cnv.physToMem(15.0f).longValue());
	}

	/** getUnits returns configured unit string */
	@Test
	void getUnits()
	{
		LinearConversion cnv = new LinearConversion(1, 1, 0, 0, "km/h");
		assertEquals("km/h", cnv.getUnits());
	}

	/** memToPhys respects PvLimits (upper clamp) */
	@Test
	void memToPhys_withLimitsUpperClamp()
	{
		PvLimits limits = new PvLimits(0.0f, 100.0f);
		LinearConversion cnv = new LinearConversion(1, 1, 0, 0, "%", limits);
		// raw 150 would be 150, but max is 100
		assertEquals(100.0f, ((Float) cnv.memToPhys(150)), 0.001f);
	}

	/** memToPhys respects PvLimits (lower clamp) */
	@Test
	void memToPhys_withLimitsLowerClamp()
	{
		PvLimits limits = new PvLimits(0.0f, 100.0f);
		// offset -200 would give negative values for small raw inputs
		LinearConversion cnv = new LinearConversion(1, 1, 0, -200, "%", limits);
		// raw 10 → 10 - 200 = -190, clamped to 0
		assertEquals(0.0f, ((Float) cnv.memToPhys(10)), 0.001f);
	}

	/** memToPhys with raw offset (added to raw before multiplication) */
	@Test
	void memToPhys_withRawOffset()
	{
		// factor=1, divider=1, offset=-10, offsetPhys=0
		LinearConversion cnv = new LinearConversion(1, 1, -10, 0, "");
		// raw 20: (20 + (-10)) * 1 / 1 + 0 = 10
		assertEquals(10.0f, cnv.memToPhys(20).floatValue(), 0.001f);
	}
}
