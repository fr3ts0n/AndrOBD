package com.fr3ts0n.pvs;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests for PvList
 */
class PvListTest
{
	/** handleData adds a new dataset keyed by its key attribute */
	@Test
	void handleData_addNewDataset()
	{
		PvList list = new PvList("id");

		Map<String, Object> data = new HashMap<>();
		data.put("id", "item1");
		data.put("value", 42);

		list.handleData(data);

		ProcessVar stored = (ProcessVar) list.get("item1");
		assertNotNull(stored, "Expected dataset with key 'item1' to be stored");
		assertEquals(42, stored.get("value"));
	}

	/** handleData updates existing dataset */
	@Test
	void handleData_updateExisting()
	{
		PvList list = new PvList("id");

		Map<String, Object> data1 = new HashMap<>();
		data1.put("id", "item1");
		data1.put("value", 10);
		list.handleData(data1);

		Map<String, Object> data2 = new HashMap<>();
		data2.put("id", "item1");
		data2.put("value", 99);
		list.handleData(data2);

		ProcessVar stored = (ProcessVar) list.get("item1");
		assertNotNull(stored);
		assertEquals(99, stored.get("value"));
	}

	/** handleData with no key attribute match does not add entry */
	@Test
	void handleData_noKeyAttributeMatch()
	{
		PvList list = new PvList("id");

		Map<String, Object> data = new HashMap<>();
		data.put("other_field", "someValue");

		list.handleData(data);

		// size is 0 since no key was found (and clear() runs in super constructor)
		assertEquals(0, list.size());
	}

	/** PvList forwards change events to registered listeners */
	@Test
	void pvList_firesChangeEvent()
	{
		PvList list = new PvList("id");
		AtomicReference<Object> eventKey = new AtomicReference<>();

		list.addPvChangeListener(event -> eventKey.set(event.getKey()));

		Map<String, Object> data = new HashMap<>();
		data.put("id", "row1");
		data.put("name", "Alice");
		list.handleData(data);

		// The list should have fired an event
		assertNotNull(eventKey.get(), "Expected a change event to be fired");
	}

	/** PvList constructor with key sets key attribute */
	@Test
	void constructor_setsKeyAttribute()
	{
		PvList list = new PvList("myKey");
		assertEquals("myKey", list.getKeyAttribute());
	}

	/** Multiple entries can be stored */
	@Test
	void handleData_multipleEntries()
	{
		PvList list = new PvList("id");

		for (int i = 0; i < 5; i++)
		{
			Map<String, Object> data = new HashMap<>();
			data.put("id", "item" + i);
			data.put("index", i);
			list.handleData(data);
		}

		assertEquals(5, list.size(), "Expected 5 entries in the PvList");
		for (int i = 0; i < 5; i++)
		{
			assertNotNull(list.get("item" + i), "Expected item" + i + " to be present");
		}
	}
}
