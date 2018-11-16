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

import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * @author se82wi
 */
public class PvTransferHandler extends TransferHandler
{

	/** Serial Version ID */
	private static final long serialVersionUID = -3156199604384589468L;
	private static final PvDataFlavor processVarFlavor = new PvDataFlavor();

	public PvTransferHandler()
	{
		super("userObject");
	}

	/* (non-Javadoc)
	   * @see javax.swing.TransferHandler#getSourceActions(JComponent c)
	   */
	@Override
	public int getSourceActions(JComponent c)
	{
		return COPY_OR_MOVE;
	}

	/* (non-Javadoc)
	   * @see javax.swing.TransferHandler#createTransferable(javax.swing.JComponent)
	   */
	@Override
	protected Transferable createTransferable(JComponent c)
	{
		if (c instanceof JTree)
		{
			try
			{
				JTree tree = (JTree) c;
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
				ProcessVar pv = (ProcessVar) node.getUserObject();
				return (Transferable) pv;
			} catch (ClassCastException ex)
			{
				ProcessVar.log.severe(this.toString() + ":" + ex.getMessage());
			}
		} else if (c instanceof PvTable)
		{
			PvTable tab = (PvTable) c;
			ProcessVar pv = tab.getPvModel().getElementAt(tab.getSelectedRow());
			return (Transferable) pv;
		}
		// anything else is handled by superclass
		return super.createTransferable(c);
	}

	/* (non-Javadoc)
	   * @see javax.swing.TransferHandler#exportDone(javax.swing.JComponent, java.awt.datatransfer.Transferable, int)
	   */
	@Override
	protected void exportDone(JComponent c, Transferable t, int action)
	{
		try
		{
			if (action == MOVE)
			{
				ProcessVar pv = (ProcessVar) t.getTransferData(processVarFlavor);
				pv.firePvChanged(new PvChangeEvent(pv, pv.getKeyAttribute(), pv, PvChangeEvent.PV_ELIMINATED));
			}
		} catch (UnsupportedFlavorException e)
		{
			ProcessVar.log.severe(this.toString() + ":" + e.getMessage());
		} catch (IOException e)
		{
			ProcessVar.log.severe(this.toString() + ":" + e.getMessage());
		}
	}

	/* (non-Javadoc)
	   * @see javax.swing.TransferHandler#importData(javax.swing.TransferHandler.TransferSupport)
	   */
	@Override
	public boolean importData(TransferSupport support)
	{
		try
		{
			if (support.getComponent() instanceof JTree)
			{
				JTree tree = (JTree) support.getComponent();
				Point dropPoint = support.getDropLocation().getDropPoint();
				TreePath path = tree.getPathForLocation(dropPoint.x, dropPoint.y);
				Object node = path.getLastPathComponent();
				if (support.isDataFlavorSupported(processVarFlavor))
				{
					ProcessVar tVar = (ProcessVar) ((PvTreeNode) node).getUserObject();
					ProcessVar chldPv = (ProcessVar) support.getTransferable().getTransferData(processVarFlavor);

					Object chldKey = chldPv.getKeyValue();
					tVar.put(chldKey, chldPv, PvChangeEvent.PV_ADDED);
					DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
					model.nodeStructureChanged((TreeNode) model.getRoot());
					return true;
				}
			}
		} catch (UnsupportedFlavorException e)
		{
			ProcessVar.log.severe(this.toString() + ":" + e.getMessage());
		} catch (IOException e)
		{
			ProcessVar.log.severe(this.toString() + ":" + e.getMessage());
		}
		// anything else is handled by superclass
		return super.importData(support);
	}

	/* (non-Javadoc)
	   * @see javax.swing.TransferHandler#canImport(javax.swing.TransferHandler.TransferSupport)
	   */
	@Override
	public boolean canImport(TransferSupport support)
	{
		return support != null
			&& support.isDataFlavorSupported(processVarFlavor);
	}
}
