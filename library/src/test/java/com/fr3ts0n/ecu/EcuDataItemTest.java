package com.fr3ts0n.ecu;

import com.fr3ts0n.pvs.PvChangeEvent;
import com.fr3ts0n.pvs.PvChangeListener;
import com.fr3ts0n.pvs.PvLimits;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for EcuDataItem – construction, raw/physical value conversions,
 * limit calculations, and buffer updates.
 */
class EcuDataItemTest
{
	/**
	 * Build a minimal EcuDataItem with an 8-bit unsigned value
	 * and a simple identity conversion (factor=1, divider=1, offset=0, offsetPhys=0).
	 */
	private EcuDataItem makeItem8bit()
	{
		Conversion[] cnv = new Conversion[EcuDataItem.SYSTEM_TYPES];
		cnv[EcuDataItem.SYSTEM_METRIC]   = new LinearConversion(1, 1, 0, 0, "");
		cnv[EcuDataItem.SYSTEM_IMPERIAL] = new LinearConversion(1, 1, 0, 0, "");
		return new EcuDataItem(
			0x01,  // pid
			0,     // offset
			1,     // numBytes
			0,     // bitOffset
			8,     // numBits
			0xFF,  // bitMask
			cnv,
			"%d",
			null,  // minValue – auto-calculated
			null,  // maxValue – auto-calculated
			0L,
			"Test item",
			"TEST"
		);
	}

	/**
	 * Build an EcuDataItem with an 8-bit unsigned value and
	 * physOffset=-40 (like OBD coolant temperature PID 0x05).
	 */
	private EcuDataItem makeTempItem()
	{
		Conversion[] cnv = new Conversion[EcuDataItem.SYSTEM_TYPES];
		cnv[EcuDataItem.SYSTEM_METRIC]   = new LinearConversion(1, 1, 0, -40, "°C");
		cnv[EcuDataItem.SYSTEM_IMPERIAL] = new LinearConversion(1, 1, 0, -40, "°C");
		return new EcuDataItem(
			0x05, 0, 1, 0, 8, 0xFF,
			cnv, "%.0f", null, null, 0L, "Coolant temperature", "ECT"
		);
	}

	/** rawMin is always 0 */
	@Test
	void rawMin_isZero()
	{
		EcuDataItem item = makeItem8bit();
		assertEquals(0L, item.rawMin());
	}

	/** rawMax for 8-bit item masked with 0xFF = 255 */
	@Test
	void rawMax_8bit()
	{
		EcuDataItem item = makeItem8bit();
		assertEquals(255L, item.rawMax());
	}

	/** physMin with identity conversion equals memToPhys(0) */
	@Test
	void physMin_identity()
	{
		EcuDataItem item = makeItem8bit();
		assertEquals(0.0f, item.physMin().floatValue(), 0.001f);
	}

	/** physMax with identity conversion equals memToPhys(255) */
	@Test
	void physMax_identity()
	{
		EcuDataItem item = makeItem8bit();
		assertEquals(255.0f, item.physMax().floatValue(), 0.001f);
	}

	/** physMin with temp offset conversion = 0 - 40 = -40 */
	@Test
	void physMin_withOffset()
	{
		EcuDataItem item = makeTempItem();
		assertEquals(-40.0f, item.physMin().floatValue(), 0.001f);
	}

	/** physMax with temp offset conversion = 255 - 40 = 215 */
	@Test
	void physMax_withOffset()
	{
		EcuDataItem item = makeTempItem();
		assertEquals(215.0f, item.physMax().floatValue(), 0.001f);
	}

	/** physVal converts raw value correctly */
	@Test
	void physVal_identity()
	{
		EcuDataItem item = makeItem8bit();
		assertEquals(100.0f, item.physVal(100L).floatValue(), 0.001f);
	}

	/** physVal uses current cnvSystem */
	@Test
	void physVal_metricSystem()
	{
		EcuDataItem item = makeTempItem();
		EcuDataItem.cnvSystem = EcuDataItem.SYSTEM_METRIC;
		// raw 0x10 = 16 → 16 - 40 = -24
		assertEquals(-24.0f, item.physVal(0x10).floatValue(), 0.001f);
	}

	/** rawVal is the inverse of physVal */
	@Test
	void rawVal_roundtrip()
	{
		EcuDataItem item = makeTempItem();
		EcuDataItem.cnvSystem = EcuDataItem.SYSTEM_METRIC;
		// physical -24 → raw 16
		assertEquals(16L, item.rawVal(-24.0f));
	}

	/** updatePvFomBuffer stores physical value in the PV */
	@Test
	void updatePvFomBuffer_storesValue()
	{
		EcuDataItem item = makeItem8bit();
		// Buffer byte 0 = 0x64 = 100
		item.updatePvFomBuffer(new char[]{0x64});
		Object value = item.pv.get(EcuDataPv.FID_VALUE);
		assertNotNull(value, "PV value should be set after buffer update");
		assertEquals(100.0f, ((Number) value).floatValue(), 0.001f);
	}

	/** updatePvFomBuffer returns the configured update period */
	@Test
	void updatePvFomBuffer_returnsUpdatePeriod()
	{
		Conversion[] cnv = new Conversion[EcuDataItem.SYSTEM_TYPES];
		cnv[EcuDataItem.SYSTEM_METRIC]   = new LinearConversion(1, 1, 0, 0, "");
		cnv[EcuDataItem.SYSTEM_IMPERIAL] = new LinearConversion(1, 1, 0, 0, "");
		EcuDataItem item = new EcuDataItem(
			0x01, 0, 1, 0, 8, 0xFF, cnv, "%d", null, null, 500L, "item", "MNE"
		);
		long period = item.updatePvFomBuffer(new char[]{0x01});
		assertEquals(500L, period);
	}

	/** PV is populated with expected meta fields after construction */
	@Test
	void construction_pvFieldsPopulated()
	{
		EcuDataItem item = makeItem8bit();
		assertEquals(0x01, item.pv.get(EcuDataPv.FID_PID));
		assertEquals(0, item.pv.get(EcuDataPv.FID_OFS));
		assertEquals("Test item", item.pv.get(EcuDataPv.FID_DESCRIPT));
		assertEquals("TEST", item.pv.get(EcuDataPv.FID_MNEMONIC));
	}

	/** toString produces hex PID.ofs.bitOfs format */
	@Test
	void toString_format()
	{
		EcuDataItem item = makeItem8bit();
		String str = item.toString();
		assertTrue(str.contains("01"), "Expected pid 01 in: " + str);
	}

	/** clone produces independent copy */
	@Test
	void clone_isIndependent()
	{
		EcuDataItem item = makeItem8bit();
		EcuDataItem cloned = (EcuDataItem) item.clone();
		assertNotNull(cloned);
		// Modifying original PV should not affect clone
		item.pv.put(EcuDataPv.FIELDS[EcuDataPv.FID_VALUE], 99.0f);
		Object clonedVal = cloned.pv.get(EcuDataPv.FID_VALUE);
		// Clone has its own PV object
		assertTrue(item.pv != cloned.pv, "Clone should have independent PV");
	}
}
