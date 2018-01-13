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
 */

package com.fr3ts0n.ecu.gui.application;

import com.fr3ts0n.pvs.IndexedProcessVar;
import com.fr3ts0n.pvs.gui.PvTableModel;

/**
 * TableModel for OBD data items
 *
 * @author erwin
 */
public class ObdItemTableModel
	extends PvTableModel
{

	/** used for caching current row */
	int currRowIndex = -1;
	/**
	 *
	 */
	private static final long serialVersionUID = -1162610644870557702L;

	/** Creates a new instance of ObdItemTableModel */
	public ObdItemTableModel()
	{
	}

	/**
	 * get the value for table location (x,y)
	 * This implementation returns the complete OBD-Item which represents the complete row
	 * the field handling shall be done by the renderer, since the OBD-item
	 * also includes formatting information which is required for rendering.
	 *
	 * @param rowIndex    row number
	 * @param columnIndex column number
	 * @return value for table location
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		// if the pv is set ...
		if (pv != null)
		{
			// get the column value
			if (rowIndex != currRowIndex && rowIndex < getRowCount())
			{
				currRowIndex = rowIndex;
				currRow = (IndexedProcessVar) pv.get(keys[rowIndex]);
			}
		}
		return (currRow);
	}

	/**
	 * fire update events for specified column on all rows
	 *
	 * @param columnId colum to update
	 */
	public synchronized void updateAllRows(int columnId)
	{
		fireTableRowsUpdated(0, getRowCount());
	}
}
