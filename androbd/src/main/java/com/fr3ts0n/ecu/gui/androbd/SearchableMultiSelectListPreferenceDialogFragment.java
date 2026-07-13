/*
 * (C) Copyright 2026 by Bader Zaidan <github@zaidan.tech>
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
 */

package com.fr3ts0n.ecu.gui.androbd;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceDialogFragmentCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * "Select All" + search dialog for {@link SearchableMultiSelectListPreference}.
 *
 * AndroidX shows preference dialogs through a {@code PreferenceDialogFragmentCompat}, so this
 * carries the same custom {@code onPrepareDialogBuilder}/{@code onDialogClosed} logic the
 * original {@code android.preference}-based class had directly on the Preference itself.
 *
 * @author BaderSZ
 */
public class SearchableMultiSelectListPreferenceDialogFragment
        extends PreferenceDialogFragmentCompat {

    private CharSequence[] entries;
    private CharSequence[] entryValues;
    private CharSequence[] items;
    private CharSequence[] values;
    private boolean[] checkedItems;

    public static SearchableMultiSelectListPreferenceDialogFragment newInstance(String key) {
        SearchableMultiSelectListPreferenceDialogFragment fragment =
                new SearchableMultiSelectListPreferenceDialogFragment();
        Bundle bundle = new Bundle(1);
        bundle.putString(ARG_KEY, key);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SearchableMultiSelectListPreference preference =
                (SearchableMultiSelectListPreference) getPreference();
        entries = preference.getEntries();
        entryValues = preference.getEntryValues();
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        if (entries == null || entryValues == null) {
            return;
        }

        SearchableMultiSelectListPreference preference =
                (SearchableMultiSelectListPreference) getPreference();

        // Add "Select All" at the top
        items = new CharSequence[entries.length + 1];
        values = new CharSequence[entryValues.length + 1];

        items[0] = "Select All";
        values[0] = "SELECT_ALL";

        System.arraycopy(entries, 0, items, 1, entries.length);
        System.arraycopy(entryValues, 0, values, 1, entryValues.length);

        final Set<String> selectedValues = new HashSet<>(preference.getValues());
        checkedItems = new boolean[items.length];

        // Pre-check items
        checkedItems[0] = selectedValues.size() == entryValues.length;
        for (int i = 1; i < items.length; i++) {
            checkedItems[i] = selectedValues.contains(values[i].toString());
        }

        final EditText search = new EditText(getContext());
        search.setHint("Search...");

        final ListView listView = new ListView(getContext());

        final ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_list_item_multiple_choice,
                new ArrayList<>(Arrays.asList(items))
        );
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        for (int i = 0; i < checkedItems.length; i++) {
            listView.setItemChecked(i, checkedItems[i]);
        }

        search.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                String filter = s.toString().toLowerCase();

                List<CharSequence> filteredItems = new ArrayList<>();
                // always keep "Select All" at top
                filteredItems.add(items[0]);

                // i=1 to skip "Select All"
                for (int i = 1; i < items.length; i++) {
                    if (items[i].toString().toLowerCase().contains(filter)) {
                        filteredItems.add(items[i]);
                    }
                }

                adapter.clear();
                adapter.addAll(filteredItems);
                adapter.notifyDataSetChanged();
            }
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            CharSequence item = adapter.getItem(position);

            int originalIndex = Arrays.asList(items).indexOf(item);
            boolean isChecked = listView.isItemChecked(position);
            checkedItems[originalIndex] = isChecked;

            // Select All clicked.
            if (originalIndex == 0) {
                for (int i = 1; i < checkedItems.length; i++) {
                    checkedItems[i] = isChecked;
                    int filteredPos = adapter.getPosition(items[i]);
                    if (filteredPos >= 0) listView.setItemChecked(filteredPos, isChecked);
                }
            } else {
                boolean allChecked = true;
                for (int i = 1; i < checkedItems.length; i++) {
                    if (!checkedItems[i]) {
                        allChecked = false;
                        break;
                    }
                }
                checkedItems[0] = allChecked;
                int filteredPos = adapter.getPosition(items[0]);
                if (filteredPos >= 0) listView.setItemChecked(filteredPos, allChecked);
            }
        });

        android.widget.LinearLayout container = new android.widget.LinearLayout(getContext());
        container.setOrientation(android.widget.LinearLayout.VERTICAL);
        container.addView(search);
        container.addView(listView);

        builder.setView(container);
        // The base PreferenceDialogFragmentCompat already supplies OK/Cancel buttons wired to
        // onDialogClosed(); this dialog only needs to override the message/view area, not the
        // buttons themselves - matching the effect of the original class's own
        // setPositiveButton/setNegativeButton calls.
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult && items != null && values != null && checkedItems != null) {
            SearchableMultiSelectListPreference preference =
                    (SearchableMultiSelectListPreference) getPreference();
            Set<String> newValues = new HashSet<>();
            for (int i = 1; i < checkedItems.length; i++) {
                if (checkedItems[i]) {
                    newValues.add(values[i].toString());
                }
            }
            if (preference.callChangeListener(newValues)) {
                preference.setValues(newValues);
            }
        }
    }
}
