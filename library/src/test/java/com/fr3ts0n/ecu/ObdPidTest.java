package com.fr3ts0n.ecu;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for ObdPid
 */
class ObdPidTest
{
	/** intValue returns the PID code */
	@Test
	void intValue()
	{
		ObdPid pid = new ObdPid(0x05);
		assertEquals(0x05, pid.intValue());
	}

	/** longValue returns the PID code */
	@Test
	void longValue()
	{
		ObdPid pid = new ObdPid(0x0C);
		assertEquals(0x0CL, pid.longValue());
	}

	/** floatValue returns the PID code */
	@Test
	void floatValue()
	{
		ObdPid pid = new ObdPid(0x0D);
		assertEquals(0x0D, (int) pid.floatValue());
	}

	/** doubleValue returns the PID code */
	@Test
	void doubleValue()
	{
		ObdPid pid = new ObdPid(0x0E);
		assertEquals(0x0E, (int) pid.doubleValue());
	}

	/** toString returns hex string of PID */
	@Test
	void toString_hexRepresentation()
	{
		ObdPid pid = new ObdPid(255);
		assertEquals("ff", pid.toString());
	}

	/** nextRequest getter and setter */
	@Test
	void setGetNextRequest()
	{
		ObdPid pid = new ObdPid(0x01);
		pid.setNextRequest(12345L);
		assertEquals(12345L, pid.getNextRequest());
	}

	/** requestSorter orders by nextRequest time */
	@Test
	void requestSorter_sortsByTime()
	{
		ObdPid p1 = new ObdPid(0x01);
		ObdPid p2 = new ObdPid(0x02);
		ObdPid p3 = new ObdPid(0x03);

		p1.setNextRequest(300L);
		p2.setNextRequest(100L);
		p3.setNextRequest(200L);

		List<ObdPid> list = new ArrayList<>();
		list.add(p1);
		list.add(p2);
		list.add(p3);
		list.sort(ObdPid.requestSorter);

		assertEquals(0x02, list.get(0).intValue()); // p2 has smallest nextRequest
		assertEquals(0x03, list.get(1).intValue());
		assertEquals(0x01, list.get(2).intValue()); // p1 has largest nextRequest
	}

	/** requestSorter treats 0 as earliest */
	@Test
	void requestSorter_zeroIsEarliest()
	{
		ObdPid early = new ObdPid(0x10);
		ObdPid later = new ObdPid(0x11);
		early.setNextRequest(0L);
		later.setNextRequest(1000L);

		assertTrue(ObdPid.requestSorter.compare(early, later) < 0,
			"PID with nextRequest=0 should sort before PID with nextRequest=1000");
	}
}
