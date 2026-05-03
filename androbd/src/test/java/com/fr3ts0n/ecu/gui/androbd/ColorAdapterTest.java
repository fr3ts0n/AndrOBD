package com.fr3ts0n.ecu.gui.androbd;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Local JVM unit tests for {@link ColorAdapter}.
 *
 * {@link ColorAdapter#getItemColor(int)} is a pure static method that only
 * accesses the compile-time-initialized {@code colors} array, so it can be
 * exercised without an Android runtime.
 */
public class ColorAdapterTest
{
    /** colors array has the expected number of entries */
    @Test
    public void colorsArraySize()
    {
        assertEquals(19, ColorAdapter.colors.length);
    }

    /** index 0 returns the first color */
    @Test
    public void getItemColor_firstElement()
    {
        assertEquals(ColorAdapter.colors[0], ColorAdapter.getItemColor(0));
    }

    /** index 1 returns the second color */
    @Test
    public void getItemColor_secondElement()
    {
        assertEquals(ColorAdapter.colors[1], ColorAdapter.getItemColor(1));
    }

    /** last valid index returns last color */
    @Test
    public void getItemColor_lastElement()
    {
        int last = ColorAdapter.colors.length - 1;
        assertEquals(ColorAdapter.colors[last], ColorAdapter.getItemColor(last));
    }

    /** index exactly equal to colors.length wraps around to index 0 */
    @Test
    public void getItemColor_wrapsAroundAtLength()
    {
        int len = ColorAdapter.colors.length;
        assertEquals(ColorAdapter.colors[0], ColorAdapter.getItemColor(len));
    }

    /** large index wraps correctly via modulo */
    @Test
    public void getItemColor_largeIndex()
    {
        int len = ColorAdapter.colors.length;
        int large = len * 5 + 3;
        assertEquals(ColorAdapter.colors[3], ColorAdapter.getItemColor(large));
    }

    /** every color has a non-zero alpha channel (fully-opaque ARGB) */
    @Test
    public void allColors_nonZeroAlpha()
    {
        for (int color : ColorAdapter.colors)
        {
            int alpha = (color >> 24) & 0xFF;
            assertNotEquals("Expected non-zero alpha for color 0x"
                    + Integer.toHexString(color), 0, alpha);
        }
    }

    /** consecutive ids map to consecutive (distinct) colors */
    @Test
    public void getItemColor_consecutiveIdsDistinct()
    {
        // First N colors must all be distinct when N <= colors.length
        int n = ColorAdapter.colors.length;
        java.util.Set<Integer> seen = new java.util.HashSet<>();
        for (int i = 0; i < n; i++)
        {
            int c = ColorAdapter.getItemColor(i);
            assertTrue("Duplicate color at index " + i + ": 0x" + Integer.toHexString(c),
                       seen.add(c));
        }
    }
}
