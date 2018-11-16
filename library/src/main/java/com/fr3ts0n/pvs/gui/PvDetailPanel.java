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

import com.fr3ts0n.pvs.ProcessVar;
import com.fr3ts0n.pvs.PvChangeEvent;
import com.fr3ts0n.pvs.PvChangeListener;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.Customizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.text.JTextComponent;

/**
 * panel class to display / edit a parocess variable and all it's attributes
 *
 * @author $Author: erwin $
 */
public class PvDetailPanel extends JPanel
	implements ActionListener, PvChangeListener, Customizer
{
	/**
	 *
	 */
	private static final long serialVersionUID = 740664204205911504L;
	// field background colors
	private static final Color unchangedColor = Color.white;
	private static final Color changedColor = Color.lightGray;
	private static final Color labelColor = Color.lightGray;
	private static final LineBorder lineBorder = new LineBorder(Color.BLACK);
	private final HashMap<Object, JComponent> fields = new HashMap<Object, JComponent>();
	private ProcessVar dataSource = null;
	private boolean editable = false;

	public PvDetailPanel()
	{
		setLayout(new GridBagLayout());
		setBackground(labelColor);
		setBorder(lineBorder);

	}

	/**
	 * create a panel of detailed information on current process variable
	 */
	public PvDetailPanel(ProcessVar data)
	{
		this();
		setProcessVar(data);
	}

	/**
	 * create a panel of detailed information on current process variable
	 */
	public PvDetailPanel(ProcessVar data, boolean allowEdit)
	{
		this();
		setEditable(allowEdit);
		setProcessVar(data);
	}

	/**
	 * set the process var which is assigned to this editor panel
	 * this creates all corresponding data fields
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	private void setProcessVar(ProcessVar data)
	{
		if (dataSource == null || !dataSource.equals(data))
		{
			// remove all child components, if they have been set already
			if (dataSource != null && !dataSource.equals(data))
			{
				dataSource.removePvChangeListener(this);
				fields.clear();
				removeAll();
			}

			// update data source variable
			dataSource = data;

			// if data source is anything real ...
			if (data != null)
			{
				Object currKey;
				Object currVal;
				JLabel hdrCmp;
				JComponent dataCmp;

				GridBagConstraints attr = new GridBagConstraints();

				// ... fill content panel with data
				ArrayList keys = new ArrayList(data.keySet());
				// sort list alphabetically
				Collections.sort(keys);
				Iterator it = keys.iterator();

				// loop through all attributes
				while (it.hasNext())
				{
		  /* set default attributes */
					attr.gridwidth = 1;
					attr.gridheight = 1;
					attr.weightx = 0.0;
					attr.fill = GridBagConstraints.BOTH;

					if ((currKey = it.next()) != null)
					{
						hdrCmp = new JLabel(currKey.toString());
						hdrCmp.setBackground(labelColor);
						hdrCmp.setBorder(lineBorder);

						// setup data component based on it's value
						if ((currVal = data.get(currKey)) == null)
						{
							dataCmp = new JLabel();
						} else
						{
							if (currVal instanceof ProcessVar)
							{
								dataCmp = new PvDetailPanel((ProcessVar) currVal, editable);
							} else
							{
								dataCmp = new JTextField(data.get(currKey).toString());
								((JTextField) dataCmp).setEditable(editable);
								((JTextField) dataCmp).addActionListener(this);
							}
						}
						dataCmp.setName(currKey.toString());
						// add the components
						add(hdrCmp, attr);
						attr.gridwidth = GridBagConstraints.REMAINDER;
						attr.weightx = 1.0;
						add(dataCmp, attr);
						// add data component to Hashmap of fields
						fields.put(currKey, dataCmp);
					}
				}
				// add 'this' panel as PvChangeListener to Process variable
				data.addPvChangeListener(this, PvChangeEvent.PV_ADDED | PvChangeEvent.PV_MODIFIED);
			}
			validate();
		}
	}

	/** Implementation of Customizer interface */
	public void setObject(Object bean)
	{
		setEditable(true);
		setProcessVar((ProcessVar) bean);
	}

	/** return the process var which is assigned to this editor panel */
	public ProcessVar getProcessVar()
	{
		return (dataSource);
	}

	/** return if the process this editor panel is editable */
	public boolean getEditable()
	{
		return (editable);
	}

	/** set this editor panel to be non-/editable */
	private void setEditable(boolean newEditable)
	{
		editable = newEditable;
	}

	/**
	 * action handler for edit fields within panel
	 * this handler also temporarily changes the color of the edit field to
	 * visibly indicate the update action.
	 */
	public void actionPerformed(ActionEvent e)
	{
		// find out which field sends the event
		JTextField edit = (JTextField) e.getSource();
		// update the corresponding process var
		dataSource.put(edit.getName(), edit.getText(), PvChangeEvent.PV_MANUAL_MOD);
		// indicate change by changing field's background color
		edit.setBackground(changedColor);
		Toolkit.getDefaultToolkit().beep();
	}

	protected void finalize() throws Throwable
	{
		dataSource.removePvChangeListener(this);
		fields.clear();
		removeAll();
		super.finalize();
	}

	/**
	 * handler for process variable changes
	 * update dialog element from process variable
	 */
	public void pvChanged(PvChangeEvent event)
	{
		// only update panel if it is displayable
		if (isDisplayable())
		{
			Component edit = fields.get(event.getKey());
			Object value = event.getValue();
			// if we have found the corresponding editor field ...
			if (edit != null)
			{
				// set the value and background for it
				edit.setBackground(event.getType() == PvChangeEvent.PV_MANUAL_MOD ? changedColor : unchangedColor);
				if (edit instanceof JTextComponent)
				{
					((JTextComponent) edit).setText(value != null ? value.toString() : "");
				} else if (edit instanceof JLabel)
				{
					((JLabel) edit).setText(value != null ? value.toString() : "");
				} else if (edit instanceof PvDetailPanel)
				{
					((PvDetailPanel) edit).setProcessVar((ProcessVar) value);
				}
			}
		}
	}
}
