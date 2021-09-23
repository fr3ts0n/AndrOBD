package com.fr3ts0n.ecu.prot.obd;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ElmProtTest
{
	static final ElmProt prot = new ElmProt();

	@Test
	void handleTelegram_MessageCount()
	{
		prot.setService(ObdProt.OBD_SVC_VEH_INFO);
		// PID message includes optional message counter
		prot.handleTelegram("490001F0000000".toCharArray());

		// F0000000 -> PID's 1,2,3,4 set
		assertEquals(1, prot.getNextSupportedPid());
		assertEquals(2, prot.getNextSupportedPid());
		assertEquals(3, prot.getNextSupportedPid());
		assertEquals(4, prot.getNextSupportedPid());
		// PIDs repeat again
		assertEquals(1, prot.getNextSupportedPid());
	}

	@Test
	void handleTelegram_NoMessageCount()
	{
		prot.setService(ObdProt.OBD_SVC_VEH_INFO);
		// PID message without optional message counter
		prot.handleTelegram("4900A5000000".toCharArray());

		// F0000000 -> PID's 1,3,6,8 set
		assertEquals(1, prot.getNextSupportedPid());
		assertEquals(3, prot.getNextSupportedPid());
		assertEquals(6, prot.getNextSupportedPid());
		assertEquals(8, prot.getNextSupportedPid());
		// PIDs repeat again
		assertEquals(1, prot.getNextSupportedPid());
	}
}