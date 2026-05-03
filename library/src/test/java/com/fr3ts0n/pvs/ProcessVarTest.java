package com.fr3ts0n.pvs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for ProcessVar
 */
class ProcessVarTest
{
	private ProcessVar pv;

	@BeforeEach
	void setUp()
	{
		pv = new ProcessVar();
	}

	/** put and get basic key-value pair */
	@Test
	void putGet_basic()
	{
		pv.put("name", "Alice");
		assertEquals("Alice", pv.get("name"));
	}

	/** put returns previous value */
	@Test
	void put_returnsPreviousValue()
	{
		pv.put("key", "first");
		Object prev = pv.put("key", "second");
		assertEquals("first", prev);
		assertEquals("second", pv.get("key"));
	}

	/** remove deletes key and returns previous value */
	@Test
	void remove_keyValue()
	{
		pv.put("x", 42);
		Object removed = pv.remove("x");
		assertEquals(42, removed);
		assertNull(pv.get("x"));
	}

	/** remove on non-existent key returns null without error */
	@Test
	void remove_nonExistent()
	{
		Object result = pv.remove("does_not_exist");
		assertNull(result);
	}

	/** clear empties all keys */
	@Test
	void clear_emptiesAll()
	{
		pv.put("a", 1);
		pv.put("b", 2);
		pv.clear();
		assertTrue(pv.isEmpty());
	}

	/** default key attribute is "key" */
	@Test
	void getKeyAttribute_default()
	{
		assertEquals(ProcessVar.DEF_KEYNAME, pv.getKeyAttribute());
	}

	/** setKeyAttribute changes key attribute */
	@Test
	void setKeyAttribute_changesAttribute()
	{
		pv.setKeyAttribute("id");
		assertEquals("id", pv.getKeyAttribute());
	}

	/** setKeyValue / getKeyValue round-trip */
	@Test
	void setGetKeyValue()
	{
		pv.setKeyAttribute("id");
		pv.setKeyValue(99);
		assertEquals(99, pv.getKeyValue());
	}

	/** PvChangeListener receives PV_ADDED event on first put */
	@Test
	void addPvChangeListener_addedEvent()
	{
		AtomicInteger eventType = new AtomicInteger(0);
		pv.addPvChangeListener(event -> eventType.set(event.getType()));

		pv.put("newKey", "newValue");

		assertTrue((eventType.get() & PvChangeEvent.PV_ADDED) != 0,
			"Expected PV_ADDED event, got: " + eventType.get());
	}

	/** PvChangeListener receives PV_MODIFIED event on value change */
	@Test
	void addPvChangeListener_modifiedEvent()
	{
		pv.put("key", "original");

		AtomicInteger eventType = new AtomicInteger(0);
		pv.addPvChangeListener(event -> eventType.set(event.getType()));

		pv.put("key", "modified");

		assertTrue((eventType.get() & PvChangeEvent.PV_MODIFIED) != 0,
			"Expected PV_MODIFIED event, got: " + eventType.get());
	}

	/** PvChangeListener receives PV_DELETED event on remove */
	@Test
	void addPvChangeListener_deletedEvent()
	{
		pv.put("toDelete", "value");

		AtomicInteger eventType = new AtomicInteger(0);
		pv.addPvChangeListener(event -> eventType.set(event.getType()));

		pv.remove("toDelete");

		assertTrue((eventType.get() & PvChangeEvent.PV_DELETED) != 0,
			"Expected PV_DELETED event, got: " + eventType.get());
	}

	/** removePvChangeListener stops notifications */
	@Test
	void removePvChangeListener_stopsNotifications()
	{
		AtomicInteger callCount = new AtomicInteger(0);
		PvChangeListener listener = event -> callCount.incrementAndGet();

		pv.addPvChangeListener(listener);
		pv.put("x", 1);           // should fire
		int countAfterFirst = callCount.get();
		assertTrue(countAfterFirst > 0, "Listener should have been called");

		pv.removePvChangeListener(listener);
		pv.put("y", 2);           // should NOT fire
		assertEquals(countAfterFirst, callCount.get(), "Listener should not be called after removal");
	}

	/** listener with specific event mask only receives matching events */
	@Test
	void addPvChangeListener_withEventMask()
	{
		AtomicReference<Integer> receivedType = new AtomicReference<>(0);
		// Only listen for PV_MODIFIED events
		pv.addPvChangeListener(event -> receivedType.set(event.getType()), PvChangeEvent.PV_MODIFIED);

		// This is an ADD, should NOT trigger MODIFIED-only listener
		pv.put("brand_new", "value");
		assertEquals(0, (int) receivedType.get(), "PV_ADDED should not trigger a PV_MODIFIED listener");

		// Now modify the existing key
		pv.put("brand_new", "changed");
		assertTrue((receivedType.get() & PvChangeEvent.PV_MODIFIED) != 0,
			"PV_MODIFIED event should trigger listener");
	}

	/** toString contains class name and key value */
	@Test
	void toString_containsKeyValue()
	{
		pv.setKeyAttribute("id");
		pv.setKeyValue(77);
		String str = pv.toString();
		assertNotNull(str);
		assertTrue(str.contains("77"), "toString should contain key value");
	}

	/** construct ProcessVar from existing Map */
	@Test
	void constructFromMap()
	{
		java.util.HashMap<String, Object> map = new java.util.HashMap<>();
		map.put("a", 1);
		map.put("b", 2);
		ProcessVar pv2 = new ProcessVar(map);
		assertEquals(1, pv2.get("a"));
		assertEquals(2, pv2.get("b"));
	}

	/** construct ProcessVar from null Map doesn't throw */
	@Test
	void constructFromNullMap()
	{
		ProcessVar pv2 = new ProcessVar(null);
		assertNotNull(pv2);
		assertTrue(pv2.isEmpty());
	}

	/** firePvChanged does not fire when no listeners registered */
	@Test
	void firePvChanged_noListeners()
	{
		// Should not throw
		pv.firePvChanged(new PvChangeEvent(pv, "key", "value", PvChangeEvent.PV_MODIFIED));
	}

	/** PvChangeEvent carries correct key and value */
	@Test
	void pvChangeEvent_keyAndValue()
	{
		AtomicReference<Object> capturedKey = new AtomicReference<>();
		AtomicReference<Object> capturedValue = new AtomicReference<>();

		pv.addPvChangeListener(event -> {
			capturedKey.set(event.getKey());
			capturedValue.set(event.getValue());
		});

		pv.put("myKey", "myValue");
		assertEquals("myKey", capturedKey.get());
		assertEquals("myValue", capturedValue.get());
	}
}
