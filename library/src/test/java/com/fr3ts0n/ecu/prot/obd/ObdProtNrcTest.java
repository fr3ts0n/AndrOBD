package com.fr3ts0n.ecu.prot.obd;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for ObdProt.NRC enum and ObdProt service handling
 */
class ObdProtNrcTest
{
	/** NRC.get returns the correct NRC for known codes */
	@Test
	void nrcGet_knownCodes()
	{
		ObdProt.NRC nrc;

		nrc = ObdProt.NRC.get(0x10);
		assertNotNull(nrc, "Expected NRC for code 0x10");
		assertEquals(ObdProt.NRC.GR, nrc);

		nrc = ObdProt.NRC.get(0x11);
		assertNotNull(nrc);
		assertEquals(ObdProt.NRC.SNS, nrc);

		nrc = ObdProt.NRC.get(0x78);
		assertNotNull(nrc);
		assertEquals(ObdProt.NRC.RCRRP, nrc);
	}

	/** NRC.get returns null for unknown code */
	@Test
	void nrcGet_unknownCode()
	{
		ObdProt.NRC nrc = ObdProt.NRC.get(0xFF);
		assertNull(nrc, "Expected null for unknown NRC code 0xFF");
	}

	/** NRC toString formats correctly */
	@Test
	void nrcToString_format()
	{
		String str = ObdProt.NRC.GR.toString(0x01);
		assertNotNull(str);
		assertTrue(str.contains("0x10"), "Expected NRC code in toString: " + str);
	}

	/** NRC display and reaction classifiers are correct for key NRCs */
	@Test
	void nrcClassifiers_grResetReaction()
	{
		assertEquals(ObdProt.NRC.DISP.ERROR, ObdProt.NRC.GR.disp);
		assertEquals(ObdProt.NRC.REACT.RESET, ObdProt.NRC.GR.react);
	}

	@Test
	void nrcClassifiers_snsServiceNotSupported()
	{
		assertEquals(ObdProt.NRC.DISP.ERROR, ObdProt.NRC.SNS.disp);
		assertEquals(ObdProt.NRC.REACT.CANCEL, ObdProt.NRC.SNS.react);
	}

	@Test
	void nrcClassifiers_rcrrpIgnore()
	{
		// RCRRP = Response pending, should be ignored
		assertEquals(ObdProt.NRC.DISP.NOTIFY, ObdProt.NRC.RCRRP.disp);
		assertEquals(ObdProt.NRC.REACT.IGNORE, ObdProt.NRC.RCRRP.react);
	}

	/** ObdProt service constants have expected values */
	@Test
	void serviceConstants()
	{
		assertEquals(0x01, ObdProt.OBD_SVC_DATA);
		assertEquals(0x02, ObdProt.OBD_SVC_FREEZEFRAME);
		assertEquals(0x03, ObdProt.OBD_SVC_READ_CODES);
		assertEquals(0x04, ObdProt.OBD_SVC_CLEAR_CODES);
		assertEquals(0x07, ObdProt.OBD_SVC_PENDINGCODES);
		assertEquals(0x09, ObdProt.OBD_SVC_VEH_INFO);
		assertEquals(0x0A, ObdProt.OBD_SVC_PERMACODES);
	}

	/** NRC code field matches constructor argument */
	@Test
	void nrcCodeField()
	{
		for (ObdProt.NRC nrc : ObdProt.NRC.values())
		{
			assertNotNull(ObdProt.NRC.get(nrc.code),
				"NRC.get should find: " + nrc.name() + " (code=0x" + Integer.toHexString(nrc.code) + ")");
		}
	}
}
