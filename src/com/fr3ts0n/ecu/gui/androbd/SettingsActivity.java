/*
 * (C) Copyright 2015 by fr3ts0n <erwin.scheuch-heilig@gmx.at>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA 02111-1307 USA
 *
 */

package com.fr3ts0n.ecu.gui.androbd;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class SettingsActivity
	extends Activity
{
	/** app preferences */
	static SharedPreferences prefs;
	/** preference keys for extension files */
	static final String[] extKeys =
		{
			"ext_file_conversions",
			"ext_file_dataitems",
			"ext_file_faultcodes"
		};

	/*
	 * (non-Javadoc)
	 *
	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		// Display the fragment as the main content.
		getFragmentManager().beginTransaction().replace(android.R.id.content,
			new PrefsFragment()).commit();
	}

	public static class PrefsFragment
		extends PreferenceFragment
		implements Preference.OnPreferenceClickListener
	{
		@Override
		public void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);

			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.settings);
			for (String key : extKeys)
				setPrefsText(key);
		}

		void setPrefsText(String key)
		{
			Preference prefComp = findPreference(key);
			prefComp.setOnPreferenceClickListener(this);
			String value = prefs.getString(key, null);
			if (value != null)
			{
				prefComp.setSummary(value);
			}
		}

		@Override
		public boolean onPreferenceClick(Preference preference)
		{
			Intent intent = preference.getIntent();
			intent.addCategory(Intent.CATEGORY_OPENABLE);
			startActivityForResult(intent, preference.hashCode());
			return true;
		}

		/**
		 * Handler for result messages from other activities
		 */
		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data)
		{
			Preference pref;
			SharedPreferences.Editor ed = prefs.edit();
			// find the right key
			for (String key : extKeys)
			{
				pref = findPreference(key);
				if (pref.hashCode() == requestCode)
				{
					String value = (resultCode == Activity.RESULT_OK) ? data.getData().toString() : null;
					ed.putString(key, value);
					pref.setSummary(value != null ? value : getString(R.string.select_extension));
				}
			}
			ed.commit();
		}

	}
}
