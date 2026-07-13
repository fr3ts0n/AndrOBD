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

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.MultiSelectListPreference;

/**
 * Legacy-API compatible select all functionality for {@link MultiSelectListPreference}.
 *
 * <p>The custom "Select All" + search dialog UI itself lives in
 * {@link SearchableMultiSelectListPreferenceDialogFragment} - AndroidX preference dialogs are
 * shown via a {@code PreferenceDialogFragmentCompat}, not via an override on the Preference class
 * itself, unlike the old {@code android.preference} API this was originally written against.
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
        super(context, attrs, defStyle);
    }
}
