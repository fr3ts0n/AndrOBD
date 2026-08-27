package com.fr3ts0n.ecu.prot.obd;

import com.fr3ts0n.ecu.EcuDataItem;
import com.fr3ts0n.pvs.PvChangeEvent;
import com.fr3ts0n.pvs.PvChangeListener;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ElmProtTest
	implements PvChangeListener
{
	static final ElmProt prot = new ElmProt();
	static final Object[] result = new Object[1];

	@Override
	public void pvChanged(PvChangeEvent event)
	{
		result[0] = event.getValue();
	}

	@Test
	void handleTelegram_MessageCount()
	{
		prot.setService(ObdProt.OBD_SVC_VEH_INFO);
		// PID message includes optional message counter
		prot.handleTelegram("490001F0000000>".toCharArray());

		// F0000000 -> PID's 1,2,3,4 set
		assertEquals(1, prot.getNextSupportedPid());
		assertEquals(2, prot.getNextSupportedPid());
		assertEquals(3, prot.getNextSupportedPid());
		assertEquals(4, prot.getNextSupportedPid());
		// PIDs repeat again
		// assertEquals(1, prot.getNextSupportedPid());
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
		// assertEquals(1, prot.getNextSupportedPid());
	}

	/**
	 * PID message with trailing padding response bytes
	 * - either from Adapter, or from vehicle OBD?
	 * @Verifies AndrOBD/#283
	 */
	@Test
	void handleTelegram_Clio2023_Data()
	{
		prot.setService(ObdProt.OBD_SVC_DATA);
		// PID message without optional message counter
		prot.handleTelegram("4100BE3EA817AA".toCharArray());
		// ensure, trailing padding bytes are detected and cut off
		// BE3EA817 -> PID's 1,3,4,5,6,7 ... set
		assertEquals(1, prot.getNextSupportedPid());
		assertEquals(3, prot.getNextSupportedPid());
		assertEquals(4, prot.getNextSupportedPid());
		assertEquals(5, prot.getNextSupportedPid());
		assertEquals(6, prot.getNextSupportedPid());
		assertEquals(7, prot.getNextSupportedPid());
		assertEquals(11, prot.getNextSupportedPid());
		assertEquals(12, prot.getNextSupportedPid());
		assertEquals(13, prot.getNextSupportedPid());
		assertEquals(14, prot.getNextSupportedPid());
		assertEquals(15, prot.getNextSupportedPid());
		assertEquals(17, prot.getNextSupportedPid());
		assertEquals(19, prot.getNextSupportedPid());
		assertEquals(21, prot.getNextSupportedPid());
		assertEquals(28, prot.getNextSupportedPid());
		assertEquals(30, prot.getNextSupportedPid());
		assertEquals(31, prot.getNextSupportedPid());
		// PIDs repeat again
		// assertEquals(1, prot.getNextSupportedPid());
	}

	/**
	 * PID message with trailing padding response bytes
	 * - either from Adapter, or from vehicle OBD?
	 * @Verifies AndrOBD/#283
	 */
	@Test
	void handleTelegram_Clio2023_VehicleInfo()
	{
		prot.setService(ObdProt.OBD_SVC_VEH_INFO);
		// PID message without optional message counter
		prot.handleTelegram("490055430280AA".toCharArray());
		// ensure, trailing padding bytes are detected and cut off
		// 55430280 -> PID's 2,4,6,8 ... set
		assertEquals(2, prot.getNextSupportedPid());
		assertEquals(4, prot.getNextSupportedPid());
		assertEquals(6, prot.getNextSupportedPid());
		assertEquals(8, prot.getNextSupportedPid());
		assertEquals(10, prot.getNextSupportedPid());
		assertEquals(15, prot.getNextSupportedPid());
		assertEquals(16, prot.getNextSupportedPid());
		assertEquals(23, prot.getNextSupportedPid());
		assertEquals(25, prot.getNextSupportedPid());
		// PIDs repeat again
		// assertEquals(1, prot.getNextSupportedPid());
	}

	@Test
	void handleTelegram_Vin_ISO_Multiline()
	{
		EcuDataItem itm = prot.dataItems.getPidDataItems(0x09, 0x02).get(1);
		itm.pv.addPvChangeListener(this);

		prot.setService(ObdProt.OBD_SVC_VEH_INFO);

		prot.sendTelegram("0902".toCharArray());
		// PID message without optional message counter
		prot.handleTelegram("49020100000057".toCharArray());
		prot.handleTelegram("49020241555A5A".toCharArray());
		prot.handleTelegram("4902035A385034".toCharArray());
		prot.handleTelegram("49020436413030".toCharArray());
		prot.handleTelegram("49020535353434".toCharArray());
		prot.handleTelegram(">".toCharArray());
		// VIN should match expected one ...
		assertEquals("WAUZZZ8P46A005544", result[0].toString());

		itm.pv.removePvChangeListener(this);
	}

	/**
	 * VIN via multiline messages
	 * @Verifies AndrOBD/#174
	 */
	@Test
	void handleTelegram_Vin_Multiline()
	{
		EcuDataItem itm = prot.dataItems.getPidDataItems(0x09, 0x02).get(1);
		itm.pv.addPvChangeListener(this);

		prot.setService(ObdProt.OBD_SVC_VEH_INFO);

		// PID message without optional message counter
		// send VIN "0123456789ABCDEFG"
		prot.handleTelegram("014".toCharArray());
		prot.handleTelegram("1:49020130313233".toCharArray());
		prot.handleTelegram("2:343536373839".toCharArray());
		prot.handleTelegram("3:41424344454647".toCharArray());

		// VIN should match expected one ...
		assertEquals("0123456789ABCDEFG", result[0].toString());

		itm.pv.removePvChangeListener(this);
	}

	/**
	 * CAL-ID via multiline messages
	 * @Verifies AndrOBD/#174
	 */
	@Test
	void handleTelegram_CalId_Multiline()
	{
		EcuDataItem itm = prot.dataItems.getPidDataItems(0x09, 0x04).get(1);
		itm.pv.addPvChangeListener(this);

		prot.setService(ObdProt.OBD_SVC_VEH_INFO);

		// PID message without optional message counter
		// send CAL-IDs "GSPA..." without length id
		prot.handleTelegram("0:490401475350".toCharArray());
		prot.handleTelegram("1:412D3132333435".toCharArray());
		prot.handleTelegram("2:363738393030".toCharArray());
		prot.handleTelegram(">".toCharArray());

		// VIN should match expected one ...
		assertEquals("GSPA-12345678900", result[0].toString());

		itm.pv.removePvChangeListener(this);
	}

	/**
	 * Read empty DFCs with normal header (DFC count)
	 * @Verifies AndrOBD #178
	 */
	@Test
	void handleTelegram_ReadDfc_Empty()
	{
		prot.setService(ObdProt.OBD_SVC_READ_CODES);
		ObdProt.tCodes.clear();

		// PID message without optional message counter
		// send CAL-IDs "GSPA..." without length id
		prot.handleTelegram("430000000000".toCharArray());

		assertEquals(1, ObdProt.tCodes.size());
		assertEquals(true, ObdProt.tCodes.containsKey(0x0000));
	}

	/**
	 * Read empty DFCs with minimal short header
	 * @Verifies AndrOBD #178
	 */
	@Test
	void handleTelegram_ReadDfc_ShortEmpty()
	{
		prot.setService(ObdProt.OBD_SVC_READ_CODES);
		ObdProt.tCodes.clear();

		// PID message without optional message counter
		// send CAL-IDs "GSPA..." without length id
		prot.handleTelegram("43000000".toCharArray());

		assertEquals(1, ObdProt.tCodes.size());
		assertEquals(true, ObdProt.tCodes.containsKey(0x0000));
	}

	/**
	 * Read empty DFCs with ISO header (message count)and DFC count
	 * @Verifies AndrOBD #178
	 */
	@Test
	void handleTelegram_ReadDfc_ISO_Empty()
	{
		prot.setService(ObdProt.OBD_SVC_READ_CODES);
		ObdProt.tCodes.clear();

		// PID message without optional message counter
		// send CAL-IDs "GSPA..." without length id
		prot.handleTelegram("43000000000000".toCharArray());

		assertEquals(1, ObdProt.tCodes.size());
		assertEquals(true, ObdProt.tCodes.containsKey(0x0000));
	}
	/**
	 * Read 3 DFCs with DFC count
	 * @Verifies AndrOBD #178
	 */
	@Test
	void handleTelegram_ReadDfc_ISO()
	{
		prot.setService(ObdProt.OBD_SVC_READ_CODES);
		ObdProt.tCodes.clear();

		// 3 DFCs with dfc count
		prot.handleTelegram("430301230456".toCharArray());
		prot.handleTelegram("4307890000".toCharArray());

		assertEquals(3, ObdProt.tCodes.size());
		assertEquals(true, ObdProt.tCodes.containsKey(0x0123));
		assertEquals(true, ObdProt.tCodes.containsKey(0x0456));
		assertEquals(true, ObdProt.tCodes.containsKey(0x0789));
	}

	@Test
	/**
	 * Read PID support telegram with ISO header
	 * Independent of current service in ECU detection mode
	 * - ISO ECU address shall be detected
	 * - DATA (Mode 1) PID's shall be detected
	 * @Verifies AndrOBD #348
	 */
	void handleTgmReadObdData_ISO_Header()
	{
		prot.setStatus(ElmProt.STAT.ECU_DETECT);
		// OBD data pid list with ISO header bytes
		prot.handleTelegram("86F1104100BE3EB8118D".toCharArray());
		prot.setStatus(ElmProt.STAT.ECU_DETECTED);

		// ensure address 0x10 detected
		assertEquals(0x10, prot.ecuAddresses.getFirst());

		// ensure, trailing padding bytes are detected and cut off
		// BE3EB811 -> PID's 1,5,12,13,14 ... set
		assertEquals(1, prot.getNextSupportedPid());
		assertEquals(3, prot.getNextSupportedPid());
		assertEquals(4, prot.getNextSupportedPid());
		assertEquals(5, prot.getNextSupportedPid());
		assertEquals(6, prot.getNextSupportedPid());
		assertEquals(7, prot.getNextSupportedPid());
		assertEquals(11, prot.getNextSupportedPid());
		assertEquals(12, prot.getNextSupportedPid());
		assertEquals(13, prot.getNextSupportedPid());
		assertEquals(14, prot.getNextSupportedPid());
		assertEquals(15, prot.getNextSupportedPid());
		assertEquals(17, prot.getNextSupportedPid());
		assertEquals(19, prot.getNextSupportedPid());
		assertEquals(20, prot.getNextSupportedPid());
		assertEquals(21, prot.getNextSupportedPid());
		assertEquals(28, prot.getNextSupportedPid());
	}

	@Test
	/**
	 * Same ISO-header telegram as handleTgmReadObdData_ISO_Header(), but with a
	 * header type byte (0x79) whose correct checksum (0x80) exposes the old
	 * isValidIsoTelegram()/fromHex() bug: BigInteger(str,16).toByteArray() drops
	 * or adds a leading byte depending on the first hex pair's high bit, and the
	 * checksum comparison never masked to unsigned - together these silently
	 * rejected ~25% of otherwise-valid ISO telegrams as bad checksums (verified
	 * by sweeping all 256 header-type values), so the header was never stripped
	 * and PID decoding broke.
	 * @Verifies AndrOBD #348
	 */
	void handleTgmReadObdData_ISO_Header_HighBitType()
	{
		prot.setService(ObdProt.OBD_SVC_DATA);
		// same payload as handleTgmReadObdData_ISO_Header(), different header type/checksum
		prot.handleTelegram("79F1104100BE3EB81180".toCharArray());
		assertEquals(1, prot.getNextSupportedPid());
		assertEquals(3, prot.getNextSupportedPid());
		assertEquals(4, prot.getNextSupportedPid());
		assertEquals(5, prot.getNextSupportedPid());
		assertEquals(6, prot.getNextSupportedPid());
		assertEquals(7, prot.getNextSupportedPid());
		assertEquals(11, prot.getNextSupportedPid());
		assertEquals(12, prot.getNextSupportedPid());
		assertEquals(13, prot.getNextSupportedPid());
		assertEquals(14, prot.getNextSupportedPid());
		assertEquals(15, prot.getNextSupportedPid());
		assertEquals(17, prot.getNextSupportedPid());
		assertEquals(19, prot.getNextSupportedPid());
		assertEquals(20, prot.getNextSupportedPid());
		assertEquals(21, prot.getNextSupportedPid());
		assertEquals(28, prot.getNextSupportedPid());
	}

	@Test
	/**
	 * Handle NRC response in ECU detection
	 * Independent of current service in ECU detection mode
	 * - CAN ECU address shall be detected
	 * @Verifies AndrOBD-Plugin #10
	 */
	void handleTgm_EcuDetect_NRC_CAN() {
		prot.setStatus(ElmProt.STAT.ECU_DETECT);
		// test case for issue AndrOBD-Plugin/#10 (NRC22 on detect)
		prot.handleTelegram("7E8037F0122".toCharArray());
		prot.setStatus(ElmProt.STAT.ECU_DETECTED);

		// ensure CAN address 0x7E8 detected
		assertEquals(0x7E8, prot.ecuAddresses.getFirst());
	}
}