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

package com.fr3ts0n.pvs.gui;

import javax.swing.JTable;

import com.fr3ts0n.pvs.ProcessVar;

/**
 * Table GUI object for process variables
 *
 * @author erwin
 */
public class PvTable extends JTable
{

	/**
	 *
	 */
	private static final long serialVersionUID = 6339359674123198139L;
	ProcessVar pv;
	private PvTableModel pvModel = new PvTableModel();
	PvTableCellRenderer renderer = new PvTableCellRenderer();

	/** Creates a new instance of PvTable */
	public PvTable()
	{
		setRowSelectionAllowed(true);
		setDefaultRenderer(Object.class, renderer);
		setModel(new javax.swing.table.DefaultTableModel(
			new Object[][]{
				{null, null, null, null},
				{null, null, null, null},
				{null, null, null, null},
				{null, null, null, null}},
			new String[]{
				"Title 1", "Title 2", "Title 3", "Title 4"
			}));
	}

	/**
	 * Creates a new instance of PvTable
	 *
	 * @param list The process var to display in table
	 */
	public PvTable(ProcessVar list)
	{
		setProcessVar(list);
		setRowSelectionAllowed(true);
	}

	/**
	 * set the process var
	 *
	 * @param list The process var to display in table
	 */
	public void setProcessVar(ProcessVar list)
	{
		pv = list;
		pvModel.setProcessVar(pv);
		setModel(pvModel);
	}

	public PvTableModel getPvModel()
	{
		return pvModel;
	}

	public void setPvModel(PvTableModel pvModel)
	{
		this.pvModel = pvModel;
	}
}
