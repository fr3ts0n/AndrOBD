package com.fr3ts0n.prot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for ProtUtils
 */
class ProtUtilsTest
{
	/** hexDumpBuffer returns non-null, non-empty result */
	@Test
	void hexDumpBuffer_notNullOrEmpty()
	{
		char[] buffer = {'A', 'B', 'C'};
		String result = ProtUtils.hexDumpBuffer(buffer);
		assertNotNull(result);
		assertTrue(result.length() > 0);
	}

	/** hexDumpBuffer formats hex bytes correctly */
	@Test
	void hexDumpBuffer_hexFormatting()
	{
		// 0x41='A', 0x42='B'
		char[] buffer = {0x41, 0x42};
		String result = ProtUtils.hexDumpBuffer(buffer);
		assertTrue(result.contains("41"), "Expected hex 41 in: " + result);
		assertTrue(result.contains("42"), "Expected hex 42 in: " + result);
	}

	/** hexDumpBuffer result contains " : " separator between hex and ASCII parts */
	@Test
	void hexDumpBuffer_containsSeparator()
	{
		char[] buffer = {'X'};
		String result = ProtUtils.hexDumpBuffer(buffer);
		assertTrue(result.contains(" : "), "Expected ' : ' separator in: " + result);
	}

	/** hexDumpBuffer replaces non-printable chars with '.' in ASCII section */
	@Test
	void hexDumpBuffer_nonPrintableReplacement()
	{
		// 0x01 is non-printable
		char[] buffer = {0x01};
		String result = ProtUtils.hexDumpBuffer(buffer);
		// ASCII part should contain '.'
		String asciiPart = result.substring(result.indexOf(" : ") + 3);
		assertEquals(".", asciiPart, "Expected '.' for non-printable char, got: " + asciiPart);
	}

	/** hexDumpBuffer ASCII section shows printable characters */
	@Test
	void hexDumpBuffer_printableChars()
	{
		char[] buffer = {'H', 'i'};
		String result = ProtUtils.hexDumpBuffer(buffer);
		String asciiPart = result.substring(result.indexOf(" : ") + 3);
		assertEquals("Hi", asciiPart, "Expected printable chars in ASCII section, got: " + asciiPart);
	}

	/** hexDumpBuffer handles empty buffer */
	@Test
	void hexDumpBuffer_emptyBuffer()
	{
		char[] buffer = {};
		String result = ProtUtils.hexDumpBuffer(buffer);
		assertNotNull(result);
		// should just be the separator
		assertEquals(" : ", result, "Expected just ' : ' for empty buffer, got: " + result);
	}

	/** hexDumpBuffer handles single byte */
	@Test
	void hexDumpBuffer_singleByte()
	{
		char[] buffer = {0x00};
		String result = ProtUtils.hexDumpBuffer(buffer);
		assertTrue(result.startsWith("00 "), "Expected hex 00 at start, got: " + result);
	}
}
