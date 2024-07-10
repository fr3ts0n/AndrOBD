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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.fr3ts0n.ecu.EcuDataItem;
import com.fr3ts0n.ecu.prot.obd.ElmProt;
import com.fr3ts0n.ecu.prot.obd.ObdProt;

import java.util.HashSet;
import java.util.Objects;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SettingsActivity
	extends Activity
{
	/** The logger object */
	private static final Logger log = Logger.getLogger(SettingsActivity.class.getName());
	
	/**
	 * app preferences
	 */
	private static SharedPreferences prefs;
	/**
	 * preference keys for extension files
	 */
	static final String[] extKeys =
	{
		"ext_file_conversions",
		"ext_file_dataitems"
	};

	/**
	 * key ids for device network settings
	 */
	private static final String[] networkKeys =
	{
		"device_address",
		"device_port"
	};
	/**
	 * key ids for device network settings
	 */
	private static final String[] bluetoothKeys =
	{
		"bt_secure_connection"
	};
	/**
	 * key ids for device network settings
	 */
	private static final String[] usbKeys =
	{
			"comm_baudrate"
	};

	// Preference key for data items
	static final String KEY_DATA_ITEMS = "data_items";
	static final String KEY_PROT_SELECT = "protocol";
	static final String KEY_COMM_MEDIUM = "comm_medium";
	static final String ELM_MIN_TIMEOUT = "elm_min_timeout";
	static final String ELM_CMD_DISABLE = "elm_cmd_disable";
    static final String ELM_TIMING_SELECT = "adaptive_timing_mode";
    private static final String KEY_BITCOIN = "bitcoin";

	/*
	 * (non-Javadoc)
	 *
	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setTheme(MainActivity.nightMode ? R.style.AppTheme_Dark : R.style.AppTheme);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		// Display the fragment as the main content.
		getFragmentManager().beginTransaction().replace(android.R.id.content,
		                                                new PrefsFragment()).commit();
	}

	@SuppressLint("ValidFragment")
	public class PrefsFragment
		extends PreferenceFragment
		implements Preference.OnPreferenceClickListener,
		SharedPreferences.OnSharedPreferenceChangeListener
	{
		Vector<EcuDataItem> items;

		@Override
		public void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);

			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.settings);

			for (String key : extKeys)
			{
				setPrefsText(key);
			}

			// set up communication media selection
			setupCommMediaSelection();
			// set up protocol selection
			setupProtoSelection();
			// set up ELM command selection
			setupElmCmdSelection();
            // set up ELM adaptive timing mode selection
			setupElmTimingSelection();
			// set up selectable PID list
			setupPidSelection();
			// update network selection fields
			updateNetworkSelections();
			findPreference(KEY_BITCOIN).setOnPreferenceClickListener(this);
			// add handler for selection update
			prefs.registerOnSharedPreferenceChangeListener(this);
		}

		/**
		 * set up protocol selection
		 */
		void setupProtoSelection()
		{
			ListPreference pref = (ListPreference) findPreference(KEY_PROT_SELECT);
			ElmProt.PROT[] values = ElmProt.PROT.values();
			CharSequence[] titles = new CharSequence[values.length];
			CharSequence[] keys = new CharSequence[values.length];
			int i = 0;
			for (ElmProt.PROT proto : values)
			{
				titles[i] = proto.toString();
				keys[i] = String.valueOf(proto.ordinal());
				i++;
			}
			// set enries and keys
			pref.setEntries(titles);
			pref.setEntryValues(keys);
			pref.setDefaultValue(titles[0]);
			// show current selection
			pref.setSummary(pref.getEntry());
		}

        /**
         * set up protocol selection
         */
        void setupElmTimingSelection()
        {
            ListPreference pref = (ListPreference) findPreference(ELM_TIMING_SELECT);
            ElmProt.AdaptTimingMode[] values = ElmProt.AdaptTimingMode.values();
            CharSequence[] titles = new CharSequence[values.length];
            CharSequence[] keys = new CharSequence[values.length];
            int i = 0;
            for (ElmProt.AdaptTimingMode mode : values)
            {
				titles[i] = mode.toString();
				keys[i] = mode.toString();
				i++;
            }
            // set entries and keys
            pref.setEntries(titles);
            pref.setEntryValues(keys);
            pref.setDefaultValue(titles[0]);
            // show current selection
            pref.setSummary(pref.getEntry());
        }

		/**
		 * set up protocol selection
		 */
		void setupElmCmdSelection()
		{
			MultiSelectListPreference pref =
				(MultiSelectListPreference) findPreference(ELM_CMD_DISABLE);
			ElmProt.CMD[] values = ElmProt.CMD.values();
			HashSet<String> selections = new HashSet<>();
			CharSequence[] titles = new CharSequence[values.length];
			CharSequence[] keys = new CharSequence[values.length];
			int i = 0;
			for (ElmProt.CMD cmd : values)
			{
				titles[i] = cmd.toString();
				keys[i] = cmd.toString();
				if(!cmd.isEnabled()) selections.add(cmd.toString());
				i++;
			}
			// set enries and keys
			pref.setEntries(titles);
			pref.setEntryValues(keys);
			pref.setValues(selections);
		}

		/**
		 * set up protocol selection
		 */
		void setupCommMediaSelection()
		{
			ListPreference pref = (ListPreference) findPreference(KEY_COMM_MEDIUM);
			CommService.MEDIUM[] values = CommService.MEDIUM.values();
			CharSequence[] titles = new CharSequence[values.length];
			CharSequence[] keys = new CharSequence[values.length];
			int i = 0;
			for (CommService.MEDIUM proto : values)
			{
				titles[i] = proto.toString();
				keys[i] = String.valueOf(proto.ordinal());
				i++;
			}
			// set enries and keys
			pref.setEntries(titles);
			pref.setEntryValues(keys);
			pref.setDefaultValue(titles[0]);
			// show current selection
			pref.setSummary(pref.getEntry());
		}

		/**
		 * set up selection for PIDs
		 */
		void setupPidSelection()
		{
			MultiSelectListPreference itemList =
				(MultiSelectListPreference) findPreference(KEY_DATA_ITEMS);

			// collect data items for selection
			items = ObdProt.dataItems.getSvcDataItems(ObdProt.OBD_SVC_DATA);
			HashSet<String> selections = new HashSet<>();
			CharSequence[] titles = new CharSequence[items.size()];
			CharSequence[] keys = new CharSequence[items.size()];
			// loop through data items
			int i = 0;
			for (EcuDataItem currItem : items)
			{
				titles[i] = currItem.label;
				keys[i] = currItem.toString();
				selections.add(currItem.toString());
				i++;
			}
			// set enries and keys
			itemList.setEntries(titles);
			itemList.setEntryValues(keys);

			// if there is no item selected, mark all as selected
			if (itemList.getValues().size() == 0)
			{
				itemList.setValues(selections);
			}
		}

		/**
		 * set up preference text for extension files
		 *
		 * @param key preference key to be set up
		 */
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

		/**
		 * Update fields for network parameters
		 *
		 * enable/disable elements for network parameters
		 * based on selection of communication medium
		 */
		void updateNetworkSelections()
		{
			boolean networkSelected =
				String.valueOf(CommService.MEDIUM.WIFI.ordinal())
					.equals(prefs.getString(KEY_COMM_MEDIUM,""));
			boolean bluetoothSelected =
				String.valueOf(CommService.MEDIUM.BLUETOOTH.ordinal())
					.equals(prefs.getString(KEY_COMM_MEDIUM,""))
				|| String.valueOf(CommService.MEDIUM.BLE.ordinal())
					.equals(prefs.getString(KEY_COMM_MEDIUM,""));
			boolean usbSelected =
					String.valueOf(CommService.MEDIUM.USB.ordinal())
							.equals(prefs.getString(KEY_COMM_MEDIUM,""));

			// enable/disable network specific entries
			for(String key : networkKeys)
			{
				Preference pref = findPreference(key);
				pref.setEnabled(networkSelected);
			}

			// enable/disable bluetooth specific entries
			for(String key : bluetoothKeys)
			{
				Preference pref = findPreference(key);
				pref.setEnabled(bluetoothSelected);
			}

			// enable/disable usb specific entries
			for(String key : usbKeys)
			{
				Preference pref = findPreference(key);
				pref.setEnabled(usbSelected);
			}
		}

		@Override
		public boolean onPreferenceClick(Preference preference)
		{
			Intent intent = preference.getIntent();
			try
			{
				if(KEY_BITCOIN.equals(preference.getKey()))
				{
					// special handling for bitcoin VIEW intent
					startActivity(intent);
				}
				else
				{
					// OPEN intents require result handling
					intent.addCategory(Intent.CATEGORY_OPENABLE);
					startActivityForResult(intent, preference.hashCode());
				}
			}
			catch(Exception e)
			{
				log.log(Level.SEVERE, "Settings", e);
				Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
			}
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
			String value = (resultCode == Activity.RESULT_OK) ? String.valueOf(data.getData()) : null;
			// find the right key
			for (String key : extKeys)
			{
				pref = findPreference(key);
				if (pref.hashCode() == requestCode)
				{
					ed.putString(key, value);
					pref.setSummary(value != null ? value : getString(R.string.select_extension));

					if(value != null)
					{
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
						{
						/* Remember persistent read permission for selected file,
						   otherwise we will NOT be allowed to load it on startup ... ;( */
							getContentResolver().takePersistableUriPermission(Objects.requireNonNull(data.getData()),
															                  Intent.FLAG_GRANT_READ_URI_PERMISSION);
						}
					}
				}
			}

			ed.apply();
		}

		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
		{
			Preference pref = findPreference(key);

			if (pref instanceof ListPreference)
			{
				ListPreference currPref = (ListPreference) pref;
				currPref.setSummary(currPref.getEntry());
			}
			else
			if (pref instanceof EditTextPreference)
			{
				EditTextPreference currPref = (EditTextPreference) pref;
				currPref.setSummary(currPref.getText());
			}

			if(KEY_COMM_MEDIUM.equals(key))
				updateNetworkSelections();

			if(ELM_TIMING_SELECT.equals(key))
				//noinspection ConstantConditions
				findPreference(ELM_MIN_TIMEOUT)
					.setEnabled(ElmProt.AdaptTimingMode.SOFTWARE.toString()
						          .equals(((ListPreference)pref).getValue())
					           );
		}
	}
}
