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

import android.app.AlertDialog;
import android.content.Context;
import android.preference.MultiSelectListPreference;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Legacy-API compatible select all functionality for {@link MultiSelectListPreference}.
 *
 * @author BaderSZ
 */
public class SearchableMultiSelectListPreference extends MultiSelectListPreference {

    @SuppressWarnings("unused")
    public SearchableMultiSelectListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressWarnings("unused")
    public SearchableMultiSelectListPreference(Context context) {
        super(context);
    }

    @SuppressWarnings("unused")
    public SearchableMultiSelectListPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        CharSequence[] entries = getEntries();
        CharSequence[] entryValues = getEntryValues();

        if (entries == null || entryValues == null) {
            super.onPrepareDialogBuilder(builder);
            return;
        }

        // Add "Select All" at the top
        final CharSequence[] items = new CharSequence[entries.length + 1];
        final CharSequence[] values = new CharSequence[entryValues.length + 1];

        items[0] = "Select All";
        values[0] = "SELECT_ALL";

        System.arraycopy(entries, 0, items, 1, entries.length);
        System.arraycopy(entryValues, 0, values, 1, entryValues.length);

        final Set<String> selectedValues = new HashSet<String>(getValues());
        final boolean[] checkedItems = new boolean[items.length];

        // Pre-check items
        checkedItems[0] = selectedValues.size() == entryValues.length;
        for (int i = 1; i < items.length; i++) {
            checkedItems[i] = selectedValues.contains(values[i].toString());
        }

        final EditText search = new EditText(getContext());
        search.setHint("Search...");

        final ListView listView = new ListView(getContext());

        final ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
                getContext(),
                android.R.layout.simple_list_item_multiple_choice,
                new ArrayList<CharSequence>(Arrays.asList(items))
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

                List<CharSequence> filteredItems = new ArrayList<CharSequence>();
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

        builder.setPositiveButton("OK", (dialog, which) -> {
            Set<String> newValues = new HashSet<String>();
            for (int i = 1; i < checkedItems.length; i++) {
                if (checkedItems[i]) {
                    newValues.add(values[i].toString());
                }
            }
            if (callChangeListener(newValues)) {
                setValues(newValues);
            }
        });

        builder.setNegativeButton("Cancel", null);
    }
}
