package com.fr3ts0n.pvs;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for PvLimits
 */
class PvLimitsTest
{
	/** Value within range returns same value */
	@Test
	void limitedValue_withinRange()
	{
		PvLimits limits = new PvLimits(0.0f, 100.0f);
		assertEquals(50.0f, limits.limitedValue(50.0f));
		assertEquals(0.0f, limits.limitedValue(0.0f));
		assertEquals(100.0f, limits.limitedValue(100.0f));
	}

	/** Value above max is clamped to max */
	@Test
	void limitedValue_aboveMax()
	{
		PvLimits limits = new PvLimits(0.0f, 100.0f);
		assertEquals(100.0f, limits.limitedValue(150.0f));
	}

	/** Value below min is clamped to min */
	@Test
	void limitedValue_belowMin()
	{
		PvLimits limits = new PvLimits(0.0f, 100.0f);
		assertEquals(0.0f, limits.limitedValue(-50.0f));
	}

	/** checkRange returns 0 (within) for value inside range */
	@Test
	void checkRange_withinRange()
	{
		PvLimits limits = new PvLimits(0.0f, 100.0f);
		assertEquals(0, limits.checkRange(50.0f));
	}

	/** checkRange returns 1 (above) for value above max */
	@Test
	void checkRange_aboveRange()
	{
		PvLimits limits = new PvLimits(0.0f, 100.0f);
		assertEquals(1, limits.checkRange(200.0f));
	}

	/** checkRange returns 2 (below) for value below min */
	@Test
	void checkRange_belowRange()
	{
		PvLimits limits = new PvLimits(0.0f, 100.0f);
		assertEquals(2, limits.checkRange(-10.0f));
	}

	/** Null min: no lower bound check */
	@Test
	void limitedValue_nullMin()
	{
		PvLimits limits = new PvLimits(null, 100.0f);
		assertEquals(-999.0f, limits.limitedValue(-999.0f));
		assertEquals(100.0f, limits.limitedValue(200.0f));
	}

	/** Null max: no upper bound check */
	@Test
	void limitedValue_nullMax()
	{
		PvLimits limits = new PvLimits(0.0f, null);
		assertEquals(9999.0f, limits.limitedValue(9999.0f));
		assertEquals(0.0f, limits.limitedValue(-5.0f));
	}

	/** Null min and max: no clamping at all */
	@Test
	void limitedValue_nullBoth()
	{
		PvLimits limits = new PvLimits(null, null);
		assertEquals(-999.0f, limits.limitedValue(-999.0f));
		assertEquals(9999.0f, limits.limitedValue(9999.0f));
	}

	/** Getter/setter for minValue and maxValue */
	@Test
	void getterSetter()
	{
		PvLimits limits = new PvLimits();
		limits.setMinValue(10.0f);
		limits.setMaxValue(90.0f);
		assertEquals(10.0f, limits.getMinValue());
		assertEquals(90.0f, limits.getMaxValue());
	}
}
