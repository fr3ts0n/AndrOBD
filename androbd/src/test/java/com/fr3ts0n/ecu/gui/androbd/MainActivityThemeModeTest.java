package com.fr3ts0n.ecu.gui.androbd;

import android.content.SharedPreferences;

import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import androidx.appcompat.app.AppCompatDelegate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Local JVM unit tests for {@link MainActivity#getThemeModePref} and
 * {@link MainActivity#themeModeToNightMode}, the tri-state theme preference
 * helpers introduced for issue #128. Both are pure static methods, exercised
 * here against a minimal in-memory {@link SharedPreferences} fake rather than
 * a real Android runtime.
 */
public class MainActivityThemeModeTest
{
    /** Minimal in-memory SharedPreferences, just enough for these tests. */
    private static class FakePrefs implements SharedPreferences
    {
        final Map<String, Object> values = new HashMap<>();

        @Override public Map<String, ?> getAll() { return values; }
        @Override public String getString(String key, String defValue)
        {
            return values.containsKey(key) ? (String) values.get(key) : defValue;
        }
        @Override public Set<String> getStringSet(String key, Set<String> defValues) { throw new UnsupportedOperationException(); }
        @Override public int getInt(String key, int defValue) { throw new UnsupportedOperationException(); }
        @Override public long getLong(String key, long defValue) { throw new UnsupportedOperationException(); }
        @Override public float getFloat(String key, float defValue) { throw new UnsupportedOperationException(); }
        @Override public boolean getBoolean(String key, boolean defValue)
        {
            return values.containsKey(key) ? (Boolean) values.get(key) : defValue;
        }
        @Override public boolean contains(String key) { return values.containsKey(key); }
        @Override public Editor edit() { return new FakeEditor(); }
        @Override public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) { }
        @Override public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) { }

        private class FakeEditor implements Editor
        {
            private final Map<String, Object> pending = new HashMap<>(values);
            private final Set<String> removed = new HashSet<>();

            @Override public Editor putString(String key, String value) { pending.put(key, value); return this; }
            @Override public Editor putStringSet(String key, Set<String> values) { throw new UnsupportedOperationException(); }
            @Override public Editor putInt(String key, int value) { throw new UnsupportedOperationException(); }
            @Override public Editor putLong(String key, long value) { throw new UnsupportedOperationException(); }
            @Override public Editor putFloat(String key, float value) { throw new UnsupportedOperationException(); }
            @Override public Editor putBoolean(String key, boolean value) { pending.put(key, value); return this; }
            @Override public Editor remove(String key) { removed.add(key); pending.remove(key); return this; }
            @Override public Editor clear() { pending.clear(); return this; }
            @Override public boolean commit() { apply(); return true; }
            @Override public void apply()
            {
                for (String key : removed) { values.remove(key); }
                values.putAll(pending);
            }
        }
    }

    @Test
    public void getThemeModePref_defaultsToSystem_whenNothingStored()
    {
        assertEquals("system", MainActivity.getThemeModePref(new FakePrefs()));
    }

    @Test
    public void getThemeModePref_readsExistingThemeModeValue()
    {
        FakePrefs prefs = new FakePrefs();
        prefs.values.put("theme_mode", "dark");
        assertEquals("dark", MainActivity.getThemeModePref(prefs));
    }

    @Test
    public void getThemeModePref_migratesLegacyTrueToDark()
    {
        FakePrefs prefs = new FakePrefs();
        prefs.values.put("night_mode", true);
        assertEquals("dark", MainActivity.getThemeModePref(prefs));
        assertEquals("dark", prefs.values.get("theme_mode"));
        assertFalse("legacy key should be removed after migration", prefs.contains("night_mode"));
    }

    @Test
    public void getThemeModePref_migratesLegacyFalseToLight()
    {
        FakePrefs prefs = new FakePrefs();
        prefs.values.put("night_mode", false);
        assertEquals("light", MainActivity.getThemeModePref(prefs));
        assertEquals("light", prefs.values.get("theme_mode"));
        assertFalse("legacy key should be removed after migration", prefs.contains("night_mode"));
    }

    @Test
    public void getThemeModePref_newKeyTakesPriorityOverLegacyKey()
    {
        FakePrefs prefs = new FakePrefs();
        prefs.values.put("night_mode", true);
        prefs.values.put("theme_mode", "light");
        assertEquals("light", MainActivity.getThemeModePref(prefs));
    }

    @Test
    public void themeModeToNightMode_mapsAllThreeStates()
    {
        assertEquals(AppCompatDelegate.MODE_NIGHT_YES, MainActivity.themeModeToNightMode("dark"));
        assertEquals(AppCompatDelegate.MODE_NIGHT_NO, MainActivity.themeModeToNightMode("light"));
        assertEquals(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, MainActivity.themeModeToNightMode("system"));
    }

    @Test
    public void themeModeToNightMode_unknownValueFallsBackToSystem()
    {
        assertEquals(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, MainActivity.themeModeToNightMode("anything-else"));
    }
}
