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

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

/**
 * Cell Renderer for PvTable component
 *
 * @author erwin
 */
public class PvTableCellRenderer extends JLabel
	implements TableCellRenderer
{

	/**
	 *
	 */
	private static final long serialVersionUID = -7686049090382566048L;
	static final EmptyBorder brdr = new EmptyBorder(0, 5, 0, 5);

	/** Creates a new instance of PvTableCellRenderer */
	public PvTableCellRenderer()
	{
		setOpaque(true);
		setBorder(brdr);
	}

	/** render a single pv attribute based on location within table */
	public Component getTableCellRendererComponent(JTable table,
	                                               Object value,
	                                               boolean isSelected,
	                                               boolean hasFocus,
	                                               int row,
	                                               int column)
	{
		setFont(table.getFont());
		setHorizontalAlignment(value instanceof Number ? RIGHT : LEFT);
		setText(String.valueOf(value));
		setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());

		return (this);
	}
}
