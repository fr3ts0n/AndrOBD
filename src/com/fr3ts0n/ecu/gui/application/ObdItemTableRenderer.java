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

import com.fr3ts0n.ecu.Conversion;
import com.fr3ts0n.ecu.Conversions;
import com.fr3ts0n.ecu.EcuDataPv;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;


/**
 * Renderer for EcuDataPv Elements
 *
 * @author erwin
 */
public class ObdItemTableRenderer
	extends JLabel
	implements TableCellRenderer
{

	private static final long serialVersionUID = -1067775643797324582L;
	static EmptyBorder brdr = new EmptyBorder(0, 5, 0, 5);
	Font parentFont = null;
	Color bgColor = null;
	Color selColor = null;
	JTable parentTable = null;


	/** Creates a new instance of ObdItemTableRenderer */
	public ObdItemTableRenderer()
	{
		setOpaque(true);
		setBorder(brdr);
	}

	/**
	 * set visualisation parameters referring to given table
	 *
	 * @param table - the table object to refer to ...
	 */
	private void setParentTable(JTable table)
	{
		parentTable = table;
		// set the font only once, and then just use it
		parentFont = table.getFont();
		setFont(parentFont);

		// get background color from Table
		bgColor = table.getBackground();
		// get selection color from Table
		selColor = table.getSelectionBackground();
	}

	public Component getTableCellRendererComponent(JTable table,
	                                               Object value,
	                                               boolean isSelected,
	                                               boolean hasFocus,
	                                               int row,
	                                               int column)
	{
		String fmtText = null;

		// if we don't know the parent table yet, set visual parameters
		if (parentTable == null) setParentTable(table);

		// background is dependent on selection status
		setBackground(isSelected ? selColor : bgColor);

		// if row is valid ...
		if (value != null)
		{
			// get column value
			Object colVal = ((EcuDataPv) value).get(column);
			if (colVal != null)
			{
				// formatting is based on column ...
				switch (column)
				{
					case EcuDataPv.FID_PID:
						setHorizontalAlignment(RIGHT);
						fmtText = String.valueOf(colVal);
						break;

					case EcuDataPv.FID_VALUE:
						setHorizontalAlignment(RIGHT);
						Object cnvObj = ((EcuDataPv) value).get(EcuDataPv.FID_CNVID);
						if (cnvObj != null && cnvObj instanceof Conversion[])
						{
							Conversion[] cnv = (Conversion[]) cnvObj;
							fmtText = cnv[Conversions.cnvSystem].physToPhysFmtString((Float) colVal,
								((EcuDataPv) value).getAsInt(EcuDataPv.FID_DECIMALS));
						} else
						{
							fmtText = Conversions.physToPhysFmtString((Float) colVal,
								((EcuDataPv) value).getAsInt(EcuDataPv.FID_CNVID),
								((EcuDataPv) value).getAsInt(EcuDataPv.FID_DECIMALS));
						}
						break;

					default:
						setHorizontalAlignment(LEFT);
						fmtText = colVal.toString();
						break;
				}
			}
		}
		setText(fmtText);

		return this;
	}
}
