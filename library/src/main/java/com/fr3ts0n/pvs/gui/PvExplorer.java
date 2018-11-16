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
import com.fr3ts0n.pvs.PvList;

import java.awt.Dimension;
import java.awt.ScrollPane;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.DropMode;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

public class PvExplorer extends JSplitPane implements TreeSelectionListener
{
	/** unique serial version ID */
	private static final long serialVersionUID = 182596839340890403L;

	/** root process variable for tree and initial table display */
	ProcessVar pvRoot;
	/** PV tree display */
	private final JTree pvTree;
	/** PV table display */
	private final PvTable pvTable;
	/** PV List as container for single process variables to be displayed in table */
	private final PvList tblList = new PvList();
	/** PV transfer handler to handle data transfers within explorer */
	private final PvTransferHandler xferHdlr = new PvTransferHandler();

	public PvExplorer(ProcessVar pv)
	{
		setDividerSize(2);
		setPreferredSize(new Dimension(640, 480));

		pvTree = new JTree(new PvTreeNode(pv));
		pvTree.getSelectionModel().addTreeSelectionListener(this);
		pvTree.setDragEnabled(true);
		pvTree.setDropMode(DropMode.ON_OR_INSERT);
		pvTree.setTransferHandler(xferHdlr);

		pvTable = new PvTable(pv);
		pvTable.setDragEnabled(true);
		pvTable.setDropMode(DropMode.ON_OR_INSERT);
		pvTable.addMouseListener(tblMouseLstnr);
		pvTable.setTransferHandler(xferHdlr);

		JScrollPane spPvTree = new JScrollPane(pvTree);
		add(spPvTree, JSplitPane.LEFT);
		JScrollPane spPvTable = new JScrollPane(pvTable);
		add(spPvTable, JSplitPane.RIGHT);
	}

	@Override
	public void valueChanged(TreeSelectionEvent e)
	{
		TreePath selPath = e.getNewLeadSelectionPath();
		if (selPath == null)
			return;

		ProcessVar pv = PvTreeNode.getPvFromTreePath(selPath);

		if (pv instanceof PvList)
		{
			// if process var is a PV list itself then show it in table
			pvTable.setProcessVar(pv);
		} else
		{
			// ... otherwise create a list with PV as contained element
			tblList.clear();
			tblList.put(pv.getKeyValue(), pv);
			pvTable.setProcessVar(tblList);
		}
	}

	/**
	 * Listener for mouse events on table side
	 */
	private final MouseListener tblMouseLstnr = new MouseListener()
	{
		@Override
		public void mouseClicked(MouseEvent e)
		{
			if (e.getClickCount() > 1)
			{
				// find the process var to selected row ...
				ProcessVar pv = pvTable.getPvModel().getElementAt(pvTable.getSelectedRow());
				// and show a detail panel about it ...
				if (pv != null)
				{
					PvDetailPanel panDetail = new PvDetailPanel(pv, true);
					ScrollPane panScr = new ScrollPane();
					panScr.setPreferredSize(new Dimension(640, 480));
					panScr.add(panDetail);
					JOptionPane.showMessageDialog(null, panScr);
				}
			}
		}

		@Override
		public void mouseEntered(MouseEvent e)
		{
		}

		@Override
		public void mouseExited(MouseEvent e)
		{
		}

		@Override
		public void mousePressed(MouseEvent e)
		{
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
		}
	};
}
