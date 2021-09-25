package com.fr3ts0n.ecu.prot.obd;

import com.fr3ts0n.ecu.EcuDataItem;
import com.fr3ts0n.pvs.PvChangeEvent;
import com.fr3ts0n.pvs.PvChangeListener;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}