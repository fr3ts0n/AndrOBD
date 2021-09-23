package com.fr3ts0n.ecu;

import com.fr3ts0n.pvs.ProcessVar;
import com.fr3ts0n.pvs.PvChangeEvent;
import com.fr3ts0n.pvs.PvChangeListener;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for dynamic conversion ranges via PID 0x4F
 * - Lambda
 * - O2 Voltage
 * - O2 Current
 *
 * Test values are described in J1979-DA PID $4F description
 */
class EcuDataItemsTest
	implements PvChangeListener
{
	static final EcuDataItems items =new EcuDataItems();
	Number resultValue = 0;

	/**
	 * Test Dynamic Lambda PID 0x24 AB
	 */
	@org.junit.jupiter.api.Test
	void updateDataItems_Lambda()
	{
		ProcessVar pv = items.getPidDataItems(0x01, 0x24).get(0).pv;
		pv.addPvChangeListener(this, PvChangeEvent.PV_MODIFIED);

		// Dynamic factor undefined: Static conversion factor
		items.updateDataItems(0x01,0x24, new char[]{0x7D,0x00,0x9C,0x40});
		assertEquals(0.976, resultValue.doubleValue(), 0.001);

		// Dynamic factor 0: conversion factor unchanged
		items.updateDataItems(0x01,0x4F, new char[]{0x00,0x00,0x00,0x00});
		items.updateDataItems(0x01,0x24, new char[]{0x7D,0x00,0x9C,0x40});
		assertEquals(0.976, resultValue.doubleValue(), 0.001);

		// Dynamic factor 4: Changed lambda factor
		items.updateDataItems(0x01,0x4F, new char[]{0x04,0x00,0x00,0x00});
		items.updateDataItems(0x01,0x24, new char[]{0x7D,0x00,0x9C,0x00});
		assertEquals(1.953, resultValue.doubleValue(),0.001);

		pv.removePvChangeListener(this);
	}

	/**
	 * Test dynamic O2 voltage PID 0x24 CD
	 */
	@org.junit.jupiter.api.Test
	void updateDataItems_O2Voltage()
	{
		ProcessVar pv = items.getPidDataItems(0x01, 0x24).get(1).pv;
		pv.addPvChangeListener(this, PvChangeEvent.PV_MODIFIED);

		// Dynamic factor undefined: Static conversion factor
		items.updateDataItems(0x01,0x24, new char[]{0x7D,0x00,0x9C,0x40});
		assertEquals(4.883, resultValue.doubleValue(), 0.01);

		// Dynamic factor 0: conversion factor unchanged
		items.updateDataItems(0x01,0x4F, new char[]{0x00,0x00,0x00,0x00});
		items.updateDataItems(0x01,0x24, new char[]{0x7D,0x00,0x9C,0x40});
		assertEquals(4.883, resultValue.doubleValue(), 0.01);

		// Dynamic factor 4: Changed o2 voltage factor
		items.updateDataItems(0x01,0x4F, new char[]{0x04,0x10,0x00,0x00});
		items.updateDataItems(0x01,0x24, new char[]{0x7D,0x00,0x9C,0x40});
		assertEquals(9.766, resultValue.doubleValue(),0.01);

		pv.removePvChangeListener(this);
	}

	/**
	 * Test dynamic O2 current PID 0x34 CD
	 */
	@org.junit.jupiter.api.Test
	void updateDataItems_O2Current()
	{
		ProcessVar pv = items.getPidDataItems(0x01, 0x34).get(1).pv;
		pv.addPvChangeListener(this, PvChangeEvent.PV_MODIFIED);

		// Dynamic factor undefined: Static conversion factor
		items.updateDataItems(0x01,0x34, new char[]{0x7D,0x00,0x5C,0x40});
		assertEquals(-35.75, resultValue.doubleValue(), 0.01);

		// Dynamic factor 0: conversion factor unchanged
		items.updateDataItems(0x01,0x4F, new char[]{0x00,0x00,0x00,0x00});
		items.updateDataItems(0x01,0x34, new char[]{0x7D,0x00,0x5C,0x40});
		assertEquals(-35.75, resultValue.doubleValue(), 0.01);

		// Dynamic factor 4: Changed o2 voltage factor
		items.updateDataItems(0x01,0x4F, new char[]{0x04,0x10,0x40,0x00});
		items.updateDataItems(0x01,0x34, new char[]{0x7D,0x00,0x5C,0x40});
		assertEquals(-17.875, resultValue.doubleValue(),0.01);

		pv.removePvChangeListener(this);
	}

	/**
	 * Handle PV change event
	 * - store changed value to test result
	 *
	 * @param event Change event
	 */
	@Override
	public void pvChanged(PvChangeEvent event)
	{
		/* Store changed value as result */
		resultValue = (Number)event.getValue();
	}
}